package org.example;
// calling one method from another
public class examplecallstack1 {
     static void displayMessage()
     {
         System.out.println("hello from display message");
         System.out.println("java debugging");

    }

    public static void main(String[] args) {
        System.out.println("program started");
        displayMessage();
        System.out.println("program finished");
    }
}
