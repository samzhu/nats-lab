package com.example.producer;

import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.CodecConfigurer;

import io.cloudevents.spring.messaging.CloudEventMessageConverter;
import io.cloudevents.spring.webflux.CloudEventHttpMessageReader;
import io.cloudevents.spring.webflux.CloudEventHttpMessageWriter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class CloudEventMessageConverterConfiguration {
    
	@Bean
	public CloudEventMessageConverter cloudEventMessageConverter() {
		log.info("有註冊 CloudEventMessageConverter");
		return new CloudEventMessageConverter();
	}

	// @Bean
	// public CodecCustomizer codecCustomizer() {
	// 	return new CodecCustomizer() {
	// 		@Override
	// 		public void customize(CodecConfigurer configurer) {
	// 			log.info("有註冊 CodecCustomizer");
	// 			configurer.customCodecs().register(new CloudEventHttpMessageReader());
	// 			configurer.customCodecs().register(new CloudEventHttpMessageWriter());
	// 		}
	// 	};
	// }
}
