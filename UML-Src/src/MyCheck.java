package com.oocourse.uml3.models.elements;

import com.oocourse.uml3.interact.common.AttributeClassInformation;
import com.oocourse.uml3.models.common.Direction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MyCheck {
    private HashMap<String, MyUmlClass> id2class;
    private HashMap<String, MyUmlInterface> id2interface;
    private HashMap<String, UmlAssociationEnd> id2associationEnd;
    private HashMap<String, ArrayList<UmlEvent>> transId2event;
    private HashMap<String, UmlTransition> id2trans;
    private boolean isCheck1 = false;
    private boolean isCheck9 = false;
    private ArrayList<ArrayList<MyUmlInterface>> ans = new ArrayList<>();
    private ArrayList<MyUmlInterface> interfaces;
    private ArrayList<MyUmlInterface> stack = new ArrayList<>();
    private int time = 1;
    private int[] dfn;
    private int[] low;

    public MyCheck(HashMap<String, MyUmlClass> id2class, HashMap<String
            , MyUmlInterface> id2interface, HashMap<String, UmlAssociationEnd> id2associationEnd
            , HashMap<String, ArrayList<UmlEvent>> transId2event, HashMap<String
            , UmlTransition> id2trans, ArrayList<MyUmlInterface> interfaces) {
        this.id2class = id2class;
        this.id2interface = id2interface;
        this.id2associationEnd = id2associationEnd;
        this.transId2event = transId2event;
        this.id2trans = id2trans;
        this.interfaces = interfaces;
    }

    public Set checkFor003(Set cl2ins) {
        dfn = new int[interfaces.size()];
        low = new int[interfaces.size()];
        HashSet<MyUmlClass> classes = new HashSet<>();
        for (MyUmlClass myclass : id2class.values()) {
            myclass.checkCir(classes, id2class); }
        HashSet<MyUmlInterface> ins = new HashSet<>();
        checkCircle();
        for (ArrayList<MyUmlInterface> list : ans) {
            if (list.size() >= 2) {
                for (int i = 0; i < list.size(); i++) {
                    ins.add(list.get(i)); } } }
        for (MyUmlClass x : classes) { cl2ins.add(x.getUmlClass()); }
        for (MyUmlInterface y : ins) { cl2ins.add(y.getUmlInterface()); }
        return cl2ins;
    }

    public void checkCircle() {
        int size = interfaces.size();
        for (int i = 0; i < size; i++) {
            if (dfn[i] == 0) { // 这个点还没有被访问过
                dfs(interfaces.get(i), i); } } }

    public void dfs(MyUmlInterface inter, int x) {
        stack.add(inter);
        dfn[x] = time++;
        low[x] = dfn[x];
        int size = interfaces.size();
        for (int i = 0; i < size; i++) {
            MyUmlInterface y = interfaces.get(i);
            if (inter.hasFather(y)) {
                if (dfn[i] == 0) {
                    dfs(y, i);
                    low[x] = ((low[x] < low[i]) ? low[x] : low[i]);
                } else if (stack.contains(y)) {
                    low[x] = ((low[x] < low[i]) ? low[x] : low[i]); } } }
        if (dfn[x] == low[x]) {
            ArrayList<MyUmlInterface> list = new ArrayList<>();
            while (true) {
                int index = stack.size() - 1;
                MyUmlInterface in = stack.get(index);
                list.add(in);
                stack.remove(index);
                if (in.equals(inter)) {
                    break; } }
            ans.add(list); } }

    public Set check002() {
        HashSet<AttributeClassInformation> pairs = new HashSet<>();
        for (MyUmlClass myClass : id2class.values()) {
            myClass.checkAttr(pairs);
        }
        return pairs;
    }

    public boolean check001(UmlElement... elements) {
        for (UmlElement e : elements) {
            String name = e.getName();
            switch (e.getElementType()) {
                case UML_ATTRIBUTE:
                    if (id2class.containsKey(e.getParentId())
                            || id2interface.containsKey(e.getParentId())) {
                        if (name == null) {
                            isCheck1 = true;
                        } else if (name.matches("[ \t]*")) {
                            isCheck1 = true;
                        }
                    }
                    break;
                case UML_PARAMETER:
                    UmlParameter upa = (UmlParameter) e;
                    if (!(upa.getDirection() == Direction.RETURN)) {
                        if (name == null) {
                            isCheck1 = true;
                        } else if (name.matches("[ \t]*")) {
                            isCheck1 = true;
                        }
                    }
                    break;
                case UML_INTERFACE:
                case UML_CLASS:
                case UML_OPERATION:
                    if (name == null) {
                        isCheck1 = true;
                    } else if (name.matches("[ \t]*")) {
                        isCheck1 = true;
                    }
                    break;
                default:
            }
        }
        return isCheck1;
    }

    public void checkEvent(String id1, String id2) {
        ArrayList<UmlEvent> events1 = transId2event.get(id1);
        ArrayList<UmlEvent> events2 = transId2event.get(id2);
        for (int i = 0; i < events1.size(); i++) {
            for (int j = 0; j < events2.size(); j++) {
                UmlEvent event1 = events1.get(i);
                UmlEvent event2 = events2.get(j);
                if (event1.getName().equals(event2.getName())) {
                    String guard1 = id2trans.get(id1).getGuard();
                    String guard2 = id2trans.get(id2).getGuard();
                    if (guard1 == null || guard2 == null) {
                        isCheck9 = true;
                        return;
                    }
                    if (guard1.equals(guard2) || (guard1.matches("[ \t]*"))
                            || (guard2.matches("[ \t]*"))) {
                        isCheck9 = true;
                        return;
                    }
                }
            }
        }
    }

    public boolean checkFor009() {
        for (String id1 : id2trans.keySet()) {
            for (String id2 : id2trans.keySet()) {
                // 找到从同一个状态出发的两个transition检查它们的trigger
                if (!id1.equals(id2)
                        && (id2trans.get(id1).getSource()).equals(id2trans.get(id2).getSource())) {
                    checkEvent(id1, id2);
                }
            }
        }
        return isCheck9;
    }

}
