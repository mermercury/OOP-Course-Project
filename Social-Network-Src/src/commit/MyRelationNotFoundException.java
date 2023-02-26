package com.oocourse.spec3.commit;

import com.oocourse.spec3.commit.Counter;
import com.oocourse.spec3.exceptions.RelationNotFoundException;

public class MyRelationNotFoundException extends RelationNotFoundException {
    private static Counter counter3 = new Counter();
    private static int time = 0;
    private int id1;
    private int id2;
    private int timeOfId1;
    private int timeOfId2;

    public MyRelationNotFoundException(int id1, int id2) {
        this.id1 = id1;
        this.id2 = id2;
        time++;
        timeOfId1 = counter3.getTime(id1);
        timeOfId2 = counter3.getTime(id2);
    }

    @Override
    public void print() {
        if (id1 < id2) {
            System.out.println("rnf-" + time + ", "
                    + id1 + "-" + timeOfId1 + ", " + id2 + "-" + timeOfId2);
        } else {
            System.out.println("rnf-" + time + ", "
                    + id2 + "-" + timeOfId2 + ", " + id1 + "-" + timeOfId1);
        }

    }
}