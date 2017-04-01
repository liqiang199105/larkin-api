package com.larkin.web.http.intercepter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum ApiClient {
    ANDROID("android", "A4H0P4JN", "4ce19ca8fcd150a4w4pj9llah24991ut"),
    IOS("ios", "FS20D7SU", "7dd5783804b6931e3imc3p3xz36v3sl2"),
    ;

    ApiClient(final String name, final String key, final String secretKey) {
        this.name = name;
        this.key = key;
        this.secretKey = secretKey;
    }

    private final String name;
    private final String key;
    private final String secretKey;
    private static Map<String, ApiClient> BY_NAME;
    private static Map<String, ApiClient> BY_KEY;

    public String getName() {
        return name;
    }
    public String getKey() {
        return key;
    }
    public String getSecretKey() {
        return secretKey;
    }
    public static ApiClient getByName(final String name) {
        return BY_NAME.get(name.toLowerCase());
    }
    public static ApiClient getByKey(final String key) {
        return BY_KEY.get(key.toUpperCase());
    }

    //keep these hashed by name for fast lookups
    static {
        final HashMap<String, ApiClient> byName = new HashMap<>();

        final HashMap<String, ApiClient> byKey = new HashMap<>();
        for (ApiClient client : ApiClient.values()) {
            byName.put(client.getName(), client);
            byKey.put(client.getKey(), client);
        }
        BY_NAME = Collections.unmodifiableMap(byName);
        BY_KEY = Collections.unmodifiableMap(byKey);
    }
}
