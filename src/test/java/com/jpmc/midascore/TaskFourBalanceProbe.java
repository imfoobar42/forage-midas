package com.jpmc.midascore;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.service.TransactionProcessor;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
class TaskFourBalanceProbe {
    private static final Logger log = LoggerFactory.getLogger(TaskFourBalanceProbe.class);

    @Autowired private UserPopulator userPopulator;
    @Autowired private FileLoader fileLoader;
    @Autowired private TransactionProcessor transactionProcessor;
    @Autowired private UserRepository userRepository;

    @Test
    void computeWilburBalance() {
        userPopulator.populate();
        String[] transactionLines = fileLoader.loadStrings("/test_data/alskdjfh.fhdjsk");
        for (String transactionLine : transactionLines) {
            String[] parts = transactionLine.split(", ");
            long sender = Long.parseLong(parts[0]);
            long recipient = Long.parseLong(parts[1]);
            float amount = Float.parseFloat(parts[2]);
            transactionProcessor.process(new Transaction(sender, recipient, amount));
        }
        float wilburBalance = userRepository.findById(9L).getBalance();
        log.info("Wilbur balance (rounded down): {}", (int)Math.floor(wilburBalance));
    }
}
