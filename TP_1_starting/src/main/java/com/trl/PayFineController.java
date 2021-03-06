package com.trl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.trl.Hold.HOLD_REASON;
import com.trl.exception.CopyNotFoundException;
import com.trl.exception.NoTransactionInProgress;
import com.trl.exception.TransactionAlreadyInProgress;
import com.trl.stdlib.StdIn;
import com.trl.stdlib.StdOut;

public class PayFineController extends Controller{
	private Patron patronTransacted;
	private List<Copy> copiesToCheckIn;
	private List<Textbook> prices;
	private final static Logger loggerIn = LogManager.getLogger(RentalApp.LOGGER_CHECKIN_NAME);
	
	public PayFineController(DataStore ds) {
		super(ds);
	}
	
	@Override
	public boolean startTransaction(Patron patron) throws TransactionAlreadyInProgress {
		
		if (this.patronTransacted !=null) {
			//existing transaction in progress!
			throw new TransactionAlreadyInProgress("there is already a transaction in progress.");
		}
		this.patronTransacted = patron;
		this.copiesToCheckIn = new ArrayList<Copy>();
		this.prices = new ArrayList<Textbook>();
		return true;
	}
	
	@Override
	public boolean endTransaction(Patron patron) throws NoTransactionInProgress{
		if (this.patronTransacted == null) {
			throw new NoTransactionInProgress("no transaction in progress");
		}
		this.patronTransacted=null;
		return true;
	}
	
	private BigDecimal calculateAmount(Copy c)  throws NoTransactionInProgress, CopyNotFoundException{
		//invalid transaction session.
		if (this.patronTransacted == null) {
			throw new NoTransactionInProgress("no transaction in progress");
		}
		return c.getTextbook().getPrice(); //return price
	}
	
	public void payFine() 
	{
		StdOut.println("Current Hold patron " + this.patronTransacted.getName() + " Reason- fine due for unpaid fine ");
		loggerIn.info("Current Hold patron " + this.patronTransacted.getName() + " Reason- fine due for unpaid fine ");
		StdOut.println("\nPlease enter the amount to pay"); //this amount is dummy, no amount functionality
		
		// this user input is not used; its only a mock 
		String amount = StdIn.readLine(); 
		
		//lookup for exsiting hold for patron and remove 
		for (Hold oldHold :this.patronTransacted.getPatronHolds()) 
		{
			this.patronTransacted.removeHold(oldHold); // remove the hold
			break;
		}
		StdOut.println("Hold removed from patron " + this.patronTransacted.getName());
		loggerIn.info("Hold removed from patron " + this.patronTransacted.getName());
	}
	
	
}
