
import com.oocourse.TimableOutput;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    public static HashMap<String, Building> getBuildings() {
        return buildings;
    }

    private static HashMap<String, Building> buildings = new HashMap<>();

    public static void main(String[] args) {
        TimableOutput.initStartTimestamp();  // 初始化时间戳

        RequestQueue waitQueue = new RequestQueue();

        ArrayList<RequestQueue> waitingQueues = new ArrayList<>();

        Schedule schedule = new Schedule(waitQueue, waitingQueues);
        schedule.start();

        for (int i = 0; i < 5; i++) {
            char tmp = (char) ('A' + i);
            String buildingName = String.valueOf(tmp);
            Building building = new Building(buildingName, 1, -1, schedule);
            buildings.put(buildingName, building);
            ElevatorVer elevatorVer = ElevatorFactory.initElevatorVer(tmp, (i + 1), schedule);
            building.addElevator(elevatorVer);
            building.start();
            waitingQueues.add(building.getAllPersonReqs());
        }

        for (int i = 1; i <= 10; i++) {
            Building building = new Building("FLOOR" + i, 2, i, schedule);
            buildings.put("FLOOR" + i, building);
            waitingQueues.add(building.getAllPersonReqs());
            building.start();
            if (i == 1) {
                building.addElevator(ElevatorFactory.initElevatorHon(schedule));
            }
        }

        InputThread inputThread = new InputThread(waitQueue, schedule);
        inputThread.start();
    }
}
