package com.example.batch.config;

import com.example.batch.product.Product;
import com.example.batch.product.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BatchConfig {
    private final ProductRepository productRepository;

    @Bean
    public Job testjob(JobRepository jobRepository, Step testStep) {
        return new JobBuilder("testjob", jobRepository)
                .start(testStep)
                .build();
    }

    @Bean
    public Step testStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("testStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    List<Product> products = productRepository.findAll();

                    ObjectMapper objectMapper = new ObjectMapper();
                    //ddd
                    Path outputPath = Path.of("output", "processed-products.jsonl");
                    Files.createDirectories(outputPath.getParent());

                    int changedCount = 0;

                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath.toFile()))) {
                        for (Product product : products) {
                            if (product.getPrice() >= 500000) {
                                String oldStatus = product.getStatus();
                                product.changeStatus("DONE");
                                changedCount++;

                                Map<String, Object> result = new LinkedHashMap<>();
                                result.put("productId", product.getId());
                                result.put("name", product.getName());
                                result.put("price", product.getPrice());
                                result.put("oldStatus", oldStatus);
                                result.put("newStatus", "DONE");

                                writer.write(objectMapper.writeValueAsString(result));
                                writer.newLine();
                            }
                        }
                    }

                    productRepository.saveAll(products);

                    log.info("상태 변경 수 = {}", changedCount);

                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
