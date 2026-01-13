package br.com.cotiinformatica.api_usuarios.configurations;

import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Usuários")
                        .description("API para gerenciamento de usuários")
                        .version("v1")
                        .contact(new Contact()
                                .name("Coti Informática")
                                .url("https://www.cotiinformatica.com.br")
                                .email("contato@cotiinformatica.com.br")
                        )
                );
    }
}

