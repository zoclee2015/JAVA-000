package week01;


import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 *
 * @author zoc
 */
public class HelloClassLoader {

    private static final String HELLO_CLASS_FILE = "F:\\geek-java-2020-git\\code\\target\\classes\\Hello.xlass";
    private static final String HELLO_CLASS_NAME = "Hello";
    private static final String HELLO_CLASS_METHOD = "hello";
    private static final MyClassLoader CLASS_LOADER =new MyClassLoader();

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {

     Class<?> aClass = CLASS_LOADER .findClass(HELLO_CLASS_NAME);
     Method helloMethod=aClass.getMethod(HELLO_CLASS_METHOD);
     helloMethod.invoke(aClass.newInstance());
    }


    private static class MyClassLoader extends ClassLoader {


        private  byte[] loadClassContent() throws IOException {
            File ff=new File("./");
            System.out.println(ff.getAbsoluteFile());
            File f = new File(HelloClassLoader.HELLO_CLASS_FILE);
            if (!f.exists()) {
                throw new FileNotFoundException(HelloClassLoader.HELLO_CLASS_FILE);
            }

            FileChannel channel = null;
            FileInputStream fs = null;
            try {
                fs = new FileInputStream(f);
                channel = fs.getChannel();
                ByteBuffer byteBuffer = ByteBuffer.allocate((int) channel.size());
                while ((channel.read(byteBuffer)) > 0) {
                     System.out.println("reading...");
                }
                return byteBuffer.array();
            } catch (IOException e) {
                e.printStackTrace();
                throw e;
            } finally {
                try {
                    assert channel != null;
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            try {
                byte[] classBytes = loadClassContent();
                // 将读取到的字节（x=255-x）
                for (int i = 0; i < classBytes.length; i++) {
                    classBytes[i] = (byte) (255 - classBytes[i]);
                }
                // 加载自定义Hello类
                return defineClass(name, classBytes, 0, classBytes.length);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return super.findClass(name);
        }
    }
}