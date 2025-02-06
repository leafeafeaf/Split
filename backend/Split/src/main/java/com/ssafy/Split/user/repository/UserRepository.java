package com.ssafy.Split.user.repository;

import com.ssafy.Split.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer> {
}
