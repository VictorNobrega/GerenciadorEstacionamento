package com.estapar.gerenciadorestacionamento.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI parkingManagerOpenApi() {
		return new OpenAPI()
				.info(new Info()
						.title("Gerenciador de Estacionamento API")
						.description("API para processar eventos do simulador, consultar permanencias e calcular faturamento por setor.")
						.version("v1")
						.license(new License().name("Private")));
	}
}
