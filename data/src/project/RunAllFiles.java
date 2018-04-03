package project;

import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JPanel;
import javax.swing.filechooser.FileSystemView;

public class RunAllFiles {

	String result;
	FileSystemView fileSystemView;
	
	public RunAllFiles() 
	{
		result = "";
		File file = new File("/Users/dinghanji/Desktop/projectFile/noncoop_ex");
		run(file);
		//runFiles(file);
		//runAfile(new File("/Users/dinghanji/Desktop/projectFile/noncoop_ex/Geospiza/00004340-MLM_GLM_FortMag.xlsx"));
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
		                					&& (!document.getName().contains("metaData") && !document.getName().contains("README") && document.getName().contains("."))
		                					&& document.length()>0)
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
		            					if(t2.getInformation() != null)
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
	
	public void run(File file)
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
		                					&& (!document.getName().contains("metaData") && !document.getName().contains("README") && document.getName().contains("."))
		                					&& document.length()>0)
		                		{		
		                			if(fileName.equals("00004340-83Hapmap_simple2_filtered.xlsx") 
		                					|| fileName.equals("00003349-data_genotypes.csv")
		                					|| fileName.equals("00004815-UK_genotypes_PED.txt"))
		                			{
		                				System.out.println(fileName+"\nTime: run out of memory"+"\nSize: "+document.length()+"\n\n");
		                			}
		                			else
		                			{
		                			runAfile(document);	
		                			}
		            			}
		                	}
	                		
	                }
	            }
	     } 
	  }
	
	public void runAfile(File file)
	{
		EditFile editFile = null;
		String fileName = file.getName();
		String information ="";
		System.out.println("\n"+fileName);
		long startTime = System.nanoTime();
		editFile = new EditFile( file);
		editFile.editTheFile( "\t", null, null);
		long endTime   = System.nanoTime();
		long totalTime = endTime - startTime;
		double seconds = (double)totalTime/1000000000.0;
		if(seconds >= 1)
		{
			information+= "Time: "+seconds+"\n";
			information+="Size: "+file.length()+"\n\n";
			System.out.println(information);
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
			if(seconds>=1)
			{
			information+="Size: "+document.length()+"\n\n";
			}
			else
			{
				information = null;
			}
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

