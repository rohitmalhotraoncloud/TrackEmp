package com.os;

import javax.xml.bind.DatatypeConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.os.app.authentication.TokenHandler;
import com.os.app.repository.CustomRepositoryImpl;

@SpringBootApplication
@EnableAutoConfiguration
@EnableScheduling
@EnableJpaRepositories(repositoryBaseClass = CustomRepositoryImpl.class)
public class Application {

	@Value("${token.secret}")
	private String secret;

	@Bean
	public TokenHandler tokenHandler() {
		return new TokenHandler(DatatypeConverter.parseBase64Binary(secret));
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
