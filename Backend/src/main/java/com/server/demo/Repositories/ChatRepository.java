package com.server.demo.Repositories;

import com.server.demo.Entities.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.*;

public interface ChatRepository extends JpaRepository<Chat, String> {

    //TODO add accessors

}
