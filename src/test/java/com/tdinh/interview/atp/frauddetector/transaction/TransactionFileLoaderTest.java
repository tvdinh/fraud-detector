package com.tdinh.interview.atp.frauddetector.transaction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import com.tdinh.interview.atp.frauddetector.transaction.Transaction;
import com.tdinh.interview.atp.frauddetector.transaction.TransactionFileLoader;
import com.tdinh.interview.atp.frauddetector.transaction.error.FileLoaderException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Unit test class for {@link TransactionFileLoader}
 * @author Tuan Dinh
 *
 */
public class TransactionFileLoaderTest {

  private TransactionFileLoader loader = new TransactionFileLoader();
  
  @Rule
  public ExpectedException exceptionRule = ExpectedException.none();
  
  @Test
  public void testGivenFileWithValidRecordsThenLoadCorrectlyAndInOrder() throws FileLoaderException {
    String filePath = "src/test/resources/transactions_3.csv";
    List<Transaction> transactions = loader.load(filePath);
    assertEquals("Should resolve in 3 transactions", 3, transactions.size());
    assertTransaction("10d7ce2f43e35fa57d1bbf8b1e2", "2014-04-29T13:15:54", 10.00, transactions.get(0));
    assertTransaction("f2f1ee2840d23ed1430cbec83c6", "2014-04-29T13:17:19", 15.00, transactions.get(1));
    assertTransaction("1f409e4283ad6375bf5d4e9372d", "2014-04-29T16:15:54", 17.00, transactions.get(2));
  }
  
  @Test
  public void testGivenFileWithSomeInvalidRecordsThenLoadValidTransaction() throws FileLoaderException {
    String filePath = "src/test/resources/transactions_5_2_invalid.csv";
    List<Transaction> transactions = loader.load(filePath);
    assertEquals("Should resolve in 3 transactions", 3, transactions.size());
    assertTransaction("10d7ce2f43e35fa57d1bbf8b1e2", "2014-04-29T13:15:54", 10.67, transactions.get(0));
    assertTransaction("f2f1ee2840d23ed1430cbec83c6", "2014-04-29T13:17:19", 15.12, transactions.get(1));
    assertTransaction("1f409e4283ad6375bf5d4e9372d", "2014-04-29T16:15:54", 17.66, transactions.get(2));
  }
  
  @Test
  public void testGivenFileEmptyRecordsThenLoadEmptyList() throws FileLoaderException {
    String filePath = "src/test/resources/transactions_empty.csv";
    List<Transaction> transactions = loader.load(filePath);
    assertEquals("Should be empty", 0, transactions.size());
  }
  
  @Test
  public void testGivenNonExistFileThenReturnError() throws FileLoaderException {
    exceptionRule.expect(FileLoaderException.class);
    exceptionRule.expectMessage("non/exist/file (No such file or directory)");
    String filePath = "non/exist/file";
    loader.load(filePath);
  }

  private void assertTransaction(String creditCardId, String dateTime, double amount, Transaction trans) {
    assertTrue("Resolved trans should not be null", trans != null);
    assertEquals(creditCardId, trans.getCreditCardId());
    assertEquals(dateTime,
        trans.getTransDateTime().format(DateTimeFormatter.ofPattern(Transaction.DATE_TIME_FORMATTER)));
    assertEquals(amount, trans.getAmount(), 0);
  }
  
}
