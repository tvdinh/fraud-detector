package com.tdinh.interview.atp.frauddetector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import com.tdinh.interview.atp.frauddetector.FraudDetector;
import com.tdinh.interview.atp.frauddetector.transaction.Transaction;
import com.tdinh.interview.atp.frauddetector.transaction.TransactionResolver;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.Test;

/**
 * Unit test class for {@link FraudDetector}.
 * @author Tuan Dinh
 *
 */
public class FraudDetectorTest {

  private FraudDetector fraudDetector = new FraudDetector();
  private TransactionResolver transactionResolver = new TransactionResolver();
  
  @Test
  public void testGivenNoFraudTransactionThenReturnEmptyFraudList() throws Exception {
    List<Transaction> transactions =
        Arrays.asList(
            transactionResolver.resolve("10d7ce2f43e35fa57d1bbf8b1e2, 2014-04-29T13:15:54, 10.00"),
            transactionResolver.resolve("10d7ce2f43e35fa57d1bbf8b1e2, 2014-04-29T17:15:54, 20.00"),
            transactionResolver.resolve("10d7ce2f43e35fa57d1bbf8b1e2, 2014-04-30T13:15:54, 10.00"));
    Set<String> fraudCreditCards = fraudDetector.fraudScan(transactions, 35.00);
    assertTrue(fraudCreditCards.isEmpty());
  }
  
  @Test
  public void testGivenFraudTransactionThenReturnFraudCreditCard() throws Exception {
    List<Transaction> transactions =
        Arrays.asList(
            transactionResolver.resolve("10d7ce2f43e35fa57d1bbf8b1e2, 2014-04-27T17:15:54, 30.00"),
            transactionResolver.resolve("10d7ce2f43e35fa57d1bbf8b1e2, 2014-04-28T18:15:54, 30.00"),
            transactionResolver.resolve("10d7ce2f43e35fa57d1bbf8b1e2, 2014-04-29T13:15:54, 10.00"),
            transactionResolver.resolve("10d7ce2f43e35fa57d1bbf8b1e2, 2014-04-29T17:15:54, 20.00"),
            transactionResolver.resolve("10d7ce2f43e35fa57d1bbf8b1e2, 2014-04-30T13:15:53, 30.00"));
    Set<String> fraudCreditCards = fraudDetector.fraudScan(transactions, 35.00);
    assertEquals(1, fraudCreditCards.size());
    assertTrue(fraudCreditCards.contains("10d7ce2f43e35fa57d1bbf8b1e2"));
  }
  
  @Test
  public void testGivenMixedFraudNonFraudThenReturnCorrectFraudCreditCards() throws Exception {
    List<Transaction> transactions =
        //10d7ce2f43e35fa57d1bbf8b1e2 and 1f409e4283ad6375bf5d4e9372d are fraudulent,
        //f2f1ee2840d23ed1430cbec83c6 total amount exceeds limit(35.00) but spend over 2 days, hence not a fraud.
        Arrays.asList(
            transactionResolver.resolve("10d7ce2f43e35fa57d1bbf8b1e2, 2014-04-29T13:15:54, 10.00"),
            transactionResolver.resolve("f2f1ee2840d23ed1430cbec83c6, 2014-04-29T15:17:19, 15.12"),
            transactionResolver.resolve("10d7ce2f43e35fa57d1bbf8b1e2, 2014-04-29T17:15:54, 20.00"),
            transactionResolver.resolve("10d7ce2f43e35fa57d1bbf8b1e2, 2014-04-30T13:15:53, 30.00"),
            transactionResolver.resolve("1f409e4283ad6375bf5d4e9372d, 2014-04-30T14:00:15, 75.66"),
            transactionResolver.resolve("1f409e4283ad6375bf5d4e9372d, 2014-04-30T16:15:54, 60.66"),
            transactionResolver.resolve("f2f1ee2840d23ed1430cbec83c6, 2014-04-30T17:15:54, 30.15"));
    Set<String> fraudCreditCards = fraudDetector.fraudScan(transactions, 35.00);
    assertEquals(2, fraudCreditCards.size());
    assertTrue(
        fraudCreditCards.containsAll(
            Arrays.asList("10d7ce2f43e35fa57d1bbf8b1e2", "1f409e4283ad6375bf5d4e9372d")));
  }
  
  @Test
  public void testGivenEmptyTransactionListThenReturnEmptyFraudulentCreditCard() throws Exception {
    List<Transaction> transactions = Collections.emptyList();
    assertEquals(0, fraudDetector.fraudScan(transactions, 35.00).size());
  }
  
  @Test
  public void testGivenLowThresholdLimitThenReturnCorrectFraudCreditCards() throws Exception {
    List<Transaction> transactions =
        Arrays.asList(
            transactionResolver.resolve("10d7ce2f43e35fa57d1bbf8b1e2, 2014-04-29T13:15:54, 5.00"),
            transactionResolver.resolve("f2f1ee2840d23ed1430cbec83c6, 2014-04-29T17:15:54, 7.00"),
            transactionResolver.resolve("1f409e4283ad6375bf5d4e9372d, 2014-04-30T13:15:54, 15.00"));
    Set<String> fraudCreditCards = fraudDetector.fraudScan(transactions, 2.00);
    assertEquals(3, fraudCreditCards.size());
    assertTrue(fraudCreditCards.containsAll(
        Arrays.asList("10d7ce2f43e35fa57d1bbf8b1e2", "1f409e4283ad6375bf5d4e9372d", "f2f1ee2840d23ed1430cbec83c6")));
  }
  
}
