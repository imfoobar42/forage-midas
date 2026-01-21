package com.jpmc.midascore.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class TransactionRecord { //Audit Trail 
    @Id
    @jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private long id;

    @ManyToOne(optional = false)
    private UserRecord sender;

    @ManyToOne(optional = false)
    private UserRecord recipient;

    @Column(nullable = false)
    private float amount;

    private float incentive;
    protected TransactionRecord(){
    }
    public TransactionRecord(
        UserRecord sender,
        UserRecord recipient,
        float amount){
            this.sender = sender;
            this.recipient = recipient;
            this.amount = amount;
    }
    public long getId() {
        return id;
    }
    public UserRecord getSender() {
        return sender;
    }
    public UserRecord getRecipient() {
        return recipient;       
    }
    public float getAmount() {
        return amount;
    }
    public void setIncentive(float incentive){
        this.incentive= incentive;
    }

    public float getIncentive() {
        return incentive;
    }
}
