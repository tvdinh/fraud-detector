package com.tdinh.interview.atp.frauddetector.transaction;

import com.tdinh.interview.atp.frauddetector.transaction.error.FileLoaderException;
import com.tdinh.interview.atp.frauddetector.transaction.error.InvalidTransactionRecordException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Load transaction from a file.
 * 
 * @author Tuan Dinh
 *
 */
public class TransactionFileLoader {
  
  /**
   * Load transactions from a file given a file path.
   * Every line in the given file represents a transaction with a format defined in
   * {@link TransactionResolver}.
   * 
   * If the {@link TransactionResolver} cannot resolve the transaction (invalid line format),
   * the loader simply ignore that transaction but print out to standard output a warning.
   * 
   * @param filePath path to file.
   * @return list of {@link Transaction}.
   * @throws FileLoaderException 
   */
  public List<Transaction> load(String filePath) throws FileLoaderException {
    List<Transaction> transactions = new ArrayList<>();
    try {
      FileReader fileReader = new FileReader(filePath);
      BufferedReader bufferedReader = new BufferedReader(fileReader);
      String record;
      TransactionResolver resolver = new TransactionResolver();
      while ((record = bufferedReader.readLine()) != null) {
        try {
          transactions.add(resolver.resolve(record));
        } catch(InvalidTransactionRecordException ex) {
          System.err.println("WARN - invalid record: [" + record + "]. Reason: " + ex.getMessage() + ". Ignore");
        }
      }
      fileReader.close();
    } catch (IOException ex) {
      throw new FileLoaderException(ex.getMessage());
    }
    return transactions;
  }
}
