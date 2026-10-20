package org.example;
// multiple method call
public class examplecallstack2 {

        static void methodC()
        {
            System.out.println("i am inside method C");
        }
        static void methodB()
        {
            methodC();

        }
        static void methodA(){
            methodB();
        }

    public static void main(String[] args) {
        System.out.println("program started");
        methodA();
        System.out.println("program finished");

    }


}
