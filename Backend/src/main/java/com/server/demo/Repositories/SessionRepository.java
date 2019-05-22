package com.server.demo.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.server.demo.Entities.Session;
import com.server.demo.Entities.User;

import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {
	
	//Returns game session with given name
    Session findByName(String name);

    Session findById(String id);
    
    Session findByHostAndName(User host, String name);

    //Find all sessions
    List<Session> findAll();
}