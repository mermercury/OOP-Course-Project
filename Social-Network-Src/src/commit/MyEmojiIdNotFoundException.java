package com.oocourse.spec3.commit;

import com.oocourse.spec3.exceptions.EmojiIdNotFoundException;

public class MyEmojiIdNotFoundException extends EmojiIdNotFoundException {
    private static Counter counter9 = new Counter();
    private static int time = 0;
    private int id;
    private int timeOfId;

    public MyEmojiIdNotFoundException(int id) {
        this.id = id;
        timeOfId = counter9.getTime(id);
        time++;
    }

    @Override
    public void print() {
        System.out.println("einf-" + time + ", " + id + "-" + timeOfId);
    }

}
