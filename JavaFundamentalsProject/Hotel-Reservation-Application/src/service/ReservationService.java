package service;

import model.Customer;
import model.IRoom;
import model.Reservation;
import model.Room;

import java.util.*;

public class ReservationService {
    Map<String,IRoom> rooms = new HashMap<>();

    Map<Customer,Reservation> reservations = new HashMap<>();

    private static final ReservationService instance = new ReservationService();

    public static ReservationService getInstance(){
        return instance;
    }

    public void addRoom(IRoom room){
        Room newRoom = new Room(room.getRoomNumber(),room.getRoomPrice(),room.getRoomType());
        rooms.put(room.getRoomNumber(),newRoom);
    }

    public IRoom getARoom(String roomId){
            return rooms.get(roomId);
    }

    public Reservation reserveARoom(Customer customer, IRoom room, Date checkInDate, Date checkOutDate){
        reservations.put(customer,new Reservation(customer,room,checkInDate,checkOutDate));
        room.setRoomFree(false);
        return reservations.get(customer);
    }

    public Collection<IRoom> findRooms(Date checkInDate,Date checkOutDate){
        if(reservations.isEmpty()) return rooms.values();
        Collection<IRoom> availableRooms = new ArrayList<>();
        for(IRoom room:rooms.values()){
            if(room.isFree()) availableRooms.add(room);
        }
        for(Reservation res:reservations.values()){
            if(!checkInDate.after(res.checkInDate()) && !checkOutDate.before(checkInDate)){
                availableRooms.add(res.room());
            }
        }
        return availableRooms;
    }

    public Reservation getCustomersReservation(Customer customer){
        return reservations.get(customer);
    }

    public Collection<IRoom> getAllRooms(){
        return rooms.values();
    }

    public void printAllReservation(){
        if(reservations.isEmpty()) System.out.println("No Reservations done");
        for(Reservation r:reservations.values()){
            System.out.println(r.toString());
        }
    }
}
