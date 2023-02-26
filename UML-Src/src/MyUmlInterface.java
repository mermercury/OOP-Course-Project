package com.oocourse.uml3.models.elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class MyUmlInterface {
    private UmlInterface umlInterface;
    private ArrayList<MyUmlInterface> fatherInterfaces;
    private HashMap<String, ArrayList<UmlAttribute>> name2attributes = new HashMap<>();
    private ArrayList<Object> associations = new ArrayList<>();
    private ArrayList<String> parentPath = new ArrayList<>();
    private HashSet<String> fatherIds = new HashSet<>();
    private HashSet<String> sonIds = new HashSet<>();
    private boolean check004 = false;

    public MyUmlInterface(UmlInterface umlInterface) {
        this.umlInterface = umlInterface;
        fatherInterfaces = new ArrayList<>();
    }

    public void addAssociation(Object umlInterface) {
        associations.add(umlInterface);
    }

    public void addFatherId(String id) {
        fatherIds.add(id);
    }

    public void updateFather(HashMap<String, MyUmlInterface> id2interface, String fa) {
        for (String id : id2interface.keySet()) {
            if (sonIds.contains(id)) {
                id2interface.get(id).addFatherId(fa);
            }
        }
    }

    public HashSet<String> getSonIds() {
        return sonIds;
    }

    public boolean addSonId(String id, MyUmlInterface son) {
        boolean flag = true;
        if (sonIds.contains(id)) {
            flag = false;
        } else {
            sonIds.add(id);
        }
        HashSet<String> sons = son.getSonIds();
        for (String sonId : sons) {
            sonIds.add(sonId);
        }
        return flag;
    }

    public boolean getCheck004() {
        return check004;
    }

    public void setCheck004(boolean check004) {
        this.check004 = check004;
    }

    public void addAttribute(UmlAttribute attribute) {
        String name = attribute.getName();
        if (name2attributes.containsKey(name)) {
            (name2attributes.get(name)).add(attribute);
        } else {
            name2attributes.put(name, new ArrayList<>());
            (name2attributes.get(name)).add(attribute);
        }
    }

    public void addFatherInterface(MyUmlInterface umlInterface) {
        fatherInterfaces.add(umlInterface);
    }

    public String getName() {
        return umlInterface.getName();
    }

    public void getFatherInterfaces(List<String> names) {
        for (MyUmlInterface a : fatherInterfaces) {
            names.add(a.getName());
            a.getFatherInterfaces(names);
        }
    }

    public boolean getFathers(List<String> ids) {
        for (MyUmlInterface a : fatherInterfaces) {
            if (fatherInterfaces.size() == 0) {
                break;
            }
            ids.add(a.getId());
            a.getFathers(ids);
        }
        HashMap<String, Integer> cnt = new HashMap<>();
        for (int i = 0; i < ids.size(); i++) {
            String fatherId = ids.get(i);
            if (cnt.containsKey(fatherId)) {
                int tmp = cnt.get(fatherId);
                tmp++;
                cnt.put(fatherId, tmp);
            } else {
                cnt.put(fatherId, 1);
            }
        }
        for (Integer i : cnt.values()) {
            if (i > 1) {
                return false;
            }
        }
        return true;
    }

    public String getId() {
        return umlInterface.getId();
    }

    public boolean hasFather(MyUmlInterface fa) {
        if (fatherInterfaces.contains(fa)) {
            return true;
        }
        return false;
    }

    public UmlInterface getUmlInterface() {
        return umlInterface;
    }
}
