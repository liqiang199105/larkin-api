package com.larkin.web.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 发送报警工具类
 * Created by zxwu on 2015/8/19.
 */
public class NotifyUtil {
    private static Logger logger = LoggerFactory.getLogger(NotifyUtil.class);
    private static final String API_URL = "http://61.135.251.33:8163/?group=%s&item=%s&msg=%s";
    private static final String ENCODING = "UTF-8";

    // 默认报警组
    public static String GROUP_DEFAULT = "bobo";
    // 主站
    public static String GROUP_WEB = "bobo.web";
    // 直播间
    public static String GROUP_ROOM = "bobo.room";
    // CMS
    public static String GROUP_CMS = "bobo.cms";
    // 财务
    public static String GROUP_FINANCE = "bobo.finance";
    // 客服
    public static String GROUP_CALLCENTER = "bobo.callcenter";

    public static void main(String[] args) {
        notifyRoom("test", "hello world!");
    }

    /**
     * 发送报警
     * @param group 报警配置组
     * @param item 报警业务项
     * @param msg 消息内容
     */
    public static void notify___(String group, String item, Object msg) {
        doGet(group, item, msg, 1);
    }

    /**
     * 发送报警到主站组
     * @param item 报警业务项
     * @param msg 消息内容
     */
    public static void notifyWeb(String item, Object msg) {
        doGet(GROUP_WEB, item, msg, 1);
    }

    /**
     * 发送报警到主站组
     * @param item 报警业务项
     * @param msg 消息内容
     */
    public static void notifyWeb(Class<?> item, Object msg) {
        doGet(GROUP_WEB, item.getSimpleName(), msg, 1);
    }

    /**
     * 发送报警到直播间组
     * @param item 报警业务项
     * @param msg 消息内容
     */
    public static void notifyRoom(String item, Object msg) {
        doGet(GROUP_ROOM, item, msg, 1);
    }

    /**
     * 发送报警到直播间组
     * @param item 报警业务项
     * @param msg 消息内容
     */
    public static void notifyRoom(Class<?> item, Object msg) {
        doGet(GROUP_ROOM, item.getSimpleName(), msg, 1);
    }

    /**
     * 发送报警到CMS组
     * @param item 报警业务项
     * @param msg 消息内容
     */
    public static void notifyCMS(String item, Object msg) {
        doGet(GROUP_CMS, item, msg, 1);
    }

    /**
     * 发送报警到CMS组
     * @param item 报警业务项
     * @param msg 消息内容
     */
    public static void notifyCMS(Class<?> item, Object msg) {
        doGet(GROUP_CMS, item.getSimpleName(), msg, 1);
    }

    /**
     * 发送报警到财务组
     * @param item 报警业务项
     * @param msg 消息内容
     */
    public static void notifyFinance(String item, Object msg) {
        doGet(GROUP_FINANCE, item, msg, 1);
    }

    /**
     * 发送报警到财务组
     * @param item 报警业务项
     * @param msg 消息内容
     */
    public static void notifyFinance(Class<?> item, Object msg) {
        doGet(GROUP_FINANCE, item.getSimpleName(), msg, 1);
    }

    /**
     * 发送报警到客服组
     * @param item 报警业务项
     * @param msg 消息内容
     */
    public static void notifyCallcenter(String item, Object msg) {
        doGet(GROUP_CALLCENTER, item, msg, 1);
    }

    /**
     * 发送报警到客服组
     * @param item 报警业务项
     * @param msg 消息内容
     */
    public static void notifyCallcenter(Class<?> item, Object msg) {
        doGet(GROUP_CALLCENTER, item.getSimpleName(), msg, 1);
    }

    /**
     * 向logserver发送心跳事件
     * @param group 组
     * @param item 对象
     * @param msg 附加信息
     * @return 写入成功返回true
     */
    public static boolean heartbeat(String group, String item, String msg) {
        return doGet(group, item, msg, 0);
    }

    /**
     * 向logserver发送心跳事件
     * @param group 组
     * @param item 对象
     * @param msg 附加信息
     * @return 写入成功返回true
     */
    public static boolean heartbeat(String group, Class<?> item, String msg) {
        return heartbeat(group, item.getSimpleName(), msg);
    }

    /**
     * 向logserver发送直接警告事件
     * @param group 组
     * @param item 对象
     * @param e 错误堆栈信息
     * @return 写入成功返回true
     */
    public static boolean alarm(String group, String item, Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.flush();
        // 将linux下换行符\n统一换成windows下的\r\n，兼容popo的换行格式
        String msg = sw.toString().replace("\r", "").replace("\n", "\r\n");
        return doGet(group, item, msg, 1);
    }

    private static boolean doGet(String group, String item, Object msg, int proxy) {
        String message;
        if (msg == null) {
            message = "null";
        } else {
            message = msg.toString();
        }

        String url;
        try {
            url = String.format(API_URL, URLEncoder.encode(group, ENCODING),
                    URLEncoder.encode(item, ENCODING),
                    URLEncoder.encode(message, ENCODING));
        } catch (UnsupportedEncodingException e1) {
            url = String.format(API_URL, group, item, message);
        }

        if (proxy == 1) {
            url += "&proxy=" + proxy;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("get URL [" + url + "]");
        }
        try {
            URL httpUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
            conn.setConnectTimeout(1000);
            conn.setUseCaches(false);
            int code = conn.getResponseCode();
            if (code >= 300) {
                logger.error("get URL [" + url + "] error: HTTP/1.1 " + code
                        + " " + conn.getResponseMessage());
                return false;
            }
        } catch (Exception e) {
            logger.error("get URL [" + url + "] error", e);
            return false;
        }
        return true;
    }
}
