package com.tdinh.interview.atp.frauddetector.transaction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import com.tdinh.interview.atp.frauddetector.transaction.Transaction;
import com.tdinh.interview.atp.frauddetector.transaction.TransactionResolver;
import com.tdinh.interview.atp.frauddetector.transaction.error.InvalidTransactionRecordException;
import java.time.format.DateTimeFormatter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Unit test class for {@link TransactionResolver}
 * 
 * @author Tuan Dinh
 *
 */
public class TransactionResolverTest {
  
  private TransactionResolver resolver = new TransactionResolver();

  @Rule
  public ExpectedException exceptionRule = ExpectedException.none();

  @Test
  public void testGivenValidTransTextRecordThenResolveCorrectly() throws Exception {
    String record = "10d7ce2f43e35fa57d1bbf8b1e2, 2014-04-29T13:15:54, 10.00";
    Transaction trans = resolver.resolve(record);
    assertTrue("Resolved trans should not be null", trans != null);
    assertEquals("10d7ce2f43e35fa57d1bbf8b1e2", trans.getCreditCardId());
    assertEquals("2014-04-29T13:15:54",
        trans.getTransDateTime().format(DateTimeFormatter.ofPattern(Transaction.DATE_TIME_FORMATTER)));
    assertEquals(10.00, trans.getAmount(), 0);
  }

  @Test
  public void testGivenInvalidNumberOfFieldsThenReturnError() throws Exception {
    exceptionRule.expect(InvalidTransactionRecordException.class);
    exceptionRule.expectMessage("Error: expecting 3 fields, found 4");
    String record = "10d7ce2f43e35fa57d1bbf8b1e2, unexpected data, 2014-04-29T13:15:54, 10.00";
    resolver.resolve(record);
  }

  @Test
  public void testGivenInvalidDateFormatThenReturnError() throws Exception {
    exceptionRule.expect(InvalidTransactionRecordException.class);
    exceptionRule.expectMessage("Invalid date time format [2014/04/29 13:15:54]");
    String record = "10d7ce2f43e35fa57d1bbf8b1e2, 2014/04/29 13:15:54, 10.00";
    resolver.resolve(record);
  }
  
  @Test
  public void testGivenInvalidAmountThenReturnError() throws Exception {
    exceptionRule.expect(InvalidTransactionRecordException.class);
    exceptionRule.expectMessage("Invalid amount [Ten dollars and zero cent]");
    String record = "10d7ce2f43e35fa57d1bbf8b1e2, 2014-04-29T13:15:54, Ten dollars and zero cent";
    resolver.resolve(record);
  }

}
