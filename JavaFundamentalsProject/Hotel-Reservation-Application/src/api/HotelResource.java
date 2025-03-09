package api;

import model.Customer;
import model.IRoom;
import model.Reservation;
import service.CustomerService;
import service.ReservationService;

import java.util.Collection;
import java.util.Date;

public class HotelResource {
    private static final HotelResource instance = new HotelResource();

    public static HotelResource getInstance(){
        return instance;
    }

    final private static ReservationService reservationService = ReservationService.getInstance();

    final private static CustomerService customerService = CustomerService.getInstance();

    public Customer getCustomer(String email){
            return customerService.getCustomer(email);
    }

    public void createACustomer(String firstName,String lastName,String email){
        CustomerService.getInstance().addCustomer(firstName,lastName,email);
    }

    public IRoom getRoom(String roomNumber){
        return reservationService.getARoom(roomNumber);
    }

    public Reservation bookARoom(Customer customer, IRoom room, Date checkInDate, Date checkOutDate){
        return reservationService.reserveARoom(customer,room,checkInDate,checkOutDate);
    }

    public Reservation getCustomersReservations(Customer customer){
        return reservationService.getCustomersReservation(customer);
    }

    public Collection<IRoom> findARoom(Date checkInDate, Date checkOutDate){
        return reservationService.findRooms(checkInDate,checkOutDate);
    }
}