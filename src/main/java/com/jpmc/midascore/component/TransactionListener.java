package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TransactionListener {

    private static final Logger logger = LoggerFactory.getLogger(TransactionListener.class);

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core-consumer")
    public void listen(Transaction transaction) {
        logger.info("Received transaction: {}", transaction);
    }
}
