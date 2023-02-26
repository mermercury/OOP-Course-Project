package com.oocourse.spec3.commit;

import com.oocourse.spec3.commit.Counter;
import com.oocourse.spec3.exceptions.GroupIdNotFoundException;

public class MyGroupIdNotFoundException extends GroupIdNotFoundException {
    private static Counter counter5 = new Counter();
    private static int time = 0;
    private int id;
    private int timeOfId;

    public MyGroupIdNotFoundException(int id) {
        this.id = id;
        timeOfId = counter5.getTime(id);
        time++;
    }

    @Override
    public void print() {
        System.out.println("ginf-" + time + ", " + id + "-" + timeOfId);
    }
}