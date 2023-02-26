package com.oocourse.uml3.models.elements;

import com.oocourse.uml3.interact.common.AttributeClassInformation;
import com.oocourse.uml3.interact.common.Pair;
import com.oocourse.uml3.interact.exceptions.user.*;
import com.oocourse.uml3.interact.exceptions.user.ClassNotFoundException;
import com.oocourse.uml3.interact.format.UserApi;
import com.oocourse.uml3.models.common.Direction;
import com.oocourse.uml3.models.common.NamedType;
import com.oocourse.uml3.models.common.Visibility;
import java.util.*;

import static com.oocourse.uml3.models.common.ElementType.UML_FINAL_STATE;
import static com.oocourse.uml3.models.common.MessageSort.CREATE_MESSAGE;
import static com.oocourse.uml3.models.common.MessageSort.DELETE_MESSAGE;

public class MyImplementation implements UserApi {
    private static HashMap<String, MyUmlClass> id2class = new HashMap<>();
    private static HashMap<String, String> name2idC = new HashMap<>();
    private static HashSet<String> classNames = new HashSet<>();
    private static HashMap<String, MyUmlInterface> id2interface = new HashMap<>();
    private static HashMap<String, MyUmlOperation> id2operation = new HashMap<>();
    private static HashMap<String, String> name2idO = new HashMap<>();
    private static HashMap<String, UmlAssociationEnd> id2associationEnd = new HashMap<>();
    private static HashSet<String> dupClassName = new HashSet<>();
    private static HashMap<String, Integer> className2ChildNum = new HashMap<>();
    private static HashMap<String, ArrayList<String>> class2methodWrongT = new HashMap<>();
    private static HashMap<String, ArrayList<String>> class2dupMethod = new HashMap<>();
    private static HashMap<String, MyStateMachine> id2stateMachine = new HashMap<>();
    private static HashMap<String, String> name2idOfStateMachine = new HashMap<>();
    private static HashMap<String, String> region2idOfStateMachine = new HashMap<>();
    private static HashSet<String> dupStateMachine = new HashSet<>();
    private static HashMap<String, ArrayList<UmlEvent>> transId2event = new HashMap<>();
    private static HashMap<String, UmlTransition> id2trans = new HashMap<>();
    private static HashMap<String, MyInteraction> id2interaction = new HashMap<>();
    private static HashMap<String, String> name2idOfInteraction = new HashMap<>();
    private static HashSet<String> dupInteraction = new HashSet<>();
    private static HashMap<String, HashMap<String, UmlLifeline>> inId2llCreator = new HashMap();
    private static HashMap<String, HashSet<String>> inId2dupCreated = new HashMap<>();
    private static HashMap<String, UmlEndpoint> id2endPoint = new HashMap<>();
    private static HashMap<String, String> stateId2name = new HashMap<>();
    private static HashMap<String, MyLifeLine> llId2ll = new HashMap<>();
    private static ArrayList<MyUmlInterface> interfaces = new ArrayList<>();
    private static boolean flag1 = false;
    private static boolean isCheck5 = false;
    private static boolean isCheck6 = false;
    private static boolean isCheck7 = false;
    private static boolean isCheck8 = false;
    private static ArrayList<MyUmlInterface> stack = new ArrayList<>();
    private static HashMap<String, HashSet<String>> coll2attr = new HashMap<>();
    private static HashSet<String> finalStateId = new HashSet<>();
    private static MyCheck myCheck;
    private HashSet<AttributeClassInformation> pairs;
    private static Set cl2ins = new HashSet<>();
    private static Set<UmlClassOrInterface> ans004 = new HashSet<>();

    public static HashMap<String, MyUmlClass> getId2class() { return id2class; }

    public static HashSet<String> getDup() { return dupClassName; }

    public MyImplementation(UmlElement... elements) {
        MyInit.init1(classNames
                , name2idC, className2ChildNum, id2interface
                , interfaces, id2associationEnd, coll2attr, elements);
        init2(elements);
        MyInit.init3(id2class, cl2ins, className2ChildNum, id2operation
                , id2interface, name2idO, elements);
        init4(elements);
        init5(elements);
        init6(elements);
        init7(elements);
        for (MyStateMachine stateMachine : id2stateMachine.values()) {
            stateMachine.findCriticalState(); }
        for (MyUmlOperation op : id2operation.values()) {
            String paId = op.getParentId();
            MyUmlClass myclass = id2class.get(paId);
            String className = myclass.getName();
            String name = op.getName();
            if (myclass.isDupMethod(op)) {
                if (class2dupMethod.containsKey(className)) {
                    class2dupMethod.get(className).add(name);
                } else {
                    class2dupMethod.put(className, new ArrayList<>(Arrays.asList(name))); } } }
        myCheck = new MyCheck(id2class, id2interface, id2associationEnd
                , transId2event, id2trans, interfaces);
        pairs = (HashSet<AttributeClassInformation>) myCheck.check002();
        flag1 = myCheck.check001(elements); }

    public int getClassCount() {
        return id2class.size(); }

    public void classNameE(String clName) throws ClassNotFoundException, ClassDuplicatedException {
        if (!classNames.contains(clName)) {
            throw new ClassNotFoundException(clName);
        } else if (dupClassName.contains(clName)) {
            throw new ClassDuplicatedException(clName); } }

    public int getClassSubClassCount(String className)
            throws ClassNotFoundException, ClassDuplicatedException {
        classNameE(className);
        return className2ChildNum.get(className); }

    public int getClassOperationCount(String className)
            throws ClassNotFoundException, ClassDuplicatedException {
        classNameE(className);
        String tmp = name2idC.get(className);
        MyUmlClass myUmlClass = id2class.get(tmp);
        return myUmlClass.getOperationCounter(); }

    public Map<Visibility, Integer>
        getClassOperationVisibility(String className, String methodName)
            throws ClassNotFoundException, ClassDuplicatedException {
        classNameE(className);
        String classId = name2idC.get(className);
        MyUmlClass myUmlClass = id2class.get(classId);
        return myUmlClass.getOperationVisibility(methodName); }

    public List<Integer> getClassOperationCouplingDegree(String className, String methodName)
            throws ClassNotFoundException, ClassDuplicatedException
            , MethodWrongTypeException, MethodDuplicatedException {
        classNameE(className);
        MyUmlClass myUmlClass = id2class.get(name2idC.get(className));
        if (class2methodWrongT.containsKey(className)
                && class2methodWrongT.get(className).contains(methodName)) {
            throw new MethodWrongTypeException(className, methodName);
        } else if (class2dupMethod.containsKey(className)
                && class2dupMethod.get(className).contains(methodName)) {
            throw new MethodDuplicatedException(className, methodName);
        }
        if (!myUmlClass.containsOpByname(methodName)) {
            return new ArrayList<>();
        }
        return myUmlClass.calCoupling(methodName); }

    public int getClassAttributeCouplingDegree(String className)
            throws ClassNotFoundException, ClassDuplicatedException {
        classNameE(className);
        MyUmlClass myUmlClass = id2class.get(name2idC.get(className));
        HashSet referenceType = new HashSet();
        return myUmlClass.calAttrCoupling(myUmlClass.getId(), referenceType); }

    public List<String> getClassImplementInterfaceList(String className)
            throws ClassNotFoundException, ClassDuplicatedException {
        classNameE(className);
        MyUmlClass myUmlClass = id2class.get(name2idC.get(className));
        List<String> interfaces = new ArrayList<>();
        myUmlClass.getInterfaceList(interfaces);
        HashSet<String> set = new HashSet<>();
        for (String a : interfaces) {
            set.add(a); }
        interfaces.clear();
        for (String a : set) {
            interfaces.add(a); }
        return interfaces; }

    public int getClassDepthOfInheritance(String className)
            throws ClassNotFoundException, ClassDuplicatedException {
        classNameE(className);
        int cnt = 0;
        MyUmlClass myUmlClass = id2class.get(name2idC.get(className));
        while (myUmlClass.getFatherClass() != null) {
            cnt++;
            myUmlClass = myUmlClass.getFatherClass(); }
        return cnt; }

    public static void init2(UmlElement... elements) {
        for (UmlElement e : elements) {
            switch (e.getElementType()) {
                case UML_ASSOCIATION:
                    UmlAssociation u = (UmlAssociation) e;
                    String id1 = u.getEnd1();
                    String id2 = u.getEnd2();
                    if (id2class.containsKey(id2associationEnd.get(id1).getReference())
                            && id2class.containsKey(id2associationEnd.get(id2).getReference())) {
                        MyUmlClass class1 = id2class.get(id2associationEnd.get(id1).getReference());
                        MyUmlClass class2 = id2class.get(id2associationEnd.get(id2).getReference());
                        class1.addAssociation(id2associationEnd.get(id2).getName());
                        class2.addAssociation(id2associationEnd.get(id1).getName());
                    } else if (id2class.containsKey(id2associationEnd.get(id1).getReference())
                           && id2interface.containsKey(id2associationEnd.get(id2).getReference())) {
                        MyUmlClass class1 = id2class.get(id2associationEnd.get(id1).getReference());
                        MyUmlInterface interface2 =
                                id2interface.get(id2associationEnd.get(id2).getReference());
                        class1.addAssociation(id2associationEnd.get(id2).getName());
                        interface2.addAssociation(class1);
                    } else if (id2class.containsKey(id2associationEnd.get(id2).getReference())
                           && id2interface.containsKey(id2associationEnd.get(id1).getReference())) {
                        MyUmlInterface interface1 =
                                id2interface.get(id2associationEnd.get(id1).getReference());
                        MyUmlClass umlClass2 =
                                id2class.get(id2associationEnd.get(id2).getReference());
                        interface1.addAssociation(umlClass2);
                        umlClass2.addAssociation(id2associationEnd.get(id1).getName());
                    } else if (id2interface.containsKey(id2associationEnd.get(id1).getReference())
                           && id2interface.containsKey(id2associationEnd.get(id2).getReference())) {
                        MyUmlInterface interface1 =
                                id2interface.get(id2associationEnd.get(id1).getReference());
                        MyUmlInterface interface2 =
                                id2interface.get(id2associationEnd.get(id2).getReference());
                        interface1.addAssociation(interface2);
                        interface2.addAssociation(interface1); }
                    break;
                case UML_ATTRIBUTE:
                    UmlAttribute a = (UmlAttribute) e;
                    String id = e.getParentId();
                    if (id2class.containsKey(id)) {
                        id2class.get(id).addAttribute(a);
                    } else if (id2interface.containsKey(id)) {
                        id2interface.get(id).addAttribute(a);
                        if (!a.getVisibility().equals(Visibility.PUBLIC)) {
                            isCheck5 = true; }
                    } else if (coll2attr.containsKey(id)) {
                        coll2attr.get(id).add(a.getId()); }
                    break;
                default:
                    break; } } }

    public static void init4(UmlElement... elements) {
        HashSet<String> parType = new HashSet<>(Arrays.asList("byte","short","int","long","float","double","char","boolean","String"));
        for (UmlElement e : elements) {
            switch (e.getElementType()) {
                case UML_PARAMETER:
                    UmlParameter upa = (UmlParameter) e;
                    String methodId = upa.getParentId();
                    UmlOperation tmp = id2operation.get(methodId).getUmlOperation();
                    String className = (id2class.get(tmp.getParentId())).getName();
                    String methodName = id2operation.get(methodId).getName();
                    if (upa.getDirection() == Direction.IN
                            && upa.getType() instanceof NamedType) {
                        NamedType type = (NamedType) upa.getType();
                        if (!parType.contains(type.getName())) {
                            if (class2methodWrongT.containsKey(className)) {
                                class2methodWrongT.get(className).add(methodName);
                            } else {
                                ArrayList<String> list = new ArrayList<>();
                                list.add(methodName);
                                class2methodWrongT.put(className, list); }
                            break;
                        } } else if (upa.getDirection() == Direction.RETURN
                            && upa.getType() instanceof NamedType) {
                        NamedType ty = (NamedType) upa.getType();
                        if (!(parType.contains(ty.getName()) || (ty.getName()).equals("void"))) {
                            if (class2methodWrongT.containsKey(className)) {
                                class2methodWrongT.get(className).add(methodName);
                            } else {
                                ArrayList<String> list = new ArrayList<>();
                                list.add(methodName);
                                class2methodWrongT.put(className, list); }
                            break; } }
                    id2operation.get(e.getParentId()).addParameter(upa);
                    break;
                default:
                    break; } } }

    public static void init5(UmlElement... elements) {
        for (UmlElement e : elements) {
            String id = e.getId();
            String name = e.getName();
            switch (e.getElementType()) {
                case UML_INTERACTION:
                    id2interaction.put(id, new MyInteraction((UmlInteraction) e));
                    inId2llCreator.put(id, new HashMap<>());
                    inId2dupCreated.put(id, new HashSet<>());
                    if (name2idOfInteraction.containsKey(name)) {
                        dupInteraction.add(name);
                    } else {
                        name2idOfInteraction.put(name, id); }
                    break;
                case UML_STATE_MACHINE:
                    id2stateMachine.put(id, new MyStateMachine((UmlStateMachine) e));
                    if (name2idOfStateMachine.containsKey(name)) {
                        dupStateMachine.add(name);
                    } else {
                        name2idOfStateMachine.put(name, id); }
                    break;
                case UML_REGION:
                    String parentId = e.getParentId();
                    region2idOfStateMachine.put(id, parentId);
                    break;
                default: } } }

    public static void init6(UmlElement... elements) {
        for (UmlElement e : elements) {
            String id = e.getId();
            String parentId = e.getParentId();
            String name = e.getName();
            switch (e.getElementType()) {
                case UML_LIFELINE:
                    MyInteraction interaction = id2interaction.get(parentId);
                    interaction.addLifeLine(new MyLifeLine((UmlLifeline) e));
                    llId2ll.put(id, new MyLifeLine((UmlLifeline) e));
                    String collId = interaction.getParentId();
                    HashSet attr = coll2attr.get(collId);
                    if (!attr.contains(((UmlLifeline) e).getRepresent())) {
                        isCheck6 = true; }
                    break;
                case UML_ENDPOINT:
                    id2endPoint.put(id, (UmlEndpoint) e);
                    break;
                case UML_STATE:
                case UML_PSEUDOSTATE:
                case UML_FINAL_STATE:
                    MyStateMachine sm = id2stateMachine.get(region2idOfStateMachine.get(parentId));
                    sm.addState(e);
                    stateId2name.put(id, name);
                    if (e.getElementType() == UML_FINAL_STATE) {
                        finalStateId.add(id); }
                    break;
                default:
                    break; } } }

    public static void init7(UmlElement... elements) {
        for (UmlElement e : elements) {
            switch (e.getElementType()) {
                case UML_MESSAGE:
                    String id;
                    String parentId = e.getParentId();
                    MyInteraction interaction = id2interaction.get(parentId);
                    UmlMessage msg = (UmlMessage) e;
                    String src = msg.getSource();
                    String tgt = msg.getTarget();
                    if (llId2ll.containsKey(tgt)) {
                        MyLifeLine ll = llId2ll.get(tgt);
                        if (ll.getDeleted()) {
                            isCheck7 = true;
                            break; }
                        if (msg.getMessageSort() == DELETE_MESSAGE && llId2ll.containsKey(tgt)) {
                            ll.setDeleted(); } }
                    if (msg.getMessageSort() == CREATE_MESSAGE) {
                        String tgtName = interaction.getLifeline(tgt).getName();
                        HashMap<String, UmlLifeline> ll2creator = inId2llCreator.get(parentId);
                        HashSet<String> dupCreated = inId2dupCreated.get(parentId);
                        if (ll2creator.containsKey(tgtName)) {
                            dupCreated.add(tgtName);
                        } else {
                            ll2creator.put(tgtName, interaction.getLifeline(src)); }
                    } else {
                        if (id2endPoint.containsKey(src) && !id2endPoint.containsKey(tgt)) {
                            interaction.addFoundMsg(tgt);
                        } else if (id2endPoint.containsKey(tgt) && !id2endPoint.containsKey(src)) {
                            interaction.addLostMsg(src); } }
                    break;
                case UML_TRANSITION:
                    id = e.getId();
                    parentId = e.getParentId();
                    UmlTransition tr = (UmlTransition) e;
                    MyStateMachine sm = id2stateMachine.get(region2idOfStateMachine.get(parentId));
                    sm.addTransition(tr);
                    id2trans.put(id, tr);
                    transId2event.put(id, new ArrayList<>());
                    if (finalStateId.contains(tr.getSource())) {
                        isCheck8 = true; }
                    break;
                case UML_EVENT:
                    transId2event.get(e.getParentId()).add((UmlEvent) e);
                    break;
                default:
                    break; } } }

    public void interactionNameE(String interactionName)
            throws InteractionNotFoundException, InteractionDuplicatedException {
        if (!name2idOfInteraction.containsKey(interactionName)) {
            throw new InteractionNotFoundException(interactionName);
        } else if (dupInteraction.contains(interactionName)) {
            throw new InteractionDuplicatedException(interactionName); } }

    public int getParticipantCount(String interactionName)
            throws InteractionNotFoundException, InteractionDuplicatedException {
        interactionNameE(interactionName);
        MyInteraction interaction = id2interaction.get(name2idOfInteraction.get(interactionName));
        return interaction.getParticipantCount(); }

    public UmlLifeline getParticipantCreator(String interactionName, String lifelineName)
            throws InteractionNotFoundException, InteractionDuplicatedException
            , LifelineNotFoundException, LifelineDuplicatedException, LifelineNeverCreatedException
            , LifelineCreatedRepeatedlyException {
        interactionNameE(interactionName);
        MyInteraction interaction = id2interaction.get(name2idOfInteraction.get(interactionName));
        if (!interaction.containsLifeline(lifelineName)) {
            throw new LifelineNotFoundException(interactionName, lifelineName);
        } else if (interaction.isDupLifeline(lifelineName)) {
            throw new LifelineDuplicatedException(interactionName, lifelineName);
        }
        HashMap<String, UmlLifeline> lifeLine2creator = inId2llCreator.get(interaction.getId());
        HashSet<String> dupCreated = inId2dupCreated.get(interaction.getId());
        if (!lifeLine2creator.containsKey(lifelineName)) {
            throw new LifelineNeverCreatedException(interactionName, lifelineName);
        } else if (dupCreated.contains(lifelineName)) {
            throw new LifelineCreatedRepeatedlyException(interactionName, lifelineName);
        }
        return lifeLine2creator.get(lifelineName); }

    public Pair<Integer, Integer> getParticipantLostAndFound(String interactionName
            , String lifelineName)
            throws InteractionNotFoundException, InteractionDuplicatedException
            , LifelineNotFoundException, LifelineDuplicatedException {
        interactionNameE(interactionName);
        MyInteraction interaction = id2interaction.get(name2idOfInteraction.get(interactionName));
        if (!interaction.containsLifeline(lifelineName)) {
            throw new LifelineNotFoundException(interactionName, lifelineName);
        } else if (interaction.isDupLifeline(lifelineName)) {
            throw new LifelineDuplicatedException(interactionName, lifelineName);
        }
        return interaction.getFoundAndLost(lifelineName); }

    public void stateNameE(String stateMachineName)
            throws StateMachineNotFoundException, StateMachineDuplicatedException {
        if (!name2idOfStateMachine.containsKey(stateMachineName)) {
            throw new StateMachineNotFoundException(stateMachineName);
        } else if (dupStateMachine.contains(stateMachineName)) {
            throw new StateMachineDuplicatedException(stateMachineName); } }

    public int getStateCount(String stateMachineName)
            throws StateMachineNotFoundException, StateMachineDuplicatedException {
        stateNameE(stateMachineName);
        MyStateMachine sm = id2stateMachine.get(name2idOfStateMachine.get(stateMachineName));
        return sm.getStateCount(); }

    public void snfAsdE(MyStateMachine sm, String stateName)
            throws StateNotFoundException, StateDuplicatedException {
        String stateMachineName = sm.getName();
        if (!sm.containState(stateName)) {
            throw new StateNotFoundException(stateMachineName, stateName);
        } else if (sm.isDupState(stateName)) {
            throw new StateDuplicatedException(stateMachineName, stateName); } }

    public boolean getStateIsCriticalPoint(String stateMachineName, String stateName)
            throws StateMachineNotFoundException, StateMachineDuplicatedException
            , StateNotFoundException, StateDuplicatedException {
        stateNameE(stateMachineName);
        MyStateMachine sm = id2stateMachine.get(name2idOfStateMachine.get(stateMachineName));
        snfAsdE(sm, stateName);
        return sm.isCriticalState(stateName); }

    public List<String> getTransitionTrigger(String smName, String srName, String tgName)
            throws StateMachineNotFoundException, StateMachineDuplicatedException
            , StateNotFoundException, StateDuplicatedException, TransitionNotFoundException {
        stateNameE(smName);
        MyStateMachine sm = id2stateMachine.get(name2idOfStateMachine.get(smName));
        snfAsdE(sm, srName);
        snfAsdE(sm, tgName);
        if (!sm.hasTrans(srName, tgName)) {
            throw new TransitionNotFoundException(smName, srName, tgName); }
        ArrayList<String> list = new ArrayList<>();
        for (String transId : transId2event.keySet()) {
            UmlTransition transition = id2trans.get(transId);
            String srcName = stateId2name.get(transition.getSource());
            String tgtName = stateId2name.get(transition.getTarget());
            if (region2idOfStateMachine.get(transition.getParentId()).equals(sm.getId())
                    && srcName != null && tgtName != null && srcName.equals(srName)
                    && tgtName.equals(tgName)
                    && !(srcName.matches("[ \t]*")) && !(tgtName.matches("[ \t]*"))) {
                ArrayList<UmlEvent> events = transId2event.get(transId);
                for (UmlEvent event : events) {
                    list.add(event.getName());
                } } }
        return list; }

    public void checkForUml001() throws UmlRule001Exception {
        if (flag1) { throw new UmlRule001Exception(); } }

    public void checkForUml002() throws UmlRule002Exception {
        if (pairs.size() != 0) { throw new UmlRule002Exception(pairs); } }

    public void checkForUml003() throws UmlRule003Exception {
        cl2ins = myCheck.checkFor003(cl2ins);
        if (cl2ins.size() != 0) {
            throw new UmlRule003Exception((Set<? extends UmlClassOrInterface>)cl2ins); } }

    public void checkForUml004() throws UmlRule004Exception {
        for (MyUmlInterface i : id2interface.values()) {
            if (!i.getFathers(new ArrayList<>())) {
                ans004.add(i.getUmlInterface()); } }
        if (ans004.size() > 0) {
            throw new UmlRule004Exception(ans004); } }

    public void checkForUml005() throws UmlRule005Exception {
        if (isCheck5) { throw new UmlRule005Exception(); } }

    public void checkForUml006() throws UmlRule006Exception {
        if (isCheck6) { throw new UmlRule006Exception(); } }

    public void checkForUml007() throws UmlRule007Exception {
        if (isCheck7) { throw new UmlRule007Exception(); } }

    public void checkForUml008() throws UmlRule008Exception {
        if (isCheck8) { throw new UmlRule008Exception(); } }

    public void checkForUml009() throws UmlRule009Exception {
        if (myCheck.checkFor009()) { throw new UmlRule009Exception(); } }
}


