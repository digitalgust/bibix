package com.egls.client.util;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.Random;
import org.mini.gui.GImage;

/**
 * 所有的文字放入此类中，另外一些工具方法可放入其中,此类一般不在游戏中初始化
 *
 */
public class Util {

    /**
     * 主菜单的字符串ID
     */
    static public final short[] STRS_ID_MAINMENU = {
        0, 3, 6};
    static public final short STR_ID_ABOUTMORE = 7; //“关于”中详细内容的ID

    /**
     * 帮助标题的ID
     */
    static public final short[] STRS_ID_HELPTITLE = {
        8, 9, 10, 11};

    /**
     * 帮助详细内容的ID
     */
    static public final short[] STRS_ID_HELPMORE = {
        12, 13, 14, 15};

    /**
     * 参数设置项
     */
    static public final short[] STRS_ID_OPTION = {
        17, 28};

    /**
     * 其它内容
     */
    static public final short STR_INTRO = 4//
            , STR_RETURN = 16//
            , STR_ABOUT = 5//
            , STR_SETUP = 3//
            , STR_AUDIO = 17//
            , STR_WAITING = 18//
            , STR_OPEN = 25//
            , STR_CLOSE = 26//
            , STR_FIRE_TO_CHANGE = 27//
            , STR_GOTO_ENEMY = 29//
            ;

    //;--------------------------------  参数相关  -----------------------------
    /**
     * 参数保存的文件名
     */
    static private final String optionRmsName = "setup"; //存储设置的RMS名

    /**
     * 当前声音是否开启
     */
    static public boolean audioOpen = true; //声音是否打开


    //;--------------------------------  取得字符串资源  -----------------------------
    /**
     * 所有的语言版本 通过增减此数组中的个数，可以适当支持语言的版本数量,也要调整LANG_*的值
     */
    static public final short[] STRS_ID_LANG = {
        19, 20, 21, 22, 23, 24
    };

    /**
     * 语言定义
     */
    static public final byte LANG_CN = 0 //中文
            , LANG_EN = 1 //英文
            , LANG_FR = 2 //法
            , LANG_DE = 3 //德
            , LANG_IT = 4 //意
            , LANG_ES = 5 //西
            ;

    /**
     * 此变量标示其当前语言为何种语言,此为用户定义当前应显示何种语言
     */
    static public byte lang = LANG_CN;

    /**
     * 当前的strRes中为什么语言
     */
    static private byte curStrLang = LANG_CN;

    /**
     * 必须用这个接口方法，因为有时候资源太多时，可能资源放在文件里，并不是全部都存在于strRes[]里面
     * 这样，当要取得某串时，就要用别的方法读取，所以，最好使用接口方法，并且当多语言存在时，这里可以跟 适当的参数，以取得不同的语言版本
     *
     * @param strID int
     * @return String
     */
    static public final String getStr(int strID) {
        if (lang != curStrLang || strRes == null) { //用户要求显示的语言和资源中的语言对不起来,或资源数组为空，则重新读入资源,
            readStrRes();
        }
        return strRes[strID];
    }

    /**
     * 返回一组字符串,以一个整形数组所给的参数为例
     *
     * @param arrStrID int[]
     * @return String[]
     */
    static public final String[] getStrs(short[] arrStrID) {
        if (lang != curStrLang || strRes == null) { //用户要求显示的语言和资源中的语言对不起来,或资源数组为空，则重新读入资源,
            readStrRes();
        }
        if (arrStrID == null) {
            return null;
        } else {
            String[] tmpStrs = new String[arrStrID.length];
            for (int i = 0; i < arrStrID.length; i++) {
                tmpStrs[i] = strRes[arrStrID[i]];
            }
            return tmpStrs;
        }
    }

    /**
     * 从文件中，读入串资源 此处的en.lng等文件，使用BuildLang工具自动生成，使用方法见BuildLang帮助
     */
    static private final void readStrRes() {
        try {
            String fileName = "";
            switch (lang) {
                case LANG_CN:
                    fileName = "cn";
                    break;
                case LANG_EN:
                    fileName = "en";
                    break;
                case LANG_FR:
                    fileName = "fr";
                    break;
                case LANG_DE:
                    fileName = "de";
                    break;
                case LANG_IT:
                    fileName = "it";
                    break;
                case LANG_ES:
                    fileName = "es";
                    break;
            }

            //此处读的.lng文件，由工具生成
            InputStream is = Util.class.getResourceAsStream("/" + fileName + ".lng");
            DataInputStream dis = new DataInputStream(is);
            short strCount = dis.readShort(); //得到字符串总个数
            strRes = new String[strCount]; //初始化数组
            for (int i = 0; i < strCount; i++) { //读出所有字符串
                strRes[i] = dis.readUTF();
            }

            dis.close();
            dis = null;
            is = null;
        } catch (Exception ex) {
        }
        curStrLang = lang; //标注当前的资源数组的语言为新读出的语言
    }

    /**
     * 这里是当前语言
     */
    static private String[] strRes = { //这个数组赋了值，这里是为了调试方便，正式使用时，应设为空,因为其内容已在cn.lng里面了
        "新游戏" //0游戏主菜单
        , "继续游戏" //1
        , "调出记录" //2
        , "游戏设置" //3
        , "帮助说明" //4
        , "关于" //5
        , "退出" //6
        , "天津猛犸科技\nwww.mammothworld.com\n" //7关于
        , "游戏简介" //8帮助标题
        , "使用规则" //9
        , "操作方法" //10
        , "客服信息" //11
        , "......1The implementation indicates its support for traversal internal to a CustomItem by setting one or both of the TRAVERSE_HORIZONTAL or TRAVERSE_VERTICAL bits in the value returned by getInteractionModes(). If neither of these bits is set, the implementation is unwilling to let CustomItems traverse internally, or the implementation does not support traversal at all. If the implementation does support traversal but has declined to permit traversal internal to CustomItems, the implementation will supply its own highlighting outside the CustomItem's content area.游戏简介具体内容" //12帮助具体内容
        , "......2使用规则具体内容" //13
        , "......3操作方法具体内容" //14
        , "......4客服信息具体内容" //15
        , "返回" //16其它常用
        , "声音" //17
        , "请稍候..." //18
        , "简体中文" //19
        , "英文" //20
        , "法文" //21
        , "德文" //22
        , "意大利文" //23
        , "西班牙文" //24
        , "开" //25
        , "关" //26
        , "按5键更改" //27
        , "语言" //28
        , "寻径到敌人处" //29
    };



    //;--------------------------------  产生随机数  -----------------------------
    private static Random random = new Random(); //定义一个随机值

    /**
     * 产生一个随机数，这个数一定是正数
     *
     * @return int 返回一个正数
     */
    public static int genRandom() {
        return random.nextInt() << 1 >>> 1; //去掉符号位
    }


}
