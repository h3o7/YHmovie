package com.yhmovie.common.util;

public class CurrentHolder {
    private static final ThreadLocal<String> currentId = new ThreadLocal<>();

    public static void setCurrentId(String id) {
        currentId.set(id);
    }

    public static String getCurrentId() {
        return currentId.get();
    }

    public static void clear() {
        currentId.remove();
    }
}
