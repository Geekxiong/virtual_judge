package com.geekxiong.vjudge.remote.impl;

import com.geekxiong.vjudge.bean.JudgeInfoBean;
import com.geekxiong.vjudge.bean.ProblemBean;
import com.geekxiong.vjudge.remote.RemoteUtil;
import com.geekxiong.vjudge.util.CommonUtil;
import com.geekxiong.vjudge.util.HttpUtil;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HDUOjRemoteUtil implements RemoteUtil {
    private String loginUrl = "http://acm.hdu.edu.cn/userloginex.php?action=login";
    private String problemUrl = "http://acm.hdu.edu.cn/showproblem.php?pid=";
    private String submitUrl = "http://acm.hdu.edu.cn/submit.php?action=submit";
    private String statusUrl = "http://acm.hdu.edu.cn/status.php?user=";
    private HttpUtil httpUtil = HttpUtil.getInstance();

    @Override
    public String getCookie(String ojAccount, String password, String userAgent) {
        Map<String, String> bodyParams = new HashMap<>();
        bodyParams.put("username", ojAccount);
        bodyParams.put("userpass", password);
        bodyParams.put("login", "Sign In");
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", userAgent);

        Response response = httpUtil.doPost(loginUrl, headers, bodyParams);
        String cookie = response.priorResponse().headers().get("Set-Cookie");
        response.close();
        cookie = cookie.replace("; path=/", "");
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
//        System.out.println(html);
        ProblemBean problemBean = parseProblemInfo(html);
        problemBean.setProbId(problemId);
        problemBean.setOjName("HDUOJ");
        problemBean.setProbId(problemId);
        problemBean.setOriginUrl(originUrl);
        return problemBean;
    }

    @Override
    public String submitCode(String problemId, String code, String language, String cookie, String userAgent) {
        Map<String, String> bodyParams = new HashMap<>();
        bodyParams.put("check", "0");
        bodyParams.put("problemid", problemId);
        bodyParams.put("language", language);
        bodyParams.put("usercode", code);
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", userAgent);
        headers.put("Cookie", cookie+"; exesubmitlang=0");
        Response response = httpUtil.doPost(submitUrl, headers, bodyParams);
        response.close();
        return null;
    }

    @Override
    public JudgeInfoBean getJudgeInfo(String submitId, String account) {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", HttpUtil.getRandomUA());
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
        judgeInfoBean.setOjName("HDUOJ");
        return judgeInfoBean;
    }

    @Override
    public List<String> getSubmitLanguages(String problemId, String userAgent) {
        return null;
    }

    private ProblemBean parseProblemInfo(String html){
        ProblemBean problemBean = new ProblemBean();
        Document doc = Jsoup.parse(html);
        Element titleNode = doc.getElementsByTag("h1").first();
        String title = titleNode.text();
        problemBean.setTitle(title);

        Element timeAndMemoryLimitNode = doc.selectFirst("span[style=font-family:Arial;font-size:12px;font-weight:bold;color:green]");
        String tmpStr = timeAndMemoryLimitNode.html();
        tmpStr = tmpStr.substring(0, tmpStr.indexOf("<br>"));
        String[] tmpStrs = tmpStr.split("&nbsp;&nbsp;&nbsp;&nbsp;");
        String timeLimit = tmpStrs[0].replace("Time Limit: ", "");
        String memoryLimit = tmpStrs[1].replace("Memory Limit: ", "");
        problemBean.setTimeLimit(timeLimit);
        problemBean.setMemoryLimit(memoryLimit);

        Elements tmp_els = doc.select("div.panel_title[align=left]");
        for (Element el: tmp_els) {
            Element curEl = el.nextElementSibling();
            tmpStr = el.text().trim();
            if(tmpStr.equals("Problem Description")){
                problemBean.setDescription(curEl.html());
            }else if(tmpStr.equals("Input")){
                problemBean.setInput(curEl.html());
            }else if(tmpStr.equals("Output")){
                problemBean.setOutput(curEl.html());
            }else if(tmpStr.equals("Sample Input")){
                problemBean.setSampleInput(curEl.html());
            }else if(tmpStr.equals("Sample Output")) {
                problemBean.setSampleOutput(curEl.html());
            }
        }
        return problemBean;
    }

    private JudgeInfoBean parseJudgeInfo(String html){
        JudgeInfoBean judgeInfoBean = new JudgeInfoBean();
        Document doc = Jsoup.parse(html);
        Element table = doc.selectFirst("table[style=border-bottom:#879BFF 1px dashed]");
        Element submitRecord = table.selectFirst("tr[align=center]");
        Elements items = submitRecord.select("td");
        judgeInfoBean.setRunId(items.get(0).text());
        judgeInfoBean.setSubmitTime(CommonUtil.dateStr2Date(items.get(1).text()));
        judgeInfoBean.setStatus(items.get(2).text());
        judgeInfoBean.setProbId(items.get(3).text());
        judgeInfoBean.setExeTime(items.get(4).text());
        judgeInfoBean.setExeMemory(items.get(5).text());
        judgeInfoBean.setCodeLength(items.get(6).text());
        judgeInfoBean.setLanguage(items.get(7).text());
        return judgeInfoBean;
    }


}
