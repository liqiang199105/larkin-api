package com.larkin.web.utils;


import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.servlet.http.HttpServletRequest;



public class IPUtil {
    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    /**
     * 获取请求的非内网IP地址
     * @param request
     * @return
     */
    public static String getIpAddress(final HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (Strings.isNullOrEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (Strings.isNullOrEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (Strings.isNullOrEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (Strings.isNullOrEmpty(ip)) {
            return "";
        }

        String ips[] = ip.split(",");
        int length = ips.length;
        for(int i = 0; i < length; i++){ //取第一个不是unknown且不是内网IP的IP
            if(ips[i].trim().equalsIgnoreCase("unknown") || isInnerIPAddress(ips[i].trim())){
                continue;
            }
            return ips[i];
        }
        return ips[length - 1];
    }


    /**
     * 私有IP：127这个网段是环回地址
     * A类 :10.0.0.0-10.255.255.255
     * B类 :172.16.0.0-172.31.255.255
     * C类 :* 192.168.0.0-192.168.255.255
     */
    public static boolean isInnerIPAddress(String ipAddress) {
        boolean isInnerIp = false;
        try{
            long ipNum = getIpNum(ipAddress);
            long aBegin = getIpNum("10.0.0.0");
            long aEnd = getIpNum("10.255.255.255");
            long bBegin = getIpNum("172.16.0.0");
            long bEnd = getIpNum("172.31.255.255");
            long cBegin = getIpNum("192.168.0.0");
            long cEnd = getIpNum("192.168.255.255");
            return isInner(ipNum, aBegin, aEnd) || isInner(ipNum, bBegin, bEnd) || isInner(ipNum, cBegin, cEnd) || ipAddress.equals("127.0.0.1");
        } catch (Exception e) {
            return false;
        }
    }

    // =================================================================================================================
    // private functions
    // =================================================================================================================
    private static long getIpNum(String ipAddress) {
        String[] ip = ipAddress.split("\\.");
        long a = Integer.parseInt(ip[0]);
        long b = Integer.parseInt(ip[1]);
        long c = Integer.parseInt(ip[2]);
        long d = Integer.parseInt(ip[3]);

        long ipNum = a * 256 * 256 * 256 + b * 256 * 256 + c * 256 + d;
        return ipNum;
    }

    private static boolean isInner(long ip, long begin, long end) {
        return (ip >= begin) && (ip <= end);
    }

}
