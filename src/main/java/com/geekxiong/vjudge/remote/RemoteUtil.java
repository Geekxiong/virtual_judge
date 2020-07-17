package com.geekxiong.vjudge.remote;

import com.geekxiong.vjudge.bean.JudgeInfoBean;
import com.geekxiong.vjudge.bean.ProblemBean;

import java.util.List;

public interface RemoteUtil {
    // 先要登录拿到cookie （每个OJ不同）
    // 然后把cookie存起来
    // 然后是爬取题面（每个OJ不同）
    // 再是使用cookie提交代码，（每个OJ不同）
    // 获取可以提交代码的语言
    // 查询提交代码的结果（每个OJ不同）
    String getCookie(String ojAccount, String password, String userAgent);

    ProblemBean getProblem(String problemId, String userAgent);

    ProblemBean getProblem(String problemId);

    List<String> getSubmitLanguages(String problemId, String userAgent);

    String submitCode(String problemId, String code, String language, String account, String cookie, String userAgent);

    JudgeInfoBean getJudgeInfo(String originRunId, String probId, String account);

    Boolean isJudging(String judgeStatus);



}
