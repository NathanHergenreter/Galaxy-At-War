package com.server.demo.Model;

import com.server.demo.Entities.User;

import java.util.List;

public class FriendsManager {

    public List<User> getFriendsList(long userId){

        //TODO get all friends of userId

        return null;
    }

    public boolean addFriend(long userId, long friendId){
        //TODO add friend pair to friendList

        return true;
    }

    public boolean removeFriend(long userId, long friendId){
        //TODO remove friend pair from friendList

        return true;
    }

}
