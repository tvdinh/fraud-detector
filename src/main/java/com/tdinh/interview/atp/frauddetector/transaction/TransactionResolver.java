package com.tdinh.interview.atp.frauddetector.transaction;

import com.tdinh.interview.atp.frauddetector.transaction.error.InvalidTransactionRecordException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Resolve a transaction text record to {@link Transaction}
 * 
 * Expecting format for the transaction text record:
 * <hashed_credit_card_number>, <date_time>, <transaction_amount> 
 * 
 * where:
 * - hashed_credit_card_number is a string
 * - <date_time> is in ISO-8061 date time format without a timezone (e.g: 2014-04-29T13:15:54)
 * - transaction amount can be converted to a decimal value.
 * 
 * Otherwise, throw {@link InvalidTransactionRecordException}.
 * 
 * @author Tuan Dinh
 *
 */
public class TransactionResolver {

  private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(Transaction.DATE_TIME_FORMATTER);

  /**
   * Resolve a {@link Transaction} from a transaction text record.
   * 
   * @param text record
   * @return a {@link Transaction}
   * @throws InvalidTransactionRecordException 
   */
  public Transaction resolve(String record) throws InvalidTransactionRecordException {
    String[] fields = record.split(",");
    if (fields.length != 3) {
      throw new InvalidTransactionRecordException("Error: expecting 3 fields, found " + fields.length);
    }
    String creditCardId = fields[0].trim();
    LocalDateTime transDateTime = resolveTranDateTime(fields[1].trim());
    double amount = resolveAmount(fields[2].trim());
    return new Transaction.Builder()
        .creditCardId(creditCardId)
        .transDateTime(transDateTime)
        .amount(amount)
        .build();
  }

  private double resolveAmount(String amount) throws InvalidTransactionRecordException {
    try {
      return Double.parseDouble(amount);
    } catch (NumberFormatException | NullPointerException ex) {
      throw new InvalidTransactionRecordException("Invalid amount [" + amount + "]");
    }
  }

  private LocalDateTime resolveTranDateTime(String dateTime) throws InvalidTransactionRecordException {
    try {
      return LocalDateTime.parse(dateTime, dateTimeFormatter);
    } catch (DateTimeParseException ex) {
      throw new InvalidTransactionRecordException("Invalid date time format [" + dateTime + "]");
    }
  }
}
