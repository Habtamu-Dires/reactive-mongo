package com.example;

import com.example.controller.ProductController;
import com.example.dto.ProductDto;
import com.example.service.ProductService;
import lombok.AllArgsConstructor;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(ProductController.class)
class ReactiveMongoApplicationTests {

	@Autowired
	private WebTestClient webTestClient;

	@MockBean
	private ProductService service;

	@Test
	public void addProductTest(){
		Mono<ProductDto> productDtoMono = Mono.just(
				new ProductDto("102", "mobile", 1, 1000));
		when(service.saveProduct(productDtoMono))
				.thenReturn(productDtoMono);

		webTestClient.post().uri("/products")
				.body(Mono.just(productDtoMono), ProductDto.class)
				.exchange()
				.expectStatus().isOk();
	}

	@Test
	public void addProductsTest(){
		Flux<ProductDto> productDtoFlux = Flux.just(
				new ProductDto("102", "mobile", 1, 1000),
				new ProductDto("103", "laptop", 1, 5000)
		);
		when(service.saveProducts(productDtoFlux))
				.thenReturn(productDtoFlux);

		webTestClient.post().uri("/products/all")
				.body(Flux.just(productDtoFlux), ProductDto.class)
				.exchange()
				.expectStatus().isOk();
	}

	@Test
	public void getProductsTest(){
		Flux<ProductDto> productDtoFlux = Flux.just(
				new ProductDto("102", "mobile", 1, 1000),
				new ProductDto("103", "laptop", 1, 5000)
		);
		//mocking the api call not to touch the database
		when(service.getProducts())
				.thenReturn(productDtoFlux);

		Flux<ProductDto> responseBody = webTestClient.get().uri("/products")
				.exchange()
				.expectStatus().isOk()
				.returnResult(ProductDto.class)
				.getResponseBody();

		StepVerifier.create(responseBody)
				.expectSubscription()
				.expectNext(new ProductDto("102", "mobile", 1, 1000))
				.expectNext(new ProductDto("103", "laptop", 1, 5000))
				.verifyComplete();
	}

	@Test
	public void getProductTest(){
		Mono<ProductDto> productDtoMono = Mono.just(new ProductDto("102", "mobile", 1, 1000));

		when(service.getProduct(any()))
				.thenReturn(productDtoMono);

		Flux<ProductDto> responseBody = webTestClient.get().uri("/products/102")
				.exchange()
				.expectStatus().isOk()
				.returnResult(ProductDto.class)
				.getResponseBody();

		StepVerifier.create(responseBody)
				.expectSubscription()
				.expectNextMatches(p -> p.getName().equals("mobile"))
				.verifyComplete();
	}

	@Test
	public void updateProductTest() {
		Mono<ProductDto> productDtoMono = Mono.just(
				new ProductDto("102", "mobile", 1, 1000));

		//mock
		when(service.updateProduct(productDtoMono, "102"))
				.thenReturn(productDtoMono);

		webTestClient.put().uri("/products/update/102")
				.body(Mono.just(productDtoMono), ProductDto.class)
				.exchange()
				.expectStatus().isOk();
	}
	@Test
	public void deleteProductTest(){
		given(service.deleteProduct(any())).willReturn(Mono.empty());

		webTestClient.delete().uri("/products/delete/102")
				.exchange()
				.expectStatus().isOk();
	}

	@Test
	public void deleteProductsTest(){
		given(service.deleteProducts())
				.willReturn(Mono.empty());

		webTestClient.delete().uri("/products/delete/all")
				.exchange()
				.expectStatus().isOk();
	}
}
