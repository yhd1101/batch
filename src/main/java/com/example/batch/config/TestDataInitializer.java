package com.example.batch.config;

import com.example.batch.product.Product;
import com.example.batch.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestDataInitializer  implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) {
            return;
        }

        for (int i = 1; i <= 10000; i++) {
            productRepository.save(
                    new Product("product-" + i, i * 100, "READY")
            );
        }
    }
}
