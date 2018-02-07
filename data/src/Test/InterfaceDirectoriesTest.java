package Test;

import static org.junit.Assert.*;
import project.InterfaceDirectories;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class InterfaceDirectoriesTest {

	private InterfaceDirectories interfaceDirectories;
	
	@Before
	public void setUp() throws Exception 
	{
		interfaceDirectories = new InterfaceDirectories();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testFindMetaData() 
	{
		assertFalse(interfaceDirectories.findMetaData(null));
		assertFalse(interfaceDirectories.findMetaData(new File("a.txt")));
		assertFalse(interfaceDirectories.findMetaData(new File("b.txt")));
		assertFalse(interfaceDirectories.findMetaData(new File("c.txt")));
	}
	
	@Test
	public void testReadTheFile() throws Exception
	{
		//assertTrue((interfaceDirectories.readTheFile(null)).equals("<br><br>"));
		assertTrue((interfaceDirectories.readTheFile(new File("a.txt"))).equals("<br><br>"));
		assertTrue(!(interfaceDirectories.readTheFile(new File("b.txt"))).equals("<br><br>"));
		assertTrue(!(interfaceDirectories.readTheFile(new File("c.txt"))).equals("<br><br>"));
	}

}
