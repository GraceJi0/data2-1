package Test;
import project.LogFile;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LogFileTest {
	
	private static LogFile logFile;

	@Before
	public void setUp() throws Exception 
	{
		logFile = new LogFile();
	}

	@After
	public void tearDown() throws Exception {}

	@Test
	public void testLogSelectRows()
	{
		List<String> a = new ArrayList<String>();
		a.add("row1");
		assertTrue((logFile.logSelectRows(new ArrayList<String>())).equals(""));
		assertTrue(!(logFile.logSelectRows(a)).equals(""));
	}
	
	@Test
	public void testLogSelectColumns()
	{
		List<String> a = new ArrayList<String>();
		a.add("column1");
		assertTrue((logFile.logSelectRows(new ArrayList<String>())).equals(""));
		assertTrue(!(logFile.logSelectRows(a)).equals(""));
	}
	
	@Test
	public void testLogMissingData()
	{
		assertTrue((logFile.logMissingData(null, null)).equals(""));
		assertTrue(!(logFile.logMissingData("", "")).equals(""));
		assertTrue(!(logFile.logMissingData("0", "NA")).equals(""));
	}
	
	@Test
	public void testLogMoveColumn()
	{
		assertTrue(!(logFile.logMoveColumn(0)).equals(""));
	}
	
	@Test
	public void testLogEditHeaders()
	{
		assertTrue(!(logFile.logEditHeaders(0)).equals(""));
	}
}
