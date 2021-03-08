package com.thoughtworks.reactiveatddworkshop.acceptance;

import com.thoughtworks.reactiveatddworkshop.ReactiveAtddWorkshopApplication;
import com.thoughtworks.reactiveatddworkshop.domain.Asset;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;

@ContextConfiguration(classes = ReactiveAtddWorkshopApplication.class)
// @WebAppConfiguration
public class AssetsIntegrationTest {

//  @Autowired
//  private WebClient webClient;

  Flux<Asset> getAssets() throws Exception {
    WebClient webClient = WebClient.builder()
        .baseUrl("http://localhost:8080")
        .defaultCookie("cookieKey", "cookieValue")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .defaultUriVariables(Collections.singletonMap("url", "http://localhost:8080"))
        .build();

    return webClient.get()
      .uri("/assets/")
      .retrieve()
    .bodyToFlux(Asset.class);
  }

}
