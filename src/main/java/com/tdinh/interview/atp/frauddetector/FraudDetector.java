package com.tdinh.interview.atp.frauddetector;

import com.tdinh.interview.atp.frauddetector.transaction.Transaction;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A fraud detector that scans a list of {@link Transaction}(s), and return
 * a collection of credit card ids where fraud has been detected.
 * 
 * @author Tuan Dinh
 *
 */
public class FraudDetector {
  
  Map<String, List<Transaction>> transactionStorage;

  /**
   * Scan a list of {@link Transaction}(s), and return
   * a set of credit card ids where fraud has been detected.
   * 
   * A credit card is identified as fraudulent if the sum amount of transactions
   * from that credit card over a 24-hour sliding window period exceeds the limit.
   * 
   * For each fraud scan, a credit card can only be detected as fraudulent once. It means
   * if a credit card has been identified as fraudulent and noted down, this method will
   * ignore all transactions from that credit card from that point onwards.
   * 
   * @param transactions
   * @param limit
   * @return
   */
  public Set<String> fraudScan(List<Transaction> transactions, double limit) {
    if (transactions.isEmpty()) {
      return Collections.emptySet();
    }
    Set<String> fraudulentCreditCards = new HashSet<>();
    transactionStorage = new HashMap<>();
    for (int i = 0; i < transactions.size(); i++) {
      Transaction trans = transactions.get(i);
      String creditCardId = trans.getCreditCardId();
      if (!fraudulentCreditCards.contains(creditCardId)) {
        List<Transaction> transList = transactionStorage.getOrDefault(creditCardId, new ArrayList<>());
        transList.add(trans);
        if (checkSingleCreditCardfraud(transList, limit)) {
          fraudulentCreditCards.add(creditCardId);
          // Remove credit card from storage, no need to check for this credit card further.
          if (transactionStorage.containsKey(creditCardId)) {
            transactionStorage.remove(creditCardId);
          }
        } else {
          transactionStorage.put(creditCardId, transList);
        }
      }
    }
    return fraudulentCreditCards;
  }

  private boolean checkSingleCreditCardfraud(List<Transaction> transList, double limit) {
    double total24hrsAmount = 0.0;
    LocalDateTime cutOff = transList.get(transList.size() - 1).getTransDateTime().minusHours(24);
    int i = transList.size() - 1;
    while (i >= 0 && transList.get(i).getTransDateTime().isAfter(cutOff)) {
      total24hrsAmount += transList.get(i).getAmount();
      if (total24hrsAmount > limit) {
        return true;
      }
      i--;
    }
    return false;
  }
}
