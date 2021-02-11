package com.example.himalayaproject.Utils;

import android.content.Context;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

public class LoadUtil {
    public static void loadPluginClass(Context context){
        try {
            //-------获取dexElements抽象成员
            Class<?> dexPathListClass = Class.forName("dalvik.system.DexPathList");
            Field dexElementsField = dexPathListClass.getDeclaredField("dexElements");
            dexElementsField.setAccessible(true);
            //获取pathList抽象成员
            Class<?> classLoaderClass = Class.forName("dalvik.system.BaseDexClassLoader");
            Field pathListField = classLoaderClass.getDeclaredField("pathList");
            pathListField.setAccessible(true);

            //获取当前使用的类加载器实例，通过抽象成员获取实例成员，取得当前类加载器对象的带数据dexElements
            ClassLoader pathClassLoader = context.getClassLoader();
            Object hostPathList = pathListField.get(pathClassLoader);
            Object[] hostDexElements =(Object[]) dexElementsField.get(hostPathList);
            //---------获取插件的dexElements

            ClassLoader pluginClassLoader = new DexClassLoader("/sdcard/pluginmodule-debug.apk",
                    context.getCacheDir().getAbsolutePath(),null, pathClassLoader);
            Object pluginPathList = pathListField.get(pluginClassLoader);
            Object[] pluginDexElements =(Object[]) dexElementsField.get(pluginPathList);


            //合并
            Object[] newElements = (Object[]) Array.newInstance(hostDexElements.getClass().getComponentType()
                    , hostDexElements.length + pluginDexElements.length);
            System.arraycopy(hostDexElements, 0, newElements, 0, hostDexElements.length);
            System.arraycopy(pluginDexElements, 0, newElements, hostDexElements.length, pluginDexElements.length);

            //赋值到宿主的PathList的dexElements
            dexElementsField.set(hostPathList, newElements);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
