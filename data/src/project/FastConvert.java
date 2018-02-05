package project;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

public class FastConvert 
{
	private File currentFile;
	
	public FastConvert(File convertFile)
	{
		currentFile = convertFile;
	}
	
	public void fastConvertDialog() 
	{
		String fileName =  currentFile.getName();
	    Object[] message = {"Convert: "+fileName, "From STRUCTURE to GENEPOP\n\n\n", 
	    		"For more file format options, please go to the \"convert\" in File menu",};
	    int option = JOptionPane.showConfirmDialog(null, message, "Fast convert", JOptionPane.OK_CANCEL_OPTION);
	    if(option == 0)
	    {	
	    		runPGDSpider();
	    }
	}
	
	public void runPGDSpider()
	{
		//String command = "java -Xmx1024m -Xms512m -jar /Users/dinghanji/Downloads/PGDSpider_2.1.1.3/PGDSpider2-cli.jar";
		String command = "java -Xmx1024m -Xms512m -jar /Users/dinghanji/Downloads/PGDSpider_2.1.1.3/PGDSpider2.jar";
		try 
		{
			Runtime.getRuntime().exec(command);
		} 
		catch (IOException e) 
		{
			JOptionPane.showConfirmDialog(null,"Can't open PGDSpider!", "Error", JOptionPane.CLOSED_OPTION);
		}
	}
	
	public void successConvertDialog()
	{
		JOptionPane.showConfirmDialog(null,"Successfull convert!", "Fast convert", JOptionPane.CLOSED_OPTION);
	}
}