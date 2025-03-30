package com.luanvan.commonservice.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application-common.yml")
public class CommonPropertyConfig {

}
