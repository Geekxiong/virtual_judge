package com.geekxiong.vjudge.remote.impl;

import com.geekxiong.vjudge.bean.JudgeInfoBean;
import com.geekxiong.vjudge.bean.ProblemBean;
import com.geekxiong.vjudge.remote.RemoteUtil;
import com.geekxiong.vjudge.util.CommonUtil;
import com.geekxiong.vjudge.util.HttpUtil;
import okhttp3.Response;
import org.apache.commons.codec.binary.Base64;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("FZURemoteUtil")
public class FZURemoteUtil implements RemoteUtil {

    private String loginUrl = "http://acm.fzu.edu.cn/login.php?act=1&dir=";
    private String problemUrl = "http://acm.fzu.edu.cn/problem.php?pid=";
    private String submitUrl = "http://acm.fzu.edu.cn/submit.php?act=5";
    private String statusUrl = "http://acm.fzu.edu.cn/log.php?user=";

    private HttpUtil httpUtil;

    @Autowired
    public void setHttpUtil(HttpUtil httpUtil) {
        this.httpUtil = httpUtil;
    }

    @Override
    public String getCookie(String ojAccount, String password, String userAgent) {
        Map<String, String> bodyParams = new HashMap<>();
        bodyParams.put("uname", ojAccount);
        bodyParams.put("upassword", password);
        bodyParams.put("submit", "Submit");
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", userAgent);
        String tmpQueryDir = Base64.encodeBase64String(("/register.php?act=done&name="+ojAccount).getBytes());
        Response response = httpUtil.doPost(loginUrl+tmpQueryDir, headers, bodyParams);
        String cookie = response.headers().get("Set-Cookie");
        response.close();
        cookie = cookie.replace("; path=/; HttpOnly", "");
        return cookie;
    }

    @Override
    public ProblemBean getProblem(String problemId, String userAgent) {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", userAgent);
        String originUrl = problemUrl+problemId;
        Response response = httpUtil.doGet(originUrl, headers, null);
        String html;
        try {
            html = response.body().string();
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        ProblemBean problemBean = parseProblemInfo(html);
        problemBean.setProbId(problemId);
        problemBean.setOjName("FZU");
        problemBean.setProbId(problemId);
        problemBean.setOriginUrl(originUrl);
        return problemBean;
    }

    @Override
    public ProblemBean getProblem(String problemId) {
        String userAgent = httpUtil.getRandomUA();
        return getProblem(problemId,  userAgent);
    }

    @Override
    public List<String> getSubmitLanguages(String problemId, String userAgent) {
        return null;
    }

    @Override
    public String submitCode(String problemId, String code, String language, String account, String cookie, String userAgent) {
        Map<String, String> bodyParams = new HashMap<>();
        bodyParams.put("usr", account);
        bodyParams.put("lang", language);
        bodyParams.put("pid", problemId);
        bodyParams.put("code", code);
        bodyParams.put("submit", "Submit");
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", userAgent);
        headers.put("Cookie", cookie);
        Response response = httpUtil.doPost(submitUrl, headers, bodyParams);
        response.close();
        return null;
    }

    @Override
    public JudgeInfoBean getJudgeInfo(String submitId, String account) {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", httpUtil.getRandomUA());
        String queryUrl = statusUrl+account;
        Response response = httpUtil.doGet(queryUrl, headers, null);
        String html;
        try {
            html = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
//        System.out.println(html);
        JudgeInfoBean judgeInfoBean = parseJudgeInfo(html);
        judgeInfoBean.setOjName("FZU");
        return judgeInfoBean;
    }

    public ProblemBean parseProblemInfo(String html){
        ProblemBean problemBean = new ProblemBean();
        Document doc = Jsoup.parse(html);
        Element titleNode = doc.selectFirst("div.problem_title");
        String title = titleNode.text();
        problemBean.setTitle(title);

        Element timeAndMemoryLimitNode = doc.selectFirst("div.problem_desc");
        String tmpStr = timeAndMemoryLimitNode.html();
        tmpStr = tmpStr.substring(tmpStr.indexOf("<br />")+6);
        String[] tmpStrs = tmpStr.split("&nbsp;&nbsp;&nbsp;&nbsp;");
        String timeLimit = tmpStrs[0].replace("Time Limit: ", "");
        String memoryLimit = tmpStrs[1].replace("Memory Limit: ", "");
        problemBean.setTimeLimit(timeLimit);
        problemBean.setMemoryLimit(memoryLimit);

        Elements contentNodes = doc.select("div.problem_content");
        Elements contentDescNodes = contentNodes.select("div.pro_desc");
        problemBean.setDescription(contentDescNodes.get(0).html());
        problemBean.setInput(contentDescNodes.get(1).html());
        problemBean.setOutput(contentDescNodes.get(2).html());

        Elements contentDataNodes = contentNodes.select("div.data");
        String sampleInput = contentDataNodes.get(0).html();
        String sampleOutput = contentDataNodes.get(1).html();
        problemBean.setSampleInput("<pre>"+sampleInput+"</pre>");
        problemBean.setSampleOutput("<pre>"+sampleOutput+"</pre>");

        return problemBean;
    }

    private JudgeInfoBean parseJudgeInfo(String html){
        JudgeInfoBean judgeInfoBean = new JudgeInfoBean();
        Document doc = Jsoup.parse(html);
        Element submitRecord = doc.selectFirst("tr[onmouseover=hl(this);]");
        Elements items = submitRecord.select("td");
        judgeInfoBean.setRunId(items.get(0).text());
        judgeInfoBean.setSubmitTime(CommonUtil.dateStr2Date(items.get(1).text()));
        judgeInfoBean.setStatus(items.get(2).text());
        judgeInfoBean.setProbId(items.get(3).text());
        judgeInfoBean.setLanguage(items.get(4).text());
        judgeInfoBean.setExeTime(items.get(5).text());
        judgeInfoBean.setExeMemory(items.get(6).text());
        judgeInfoBean.setCodeLength(items.get(7).text());

        return judgeInfoBean;
    }
}