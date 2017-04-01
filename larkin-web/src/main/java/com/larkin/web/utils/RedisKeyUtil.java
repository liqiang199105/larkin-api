package com.larkin.web.utils;

public class RedisKeyUtil {
    public static final String PAINTER = "Painter#";


    /**
     * 手机验证码缓存
     * @param phone
     * @return
     */
    public static String getUserVerifyCodeKey(final String phone){
        return PAINTER + phone + "#";
    }

    /**
     * 防止恶意刷短信接口，三秒内不能重复发。
     * @param phone
     * @return
     */
    public static String getUserVerifyCodeTime(final String phone){
        return PAINTER  + "Expire#" + phone;
    }

    /**
     * 获取用户的token
     * @param userId
     * @return
     */
    public static String getUserToken(final String userId){
        return PAINTER + userId + "#";
    }

    /**
     * 主播昵称index
     * @return
     */
    public static String getUserNickIndex(){
        return PAINTER + "Index#";
    }

    /**
     * 获取APP的版本号
     * @param app
     * @return
     */
    public static String getAppVersion(String app){
        return PAINTER + "AppVersion#" + app;
    }

    /**
     * 获取APP的下载地址
     * @param app
     * @return
     */
    public static String getAppDownloadUrl(String app){
        return PAINTER + "AppDownloadUrl#" + app;
    }

    /**
     * APP是否强制升级
     * @param app
     * @return
     */
    public static String getAppUpgradeLevel(String app){

        return PAINTER + "AppUpgradeLevel#" + app;
    }
    /**
     * APP 升级描述
     * @param app
     * @return
     */
    public static String getAppUpgradeDesc(String app){
        return PAINTER + "AppUpgradeDesc#" + app;
    }


}
