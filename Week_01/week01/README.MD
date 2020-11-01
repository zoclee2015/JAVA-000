**作业1：
（选做）自己写一个简单的 Hello.java，里面需要涉及基本类型，四则运行，if 和 for，然后自己分析一下对应的字节码，有问题群里讨论**

**code：**
~~~~~~~~
package week01;


/**
 * @author zoc
 */
public class Hello {
    private static final int COUNT=100;
    static {
        System.out.println("static init : hello class initialized!!");
    }
    public static void main(String[] args) {
       int a=1;
       float b=2;
       int c=(a+b)*5;
       for(int i=0;i<COUNT;i++){
           if (c>0) {
               c--;
           }
       }
    }
}

~~~~~~~~
**byte code analysis**【IDEA试图下查看】
~~~~
// class version 49.0 (49)  //javaSE5，我本机是java8，用idea看，java版本是1.5，有点怪怪啊？
// access flags 0x21
public class week01/Hello {

  // compiled from: Hello.java

  // access flags 0x1A
  private final static I count = 100

  // access flags 0x1
  public <init>()V  //默认构造函数初始化 没有参数 返回V
   L0
    LINENUMBER 7 L0  //开始加载类的位置代码和字节码关系 
    ALOAD 0  //把this 压栈
    INVOKESPECIAL java/lang/Object.<init> ()V //调用父类Object的初始构造
    RETURN
   L1        //执行hello的默认构造
    LOCALVARIABLE this Lweek01/Hello; L0 L1 0
    MAXSTACK = 1  //栈的深度 1
    MAXLOCALS = 1 //变量1个

  // access flags 0x9
  public static main([Ljava/lang/String;)V
   L0
    LINENUMBER 13 L0
    ICONST_1  //int 类型变量 A
    ISTORE 1  //把值1 存到局部变量表
   L1
    LINENUMBER 14 L1
    ICONST_2 //int 类型变量 B
    ISTORE 2 //把值存到局部变量表
   L2
    LINENUMBER 15 L2
    ILOAD 1  //从局部变量表的位置1读取数据并压栈
    ILOAD 2
    IADD    //在栈里做加法计算
    ICONST_5 //int 类型 常量 C
    IMUL  //做乘法
    ISTORE 3 //将结果存入局部变量表
   L3
    LINENUMBER 16 L3
    ICONST_0
    ISTORE 4  // 准备for 的变量 i,存入值0给i，并存到局部变量表
   L4
    ILOAD 4  //从局部变量表取出i 到 栈
    BIPUSH 100 //取出常量100
    IF_ICMPGE L5 // i和常量的比较，如果表达式成立，跳转L5，执行return
   L6
    LINENUMBER 17 L6
    ILOAD 3 //加载变量表位置3的C到栈
    IFLE L7 //比较 如果不成立 执行for的i自增 
   L8
    LINENUMBER 18 L8
    IINC 3 -1  //对位置为3的变零做增加 -1 
   L7
    LINENUMBER 16 L7
    IINC 4 1 //执行for的i自增 
    GOTO L4
   L5
    LINENUMBER 21 L5
    RETURN
   L9 //本地变量表及栈、变量个数
    LOCALVARIABLE i I L4 L5 4
    LOCALVARIABLE args [Ljava/lang/String; L0 L9 0
    LOCALVARIABLE a I L1 L9 1
    LOCALVARIABLE b I L2 L9 2
    LOCALVARIABLE c I L3 L9 3
    MAXSTACK = 2
    MAXLOCALS = 5

  // access flags 0x8
  static <clinit>()V //静态函数
   L0
    LINENUMBER 10 L0
    GETSTATIC java/lang/System.out : Ljava/io/PrintStream;
    LDC "static init : hello class initialized!!"
    INVOKEVIRTUAL java/io/PrintStream.println (Ljava/lang/String;)V
   L1
    LINENUMBER 11 L1
    RETURN
    MAXSTACK = 2
    MAXLOCALS = 0
}

~~~~
作业2：（必做）自定义一个 Classloader，加载一个 Hello.xlass 文件，执行 hello 方法，此文件内容是一个 Hello.class 文件所有字节（x=255-x）处理后的文件。文件群里提供

code
~~~~
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
~~~~
作业3：（必做）画一张图，展示 Xmx、Xms、Xmn、Meta、DirectMemory、Xss 这些内存参数的关系
> ~~~~
> ![jvm stuct](F:\geek-java-2020-git\code\src\main\resources\jvm stuct.jpg)
> ~~~~