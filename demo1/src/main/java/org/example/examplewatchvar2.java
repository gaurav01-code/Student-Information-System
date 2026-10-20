package org.example;

import java.util.Scanner;

public class examplewatchvar2 {
    public static int maxOfThree(int a, int b, int c) {
        int max = a;

        if (b > max) {
            max = b;
        }

        if (c > max) {
            max = c;
        }

        return max;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter first number: ");
        int first = scanner.nextInt();

        System.out.print("Enter second number: ");
        int second = scanner.nextInt();

        System.out.print("Enter third number: ");
        int third = scanner.nextInt();

        int max = maxOfThree(first, second, third);
        System.out.println("Maximum number is: " + max);

        scanner.close();
    }
}
