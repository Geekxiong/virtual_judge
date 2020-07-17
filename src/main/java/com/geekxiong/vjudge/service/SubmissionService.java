package com.geekxiong.vjudge.service;

public interface SubmissionService {
    Long submitCode(Long userId, Long problemId, String code, String language);
}
