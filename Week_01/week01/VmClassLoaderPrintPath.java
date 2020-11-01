package week01;

import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

public class VmClassLoaderPrintPath {
    public static void main(String[] args) {
        //启动类加载器
        URL[] urls = sun.misc.Launcher.getBootstrapClassPath().getURLs();
        System.out.println("启动类加载器");
        for (URL url : urls) {
            System.out.println("===>" + url.toString());
        }
        //扩展类加载
        printClassLoader("扩展类加载 ", VmClassLoaderPrintPath.class.getClassLoader().getParent());
        //应用类加载器
        printClassLoader("应用类加载器", VmClassLoaderPrintPath.class.getClassLoader());

    }

    public static void printClassLoader(String name, ClassLoader classLoader) {
        if (classLoader != null) {
            System.out.println(name + " ClassLoader ->" + classLoader.toString());
            printUrlClassLoader(classLoader);
        } else {
            System.out.println(name + " ClassLoader -> null");
        }

    }

    public static void printUrlClassLoader(ClassLoader classLoader) {
        Object ucp = insightField(classLoader, "ucp");
        Object path = insightField(ucp, "path");
        ArrayList ps = (ArrayList) path;
        assert ps != null;
        for (Object p : ps
        ) {
            System.out.println("==>" + p.toString());

        }
    }

    public static Object insightField(Object object, String name) {
        try {
            Field f ;
            if (object instanceof URLClassLoader) {
                f = URLClassLoader.class.getDeclaredField(name);
            } else {
                f = object.getClass().getDeclaredField(name);
            }
            f.setAccessible(true);
            return f.get(object);
        } catch (Exception e) {
            e.printStackTrace();
            return null;

        }
    }
}
