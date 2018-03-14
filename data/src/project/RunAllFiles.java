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
		                				//System.out.println(fileName+"    ---");
		                				RunableThread t2 = new RunableThread("run_files",document);
		                				t2.start();
		                				try 
		                				{
											Thread.sleep(5000);
									} 
		                				catch (InterruptedException e) 
		                				{
											e.printStackTrace();
									}
		                				if(t2.t.isAlive())
		                				{
		                					t2.change();
		                					t2.stopThread();
		                				}
		                				System.out.println(t2.getInformation());
				                		
		            			}
		                	}
	                		
	                }
	            }
	     } 
	  }
	
	/* public void run(File file)
	 {
		 if (file != null && file.isDirectory()) 
	     {
	            File[] files = file.listFiles();
	            for (File child : files) 
	            {
	                if (child.isDirectory()) 
	                {
	                		if(!child.getName().equals("Geospiza") && !child.getName().equals("Hirundo%20rustica") )
	                		{
		                		File[] childrenfiles = child.listFiles();
		                		for(File document : childrenfiles)
		                		{
		                			
		                			String information = "";
		                			String fileName = document.getName();
		                			if((document.getName().contains("txt")||document.getName().contains("csv")||
		                					document.getName().contains("xlsx")||document.getName().contains("xls"))
		                					&& (!document.getName().contains("metaData") && !document.getName().contains("README") && document.getName().contains(".")))
		                			{
		                				information+=fileName+"\n";
		                				if(fileName.equals("00004340-83Hapmap_simple2_filtered.xlsx") 
		                						|| fileName.equals("00001958-2016_06_27 MalesSpecMorph_museum.xlsx")
		                						|| fileName.equals("00003053-GeneticsPedigree_Publication.xlsx")
		                						|| fileName.equals("00003349-data_genotypes.csv")
		                						|| fileName.equals("00004815-UK_genotypes_PED.txt"))
		                				{
		                					information+= "Time: too long\n";
				                			information+="Size: "+document.length()+"\n\n";
		                				}
		                				else
		                				{
		                					System.out.println(fileName+"    ---");
				                			long startTime = System.nanoTime();
				                			EditFile editFile = new EditFile( document);
				                			long endTime   = System.nanoTime();
				                			long totalTime = endTime - startTime;
				                			double seconds = (double)totalTime/1000000000.0;
				                			information+= "Time: "+seconds+"\n";
				                			information+="Size: "+document.length()+"\n\n";
		                				}
		                				result+=information;
			                			System.out.println(information);
		                			}
		                		}
	                		}
	                }
	            }
	     }
		 
	  }*/
}

class RunableThread implements Runnable
{
	public Thread t;
	private String threadName;
	private String fileName;
	private File document;
	private String information;
	
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
		EditFile editFile = new EditFile( document);
		long endTime   = System.nanoTime();
		long totalTime = endTime - startTime;
		double seconds = (double)totalTime/1000000000.0;
		information+= "Time: "+seconds+"\n";
		information+="Size: "+document.length()+"\n\n";
	}
	
	public void change()
	{
		information+= "Time: too long\n";
		information+="Size: "+document.length()+"\n\n";
	}
	
	public void stopThread()
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
