package model;

public class Room implements IRoom{
    private final String roomNumber;
    private final Double price;
    private final RoomTypes enumeration;
    private boolean roomFree;

    public Room(String roomNumber,Double price,RoomTypes enumeration){
        this.roomNumber = roomNumber;
        this.price = price;
        this.enumeration = enumeration;
        this.roomFree = true;
    }
    @Override
    public String getRoomNumber() {
        return this.roomNumber;
    }

    @Override
    public Double getRoomPrice() {
        return this.price;
    }

    @Override
    public RoomTypes getRoomType() {
        return this.enumeration;
    }

    @Override
    public boolean isFree() {
        return this.roomFree;
    }

    @Override
    public void setRoomFree(boolean roomFree) {
        this.roomFree=roomFree;
    }

    @Override
    public String toString() {
        return "Room{" +
                "roomNumber='" + roomNumber + '\'' +
                ", price=" + price +
                ", enumeration=" + enumeration +
                '}';
    }
}
