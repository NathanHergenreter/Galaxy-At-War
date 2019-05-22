package com.server.demo.Model;

import java.util.List;

public class MessageManager {

    public String getIdFromNames(List<String> users){

        //Alphabetize users and create a colon seperated list
        //IE: [b,a,c] becomes a:b:c

        return null;
    }

    public boolean addMessage(String id, String message){

        //TODO create a new conversation if none exists for this group

        //TODO save message
        //Note: id should be a colon seperated list of usernames in alphabetical order
        //This function also adds the message number to the id before creating the Chat object

        return true;
    }



    public boolean deleteMessage(String id, int messageNumber){

        id = id+":"+messageNumber;

        //TODO search for and delete message
        //Make sure to reduce total messages number for that conversation


        return true;
    }

}
