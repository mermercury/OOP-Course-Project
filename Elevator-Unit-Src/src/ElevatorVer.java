
import java.util.Iterator;

public class ElevatorVer extends Thread implements Elevator {
    private RequestQueue waitList;
    private RequestQueue eleList;
    private Schedule schedule;

    //buildingNum就是ABCDE
    public ElevatorVer(char buildingNum, RequestQueue waitList, int id
            , int personNumMax, int speedMove, Schedule schedule) {
        this.buildingNum = buildingNum;
        this.waitList = waitList;
        this.eleList = new RequestQueue();
        floorMin = 1;
        floorMax = 10;
        this.speedMove = speedMove;
        speedOpen = 200;
        speedClose = 200;
        this.personNumMax = personNumMax;
        currentFloor = 1;
        targetFloor = 1;
        direction = 0;
        isOpen = false;
        this.id = id;
        this.schedule = schedule;
    }

    // 电梯固有属性
    private char buildingNum;
    private int floorMin;
    private int floorMax;
    private int speedMove;
    private int speedOpen;
    private int speedClose;
    private int personNumMax;
    private int id;

    // elevator current state
    private int currentFloor;
    private int targetFloor;
    private int direction;
    private  boolean isOpen;

    @Override
    public void run() {
        while (true) {
            if (waitList.isEnd() && waitList.isEmpty() && eleList.isEmpty()) {
                break;
            } else {
                try {
                    checkRequests();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                closeDoor();
                if (waitList.isEnd() && waitList.isEmpty() && eleList.isEmpty()) {
                    break;
                }
                if (direction == 0) {
                    getMainRequest();
                }
                if (currentFloor < floorMin | currentFloor > floorMax) {
                    break;
                }
                move();
                arrive();
            }
        }
    }

    public void getMainRequest() {
        if (eleList.isEmpty()) {
            try {
                getMainRequest1();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            getMainRequest2();
        }
    }

    public synchronized void getMainRequest1() throws InterruptedException {
        Person mainRequest = waitList.readOneRequest();
        if (mainRequest != null) {
            setTargetFloor(mainRequest.getFromFloor());
            if (targetFloor > currentFloor) {
                direction = 1;
            } else if (targetFloor < currentFloor) {
                direction = -1;
            }
        }
    }

    public void getMainRequest2() {
        setTargetFloor(eleList.readOneRequest().getToFloor());
        if (targetFloor > currentFloor) {
            direction = 1;
        } else {
            direction = -1;
        }
    }

    public int getTargetFloor() {
        return targetFloor;
    }

    public void setTargetFloor(int targetFloor) {
        this.targetFloor = targetFloor;
    }

    public void checkRequests() throws InterruptedException {
        checkInside();
        checkOutside();
    }

    public void checkInside() {
        if (!eleList.isEmpty()) {
            Iterator iterator = eleList.getRequests().iterator();
            while (iterator.hasNext()) {
                Person person = (Person) iterator.next();
                if (person.getToFloor() == currentFloor) {
                    getPassengerOut(person);
                    iterator.remove();
                    schedule.scheduleWake();
                }
            }
        }
    }

    public void getPassengerOut(Person curPassenger) {
        openDoor();
        OutputThread.println("OUT-" + curPassenger.getPersonId() + "-"
                + buildingNum + "-" + currentFloor + "-" + id);
        if (buildingNum != curPassenger.getToBuilding()) {
            curPassenger.setTransFloor(-1);
            curPassenger.setFromFloor(currentFloor);
            String buildingName = "FLOOR" + currentFloor;
            Main.getBuildings().get(buildingName).getAllPersonReqs().addRequest(curPassenger);
        }

    }

    public void openDoor() {
        if (!isOpen) {
            OutputThread.println("OPEN-" + buildingNum + "-" + currentFloor + "-" + id);
            try {
                Thread.sleep(speedOpen);
                Thread.sleep(speedClose);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            isOpen = true;
        }
    }

    public void closeDoor() {
        if (isOpen) {
            OutputThread.println("CLOSE-" + buildingNum + "-" + currentFloor + "-" + id);
            isOpen = false;
        }
    }

    public void checkOutside() throws InterruptedException {
        synchronized (waitList) {
            Person outsidePassenger;
            if (!waitList.isEmpty()) {
                Iterator iterator = waitList.getRequests().iterator();
                while (iterator.hasNext()) {
                    outsidePassenger = (Person) iterator.next();
                    if (eleList.getRequests().size() >= personNumMax) {
                        break;
                    } else if (outsidePassenger != null
                            && outsidePassenger.getFromFloor() == currentFloor
                            && (((outsidePassenger.getToFloor() - currentFloor) * (targetFloor - currentFloor)) >= 0)) {
                        getPassengerIn(outsidePassenger);
                        iterator.remove();
                    }
                }
            }
        }
    }

    public void getPassengerIn(Person outsidePassenger) {
        openDoor();
        OutputThread.println("IN-" + outsidePassenger.getPersonId() + "-" +
                buildingNum + "-" + currentFloor + "-" + id);
        eleList.addRequest(outsidePassenger);
    }

    public void move() {
        if (direction < 0) {
            try {
                Thread.sleep(speedMove);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            currentFloor -= 1;
            if (currentFloor < floorMin) {
                return;
            }
            OutputThread.println("ARRIVE-" + buildingNum + "-" + currentFloor + "-" + id);
        } else if (direction > 0) {
            try {
                Thread.sleep(speedMove);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            currentFloor += 1;
            if (currentFloor > floorMax) {
                return;
            }
            OutputThread.println("ARRIVE-" + buildingNum + "-" + currentFloor + "-" + id);
        }

    }

    public void arrive() {
        //当前目标楼层已到达
        if (currentFloor == targetFloor) {
            targetFloor = currentFloor;
            direction = 0;
        }
    }

    @Override
    public RequestQueue getWaitList() {
        return waitList;
    }

    @Override
    public RequestQueue getEleList() {
        return eleList;
    }
}


