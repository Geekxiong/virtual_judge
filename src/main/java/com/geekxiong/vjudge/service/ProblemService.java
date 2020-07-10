package com.geekxiong.vjudge.service;

import com.geekxiong.vjudge.entity.Problem;

public interface ProblemService {

    Problem getProblemByOjAndProbId(String ojName, String problemId);


}
