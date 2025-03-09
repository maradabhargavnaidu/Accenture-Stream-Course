package utils;

import java.util.Scanner;

public class getYesOrNo {
    private static final Scanner sc = new Scanner(System.in);
    public static boolean getYesOrNo(String message){
        while(true){
            System.out.println(message+"(y/n):");
            String input = sc.nextLine().trim().toLowerCase();
            if(input.equals("y")) return true;
            else if(input.equals("n")) return false;
            else System.out.println("Invalid Input Please Enter y or n");
        }
    }
}
