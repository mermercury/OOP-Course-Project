package com.oocourse.spec3.commit;

import java.util.Arrays;

public class Dsu {
    private int[] size;
    private int[] root;

    public Dsu(int n) {
        size = new int[n + 1];
        root = new int[n + 1];
        Arrays.fill(size, 1);
        for (int i = 0; i < n + 1; i++) {
            root[i] = i;
        }
    }

    public int find(int x) {
        if (root[x] != x) {
            root[x] = find(root[x]);
        }
        return root[x];
    }

    public boolean union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);
        if (rootX == rootY) {
            return false;
        }

        if (size[rootX] < size[rootY]) {
            root[rootX] = rootY;
            size[rootY] += size[rootX];
        } else {
            root[rootY] = rootX;
            size[rootX] += size[rootY];
        }
        return true;
    }
}
