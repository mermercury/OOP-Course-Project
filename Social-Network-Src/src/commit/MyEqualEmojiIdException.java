package com.oocourse.spec3.commit;

import com.oocourse.spec3.exceptions.EqualEmojiIdException;

public class MyEqualEmojiIdException extends EqualEmojiIdException {
    private static Counter counter10 = new Counter();
    private int id;
    private static int time = 0;
    private int timeOfId;

    public MyEqualEmojiIdException(int id) {
        this.id = id;
        timeOfId = counter10.getTime(id);
        time++;
    }

    @Override
    public void print() {
        System.out.println("eei-" + time + ", " + id + "-" + timeOfId);
    }
}