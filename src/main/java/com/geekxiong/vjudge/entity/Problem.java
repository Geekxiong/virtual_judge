package com.geekxiong.vjudge.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
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
    private String timeLimit;

    @Column()
    private String memoryLimit;

    @Column(columnDefinition="text")
    private String input;

    @Column(columnDefinition="text")
    private String output;

    @Column(columnDefinition="text")
    private String sampleInput;

    @Column(columnDefinition="text")
    private String sampleOutput;

    @Column()
    private String originOj;

    @Column()
    private String originProbId;

    @Column(columnDefinition="text")
    private String originUrl;

    @JsonBackReference
    @OneToMany(mappedBy = "problem", cascade = CascadeType.REFRESH)
    private Set<Submission> submissions;

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

    public String getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(String timeLimit) {
        this.timeLimit = timeLimit;
    }

    public String getMemoryLimit() {
        return memoryLimit;
    }

    public void setMemoryLimit(String memoryLimit) {
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

    public Set<Submission> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(Set<Submission> submissions) {
        this.submissions = submissions;
    }
}
