package com.geekxiong.vjudge.bean;

import java.util.Date;

public class JudgeInfoBean {
    private String runId;
    private Date submitTime;
    private String status;
    private String ojName;
    private String probId;
    private Integer exeTime;
    private Integer exeMemory;
    private Integer codeLength;
    private String language;

    public String getRunId() {
        return runId;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOjName() {
        return ojName;
    }

    public void setOjName(String ojName) {
        this.ojName = ojName;
    }

    public String getProbId() {
        return probId;
    }

    public void setProbId(String probId) {
        this.probId = probId;
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

    @Override
    public String toString() {
        return "JudgeInfoBean{" +
                "runId='" + runId + '\'' +
                ", submitTime=" + submitTime +
                ", status='" + status + '\'' +
                ", ojName='" + ojName + '\'' +
                ", probId='" + probId + '\'' +
                ", exeTime='" + exeTime + '\'' +
                ", exeMemory='" + exeMemory + '\'' +
                ", codeLength='" + codeLength + '\'' +
                ", language='" + language + '\'' +
                '}';
    }
}
