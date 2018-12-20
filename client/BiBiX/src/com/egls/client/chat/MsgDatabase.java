/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egls.client.chat;

import com.egls.client.game.Const;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Gust
 */
public class MsgDatabase extends Thread {

    long roleid;
    boolean exit = false;

    String saveRoot;
    static final String META_FILE_NAME = "_meta.db";
    static final String DATA_FILE_NAME = "_data.db";

    String metaFileName;
    String dataFileName;

    static final int DATA_BLOCK_SIZE = 4096;

    Map<String, MetaItem> metas = new HashMap();

    Map<Integer, MsgPage> cache = new LinkedHashMap() {
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            if (cache.size() > 1000) {
                return true;
            }
            return false;
        }
    };

    Map<String, byte[]> media = new LinkedHashMap() {
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            if (cache.size() > 100) {
                return true;
            }
            return false;
        }
    };

    int maxSerialId = -1;

    public MsgDatabase(String saveRoot, long roleid) {
        this.saveRoot = saveRoot;
        this.roleid = roleid;

        metaFileName = getMetaFileName();
        dataFileName = getDataFileName();

        repairDataFile(new File(dataFileName));
        LoadMetaFile();
    }

    public void run() {
        while (!exit) {
            try {
                for (MsgPage page : cache.values()) {
                    if (page.dirty) {
                        saveData(page.pageId, page.save());
                        page.dirty = false;
                    }
                }
                Thread.sleep(1000);
            } catch (Exception e) {
            }

        }
    }

    synchronized int applicateSerialId() {
        MetaItem mi = getFreeBlockMeta();
        if (mi != null) {
            int i = mi.getAndRemoveLastSerialIds();
            if (i >= 0) {
                return i;
            }
        }
        return ++maxSerialId;
    }

    public Collection<MetaItem> getMetas() {
        return metas.values();
    }

    public void putMsg(MsgItem item) {
        MetaItem mi = getMeta(item.sessionRoleId, item.groupid);
        int lastId = mi.getLastSerialIds();
        MsgPage page = null;

        boolean needNew = false;
        if (lastId < 0) {
            needNew = true;
        } else {
            page = getMsgPage(lastId);
            if (DATA_BLOCK_SIZE - page.blockSize < item.getSerialSize()) {
                needNew = true;
            }
        }
        if (needNew) {
            lastId = applicateSerialId();
            mi.pageIds.add(lastId);

            page = new MsgPage();
            page.pageId = lastId;
            page.sessionid = item.sessionRoleId;
            page.groupid = item.groupid;
            page.firstAt = item.time;

            cache.put(lastId, page);

            saveMetaFile();
        }
        page.endAt = item.time;
        page.addItem(item);
        if (item.isMediaMsg()) {
            putMedia(item.getMediaId(), item.thumb);
        }

        saveData(lastId, page.save());
    }

    public void removeSession(long friendid, long sessionid) {
        MetaItem mi = getMeta(friendid, sessionid);
        if (mi != null) {
            for (Integer i : mi.pageIds) {
                MsgPage page = getMsgPage(i);
                if (page != null) {
                    page.clear(this);
                    saveData(i, page.save());
                }
            }

            MetaItem free = getFreeBlockMeta();
            free.pageIds.addAll(mi.pageIds);
            mi.pageIds.clear();

            metas.remove(getKey(friendid, sessionid));
            saveMetaFile();
        }
    }

    public int getPageSize(long friendid, long sessionid) {
        MetaItem mi = getMeta(friendid, sessionid);
        return mi.pageIds.size();
    }

    public List<Integer> getPageSerialId(long friendid, long sessionid) {
        MetaItem mi = getMeta(friendid, sessionid);
        List<Integer> list = new ArrayList();
        list.addAll(mi.pageIds);
        return list;
    }

    /**
     * 得到某一页之前的一页,如果是-1,则返回最后一条msg
     *
     * @param friendid
     * @param sessionid
     * @param time
     * @return
     */
    public MsgPage getPrePage(long friendid, long sessionid, int pageId) {
        MetaItem mi = getMeta(friendid, sessionid);
        int selected = -1;
        if (pageId == -1) {
            selected = mi.getLastSerialIds();
        } else {
            for (int i = mi.pageIds.size() - 1; i >= 0; i--) {
                Integer s = mi.pageIds.get(i);
                if (s == pageId && i - 1 >= 0) {
                    selected = mi.pageIds.get(i - 1);
                    break;
                }
            }
        }
        if (selected != -1) {
            MsgPage db = getMsgPage(selected);
            if (db != null) {
                return db;
            }
        }
        return null;
    }

    public MsgPage getNextPage(long friendid, long sessionid, int pageId) {
        MetaItem mi = getMeta(friendid, sessionid);
        int selected = -1;
        if (pageId == -1) {
            selected = mi.getFirstSerialIds();
        } else {
            for (int i = 0, imax = mi.pageIds.size(); i < imax; i++) {
                Integer s = mi.pageIds.get(i);
                if (s == pageId && i + 1 < imax) {
                    selected = mi.pageIds.get(i + 1);
                    break;
                }
            }
        }
        if (selected != -1) {
            MsgPage db = getMsgPage(selected);
            if (db != null) {
                return db;
            }
        }
        return null;
    }

    void saveMetaFile() {
        FileWriter fw = null;
        try {
            File metaFile = new File(getMetaFileName());
            fw = new FileWriter(metaFile);
            BufferedWriter bw = new BufferedWriter(fw);
            for (MetaItem mi : metas.values()) {
                String s = mi.toString();
                bw.write(s);
                //System.out.println("save meta: " + s);
                bw.write("\n");
            }
            bw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                fw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    void LoadMetaFile() {
        FileReader fr = null;
        try {
            File metaFile = new File(getMetaFileName());
            if (!metaFile.exists()) {
                return;
            }
            fr = new FileReader(metaFile);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                //System.out.println("load meta:" + line);
                MetaItem mi = new MetaItem();
                mi.parseMeta(line);
                metas.put(mi.key, mi);
                int max = mi.getMaxSerialId();
                if (max > maxSerialId) {
                    maxSerialId = max;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (fr != null) {
                    fr.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    MetaItem getMeta(long friendid, long sessionid) {
        String key = getKey(friendid, sessionid);
        MetaItem mi = metas.get(key);
        if (mi == null) {
            mi = new MetaItem();
            mi.roleid = friendid;
            mi.groupid = sessionid;
            metas.put(key, mi);
        }
        return mi;
    }

    public String getKey(long friendid, long sessionid) {
        return Long.toString(friendid) + "," + Long.toString(sessionid);
    }

    /**
     * return free block meta
     *
     * @return
     */
    public MetaItem getFreeBlockMeta() {
        return getMeta(0, 0);
    }

    public MsgPage getMsgPage(int pageId) {
        MsgPage db = cache.get(pageId);
        if (db == null) {
            byte[] b = loadData(pageId);
            db = new MsgPage();
            db.pageId = pageId;
            db.load(b);
            cache.put(pageId, db);
        }
        db.loadMedia(this);
        return db;
    }

    byte[] loadData(int pageId) {
        try {
            File f = new File(dataFileName);
            if (!f.exists()) {
                System.out.println("file not found :" + dataFileName);
                return null;
            }
            RandomAccessFile raf = new RandomAccessFile(f, "r");
            long offset = pageId * DATA_BLOCK_SIZE;
            raf.seek(offset);
            byte[] b = new byte[DATA_BLOCK_SIZE];
            raf.read(b, 0, DATA_BLOCK_SIZE);
            raf.close();
            return b;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    void saveData(int pageId, byte[] b) {
        try {
            File f = new File(dataFileName);
            long offset = pageId * DATA_BLOCK_SIZE;
            if (f.length() < offset) {
                System.out.println("datafile size error :" + f.length() + " expected :" + offset);
                repairDataFile(f);
            }
            if (b.length < DATA_BLOCK_SIZE) {
                byte[] data = new byte[DATA_BLOCK_SIZE];
                System.arraycopy(b, 0, data, 0, b.length);
                b = data;
            } else if (b.length > DATA_BLOCK_SIZE) {
                throw new RuntimeException("data block size error :" + f.length() + " expected :" + offset);
            }
            RandomAccessFile raf = new RandomAccessFile(f, "rw");
            raf.seek(offset);
            raf.write(b, 0, b.length);

            raf.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    void repairDataFile(File f) {
        metas.clear();

        int fsize = (int) f.length();
        for (int i = 0, imax = fsize / DATA_BLOCK_SIZE; i < imax; i++) {

            byte[] b = loadData(i);
            MsgPage mp = new MsgPage();
            mp.pageId = i;
            mp.load(b);
            MetaItem meta = getMeta(mp.sessionid, mp.groupid);
            meta.roleid = mp.sessionid;
            meta.groupid = mp.groupid;
            meta.pageIds.add(i);
            if (meta.lastMsgAt < mp.endAt) {
                meta.lastMsgAt = mp.endAt;
            }

            cache.put(i, mp);
        }
        saveMetaFile();
    }

    void putMedia(String id, byte[] data) {
        if (id == null || data == null) {
            return;
        }
        media.put(id, data);
        File folder = new File(getMediaFolderName());
        if (!folder.exists()) {
            folder.mkdirs();
        }

        try {
            File m = new File(getMediaFolderName() + id);
            FileOutputStream fos = new FileOutputStream(m);
            fos.write(data);
            fos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    byte[] getMedia(String id) {
        if (id == null) {
            return null;
        }
        byte[] data = media.get(id);
        if (data == null) {
            try {
                File m = new File(getMediaFolderName() + id);
                if (m.exists()) {
                    data = new byte[(int) m.length()];
                    FileInputStream fis = new FileInputStream(m);
                    fis.read(data, 0, data.length);
                    fis.close();
                }
                media.put(id, data);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return data;
    }

    void removeMedia(String id) {
        if (id == null) {
            return;
        }
        media.remove(id);
        try {
            File m = new File(getMediaFolderName() + id);
            if (m.exists()) {
                m.delete();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    String getMetaFileName() {
        return saveRoot + "/" + roleid + META_FILE_NAME;
    }

    String getDataFileName() {
        return saveRoot + "/" + roleid + DATA_FILE_NAME;
    }

    String getMediaFolderName() {
        return saveRoot + Const.SAVE_MSG_PATH;
    }

    //==========================================================================
    static void test1() {
        long roleid = 100001;
        long sessionid = 0;

        MsgDatabase mdb = new MsgDatabase("./", roleid);

        for (int i = 0; i < 1000; i++) {
            MsgItem msg = new MsgItem();
            msg.toid = 10002;
            msg.time = System.currentTimeMillis();
            msg.toid = roleid;
            msg.groupid = sessionid;
            msg.fromid = 10002;
            msg.msg = "this is a test 1 " + i;
            mdb.putMsg(msg);

            MsgItem msg1 = new MsgItem();
            msg1.toid = 10003;
            msg1.time = System.currentTimeMillis();
            msg1.toid = roleid;
            msg1.groupid = sessionid;
            msg1.fromid = 10003;
            msg1.msg = "this is a test 2 " + i;
            mdb.putMsg(msg1);
        }
    }

    static void test2() {

        long roleid = 100001;
        long sessionid = 0;

        MsgDatabase mdb = new MsgDatabase("./", roleid);

        long friendid = 10002;
        MsgPage page = mdb.getPrePage(friendid, sessionid, -1);
        while (page != null) {
            for (MsgItem mi : page.items) {
                System.out.println(mi.msg);
            }
            page = mdb.getPrePage(friendid, sessionid, page.pageId);
        }

        friendid = 10003;
        page = mdb.getNextPage(friendid, sessionid, -1);
        while (page != null) {
            for (MsgItem mi : page.items) {
                System.out.println(mi.msg);
            }
            page = mdb.getNextPage(friendid, sessionid, page.pageId);
        }
    }

    public static void main(String[] args) {
        test1();
        test2();
    }

    void clearAll() {
        try {
            File f = new File(dataFileName);
            if (!f.exists()) {
                System.out.println("file not found on delete :" + dataFileName);
            }
            f.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            File f = new File(metaFileName);
            if (!f.exists()) {
                System.out.println("file not found on delete :" + metaFileName);
            }
            f.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
