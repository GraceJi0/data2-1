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

//Set log files' contents and locations
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
		logChangesFilePath = "";
		logDeleteFilePath = "";
	}
	
	//Open a window, allow users to select the location of log files and create log files
	public String saveLogFile(String fileContent)
    {
    	String logFilePath="";
    	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		Date date = new Date();
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new java.io.File("."));
		chooser.setDialogTitle("Choose location for log file");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) 
		{
			if(fileContent.contains("LogDelete"))
			{
				logFilePath += chooser.getSelectedFile()+"/logDelete"+dateFormat.format(date)+".txt";
			}
			else if(fileContent.contains("LogEdit"))
			{
				logFilePath += chooser.getSelectedFile()+"/logEdit"+dateFormat.format(date)+".txt";
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
	
	//Write the delete files' information to logDelete.txt
	public void writeToLogDeleteFile(String message)
    {
    	if(!logDeleteFilePath.equals(""))
    	{
			File logDelete = new File(logDeleteFilePath);
			try 
			{
				BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(logDelete,true));
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
				Date date = new Date();
				String logMessage = dateFormat.format(date)+"\t"+message+currentFile.getName()+"\nPath:"+currentFile.getPath()+"\n\n";
				bufferedWriter.write(logMessage);
				bufferedWriter.close();
			} 
			catch (IOException e) 
			{	
				e.printStackTrace();
			}
    	}
    }
	
	//Write the files' basic information to logEdit.txt
	public void initializelLogEditFile()
    {
    	if(!logChangesFilePath.equals(""))
    	{
    		File logEdit = new File(logChangesFilePath);
			try 
			{
				BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(logEdit,true));
				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				Date date = new Date();
				String logMessage = "\n\n"+dateFormat.format(date)+"\nEdit file: "+currentFile.getName()+"\nPath:"+currentFile.getPath();
				bufferedWriter.write(logMessage);
				bufferedWriter.close();
			} 
			catch (IOException e) 
			{	
				e.printStackTrace();
			}
    	}
    }
	
	//Write file's edit information to logEdit.txt (what changes happend to the current file)
	public void writeToLogEditFile()
	{
		if(!logChangesFilePath.equals(""))
		{
			File logEdit = new File(logChangesFilePath);
			try 
			{
				BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(logEdit,true));
				String logMessage = editFileString;
				bufferedWriter.write(logMessage);
				bufferedWriter.close();
				editFileString = "";
			} 
			catch (IOException e) 
			{	
				e.printStackTrace();
			}
		}
	}
	
	//**********************set log messages for different functions***************
	
	//Delete rows
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
	
	//Delete a columns
	public String logSelectColumn(List<String> columnList)
	{
		if(!columnList.isEmpty())
		{
			String message = "\nDelete cloumns: ";
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
	
	//Replace missing data
	public String logMissingData(String missingData, String replaceData)
	{
		if(missingData != null && replaceData != null)
		{
			String message = "\nReplace missing data \""+missingData+"\""+" with \""+replaceData+"\".";
			editFileString +=message;
		}
		return editFileString;
	}
	
	//Move the given column to the end of the file
	public String logMoveColumn(int columnIndex)
	{
		String message = "\nMove column "+columnIndex+" to the end of the file.";
		editFileString +=message;
		return editFileString;
	}
	
	//Replace all spaces in the given column
	public String logEditColumn(int columnIndex)
	{
		String message = "\nReplace all spaces in column "+ columnIndex +" with underscores";
		editFileString +=message;
		return editFileString;
	}
	
	//Delete every second element at the given row
	public String logEditHeadersFormat(int columnStart, int columnEnd, int row)
	{
		String message = "\nDelete every second cell frome column "+columnStart+" to "+columnEnd+" at row "+ row;
		editFileString +=message;
		return editFileString;
	}
	
	//Add the given text to the end of every element in the given column
	public String logAddText(int columnIndex, String text)
	{
		String message = "\nAdd \""+text+"\" to the end of every cell in column " + columnIndex;
		editFileString +=message;
		return editFileString;
	}
	
	//Save as
	public String logSaveAs(String newFileName)
	{
		String message = "\nSave as a new file: "+newFileName;
		editFileString += message;
		return editFileString;
	}
	
	//Fast convert
	public String logFastConvert(String content)
	{
		return editFileString += content;
	}
	
	public void setCurrentFile(File currentFile)
	{
		this.currentFile = currentFile;
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
