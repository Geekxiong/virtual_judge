package com.geekxiong.vjudge.repository;

import com.geekxiong.vjudge.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

}
