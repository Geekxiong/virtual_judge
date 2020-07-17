package com.geekxiong.vjudge.service.impl;

import com.geekxiong.vjudge.entity.Problem;
import com.geekxiong.vjudge.entity.Submission;
import com.geekxiong.vjudge.entity.User;
import com.geekxiong.vjudge.remote.RemoteDispatcher;
import com.geekxiong.vjudge.repository.ProblemRepository;
import com.geekxiong.vjudge.repository.SubmissionRepository;
import com.geekxiong.vjudge.repository.UserRepository;
import com.geekxiong.vjudge.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class SubmissionServiceImpl implements SubmissionService {
    private UserRepository userRepository;
    private ProblemRepository problemRepository;
    private SubmissionRepository submissionRepository;
    private RemoteDispatcher remoteDispatcher;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setProblemRepository(ProblemRepository problemRepository) {
        this.problemRepository = problemRepository;
    }

    @Autowired
    public void setSubmissionRepository(SubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
    }

    @Autowired
    public void setRemoteDispatcher(RemoteDispatcher remoteDispatcher) {
        this.remoteDispatcher = remoteDispatcher;
    }

    @Override
    public Long submitCode(Long userId, Long problemId, String code, String language) {
        User user = userRepository.findById(userId).orElse(null);
        Problem problem = problemRepository.findById(problemId).orElse(null);
        Submission submission = new Submission();
        submission.setCode(code);
        submission.setSubmitTime(new Date());
        submission.setLanguage(language);
        submission.setUser(user);
        submission.setProblem(problem);
        submission.setStatus(Submission.Status.SUBMITTING);
        // 交给dispatcher, 进入代码提交队列
        submissionRepository.save(submission);
        remoteDispatcher.pushSubmitQueue(submission.getId(), problem.getOriginOj());
        return submission.getId();
    }
}
