package com.egls.client.game;

/**
 * 常量
 *
 * @author yaha
 *
 */
public class Const {

    static final public String clientVer = "1.0";
    static final public String RES_PATH = "/res/";
    static final public String RES_IMG_PATH = "/res/img/";
    static final public String SAVE_MSG_PATH = "/files/";

//    public static String serverIp = "10.0.1.9";
//    public static String serverIp = "10.0.1.11";
//    public static String serverIp = "192.168.31.164";
//    public static String serverIp = "127.0.0.1";
//    public static String serverIp = "192.168.0.253";
//    public static String serverIp = "42.62.97.181";
    public static String serverIp = "www.ebsee.com";
    public static int port = 5678;

    // *****************************************
    // *CMD*
    // *****************************************
    // CMD 类型
    public static final short CMD_SYS = 0x01;
    public static final short CMD_MAP = 0x02;
    public static final short CMD_ROLE = 0x03;
    public static final short CMD_GAMEOBJ = 0x04;
    public static final short CMD_FIGHT = 0x05;
    public static final short CMD_TEAM = 0x0A;
    public static final short CMD_MONSTER = 0x0C;
    public static final short CMD_CHAT = 0x12;
    public static final short CMD_CHATSESSION = 0x13;
    public static final short CMD_INSTANCE = 0x1C;

    // SYS CMD
    public static final short C_SYS_LOGIN = 0x0100;
    public static final short C_SYS_LOGOUT = 0x0101;
    public static final short C_SYS_XOR_KEY = 0x0103;
    public static final short C_SYS_ONLINE = 0x0108;
    public static final short C_SYS_ONLINE_USER = 0x0112;
    public static final short C_SYS_PING = 0x011C;
    public static final short C_SYS_REQUEST_CLIENT_DATA = 0x011E;
    //
    public static final short S_SYS_LOGIN = 0x0100;
    public static final short S_SYS_LOGOUT = 0x0101;
    public static final short S_SYS_MESSAGE = 0x0103;
    public static final short S_SYS_UI = 0x0104;
    public static final short S_SYS_UI_CMD = 0x0106;
    public static final short S_SYS_ONLINE_USER = 0x010A;//
    public static final short S_SYS_PING = 0x010D;
    public static final short S_SYS_SEND_CLIENT_DATA = 0x010E;
    public static final short S_SYS_SEND_MENU_LEFT_LIST = 0x0110;//左键主菜单
    public static final short S_SYS_EXEC_SCRIPT = 0x0111;//执行脚本
    public static final short S_SEND_TIPS = 0x0113;//发送提示信息
    // MAP CMD
    public static final short C_MAP_APPLY_DATA = 0x0200;// 请求三块地图
    public static final short C_MAP_GATE = 0x0201;
    public static final short C_MAP_LOADING_SUCCEED = 0x0202;
    public static final short C_MAP_WORLD_MAP = 0x0203;
    public static final short C_MAP_HOME = 0x0204;
    public static final short C_MAP_WORLD_MAP_2 = 0x0205;
    //
    public static final short S_MAP_SEND_DATA = 0x0200;// 发送九块地图
    public static final short S_MAP_CHANGE_ROOM = 0x0201;
    public static final short S_MAP_OFF_LIMITS = 0x0202;
    public static final short S_MAP_CHANGE_WEATHER = 0x0203;
    public static final short S_MAP_SEND_WORLDMAP = 0x0204;
    public static final short S_MAP_SEND_DATA_GRID = 0x0206;// 
    public static final short S_MAP_SEND_MAPMAP = 0x0207;
    public static final short S_MAP_SEND_GATES = 0x0208;// 发送门给客户端
    public static final short S_MAP_SEND_3D_PROP = 0x0209;//发送3D数据
    public static final short S_MAP_MIDI = 0x020A;//当前地图
    public static final short S_MAP_SEND_INFO = 0x020B;//当前地图可否战斗
    // ROLE CMD
    public static final short C_PLAYER_MONEY = 0x0301;
    public static final short C_PLAYER_ATTRIBUTE = 0x0302;
    public static final short C_PLAYER_RELIVE = 0x0303;
    public static final short C_PLAYER_AUTO_POTION = 0x0304;
    public static final short C_PLAYER_ESCAPE = 0x0305;
    public static final short C_PLAYER_TRANSMIT_ID = 0x0306; // 传送至玩家
    public static final short C_PLAYER_POP_FORM = 0x0307;// 弹板设置
    public static final short C_PLAYER_MOVE_OFFSET = 0x0308; //
    public static final short C_PLAYER_CANCEL_PATH = 0x0309;// 取消寻径
    public static final short C_PLAYER_FIND_PATH = 0x030A;// 寻径
    public static final short C_PLAYER_LEVEL_UP = 0x030B; // 人物升级
    public static final short C_PLAYER_POINT_INCREASE = 0x030C; // 人物加点
    public static final short C_PLAYER_SHORTCUT_DEFAULT = 0x030F;// 回复默认设置
    public static final short C_PLAYER_GOTO = 0x0310;// 寻径 到某个id的gameobj处
    public static final short C_PLAYER_TRANSMIT_XY = 0x0311;// 传送至坐标
    public static final short C_PLAYER_REQUEST_UI = 0x0314;// 请求ui
    public static final short C_PLAYER_START_FOLLOW = 0x0315;// 开始跟随
    public static final short C_PLAYER_MOVE = 0x0316; //移动到绝对位置
    public static final short C_PLAYER_SYS_CHAT = 0x0319; // 用于控制系统聊天
    public static final short C_PLAYER_HIDE_NEIGHBOR = 0x031A; // 隐藏邻居
    //
    public static final short S_PLAYER_UPDATE = 0x0300;
    public static final short S_PLAYER_MOVE = 0x0301;
    public static final short S_PLAYER_LEVELUP = 0x0302;
    public static final short S_PLAYER_EXP = 0x0304;
    public static final short S_PLAYER_MONEY = 0x0308;
    public static final short S_PLAYER_ATTACK_RANGE = 0x0309;
    public static final short S_PLAYER_MOVE_SHOW = 0x030B;// 全世界寻径路径;或走秀
    public static final short S_PLAYER_STOP_MOVE_SHOW = 0x030C;// 停止寻径
    public static final short S_PLAYER_POP_FORM = 0x030E;// 弹板设置
    public static final short S_PLAYER_SYS_CHAT = 0x031C; // 用于控制系统聊天
    // Game Object CMD
    public static final short C_GAMEOBJ_VIEW = 0x0400;
    public static final short C_GAMEOBJ_QUERY = 0x0401;
    //
    public static final short S_GAMEOBJ_ADD = 0x0400;
    public static final short S_GAMEOBJ_REMOVE = 0x0401;
    public static final short S_GAMEOBJ_MOVE = 0x0402;
    public static final short S_GAMEOBJ_LEVELUP = 0x0403;
    public static final short S_GAMEOBJ_HPMP = 0x0404;
    public static final short S_GAMEOBJ_STATE = 0x0405;
    public static final short S_GAMEOBJ_MAXHP = 0x0406;// 生命
    public static final short S_GAMEOBJ_MAXMP = 0x0407;// 法力
    public static final short S_GAMEOBJ_RELATIONSHIP = 0x0408;
    public static final short S_GAMEOBJ_TALK = 0x040A;
    public static final short S_GAMEOBJ_UPDATE_SPEED = 0x040E;
    public static final short S_GAMEOBJ_MOVE_OFFSET = 0x0410;
    public static final short S_GAMEOBJ_MOVE_IM = 0x0413;// 瞬移
    public static final short S_GAMEOBJ_CHANGE_LEVEL = 0x0419;// 怪物改变 等级    
    public static final short S_GAMEOBJ_DIRECT = 0x041B;//邻居转向
    // Fight CMD
    public static final short C_FIGHT_ATTACK = 0x0500;
    public static final short C_FIGHT_STOP = 0x0505;
    public static final short C_FIGHT_DELETE_ENEMY = 0x0506;//删除仇人
    public static final short C_FIGHT_KEEP_ENEMY = 0x0507;//保留仇人
    public static final short S_FIGHT_ATTACK = 0x0500;
    public static final short S_FIGHT_BE_ATTACKED = 0x0501;
    public static final short S_FIGHT_OTHERS = 0x0502;
    public static final short S_FIGHT_AUTO = 0x0503;//发运普通攻击
    // Team CMD
    public static final short C_TEAM_JOIN = 0x0A01;
    public static final short C_TEAM_LEAVE = 0x0A02;
    public static final short C_TEAM_DISMISS = 0x0A03;
    public static final short C_TEAM_DEMISE = 0x0A04;
    public static final short C_TEAM_KICK = 0x0A05;
    public static final short C_TEAM_ADD = 0x0A06;
    // Team CMD
    public static final short S_TEAM_ADD = 0x0A01;
    public static final short S_TEAM_REMOVE = 0x0A02;
    public static final short S_TEAM_DISMISS = 0x0A03;
    public static final short S_TEAM_LEADER_CHANGE = 0x0A04;
    public static final short S_TEAM_SET_ROOMID = 0x0A05;
    public static final short S_TEAM_SET_BUDDY_LEVEL = 0x0A06;
    // Monster CMD
    public static final short C_MONSTER_GET_MENU = 0x0C01;
    public static final short C_MONSTER_ACTIVE_TRIGGER = 0x0C02;
    public static final short C_MONSTER_GOTO = 0x0C05;
    public static final short C_MONSTER_CHEATUI = 0x0C06;
    public static final short C_MONSTER_TRANS = 0x0C07;
    public static final short S_MONSTER_QUEST_STATE = 0x0C00;
    public static final short S_MONSTER_STATE_BLUEMAP = 0x0C01;
    // Chat CMD
    public static final short C_CHAT_SPEAK = 0x1200;// 公聊
    public static final short C_CHAT_WHISPER = 0x1201;// 私聊
    public static final short C_CHAT_SETTING = 0x1202;// 聊天设置
    public static final short C_CHAT_AUTOREPLY = 0x1203;// 设置自动回复
    //
    public static final short S_CHAT_RECEIVE_MSG = 0x1200;// 发送消息给客户端

    //char session
    public static final short C_CHATSESSION_SPEAK = 0x1300;// chat 
    public static final short C_CHATSESSION_ATTACHMENT_GET = 0x1301;
    public static final short C_CHATSESSION_SPEAK_OK = 0x1302;
    public static final short C_CHATSESSION_FRIENDLIST_GET = 0x1330;//
    public static final short C_CHATSESSION_FRIEND_ADD = 0x1331;
    public static final short C_CHATSESSION_FRIEND_REMOVE = 0x1332;
    public static final short C_CHATSESSION_FRIEND_UPDATE = 0x1333;
    public static final short C_CHATSESSION_FRIEND_REQUEST = 0x1334;
    public static final short C_CHATSESSION_SESSION_ADD = 0x1360;//
    public static final short C_CHATSESSION_SESSION_LIST_GET = 0x1361;
    public static final short C_CHATSESSION_SESSION_GET = 0x1362;
    public static final short C_CHATSESSION_SESSION_UPDATE = 0x1363;
    public static final short C_CHATSESSION_SESSION_REMOVE = 0x1364;
    public static final short C_CHATSESSION_SESSION_MEMBER_ADD = 0x136A;
    public static final short C_CHATSESSION_SESSION_MEMBER_REMOVE = 0x136B;
    public static final short C_CHATSESSION_SESSION_MEMBER_UPDATE = 0x136C;
    public static final short C_CHATSESSION_RECONNECT = 0x136D;
    public static final short C_CHATSESSION_QR_DECODE = 0x136E;
    public static final short C_CHATSESSION_QR_ENCODE = 0x136F;
    public static final short C_CHATSESSION_DEVICE_TOKEN = 0x1370;
    public static final short C_CHATSESSION_ACTIVE = 0x1371;
    public static final short C_CHATSESSION_HEAD_SET = 0x1372;
    public static final short C_CHATSESSION_HEAD_GET = 0x1373;
    //
    public static final short S_CHATSESSION_SPEAK = 0x1300;// chat 
    public static final short S_CHATSESSION_ATTACHMENT_GET = 0x1301;
    public static final short S_CHATSESSION_FRIENDLIST_GET = 0x1330;//
    public static final short S_CHATSESSION_FRIEND_ADD = 0x1331;
    public static final short S_CHATSESSION_FRIEND_REMOVE = 0x1332;
    public static final short S_CHATSESSION_FRIEND_UPDATE = 0x1333;
    public static final short S_CHATSESSION_FRIEND_REQUEST = 0x1334;
    public static final short S_CHATSESSION_SESSION_ADD = 0x1360;//
    public static final short S_CHATSESSION_SESSION_LIST_GET = 0x1361;
    public static final short S_CHATSESSION_SESSION_GET = 0x1362;
    public static final short S_CHATSESSION_SESSION_UPDATE = 0x1363;
    public static final short S_CHATSESSION_SESSION_REMOVE = 0x1364;
    public static final short S_CHATSESSION_SESSION_MEMBER_ADD = 0x136A;
    public static final short S_CHATSESSION_SESSION_MEMBER_REMOVE = 0x136B;
    public static final short S_CHATSESSION_SESSION_MEMBER_UPDATE = 0x136C;
    public static final short S_CHATSESSION_QR_DECODE = 0x136E;
    public static final short S_CHATSESSION_QR_ENCODE = 0x136F;
    public static final short S_CHATSESSION_HEAD_GET = 0x1373;
    
    
    /* Ï
     * 副本命令
     */
    public static final short C_INSTANCE_JOIN = 0x1C00;// 加入副本
    public static final short C_INSTANCE_PERMIT = 0x1C01;// 同意加入守城
    public static final short C_INSTANCE_REFUSE = 0x1C02;// 拒绝加入守城
    public static final short C_INSTANCE_SET_DIFFLEVEL = 0x1C03;// 副本难度设置
    public static final short C_INSTANCE_QUERY_INFO = 0x1C04;// 查看副本信息
    public static final short C_INSTANCE_QUIT = 0x1C05;// 离开副本
    public static final short C_INSTANCE_SPECIAL_OPERATION = 0x1C06;// 特殊战场操作
    public static final short C_INSTANCE_ARENA_WATCH = 0x1C07;//观战
    //跨服指令
    public static final short C_S2S_LOGIN = 0x2A00;//登录
    public static final short C_S2S_ROLEDATA = 0x2A01;//登录角色数据
    public static final short C_S2S_LOGOUT = 0x2A02;//登出
    public static final short C_S2S_MAPBUFF = 0x2A03;//地图缓冲尺寸
    public static final short C_S2S_REMOTE_TRIGGER = 0x2A04;//远程trigger
    public static final short C_S2S_GROUP_SLAVE_LOGIN = 0x2A80;//从服务器远程登录
    public static final short C_S2S_GROUP_SLAVE_LOGOUT = 0x2A81;//从服务器登出
    public static final short C_S2S_GROUP_CHAT2ALL = 0x2A82;//从机发来的全服传话
    public static final short C_S2S_GROUP_KEEP_ONLINE = 0x2A83;//保持在线
    public static final short C_S2S_GROUP_ACTIVE_TRIGGER_2ALL = 0x2A84;//从机发来的全服执行trigger
    //
    public static final short S_S2S_GROUPSLAVE_LOGOUT = 0x2A81;//主服务器登出
    public static final short S_S2S_GROUP_CHAT2ALL = 0x2A82;//主机发来的全服传话
    public static final short S_S2S_GROUP_KEEP_ONLINE = 0x2A83;//保持在线
    public static final short S_S2S_GROUP_ACTIVE_TRIGGER_2ALL = 0x2A84;//主机发来的全服执行trigger

    //故事剧情
    public static final short C_STORY_NEXT = 0x3000;//剧情菜单选项
    public static final short S_STORY_START = 0x3000;//客户端片头剧情开始

}
