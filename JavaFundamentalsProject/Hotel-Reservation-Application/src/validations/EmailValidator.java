package validations;

import java.util.Scanner;
import java.util.regex.Pattern;

public class EmailValidator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^(.+)@(.+).(.+)$");

    private static final Scanner sc = new Scanner(System.in);

    public static String getValidEmail(){
        while(true){
            System.out.println("Enter Email format: name@domain.com:");
            String email = sc.nextLine().trim();
            if(EMAIL_PATTERN.matcher(email).matches()){
                return email;
            }else{
                System.out.println("Invalid Email format ! please try again");
            }
        }
    }
}
