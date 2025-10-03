package com.example.VTM.userAdministartion.repository;

import com.example.VTM.userAdministartion.entityOrDomain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsernameOrEmail(String username, String email);

    boolean existsByContactNumber(String contactNumber);

    User findByUsernameOrEmailOrContactNumber(String input, String input1, String input2);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    User findByContactNumber(String contactNumber);
    Optional<User> findByEmail(String email);
}
