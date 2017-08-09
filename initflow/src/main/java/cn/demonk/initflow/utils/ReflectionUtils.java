package cn.demonk.initflow.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 反射工具类
 */
public class ReflectionUtils {

    public static <T> T create(Class<T> cls, Object... args) {
        return create(cls, resolveArgsTypes(args), args);
    }

    public static <T> T create(Class<T> cls, Class<?>[] types, Object... args) {
        try {
            Constructor<T> ctr = cls.getDeclaredConstructor(types);
            ctr.setAccessible(true);
            return ctr.newInstance(args);
        } catch (Throwable e) {
            return null;
        }
    }

    // Getter

    public static Method getMethod(String className, String methodName, Class<?>... types) {
        try {
            Class<?> targetClass = Class.forName(className);
            return getMethod(targetClass, methodName, types);
        } catch (Throwable e) {
            return null;
        }
    }

    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... types) {
        try {
            Method method = clazz.getDeclaredMethod(methodName, types);
            method.setAccessible(true);
            return method;
        } catch (Throwable e) {
            return null;
        }
    }

    public static Field getField(Class<?> targetClass, String fieldName) {
        Field field = null;
        try {
            field = targetClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (Throwable e) {
            if (targetClass != null) {
                targetClass = targetClass.getSuperclass();
                if (targetClass != null && targetClass != Object.class) {
                    return getField(targetClass, fieldName);
                }
            }
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Object target, String... fieldNames) {
        try {
            Field field;
            Class<?> cls = target.getClass();
            for (String fieldName : fieldNames) {
                field = getField(cls, fieldName);
                if (field != null) {
                    return (T) field.get(target);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Object target, Field field) {
        try {
            if (field != null) {
                field.setAccessible(true);
                return (T) field.get(target);
            }
        } catch (Throwable e) {
        }
        return null;
    }

    public static void setFieldValue(Object target, Object... fieldNameValuePairs) {
        try {
            Field field;
            Class<?> cls = target.getClass();
            for (int i = 0; i < fieldNameValuePairs.length; i += 2) {
                field = getField(cls, (String) fieldNameValuePairs[i]);
                if (field != null) {
                    field.set(target, fieldNameValuePairs[i + 1]);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static boolean setField(Object target, Field field, Object val) {
        boolean ret = false;
        try {
            field.setAccessible(true);
            field.set(target, val);
            ret = true;
        } catch (Throwable e) {
        }
        return ret;
    }

    public static Field getField(Object target, String... fieldNames) {
        try {
            Field field;
            Class<?> cls = target.getClass();
            for (String fieldName : fieldNames) {
                field = getField(cls, fieldName);
                if (field != null) {
                    return field;
                }
            }
        } catch (Throwable e) {
        }
        return null;
    }

    // Invoke

    public static Object invokeStatic(String className, String methodName, Object... args) {
        return invokeStatic(className, methodName, resolveArgsTypes(args), args);
    }

    public static Object invokeStatic(String className, String methodName, Class<?>[] argTypes, Object... args) {
        try {
            return invokeStatic(Class.forName(className), methodName, argTypes, args);
        } catch (Throwable e) {
            return null;
        }
    }

    public static Object invokeStatic(Class<?> classType, String methodName, Object... args) {
        return invokeStatic(classType, methodName, resolveArgsTypes(args), args);
    }

    public static Object invokeStatic(Class<?> classType, String methodName, Class<?>[] argTypes, Object... args) {
        try {
            return invoke(null, classType, methodName, argTypes, args);
        } catch (Throwable e) {
            return null;
        }
    }

    public static Object invokeStatic(Method method, Object... args) {
        return invoke(null, method, args);
    }

    /**
     * @param args (Note:there must be a clear distinction between int type and
     *             float, double, etc.)
     */
    public static Object invoke(Object obj, String methodName, Object... args) {
        return invoke(obj, obj.getClass(), methodName, resolveArgsTypes(args), args);
    }

    public static Object invoke(Object obj, Method method, Object... args) {
        try {
            method.setAccessible(true);
            return method.invoke(Modifier.isStatic(method.getModifiers()) ? null : obj, args);
        } catch (Throwable e) {
            return null;
        }
    }

    public static Object invoke(Object obj, Class<?> targetClass, String methodName, Class<?>[] argTypes, Object... args) {
        try {
            Method method = getDeclaredMethodRecursive(targetClass, methodName, argTypes);
            return method.invoke(obj, args);
        } catch (Throwable e) {
            return null;
        }
    }

    public static Method getDeclaredMethodRecursive(Class<?> clazz, String methodName, Class<?>[] argTypes) {
        Method method = null;
        do {
            try {
                method = clazz.getDeclaredMethod(methodName, argTypes);
            } catch (Exception e) {
            }
            clazz = clazz.getSuperclass();
        }
        while (method == null && clazz != Object.class);

        if (method != null) {
            method.setAccessible(true);
        }
        return method;
    }

    public static Class<?>[] DUMMY_PARAM_TYPES = {};
    public static Object[] DUMMY_PARAMS = {};

    public static <T> Constructor<T> getConstructor(Class<T> clazz, Class<?>[] parameterTypes) {
        if (clazz == null) {
            return null;
        }

        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            Class<?>[] types = constructor.getParameterTypes();
            if (isAssignable(types, parameterTypes)) {
                return (Constructor<T>) constructor;
            }
        }
        return null;
    }

    public static <T> T construct(Class<T> clazz, Class<?>[] paramTypes, Object[] params) {
        Constructor<T> constructor = getConstructor(clazz, paramTypes);
        if (constructor == null) {
            return null;
        }

        constructor.setAccessible(true);
        try {
            return constructor.newInstance(params);
        } catch (Throwable e) {
        }
        return null;
    }

    public static <T> T construct(Class<T> clazz) {
        return construct(clazz, DUMMY_PARAM_TYPES, DUMMY_PARAMS);
    }

    private static boolean isAssignable(Class<?>[] target, Class<?>[] source) {
        if (target == null && source == null) {
            return true;
        } else if (target == null || source == null) {
            return false;
        }

        if (target.length != source.length) {
            return false;
        }

        for (int i = 0; i < target.length; ++i) {
            if (!TypeUtil.isAssignable(target[i], source[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return resolve primitive type for all primitive wrapper types.
     */
    public static Class<?> rawType(Class<?> type) {
        if (type.equals(Boolean.class)) {
            return boolean.class;
        } else if (type.equals(Integer.class)) {
            return int.class;
        } else if (type.equals(Float.class)) {
            return float.class;
        } else if (type.equals(Double.class)) {
            return double.class;
        } else if (type.equals(Short.class)) {
            return short.class;
        } else if (type.equals(Long.class)) {
            return long.class;
        } else if (type.equals(Byte.class)) {
            return byte.class;
        } else if (type.equals(Character.class)) {
            return char.class;
        }

        return type;
    }

    private static Class<?>[] resolveArgsTypes(Object... args) {
        Class<?>[] types = null;
        if (args != null && args.length > 0) {
            types = new Class<?>[args.length];
            for (int i = 0; i < args.length; ++i) {
                types[i] = rawType(args[i].getClass());
            }
        }
        return types;
    }

    ////////
    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Object obj, String fieldName, boolean resolveParent) throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException {
        Object[] rs = getField(obj, fieldName, resolveParent);
        if (rs == null) {
            throw new NoSuchFieldException("field:" + fieldName);
        }
        Field field = (Field) rs[0];
        Object targetObj = rs[1];
        return (T) field.get(targetObj);
    }


    private static Object[] getField(Object obj, String elFieldName, boolean resolveParent) throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException {
        if (obj == null) {
            return null;
        }
        String[] fieldNames = elFieldName.split("[.]");
        Object targetObj = obj;
        Class<?> targetClass = targetObj.getClass();
        Object val = null;
        int i = 0;
        Field field = null;
        Object[] rs = new Object[2];
        for (String fName : fieldNames) {
            i++;
            field = getField_(targetClass, fName, resolveParent);
            field.setAccessible(true);
            rs[0] = field;
            rs[1] = targetObj;
            val = field.get(targetObj);
            if (val == null) {
                if (i < fieldNames.length) {
                    throw new IllegalAccessException("can not getFieldValue as field '" + fName + "' value is null in '" + targetClass.getName() + "'");
                }
                break;
            }
            targetObj = val;
            targetClass = targetObj.getClass();
        }
        return rs;
    }


    public static Field getField_(Class<?> targetClass, String fieldName, boolean resolveParent) throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException {
        Field rsField = null;
        NoSuchFieldException noSuchFieldExceptionOccor = null;
        try {
            Field field = targetClass.getDeclaredField(fieldName);
            rsField = field;
            if (!resolveParent) {
                field.setAccessible(true);
                return field;
            }
        } catch (NoSuchFieldException e) {
            noSuchFieldExceptionOccor = e;
        }
        if (noSuchFieldExceptionOccor != null) {
            if (resolveParent) {
                while (true) {
                    targetClass = targetClass.getSuperclass();
                    if (targetClass == null) {
                        break;
                    }
                    try {
                        Field field = targetClass.getDeclaredField(fieldName);
                        field.setAccessible(true);
                        rsField = field;
                        break;
                    } catch (NoSuchFieldException e) {
                        if (targetClass.getSuperclass() == null) {
                            throw e;
                        }
                    }
                }
            } else {
                throw noSuchFieldExceptionOccor;
            }
        }
        return rsField;
    }

    public static void setFieldValue(Object obj, String fieldName, Object val, boolean resolveParent) throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException {
        Object[] rs = getField(obj, fieldName, resolveParent);
        if (rs == null) {
            throw new NoSuchFieldException("field:" + fieldName);
        }
        Field field = (Field) rs[0];
        Object targetObj = rs[1];
        field.set(targetObj, val);
    }

    public static boolean isStatic(int modifiers) {
        return Modifier.isStatic(modifiers);
    }

    public static boolean staticOrFinal(int modifiers) {
        if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
            // skip all static or final.
            return true;
        }

        return false;
    }

    /**
     * Search Declared fields of Class from hierarchy.
     *
     * @param from The search beginning class which always be sub class.
     * @param end  The search ending which always be super class.
     * @return Always return not-null.
     */
    @SuppressWarnings("unchecked")
    public static List<Field> searchDeclaredFields(Class<?> from, Class<?> end) {

        List<Field> list = null;
        for (Field[] fields = null; from != end; from = from.getSuperclass()) {
            fields = from.getDeclaredFields();
            if (fields != null && fields.length > 0) {
                if (list == null) {
                    list = new ArrayList<Field>(fields.length);
                }
                list.addAll(Arrays.asList(fields));
            }
        }

        return (List<Field>) (list == null ? Collections.emptyList() : list);
    }

    /**
     * Search Declared methods of Class from hierarchy.
     *
     * @param from The search beginning class which always be sub class.
     * @param end  The search ending which always be super class.
     * @return Always return not-null.
     */
    @SuppressWarnings("unchecked")
    public static List<Method> searchDeclaredMethods(Class<?> from, Class<?> end) {

        List<Method> list = null;
        for (Method[] methods = null; from != end; from = from.getSuperclass()) {
            methods = from.getDeclaredMethods();
            if (methods != null && methods.length > 0) {
                if (list == null) {
                    list = new ArrayList<Method>(methods.length);
                }
                list.addAll(Arrays.asList(methods));
            }
        }

        return (List<Method>) (list == null ? Collections.emptyList() : list);
    }
}


class TypeUtil {

    private static final Set<Class<?>> PRIMITIVE_TYPES = new HashSet<Class<?>>();

    static {
        PRIMITIVE_TYPES.add(int.class);
        PRIMITIVE_TYPES.add(Integer.class);

        PRIMITIVE_TYPES.add(short.class);
        PRIMITIVE_TYPES.add(Short.class);

        PRIMITIVE_TYPES.add(long.class);
        PRIMITIVE_TYPES.add(Long.class);

        PRIMITIVE_TYPES.add(float.class);
        PRIMITIVE_TYPES.add(Float.class);

        PRIMITIVE_TYPES.add(double.class);
        PRIMITIVE_TYPES.add(Double.class);

        PRIMITIVE_TYPES.add(byte.class);
        PRIMITIVE_TYPES.add(Byte.class);

        PRIMITIVE_TYPES.add(boolean.class);
        PRIMITIVE_TYPES.add(Boolean.class);

        PRIMITIVE_TYPES.add(byte.class);
        PRIMITIVE_TYPES.add(Byte.class);

        PRIMITIVE_TYPES.add(char.class);
        PRIMITIVE_TYPES.add(Character.class);
    }

    public static boolean isPrimitive(Class<?> target) {
        return PRIMITIVE_TYPES.contains(target);
    }

    public static boolean isPrimitiveOrString(Class<?> target) {
        if (String.class.isAssignableFrom(target)) {
            return true;
        }

        return isPrimitive(target);
    }

    public static boolean isArrayOrContainer(Class<?> target) {
        if (target.isArray()) {
            return true;
        }

        if (List.class.isAssignableFrom(target) || Map.class.isAssignableFrom(target)) {
            return true;
        }

        return false;
    }

    public static boolean isAssignable(Class<?> target, Class<?> source) {
        if (target == Object.class || target.isAssignableFrom(source)) {
            return true;
        }

        return (TypeUtil.isPrimitive(target) && TypeUtil.isPrimitive(source));
    }

}