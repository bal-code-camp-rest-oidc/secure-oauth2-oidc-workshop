package com.example.library.client.credentials.batch;

import com.example.library.client.credentials.web.BookResource;
import org.springframework.batch.item.support.AbstractItemStreamItemWriter;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

public class WebClientItemWriter<T> extends AbstractItemStreamItemWriter<T> {

    private final WebClient webClient;
    private final String targetUrl;

    public WebClientItemWriter(WebClient webClient, String targetUrl) {
        this.webClient = webClient;
        this.targetUrl = targetUrl;
    }

    @Override
    public void write(List<? extends T> items) {
        items.forEach(
                item -> webClient.post().uri(targetUrl + "/books").bodyValue(item)
                        .retrieve()
                        .onStatus(
                                currentStatus -> currentStatus.value() == HttpStatus.UNAUTHORIZED.value(),
                                clientResponse -> Mono.error(new BadCredentialsException("Not authenticated")))
                        .onStatus(
                                currentStatus -> currentStatus.value() == HttpStatus.BAD_REQUEST.value(),
                                clientResponse -> Mono.error(new IllegalArgumentException(clientResponse.statusCode().getReasonPhrase())))
                        .onStatus(
                                HttpStatus::is5xxServerError,
                                clientResponse -> Mono.error(new RuntimeException(clientResponse.statusCode().getReasonPhrase())))
                        .bodyToMono(BookResource.class).log().block()
        );
    }
}
