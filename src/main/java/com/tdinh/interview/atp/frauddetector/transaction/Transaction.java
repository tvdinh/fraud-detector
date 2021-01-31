package com.tdinh.interview.atp.frauddetector.transaction;

import java.time.LocalDateTime;

/**
 * A credit card transaction that includes:
 * (1) A credit card identification (a hashed value of the credit card number)
 * (2) Date and time when the transaction occurs
 * (3) Transaction amount
 * 
 * @author Tuan Dinh
 *
 */
public class Transaction {

  public static final String DATE_TIME_FORMATTER = "yyyy-MM-dd'T'HH:mm:ss";

  private String creditCardId;
  private LocalDateTime transDateTime;
  private double amount;

  public String getCreditCardId() {
    return creditCardId;
  }

  public LocalDateTime getTransDateTime() {
    return transDateTime;
  }

  public double getAmount() {
    return amount;
  }

  public static class Builder {
    private String creditCardId;
    private LocalDateTime transDateTime;
    private double amount;

    public Builder creditCardId(String creditCardId) {
      this.creditCardId = creditCardId;
      return this;
    }

    public Builder transDateTime(LocalDateTime transDateTime) {
      this.transDateTime = transDateTime;
      return this;
    }

    public Builder amount(double amount) {
      this.amount = amount;
      return this;
    }

    public Transaction build() {
      return new Transaction(this);
    }
  }

  private Transaction(Builder builder) {
    this.creditCardId = builder.creditCardId;
    this.transDateTime = builder.transDateTime;
    this.amount = builder.amount;
  }
}
