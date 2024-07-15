package com.orderManagement.service;

import com.orderManagement.model.Order;
import com.orderManagement.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    public Mono<String> placeOrder(Order order){
        return webClientBuilder.build()
                .get()
                .uri("http://localhost:8081/api/inventory/" + order.getProductCode())
                .retrieve()
                .bodyToMono(Boolean.class)
                .flatMap(isAvailable -> {
                    if(Boolean.TRUE.equals(isAvailable)){
                        orderRepository.save(order);
                        return Mono.just("Order placed successfully");
                    } else {
                        return Mono.just("Product is not available");
                    }
                });
    }

}
