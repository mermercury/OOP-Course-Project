package com.oocourse.uml3.models.elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class MyStateMachine {
    private UmlStateMachine umlStateMachine;
    private HashMap<String, MyState> id2state;
    private HashMap<String, List<String>> transSrcName2Tgt;
    private HashMap<String, String> name2idOfState;
    private HashSet<String> dupStateName;
    // <起始状态，路径集合>  <起始状态，终止状态集合>
    private UmlPseudostate pseudostate;
    private ArrayList<UmlFinalState> finalStates;
    private static List<List<String>> paths;
    private HashSet<String> nameOfCriticalState;
    private HashSet<String> finalStatesId;
    private List<List<String>> final2critical = new ArrayList<>();

    public MyStateMachine(UmlStateMachine umlStateMachine) {
        this.umlStateMachine = umlStateMachine;
        id2state = new HashMap<>();
        name2idOfState = new HashMap<>();
        transSrcName2Tgt = new HashMap<>();
        dupStateName = new HashSet<>();
        nameOfCriticalState = new HashSet<>();
        finalStates = new ArrayList<>();
        paths = new ArrayList<>();
        finalStatesId = new HashSet<>();
    }

    public void findCriticalState() {
        if (finalStates.size() == 0) {
            return;
        }
        List<String> chain = new ArrayList();
        for (int i = 0; i < finalStates.size(); i++) {
            final2critical.add(new ArrayList<>());
            dfs(pseudostate.getId(), finalStates.get(i).getId(), chain, paths);
            if (paths.size() == 0) { continue; }
            List<String> list = paths.get(0);
            int j = 0;
            for (int k = 0; k < list.size(); k++) {
                String id = list.get(k);
                for (j = 1; j < paths.size(); j++) {
                    if (!paths.get(j).contains(id)) {
                        break;
                    }
                }
                if (j == paths.size() && !finalStatesId.contains(id)) {
                    final2critical.get(i).add(id);
                }
            }
            chain.clear();
            paths.clear();
        }
        int k;
        for (int i = 0; i < finalStates.size(); i++) {
            List<String> list = final2critical.get(0);
            for (int j = 0; j < list.size(); j++) {
                String id = list.get(j);
                if (finalStates.size() > 1) {
                    for (k = 1; k < finalStates.size(); k++) {
                        if (!final2critical.get(k).contains(id)
                                && final2critical.get(k).size() != 0) {
                            break;
                        }
                    }
                    if (k == finalStates.size()) {
                        nameOfCriticalState.add(id2state.get(id).getName());
                    }
                } else {
                    nameOfCriticalState.add(id2state.get(id).getName());
                }
            }
        }
    }

    public String getName() {
        return umlStateMachine.getName();
    }

    public void dfs(String curId, String finalId, List<String> chain, List<List<String>> res) {
        MyState curState = id2state.get(curId);
        if (curState.getName() == null && !curId.equals(pseudostate.getId())) {
            if (curId.equals(finalId)) {
                res.add(new ArrayList<>(chain));
            }
            return;
        }

        for (String linkedId : curState.getLinkedStateId()) {
            if (chain.contains(linkedId)) { continue; }
            chain.add(linkedId);
            if (!linkedId.equals(curId)) {
                dfs(linkedId, finalId, chain, res);
            }
            chain.remove(chain.size() - 1);
        }
    }

    public void addState(UmlElement state) {
        if (state instanceof UmlPseudostate) {
            pseudostate = (UmlPseudostate) state;
        } else if (state instanceof UmlFinalState) {
            finalStates.add((UmlFinalState) state);
            finalStatesId.add(state.getId());
        }
        String name = state.getName();
        String id = state.getId();
        id2state.put(id, new MyState(state));
        if (name2idOfState.containsKey(name)) {
            dupStateName.add(name);
        } else {
            name2idOfState.put(name, id);
        }
    }

    public void addTransition(UmlTransition transition) {
        String srcId = transition.getSource();
        String tgtId = transition.getTarget();
        MyState src = id2state.get(srcId);
        MyState tgt = id2state.get(tgtId);
        src.addLinkedState(tgtId);
        if (src.getName() != null && tgt.getName() != null) {
            if (transSrcName2Tgt.containsKey(src.getName())) {
                transSrcName2Tgt.get(src.getName()).add(tgt.getName());
            } else {
                transSrcName2Tgt.put(src.getName(), new ArrayList<>());
                transSrcName2Tgt.get(src.getName()).add(tgt.getName());
            }
        }
    }

    public String getId() {
        return umlStateMachine.getId();
    }

    public boolean hasTrans(String sourceStateName, String targetStateName) {
        for (String src : transSrcName2Tgt.keySet()) {
            if (src != null) {
                if (src.equals(sourceStateName)
                        && (transSrcName2Tgt.get(src)).contains(targetStateName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getStateCount() {
        return id2state.size();
    }

    public boolean containState(String stateName) {
        if (name2idOfState.containsKey(stateName)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isDupState(String stateName) {
        if (dupStateName.contains(stateName)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isCriticalState(String name) {
        if (nameOfCriticalState.contains(name)) {
            return true;
        } else {
            return false;
        }
    }

}
