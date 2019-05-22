package com.server.demo.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.server.demo.Entities.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

}
