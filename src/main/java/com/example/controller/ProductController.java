package com.example.controller;

import com.example.dto.ProductDto;
import com.example.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductController {

    private final ProductService service;


    @GetMapping
    public Flux<ProductDto> getProducts(){
        return service.getProducts();
    }

    @GetMapping("/{id}")
    public Mono<ProductDto> getProduct(@PathVariable("id") String id){
        return service.getProduct(id);
    }

    @GetMapping("/product-range")
    public Flux<ProductDto> getProductBetweenRange(
            @RequestParam("min") double min, @RequestParam("max") double max){
        return  service.getProductInRange(min, max);
    }

    @PostMapping
    public Mono<ProductDto> saveProduct(@RequestBody Mono<ProductDto> productDtoMono){
        System.out.println("controller method called ... ");
        return service.saveProduct(productDtoMono);
    }

    @PostMapping("/all")
    public Flux<ProductDto> saveProducts(@RequestBody Flux<ProductDto> productDtoFlux){
        return service.saveProducts(productDtoFlux);
    }

    @PutMapping("/update/{id}")
    public Mono<ProductDto> updateProduct(@RequestBody Mono<ProductDto> productDtoMono, @PathVariable String id){
        return service.updateProduct(productDtoMono, id);
    }

    @DeleteMapping("delete/{id}")
    public Mono<Void> deleteProduct(@PathVariable("id") String id){
        return service.deleteProduct(id);
    }

    @DeleteMapping("delete-all")
    public Mono<Void> deleteProducts(){
        return service.deleteProducts();
    }
}