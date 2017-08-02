package com.os.app.beans;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class RestTemplateFactory implements FactoryBean<RestTemplate>, InitializingBean {
	public static String BASE_URL = "https://tpmoutdoor.capsulecrm.com/api";
	private static ObjectMapper mapper;
	private RestTemplate restTemplate;

	@Value("${capsulecrm.domain}")
	String domainName;

	@Value("${capsulecrm.port}")
	int portNumber;

	@Value("${capsulecrm.token}")
	String securityToken;

	public RestTemplateFactory() {
		super();
	}

	@Override
	public RestTemplate getObject() {
		return restTemplate;
	}

	@Override
	public Class<RestTemplate> getObjectType() {
		return RestTemplate.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
	
	static {
		mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
		mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
	}

	
	@Override
	public void afterPropertiesSet() {

		final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(new AuthScope(null, -1, AuthScope.ANY_REALM),
				new UsernamePasswordCredentials(securityToken, "x"));
		final CloseableHttpClient client = HttpClientBuilder.create()
				.setDefaultCredentialsProvider(credentialsProvider).build();

		final ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(client);

		restTemplate = new RestTemplate(requestFactory);
		restTemplate.getMessageConverters().clear();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter(mapper));
	}

	public void setRestTemplate(RestTemplate restTemplate2) {
		this.restTemplate	= restTemplate2;
	}

}