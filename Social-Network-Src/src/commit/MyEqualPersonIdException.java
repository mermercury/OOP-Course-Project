package com.oocourse.spec3.commit;

import com.oocourse.spec3.commit.Counter;
import com.oocourse.spec3.exceptions.EqualPersonIdException;

public class MyEqualPersonIdException extends EqualPersonIdException {
    //    private int id;
    private static Counter counter1 = new Counter();
    private int id;
    private static int time = 0;
    private int timeOfId;

    public MyEqualPersonIdException(int id) {
        this.id = id;
        timeOfId = counter1.getTime(id);
        time++;
    }

    @Override
    public void print() {
        System.out.println("epi-" + time + ", " + id + "-" + timeOfId);
    }
}
