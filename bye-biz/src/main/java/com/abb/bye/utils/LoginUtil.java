package com.abb.bye.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.security.InvalidKeyException;

/**
 * @author cenpeng.lwm
 * @since 2019/5/26
 */
public class LoginUtil {
    private static Logger logger = LoggerFactory.getLogger(LoginUtil.class);
    private static String clientKey = "bye";
    private static String loginCookieName = "__login_user_id__";
    private static Boolean useSecureCookie = false;
    private static Method setHttpOnlyMethod;
    public static final String ENCODING_UTF8 = "UTF-8";
    private static Cipher clientCookieEncryptCipher;
    private static Cipher clientCookieDecryptCipher;

    static {
        clientCookieEncryptCipher = EncodeUtil.getCipher(clientKey, true);
        clientCookieDecryptCipher = EncodeUtil.getCipher(clientKey, false);
        setHttpOnlyMethod = ReflectionUtils.findMethod(Cookie.class, "setHttpOnly", boolean.class);
    }

    public static String encodeCookie(String value) throws InvalidKeyException, IllegalBlockSizeException,
        BadPaddingException, UnsupportedEncodingException {
        return EncodeUtil.encodeText(clientCookieEncryptCipher, value, ENCODING_UTF8);
    }

    public static String decodeCookie(String value) throws InvalidKeyException, IllegalBlockSizeException,
        BadPaddingException, UnsupportedEncodingException {
        return EncodeUtil.decodeText(clientCookieDecryptCipher, value, ENCODING_UTF8);
    }

    private static String getCookiePath(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        return contextPath.length() > 0 ? contextPath : "/";
    }

    public static String getLoginUser(HttpServletRequest request) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, UnsupportedEncodingException {
        Cookie[] cookies = request.getCookies();
        if ((cookies == null) || (cookies.length == 0)) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (loginCookieName.equals(cookie.getName())) {
                return decodeCookie(cookie.getValue());
            }
        }
        return null;
    }

    public static void removeCookie(String domain, HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = new Cookie(loginCookieName, null);
        cookie.setMaxAge(0);
        cookie.setPath(getCookiePath(request));
        if (domain != null) {
            cookie.setDomain(domain);
        }
        response.addCookie(cookie);
    }

    public static void setLoginCookie(String domain, String value, int maxAge, HttpServletRequest request, HttpServletResponse response)
        throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, UnsupportedEncodingException {
        String cookieValue = encodeCookie(value);
        Cookie cookie = new Cookie(loginCookieName, cookieValue);
        cookie.setMaxAge(maxAge);
        cookie.setPath(getCookiePath(request));
        if (domain != null) {
            cookie.setDomain(domain);
        }
        if (maxAge < 1) {
            cookie.setVersion(1);
        }
        if (useSecureCookie == null) {
            cookie.setSecure(request.isSecure());
        } else {
            cookie.setSecure(useSecureCookie);
        }
        if (setHttpOnlyMethod != null) {
            ReflectionUtils.invokeMethod(setHttpOnlyMethod, cookie, Boolean.TRUE);
        } else if (logger.isDebugEnabled()) {
            logger.debug("Note: Cookie will not be marked as HttpOnly because you are not using Servlet 3.0 (Cookie#setHttpOnly(boolean) was not found).");
        }
        response.addCookie(cookie);
    }

    public static void main(String[] args) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, UnsupportedEncodingException {
        String c = encodeCookie("xxxxx");
        System.out.println(c);
        System.out.println(decodeCookie(c));
    }
}
