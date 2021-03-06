package com.thoughtworks.reactiveatddworkshop.infrastructure;

import com.thoughtworks.reactiveatddworkshop.domain.Asset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import static com.thoughtworks.reactiveatddworkshop.config.CustomConnectionFactoryInitializer.BTC_ASSET_NAME;
import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AssetsControllerShould {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("Return all the results.")
    void returnAllAssets() {
        assertEquals(BTC_ASSET_NAME,
                webTestClient.get()
                        .uri("/assets")
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBodyList(Asset.class)
                        .hasSize(3)
                        .returnResult()
                        .getResponseBody()
                        .get(0)
                        .getName());
    }

    @Test
    @DisplayName("Insert new Assets and get it by id")
    void insertNewAssets() {
        Asset insertedAsset = webTestClient
                .post()
                .uri("/assets")
                .body(BodyInserters.fromProducer(Mono.just(new Asset(null,
                        BTC_ASSET_NAME,
                        5.0)), Asset.class))
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(Asset.class)
                .returnResult()
                .getResponseBody();

        Asset retrievedAsset = webTestClient
                .get()
                .uri("/assets/" + insertedAsset.getId())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Asset.class)
                .returnResult()
                .getResponseBody();

        assertEquals(retrievedAsset, insertedAsset);
        deleteById(insertedAsset.getId());
    }

    @Test
    @DisplayName("Retrieve assets by name")
    void retrieveAssetsByName() {

        String randomName = "randomName";
        Asset insertedAsset = webTestClient
                .post()
                .uri("/assets")
                .body(BodyInserters.fromProducer(Mono.just(new Asset(null,
                        randomName,
                        1.0)), Asset.class))
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(Asset.class)
                .returnResult()
                .getResponseBody();

        Asset retrievedAsset = webTestClient
                .get()
                .uri("/assets/name/" + randomName)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Asset.class)
                .hasSize(1)
                .returnResult()
                .getResponseBody()
                .get(0);

        assertEquals(retrievedAsset.getName(), randomName);
        deleteById(insertedAsset.getId());
    }

    @Test
    @DisplayName("Delete assets")
    void deleteAssets() {

        checkAssetsNumber(3);

        Asset insertedAsset = webTestClient
                .post()
                .uri("/assets")
                .body(BodyInserters.fromProducer(Mono.just(new Asset(null,
                        BTC_ASSET_NAME,
                        1.0)), Asset.class))
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(Asset.class)
                .returnResult()
                .getResponseBody();

        checkAssetsNumber(4);

        deleteById(insertedAsset.getId());

        checkAssetsNumber(3);


    }

    @Test
    @DisplayName("Retrieve the value in USD of all my assets")
    void retrieveAllMyAssetsValue() {

        Asset firstAsset = webTestClient
                .post()
                .uri("/assets")
                .body(BodyInserters.fromProducer(Mono.just(new Asset(null,
                        BTC_ASSET_NAME,
                        1.0)), Asset.class))
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(Asset.class)
                .returnResult()
                .getResponseBody();

        Double firstValue = webTestClient
                .get()
                .uri("/assets/value/")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Double.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(firstValue);
        Asset secondAsset = webTestClient
                .post()
                .uri("/assets")
                .body(BodyInserters.fromProducer(Mono.just(new Asset(null,
                        BTC_ASSET_NAME,
                        1.0)), Asset.class))
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(Asset.class)
                .returnResult()
                .getResponseBody();

        Double secondValue = webTestClient
                .get()
                .uri("/assets/value/")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Double.class)
                .returnResult()
                .getResponseBody();

        assertTrue(secondValue > firstValue);

        deleteById(firstAsset.getId());

    }

    private void deleteById(String assetId) {
        webTestClient
                .delete()
                .uri("/assets/" + assetId)
                .exchange()
                .expectStatus()
                .isOk();
    }

    private void checkAssetsNumber(int expectedAssetsNumber) {
        webTestClient.get()
                .uri("/assets")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Asset.class)
                .hasSize(expectedAssetsNumber);
    }

}
