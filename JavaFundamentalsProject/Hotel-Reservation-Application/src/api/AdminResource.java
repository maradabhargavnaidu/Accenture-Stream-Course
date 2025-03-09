package api;

import model.Customer;
import model.IRoom;
import model.Room;
import service.CustomerService;
import service.ReservationService;

import java.util.Collection;
import java.util.List;

public class AdminResource {
    private static final AdminResource instance = new AdminResource();

    public static AdminResource getInstance(){
        return instance;
    }
    final private static ReservationService reservation = ReservationService.getInstance();

    final private static CustomerService customerService = CustomerService.getInstance();

    public static void addRoom(Collection<IRoom> rooms){
        for(IRoom r:rooms){
            reservation.addRoom(r);
        }
    }

    public Collection<IRoom> getAllRooms(){
        return reservation.getAllRooms();
    }

    public Collection<Customer> getAllCustomers(){
        return customerService.getAllCustomers();
    }

    public void displayAllReservations(){
        reservation.printAllReservation();
    }
}
