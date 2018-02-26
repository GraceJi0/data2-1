package Test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import project.FastConvert;

public class FastConvertTest {
	private FastConvert fastConvert;

	@Before
	public void setUp() throws Exception 
	{
		fastConvert = new FastConvert(null,null);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testShowConvertMessage() 
	{
		String message1 = "ERROR:\nCan't convert the file, please remove all spaces in file's name or try \"convert\" in \"file\" menu.";
		String message2 = "ERROR:\nThere're errors when converting, please check the file's format and type.";
		String message3 = "Successfull convert!";
		assertTrue(fastConvert.showConvertMessage(null).equals(message1));
		assertTrue(fastConvert.showConvertMessage("").equals(message1));
		assertTrue(fastConvert.showConvertMessage("Usage: PGDSpiderCli <options...>").equals(message1));
		assertTrue(fastConvert.showConvertMessage("Error").equals(message2));
		assertTrue(fastConvert.showConvertMessage("ERROR").equals(message2));
		assertTrue(fastConvert.showConvertMessage("done").equals(message3));
	}

}
