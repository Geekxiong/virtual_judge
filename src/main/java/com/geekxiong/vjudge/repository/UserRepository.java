package com.geekxiong.vjudge.repository;

import com.geekxiong.vjudge.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
