
import java.util.ArrayList;

public class RequestQueue {
    private ArrayList<Person> requests;
    private boolean isEnd;

    public synchronized boolean isEnd() {
        notifyAll();
        return isEnd;
    }

    public RequestQueue() {
        requests = new ArrayList<>();
        this.isEnd = false;
    }

    public synchronized void setEnd(boolean end) {
        isEnd = end;
        notifyAll();
    }

    public synchronized ArrayList<Person> getRequests() {
        return requests;
    }

    public synchronized void addRequest(Person request) {
        requests.add(request);
        notifyAll();
    }

    public synchronized Person getOneRequest() {
        //这里放 if 还是 while 有区别吗
        if (requests.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (requests.isEmpty()) {
            return null;
        }
        Person request = requests.get(0);
        requests.remove(0);
        notifyAll();
        return request;
    }

    public synchronized Person readOneRequest() {
        //这里放 if 还是 while 有区别吗
        if (requests.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (requests.isEmpty()) {
            return null;
        }
        Person person = requests.get(0);
        notifyAll();
        return person;
    }

    public synchronized boolean isEmpty() {
        notifyAll();
        return requests.isEmpty();
    }
}
