package com.tdinh.interview.atp.frauddetector;

import com.tdinh.interview.atp.frauddetector.transaction.Transaction;
import com.tdinh.interview.atp.frauddetector.transaction.TransactionFileLoader;
import com.tdinh.interview.atp.frauddetector.transaction.error.FileLoaderException;
import java.util.List;
import java.util.Set;

/**
 * A credit card fraud detector application.
 * <p>
 * The FraudDetectionApp loads all credit card transactions from a text file and then performs a fraud scan on those
 * transactions against an amount limit.
 * <p>
 * The FraudDetectionApp takes two inputs: first is the amount limit and the second is the path to a text file (.csv)
 * that contains transactions records in a predefined format. It then prints out to standard output the list of fraudulent
 * credit cards if there are any. It also provides some basic forms of error handling if the transaction file does not
 * exist or invalid price threshold.
 * <p>
 * The FraudDetectionApp uses {@link TransactionFileLoader} to loads the {@link Transaction} from the text file
 * and then uses {@link FraudDetector} to perform the fraud scan.
 * 
 * @author Tuan Dinh
 */
public class FraudDetectionApp {

  private TransactionFileLoader transFileLoader;
  private FraudDetector fraudDetector;

  public FraudDetectionApp() {
    this.transFileLoader = new TransactionFileLoader();
    this.fraudDetector = new FraudDetector();
  }

  public void run(String[] args) {
    if (args.length != 2) {
      System.err.println(
          "Invalid inputs. Expecting exactly 2 arguments: (i) Price threshold and (ii) path transaction record file.\nAborting...");
      return;
    }

    double limit = 0.0;
    try {
      limit = Double.parseDouble(args[0]);
    } catch (NumberFormatException ex) {
      System.err.println(
          "Invalid limit [" + args[0] + "], expecting numeric value.\nAborting...");
      return;
    }
    try {
      List<Transaction> transactions = transFileLoader.load(args[1]);
      Set<String> fraudCreditCards = fraudDetector.fraudScan(transactions, limit);

      if (fraudCreditCards.isEmpty()) {
        System.out.println("No fraud detected!");
      } else {
        System.out.println(
            "There are " + fraudCreditCards.size() + " fraudulent credit card(s):");
        fraudCreditCards.forEach(System.out::println);
      }
    } catch (FileLoaderException ex) {
      System.err.println("File error: " + ex.getMessage() + ".\nAborting...");
      return;
    }

  }

  public static void main(String[] args) {

    FraudDetectionApp app = new FraudDetectionApp();
    app.run(args);

  }
}
