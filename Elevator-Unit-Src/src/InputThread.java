
import com.oocourse.elevator3.ElevatorInput;
import com.oocourse.elevator3.ElevatorRequest;
import com.oocourse.elevator3.PersonRequest;
import com.oocourse.elevator3.Request;

import java.io.IOException;

public class InputThread extends Thread {
    private final RequestQueue waitQueue;
    private Schedule schedule;

    public InputThread(RequestQueue waitQueue, Schedule schedule) {
        this.waitQueue = waitQueue;
        this.schedule = schedule;
    }

    @Override
    public void run() {
        ElevatorInput elevatorInput = new ElevatorInput(System.in);
        while (true) {
            Request request = elevatorInput.nextRequest();
            if (request == null) {
                waitQueue.setEnd(true);
                break;
            } else {
                if (request instanceof PersonRequest) {
                    waitQueue.addRequest(new Person((PersonRequest) request));
                } else if (request instanceof ElevatorRequest) {
                    ElevatorFactory.createElevator((ElevatorRequest)request, schedule);
                }
            }
        }
        try {
            elevatorInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
