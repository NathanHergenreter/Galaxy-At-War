package com.server.demo.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.server.demo.Entities.Messages;

public interface MessagesRepository extends JpaRepository<Messages, Long> {

	// TODO add accessors

}
