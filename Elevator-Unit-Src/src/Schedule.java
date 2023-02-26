
import java.util.ArrayList;

public class Schedule extends Thread {
    private final RequestQueue waitQueue;
    private final ArrayList<RequestQueue> waitingQueues;
    private ArrayList<Elevator> elevators = new ArrayList<>();
    private boolean state = false;

    public Schedule(RequestQueue waitQueue
            , ArrayList<RequestQueue> waitingQueues) {
        this.waitQueue = waitQueue;
        this.waitingQueues = waitingQueues;
    }

    @Override
    public void run() {
        while (true) {
            if (waitQueue.isEmpty() && waitQueue.isEnd() && state) {
                for (int i = 0; i < waitingQueues.size(); i++) {
                    waitingQueues.get(i).setEnd(true);
                }
                return;
            }
            Person request = waitQueue.getOneRequest();
            if (request == null) {
                continue;
            }
            if (request.getFromBuilding() != request.getToBuilding()) {
                int floor = request.getFromFloor();
                int m = calTransFloor(request);
                if (floor == m) {
                    waitingQueues.get(floor + 4).addRequest(request);
                } else {
                    request.setTransFloor(m);
                    if (request.getFromBuilding() == 'A') {
                        waitingQueues.get(0).addRequest(request);
                    } else if (request.getFromBuilding() == 'B') {
                        waitingQueues.get(1).addRequest(request);
                    } else if (request.getFromBuilding() == 'C') {
                        waitingQueues.get(2).addRequest(request);
                    } else if (request.getFromBuilding() == 'D') {
                        waitingQueues.get(3).addRequest(request);
                    } else if (request.getFromBuilding() == 'E') {
                        waitingQueues.get(4).addRequest(request);
                    }
                }
            } else if (request.getFromBuilding() == 'A') {
                waitingQueues.get(0).addRequest(request);
            } else if (request.getFromBuilding() == 'B') {
                waitingQueues.get(1).addRequest(request);
            } else if (request.getFromBuilding() == 'C') {
                waitingQueues.get(2).addRequest(request);
            } else if (request.getFromBuilding() == 'D') {
                waitingQueues.get(3).addRequest(request);
            } else if (request.getFromBuilding() == 'E') {
                waitingQueues.get(4).addRequest(request);
            }
        }
    }

    public synchronized int calTransFloor(Person person) {
        int m = 1000;
        for (int i = 1; i <= 10; i++) {
            String buildingName = "FLOOR" + i;
            int tmp = Main.getBuildings().get(buildingName).getTransFloor(person);
            if (tmp != -1 && change(m, tmp, person)) {
                m = tmp;
            }
        }
        return m;
    }

    private synchronized boolean change(int m, int tmp, Person person) {
        if ((Math.abs(person.getFromFloor() - tmp) + Math.abs(person.getToFloor() - tmp))
                < (Math.abs(person.getFromFloor() - m) + Math.abs(person.getToFloor() - m))) {
            return true;
        } else {
            return false;
        }
    }

    public synchronized ArrayList<Elevator> getElevators() {
        return elevators;
    }

    public synchronized RequestQueue getWaitQueue() {
        return waitQueue;
    }

    public synchronized void scheduleWake() {
        int i;
        for (i = 0; i < elevators.size(); i++) {
            Elevator elevator = elevators.get(i);
            if ((!elevator.getWaitList().isEmpty()) | (!elevator.getEleList().isEmpty())) {
                state = false;
                break;
            }
        }
        if (i == elevators.size()) {
            state = true;
        }
        waitQueue.addRequest(null);
    }

}

