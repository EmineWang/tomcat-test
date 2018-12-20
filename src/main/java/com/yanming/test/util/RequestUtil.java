package com.yanming.test.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class RequestUtil {
    private static final Log logger = LogFactory.getLog(RequestUtil.class);

    public RequestUtil() {
    }

    public static RequestClientInfo getRequestClientInfo(HttpServletRequest request) {
        RequestClientInfo clientInfo = new RequestClientInfo();
        clientInfo.setIp(getIp(request));
        clientInfo.setReferrer(getHostByReferrer(request.getHeader("Referer")));
        clientInfo.setAgent(request.getHeader("User-Agent"));
        return clientInfo;
    }

    public static String getHostByReferrer(String referrer) {
        if (StringUtils.isBlank(referrer)) {
            return null;
        } else {
            URL url;
            try {
                url = new URL(referrer);
            } catch (Exception var3) {
                logger.error("获取请求url对应域名出错");
                throw new RuntimeException("获取请求url对应域名出错", var3);
            }

            return url.getHost();
        }
    }

    public static String getAppHost(HttpServletRequest request) {
        StringBuffer url = request.getRequestURL();
        return url.delete(url.length() - request.getRequestURI().length(), url.length()).toString();
    }

    public static String getAppHostWithContext(HttpServletRequest request) {
        return getAppHost(request) + request.getServletContext().getContextPath();
    }

    public static String buildRequestUrl(HttpServletRequest request) {
        StringBuffer originalURL = request.getRequestURL();
        String url = originalURL.toString();
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters != null && parameters.size() > 0) {
            originalURL.append("?");
            Iterator i$ = parameters.keySet().iterator();

            while (i$.hasNext()) {
                String key = (String) i$.next();
                String[] values = (String[]) parameters.get(key);

                for (int i = 0; i < values.length; ++i) {
                    originalURL.append(key).append("=").append(values[i]).append("&");
                }
            }

            url = originalURL.substring(0, originalURL.length() - 1);
        }

        return url;
    }

    public static void logRequestDetail(HttpServletRequest request) {
        logger.debug("请求的详细属性为：");
        logger.debug("参数: ");
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters != null && parameters.size() > 0) {
            Iterator i$ = parameters.keySet().iterator();

            while (i$.hasNext()) {
                String key = (String) i$.next();
                String[] values = (String[]) parameters.get(key);

                for (int i = 0; i < values.length; ++i) {
                    logger.debug("param: [" + key + "] " + values[i]);
                }
            }
        }

        logger.debug("其它: ");
        logger.debug("Protocol: " + request.getProtocol());
        logger.debug("Scheme: " + request.getScheme());
        logger.debug("Server Name: " + request.getServerName());
        logger.debug("Server Port: " + request.getServerPort());
        logger.debug("Server Info: " + request.getServletContext().getServerInfo());
        logger.debug("Remote Addr: " + request.getRemoteAddr());
        logger.debug("Remote Host: " + request.getRemoteHost());
        logger.debug("Remote User" + request.getRemoteUser());
        logger.debug("Character Encoding: " + request.getCharacterEncoding());
        logger.debug("Content Length: " + request.getContentLength());
        logger.debug("Content Type: " + request.getContentType());
        logger.debug("Auth Type: " + request.getAuthType());
        logger.debug("HTTP Method: " + request.getMethod());
        logger.debug("Path Info: " + request.getPathInfo());
        logger.debug("Path Trans: " + request.getPathTranslated());
        logger.debug("Query String: " + request.getQueryString());
        logger.debug("Session Id: " + request.getRequestedSessionId());
        logger.debug("Request URI: " + request.getRequestURI());
        logger.debug("Servlet Path: " + request.getServletPath());
        logger.debug("Accept: " + request.getHeader("Accept"));
        logger.debug("Host: " + request.getHeader("Host"));
        logger.debug("Referer : " + request.getHeader("Referer"));
        logger.debug("Accept-Language : " + request.getHeader("Accept-Language"));
        logger.debug("Accept-Encoding : " + request.getHeader("Accept-Encoding"));
        logger.debug("User-Agent : " + request.getHeader("User-Agent"));
        logger.debug("Connection : " + request.getHeader("Connection"));
        logger.debug("Cookie : " + request.getHeader("Cookie"));
        logger.debug("Session Created : " + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")).format(new Date(request.getSession().getCreationTime())));
        logger.debug("Session LastAccessed : " + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")).format(new Date(request.getSession().getLastAccessedTime())));
    }

    public static String getIp(HttpServletRequest request) {
        String ip = null;
        ip = request.getHeader("X-Forwarded-For");
        if (isRealIP(ip)) {
            return getRealIp(ip);
        } else {
            ip = request.getHeader("Proxy-Client-IP");
            if (isRealIP(ip)) {
                return getRealIp(ip);
            } else {
                ip = request.getHeader("WL-Proxy-Client-IP");
                if (isRealIP(ip)) {
                    return getRealIp(ip);
                } else {
                    ip = request.getHeader("HTTP_CLIENT_IP");
                    if (isRealIP(ip)) {
                        return getRealIp(ip);
                    } else {
                        ip = request.getHeader("HTTP_X_FORWARDED_FOR");
                        if (isRealIP(ip)) {
                            return getRealIp(ip);
                        } else {
                            ip = request.getParameter("__fromReferIP");
                            if (isRealIP(ip)) {
                                return getRealIp(ip);
                            } else {
                                ip = request.getHeader("X-Real-IP");
                                if (isRealIP(ip)) {
                                    return getRealIp(ip);
                                } else {
                                    ip = request.getRemoteAddr();
                                    return ip;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static boolean isRealIP(String ip) {
        return StringUtils.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip);
    }

    private static String getRealIp(String ip) {
        return ip.indexOf(",") != -1 ? StringUtils.left(ip.split(",")[0], 15) : ip;
    }

    public static boolean isJsonAjaxRequest(HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        return accept != null && accept.indexOf("application/json") != -1;
    }

    public static Map<String, String> getParameterMap(HttpServletRequest request) {
        Map<String, String[]> properties = request.getParameterMap();
        Map<String, String> returnMap = new HashMap();
        String value = "";

        String name;
        for (Iterator i$ = properties.entrySet().iterator(); i$.hasNext(); returnMap.put(name, value)) {
            Map.Entry<String, String[]> entry = (Map.Entry) i$.next();
            name = (String) entry.getKey();
            String[] values = (String[]) entry.getValue();
            if (values == null) {
                value = "";
            } else {
                for (int i = 0; i < values.length; ++i) {
                    value = values[i] + ",";
                }

                value = value.substring(0, value.length() - 1);
            }
        }

        return returnMap;
    }

    public static String getLocalRealIp() throws SocketException {
        String localip = null;
        String netip = null;
        Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
        InetAddress ip = null;
        boolean finded = false;

        while (netInterfaces.hasMoreElements() && !finded) {
            NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();
            Enumeration address = ni.getInetAddresses();

            while (address.hasMoreElements()) {
                ip = (InetAddress) address.nextElement();
                if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {
                    netip = ip.getHostAddress();
                    finded = true;
                    break;
                }

                if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {
                    localip = ip.getHostAddress();
                }
            }
        }

        return netip != null && !"".equals(netip) ? netip : localip;
    }

    public static String getRequestIgnoreCaseParameter(HttpServletRequest request, String parameter) {
        String paramValue = null;
        Enumeration pnameEnum = request.getParameterNames();

        while (pnameEnum.hasMoreElements()) {
            String paramName = (String) pnameEnum.nextElement();
            if (paramName.equalsIgnoreCase(parameter)) {
                paramValue = request.getParameter(paramName);
                break;
            }
        }

        return paramValue;
    }
}
