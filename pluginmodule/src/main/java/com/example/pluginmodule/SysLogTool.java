package com.example.pluginmodule;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class SysLogTool {

    public static ReferenceQueue mReferenceQueue = new ReferenceQueue();
    public static List<WeakReference> mWeakReferences = new ArrayList<>();

    public static void regist(Object obj) {
        mWeakReferences.add(new WeakReference(obj, mReferenceQueue));
        System.out.println("注册弱引用----------------------------------------------");
    }

    public static void print() {
        System.out.println("手动GC-------------------------------------------------");

        Runtime.getRuntime().gc();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Reference ref = mReferenceQueue.poll();
        if (ref != null) {
            System.out.println("对象成功回收++++++++++++++++++++++++++++++++++++++++");
        }else {
            System.out.println("对象未被回收----------------------------------------");
        }

    }
}
