package model;

public interface IRoom {
    public String getRoomNumber();
    public Double getRoomPrice();
    public RoomTypes getRoomType();
    public boolean isFree();
    public void setRoomFree(boolean roomFree);
}
