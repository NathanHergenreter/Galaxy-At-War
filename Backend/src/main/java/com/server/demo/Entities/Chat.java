package com.server.demo.Entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

// http://qnimate.com/database-design-for-storing-chat-messages/
@Entity
@Table(name = "chat")
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
    
    @Column(name="group_id")
    private String groupId;
    
    @Column
    private String message;

    @Column
    private String senderName;

    //Default Constructor
    protected Chat(){}
    
    //Group ID
    public String getGroupId(){ return this.groupId; }
    public void setGroupId(String groupId){ this.groupId=groupId; }

    //Message
    public String getMessage(){ return this.message; }
    public void setMessage(String message) { this.message = message; }

    //Sender Name
    public String getSenderName() { return this.senderName; }
    public void setSenderName(String senderName){ this.senderName = senderName; }
}
