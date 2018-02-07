package project;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class LogFile 
{
	private String logDeleteFilePath;
	private String logChangesFilePath;
	private File currentFile;
	
	public LogFile(File currentFile)
	{
		this.currentFile = currentFile;
	}
	
	public void setLogFile()
    {
    		String title = "Please set the locations that you want to save the log file.";
    		JButton logDeleteBtn = new JButton("Log file that records all the deleted file.");
    		JButton logChangeBtn = new JButton("Logfile that records all the changes that happens on a file.");
    		
    		logDeleteBtn.addActionListener(new ActionListener()
    		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				String fileContent = "LogDelete.txt\nThis log file records all the files that has been deleted.\n\n";
				logDeleteFilePath = saveLogFile(fileContent);
			}
    		});
    		logChangeBtn.addActionListener(new ActionListener()
    		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				String fileContent = "LogEdit.txt\nThis log file records all the changes that happened on each file.\n\n";
				logChangesFilePath = saveLogFile(fileContent);
			}
    		});
    		
    		Object message[] = {title, logDeleteBtn, logChangeBtn};
        	Object[] closeMessage= {"Close"};
        	JOptionPane.showOptionDialog(null,message, "Set location for log files",
               JOptionPane.CLOSED_OPTION, -1, null, closeMessage, null);
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
	
	public void writeToLogFile()
    {
    		File file = new File("logDelete.txt");
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
				String logMessage = dateFormat.format(date)+"\tDelete file: "+currentFile.getName()
									+"\nPath:"+currentFile.getPath()+"\n\n";
				bufferedWriter.write(logMessage);
				bufferedWriter.close();
			} 
			catch (IOException e) 
			{	
				e.printStackTrace();
			}
    		}
    		else
    		{
    			System.out.println("======");
    		}
    }
	
	public String getLogDeleteFilePath()
	{
		return logDeleteFilePath;
	}
	
	public String LogChangesFilePath()
	{
		return logChangesFilePath;
	}
}
