package com.jpmc.midascore.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import jakarta.transaction.Transactional;
import com.jpmc.midascore.entity.Incentive;
import org.springframework.web.client.RestTemplate;

@Service
public class TransactionProcessor {
    
    private static final Logger logger = LoggerFactory.getLogger(TransactionProcessor.class);

    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;  
    private final RestTemplate restTemplate;
    public TransactionProcessor(
        UserRepository userRepository,
      TransactionRecordRepository transactionRecordRepository,
      RestTemplate restTemplate) {
            this.userRepository = userRepository;
            this.transactionRecordRepository = transactionRecordRepository;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public boolean process(Transaction transaction) {
     //call Ince
     if(transaction==null)   return false;
     long senderId=transaction.getSenderId();
     long recipientId=transaction.getRecipientId();
     float amount=transaction.getAmount();

     UserRecord sender = userRepository.findById(senderId);
     UserRecord recipient = userRepository.findById(recipientId);

     if(sender==null || recipient==null){
    logger.debug("Invalid user");
      return false;
    }
    if(sender.getBalance()<amount){
      logger.debug("Insufficient balance");
      return false;
    }
    // Call incentive API
    Incentive incentive = restTemplate.postForObject(
        "http://localhost:8080/incentive",
        transaction,
        Incentive.class
    );

    float incentiveAmount = incentive.getAmount();

    // Update balances
    sender.setBalance(sender.getBalance() - amount);
    recipient.setBalance(recipient.getBalance() + amount + incentiveAmount);

    // Save and persist
    userRepository.save(sender);
    userRepository.save(recipient);

    // Create transaction record with incentive
    TransactionRecord record = new TransactionRecord(sender, recipient, amount);
    record.setIncentive(incentiveAmount);
    transactionRecordRepository.save(record);
    logger.debug("Proceeeds:{}->{} amount {}",senderId,recipientId,amount);
    return true;
    }
}
