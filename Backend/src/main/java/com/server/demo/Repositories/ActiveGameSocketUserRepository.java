package com.server.demo.Repositories;

import com.server.demo.Model.ActiveGameSessionUser;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ActiveGameSocketUserRepository extends CrudRepository<ActiveGameSessionUser, Integer> {
    List<ActiveGameSessionUser> findByUsername(String username);
    List<ActiveGameSessionUser> findBySessionId(String sessionId);
    List<ActiveGameSessionUser> findByGameId(Integer gameId);
}
