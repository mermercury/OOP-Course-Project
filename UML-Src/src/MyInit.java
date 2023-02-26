package com.oocourse.uml3.models.elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MyInit {
    public static void init1(HashSet<String> classNames
            , HashMap<String, String> name2idC, HashMap<String, Integer> className2ChildNum
            , HashMap<String, MyUmlInterface> id2interface, ArrayList<MyUmlInterface> interfaces
            , HashMap<String, UmlAssociationEnd> id2associationEnd
            , HashMap<String, HashSet<String>> coll2attr, UmlElement... elements) {
        HashMap<String, MyUmlClass> id2class = MyImplementation.getId2class();
        HashSet<String> dupClassName = MyImplementation.getDup();
        for (UmlElement e : elements) {
            switch (e.getElementType()) {
                case UML_CLASS:
                    MyUmlClass umlClass = new MyUmlClass((UmlClass) e);
                    String name = e.getName();
                    id2class.put(e.getId(), umlClass);
                    if (!dupClassName.contains(name)) {
                        if (classNames.contains(name)) {
                            dupClassName.add(name);
                        } else {
                            classNames.add(umlClass.getName());
                            name2idC.put(e.getName(), e.getId());
                            className2ChildNum.put(name, 0); } }
                    break;
                case UML_INTERFACE:
                    MyUmlInterface umlInterface = new MyUmlInterface((UmlInterface) e);
                    id2interface.put(e.getId(), umlInterface);
                    interfaces.add(umlInterface);
                    break;
                case UML_ASSOCIATION_END:
                    UmlAssociationEnd a = (UmlAssociationEnd) e;
                    id2associationEnd.put(e.getId(), a);
                    break;
                case UML_COLLABORATION:
                    coll2attr.put(e.getId(), new HashSet<>());
                    break;
                default:
                    break; } } }

    public static void init3(HashMap<String, MyUmlClass> id2class, Set cl2ins,
              HashMap<String, Integer> className2ChildNum
            , HashMap<String, MyUmlOperation> id2operation
            , HashMap<String, MyUmlInterface> id2interface, HashMap<String, String> name2idO
            , UmlElement... elements) {
        for (UmlElement e : elements) {
            switch (e.getElementType()) {
                case UML_OPERATION:
                    String id = e.getId(); // 方法的id
                    String id1 = e.getParentId(); // 所属类的id
                    String name = e.getName();
                    MyUmlOperation op = new MyUmlOperation((UmlOperation) e);
                    MyUmlClass myclass = id2class.get(id1);
                    name2idO.put(name, id);
                    id2operation.put(id, op);
                    myclass.addOperation(op);
                    break;
                case UML_GENERALIZATION:
                    UmlGeneralization gen = (UmlGeneralization) e;
                    id1 = gen.getSource();
                    String id2 = gen.getTarget();
                    if (id1.equals(id2)) {
                        if (id2class.containsKey(id1)) {
                            cl2ins.add(id2class.get(id1).getUmlClass());
                        } else if (id2interface.containsKey(id1)) {
                            cl2ins.add(id2interface.get(id1).getUmlInterface()); } }
                    if (id2class.containsKey(id1)) {
                        id2class.get(id1).setFatherClass(id2class.get(id2));
                        id2class.get(id2).addChildClass(id2class.get(id1));
                        Integer t = className2ChildNum.get((id2class.get(id2)).getName());
                        t++;
                        className2ChildNum.replace((id2class.get(id2)).getUmlClass().getName(), t);
                    } else if (id2interface.containsKey(id1)) {
                        id2interface.get(id1).addFatherInterface(id2interface.get(id2)); }
                    break;
                case UML_INTERFACE_REALIZATION:
                    UmlInterfaceRealization uir = (UmlInterfaceRealization) e;
                    id1 = uir.getSource(); // UmlClass
                    id2 = uir.getTarget(); // UmlInterface
                    id2class.get(id1).addInterface(id2interface.get(id2));
                    break;
                default:
                    break; } } }
}
