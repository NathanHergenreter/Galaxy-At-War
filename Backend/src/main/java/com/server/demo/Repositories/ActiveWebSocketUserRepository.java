package com.server.demo.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.server.demo.Model.ActiveWebSocketUser;

public interface ActiveWebSocketUserRepository extends CrudRepository<ActiveWebSocketUser, Integer> {

	@Query("select DISTINCT(u.username) from ActiveWebSocketUser u where u.username != ?#{principal?.username}")
	List<String> findAllActiveUsers();
	
	List<ActiveWebSocketUser> findBySessionId(String sessionId);

	List<ActiveWebSocketUser> findByUsername(String username);

}
