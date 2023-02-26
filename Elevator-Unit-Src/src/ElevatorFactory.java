import com.oocourse.elevator3.ElevatorRequest;

public class ElevatorFactory {
    public static void createElevator(ElevatorRequest elevatorRequest, Schedule schedule) {
        if (elevatorRequest.getType().equals("building")) {
            ElevatorVer elevatorVer = new ElevatorVer(elevatorRequest.getBuilding()
                    , new RequestQueue(), elevatorRequest.getElevatorId()
                    , elevatorRequest.getCapacity()
                    , (int)(elevatorRequest.getSpeed() * 1000), schedule);
            String s = String.valueOf(elevatorRequest.getBuilding());
            Main.getBuildings().get(s).addElevator(elevatorVer);
            elevatorVer.start();
            schedule.getElevators().add(elevatorVer);
        } else if (elevatorRequest.getType().equals("floor")) {
            int floor = elevatorRequest.getFloor();
            ElevatorHon elevatorHon = new ElevatorHon("FLOOR" + floor
                    , new RequestQueue(), elevatorRequest.getElevatorId()
                    , floor, elevatorRequest.getCapacity()
                    , (int)(elevatorRequest.getSpeed() * 1000)
                    , elevatorRequest.getSwitchInfo(), schedule);
            String buildingName = "FLOOR" + floor;
            Main.getBuildings().get(buildingName).addElevator(elevatorHon);
            elevatorHon.start();
            schedule.getElevators().add(elevatorHon);
        }
    }

    public static ElevatorVer initElevatorVer(char buildingName, int id, Schedule schedule) {
        ElevatorVer elevatorVer = new ElevatorVer(buildingName
                , new RequestQueue(), id, 8, 600, schedule);
        elevatorVer.start();
        schedule.getElevators().add(elevatorVer);
        return elevatorVer;
    }

    public static ElevatorHon initElevatorHon(Schedule schedule) {
        ElevatorHon elevatorHon = new ElevatorHon("FLOOR1",new RequestQueue(), 6
                , 1, 8, 600, 31, schedule);
        elevatorHon.start();
        schedule.getElevators().add(elevatorHon);
        return elevatorHon;
    }
}
