/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egls.client;

import org.mini.gui.GLanguage;

/**
 *
 * @author Gust
 */
public class BbStrings extends GLanguage {

    static {
        addString("My", new String[]{"My", "我的", "我的"});
        addString("Map", new String[]{"Map", "地图", "地圖"});
        addString("Session", new String[]{"Back", "列表", "列表"});
        addString("+ Add", new String[]{"+ Add", "+ 添加", "+ 添加"});
        addString("Add Friend", new String[]{"Add Friend", "添加好友", "添加好友"});
        addString("Input the bbid:", new String[]{"Input the bbid, or scan QR code.", "输入好友bbid,或扫描二维码.", "輸入好友bbid,或掃描二維碼."});
        addString("Add", new String[]{"Add", "添加", "添加"});
        addString("Cancel", new String[]{"Cancel", "取消", "取消"});
        addString("State", new String[]{"State", "状态", "狀態"});
        addString("bbid not illegal", new String[]{"bbid not illegal", "bbid 不正确", "bbid 不正確"});
        addString("Remember me", new String[]{"Remember me", "记住", "記住"});
        addString("Sign in/up", new String[]{"Sign in/up", "登入/注册", "登入/注冊"});
        addString("Login", new String[]{"Login", "登录", "登錄"});
        addString("Email (auto sign up if not exists)", new String[]{"Email (auto sign up if not exists)", "帐号(不存在时将注册)", "帳號(不存在時將注冊)"});
        addString("BiBi", new String[]{"BiBi", "BiBi", "BiBi"});
        addString("Email", new String[]{"Email", "邮件地址", "電郵"});
        addString("Password", new String[]{"Password", "密码", "密碼"});
        addString("Request list", new String[]{"Request list", "好友申请列表:", "好友申請列表:"});
        addString("Cancel", new String[]{"Cancel", "取消", "取消"});
        addString("Decline", new String[]{"Decline", "拒绝", "拒絕"});
        addString("Clear Message", new String[]{"Clear Message", "删除所有消息", "刪除所有消息"});
        addString("Remove Friend", new String[]{"Remove Friend", "移除好友", "移除好友"});
        addString("Add Member", new String[]{"Add Member", "添加成员", "添加成員"});
        addString("Ok", new String[]{"Ok", "确定", "確定"});
        addString("Delete", new String[]{"Delete", "删除", "删除"});
        addString("Are you sure?", new String[]{"Are you sure?", "你确定吗?", "你確定嗎?"});
        addString("Remove Member", new String[]{"Remove Member", "移除成员", "移除成員"});
        addString("Infomation", new String[]{"Infomation", "信息", "資訊"});
        addString("Remove", new String[]{"Remove", "移除", "移除"});
        addString("Camera Scan", new String[]{"Camera Scan", "扫一扫", "掃一掃"});
        addString("Change", new String[]{"Change", "修改", "修改"});
        addString("Logout", new String[]{"Logout", "退出登录", "退出登錄"});
        addString("Qr Code", new String[]{"Qr Code", "二维码", "二維碼"});
        addString("Send Photo", new String[]{"Send Photo", "发送照片", "發送照片"});
        addString("Camera", new String[]{"Camera", "拍摄照片", "拍攝照片"});
        addString("Voice", new String[]{"Voice", "发送语音", "發送語音"});
        addString("VoiceNow", new String[]{"Telephone", "电话", "電話"});
        addString("Send", new String[]{"Send", "发送", "發送"});
        addString("Request be friend", new String[]{"Request be friend", "请求添加好友", "請求添加好友"});
        addString("Account : ", new String[]{"Account : ", "账号 : ", "账号 : "});
        addString("My BBID : ", new String[]{"My BBID : ", "我的BBID : ", "我的BBID : "});
        addString("Record", new String[]{"Record", "录音", "錄音"});
        addString("Stop", new String[]{"Stop", "停止", "停止"});
        addString("Playback", new String[]{"Playback", "播放", "播放"});
        addString("Audio Recodr", new String[]{"Audio Recoder", "录音机", "錄音機"});
        addString("Clear All", new String[]{"Clear All", "清除所有记录", "清隊所有記錄"});
        addString("Notify", new String[]{"Notify", "消息", "消息"});
        addString("Qr Code is generating", new String[]{"Qr Code is generating", "二维码正在生成", "二維碼正在生成"});
        addString("Submited change", new String[]{"Submited change", "已提交修改", "已提交修改"});
        addString("It's in building, energy wasted", new String[]{"It's in building, energy wasted", "功能开发中,很耗电", "功能開發中,能耗嚴重"});
        addString("English", new String[]{"English", "English", "English"});
        addString("简体中文", new String[]{"简体中文", "简体中文", "简体中文"});
        addString("繁體中文", new String[]{"繁體中文", "繁體中文", "繁體中文"});
        addString("Not available", new String[]{"Not available", "不可用", "不可用"});
        addString("More", new String[]{"More", "更多", "更多"});
        addString("Forward", new String[]{"Forward", "转发", "轉發"});
        addString("Exit to AppManager", new String[]{"Exit to AppManager", "退到应用管理器", "退到應用管理器"});
    }

    public static String getString(String key) {
        return GLanguage.getString(key);
    }
}
