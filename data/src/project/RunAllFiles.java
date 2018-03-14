package project;

import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.filechooser.FileSystemView;

public class RunAllFiles {

	String result;
	FileSystemView fileSystemView;
	
	public RunAllFiles() 
	{
		result = "";
		File file = new File("/Users/dinghanji/Desktop/projectFile/noncoop_ex");
		//run(file);
		runFiles(file);
		System.out.println("end");
	}
	
	public void runFiles(File file)
	 {
		 if (file != null && file.isDirectory()) 
	     {
	           File[] files = file.listFiles();
	            for (File child : files) 
	            {
	                if (child.isDirectory()) 
	                {
		                	File[] childrenfiles = child.listFiles();
		                	for(File document : childrenfiles)
		                	{		
		                		String fileName = document.getName();
		                		if((document.getName().contains("txt")||document.getName().contains("csv")||
		                					document.getName().contains("xlsx")||document.getName().contains("xls"))
		                					&& (!document.getName().contains("metaData") && !document.getName().contains("README") && document.getName().contains(".")))
		                		{		
		                			RunableThread t2 = new RunableThread("run_files",document);
		                			t2.start();
		                			try 
		                			{
									t2.t.join(5000);
								} 
		                			catch (InterruptedException e)
		                			{
									e.printStackTrace();
								}
		                			//System.out.println(fileName);
		            				if(t2.t.isAlive())
			            			{
			                				t2.change();
			                				System.out.println(t2.getInformation());
			                				t2.interrupt();             					
			                		}
		            				else
		            				{
		            					System.out.println(t2.getInformation());
		            				}
		                				
		            			}
		                	}
	                		
	                }
	            }
	     } 
	  }
	
	 
}

class RunableThread implements Runnable
{
	public Thread t;
	private String threadName;
	private String fileName;
	private File document;
	private String information;
	EditFile editFile;
	
	public RunableThread(String newName, File file)
	{
		threadName = newName;
		document = file;
		this.fileName = file.getName();
	}
	
	@Override
	public void run() 
	{
			information ="";
			information = fileName+"\n";
			long startTime = System.nanoTime();
			 editFile = new EditFile( document);
			long endTime   = System.nanoTime();
			long totalTime = endTime - startTime;
			double seconds = (double)totalTime/1000000000.0;
			information+= "Time: "+seconds+"\n";
			information+="Size: "+document.length()+"\n\n";
	}
	
	public void change()
	{
		information = fileName+"\n";
		information+= "Time: too long\n";
		information+="Size: "+document.length()+"\n\n";
	}
	
	public void interrupt()
	{
		t.interrupt();
	}
	
	public String getInformation()
	{
		return information;
	}
	
	public void start()
	{
	    if (t == null) 
	    {
	         t = new Thread (this, threadName);
	         t.start ();
	    }
	}
}

