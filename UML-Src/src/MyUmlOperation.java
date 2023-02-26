package com.oocourse.uml3.models.elements;

import com.oocourse.uml3.models.common.Direction;
import com.oocourse.uml3.models.common.ReferenceType;
import com.oocourse.uml3.models.common.Visibility;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class MyUmlOperation {
    private UmlOperation umlOperation;
    private ArrayList<UmlParameter> parameters = new ArrayList<>();

    public UmlOperation getUmlOperation() {
        return umlOperation;
    }

    public MyUmlOperation(UmlOperation umlOperation) {
        this.umlOperation = umlOperation;
    }

    public ArrayList<UmlParameter> getUmlParameters() {
        return parameters;
    }

    public void addParameter(UmlParameter umlParameter) {
        parameters.add(umlParameter);
    }

    public String getName() {
        return umlOperation.getName();
    }

    public Visibility getVisibility() {
        return umlOperation.getVisibility();
    }

    public boolean hasDupParas(MyUmlOperation op) {
        ArrayList<UmlParameter> paras = (ArrayList<UmlParameter>) op.getUmlParameters().clone();
        ArrayList<UmlParameter> myParas = (ArrayList<UmlParameter>) parameters.clone();
        Iterator it = paras.iterator();
        Iterator myIt = myParas.iterator();
        UmlParameter para;
        UmlParameter para1;
        while (it.hasNext()) {
            para = (UmlParameter) it.next();
            if (para.getDirection() == Direction.RETURN) {
                it.remove();
            }
        }
        while (myIt.hasNext()) {
            para = (UmlParameter) myIt.next();
            if (para.getDirection() == Direction.RETURN) {
                myIt.remove();
            }
        }
        if (paras.size() != myParas.size()) {
            return false;
        }
        it = paras.iterator();
        while (it.hasNext()) {
            para = (UmlParameter) it.next();
            myIt = myParas.iterator();
            while (myIt.hasNext()) {
                para1 = (UmlParameter) myIt.next();
                if (para.getType().equals(para1.getType())) {
                    it.remove();
                    myIt.remove();
                    break;
                }
            }
        }
        if (paras.size() == 0 && myParas.size() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public int calMCoupling(String classId) {
        HashSet set = new HashSet();
        for (UmlParameter para : parameters) {
            if (para.getType() instanceof ReferenceType) {
                if (!((((ReferenceType) para.getType()).getReferenceId()).equals(classId))) {
                    set.add(((ReferenceType) para.getType()).getReferenceId());
                }
            }
        }
        return set.size();
    }

    public String getParentId() {
        return umlOperation.getParentId();
    }
}
