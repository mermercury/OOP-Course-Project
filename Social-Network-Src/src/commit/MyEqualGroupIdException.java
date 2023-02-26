package com.oocourse.spec3.commit;

import com.oocourse.spec3.exceptions.EqualGroupIdException;

public class MyEqualGroupIdException extends EqualGroupIdException {
    private static Counter counter6 = new Counter();
    private int id;
    private static int time = 0;
    private int timeOfId;

    public MyEqualGroupIdException(int id) {
        this.id = id;
        timeOfId = counter6.getTime(id);
        time++;
    }

    @Override
    public void print() {
        System.out.println("egi-" + time + ", " + id + "-" + timeOfId);
    }
}