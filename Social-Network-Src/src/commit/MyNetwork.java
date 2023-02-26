package com.oocourse.spec3.commit;

import com.oocourse.spec3.exceptions.EqualGroupIdException;
import com.oocourse.spec3.exceptions.EqualMessageIdException;
import com.oocourse.spec3.exceptions.EqualPersonIdException;
import com.oocourse.spec3.exceptions.EqualRelationException;
import com.oocourse.spec3.exceptions.GroupIdNotFoundException;
import com.oocourse.spec3.exceptions.MessageIdNotFoundException;
import com.oocourse.spec3.exceptions.PersonIdNotFoundException;
import com.oocourse.spec3.exceptions.RelationNotFoundException;
import com.oocourse.spec3.exceptions.EmojiIdNotFoundException;
import com.oocourse.spec3.exceptions.EqualEmojiIdException;

import com.oocourse.spec3.main.Group;
import com.oocourse.spec3.main.Message;
import com.oocourse.spec3.main.EmojiMessage;
import com.oocourse.spec3.main.NoticeMessage;
import com.oocourse.spec3.main.RedEnvelopeMessage;
import com.oocourse.spec3.main.Network;
import com.oocourse.spec3.main.Person;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Map;
import java.util.Iterator;
import java.util.HashSet;

public class MyNetwork implements Network {
    private HashMap<Integer, Group> groups;
    private HashMap<Integer, Message> messages;
    private HashMap<Integer, MyPerson> people; // personId, person
    private HashMap<Integer, ArrayList<Integer>> blocks;
    private HashMap<Integer, int[]> edges;
    private int curBlock;
    private int numOfBlock;
    private HashMap<Integer, Integer> emojiId2Heat;

    public MyNetwork() {
        this.groups = new HashMap<>();
        this.people = new HashMap<>();
        this.blocks = new HashMap<>();
        this.curBlock = 0;
        this.numOfBlock = 0;
        this.messages = new HashMap<>();
        this.emojiId2Heat = new HashMap<>();
    }

    public boolean containsEmojiId(int id) {
        if (emojiId2Heat.containsKey(id)) {
            return true;
        } else {
            return false;
        }
    }

    public int krus(int n, ArrayList<Integer> nodes) {
        Dsu dsu = new Dsu(n);
        PriorityQueue<int[]> heap = new PriorityQueue<>((a,b) -> a[2] - b[2]);

        for (int i = 0; i < n; i++) {
            MyPerson p1 = (MyPerson) getPerson(nodes.get(i));
            for (int j = i + 1; j < n; j++) {
                MyPerson p2 = (MyPerson) getPerson(nodes.get(j));
                if (p1.isLinked(p2)) {
                    heap.offer(new int[]{i, j, p1.queryValue(p2)});
                }
            }
        }

        int res = 0;
        while (!heap.isEmpty()) {
            int[] cur = heap.poll();
            if (dsu.union(cur[0], cur[1])) {
                res += cur[2];
            }
        }
        return res;
    }

    public int queryLeastConnection(int id) throws PersonIdNotFoundException {
        if (!contains(id)) {
            throw new MyPersonIdNotFoundException(id);
        } else {
            int blockNum = ((MyPerson)getPerson(id)).getBlockNum();
            ArrayList nodes = blocks.get(blockNum);
            int n = nodes.size();

            return krus(n, nodes);
        }
    }

    public int queryGroupPeopleSum(int id) throws GroupIdNotFoundException {
        if (!groups.containsKey(id)) {
            throw new MyGroupIdNotFoundException(id);
        } else {
            return groups.get(id).getSize();
        }
    }

    public int queryGroupValueSum(int id) throws GroupIdNotFoundException {
        if (!groups.containsKey(id)) {
            throw new MyGroupIdNotFoundException(id);
        } else {
            return groups.get(id).getValueSum();
        }
    }

    public int queryGroupAgeVar(int id) throws GroupIdNotFoundException {
        if (!groups.containsKey(id)) {
            throw new MyGroupIdNotFoundException(id);
        } else {
            return groups.get(id).getAgeVar();
        }
    }

    public boolean containsMessage(int id) {
        if (messages.containsKey(id)) {
            return true;
        } else {
            return false;
        }
    }

    public int querySocialValue(int id) throws PersonIdNotFoundException {
        if (!contains(id)) {
            throw new MyPersonIdNotFoundException(id);
        } else {
            return getPerson(id).getSocialValue();
        }
    }

    public List<Message> queryReceivedMessages(int id) throws PersonIdNotFoundException {
        if (!contains(id)) {
            throw new MyPersonIdNotFoundException(id);
        } else {
            return getPerson(id).getReceivedMessages();
        }
    }

    public void storeEmojiId(int id) throws EqualEmojiIdException {
        if (emojiId2Heat.containsKey(id)) {
            throw new MyEqualEmojiIdException(id);
        } else {
            emojiId2Heat.put(id, 0);
        }
    }

    public int queryMoney(int id) throws PersonIdNotFoundException {
        if (!contains(id)) {
            throw new MyPersonIdNotFoundException(id);
        } else {
            return getPerson(id).getMoney();
        }
    }

    public int queryPopularity(int id) throws EmojiIdNotFoundException {
        if (!emojiId2Heat.containsKey(id)) {
            throw new MyEmojiIdNotFoundException(id);
        } else {
            return emojiId2Heat.get(id);
        }
    }

    public int deleteColdEmoji(int limit) {
        Iterator<Map.Entry<Integer, Integer>> it = emojiId2Heat.entrySet().iterator();
        while (it.hasNext()) {
            int heat = it.next().getValue();
            if (heat < limit) {
                it.remove();
            }
        }
        Iterator<Map.Entry<Integer, Message>> it2 = messages.entrySet().iterator();
        while (it2.hasNext()) {
            Message msg = it2.next().getValue();
            if (msg instanceof EmojiMessage &&
                    !emojiId2Heat.containsKey(((EmojiMessage)msg).getEmojiId())) {
                it2.remove();
            }
        }
        return emojiId2Heat.size();
    }

    public void clearNotices(int personId) throws PersonIdNotFoundException {
        if (!contains(personId)) {
            throw new MyPersonIdNotFoundException(personId);
        } else {
            Iterator it = (getPerson(personId).getMessages()).iterator();
            while (it.hasNext()) {
                Message msg = (Message) it.next();
                if (msg instanceof NoticeMessage) {
                    it.remove();
                }
            }
        }
    }

    private int dijs(Person p1, Person p2) {
        PriorityQueue<Edge> queue = new PriorityQueue<>();
        // 记录每个点到p1的dis
        HashMap<Integer, Integer> id2dis = new HashMap<>();
        HashSet<Integer> visitedId = new HashSet<>();
        queue.add(new Edge(p1.getId(), 0));
        while (!queue.isEmpty()) {
            Edge edge = queue.poll();
            if (edge.getId() == p2.getId()) {
                // dis: 从p1->p2的最短边权和
                return edge.getDis();
            }
            if (visitedId.contains(edge.getId())) {
                continue;
            }
            visitedId.add(edge.getId());
            int base = edge.getDis();
            MyPerson p = ((MyPerson) getPerson(edge.getId()));
            p.getAcquaintance().forEach((linkedId, weight) -> {
                // 以base(最小值) 更新 新增结点的邻接结点的dis
                int key = linkedId;
                int value = weight + base;
                if (!visitedId.contains(key)
                        && (!id2dis.containsKey(key) || id2dis.get(key) > value)) {
                    id2dis.put(key, value);
                    queue.add(new Edge(key, value));
                }
            });
        }
        return -1;
    }

    public int sendIndirectMessage(int id) throws
            MessageIdNotFoundException {
        if (!containsMessage(id) ||
                (containsMessage(id) && getMessage(id).getType() == 1)) {
            throw new MyMessageIdNotFoundException(id);
        } else {
            try {
                if (!isCircle(getMessage(id).getPerson1().getId()
                        , getMessage(id).getPerson2().getId())) {
                    return -1;
                }
                else {
                    Message msg = getMessage(id);
                    MyPerson p1 = (MyPerson) msg.getPerson1();
                    MyPerson p2 = (MyPerson) msg.getPerson2();
                    p1.setSocialValue(p1.getSocialValue() + msg.getSocialValue());
                    p2.setSocialValue(p2.getSocialValue() + msg.getSocialValue());
                    messages.remove(id);
                    if (msg instanceof RedEnvelopeMessage) {
                        p1.addMoney(-1 * ((RedEnvelopeMessage)msg).getMoney());
                        p2.addMoney(((RedEnvelopeMessage)msg).getMoney());
                    } else if (msg instanceof EmojiMessage) {
                        int oldHeat = emojiId2Heat.get(((EmojiMessage)msg).getEmojiId());
                        emojiId2Heat.replace(((EmojiMessage)msg).getEmojiId(), oldHeat + 1);
                    }
                    p2.receiveMsg(msg);
                    return dijs(p1, p2);
                }
            } catch (PersonIdNotFoundException e) {
                e.printStackTrace();
            }
        }
        return id;
    }

    public Message getMessage(int id) {
        if (!containsMessage(id)) {
            return null;
        } else {
            return messages.get(id);
        }
    }

    public void addMessage(Message message) throws
            EqualMessageIdException, EqualPersonIdException, EmojiIdNotFoundException {
        if (containsMessage(message.getId())) {
            throw new MyEqualMessageIdException(message.getId());
        } else if ((message instanceof EmojiMessage)
                && !containsEmojiId(((EmojiMessage) message).getEmojiId())) {
            throw new MyEmojiIdNotFoundException(((MyEmojiMessage)message).getEmojiId());
        } else if (message.getType() == 0 && message.getPerson1() == message.getPerson2()
                && (!(message instanceof EmojiMessage) | ((message instanceof EmojiMessage)
                && containsEmojiId(((EmojiMessage) message).getEmojiId())))) {
            throw new MyEqualPersonIdException(message.getPerson1().getId());
        } else {
            messages.put(message.getId(), message);
        }
    }

    @Override
    public boolean contains(int id) {
        if (people.get(id) != null) {
            return true;
        }
        return false;
    }

    @Override
    public Person getPerson(int id) {
        if (contains(id)) {
            return people.get(id);
        }
        return null;
    }

    @Override
    public void addPerson(Person person) throws EqualPersonIdException {
        if (people.containsKey(person.getId())) {
            throw new MyEqualPersonIdException(person.getId());
        } else {
            people.put(person.getId(), (MyPerson)person);
            curBlock++;
            numOfBlock++;
            ((MyPerson)person).setBlockNum(curBlock);
            ArrayList<Integer> peopleInBlock = new ArrayList<>();
            peopleInBlock.add(person.getId());
            blocks.put(curBlock, peopleInBlock);
        }
    }

    public void sendMessage(int id) throws
            RelationNotFoundException, MessageIdNotFoundException, PersonIdNotFoundException {
        if (!containsMessage(id)) {
            throw new MyMessageIdNotFoundException(id);
        } else if (getMessage(id).getType() == 0
                && !(getMessage(id).getPerson1().isLinked(getMessage(id).getPerson2()))) {
            Message msg = getMessage(id);
            throw new MyRelationNotFoundException(msg.getPerson1().getId()
                    , msg.getPerson2().getId());
        } else if (getMessage(id).getType() == 1
                && !(getMessage(id).getGroup().hasPerson(getMessage(id).getPerson1()))) {
            throw new MyPersonIdNotFoundException(getMessage(id).getPerson1().getId());
        } else if (getMessage(id).getType() == 0 &&
                  getMessage(id).getPerson1().isLinked(getMessage(id).getPerson2()) &&
                  getMessage(id).getPerson1() != getMessage(id).getPerson2()) {
            Message msg = getMessage(id);
            MyPerson p1 = (MyPerson) msg.getPerson1();
            MyPerson p2 = (MyPerson) msg.getPerson2();
            p1.setSocialValue(p1.getSocialValue() + msg.getSocialValue());
            p2.setSocialValue(p2.getSocialValue() + msg.getSocialValue());
            messages.remove(id);
            p2.receiveMsg(msg);
            if (msg instanceof RedEnvelopeMessage) {
                p1.addMoney(-1 * ((RedEnvelopeMessage)msg).getMoney());
                p2.addMoney(((RedEnvelopeMessage)msg).getMoney());
            } else if (msg instanceof EmojiMessage) {
                int oldHeat = emojiId2Heat.get(((EmojiMessage)msg).getEmojiId());
                emojiId2Heat.replace(((EmojiMessage)msg).getEmojiId(), oldHeat + 1);
            }
        } else if (containsMessage(id) && getMessage(id).getType() == 1 &&
                getMessage(id).getGroup().hasPerson(getMessage(id).getPerson1())) {
            Message msg = getMessage(id);
            MyPerson p1 = (MyPerson) msg.getPerson1();
            MyGroup group = (MyGroup) msg.getGroup();
            for (MyPerson p : group.getPeople().values()) {
                p.setSocialValue(p.getSocialValue() + msg.getSocialValue());
            }
            if (msg instanceof RedEnvelopeMessage) {
                int i = (((RedEnvelopeMessage) msg).getMoney()) / msg.getGroup().getSize();
                p1.addMoney(-1 * i * (msg.getGroup().getSize() - 1));
                for (MyPerson p : group.getPeople().values()) {
                    if (p != p1) {
                        p.addMoney(i);
                    }
                }
            } else if (msg instanceof EmojiMessage) {
                int oldHeat = emojiId2Heat.get(((EmojiMessage)msg).getEmojiId());
                emojiId2Heat.replace(((EmojiMessage)msg).getEmojiId(), oldHeat + 1);
            }
            messages.remove(id);
        }
    }

    @Override
    public void addRelation(int id1, int id2, int value)
            throws PersonIdNotFoundException, EqualRelationException {
        MyPerson person1 = people.get(id1);
        MyPerson person2 = people.get(id2);
        if (person1 != null && person2 != null && !getPerson(id1).isLinked(getPerson(id2))) {
            person1.addRelation(person2, value);
            person2.addRelation(person1, value);
            int newId = person2.getBlockNum();
            if (person1.getBlockNum() != newId) {
                for (int id : blocks.get(person1.getBlockNum())) {
                    ((MyPerson) getPerson(id)).setBlockNum(newId);
                    blocks.get(newId).add(id);
                }
                numOfBlock--;
            }
            for (Group group : groups.values()) {
                if (group.hasPerson(person1) && group.hasPerson(person2)) {
                    ((MyGroup)group).addValueSum(value * 2);
                }
            }
        } else if (!contains(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (contains(id1) && !contains(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        } else if (contains(id1) && contains(id2) && getPerson(id1).isLinked(getPerson(id2))) {
            throw new MyEqualRelationException(id1, id2);
        }

    }

    @Override
    public int queryValue(int id1, int id2)
            throws PersonIdNotFoundException, RelationNotFoundException {
        if (contains(id1) && contains(id2) && getPerson(id1).isLinked(getPerson(id2))) {
            return getPerson(id1).queryValue(getPerson(id2));
        } else if (!contains(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (contains(id1) && !contains(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        } else if (contains(id1) && contains(id2) && !getPerson(id1).isLinked(getPerson(id2))) {
            throw new MyRelationNotFoundException(id1, id2);
        }
        return 0;
    }

    @Override
    public int queryPeopleSum() {
        return people.size();
    }

    @Override
    public boolean isCircle(int id1, int id2) throws PersonIdNotFoundException {
        if (!contains(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (contains(id1) && !contains(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        }
        return (((MyPerson) getPerson(id1)).getBlockNum()
                == ((MyPerson) getPerson(id2)).getBlockNum());
    }

    @Override
    public int queryBlockSum() {
        return numOfBlock;
    }

    @Override
    public void addGroup(Group group) throws EqualGroupIdException {
        if (groups.containsKey(group.getId())) {
            throw new MyEqualGroupIdException(group.getId());
        } else {
            groups.put(group.getId(), group);
        }
    }

    @Override
    public Group getGroup(int id) {
        if (groups.containsKey(id)) {
            return groups.get(id);
        }
        return null;
    }

    @Override
    public void addToGroup(int id1, int id2)
            throws GroupIdNotFoundException, PersonIdNotFoundException, EqualPersonIdException {
        if (groups.containsKey(id2) && people.containsKey(id1)
                && !groups.get(id2).hasPerson(getPerson(id1)) && getGroup(id2).getSize() < 1111) {
            groups.get(id2).addPerson(getPerson(id1));
        } else if (groups.containsKey(id2) && people.containsKey(id1)
                && !groups.get(id2).hasPerson(getPerson(id1)) && getGroup(id2).getSize() >= 1111) {
            // do nothing
        } else if (!groups.containsKey(id2)) {
            throw new MyGroupIdNotFoundException(id2);
        } else if (groups.containsKey(id2) && !people.containsKey(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (groups.containsKey(id2) && people.containsKey(id1)
                && groups.get(id2).hasPerson(getPerson(id1))) {
            throw new MyEqualPersonIdException(id1);
        }
    }

    @Override
    public void delFromGroup(int id1, int id2)
            throws GroupIdNotFoundException, PersonIdNotFoundException, EqualPersonIdException {
        if (groups.containsKey(id2) && people.containsKey(id1)
                && groups.get(id2).hasPerson(getPerson(id1))) {
            groups.get(id2).delPerson(getPerson(id1));
        } else if (!groups.containsKey(id2)) {
            throw new MyGroupIdNotFoundException(id2);
        } else if (groups.containsKey(id2) && !people.containsKey(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (groups.containsKey(id2) && people.containsKey(id1)
                && !groups.get(id2).hasPerson(getPerson(id1))) {
            throw new MyEqualPersonIdException(id1);
        }
    }

}
