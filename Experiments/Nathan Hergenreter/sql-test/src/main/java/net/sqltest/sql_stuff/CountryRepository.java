package net.sqltest.sql_stuff;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

//CrudRepository provides methods for working with entries, including saving, deleting and finding
public interface CountryRepository extends JpaRepository<Country, Long> {

    List<Country> findByName(String name);
    
}
