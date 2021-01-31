package com.tdinh.interview.atp.frauddetector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Integration test for the Fraud Detection Application.
 * @see {@link FraudDetectionApp}
 * 
 * @author Tuan Dinh
 *
 */
public class FraudDetectionAppTest {
  
  FraudDetectionApp app = new FraudDetectionApp();
  
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;

  @Before
  public void setUpStreams() {
      System.setOut(new PrintStream(outContent));
      System.setErr(new PrintStream(errContent));
  }

  @After
  public void restoreStreams() {
      System.setOut(originalOut);
      System.setErr(originalErr);
  }

  /**
   * Input:
   * threshold = 35.00
   * transactions_10_nofraud.csv contains 10 transactions with no fraud.
   * 
   * Expect return empty fraud set.
   */
  @Test
  public void testGivenNoFraudThenReturnEmptyFraudSet() throws Exception {
    String filePath = "src/test/resources/transactions_10_nofraud.csv";
    app.run(new String[] {"35.00", filePath});
    assertEquals("No fraud detected!\n", outContent.toString());
  }
  
  /**
   * Input:
   * threshold = 35.00
   * transactions_10.csv contains 10 valid records from 3 credit cards
   * with one fraud.
   * 
   * Expect return correct fraudulent credit card.
   */
  @Test
  public void testGivenTransactionWithFraudThenDetectCorrectlyExpectedFraud() throws Exception {
    String filePath = "src/test/resources/transactions_10.csv";
    app.run(new String[] {"35.00", filePath});
    assertTrue(outContent.toString().contains("There are 1 fraudulent credit card(s):"));
    assertTrue(outContent.toString().contains("1f409e4283ad6375bf5d4e9372d"));
  }
  
  /**
   * Input:
   * threshold = 35.00
   * transactions_15_5_invalid.csv contains 15 records with 5 in invalid format.
   * 
   * Expect those invalid records are ignored and fraud detection works on the remaining
   * valid records.
   */
  @Test
  public void testGivenSomeInvalidRecordsThenStillScanValidRecords() throws Exception {
    String filePath = "src/test/resources/transactions_15_5_invalid.csv";
    app.run(new String[] {"35.00", filePath});
    assertTrue(outContent.toString().contains("There are 1 fraudulent credit card(s):"));
    assertTrue(outContent.toString().contains("1f409e4283ad6375bf5d4e9372d"));
  }
  
  @Test
  public void testGivenNonExistFileThenHandleError() throws Exception {
    String filePath = "non/exist/file";
    app.run(new String[] {"35.00", filePath});
    assertEquals("File error: non/exist/file (No such file or directory).\n" + 
        "Aborting...\n", errContent.toString());
  }
  
  @Test
  public void testGivenInvalidPriceThresholdThenHandleError() throws Exception {
    String filePath = "src/test/resources/transactions_10_nofraud.csv";
    app.run(new String[] {"Thirty-five dollars", filePath});
    assertEquals("Invalid limit [Thirty-five dollars], expecting numeric value.\n" + 
        "Aborting...\n", errContent.toString());
  }
  
}
