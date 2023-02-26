package com.oocourse.spec3.commit;

import com.oocourse.spec3.commit.Counter;
import com.oocourse.spec3.exceptions.EqualRelationException;

public class MyEqualRelationException extends EqualRelationException {
    private static Counter counter4 = new Counter();
    private static int time = 0;
    private int id1;
    private int id2;
    private int timeOfId1;
    private int timeOfId2;

    public MyEqualRelationException(int id1, int id2) {
        this.id1 = id1;
        this.id2 = id2;
        time++;
        if (id1 == id2) {
            timeOfId1 = counter4.getTime(id1);
            timeOfId2 = timeOfId1;
        } else {
            timeOfId1 = counter4.getTime(id1);
            timeOfId2 = counter4.getTime(id2);
        }
    }

    @Override
    public void print() {
        if (id1 < id2) {
            System.out.println("er-" + time + ", " + id1 + "-" + timeOfId1
                    + ", " + id2 + "-" + timeOfId2);
        }
        else {
            System.out.println("er-" + time + ", " + id2 + "-" + timeOfId2
                    + ", " + id1 + "-" + timeOfId1);
        }
    }
}
