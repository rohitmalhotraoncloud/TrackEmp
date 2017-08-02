package com.os.app.config;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;

@Profile("cloud")
@EnableAsync
public class CloudConfiguration {

}
