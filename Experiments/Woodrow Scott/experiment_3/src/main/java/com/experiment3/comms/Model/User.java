package com.experiment3.comms.Model;

import javax.persistence.*;

@Entity
@Table(name="user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private long id;
    @Column(name="name", nullable = false)
    private String userName;
    @Column(name="admin", nullable = false)
    private boolean admin;
    @Column (name="email")
    private String email;

    public Long getId(){
        return id;
    }

    public String getUserName(){
        return userName;
    }

    public Boolean getAdmin(){
        return admin;
    }

    public void setEmail(String mail){
        email = mail;
    }

    public String getEmail(){
        return email;
    }

    protected User(){}

    public User(String name, String mail){
        userName = name;
        email=mail;
    }

    @Override
    public String toString(){
        return String.format("%d : %s : %s : %b", id, userName, email, admin);
    }

}
