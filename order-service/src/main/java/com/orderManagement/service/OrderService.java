package com.orderManagement.service;

import com.orderManagement.model.Order;
import com.orderManagement.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class OrderService {
   private final KafkaTemplate<String, String> kafkaTemplate;
   private final OrderRepository orderRepository;

   private final ConcurrentHashMap<String, MonoSink<String>> orderResponseMap = new ConcurrentHashMap<>();
   private final ConcurrentHashMap<String, Order> orderMap = new ConcurrentHashMap<>();

   public Mono<String> placeOrder(Order order){
       return Mono.create(sink -> {
           //store the sink to complete it later
           orderResponseMap.put(order.getProductCode(), sink);
           orderMap.put(order.getProductCode(),order);

           //publish order request to kafka
           kafkaTemplate.send("order-topic", order.getProductCode());
       });
   }

   @KafkaListener(topics = "inventory-response-topic", groupId = "order-group")
   public void handleInventoryResponse(String message){
        String[] parts = message.split(":");
        String productCode = parts[0];
        String availabilityStatus = parts[1];

       // Find the corresponding MonoSink and complete it
       MonoSink<String> sink = orderResponseMap.get(productCode);
       if (sink != null) {
           if ("available".equals(availabilityStatus)) {
               // Retrieve the full order object from the map and save it
               Order order = orderMap.get(productCode);
               if (order != null) {
                   orderRepository.save(order);
                   sink.success("Order placed successfully");
               } else {
                   sink.error(new RuntimeException("Order not found"));
               }
           } else {
               sink.success("Product is not available");
           }
       }

   }

}
