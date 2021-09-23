package com.zkzy.portal.common.Filter;

import com.zkzy.portal.common.controller.LicenseAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by Thinkpad-W530 on 2020/9/11.
 */
@Configuration
public class LicenseTimeTask implements ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(LicenseTimeTask.class);

    private ApplicationContext context;

    @Scheduled(cron = "0 30 9 * * ?")
    public void scheduled() {
        try {
            Boolean a = LicenseAuth.authLicense();
            if (!a) {
                ConfigurableApplicationContext ctx = (ConfigurableApplicationContext) context;
                ctx.close();
            }else {
                LOGGER.info("————————————The License is valid——————————————");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}
