package com.oocourse.spec3.commit;

import com.oocourse.spec3.main.Group;
import com.oocourse.spec3.main.Person;

import java.util.HashMap;

public class MyGroup implements Group {
    private int id;
    private HashMap<Integer, MyPerson> people;
    private int valueSum;
    private int ageSum;

    public MyGroup(int id) {
        this.id = id;
        this.people = new HashMap();
        this.valueSum = 0;
        this.ageSum = 0;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public void addPerson(Person person) {
        people.put(person.getId(), (MyPerson) person);
        ageSum += person.getAge();
        for (MyPerson p : people.values()) {
            // renew valueSum
            if (p.isLinked(person)) {
                valueSum += (p.queryValue(person)) * 2;
            }
        }
    }

    @Override
    public boolean hasPerson(Person person) {
        if (people == null) {
            return false;
        }
        if (people.containsKey(person.getId())) {
            return true;
        }
        return false;
    }

    @Override
    public int getValueSum() {
        return this.valueSum;
    }

    @Override
    public int getAgeMean() {
        if (getSize() == 0) {
            return 0;
        }
        return ageSum / getSize();
    }

    @Override
    public int getAgeVar() {
        int ageVar = 0;
        int size = getSize();
        if (size == 0) {
            return 0;
        }
        for (MyPerson p : people.values()) {
            ageVar += (p.getAge() - getAgeMean()) * (p.getAge() - getAgeMean());
        }
        return ageVar / size;
    }

    @Override
    public void delPerson(Person person) {
        people.remove(person.getId());
        ageSum -= person.getAge();
        for (MyPerson p : people.values()) {
            if (p.isLinked(person)) {
                valueSum -= (p.queryValue(person)) * 2;
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Group) {
            return (((Group) obj).getId() == id);
        } else {
            return false;
        }
    }

    @Override
    public int getSize() {
        return people.size();
    }

    public HashMap<Integer, MyPerson> getPeople() {
        return people;
    }

    public void addValueSum(int add) {
        valueSum += add;
    }
}
