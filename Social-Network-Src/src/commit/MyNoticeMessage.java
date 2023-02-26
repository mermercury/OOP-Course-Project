package com.oocourse.spec3.commit;

import com.oocourse.spec3.main.Group;
import com.oocourse.spec3.main.NoticeMessage;
import com.oocourse.spec3.main.Person;

public class MyNoticeMessage extends MyMessage implements NoticeMessage {
    private String string;

    /*@ ensures type == 0;
      @ ensures group == null;
      @ ensures id == messageId;
      @ ensures person1 == messagePerson1;
      @ ensures person2 == messagePerson2;
      @ ensures string == noticeString;
      @*/
    public MyNoticeMessage(int messageId, String noticeString
            , Person messagePerson1, Person messagePerson2) {
        super(messageId, noticeString.length()
                , messagePerson1, messagePerson2);
        string = noticeString;
    }

    /*@ ensures type == 1;
      @ ensures person2 == null;
      @ ensures id == messageId;
      @ ensures person1 == messagePerson1;
      @ ensures group == messageGroup;
      @ ensures string == noticeString;
      @*/
    public MyNoticeMessage(int messageId, String noticeString
            , Person messagePerson1, Group messageGroup) {
        super(messageId, noticeString.length()
                , messagePerson1, messageGroup);
        string = noticeString;
    }

    //@ ensures \result == string;
    public /*@ pure @*/ String getString() {
        return string;
    }

}
