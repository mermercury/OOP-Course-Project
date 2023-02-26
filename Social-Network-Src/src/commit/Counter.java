package com.oocourse.spec3.commit;

import java.util.HashMap;

public class Counter {
    private HashMap<Integer, Integer> id2time;

    public Counter() {
        id2time = new HashMap<>();
    }

    public int getTime(int id) {
        if (id2time.containsKey(id)) {
            id2time.put(id, ((id2time.get(id) + 1)));
            return id2time.get(id);
        } else {
            id2time.put(id, 1);
            return 1;
        }
    }
}
