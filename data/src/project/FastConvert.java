package project;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.JOptionPane;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JTextField;


public class FastConvert 
{
	private File currentFile;
	private List<List<String>> fileArray;
	private int markerNumInt;
	private String fastConvertLogString;
	
	public FastConvert(File convertFile, List<List<String>> fileArray)
	{
		currentFile = convertFile;
		markerNumInt = -1;
		fastConvertLogString = "";
	}
	
	//set the fast convert dialog and allow users to input the missing value
	public void fastConvertDialog() 
	{
		String fileName =  currentFile.getName();
		JTextField missingValueInput = new JTextField();
		JTextField marksNumInput = new JTextField();
	    Object[] message = {"Convert: "+fileName, "From STRUCTURE to GENEPOP","\nEnter the missing value(default is -9):",
	    		missingValueInput,"Enter the number of markers (loci) listed in the file:",marksNumInput,"\n\n", 
	    		"(Fast convert will generate a spid file automatically.)\nFor more file format options, please go to the \"convert\" in File menu"};
	    int option = JOptionPane.showConfirmDialog(null, message, "Fast convert", JOptionPane.OK_CANCEL_OPTION);
	    if(option == 0)
	    {	
	    		runPGDSpider(missingValueInput.getText(), marksNumInput.getText());
	    }
	}
	
	//run PGDSpider using command line, creat spid file and output file for converting, if convert is not successful, 
	//delete the output file and spid file.
	public void runPGDSpider(String missingValue, String markerNum)
	{
		String inputPath = currentFile.getAbsolutePath();
		String outputPath = currentFile.getParentFile().getAbsolutePath()+"/GENEPOP"+currentFile.getName();
		String spidPath = currentFile.getParentFile().getAbsolutePath()+"/PGDSpiderSpidFile.spid";
		/*int option = checkFileFormat();
		if(markerNumInt == -1)
		{
			markerNum = "";
		}
		else
		{
			markerNum =  Integer.toString(markerNumInt);
		}*/
		creatSpidFile(missingValue, markerNum,spidPath);
		String commandFastConvert = "java -Xmx1024m -Xms512m -jar /Users/dinghanji/Downloads/PGDSpider_2.1.1.3/PGDSpider2-cli.jar "+
									"-inputfile "+ inputPath + " -inputformat STRUCTURE -outputfile "+ outputPath +
									" -outputformat GENEPOP -spid " + spidPath;
		/*String commandFastConvert = "java -Xmx1024m -Xms512m -jar /Users/dinghanji/Downloads/PGDSpider_2.1.1.3/PGDSpider2-cli.jar "
				+ "-inputfile /Users/dinghanji/Desktop/test1.txt -inputformat STRUCTURE -outputfile /Users/dinghanji/Desktop/test2.txt "
				+ "-outputformat GENEPOP -spid /Users/dinghanji/Desktop/mine.spid";*/
		try 
		{
			Process pros = Runtime.getRuntime().exec(commandFastConvert);
			InputStream in = pros.getInputStream();
			InputStream err = pros.getErrorStream();
			String result = readInputStream(in);
			String resultMessage = readInputStream(err);
			JOptionPane.showConfirmDialog(null,result+resultMessage, "PGDSpider", JOptionPane.CLOSED_OPTION);
			if(result.contains("ERROR"))
			{
				File spidFile = new File(spidPath);
				spidFile.delete();
				File output = new File(outputPath);
				output.delete();
			}
			else
			{
				setfastConvertLog();
			}
		} 
		catch (IOException e) 
		{
			JOptionPane.showConfirmDialog(null,"Can't open PGDSpider!", "Error", JOptionPane.CLOSED_OPTION);
		}
	}
	
	public void runDetailConvert()
	{
		String inputPath = currentFile.getAbsolutePath();
		String commandLine = "java -Xmx1024m -Xms512m -jar /Users/dinghanji/Downloads/PGDSpider_2.1.1.3/PGDSpider2.jar";
		try 
		{
			Process pros = Runtime.getRuntime().exec(commandLine);
		} 
		catch (IOException e) 
		{
			JOptionPane.showConfirmDialog(null,"Can't open PGDSpider!", "Error", JOptionPane.CLOSED_OPTION);
		}
	}
	
	//read the log message after convert.
	public String readInputStream(InputStream stream)
	{
		BufferedReader text = new BufferedReader(new InputStreamReader(stream));
		String message = "";
		try 
		{
			String line = text.readLine();
			while(line != null)
			{
				message += line+"\n";
				line = text.readLine();
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return message;
	}
	
	//create a spid file.
	public String creatSpidFile(String missingValue, String marksNum, String spidPath)
	{
		String file = "";
		if(missingValue == null || missingValue.equals(""))
		{
			missingValue = "-9";
		}
		DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
		Date date = new Date();
		file += "# spid-file generated: "+dateFormat.format(date)+"\n"+
				"# STRUCTURE Parser questions\n"+
				"PARSER_FORMAT=STRUCTURE\n\n"+
				"# What is the ploidy of the data?\n"+
				"STRUCTURE_PARSER_PLOIDY_QUESTION=DIPLOID_ONE_ROW\n"+
				"# Select the type of the data:\n"+
				"STRUCTURE_PARSER_DATA_TYPE_QUESTION=MICROSAT\n"+
				"# Enter the number of markers (loci) listed in the input file:\n"+
				//"STRUCTURE_PARSER_NUMBER_LOCI_QUESTION="+marksNum+"\n"+ ////////////////////not working if I remove "20"
				"STRUCTURE_PARSER_NUMBER_LOCI_QUESTION=20\n"+
				"# Are marker (locus) names included?\n" + 
				"STRUCTURE_PARSER_LOCI_NAMES_QUESTION=false\n"+
				"# How are Microsat alleles coded?\n" + 
				"STRUCTURE_PARSER_MICROSAT_CODING_QUESTION=REPEATS\n"+
				"# Enter the size of the repeated motif (same for all loci: one number; different: comma separated list (e.g.: 2,2,3,2):\n" + 
				"STRUCTURE_PARSER_REPEAT_SIZE_QUESTION=\n" + 
				"# What is the missing value code (-9, -999, ...):\n" + 
				"STRUCTURE_PARSER_MISSING_CODE_QUESTION="+missingValue+"\n"+
				"# Is the \"PopData\" column (population identifier) present in the input file?\n" + 
				"STRUCTURE_PARSER_POP_DATA_PRESENT_QUESTION=true\n"+
				"# Is the \"Phase Information\" row present?\n" + 
				"STRUCTURE_PARSER_PHASE_ROW_QUESTION=false\n"+
				"# Are individual names (labels) included in the input file?\n" + 
				"STRUCTURE_PARSER_IND_NAMES_QUESTION=true\n"+
				"# Are the \"Recessive Alleles\" row and/or the \"Inter-Marker Distance\" row present in the input file?\n" + 
				"STRUCTURE_PARSER_ADDITIONAL_ROW_QUESTION=NONE\n\n"+
				"# GENEPOP Writer questions\n" + 
				"WRITER_FORMAT=GENEPOP\n\n"+
				"# Specify which data type should be included in the GENEPOP file  (GENEPOP can only analyze one data type per file):\n" + 
				"GENEPOP_WRITER_DATA_TYPE_QUESTION=MICROSAT\n" + 
				"# Specify the locus/locus combination you want to write to the GENEPOP file:\n" + 
				"GENEPOP_WRITER_LOCUS_COMBINATION_QUESTION=";
		try 
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(spidPath));
			bw.write(file);
			bw.close();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		return file;
	}
	
	//check the file's format, get the number of marks of this file.
	public int checkFileFormat()
	{
		int count = 0;
		boolean rowFormat = true;
		boolean columnFormat = true;
		int option = -1;
		//int prevRowLength = 0;
		
		System.out.println(fileArray.toString());
		int prevRowLength = fileArray.get(0).size();
		int prevColumnLength = fileArray.size();
		for(int i = 0; i < fileArray.size(); i++)
		{
			int rowLength = fileArray.get(i).size();
			if(rowLength == prevRowLength)
			{
				int columnLength = 0;
				for(int j = 0 ; j < rowLength; j++)
				{
					if(!fileArray.get(i).get(j).isEmpty())
					{
						try
						{
							int data = Integer.parseInt(fileArray.get(i).get(j));
							count++;
						}
						catch(Exception e)
						{
							
						}
						columnLength++;
					}
				}
				if(columnLength != prevColumnLength)
				{
					columnFormat = false;
					prevColumnLength = columnLength;
				}
			}
			else
			{
				rowFormat = false;
				prevRowLength = rowLength;
			}
		}
		if(rowFormat == false || columnFormat == false)
		{
			option = JOptionPane.showConfirmDialog(null,"The file might not in STRUCTURE format, do you still want to conver the file?",
					"Error", JOptionPane.YES_NO_OPTION);
		}
		else
		{
			markerNumInt = count;
		}
		return option;
	}
	
	public void setfastConvertLog()
	{
		fastConvertLogString += "\nFast convert: convert file from STRUCTURE to GENEPOP.";
	}
	
	public String getfastConvertLog()
	{
		return fastConvertLogString;
	}
}
