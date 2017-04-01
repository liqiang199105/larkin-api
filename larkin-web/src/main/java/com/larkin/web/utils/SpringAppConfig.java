package com.larkin.web.utils;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public enum SpringAppConfig {


    APP_ENV("app.env", "dev"),
    OS_NAME("os.name", System.getProperty("os")),
    APP_VERSION_GIT_COMMIT("app.version.git.commit", getStaticManifest("App-Version-Git-Commit"), true),
    APP_VERSION_GIT_COMMIT_TIME("app.version.git.commit.time", getStaticManifest("App-Version-Git-Commit-Time"), true),
    APP_VERSION_GIT_BRANCH("app.version.git.branch", getStaticManifest("App-Version-Git-Branch"), true),
    APP_VERSION_MAVEN("app.version.maven", getStaticManifest("App-Version-Maven"), true),
    APP_CLASSPATHS("app.classpaths", getStaticManifest("Class-Path"), true),
    APP_DEPLOYTIME("app.deploytime", String.valueOf(System.currentTimeMillis()));

    private String key;
    private String defaultValue;
    private boolean allowNull;

    SpringAppConfig(final String key) {
        this.key = key;
    }

    SpringAppConfig(final String key, final String defaultValue) {
        this(key);
        this.defaultValue = defaultValue;
    }

    SpringAppConfig(final String key, final String defaultValue, final boolean allowNull) {
        this(key);
        this.defaultValue = defaultValue;
        this.allowNull = allowNull;
    }

    public String getKey() {
        return key;
    }
    public String getDefaultValue() {
        return defaultValue;
    }

    public void set(String value) {
        System.setProperty(getKey(), value);
    }

    public String getValue() {
        String v = System.getProperty(getKey());
        if(Strings.isNullOrEmpty(v)) {
            v =  getDefaultValue();
            if(!Strings.isNullOrEmpty(v)) {
                set(v);
            }
        }
        Preconditions.checkState(allowNull || !Strings.isNullOrEmpty(v), "The system property " + getKey() + " is not set");
        return v;
    }

    public static boolean isDevMode() {
        return SpringAppConfig.APP_ENV.getValue().equals("dev");
    }

    public static boolean isQaMode() {
        return SpringAppConfig.APP_ENV.getValue().equals("qa");
    }

    private static String getStaticManifest(String key) {
        Enumeration resEnum;
        try {
            resEnum = Thread.currentThread().getContextClassLoader().getResources(JarFile.MANIFEST_NAME);
            while (resEnum.hasMoreElements()) {
                try {
                    URL url = (URL)resEnum.nextElement();
                    InputStream is = url.openStream();
                    if (is != null) {
                        Manifest manifest = new Manifest(is);
                        Attributes mainAttributes = manifest.getMainAttributes();
                        String version = mainAttributes.getValue(key);
                        if(version != null) {
                            return version;
                        }
                    }
                } catch (Exception e) {
                    // Silently ignore wrong manifests on classpath?
                }
            }
        } catch (IOException e1) {
            // Silently ignore wrong manifests on classpath?
        }
        return null;
    }
}

