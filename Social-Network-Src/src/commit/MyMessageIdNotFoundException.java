package com.oocourse.spec3.commit;

import com.oocourse.spec3.exceptions.MessageIdNotFoundException;

public class MyMessageIdNotFoundException extends MessageIdNotFoundException {
    private static Counter counter8 = new Counter();
    private static int time = 0;
    private int id;
    private int timeOfId;

    public MyMessageIdNotFoundException(int id) {
        this.id = id;
        timeOfId = counter8.getTime(id);
        time++;
    }

    @Override
    public void print() {
        System.out.println("minf-" + time + ", " + id + "-" + timeOfId);
    }
}
