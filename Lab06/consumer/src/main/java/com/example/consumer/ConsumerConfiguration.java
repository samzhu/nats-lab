package com.example.consumer;

import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsumerConfiguration {


    @Bean
	public Consumer<String> sink() {
		return System.out::println;
	}
    
}
