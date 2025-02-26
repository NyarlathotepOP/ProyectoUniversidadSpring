package com.restaurante.proyecto.config;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.server.i18n.AcceptHeaderLocaleContextResolver;
import org.springframework.web.server.i18n.LocaleContextResolver;

// Configuración de la aplicación para internacionalización

@Configuration
public class ApplicationConfig {

    // Resolver de contexto de localización para aceptar la localización de la cabecera HTTP Accept-Language

    @Bean
    public LocaleContextResolver localeContextResolver() {
        AcceptHeaderLocaleContextResolver Resolver = new AcceptHeaderLocaleContextResolver();
        Resolver.setDefaultLocale(Locale.ENGLISH);
        return Resolver;
    }

    // Configuración de la fuente de mensajes para cargar los mensajes de los archivos de propiedades

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages/messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}