package com.geekxiong.vjudge.service;

import com.geekxiong.vjudge.entity.Problem;

public interface ProblemService {

    Problem getByOjAndProbId(String ojName, String problemId);

    Problem getById(Long problemId);

    Problem updateById(Long problemId);

}
