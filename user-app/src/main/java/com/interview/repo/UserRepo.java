package com.interview.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.interview.model.User;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {

}
