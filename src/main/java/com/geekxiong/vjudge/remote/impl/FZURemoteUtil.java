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
    private String statusUrl = "http://acm.fzu.edu.cn/log.php";

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
        String userAgent = HttpUtil.getRandomUA();
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
    public JudgeInfoBean getJudgeInfo(String originRunId, String probId, String account) {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", HttpUtil.getRandomUA());
        // get请求参数，user指定账号，
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("user", account);
        // pid指定题目，进一步筛选结果
        if(originRunId!=null){
            urlParams.put("pid", probId);
        }
        Response response = httpUtil.doGet(statusUrl, headers, urlParams);
        String html;
        try {
            html = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
//        System.out.println(html);
        JudgeInfoBean judgeInfoBean = parseJudgeInfo(html, originRunId);
        judgeInfoBean.setOjName("FZU");
        return judgeInfoBean;
    }

    @Override
    public Boolean isJudging(String judgeStatus) {
        return judgeStatus.equals("Queuing");
    }

    public ProblemBean parseProblemInfo(String html){
        ProblemBean problemBean = new ProblemBean();
        Document doc = Jsoup.parse(html);
        Element titleNode = doc.selectFirst("div.problem_title");
        String title = titleNode.text();
        problemBean.setTitle(title);

        // 获取int 类型的时间限制和内存限制
        problemBean.setTimeLimit(Integer.parseInt(CommonUtil.regFind(html, "Time Limit: (\\d+) mSec")));
        problemBean.setMemoryLimit(Integer.parseInt(CommonUtil.regFind(html, "Memory Limit : (\\d+) KB")));

        Elements contentNodes = doc.select("div.problem_content");
        Elements contentDescNodes = contentNodes.select("div.pro_desc");
        problemBean.setDescription(contentDescNodes.get(0).html());
        problemBean.setInput(contentDescNodes.get(1).html());
        problemBean.setOutput(contentDescNodes.get(2).html());

        String sampleInput = CommonUtil.regFind(html, "Sample Input</h2>\\s*<div class=\"data\">([\\s\\S]*?)</div>\\s*<h2><img");
        String sampleOutput = CommonUtil.regFind(html, "Sample Output</h2>\\s*<div class=\"data\">([\\s\\S]*?)</div>\\s*(<h2><img|</div>\\s*<br />)");
        problemBean.setSampleInput(sampleInput);
        problemBean.setSampleOutput(sampleOutput);
        problemBean.setHint(CommonUtil.regFind(html, "Hint</h2>([\\s\\S]+?)(<h2><img|</div>\\s*<br />)"));

        return problemBean;
    }

    private JudgeInfoBean parseJudgeInfo(String html, String originRunId){
        JudgeInfoBean judgeInfoBean = new JudgeInfoBean();
        Document doc = Jsoup.parse(html);
        Element submitRecord = null;
        if(originRunId==null){
            submitRecord = doc.selectFirst("tr[onmouseover=hl(this);]");
        }else {
            // 如果输入了runId，那就要去查询结果中找相应的那一条记录
            Elements submitRecords = doc.select("tr[onmouseover=hl(this);]");
            for(Element tmpRecord: submitRecords){
                String tmpRunId = tmpRecord.selectFirst("td").text();
                if(tmpRunId.equals(originRunId)){
                    submitRecord = tmpRecord;
                    break;
                }
            }
        }
        if(submitRecord == null){
            return null;
        }

        Elements items = submitRecord.select("td");
        judgeInfoBean.setRunId(items.get(0).text());
        judgeInfoBean.setSubmitTime(CommonUtil.dateStr2Date(items.get(1).text()));
        judgeInfoBean.setStatus(items.get(2).text());
        judgeInfoBean.setProbId(items.get(3).text());
        judgeInfoBean.setLanguage(items.get(4).text());
        // 运行时间
        String exeTimeStr = items.get(5).text();
        Integer exeTime = Integer.parseInt(exeTimeStr.replace("ms","").trim());
        judgeInfoBean.setExeTime(exeTime);
        // 运行内存
        String exeMemoryStr = items.get(6).text();
        Integer exeMemory = Integer.parseInt(exeMemoryStr.replace("KB","").trim());
        judgeInfoBean.setExeMemory(exeMemory);
        // 代码长度
        String CodeLengthStr = items.get(7).text();
        Integer CodeLength = Integer.parseInt(CodeLengthStr.replace("B","").trim());
        judgeInfoBean.setCodeLength(CodeLength);

        return judgeInfoBean;
    }
}
