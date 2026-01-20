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

@Service
public class TransactionProcessor {
    
    private static final Logger logger = LoggerFactory.getLogger(TransactionProcessor.class);

    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;  

    public TransactionProcessor(
        UserRepository userRepository,
        TransactionRecordRepository transactionRecordRepository) {
            this.userRepository = userRepository;
            this.transactionRecordRepository = transactionRecordRepository;
    }

    @Transactional
    public boolean process(Transaction transaction) {
        // Implementation goes here
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
    sender.setBalance(sender.getBalance()-amount);
    recipient.setBalance(recipient.getBalance()+amount);
    userRepository.save(sender);
    userRepository.save(recipient); 

    //persist transaction record
    TransactionRecord record = new TransactionRecord(
        sender,
        recipient,
        amount
    );
    transactionRecordRepository.save(record);
    logger.debug("Proceeeds:{}->{} amount {}",senderId,recipientId,amount);
    return true;
    }
}
