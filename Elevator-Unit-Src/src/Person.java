import com.oocourse.elevator3.PersonRequest;

public class Person {
    public PersonRequest getPersonRequest() {
        return personRequest;
    }

    private PersonRequest personRequest;
    private int transFloor;
    private int fromFloor;
    private char fromBuilding;

    public void setTransFloor(int transFloor) {
        this.transFloor = transFloor;
    }

    public Person(PersonRequest personRequest) {
        this.personRequest = personRequest;
        this.transFloor = -1; // 如果不换乘
        this.fromFloor = personRequest.getFromFloor();
        this.fromBuilding = personRequest.getFromBuilding();
    }

    public int getFromFloor() {
        return fromFloor;
    }

    public int getToFloor() {
        if (transFloor == -1) {
            return personRequest.getToFloor();
        } else {
            return transFloor;
        }
    }

    public void setFromFloor(int fromFloor) {
        this.fromFloor = fromFloor;
    }

    public int getPersonId() {
        return personRequest.getPersonId();
    }

    public char getFromBuilding() {
        return this.fromBuilding;
    }

    public void setFromBuilding(char fromBuilding) {
        this.fromBuilding = fromBuilding;
    }

    public char getToBuilding() {
        return personRequest.getToBuilding();
    }

}
