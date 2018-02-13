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

import javax.swing.JTextField;


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
		JTextField missingValueInput = new JTextField();
	    Object[] message = {"Convert: "+fileName, "From STRUCTURE to GENEPOP","\nWhat is the missing value code?(default is -9)",
	    		missingValueInput,"\n\n", "(Fast convert will generate a spid file automatically.)\nFor more file format options, please go to the \"convert\" in File menu",};
	    int option = JOptionPane.showConfirmDialog(null, message, "Fast convert", JOptionPane.OK_CANCEL_OPTION);
	    if(option == 0)
	    {	
	    		runPGDSpider(missingValueInput.getText());
	    }
	}
	
	public void runPGDSpider(String missingValue)
	{
		String inputPath = currentFile.getAbsolutePath();
		String outputPath = currentFile.getParentFile().getAbsolutePath()+"/GENEPOP"+currentFile.getName();
		String spidPath = currentFile.getParentFile().getAbsolutePath()+"/PGDSpiderSpidFile.spid";
		creatSpidFile(missingValue, spidPath);
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
		} 
		catch (IOException e) 
		{
			JOptionPane.showConfirmDialog(null,"Can't open PGDSpider!", "Error", JOptionPane.CLOSED_OPTION);
		}
	}
	
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
	
	public String creatSpidFile(String missingValue,String spidPath)
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
				"STRUCTURE_PARSER_NUMBER_LOCI_QUESTION=20\n"+ ////////////////////not working if I remove "20"
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
}
