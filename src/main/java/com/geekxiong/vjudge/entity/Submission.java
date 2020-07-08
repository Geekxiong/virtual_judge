package com.geekxiong.vjudge.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "submission")
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column()
    private Long id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @Temporal(TemporalType.TIMESTAMP)
    @Column()
    private Date submitTime;

    @Column(columnDefinition="text")
    private String code;

    @Column()
    private Integer status;

    @Column()
    private String originRunId;

    @Column()
    private Integer exeTime;

    @Column()
    private Integer exeMemory;

    @Column()
    private Integer codeLength;

    @Column()
    private String language;

    @Column()
    private String judgeStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getOriginRunId() {
        return originRunId;
    }

    public void setOriginRunId(String originRunId) {
        this.originRunId = originRunId;
    }

    public Integer getExeTime() {
        return exeTime;
    }

    public void setExeTime(Integer exeTime) {
        this.exeTime = exeTime;
    }

    public Integer getExeMemory() {
        return exeMemory;
    }

    public void setExeMemory(Integer exeMemory) {
        this.exeMemory = exeMemory;
    }

    public Integer getCodeLength() {
        return codeLength;
    }

    public void setCodeLength(Integer codeLength) {
        this.codeLength = codeLength;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getJudgeStatus() {
        return judgeStatus;
    }

    public void setJudgeStatus(String judgeStatus) {
        this.judgeStatus = judgeStatus;
    }
}
