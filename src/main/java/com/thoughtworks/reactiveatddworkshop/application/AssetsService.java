package com.thoughtworks.reactiveatddworkshop.application;

import com.thoughtworks.reactiveatddworkshop.domain.Asset;
import com.thoughtworks.reactiveatddworkshop.infrastructure.AssetsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AssetsService {
    private final AssetsRepository assetsRepository;
    private final BitcoinSpotPriceService bitcoinSpotPriceService;

    @Autowired
    public AssetsService(AssetsRepository assetsRepository, BitcoinSpotPriceService bitcoinSpotPriceService) {
        this.assetsRepository = assetsRepository;
        this.bitcoinSpotPriceService = bitcoinSpotPriceService;
    }

    public Mono<Asset> createAsset(Asset asset) {
        return assetsRepository.save(asset);
    }

    public Flux<Asset> getAllAssets() {
        return assetsRepository.findAll();
    }

    public Mono<Asset> findById(String assetId) {
        return assetsRepository.findById(assetId);
    }

    public Mono<Asset> updateAsset(String assetId, Asset asset) {
        return assetsRepository.findById(assetId)
                .flatMap(dbAsset -> {
                    dbAsset.setAmount(asset.getAmount());
                    dbAsset.setName(asset.getName());
                    return assetsRepository.save(dbAsset);
                });
    }

    public Mono<Asset> deleteAsset(String assetId) {
        return assetsRepository.findById(assetId)
                .flatMap(existingAsset -> assetsRepository.delete(existingAsset)
                        .then(Mono.just(existingAsset)));
    }

    public Flux<Asset> findByAssetName(String assetName) {
        return assetsRepository.findByName(assetName);
    }

    public Mono<Double> getAssetsValue() {

        return assetsRepository
                .findAll()
                .flatMap(asset ->
                        bitcoinSpotPriceService
                                .getBitcoinSpotPrice()
                                .map(price ->
                                        price.getAmount() * asset.getAmount()))
                .reduce(Double::sum);

    }
}
