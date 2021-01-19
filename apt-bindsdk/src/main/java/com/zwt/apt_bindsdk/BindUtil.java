package com.zwt.apt_bindsdk;

import android.app.Activity;

import java.lang.reflect.Method;

public class BindUtil {

    public static void bindView(Activity activity) {
        try {
            Class aClass = activity.getClass();
            Class<?> bindViewClass = Class.forName(aClass.getName() + "_ViewBinding");
            Method method = bindViewClass.getMethod("bindView", aClass);
            method.invoke(bindViewClass.newInstance(), activity);
        } catch (Throwable throwable) {
             throwable.printStackTrace();
        }
    }
}
