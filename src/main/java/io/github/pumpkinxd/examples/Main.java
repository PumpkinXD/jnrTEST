package io.github.pumpkinxd.examples;

import com.kenai.jffi.internal.StubLoader;
import jnr.ffi.LibraryLoader;


public class Main {
    public interface testLib{
        void sayHiJavaFromC();
    }

//    private static final testLib testLib = LibraryLoader.create(testLib.class).load("test");



    public static void main(String[] args) throws InterruptedException {
        try {
            System.out.println("hello from java\n");

            final testLib testLib = LibraryLoader.create(testLib.class).load("test");
            testLib.sayHiJavaFromC();
        }catch(Exception e) {
        String emsg = e.getMessage().toString();
            System.out.println(emsg+"\n");
            Thread.sleep(2000);
        }finally {
            System.out.println("\nbye\n");
//            System.exit(0);
        }

    }
}