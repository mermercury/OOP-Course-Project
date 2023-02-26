package com.oocourse.uml3.models.elements;

import com.oocourse.uml3.interact.common.Pair;

import java.util.HashMap;
import java.util.HashSet;

public class MyInteraction {
    private UmlInteraction interaction;
    private HashMap<String, MyLifeLine> id2lifeline = new HashMap<>();
    private HashMap<String, String> name2idOfLifeline = new HashMap<>();
    private HashSet<String> dupLifeline = new HashSet<>();

    public MyInteraction(UmlInteraction interaction) {
        this.interaction = interaction;
    }

    public void addLifeLine(MyLifeLine lifeline) {
        String id = lifeline.getId();
        String name = lifeline.getName();
        id2lifeline.put(id, lifeline);
        if (name2idOfLifeline.containsKey(name)) {
            dupLifeline.add(name);
        } else {
            name2idOfLifeline.put(name, id);
        }
    }

    public int getParticipantCount() {
        return id2lifeline.size();
    }

    public void addFoundMsg(String lifeLineId) {
        id2lifeline.get(lifeLineId).addFound();
    }

    public void addLostMsg(String lifeLineId) {
        id2lifeline.get(lifeLineId).addLost();
    }

    public boolean containsLifeline(String lifeLineName) {
        if (name2idOfLifeline.containsKey(lifeLineName)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isDupLifeline(String lifeLineName) {
        if (dupLifeline.contains(lifeLineName)) {
            return true;
        } else {
            return false;
        }
    }

    public UmlLifeline getLifeline(String lifeLineId) {
        return (id2lifeline.get(lifeLineId)).getLifeline();
    }

    public Pair<Integer, Integer> getFoundAndLost(String lifeLineName) {
        MyLifeLine lifeLine = id2lifeline.get(name2idOfLifeline.get(lifeLineName));
        Integer x = lifeLine.getFoundCount();
        Integer y = lifeLine.getLostCount();
        Pair<Integer, Integer> pair = new Pair(x, y);
        return pair;
    }

    public String getId() {
        return interaction.getId();
    }

    public String getParentId() {
        return interaction.getParentId();
    }
}
