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
	public void tearDown() throws Exception {
	}

	@Test
	public void testLogSelectRows()
	{
		List<String> a = new ArrayList<String>();
		a.add("row1");
		assertTrue((logFile.logSelectRows(new ArrayList<String>())).equals(""));
		assertTrue(!(logFile.logSelectRows(a)).equals(""));
	}

}
