package com.oocourse.spec3.commit;

import com.oocourse.spec3.exceptions.EqualMessageIdException;

public class MyEqualMessageIdException extends EqualMessageIdException {
    private static Counter counter7 = new Counter();
    private int id;
    private static int time = 0;
    private int timeOfId;

    public MyEqualMessageIdException(int id) {
        this.id = id;
        timeOfId = counter7.getTime(id);
        time++;
    }

    @Override
    public void print() {
        System.out.println("emi-" + time + ", " + id + "-" + timeOfId);
    }
}
