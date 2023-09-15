package org.example.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.example.entity.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;

public class StepSkipListener implements SkipListener<Customer, Number> {

    Logger logger = LoggerFactory.getLogger(StepSkipListener.class);

    @SneakyThrows
    public void onSkipInProcess(Customer customer, Throwable t) {
        logger.info("Item {} was skipped due to exception {}", new ObjectMapper().writeValueAsString(customer), t.getMessage());
    }
}
