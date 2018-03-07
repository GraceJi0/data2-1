package project;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.chainsaw.Main;



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
		this.fileArray = fileArray;
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
		String commandFastConvert = "";
		String inputPath = "";
		String outputPath = "";
		String spidPath = "";
		String currentFilePath = "";
		String PGDSpiderPath = "";
		
		int option = checkFileFormat();
		if(markerNumInt == -1)
		{
			markerNum = "";
		}
		else
		{
			markerNum =  Integer.toString(markerNumInt);
		}
		if(option == JOptionPane.YES_OPTION)
		{
			if(isMac())
			{
				inputPath = currentFile.getAbsolutePath();
				outputPath = currentFile.getParentFile().getAbsolutePath()+"/GENEPOP"+currentFile.getName();
				/*if(inputPath.contains(" "))
				{
					inputPath = "\""+inputPath+"\"";
				}
				if(outputPath.contains(" "))
				{
					outputPath = "\""+outputPath+"\"";
				}*/
				 spidPath = currentFile.getParentFile().getAbsolutePath()+"/PGDSpiderSpidFile.spid";
				//String PGDSpiderPath = (Main.class.getResource("/Resources/PGDSpider2-cli.jar")).getPath();
				 currentFilePath = new File(".").getAbsolutePath();
				 PGDSpiderPath = currentFilePath.substring(0, currentFilePath.lastIndexOf("/"))+"/PGDSpider_2.1.1.3/PGDSpider2-cli.jar";
				//System.out.println(PGDSpiderPath);
				/*inputPath = inputPath.replace(" ", "\\ ");
				outputPath = outputPath.replace(" ", "\\ ");
				spidPath = spidPath.replace(" ", "\\ ");*/
				creatSpidFile(missingValue, markerNum,spidPath);
				String[] commandFastConvertArray = {"java", "-Xmx1024m", "-Xms512m", "-jar", PGDSpiderPath, "-inputfile", inputPath, 
						 "-inputformat","STRUCTURE","-outputfile",outputPath,"-outputformat","GENEPOP","-spid",spidPath};
				
				commandFastConvert = "java -Xmx1024m -Xms512m -jar " + PGDSpiderPath +" -inputfile "+ inputPath + 
						" -inputformat STRUCTURE -outputfile "+ outputPath + " -outputformat GENEPOP -spid " + spidPath;
				
				//commandFastConvert = "java -Xmx1024m -Xms512m -jar /Users/dinghanji/Documents/data1.1/project-dataManagement/data/PGDSpider_2.1.1.3/PGDSpider2-cli.jar -inputfile /Users/dinghanji/Desktop/projectFile/saveas/00001622-Microsatellite\\ genotypes\\ Zosterops\\ hybrid\\ zone\\ Dryad.txt -inputformat STRUCTURE -outputfile /Users/dinghanji/Desktop/projectFile/saveas/GENEPOP00001622-Microsatellite\\ genotypes\\ Zosterops\\ hybrid\\ zone\\ Dryad.txt -outputformat GENEPOP -spid /Users/dinghanji/Desktop/projectFile/saveas/PGDSpiderSpidFile.spid";
				
				/*for(int i = 0 ; i < commandFastConvertArray.length; i++)
				{
					System.out.print(commandFastConvertArray[i]+" ");
				}*/
			}
			else if(isWindows())
			{
				inputPath = currentFile.getAbsolutePath();
				outputPath = currentFile.getParentFile().getAbsolutePath()+"\\GENEPOP"+currentFile.getName();
				spidPath = currentFile.getParentFile().getAbsolutePath()+"\\PGDSpiderSpidFile.spid";
				currentFilePath = new File(".").getAbsolutePath();
				PGDSpiderPath = currentFilePath.substring(0, currentFilePath.lastIndexOf("\\"))+"\\PGDSpider_2.1.1.3\\PGDSpider2-cli.exe";
				if(inputPath.contains(" "))
				{
					inputPath = "\""+inputPath+"\"";   
				}
				if(outputPath.contains(" "))
				{
					outputPath = "\""+outputPath+"\"";
				}
				if(PGDSpiderPath.contains(" "))
				{
					PGDSpiderPath = "\""+PGDSpiderPath+"\"";
				}
				
				creatSpidFile(missingValue, markerNum,spidPath);
				
				if(spidPath.contains(" "))
				{
					spidPath = "\""+spidPath+"\"";
	
				}
				commandFastConvert = PGDSpiderPath +" -inputfile "+ inputPath + 
						" -inputformat STRUCTURE -outputfile "+ outputPath + " -outputformat GENEPOP -spid " + spidPath;
			}
			try 
			{
				Process pros = Runtime.getRuntime().exec(commandFastConvert);
				OutputStream out = pros.getOutputStream();
				/*ProcessBuilder pb = new ProcessBuilder(commandFastConvertArray);
				Process pros = pb.start();*/
				InputStream in = pros.getInputStream();
				InputStream err = pros.getErrorStream();
				
				String result = readInputStream(in)+readInputStream(err);
				//commandFastConvert += "\n\n\n"+PGDSpiderPath;/////////////////////////
				JFrame errorMessageFrame = showPGDSpiderErrorMessage(result,commandFastConvert);
				if(errorMessageFrame != null)
				{
					errorMessageFrame.setMinimumSize(new Dimension(700,300));
					errorMessageFrame.setMaximumSize(new Dimension(900,600));
				}
				
				if(result.contains("ERROR") || result.contains("Error") /*|| result.contains("Usage: PGDSpiderCli")*/)
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
				System.out.println(e);
				JOptionPane.showConfirmDialog(null,"Can't open PGDSpider!", "Error", JOptionPane.CLOSED_OPTION);
			}
		}
	}
	
	public void runDetailConvert()
	{
		String currentFilePath = new File(".").getAbsolutePath();
		String PGDSpiderPath = currentFilePath.substring(0, currentFilePath.lastIndexOf("/"))+"/PGDSpider_2.1.1.3/PGDSpider2.jar";
		String commandLine = "java -Xmx1024m -Xms512m -jar " + PGDSpiderPath;
		try 
		{
			Runtime.getRuntime().exec(commandLine);
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
		FileWriter fileWriter = null;
		BufferedWriter bw = null;
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
				"STRUCTURE_PARSER_NUMBER_LOCI_QUESTION="+marksNum+"\n"+
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
				bw = new BufferedWriter(new FileWriter(spidPath));
				bw.write(file);
				bw.close();
		} 
		catch (IOException e1) 
		{
				System.out.println("can't creat file writer!");
				e1.printStackTrace();
		}
		return file;
	}
	
	//check the file's format, get the number of marks of this file.
	public int checkFileFormat()
	{
		int count = 0;
		boolean columnFormat = true;
		int option = -1;
		int columnLength = 0;
		int countResult = 0;
		int prevColumnLength = fileArray.get(1).size();
		for(int i = 0; i < fileArray.size(); i++)
		{
			count = 0;
			if(!fileArray.get(i).isEmpty())
			{
				int length = fileArray.get(i).size();
				for(int j = 0 ; j < length; j++)
				{
					if(!fileArray.get(i).get(j).isEmpty())
					{
						try
						{
							Integer.parseInt(fileArray.get(i).get(j));
							count++;
						}
						catch(Exception e){}
					}
					columnLength++;
				}
				if(columnLength != prevColumnLength)
				{
					columnFormat = false;
				}
				prevColumnLength = columnLength;
				columnLength = 0;
				countResult = count;
			}
		}
		if(columnFormat == false || countResult % 2 != 0)
		{
			option = JOptionPane.showConfirmDialog(null,"The file might not in STRUCTURE format, do you still want to conver the file?",
					"Error", JOptionPane.YES_NO_OPTION);
		}
		else
		{
			option = JOptionPane.YES_OPTION;
		}
		markerNumInt = countResult/2;
		return option;
	}
	
	public String showConvertMessage(String result)
	{	
		String message = "";
		if(result == null || result.trim().equals("") || result.contains("Usage: PGDSpiderCli") )
		{
			message = "ERROR: \nCan't convert the file, please remove all spaces in file's name or try \"convert\" in \"file\" menu.";
		}
		else if(result.contains("ERROR") || result.contains("Error"))
		{
			message = "ERROR: \nThere're errors when converting, please check the file's format and type.";
		}
		else if(result.contains("Java Runtime Environment"))
		{
			message = "ERROR:  \n Can't run PGDSPider, please install Java Runtime Environment!";
		}
		else
		{
			message = "Successfull convert!";
		}
		return message;
	}
	
	public JFrame showPGDSpiderErrorMessage(String result, String commandLine)
	{
		String convertMessage =  showConvertMessage(result);
		JFrame errorFrame = new JFrame("PGDSpider"); 
		errorFrame.setLayout(new BorderLayout());
		
		JPanel errorMessagePanel = new JPanel();
		errorMessagePanel.setLayout(new BorderLayout());
		errorMessagePanel.setBorder(new EmptyBorder(0,20,10,20));
		
		JTextArea errorMessageTextArea = new JTextArea();
		JScrollPane errorMessageScroll = new JScrollPane(errorMessageTextArea,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		String text = "Command line:\n"+ commandLine + "\n\n"+ result;
		errorMessageTextArea.setText(text);
		errorMessageTextArea.setEditable(false);
		errorMessageTextArea.setLineWrap(true);
		errorMessageTextArea.setWrapStyleWord(true);
		
		JLabel convertMessageLabel = new JLabel(convertMessage);
		convertMessageLabel.setBorder(new EmptyBorder(20,20,0,20));
		//convertMessageLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
		convertMessageLabel.setForeground(Color.red);
		
		JLabel detailsLabel = new JLabel("Details");
		
		JPanel titlePanel = new JPanel();
		titlePanel.setLayout(new GridLayout(2,1));
		titlePanel.add(convertMessageLabel);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.TRAILING));
		JButton okBtn = new JButton("OK");
		okBtn.addActionListener(new ActionListener()
        {
			public void actionPerformed(ActionEvent ae) 
			{	
				errorFrame.dispose();
			}
		});
		buttonPanel.add(okBtn);
		
		errorMessagePanel.add(detailsLabel, BorderLayout.NORTH);
		errorMessagePanel.add(errorMessageScroll, BorderLayout.CENTER);
		errorMessagePanel.add(buttonPanel, BorderLayout.SOUTH);
		
		errorFrame.add(titlePanel, BorderLayout.NORTH);
		errorFrame.add(errorMessagePanel, BorderLayout.CENTER);
		errorFrame.setVisible(true);
		return errorFrame;
	}
	
	public boolean isWindows()
	{
		return System.getProperty("os.name").toLowerCase().indexOf("win")>=0;
	}
	
	public boolean isMac()
	{
		return System.getProperty("os.name").toLowerCase().indexOf("mac")>=0;
	}
	
	public boolean isUnix()
	{
		String os = System.getProperty("os.name").toLowerCase();
		return os.indexOf("nux")>=0 || os.indexOf("nix")>=0 ||os.indexOf("aix")>=0 ;
	}
	
	public void setfastConvertLog()
	{
		fastConvertLogString += "\nFast convert: convert file from STRUCTURE to GENEPOP.";
	}
	
	public String getfastConvertLog()
	{
		return fastConvertLogString;
	}
	
	public String escapeSpaces(String path)
	{
		String[] pathArray = path.split("\\\\");
		path = "";
		for(int i = 0 ; i < pathArray.length; i++)
		{
			if(pathArray[i].contains(" "))
			{
				pathArray[i] = "\""+pathArray[i]+"\"";
			}
		}
		for(int i = 0 ; i < pathArray.length; i++)
		{
			path += pathArray[i]+"\\";
		}
		return path;
	}
}