
import java.util.Iterator;

public class ElevatorHon extends Thread implements Elevator {
    private RequestQueue waitList;
    private RequestQueue eleList;
    private char[] letters = new char[5];

    public int getEleId() {
        return id;
    }

    public ElevatorHon(String buildingNum, RequestQueue waitList
            , int id, int floor, int personNumMax, int speedMove
            , int switchInfo, Schedule schedule) {
        this.buildingNum = buildingNum;
        this.waitList = waitList;
        this.eleList = new RequestQueue();
        this.speedMove = speedMove;
        speedOpen = 200;
        speedClose = 200;
        this.personNumMax = personNumMax;
        targetBuilding = 'A';
        direction = 0;
        isOpen = false;
        this.id = id;
        this.floor = floor;
        index = 0;
        this.switchInfo = switchInfo;
        this.schedule = schedule;

        for (int i = 0; i < 5; i++) {
            letters[i] = (char) ('A' + i);
        }

        //((M >> (P -'A')) & 1)
        for (int i = 0; i < 5; i++) {
            if (((switchInfo >> (letters[i] - 'A')) & 1) == 1) {
                path[i] = true;
            }
        }
    }

    // 电梯固有属性
    private String buildingNum;
    private int speedMove;
    private int speedOpen;
    private int speedClose;
    private int personNumMax;
    private int id;
    private int floor;
    private int switchInfo;

    // elevator current state
    private char targetBuilding;
    private int direction;
    // direction: 运行方向 1-ABCDEA 2-AEDCBA
    private boolean isOpen;
    private int index;
    private boolean[] path = new boolean[5];
    private Schedule schedule;

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
            setTargetBuilding(mainRequest.getFromBuilding());
            direction = getResDirection(targetBuilding);
        }
    }

    public void getMainRequest2() {
        setTargetBuilding(eleList.readOneRequest().getToBuilding());
        direction = getResDirection(targetBuilding);
    }

    public int getTargetBuilding() {
        return targetBuilding;
    }

    public void setTargetBuilding(char target) {
        this.targetBuilding = target;
    }

    public void checkRequests() throws InterruptedException {
        checkInside();
        checkOutside();
    }

    public void checkInside() {
        if (!eleList.isEmpty() && path[index]) {
            Iterator iterator = eleList.getRequests().iterator();
            while (iterator.hasNext()) {
                Person person = (Person) iterator.next();
                if (person.getToBuilding() == getCurBuildingName()) {
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
                + getCurBuildingName() + "-" + floor + "-" + id);
        if (curPassenger.getToFloor() != floor) {
            curPassenger.setFromBuilding(getCurBuildingName());
            curPassenger.setTransFloor(-1);
            String s = String.valueOf(getCurBuildingName());
            Main.getBuildings().get(s).getAllPersonReqs().addRequest(curPassenger);
        }

    }

    public void openDoor() {
        if (!isOpen && path[index]) {
            OutputThread.println("OPEN-" + getCurBuildingName() + "-" + floor + "-" + id);
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
            OutputThread.println("CLOSE-" + getCurBuildingName() + "-" + floor + "-" + id);
            isOpen = false;
        }
    }

    public void checkOutside() throws InterruptedException {
        synchronized (waitList) {
            Person outsidePassenger;
            if (!waitList.isEmpty() && path[index]) {
                Iterator iterator = waitList.getRequests().iterator();
                while (iterator.hasNext()) {
                    outsidePassenger = (Person) iterator.next();
                    if (eleList.getRequests().size() >= personNumMax) {
                        break;
                    } else if (outsidePassenger != null
                            && outsidePassenger.getFromBuilding() == getCurBuildingName()
                            && (getResDirection(outsidePassenger.getToBuilding()) * direction) >= 0)
                    {
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
                getCurBuildingName() + "-" + floor + "-" + id);
        eleList.addRequest(outsidePassenger);
    }

    public void move() {
        if (direction < 0) {
            try {
                Thread.sleep(speedMove);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            index = Math.floorMod(index - 1, 5);
            OutputThread.println("ARRIVE-" + getCurBuildingName() + "-" + floor + "-" + id);
        } else if (direction > 0) {
            try {
                Thread.sleep(speedMove);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            index = (index + 1) % 5;
            OutputThread.println("ARRIVE-" + getCurBuildingName() + "-" + floor + "-" + id);
        }
    }

    public void arrive() {
        //当前目标楼层已到达
        if (getCurBuildingName() == targetBuilding) {
            direction = 0;
        }
    }

    @Override
    public RequestQueue getWaitList() {
        return waitList;
    }

    public char getCurBuildingName() {
        return ((char) (this.index + 'A'));
    }

    public int getResDirection(char target) {
        int dir = 0;
        int num = target - 'A'; //possible: 01234 ABCDE
        if (Math.abs(num - index) <= 2) {
            if (index < num) {
                dir = 1;
            } else if (index > num) {
                dir = -1;
            }
        } else {
            if (index < num) {
                dir = -1;
            } else if (index > num) {
                dir = 1;
            }
        }
        return dir;
    }

    public int getSwitchInfo() {
        return switchInfo;
    }

    public RequestQueue getEleList() {
        return eleList;
    }

}


