package com.zkzy.portal.common.Listener;

import com.zkzy.portal.common.controller.LicenseAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * Created by Thinkpad-W530 on 2020/9/11.
 */
@Configuration
public class LicenseListener implements ApplicationListener<ContextRefreshedEvent>, Ordered, ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(LicenseListener.class);

    private ApplicationContext context;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        LOGGER.info("-----------container has started-----------");
        try {
            Boolean a = LicenseAuth.authLicense();
            if (!a) {
                ConfigurableApplicationContext ctx = (ConfigurableApplicationContext) context;
                ctx.close();
            } else {
                LOGGER.info("————————————The License is valid——————————————");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}
