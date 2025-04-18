package model;

import java.util.Date;

public record Reservation(Customer customer, IRoom room, Date checkInDate, Date checkOutDate) {

    @Override
    public String toString() {
        return "Reservation{" +
                "customer=" + customer +
                ", room=" + room +
                ", checkInDate=" + checkInDate +
                ", checkOutDate=" + checkOutDate +
                '}';
    }
}
