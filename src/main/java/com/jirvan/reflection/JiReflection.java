package com.jirvan.reflection;

import java.lang.reflect.*;

public class JiReflection {

    public static <T> T newInstance(Class<? extends T> clazz, Object... initargs) {
        try {
            return clazz.getConstructor().newInstance(initargs);
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodRuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new InvocationTargetRuntimeException(e);
        } catch (InstantiationException e) {
            throw new InstantiationRuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalAccessRuntimeException(e);
        }
    }

}
