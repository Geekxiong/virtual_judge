package com.geekxiong.vjudge.remote;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service("register")
public class RemoteUtilRegister implements InitializingBean, ApplicationContextAware {

    private Map<String, RemoteUtil> remoteUtilImpMap = new HashMap<>();
    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() {
        Map<String, RemoteUtil> beanMap = applicationContext.getBeansOfType(RemoteUtil.class);
        String name;
        for (RemoteUtil remoteUtil : beanMap.values()) {
            name = remoteUtil.getClass().getSimpleName();
            System.out.println("---key:\t" + name);
            remoteUtilImpMap.put(name, remoteUtil);
        }
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public RemoteUtil getRemoteUtil(String name) {
        return remoteUtilImpMap.get(name);
    }
}
