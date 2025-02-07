package com.ssafy.Split.domain.user.repository;

import com.ssafy.Split.domain.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer> {
}
