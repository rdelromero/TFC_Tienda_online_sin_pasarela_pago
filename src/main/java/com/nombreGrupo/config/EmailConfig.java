package com.nombreGrupo.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailConfig {

	@Bean
	public ModelMapper modeloMapper() {
		return new ModelMapper();
	}
	
}
