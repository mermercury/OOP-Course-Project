package com.oocourse.spec3.commit;

import com.oocourse.spec3.main.Message;
import com.oocourse.spec3.main.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class MyPerson implements Person {
    private int id;
    private String name;
    private int age;
    private HashMap<Integer, Integer> acquaintance;    // personId-value
    private int blockNum;
    private int money;
    private int socialValue;
    private ArrayList<Message> messages;

    public HashMap<Integer, Integer> getAcquaintance() {
        return acquaintance;
    }

    public int getBlockNum() {
        return blockNum;
    }

    public void setBlockNum(int blockNum) {
        this.blockNum = blockNum;
    }

    public MyPerson(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.acquaintance = new HashMap<>();
        this.money = 0;
        this.socialValue = 0;
        this.messages = new ArrayList<>();
    }

    public void addSocialValue(int num) {
        socialValue += num;
    }

    public int getSocialValue() {
        return socialValue;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public List<Message> getReceivedMessages() {
        ArrayList<Message> receivedMsg = new ArrayList<>();
        int cnt = 0;
        int size = messages.size() - 1;
        for (int i = size; i >= 0 && cnt < 4; i--,cnt++) {
            receivedMsg.add(messages.get(i));
        }
        return receivedMsg;
    }

    public void addMoney(int num) {
        money += num;
    }

    public int getMoney() {
        return money;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getAge() {
        return this.age;
    }

    @Override
    public boolean isLinked(Person person) {
        if (person.getId() == this.id) {
            return true;
        }
        if (acquaintance.containsKey(person.getId())) {
            return true;
        }
        return false;
    }

    @Override
    public int queryValue(Person person) {
        int res = 0;
        if (acquaintance.containsKey(person.getId())) {
            return acquaintance.get(person.getId());
        }
        return res;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Person) {
            return (((Person) obj).getId() == id);
        } else {
            return false;
        }
    }

    @Override
    public int compareTo(Person p2) {
        return name.compareTo(p2.getName());
    }

    public void addRelation(Person p, int value) {
        acquaintance.put(p.getId(), value);
        return;
    }

    public void setSocialValue(int socialValue) {
        this.socialValue = socialValue;
    }

    public void receiveMsg(Message msg) {
        messages.add(msg);
    }
}