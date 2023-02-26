
import java.util.ArrayList;

public class Building extends Thread {
    public ArrayList getElevators() {
        return elevators;
    }

    private ArrayList elevators;

    public String getBuildingName() {
        return buildingName;
    }

    private String buildingName;
    private int type;
    private RequestQueue allPersonReqs = new RequestQueue();
    private int floor;
    private Schedule schedule;

    public Building(String buildingName, int type, int floor, Schedule schedule) {
        this.buildingName = buildingName;
        this.type = type;
        this.floor = floor;
        this.schedule = schedule;
        if (type == 1) {
            this.elevators = new ArrayList<ElevatorVer>();
        } else {
            this.elevators = new ArrayList<ElevatorHon>();
        }
    }

    public void addElevator(Elevator elevator) {
        if (elevator instanceof ElevatorVer) {
            elevators.add((ElevatorVer) elevator);
        } else if (elevator instanceof ElevatorHon) {
            elevators.add((ElevatorHon) elevator);
        }
    }

    public RequestQueue getAllPersonReqs() {
        return allPersonReqs;
    }

    @Override
    public void run() {
        int index = 0;
        while (true) {
            // 有关结束线程的判断
            if (allPersonReqs.isEmpty() && allPersonReqs.isEnd()) {
                for (int i = 0; i < elevators.size(); i++) {
                    ((Elevator) (elevators.get(i))).getWaitList().setEnd(true);
                }
                return;
            }
            Person request = allPersonReqs.getOneRequest();
            if (request == null) {
                continue;
            }
            if (type == 1) {
                int size = elevators.size();
                if (size > 0) {
                    ((Elevator) (elevators.get(index))).getWaitList().addRequest(request);
                    index = (index + 1) % size;
                }
            }
            // 横向电梯需要额外判断
            else {
                int size = elevators.size();
                if (size > 0) {
                    while (index >= 0 && index < size) {
                        if (((((ElevatorHon) (elevators.get(index))).getSwitchInfo() >> (request.getFromBuilding() - 'A')) & 1) + ((((ElevatorHon) (elevators.get(index))).getSwitchInfo() >> (request.getToBuilding() - 'A')) & 1) == 2) {
                            ((Elevator) (elevators.get(index))).getWaitList().addRequest(request);
                            break;
                        }
                        index = (index + 1) % size;
                    }
                }
            }
        }
    }

    public int getTransFloor(Person person) {
        if (type == 2) {
            int m = -1;
            int size = elevators.size();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    ElevatorHon ehon = ((ElevatorHon) (elevators.get(i)));
                    if (((ehon.getSwitchInfo() >> (person.getFromBuilding() - 'A')) & 1)
                            + ((ehon.getSwitchInfo() >> (person.getToBuilding() - 'A')) & 1) == 2) {
                        m = floor;
                        return m;
                    }
                }
            }
        }
        return -1;
    }
}
