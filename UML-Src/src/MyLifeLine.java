package com.oocourse.uml3.models.elements;

public class MyLifeLine {
    private UmlLifeline lifeline;
    private int foundCount;
    private int lostCount;
    private boolean deleted;

    public MyLifeLine(UmlLifeline lifeline) {
        this.lifeline = lifeline;
        foundCount = 0;
        lostCount = 0;
        deleted = false;
    }

    public String getName() {
        return lifeline.getName();
    }

    public String getId() {
        return lifeline.getId();
    }

    public void addFound() {
        foundCount++;
    }

    public void addLost() {
        lostCount++;
    }

    public UmlLifeline getLifeline() {
        return lifeline;
    }

    public int getFoundCount() {
        return foundCount;
    }

    public int getLostCount() {
        return lostCount;
    }

    public void setDeleted() {
        this.deleted = true;
    }

    public boolean getDeleted() {
        return deleted;
    }
}
