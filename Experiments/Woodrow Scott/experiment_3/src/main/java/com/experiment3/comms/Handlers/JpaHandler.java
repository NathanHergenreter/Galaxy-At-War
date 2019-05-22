package com.experiment3.comms.Handlers;

import com.experiment3.comms.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
public interface JpaHandler extends JpaRepository <User, Long> {

    @Query("select u from User u where u.userName = :username")
    User findByUserName(@Param("username") String username);

}
