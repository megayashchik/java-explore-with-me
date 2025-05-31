package ru.practicum.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class StatsClientConfig {

	@Value("${stats-server.url}")
	private String serverUrl;

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder
				.setConnectTimeout(Duration.ofSeconds(5))
				.setReadTimeout(Duration.ofSeconds(5))
				.requestFactory(HttpComponentsClientHttpRequestFactory::new)
				.build();
	}

	@Bean
	public StatsClient statsClient(RestTemplate restTemplate) {
		return new StatsClient(serverUrl, restTemplate);
	}
}