package com.oocourse.uml3.models.elements;

import java.util.HashMap;
import java.util.HashSet;

public class MyState {
    private UmlElement umlState;
    private HashSet<String> linkedStateId = new HashSet<>();
    private HashMap<UmlTransition, HashSet<UmlEvent>> trans2event;

    public MyState(UmlElement umlState) {
        this.umlState = umlState;
    }

    public String getName() {
        return umlState.getName();
    }

    public void addLinkedState(String id) {
        linkedStateId.add(id);
    }

    public HashSet<String> getLinkedStateId() {
        return linkedStateId;
    }

}
