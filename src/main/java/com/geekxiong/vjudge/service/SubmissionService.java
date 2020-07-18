package com.geekxiong.vjudge.service;

import com.geekxiong.vjudge.entity.Submission;

public interface SubmissionService {
    Long submitCode(Long userId, Long problemId, String code, String language);

    Submission getById(Long id);
}
