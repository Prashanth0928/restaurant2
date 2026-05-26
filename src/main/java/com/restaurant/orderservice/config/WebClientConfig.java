package com.restaurant.orderservice.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Configures a Spring WebClient bean for making reactive HTTP calls to external services.
 *
 * WebClient is the modern, non-blocking replacement for RestTemplate.
 * Even in a traditional Spring MVC application, WebClient is the recommended
 * way to call external REST APIs because it supports timeouts, retries, and
 * streaming out of the box.
 *
 * Usage example in a service class:
 * <pre>
 *   {@literal @}Autowired
 *   private WebClient webClient;
 *
 *   public OrderResponseDTO fetchExternalOrder(String orderId) {
 *       return webClient.get()
 *           .uri("/external-service/orders/{id}", orderId)
 *           .retrieve()
 *           .bodyToMono(OrderResponseDTO.class)
 *           .block();
 *   }
 * </pre>
 */
@Slf4j
@Configuration
public class WebClientConfig {

    @Value("${webclient.base-url:http://localhost:8080}")
    private String baseUrl;

    @Value("${webclient.timeout.connect:5000}")
    private int connectTimeoutMs;

    @Value("${webclient.timeout.read:5000}")
    private int readTimeoutMs;

    @Bean
    public WebClient webClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMs)
                .responseTimeout(Duration.ofMillis(readTimeoutMs))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(readTimeoutMs, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(readTimeoutMs, TimeUnit.MILLISECONDS)));

        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(logRequest())
                .filter(logResponse())
                .build();
    }

    // ── TheMealDB WebClient — points to a real external API on the internet ──

    @Bean
    public WebClient mealDbWebClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 8000)
                .responseTimeout(Duration.ofMillis(8000))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(8000, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(8000, TimeUnit.MILLISECONDS)));

        return WebClient.builder()
                .baseUrl("https://www.themealdb.com")
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(logRequest())
                .filter(logResponse())
                .build();
    }

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            log.debug("WebClient → {} {}", request.method(), request.url());
            return Mono.just(request);
        });
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {
            log.debug("WebClient ← status: {}", response.statusCode());
            return Mono.just(response);
        });
    }
}