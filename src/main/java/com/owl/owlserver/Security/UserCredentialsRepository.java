package com.owl.owlserver.Security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCredentialsRepository extends JpaRepository<UserCredentials,Integer> {

    UserCredentials findByUsername(String username);
}