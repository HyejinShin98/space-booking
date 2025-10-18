package com.hyejin.space_booking.util;

public class StringUtils {
    public static String coalesce(String a, String b) {
        return (a != null && !a.isBlank()) ? a : b;
    }

    public static String safeNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }

    public static String requireNonBlank(String s, String name) {
        if (s == null || s.isBlank()) {
            throw new IllegalArgumentException(name + " is blank");
        }
        return s;
    }

    public static String fallbackNickname(String nick, String uid) {
        if (nick != null && !nick.isBlank()) return nick;
        if (uid == null) return "kakao_user";
        return "kakao_" + uid.substring(Math.max(0, uid.length() - 6));
    }

    public static boolean isNotBlank(String s) {
        return s != null && !s.isBlank();
    }
}
