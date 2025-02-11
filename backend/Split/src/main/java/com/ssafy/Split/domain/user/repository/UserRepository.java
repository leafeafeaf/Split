package com.ssafy.Split.domain.user.repository;

import com.ssafy.Split.domain.user.domain.entity.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {
    //로그인할 유저의 정보를 가져오는 메소드
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByNickname(@NotBlank(message = "닉네임은 필수 입력값입니다.") String nickname);
}
