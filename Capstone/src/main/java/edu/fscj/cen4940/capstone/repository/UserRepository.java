package edu.fscj.cen4940.capstone.repository;

import edu.fscj.cen4940.capstone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    List<User> findAll();
    User findByUsername(String username);
    void deleteByUsername(String username);
    User findByEmail(String email);
    Boolean existsByEmail(String email);
}