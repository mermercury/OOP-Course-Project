package com.oocourse.spec3.commit;

import com.oocourse.spec3.commit.Counter;
import com.oocourse.spec3.exceptions.PersonIdNotFoundException;

public class MyPersonIdNotFoundException extends PersonIdNotFoundException {
    private static Counter counter2 = new Counter();
    private static int time = 0;
    private int id;
    private int timeOfId;

    public MyPersonIdNotFoundException(int id) {
        this.id = id;
        timeOfId = counter2.getTime(id);
        time++;
    }

    @Override
    public void print() {
        System.out.println("pinf-" + time + ", " + id + "-" + timeOfId);
    }
}
