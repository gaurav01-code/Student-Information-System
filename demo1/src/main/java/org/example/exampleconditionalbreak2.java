package org.example;
// find any specific number in array
public class exampleconditionalbreak2 {
    public static void main(String[] args) {
        int[] numbers ={10,20,30,40,50};
        for(int i =0; i<numbers.length; i++)
        {
            int current = numbers[i];
            if(current ==40)
            {
                System.out.println("Current number"+ current);
            }
            System.out.println("checking" +current);
        }
    }
}
