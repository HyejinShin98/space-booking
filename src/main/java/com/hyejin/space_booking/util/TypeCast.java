package com.hyejin.space_booking.util;

import java.util.List;
import java.util.Map;

public class TypeCast {
    private TypeCast() { /* static only */ }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> asMap(Object o) {
        return (o instanceof Map<?, ?> m) ? (Map<String, Object>) m : Map.of();
    }

    public static String asString(Object o) {
        return (o == null) ? null : String.valueOf(o);
    }

    public static Long asLong(Object o) {
        if (o == null) return null;
        if (o instanceof Number n) return n.longValue();
        try { return Long.parseLong(o.toString()); }
        catch (Exception ignored) { return null; }
    }

    public static Boolean asBoolean(Object o) {
        if (o == null) return null;
        if (o instanceof Boolean b) return b;
        return Boolean.parseBoolean(o.toString());
    }

    public static Integer asInt(Object o) {
        if (o == null) return null;
        if (o instanceof Number n) return n.intValue();
        try { return Integer.parseInt(o.toString()); }
        catch (Exception ignored) { return null; }
    }

    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> asMapList(Object o) {
        return (o instanceof List<?> l) ? (List<Map<String, Object>>) l : List.of();
    }

    public static <T> T cast(Object o, Class<T> type) {
        return type.isInstance(o) ? type.cast(o) : null;
    }

}
