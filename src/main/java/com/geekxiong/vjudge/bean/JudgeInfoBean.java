package com.geekxiong.vjudge.bean;

import java.util.Date;

public class JudgeInfoBean {
    private String runId;
    private Date submitTime;
    private String status;
    private String ojName;
    private String probId;
    private String exeTime;
    private String exeMemory;
    private String codeLength;
    private String Language;

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

    public String getExeTime() {
        return exeTime;
    }

    public void setExeTime(String exeTime) {
        this.exeTime = exeTime;
    }

    public String getExeMemory() {
        return exeMemory;
    }

    public void setExeMemory(String exeMemory) {
        this.exeMemory = exeMemory;
    }

    public String getCodeLength() {
        return codeLength;
    }

    public void setCodeLength(String codeLength) {
        this.codeLength = codeLength;
    }

    public String getLanguage() {
        return Language;
    }

    public void setLanguage(String language) {
        Language = language;
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
                ", Language='" + Language + '\'' +
                '}';
    }
}
