package Test;

import static org.junit.Assert.*;
import project.EditFile;
import java.io.File;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EditFileTest {
	
	EditFile editFile1;
	EditFile editFile2;
	EditFile editFile3;
	EditFile editFile4;
	
	@Before
	public void setUp() throws Exception {
		editFile1 = new EditFile(null);
		editFile2 = new EditFile(new File("a.txt")); //test an empty file
		editFile3 = new EditFile(new File("b.txt")); //test a file with only one character
		editFile4 = new EditFile(new File("c.txt")); //test a simple file
	}
	
	@Test
	public void testEditTheFile()
	{
		assertTrue(editFile1.editTheFile("", null,""));
		assertFalse(editFile2.editTheFile("", null,""));
		assertFalse(editFile3.editTheFile("\t", null,""));
		assertFalse(editFile4.editTheFile("\t", null,""));
	}
	
	@Test
	public void testGetFileString()
	{
		assertTrue((editFile1.getFileString()).equals(""));
		assertTrue((editFile2.getFileString()).equals(""));
		assertTrue(!(editFile3.getFileString()).equals(""));
		assertTrue(!(editFile4.getFileString()).equals(""));
	}
	
	@Test
	public void testFileArrayToFileString()
	{
		assertTrue((editFile1.fileArrayToFileString(0)).equals(""));
		assertTrue((editFile2.fileArrayToFileString(0)).equals(""));
		assertTrue((editFile3.fileArrayToFileString(0)).equals(""));
	}
	
	@Test
	public void testReplaceMissingData()
	{
		assertTrue(editFile1.replaceMissingData());
		assertTrue(editFile2.replaceMissingData());
		assertTrue(editFile3.replaceMissingData());
	}
	
	@Test
	public void testReplaceSpaceInHeader()
	{
		assertTrue(editFile1.replaceSpaceInHeader(0));
		assertTrue(editFile2.replaceSpaceInHeader(0));
		assertTrue(editFile3.replaceSpaceInHeader(0));
	}
	
	@Test
	public void testGetMyFileExtension()
	{
		assertTrue((editFile1.getMyFileExtension()).equals(""));
		assertTrue((editFile2.getMyFileExtension()).equals("txt"));
		assertTrue((editFile3.getMyFileExtension()).equals("txt"));
		assertTrue((editFile4.getMyFileExtension()).equals("txt"));
	}
	
	@After
	public void tearDown() throws Exception{}

}
