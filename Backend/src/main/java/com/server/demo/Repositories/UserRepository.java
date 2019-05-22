package com.server.demo.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.server.demo.Entities.User;

public interface UserRepository extends JpaRepository<User, Long> {

	//Returns user with given name
    User findByName(String name);
    
}
