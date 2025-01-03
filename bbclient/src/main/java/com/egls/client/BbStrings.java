/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egls.client;

import org.mini.apploader.GApplication;
import org.mini.gui.GLanguage;

/**
 * @author Gust
 */
public class BbStrings {
    static GApplication sapp;

    static public void loadString(GApplication app) {
        sapp = app;
        app.regString("Exit", new String[]{"Exit", "退出", "退出"});
        app.regString("My", new String[]{"My", "我的", "我的"});
        app.regString("Map", new String[]{"Map", "地图", "地圖"});
        app.regString("Session", new String[]{"Back", "列表", "列表"});
        app.regString("__Add", new String[]{"+ Add", "+ 添加", "+ 添加"});
        app.regString("Add_Friend", new String[]{"Add Friend", "添加好友", "添加好友"});
        app.regString("Input_the_bbid:", new String[]{"Input the bbid, or scan QR code.", "输入好友bbid,或扫描二维码.", "輸入好友bbid,或掃描二維碼."});
        app.regString("Add", new String[]{"Add", "添加", "添加"});
        app.regString("Cancel", new String[]{"Cancel", "取消", "取消"});
        app.regString("State", new String[]{"State", "状态", "狀態"});
        app.regString("bbid_not_illegal", new String[]{"bbid not illegal", "bbid 不正确", "bbid 不正確"});
        app.regString("Remember_me", new String[]{"Remember me", "记住", "記住"});
        app.regString("Sign_in_up", new String[]{"Sign in/up", "登入/注册", "登入/注冊"});
        app.regString("Login", new String[]{"Login", "登录", "登入"});
        app.regString("Auto_sign_up_if_account_not_exists", new String[]{"Auto sign up if account not exists", "帐号不存在时将注册", "帳號不存在時將注冊"});
        app.regString("BiBi", new String[]{"BiBi", "BiBi", "BiBi"});
        app.regString("Email", new String[]{"Email", "邮件地址", "電郵"});
        app.regString("Password", new String[]{"Password", "密码", "密碼"});
        app.regString("Request_list", new String[]{"Request list", "好友申请列表:", "好友申請列表:"});
        app.regString("Cancel", new String[]{"Cancel", "取消", "取消"});
        app.regString("Decline", new String[]{"Decline", "拒绝", "拒絕"});
        app.regString("Clear_Message", new String[]{"Clear Message", "删除所有消息", "刪除所有消息"});
        app.regString("Remove_Friend", new String[]{"Remove Friend", "移除好友", "移除好友"});
        app.regString("Add_Member", new String[]{"Add Member", "添加成员", "添加成員"});
        app.regString("Ok", new String[]{"Ok", "确定", "確定"});
        app.regString("Delete", new String[]{"Delete", "删除", "删除"});
        app.regString("Are_you_sure_", new String[]{"Are you sure?", "你确定吗?", "你確定嗎?"});
        app.regString("Remove_Member", new String[]{"Remove Member", "移除成员", "移除成員"});
        app.regString("Information", new String[]{"Information", "信息", "資訊"});
        app.regString("Remove", new String[]{"Remove", "移除", "移除"});
        app.regString("Camera_Scan", new String[]{"Camera Scan", "扫一扫", "掃一掃"});
        app.regString("Change", new String[]{"Change", "修改", "修改"});
        app.regString("Logout", new String[]{"Logout", "退出登录", "登出"});
        app.regString("Qr_Code", new String[]{"Qr Code", "二维码", "二維碼"});
        app.regString("Send_Photo", new String[]{"Send Photo", "发送照片", "發送照片"});
        app.regString("Camera", new String[]{"Camera", "拍摄照片", "拍攝照片"});
        app.regString("Voice", new String[]{"Voice", "发送语音", "發送語音"});
        app.regString("VoiceNo_w", new String[]{"Telephone", "电话", "電話"});
        app.regString("Send", new String[]{"Send", "发送", "發送"});
        app.regString("Request_be_friend", new String[]{"Request be friend", "请求添加好友", "請求添加好友"});
        app.regString("Account", new String[]{"Account", "账号", "账号"});
        app.regString("My_BBID", new String[]{"My BBID", "我的BBID", "我的BBID"});
        app.regString("Record", new String[]{"Record", "录音", "錄音"});
        app.regString("Stop", new String[]{"Stop", "停止", "停止"});
        app.regString("Playback", new String[]{"Playback", "播放", "播放"});
        app.regString("Audio_Record", new String[]{"Audio Decoder", "录音机", "錄音機"});
        app.regString("Clear_All", new String[]{"Clear All", "清除所有记录", "清隊所有記錄"});
        app.regString("Notify", new String[]{"Notify", "消息", "消息"});
        app.regString("Qr_Code_is_generating", new String[]{"Qr Code is generating", "二维码正在生成", "二維碼正在生成"});
        app.regString("Submitted_change", new String[]{"Submitted change", "已提交修改", "已提交修改"});
        app.regString("It_s_in_building_energy_wasted", new String[]{"It's in building, energy wasted", "功能开发中,很耗电", "功能開發中,能耗嚴重"});
        app.regString("English", new String[]{"English", "English", "English"});
        app.regString("简体中文", new String[]{"简体中文", "简体中文", "简体中文"});
        app.regString("繁體中文", new String[]{"繁體中文", "繁體中文", "繁體中文"});
        app.regString("Not_available", new String[]{"Not available", "不可用", "不可用"});
        app.regString("More", new String[]{"More", "更多", "更多"});
        app.regString("Forward", new String[]{"Forward", "转发", "轉發"});
        app.regString("Exit_to_AppManager", new String[]{"Exit to AppManager", "退到应用管理器", "退到應用管理器"});
    }

    public static String getString(String key) {
        return sapp.getString(key);
    }
}
