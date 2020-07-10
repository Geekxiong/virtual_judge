package com.geekxiong.vjudge.repository;

import com.geekxiong.vjudge.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemRepository extends JpaRepository<Problem, Long> {
    Problem findByOriginOjAndOriginProbId(String originOj, String problemId);
}
