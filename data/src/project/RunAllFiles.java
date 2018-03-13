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
		                		String information = "";
		                		String fileName = document.getName();
		                		if((document.getName().contains("txt")||document.getName().contains("csv")||
		                					document.getName().contains("xlsx")||document.getName().contains("xls"))
		                					&& (!document.getName().contains("metaData") && !document.getName().contains("README") && document.getName().contains(".")))
		                		{
		                				information+=fileName+"\n";
		                				//System.out.println(fileName+"    ---");
		                				Thread t1 = new Thread("wait");
		                				Thread t2 = new Thread("run_files");
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
