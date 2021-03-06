package com.geekxiong.vjudge.remote;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.geekxiong.vjudge.bean.AccountBean;
import com.geekxiong.vjudge.bean.JudgeInfoBean;
import com.geekxiong.vjudge.entity.Problem;
import com.geekxiong.vjudge.entity.Submission;
import com.geekxiong.vjudge.repository.SubmissionRepository;
import com.geekxiong.vjudge.util.HttpUtil;
import com.geekxiong.vjudge.util.RedisUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @Description Dispatcher
 * @Author xiong
 * @Date 2020/07/17 8:59
 * @Version 1.0
 */
@Component
public class RemoteDispatcher {
    private RemoteUtilRegister remoteUtilRegister;
    private Map<String, List<AccountBean>> remoteAccounts;
    private RedisUtil redisUtil;
    private SubmissionRepository submissionRepository;
    private final String SUBMIT_QUEUE = "submit_queue";
    private final Long EXPIRATION_TIME = 30L*60*1000;

    @Autowired
    public void setRemoteUtilRegister(RemoteUtilRegister remoteUtilRegister) {
        this.remoteUtilRegister = remoteUtilRegister;
    }
    @Autowired
    public void setRedisUtil(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }
    @Autowired
    public void setSubmissionRepository(SubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
    }

    RemoteDispatcher(@Value("${config_file.account}") String accountFilePath) throws IOException {
        initAccounts(accountFilePath);
    }

    /**
     * 题目进交题队列
     * @param submissionId 提交ID
     */
    public void pushSubmitQueue(Long submissionId, String ojName){
        // 加ojName 是想在后面获取账号时不用先查询数据库
        redisUtil.rightPushToList(SUBMIT_QUEUE, submissionId+"#"+ojName);
    }

    /**
     * 遍历提交代码任务队列
     */
    public void fetchSubmitQueue(){
        // 先判断有没有需要提交的代码任务，判断队列长度即可
        Long queueSize = redisUtil.getListSize(SUBMIT_QUEUE);
        // 如果没有任务，结束
        if(queueSize==0){
            return;
        }
        String submitTask = (String)redisUtil.leftPopFromList(SUBMIT_QUEUE);
        String[] tempArr = submitTask.split("#");
        Long submitId = Long.parseLong(tempArr[0]);
        String ojName = tempArr[1];
        // 先去拿可用的（lock=false）的account，如果没有的话就结束
        // 获取到一个已经加了锁的account，使用完需要解锁
        AccountBean account = getFreeAccount(ojName);
        if(account==null){
            // 取的任务，放回去，有点傻
            redisUtil.leftPushToList(SUBMIT_QUEUE, submitTask);
            return;
        }
        // 异步处理代码提交
        execAsyncSubmit(submitId, account);
    }

    /**
     * 该方法会被异步执行，
     * @param submitId 提交的Id
     * @param account 提交的账号
     */
    @Async("submitCodeExecutor")
    void execAsyncSubmit(Long submitId, AccountBean account){
        Submission submission = submissionRepository.findById(submitId).orElse(null);
        Boolean isJudging = submitCode(submission, account);
        account.setLock(false);
        account.setLastUseTime(System.currentTimeMillis());
        if(isJudging){
            exeAsyncQuery(submission);
        }
    }

    /**
     * 异步查询判题结果
     * @param submission 代码提交记录
     */
    @Async("queryJudgeInfoExecutor")
    void exeAsyncQuery(Submission submission){
        Problem problem = submission.getProblem();
        RemoteUtil remoteUtil = remoteUtilRegister.getRemoteUtil(problem.getOriginOj()+"RemoteUtil");
        // 如果查询时还在判题，就暂停一秒钟，然后再查
        int maxQueryTimes = 20;
        JudgeInfoBean judgeInfo;
        do {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            judgeInfo = remoteUtil.getJudgeInfo(submission.getOriginRunId(), problem.getOriginProbId(), submission.getSubmitAccount());
            maxQueryTimes--;
        }while (remoteUtil.isJudging(judgeInfo.getStatus())&&maxQueryTimes>=0);
        if(maxQueryTimes<0){
            submission.setStatus(Submission.Status.QUERY_FAILED);
        }else {
            submission.setStatus(Submission.Status.JUDGED);
            submission.setJudgeStatus(judgeInfo.getStatus());
            submission.setExeMemory(judgeInfo.getExeMemory());
            submission.setExeTime(judgeInfo.getExeTime());
            submission.setCodeLength(judgeInfo.getCodeLength());
        }
        submissionRepository.save(submission);
    }


    /**
     * 提交代码，并获取其runID
     * @param submission 代码提交记录
     * @param account 用于提交代码的账号
     * @return 返回是否需要进线程池进行结果查询
     */
    private Boolean submitCode(Submission submission, AccountBean account){
        Problem problem = submission.getProblem();
        RemoteUtil remoteUtil = remoteUtilRegister.getRemoteUtil(problem.getOriginOj()+"RemoteUtil");
        // 先提交代码
        // 大部分OJ 不会返回runId
        // 这时候需要趁着这个账号还在锁死中的时候去查询oj中的status得到runId
        String runId = remoteUtil.submitCode(
                problem.getOriginProbId(),
                submission.getCode(),
                submission.getLanguage(),
                account.getAccount(),
                account.getCookie(),
                account.getUserAgent());
        // 然后进行第一次查询，其目的是获取到原始的run id
        JudgeInfoBean judgeInfo = remoteUtil.getJudgeInfo(runId, problem.getOriginProbId(), account.getAccount());
        submission.setStatus(Submission.Status.JUDGING);
        submission.setOriginRunId(judgeInfo.getRunId());
        submission.setJudgeStatus(judgeInfo.getStatus());
        submission.setExeMemory(judgeInfo.getExeMemory());
        submission.setExeTime(judgeInfo.getExeTime());
        submission.setCodeLength(judgeInfo.getCodeLength());
        submission.setSubmitAccount(account.getAccount());
        submissionRepository.save(submission);
        // 如果还在判题中，那么就就要异步循环获取它的结果
        return remoteUtil.isJudging(submission.getJudgeStatus());
    }




    /**
     * 初始化远程账号
     * @throws IOException 读文件的IO异常
     */
    private void initAccounts(String accountFilePath) throws IOException {
        Resource resource = new ClassPathResource(accountFilePath);
        File file = resource.getFile();
        String jsonStr = FileUtils.readFileToString(file, "UTF-8");
        JSONObject json = JSON.parseObject(jsonStr);
        Set<String> jsonKeys = json.keySet();
        remoteAccounts = new HashMap<>(jsonKeys.size());
        for(String key: jsonKeys){
            List<AccountBean> accountList = new ArrayList<>();
            JSONArray arr = json.getJSONArray(key);
            for(int i=0; i<arr.size(); i++){
                JSONObject jObj = arr.getJSONObject(i);
                AccountBean accountBean = new AccountBean();
                accountBean.setOj(key);
                accountBean.setAccount(jObj.getString("account"));
                accountBean.setPassword(jObj.getString("password"));
                accountBean.setLock(false);
                accountList.add(accountBean);
            }
            remoteAccounts.put(key, accountList);
        }
    }

    /**
     * 根据oj获取空闲的账号，
     * @param ojName oj名
     * @return 空闲账号信息
     */
    private AccountBean getFreeAccount(String ojName){
        List<AccountBean> accounts = remoteAccounts.get(ojName);
        for(AccountBean account: accounts){
            // 如果被占用，就看下一个
            if(account.isLock()){
                continue;
            }
            // 如果没有被占用，先给他锁定
            account.setLock(true);
            // 再看它登录没有，再判断登录过期没
            if(!checkAccountIsLogin(account)){
                remoteAccountLogin(account);
            }
            return account;
        }
        return null;
    }

    /**
     * 账号登录
     * @param account 账号
     */
    private void remoteAccountLogin(AccountBean account){
        RemoteUtil remoteUtil = remoteUtilRegister.getRemoteUtil(account.getOj()+"RemoteUtil");
        // 换一个UA
        String userAgent = HttpUtil.getRandomUA();
        account.setUserAgent(userAgent);
        // 登录获取cookie
        String cookie = remoteUtil.getCookie(account.getAccount(), account.getPassword(), account.getUserAgent());
        account.setCookie(cookie);
        account.setLastUseTime(System.currentTimeMillis());
    }

    /**
     * 判断账号是否登录了
     * @param account 账号
     * @return 返回登录结果
     */
    private Boolean checkAccountIsLogin(AccountBean account){
        // 如果cookie有值，lastUseTime不为空，表明登录过
        if(StringUtils.hasText(account.getCookie())&&account.getLastUseTime()!=null){
            // 登录过，就只需检测登录时效过没过
            Long now = System.currentTimeMillis();
            Long lastUseTime = account.getLastUseTime();
            return now - lastUseTime <= EXPIRATION_TIME;
        }
        return false;
    }
}
