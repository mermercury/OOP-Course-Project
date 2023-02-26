package com.oocourse.spec3.commit;

public class Edge implements Comparable<Edge> {
    private int id; //邻接结点:person id
    private int dis;//queryValue

    public Edge(int id, int dis) {
        this.id = id;
        this.dis = dis;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDis() {
        return dis;
    }

    @Override
    public int compareTo(Edge o) {
        return this.dis - o.dis;
    }
}

