import api.AdminResource;

import api.HotelResource;
import model.Customer;
import model.IRoom;
import model.Room;
import model.RoomTypes;
import utils.getYesOrNo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

public class AdminMenu {
    static AdminResource adminResource = AdminResource.getInstance();

    static void addARoom(){
        Collection<IRoom> rooms = new ArrayList<>();
        while(true) {
            Scanner sc = new Scanner(System.in);
            String roomNumber = enterRoomNumber(sc);
            IRoom alreadyExists = HotelResource.getInstance().getRoom(roomNumber);
            System.out.println(alreadyExists);
            if(alreadyExists!=null){
                System.out.println("Room Already Exists with that Number");
                continue;
            }
            Double price = enterPrice(sc);
            RoomTypes roomType = enterRoomType(sc);
            rooms.add(new Room(roomNumber,price,roomType));
            boolean likeToAdd = getYesOrNo.getYesOrNo("would you like to add another room ");
            if(!likeToAdd) break;
        }
        AdminResource.addRoom(rooms);
        System.out.println("Room Added Successfully");
        AdminMenu.main(new String[]{});
    }
    static String enterRoomNumber(Scanner sc){
        System.out.println("Enter room number:");
        String roomNumber = sc.nextLine();
        try{
            Integer num = Integer.parseInt(roomNumber);
            return roomNumber;
        } catch (NumberFormatException e) {
            System.out.println("Invalid Enter only numbers");
            return enterRoomNumber(sc);
        }
    }
    static Double enterPrice(Scanner sc){
        System.out.println("Enter price per night:");
        String price = sc.nextLine();
         try{
             return Double.parseDouble(price);
         }catch(NumberFormatException e){
             System.out.println("Invalid Input");
             return enterPrice(sc);
         }
    }

    static RoomTypes enterRoomType(Scanner sc){
        System.out.println("Enter room type: 1 for single bed, 2 for double bed");
        String in = sc.nextLine();
        try{
            String type = "";
            if(Integer.parseInt(in)==1) type = "SINGLE";
            if(Integer.parseInt(in)==2) type = "DOUBLE";
            return RoomTypes.valueOf(type.toUpperCase());
        }catch(IllegalArgumentException e){
            System.out.println("Invalid Input");
            return enterRoomType(sc);
        }
    }

    static void seeAllCustomers(){
        Collection<Customer> customers = adminResource.getAllCustomers();
        if(customers.isEmpty()) System.out.println("No Customers");
        else{
            for(Customer customer:customers){
                System.out.println(customer.toString());
            }
        }
        AdminMenu.main(new String[]{});
    }

    static void seeAllRooms(){
        Collection<IRoom> rooms = adminResource.getAllRooms();
        if(rooms.isEmpty()) System.out.println("No Rooms Added Yet!");
        else{
            for(IRoom room:rooms){
                System.out.println(room.toString());
            }
        }
        AdminMenu.main(new String[]{});
    }

    static void seeAllReservations(){
        adminResource.displayAllReservations();
        AdminMenu.main(new String[]{});
    }

    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        System.out.println("1. See all Customers");
        System.out.println("2. See all Rooms");
        System.out.println("3. See all Reservations");
        System.out.println("4. Add a Room");
        System.out.println("5. Back to Main Menu");
        System.out.println("Please Select a number for the menu option");
        String option = sc.nextLine();
        switch(option){
            case "1":
                seeAllCustomers();
                break;
            case "2":
                seeAllRooms();
                break;
            case "3":
                seeAllReservations();
                break;
            case "4":
                addARoom();
                break;
            case "5":
                MainMenu.main(new String[]{});
                break;
            default:
                System.out.println("Invalid Input");
                AdminMenu.main(new String[]{});
        }
    }
}
