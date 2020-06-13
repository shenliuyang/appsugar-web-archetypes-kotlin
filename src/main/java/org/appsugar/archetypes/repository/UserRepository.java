package org.appsugar.archetypes.repository;


import org.appsugar.archetypes.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
