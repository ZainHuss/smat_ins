package com.smat.ins.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Base64;

/**
 * Utility to produce a safe, shallow representation of entity objects for draft serialization.
 * It converts objects into Maps/Lists/primitives, replacing complex relations with ids or simple strings.
 */
public class DraftUtils {

    private static final Set<Class<?>> SIMPLE_TYPES = new HashSet<>(Arrays.asList(
            String.class, Boolean.class, Byte.class, Short.class, Integer.class, Long.class,
            Float.class, Double.class, Character.class, java.util.Date.class, java.time.LocalDate.class,
            java.time.LocalDateTime.class, java.math.BigDecimal.class
    ));

    public static Object toSafeStructure(Object src) {
        return toSafeStructure(src, 0, 4);
    }

    public static Object toSafeStructure(Object src, int maxDepth) {
        return toSafeStructure(src, 0, maxDepth);
    }

    private static Object toSafeStructure(Object src, int depth, int maxDepth) {
        if (src == null) return null;
        if (depth > maxDepth) return src.toString();

        Class<?> clazz = src.getClass();

        // primitives, wrappers, String, Date, Number, Enum
        if (clazz.isPrimitive() || SIMPLE_TYPES.contains(clazz) || clazz.isEnum() || Number.class.isAssignableFrom(clazz)) {
            return src;
        }

        // byte[] -> base64
        if (src instanceof byte[]) {
            return Base64.getEncoder().encodeToString((byte[]) src);
        }

        // Arrays
        if (clazz.isArray()) {
            int len = Array.getLength(src);
            List<Object> list = new ArrayList<>(len);
            for (int i = 0; i < len; i++) {
                Object el = Array.get(src, i);
                list.add(toSafeStructure(el, depth + 1, maxDepth));
            }
            return list;
        }

        // Collections
        if (src instanceof Collection) {
            Collection<?> c = (Collection<?>) src;
            List<Object> list = new ArrayList<>(c.size());
            for (Object el : c) list.add(toSafeStructure(el, depth + 1, maxDepth));
            return list;
        }

        // Maps
        if (src instanceof Map) {
            Map<?, ?> m = (Map<?, ?>) src;
            Map<Object, Object> out = new LinkedHashMap<>();
            for (Map.Entry<?, ?> e : m.entrySet()) {
                out.put(e.getKey(), toSafeStructure(e.getValue(), depth + 1, maxDepth));
            }
            return out;
        }

        // For other objects (likely entities), build a shallow map of simple properties and ids of relations
        Map<String, Object> map = new LinkedHashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field f : fields) {
            try {
                f.setAccessible(true);
                Object val = f.get(src);
                if (val == null) continue;

                Class<?> ft = f.getType();

                // simple directly serializable
                if (ft.isPrimitive() || SIMPLE_TYPES.contains(ft) || ft.isEnum() || Number.class.isAssignableFrom(ft)) {
                    map.put(f.getName(), val);
                    continue;
                }

                if (val instanceof byte[]) {
                    map.put(f.getName(), Base64.getEncoder().encodeToString((byte[]) val));
                    continue;
                }

                // Collections or nested objects - try to extract id if present to avoid deep graphs
                Object id = extractId(val);
                if (id != null) {
                    map.put(f.getName() + "_id", id);
                    continue;
                }

                if (val instanceof Collection) {
                    Collection<?> coll = (Collection<?>) val;
                    List<Object> ids = new ArrayList<>();
                    for (Object el : coll) {
                        Object eid = extractId(el);
                        if (eid != null) ids.add(eid);
                        else ids.add(toSafeStructure(el, depth + 1, maxDepth));
                    }
                    map.put(f.getName(), ids);
                    continue;
                }

                // fallback: shallow representation (toString or recursive shallow)
                map.put(f.getName(), toSafeStructure(val, depth + 1, maxDepth));

            } catch (Throwable t) {
                // ignore individual field errors
            }
        }

        return map;
    }

    private static Object extractId(Object obj) {
        if (obj == null) return null;
        try {
            Class<?> c = obj.getClass();
            // try getId, getIdValue, getIdentifier
            for (String name : Arrays.asList("getId", "getID", "get_id", "getIdentifier", "getPk")) {
                try {
                    Method m = c.getMethod(name);
                    Object id = m.invoke(obj);
                    if (id != null) return id;
                } catch (NoSuchMethodException ignored) {
                }
            }
            // try field named id
            try {
                Field f = c.getDeclaredField("id");
                f.setAccessible(true);
                Object id = f.get(obj);
                if (id != null) return id;
            } catch (NoSuchFieldException ignored) {
            }
        } catch (Throwable t) {
            // ignore
        }
        return null;
    }
}
