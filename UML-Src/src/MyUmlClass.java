package com.oocourse.uml3.models.elements;

import com.oocourse.uml3.interact.common.AttributeClassInformation;
import com.oocourse.uml3.models.common.ReferenceType;
import com.oocourse.uml3.models.common.Visibility;

import java.util.*;

public class MyUmlClass {
    private UmlClass umlClass;
    private HashMap<String, ArrayList<UmlAttribute>> name2attributes = new HashMap<>();
    private HashMap<String, ArrayList<MyUmlOperation>> name2operations = new HashMap<>();
    private MyUmlClass fatherClass;
    private ArrayList<MyUmlInterface> interfaces = new ArrayList<>();
    private ArrayList<Object> associations = new ArrayList<>();
    private ArrayList<MyUmlClass> childClass = new ArrayList<>();
    private ArrayList<String> interfaceList = new ArrayList<>(); // 名字
    private boolean hasInterfaceList;
    private boolean hasAttrCoupling;
    private int attrCoupling;
    private ArrayList<String> parentPath = new ArrayList<>();
    private HashMap<String, Integer> attrs = new HashMap<>();

    public MyUmlClass(UmlClass umlClass) {
        this.umlClass = umlClass;
        fatherClass = null;
        hasInterfaceList = false;
        attrCoupling = 0;
        hasAttrCoupling = false;
    }

    public void checkAttr(HashSet<AttributeClassInformation> pairs) {
        for (String name : name2attributes.keySet()) {
            ArrayList list = name2attributes.get(name);
            if (list.size() > 1) {
                pairs.add(new AttributeClassInformation(name,getName()));
            }
        }
        for (String name : attrs.keySet()) {
            if (attrs.get(name) > 1 && !(name.matches("[ \t]*"))) {
                pairs.add(new AttributeClassInformation(name,getName()));
            } else if (name2attributes.containsKey(name)) {
                pairs.add(new AttributeClassInformation(name,getName()));
            }
        }
    }

    public UmlClass getUmlClass() {
        return umlClass;
    }

    public int getOperationCounter() {
        int counter = 0;
        for (List operations : name2operations.values()) {
            counter += operations.size();
        }
        return counter;
    }

    public Map getOperationVisibility(String s) {
        Map map = new HashMap<Visibility, Integer>();
        map.put(Visibility.PUBLIC, 0);
        map.put(Visibility.PRIVATE, 0);
        map.put(Visibility.PACKAGE, 0);
        map.put(Visibility.PROTECTED, 0);
        if (!name2operations.containsKey(s)) {
            return map;
        }
        List ops = name2operations.get(s);
        for (Object op : ops) {
            if (op instanceof MyUmlOperation) {
                Visibility v = ((MyUmlOperation)op).getVisibility();
                Integer tmp = (Integer) (map.get(v));
                tmp++;
                map.replace(v, tmp);
            }
        }
        return map;
    }

    public MyUmlClass getFatherClass() {
        return fatherClass;
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

    public Boolean containsOpByname(String name) {
        if (name2operations.containsKey(name)) {
            return true;
        } else {
            return false;
        }
    }

    public List<Integer> calCoupling(String methodName) {
        List<MyUmlOperation> methods = name2operations.get(methodName);
        List<Integer> list = new ArrayList<>();
        for (MyUmlOperation op : methods) {
            list.add(op.calMCoupling(getId()));
        }
        return list;
    }

    public int calAttrCoupling(String classId, HashSet referenceType) {
        for (ArrayList<UmlAttribute> attr : name2attributes.values()) {
            for (UmlAttribute a : attr) {
                if (a.getType() instanceof ReferenceType) {
                    if (!(((ReferenceType) a.getType()).getReferenceId()).equals(classId)) {
                        referenceType.add(((ReferenceType) a.getType()).getReferenceId());
                    }
                }
            }
        }
        if (fatherClass != null) {
            attrCoupling = fatherClass.calAttrCoupling(classId, referenceType);
        } else {
            attrCoupling = referenceType.size();
        }
        return attrCoupling;
    }

    public boolean isDupMethod(MyUmlOperation operation) {
        String opName = operation.getName();
        if (!name2operations.containsKey(opName)) {
            return false;
        }
        List<MyUmlOperation> ops = name2operations.get(opName);
        for (MyUmlOperation tmp : ops) {
            if (tmp != operation && tmp.hasDupParas(operation) && (tmp.getName()).equals(opName)) {
                return true;
            }
        }
        return false;
    }

    public void addOperation(MyUmlOperation operation) {
        String name = operation.getName();
        if (name2operations.containsKey(name)) {
            (name2operations.get(name)).add(operation);
        } else {
            name2operations.put(name, new ArrayList<>());
            (name2operations.get(name)).add(operation);
        }
    }

    public void addAssociation(String name) {
        //associations.add(element);
        if (name != null) {
            if (attrs.containsKey(name)) {
                int i = attrs.get(name);
                i++;
                attrs.put(name, i);
            } else {
                attrs.put(name, 1);
            }
        }
    }

    public void addInterface(MyUmlInterface umlInterface) {
        interfaces.add(umlInterface);
    }

    public void setFatherClass(MyUmlClass fatherClass) {
        this.fatherClass = fatherClass;
    }

    public void addChildClass(MyUmlClass child) {
        childClass.add(child);
    }

    public void getInterfaceList(List<String> names) {
        if (hasInterfaceList) {            // 如果已经调用过该方法，则直接返回结果
            names.addAll(interfaceList);
            return;
        }

        for (MyUmlInterface a : interfaces) {
            interfaceList.add(a.getName());
            a.getFatherInterfaces(interfaceList);      // 调用该接口的该方法，寻找间接接口
        }

        if (fatherClass != null) {
            fatherClass.getInterfaceList(interfaceList);
        }

        hasInterfaceList = true;
        names.addAll(interfaceList);   // 更新传入的names

    }

    public String getName() {
        return umlClass.getName();
    }

    public String getId() {
        return umlClass.getId();
    }

    public void checkCir(HashSet<MyUmlClass> classes, HashMap<String, MyUmlClass> id2class) {
        MyUmlClass curFa = fatherClass;
        parentPath.add(getId());
        while (curFa != null) {
            String id = curFa.getId();
            if (parentPath.contains(id)) {
                int index = parentPath.indexOf(id);
                int size = parentPath.size();
                for (int i = index; i < size; i++) {
                    classes.add((id2class.get(parentPath.get(i))));
                }
                for (int i = index; i < size; i++) {
                    parentPath.remove(index);
                }
                break;
            }
            parentPath.add(id);
            curFa = curFa.getFatherClass();
        }
    }
}
