package com.trl;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.trl.Hold.HOLD_REASON;
import com.trl.exception.CopyNotFoundException;
import com.trl.exception.HasHoldsException;
import com.trl.exception.NoTransactionInProgress;
import com.trl.exception.TransactionAlreadyInProgress;

public class CheckOutControllerTest 
{
	private CheckOutController checkOutController;
	private Patron patronTransacted;
	private Hold hold;
	private final static DataStore ds = new DataStore();
	private final Patron patronHolds = ds.getPatron("008"); 
	private RentalAppViewStub view;
	
	@Before
	public void setUp() throws Exception 
	{
		view = new RentalAppViewStub();
		checkOutController = new CheckOutController(ds, view);
		patronTransacted= new Patron("n", "id");
		Copy copy = new Copy("001", new Textbook("id", new BigDecimal("1"), "ISBN", "author", "title", "edition"));
		hold= new Hold(copy, patronTransacted, HOLD_REASON.OverdueBook);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testStartTransaction() throws Exception {
		assertTrue(checkOutController.startTransaction(patronTransacted) ); 
	}

	@Test(expected=HasHoldsException.class)
	public void testStartHoldTransaction() throws Exception {
			patronTransacted.addHold(hold);
			checkOutController.startTransaction(patronTransacted);
			fail();
	}
	

	@Test(expected=TransactionAlreadyInProgress.class)
	public void testStart2Transactions() throws Exception {
		checkOutController.startTransaction(patronTransacted);
		checkOutController.startTransaction(patronTransacted);
		fail();
	}

	@Test
	public void testEndTransaction()  throws Exception{
		assertTrue(checkOutController.startTransaction(patronTransacted));
		assertTrue(checkOutController.endTransaction(patronTransacted));
	}

	@Test(expected=NoTransactionInProgress.class)
	public void testEnd2Transaction() throws Exception{
		assertTrue(checkOutController.startTransaction(patronTransacted));
		assertTrue(checkOutController.endTransaction(patronTransacted));
		assertTrue(checkOutController.endTransaction(patronTransacted));
	 
	}
	
	@Test
	public void testCheckOut() throws Exception {
		view.addInputString("Copy1");
		view.addInputString("N");
		assertTrue(checkOutController.startTransaction(patronTransacted)); 
		checkOutController.checkOutBooks();
		assertTrue(checkOutController.endTransaction(patronTransacted)); 
		assertEquals(2, view.getOutputs().size());
	}

	@Test(expected=HasHoldsException.class)
	public void testCheckOutWithHolds() throws Exception {
		view.addInputString("Copy1");
		view.addInputString("N");
		assertTrue(checkOutController.startTransaction(patronHolds)); 
		checkOutController.checkOutBooks();
		fail();
	}
	@Test 
	public void testCheckOutBadCopy() throws Exception {
		view.addInputString("CopyBad");
		view.addInputString("N");
		assertTrue(checkOutController.startTransaction(patronTransacted)); 
		checkOutController.checkOutBooks();
		assertEquals(3, view.getOutputs().size());
		assertEquals("copyID CopyBad not found!", view.getOutputs().get(1));
	}
}
