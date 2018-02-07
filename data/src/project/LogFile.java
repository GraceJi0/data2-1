package project;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JFileChooser;

public class LogFile 
{
	private String logDeleteFilePath;
	private String logChangesFilePath;
	private File currentFile;
	private String editFileString;
	
	public LogFile()
	{
		currentFile = null;
		editFileString = "";
	}
	
	public void setCurrentFile(File currentFile)
	{
		this.currentFile = currentFile;
	}
	
	public String saveLogFile(String fileContent)
    {
    		String logFilePath="";
    	    JFileChooser chooser = new JFileChooser();
    	    chooser.setCurrentDirectory(new java.io.File("."));
    	    chooser.setDialogTitle("Choose location for log file");
    	    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    	    chooser.setAcceptAllFileFilterUsed(false);

    	    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) 
    	    {
    	    		if(fileContent.contains("LogDelete"))
    	    		{
    	    			logFilePath += chooser.getSelectedFile()+"/logDelete.txt";
    	    		}
    	    		else if(fileContent.contains("LogEdit"))
    	    		{
    	    			logFilePath += chooser.getSelectedFile()+"/logEdit.txt";
    	    		}
			try 
			{	
				FileWriter fw = new FileWriter(new File(logFilePath));
				fw.write(fileContent);
	            fw.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
    	    } 
    	    return logFilePath;
    }
	
	public void writeToLogDeleteFile()
    {
    		//File file = new File("logDelete.txt");
    		//if(file.exists())
    		if(!logDeleteFilePath.equals(""))
    		{
    			//System.out.println("+++++++");
			File logDelete = new File(logDeleteFilePath);
			try 
			{
				BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(logDelete,true));
				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				Date date = new Date();
				String logMessage = dateFormat.format(date)+"\tDelete file: "+currentFile.getName()+"\nPath:"+currentFile.getPath()+"\n\n";
				bufferedWriter.write(logMessage);
				bufferedWriter.close();
			} 
			catch (IOException e) 
			{	
				e.printStackTrace();
			}
    		}
    }
	
	public void writeToLogEditFile()
    {
    		//File file = new File("logDelete.txt");
    		//if(file.exists())
    		if(!logChangesFilePath.equals(""))
    		{
    			//System.out.println("+++++++");
			File logEdit = new File(logChangesFilePath);
			try 
			{
				BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(logEdit,true));
				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				Date date = new Date();
				String logMessage = dateFormat.format(date)+"\nEdit file: "+currentFile.getName()+"\nPath:"+currentFile.getPath()+"\n\n";
				bufferedWriter.write(logMessage);
				bufferedWriter.close();
			} 
			catch (IOException e) 
			{	
				e.printStackTrace();
			}
    		}
    }
	
	public String logSelectRows(List<String> rowList)
	{
		if(!rowList.isEmpty())
		{
			String message = "\nDelete rows: ";
			for(int i = 0 ; i < rowList.size();i++)
			{
				if(!rowList.get(i).isEmpty())
				{
					message+=rowList.get(i)+" ";
				}
			}
			editFileString +=message;
		}
		return editFileString;
	}
	
	public String logSelectColumn(List<String> columnList)
	{
		if(!columnList.isEmpty())
		{
			String message = "\nDelete rows: ";
			for(int i = 0 ; i < columnList.size();i++)
			{
				if(!columnList.get(i).isEmpty())
				{
					message+=columnList.get(i)+" ";
				}
			}
			editFileString +=message;
		}
		return editFileString;
	}
	
	public String logMissingData(String missingData, String replaceData)
	{
		String message = "\nReplace missing data \""+missingData+"\""+" with \""+replaceData+"\".";
		editFileString +=message;
		return editFileString;
	}
	
	public String logMoveColumn(String columnIndex)
	{
		String message = "\nMove column "+columnIndex+" to the end of the file.";
		editFileString +=message;
		return editFileString;
	}
	
	public String logEditHeaders(String rowIndex)
	{
		String message = "\nReplace all spaces in headers(row"+ rowIndex +") with underscores";
		editFileString +=message;
		return editFileString;
	}
	
	public String logEditHeadersFormat(String content)
	{
		String message = "\nEdit headers format";
		editFileString +=message;
		return editFileString;
	}
	
	public void setLogDeleteFilePath(String path)
	{
		logDeleteFilePath = path;
	}
	
	public void setLogChangesFilePath(String path)
	{
		logChangesFilePath = path;
	}
}
