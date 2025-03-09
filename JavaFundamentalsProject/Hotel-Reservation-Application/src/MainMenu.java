import api.HotelResource;
import model.Customer;
import model.IRoom;
import model.Reservation;
import utils.getYesOrNo;
import validations.EmailValidator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Scanner;

import static java.lang.System.exit;

public class MainMenu {
    static HotelResource hotelResource = HotelResource.getInstance();

    static void findAndReserveARoom(){
        Scanner sc = new Scanner(System.in);
        Date checkInDate = getDate(sc,"CheckIn");
        Date checkOutDate =getDate(sc,"checkOut");
        Collection<IRoom> availableRooms = hotelResource.findARoom(checkInDate,checkOutDate);
        if(availableRooms.isEmpty()){
            System.out.println("No Rooms Available");
            MainMenu.main(new String[]{});
        }
        else {
            for (IRoom room : availableRooms) {
                System.out.println(room.toString());
            }
        }
        boolean likeToBook = getYesOrNo.getYesOrNo("Would u like to book a room ?");
        if(likeToBook) {
            boolean haveAccount = getYesOrNo.getYesOrNo("Do you have an account with us ?");
            if (haveAccount) {
                String email = EmailValidator.getValidEmail();
                Customer customer = hotelResource.getCustomer(email);
                if(customer==null){
                    System.out.println("Account Not Found");
                    System.out.println("Please Create Account and come back");
                    MainMenu.main(new String[]{});
                }else{
                    IRoom room = null;
                    while(true) {
                        System.out.println("What room number would you like to reserve");
                        String roomNum = sc.nextLine();
                        room = hotelResource.getRoom(roomNum);
                        if (!availableRooms.contains(room)) {
                            System.out.println("Please Enter the number from available Rooms only");
                        }else{
                            break;
                        }
                    }
                    Reservation res = hotelResource.bookARoom(customer,room,checkInDate,checkOutDate);
                    System.out.println(res.toString());
                    MainMenu.main(new String[]{});
                }
            }else{
                System.out.println("Please Create Account and come back");
                MainMenu.main(new String[]{});
            }
        }else{
            MainMenu.main(new String[]{});
        }
    }

    static Date getDate(Scanner sc,String dateType){
        System.out.println("Enter"+ dateType +" Date mm/dd/yyyy example 02/01/2020");
        String date = sc.nextLine();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        try{
            return dateFormat.parse(date);
        } catch (ParseException e) {
            System.out.println("Invalid Date Format");
            return getDate(sc,dateType);
        }
    }

    static void seeMyReservations(){
        Scanner sc = new Scanner(System.in);
        String customerEmail = EmailValidator.getValidEmail();
        Customer customer = hotelResource.getCustomer(customerEmail);
        if(customer==null){
            System.out.println("Account Not Found");
            System.out.println("Please Create Account and come back");
            MainMenu.main(new String[]{});
        }
        Reservation customerReservation = hotelResource.getCustomersReservations(customer);
        if(customerReservation==null){
            System.out.println("No Reservations Done");
            MainMenu.main(new String[]{});
        }
        System.out.println(customerReservation.toString());
        MainMenu.main(new String[]{});
    }

    static void createAnAccount(){
        Scanner sc = new Scanner(System.in);
        String mail = EmailValidator.getValidEmail();
        Customer customer = hotelResource.getCustomer(mail);
        if(customer!=null){
            System.out.println("User already Exists");
            MainMenu.main(new String[]{});
        }
        System.out.println("Enter First Name:");
        String firstName = sc.nextLine();
        System.out.println("Enter Last Name:");
        String lastName = sc.nextLine();
        HotelResource hotelRes = HotelResource.getInstance();
        hotelRes.createACustomer(firstName,lastName,mail);
        System.out.println("Account Created Successfully");
        MainMenu.main(new String[]{});
    }

    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to the Hotel Reservation Application");
        System.out.println("1. Find and reserve a room");
        System.out.println("2. See my reservations");
        System.out.println("3. Create an Account");
        System.out.println("4. Admin");
        System.out.println("5. Exit");
        System.out.println("Please Select a number for the menu option");
        String option = sc.nextLine();
        switch(option){
            case "1":
                findAndReserveARoom();
                break;
            case "2":
                seeMyReservations();
                break;
            case "3":
                createAnAccount();
                break;
            case "4":
                AdminMenu.main(new String[]{});
                break;
            case "5":
                System.out.println("Exit");
                exit(0);
            default:
                System.out.println("Invalid Input");
                MainMenu.main(new String[]{});
        }
    }
}
