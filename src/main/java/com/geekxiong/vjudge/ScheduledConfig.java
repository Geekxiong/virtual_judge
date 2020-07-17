package com.geekxiong.vjudge;

import com.geekxiong.vjudge.remote.RemoteDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/**
 * @Description Scheduled
 * @Author xiong
 * @Date 2020/07/17 10:30
 * @Version 1.0
 */

@Component()
public class ScheduledConfig {
    private final Logger LOGGER = LoggerFactory.getLogger(ScheduledConfig.class);

    private RemoteDispatcher remoteDispatcher;

    @Autowired
    public void setRemoteDispatcher(RemoteDispatcher remoteDispatcher) {
        this.remoteDispatcher = remoteDispatcher;
    }

    // 每半秒钟执行,C
    @Scheduled(fixedRate = 500)
    public void judgeFetch() {
        remoteDispatcher.fetchSubmitQueue();
//        LOGGER.info("=====>>>>>遍历代码提交队列");
    }
}
