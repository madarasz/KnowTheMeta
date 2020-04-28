package com.madarasz.knowthemeta.database.DRs;

import java.util.List;

import com.madarasz.knowthemeta.database.DOs.User;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    @Query("MATCH (u:User {user_id: $id}) RETURN u LIMIT 1")
    User findById(int id);

    @Query("MATCH (u:User) RETURN u")
    List<User> listAll();
}