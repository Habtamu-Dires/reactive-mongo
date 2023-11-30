package com.example.service;

import com.example.dto.ProductDto;
import com.example.repository.ProductRepository;
import com.example.utils.AppUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Range;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository repository;

    public Flux<ProductDto> getProducts(){
        return repository.findAll()
                .delayElements(Duration.ofSeconds(1))
                .doOnNext(product -> {
                    //log
                    System.out.println("Retrieved product: " + product);
                })
                .map(AppUtils::entityToDto);
    }

    public Mono<ProductDto> getProduct(String id){
        return repository.findById(id)
                .map(AppUtils::entityToDto);
    }

    public Flux<ProductDto> getProductInRange(double min, double max){
        return repository.findByPriceBetween(Range.closed(min,max));
    }

    public Mono<ProductDto> saveProduct(Mono<ProductDto> productDtoMono){
        System.out.println("service method called ... ");
        return productDtoMono.map(AppUtils::dtoToEntity)
                .flatMap(repository::insert)
                .map(AppUtils::entityToDto);
    }

    public Flux<ProductDto> saveProducts(Flux<ProductDto> productDtoFlux){
        return productDtoFlux.map(AppUtils::dtoToEntity)
                .delayElements(Duration.ofSeconds(5))
                .flatMap(repository::insert)
                .doOnNext(p -> {
                    System.out.println("Saving product " + p.getName());
                })
                .map(AppUtils::entityToDto);
    }

    public Mono<ProductDto> updateProduct(Mono<ProductDto> productDtoMono, String id){
        return repository.findById(id)
                .flatMap(p -> productDtoMono.map(AppUtils::dtoToEntity))
                .doOnNext(e->e.setId(id))
                .flatMap(repository::save)
                .map(AppUtils::entityToDto);
    }

    public Mono<Void> deleteProduct(String  id){
        return repository.deleteById(id);
    }

    public Mono<Void> deleteProducts() {
        return repository.deleteAll();
    }
}
