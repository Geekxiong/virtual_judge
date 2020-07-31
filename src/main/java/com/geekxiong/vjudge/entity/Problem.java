package com.geekxiong.vjudge.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.geekxiong.vjudge.bean.ProblemBean;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "problem")
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column()
    private Long id;

    @Column()
    private String title;

    @Column(columnDefinition="text")
    private String description;

    @Column()
    private Integer timeLimit;

    @Column()
    private Integer memoryLimit;

    @Column(columnDefinition="text")
    private String input;

    @Column(columnDefinition="text")
    private String output;

    @Column(columnDefinition="text")
    private String sampleInput;

    @Column(columnDefinition="text")
    private String sampleOutput;

    @Column(columnDefinition="text")
    private String hint;

    @Column()
    private Integer submitNumber;

    @Column()
    private Integer acceptNumber;

    @Column()
    private String originOj;

    @Column()
    private String originProbId;

    @Column(columnDefinition="text")
    private String originUrl;

    @Temporal(TemporalType.TIMESTAMP)
    @Column()
    private Date updateTime;

    @JsonBackReference
    @OneToMany(mappedBy = "problem", cascade = CascadeType.REFRESH)
    private Set<Submission> submissions;

    public Problem(){}

    public Problem(ProblemBean problemBean){
        this.originOj = problemBean.getOjName();
        this.originProbId = problemBean.getProbId();
        this.originUrl = problemBean.getOriginUrl();
        this.title = problemBean.getTitle();
        this.description = problemBean.getDescription();
        this.input = problemBean.getInput();
        this.output = problemBean.getOutput();
        this.sampleInput = problemBean.getSampleInput();
        this.sampleOutput = problemBean.getSampleOutput();
        this.hint = problemBean.getHint();
        this.timeLimit = problemBean.getTimeLimit();
        this.memoryLimit = problemBean.getMemoryLimit();
        this.submitNumber = 0;
        this.acceptNumber = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
    }

    public Integer getMemoryLimit() {
        return memoryLimit;
    }

    public void setMemoryLimit(Integer memoryLimit) {
        this.memoryLimit = memoryLimit;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getSampleInput() {
        return sampleInput;
    }

    public void setSampleInput(String sampleInput) {
        this.sampleInput = sampleInput;
    }

    public String getSampleOutput() {
        return sampleOutput;
    }

    public void setSampleOutput(String sampleOutput) {
        this.sampleOutput = sampleOutput;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public Integer getSubmitNumber() {
        return submitNumber;
    }

    public void setSubmitNumber(Integer submitNumber) {
        this.submitNumber = submitNumber;
    }

    public Integer getAcceptNumber() {
        return acceptNumber;
    }

    public void setAcceptNumber(Integer acceptNumber) {
        this.acceptNumber = acceptNumber;
    }

    public String getOriginOj() {
        return originOj;
    }

    public void setOriginOj(String originOj) {
        this.originOj = originOj;
    }

    public String getOriginProbId() {
        return originProbId;
    }

    public void setOriginProbId(String originProbId) {
        this.originProbId = originProbId;
    }

    public String getOriginUrl() {
        return originUrl;
    }

    public void setOriginUrl(String originUrl) {
        this.originUrl = originUrl;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Set<Submission> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(Set<Submission> submissions) {
        this.submissions = submissions;
    }
}
