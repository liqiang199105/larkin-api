package com.larkin.web.http.intercepter;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class ApiSignatureUtil {

    public static final String SIGNATURE = "sign";
    public static final String TIMESTAMP = "timestamp";
    public static final String USER_ID = "userId";
    public static final String LOGIN_TOKEN = "token";
    public static final String APP_KEY = "appKey";
    public static final String APP_SECRET = "appSecret";
    public static final String NONCE_STRING = "nonceStr";  // RandomStringUtils.randomAlphabetic(16);

    /***
     * This method generates the signature given the parameters.  NOTE: it does *no* error-checking
     * (for the sake of speed) and will throw various errors if the parameters aren't formed as expected.
     * @param parameters - the parameters passed to the method.
     * @return The signature for the keys.  NOTE: this method does no verification that the private key is one of the keys.
     * If you're passing in a private key, make sure it's one of the keys.
     * @throws UnsupportedEncodingException
     */

    public static String generateSignature(final Map<String, String> parameters) throws UnsupportedEncodingException {
        List<String> sortedKeys = new ArrayList<>(parameters.keySet());
        Collections.sort(sortedKeys);
        StringBuilder sb = new StringBuilder();
        for (String key : sortedKeys) {
            sb.append(key);
            sb.append(parameters.get(key));
        }
        return DigestUtils.md5DigestAsHex(sb.toString().getBytes("UTF-8")).toLowerCase();
    }

}
