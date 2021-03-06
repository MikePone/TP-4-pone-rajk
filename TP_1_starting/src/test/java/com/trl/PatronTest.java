package com.trl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PatronTest {
	private Patron patron;
	private static final String NAME="Patron1";
	private static final String PATRONID="001";
	private Copy newCopy;
	private final Date dueDate = new Date();
	
	@Before
	public void setUp()
	{
		patron= new Patron(NAME, PATRONID);

		Textbook newTextbook = new Textbook("id", new BigDecimal("40"), "ISBN", "author", "title", "edition");
		newCopy = new Copy("cid", newTextbook);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testPatron() {
		assertNotNull(patron); 
	}

	@Test
	public void testGetName() {
		assertEquals(NAME, patron.getName());
	}

	@Test(expected=IllegalArgumentException.class)
	public void testGetNullName() {
		patron= new Patron(null, PATRONID);
	}

	@Test
	public void testCheckOut() {
		patron.checkCopyOut(newCopy, dueDate);
		assertTrue(patron.hasCopyCheckedOut(newCopy));
		assertEquals(dueDate, newCopy.getDueDate());
	}
	
	@Test
	public void testCheckIn() {
		patron.checkCopyOut(newCopy, dueDate);
		assertTrue(patron.hasCopyCheckedOut(newCopy));
		assertEquals(dueDate, newCopy.getDueDate());
		patron.checkCopyIn(newCopy);
		assertFalse(patron.hasCopyCheckedOut(newCopy));
		assertNull(newCopy.getDueDate());
	}
	
}
