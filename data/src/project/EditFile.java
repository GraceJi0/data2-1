package project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.spi.FileTypeDetector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.awt.Cursor;
import java.io.*;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tika.Tika;
import org.apache.tika.mime.MimeTypes;
import com.google.common.io.Files;
import org.apache.poi.hssf.usermodel.*;

public class EditFile 
{
	private static final int ADD_ROW_NUMBER_OPTION = 1;
	private static final int DONT_ADD_ROW_NUMBER_OPTION = 0;
    private File currentFile;
    private String fileString;
    private List<List<String>> fileArray;
    private int columnNum;
    private int rowNum;
    private String missingCh;
    private String replaceCh;
    private String splitExpression;
    private String sheetName;
    private String rename;
    private boolean keepChangedFile;
    private boolean resetLabel;
    
    public EditFile(File file) 
    {
    		fileArray = new ArrayList<List<String>>();
    		fileString = "";
    		if(file != null)
    		{
    			currentFile = file;
    		}
    		else
    		{
    			JOptionPane.showConfirmDialog(null,  "Can't edit the file", 
            			"Error", JOptionPane.CLOSED_OPTION);
    		}
        missingCh = null;
        replaceCh = null;
        rowNum = 0;
        columnNum = 0;
        sheetName = null;
        rename = "";
        keepChangedFile = false;
        resetLabel = false;
        getFileString();
    }
    
    public boolean editTheFile(String expression, JPanel gui, String theSheetName)
    {
    		String extenssion = getMyFileExtension();
    		boolean error = false;
    		if(currentFile != null)
    		{
	    		if(keepChangedFile == false)
	        {
	            	fileArray.removeAll(fileArray);
	            	rowNum =0;
	            	columnNum = 0;
		        try 
		        {
		            if(extenssion.equals("xlsx"))
		            {
		                error = editXLSXfile(gui,theSheetName);
		            }
		            else if(extenssion.equals("xls"))
		            {
		            		error = editXLSfile(gui,theSheetName);
		            }
		            else if(extenssion.equals("gff") || extenssion.equals("tped") || 
		            		extenssion.equals("bayescan") || extenssion.equals("hmp") || 
		            		extenssion.equals("pdf") || extenssion.equals("mts") || 
		            		extenssion.equals("doc") || extenssion.equals("jar"))
		            {
		            		JOptionPane.showConfirmDialog(null,  "Can't open the file!\nPlease click \"Open \" or \"Locate\" to edit the file", 
		            			"Error001", JOptionPane.CLOSED_OPTION);
		            		error = true;
		            }
		            else if(extenssion.equals("txt") || extenssion.equals("csv"))
		            {
		            		error = separateFile(expression);
		            }
		            else if(extenssion.equals("")) //if current file doesn't have an extension
		            {
		            		String message = null;
		            		FileTypeDetector detector = new TikaFileTypeDetector();
		                String contentType = detector.probeContentType(currentFile.toPath());
		                if(contentType.equals("application/vnd.ms-excel"))
		                {
		                		int option = 0;
		                		if(rename.equals(""))
		                		{
			                		message = "This file doesn't have an extenssion. Do you want to try to open it as an xls file?";
			                		option = JOptionPane.showConfirmDialog(null, message, "Error", JOptionPane.OK_CANCEL_OPTION);
		                		}
		                		if(option == 0)
		                		{
			                		if(!editXLSfile(gui,theSheetName)) //If no error
			                		{
			                			rename = "xls";
			                		}
			                		else
			                		{
			                			error = true;
			                		}
		                		}
		                		else
		                		{
		                			error = true;
		                		}
		                }
		                else if(contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
		                {
			                	int option = 0;
		                		if(rename.equals(""))
		                		{
				                	message = "This file doesn't have an extenssion. Do you want to try to open it as an xlsx file?";
				            		option = JOptionPane.showConfirmDialog(null, message, "Error", JOptionPane.OK_CANCEL_OPTION);
		                		}
			            		if(option == 0)
			            		{
			                		if(!editXLSXfile(gui,theSheetName)) // if no error
			                		{
			                			rename = "xlsx";
			                		}
			                		else
			                		{
			                			error = true;
			                		}
			            		}
			            		else
			            		{
			            			error = true;
			            		}
		                }
		                else if(contentType.equals("text/plain"))
		                {
			                	int option = 0;
		                		if(rename.equals(""))
		                		{
				                	message = "This file doesn't have an extenssion. Do you want to try to open it as an txt file?";
				            		option = JOptionPane.showConfirmDialog(null, message, "Error", JOptionPane.OK_CANCEL_OPTION);
		                		}
			            		if(option == 0)
			            		{
			                		if(!separateFile(expression))  //if no error
			                		{
			                			rename = "txt";
			                		}
			            		}
			            		else
			            		{
			            			error = true;
			            		}
		                }
		                else
		                {
		            			JOptionPane.showConfirmDialog
		            								(null, "Can't open the file!\nPlease click \"Open \" or \"Locate\" to edit the file", 
		                                         "Error", JOptionPane.CLOSED_OPTION);
		            			error = true;
		            		}
		            }
		            else //if file's type is unusual.
		            {
		            		int option = JOptionPane.showConfirmDialog(null,  "Open this file might cause the program crash, do you still want to open it?", 
		            			"Warning", JOptionPane.OK_CANCEL_OPTION);
		            		if(option == 0)
		            		{
		            			System.out.println(currentFile.length());
		            			error = separateFile(expression);
		            		}
		            		else
		            		{
		            			error = true;
		            		}
		            }
		        } 
		        catch (IOException e) 
		        {
		            JOptionPane.showConfirmDialog(null, "Can't open the file!\nPlease click \"Open \" or \"Locate\" to edit the file", 
		                                          "Error002", JOptionPane.CLOSED_OPTION);
		        }
	        }
	    		else
	    		{
	    			keepChangedFile = false;
	    		}
    		}
    		else
    		{
    			error = true;
    		}
        return error;
    }
    
    //*********************edit txt files***************************
    public boolean separateFile(String expression) throws FileNotFoundException
    {
    		boolean error = false;
    		getFileString();  //get the file string from original file 
	    String lines[] = fileString.split("\n");
	    int lineNum = 0;
	    try
	    {
		    while(lineNum<lines.length && lines[lineNum] != null)
		    {
		    		String line = lines[lineNum];
		        line.trim();
		        String[] dataLine = line.split(expression);
		        
		        int start = 0;
		        if(expression.equals("line")) // if it split by line, just display the data by line
		        {
		        		dataLine = new String[1];
		        		dataLine[0] = line;
		         }
		         for(int i = start; i < dataLine.length; i++)
		         {
		        	 	fileArray.add(new ArrayList<String>());
		            fileArray.get(lineNum).add(dataLine[i].trim());
		         }
		         lineNum++;
		         if(dataLine.length > columnNum) //keep the biggest columnNum in different rows to be the columnNum
		         {
		                	columnNum = dataLine.length;
		         }  
		     }
		     if(fileArray.size() == 0)
		     {
		           JOptionPane.showConfirmDialog(null, 
		           		"Can't open the file!\nPlease click \"Open \" or \"Locate\" to edit the file", 
		           		"Error003", JOptionPane.CLOSED_OPTION); 
		           error = true;
		     }
		     else
		     {
		           columnNum = fileArray.get(0).size();
		           rowNum = lineNum;
		           splitExpression = expression;
		           addRowLabel();
		     }
	    }
	    catch(Exception e)
	    {
	    		JOptionPane.showConfirmDialog(null, 
	           		"Can't open the file!\nPlease click \"Open \" or \"Locate\" to edit the file", 
	           		"Error003-1", JOptionPane.CLOSED_OPTION); 
	    		error = true;
	    }
         return error;
    }
    
    //read the file from original file and put them in variable "fileString"
    public String getFileString()
    {
    		fileString = "";
    		if(currentFile != null)
    		{
	        try
	        {
	        		BufferedReader br = new BufferedReader(new FileReader(currentFile));
	            String line = br.readLine();
	            while(line != null)
	            {
	                fileString+=line+"\n";
	                line = br.readLine();
	            }
	            br.close();
	        }
	        catch(IOException e)
	        {
	            JOptionPane.showConfirmDialog(null, 
	                "Can't open the file!\nPlease click \"Open \" or \"Locate\"", "Error 004", 
	                 JOptionPane.CLOSED_OPTION);            
	        }
    		}
    		return fileString;
    }
    
    //go through the fileArray and add all strings to a long string for writing back.
    public String fileArrayToFileString(int keepRowNumber)
    {
    		fileString = "";
    		int start = 0;
		for(int i = 0; i< fileArray.size(); i++)
		{
			int length = fileArray.get(i).size();
			if(keepRowNumber == DONT_ADD_ROW_NUMBER_OPTION)
			{
				start = 1;
			}
			for(int j = start; j < length; j++)
			{
				if(j == (length-1))
				{
					fileString+=fileArray.get(i).get(j)+"\n";
				}
				else
				{
					fileString+=fileArray.get(i).get(j)+	splitExpression;
				}
			}	
		}
		return fileString;
    }
    
    //**********************edit xlsx files**************************
    public boolean editXLSXfile(JPanel gui, String theSheetName) throws IOException
    {   
	    	Boolean error = false;
	    FileInputStream fip = new FileInputStream(currentFile);
	        
	    if(fip.available() > 0)
	    {
	        	/*if(gui != null)
	        	{
	   			gui.setCursor(new Cursor(Cursor.WAIT_CURSOR));
	        	}*/
	        	try
	        	{
		        XSSFWorkbook workbook = new XSSFWorkbook(fip);
			    String sheets[] = getAllXLSXSheet(workbook);
				String sheetSelected;
				if(theSheetName.equals(""))
				{
					 sheetSelected = (String)JOptionPane.showInputDialog(null, "Select a sheet to edit:",
		        						"Select a sheet", JOptionPane.PLAIN_MESSAGE,null, sheets,null);
				}
				else
				{
					sheetSelected = theSheetName;
				}
				if(sheetSelected != null)
				{
					if(gui != null)
					{
						gui.setCursor(new Cursor(Cursor.WAIT_CURSOR));
					}
					sheetName = sheetSelected;
					XSSFSheet sheet = workbook.getSheet(sheetSelected);
					readTheXLSXSheet(sheet);
					if(gui != null)
					{
						gui.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));					
					}
				}
				else
				{
					error = true;
				}
				addRowLabel();
	        	}
	        	catch(Exception e)
	        	{
	        		JOptionPane.showConfirmDialog
					(null, "Can't open the file!\nPlease click \"Open \" or \"Locate\" to edit the file", 
		         "Error8", JOptionPane.CLOSED_OPTION);
	        		error = true;
	        	}
			fip.close();
	    }
	    else
	    {
	        		JOptionPane.showConfirmDialog(null,
	        			"Can't open the file!\nPlease click \"Open \" or \"Locate\" to edit the file", 
	                    "Error005", JOptionPane.CLOSED_OPTION); 
	        		error = true;
	    }
        return error;
    }
    
    public String[] getAllXLSXSheet(XSSFWorkbook workbook)
    {
    		int length = workbook.getNumberOfSheets();
    		String sheets[] = new String[length];
    		for(int i = 0 ; i < length; i++)
    		{
    			sheets[i] = workbook.getSheetAt(i).getSheetName();
    		}
    		return sheets;
    }
    
	public void readTheXLSXSheet(XSSFSheet spreadsheet)
    {
    		DataFormatter dataFormatter = new DataFormatter();
    		Iterator < Row >  rowIterator = spreadsheet.iterator();
    		XSSFRow row;
    		int index = 0;
        while (rowIterator.hasNext()) 
        {
        		rowNum++;
        		fileArray.add(new ArrayList<String>());
        		row = (XSSFRow) rowIterator.next();
        		
        		int lastColumn = row.getLastCellNum();
        		for(int cn = 0; cn < lastColumn;cn++)
        		{
        			Cell cell = row.getCell(cn);
        			String cellValue = " ";
        			if(cell != null)
        			{
        				cellValue = dataFormatter.formatCellValue(cell);
        			}
        			index++;
        			fileArray.get(rowNum-1).add(cellValue);
        		}
        		if(index > columnNum)
        		{
        			columnNum = index;
        		}
        		index = 0;
        }
    }
    
    //**********************edit xls files**************************
    public boolean editXLSfile(JPanel gui,String theSheetName)
    {
    		Boolean error = false;
		try 
		{
			FileInputStream fip = new FileInputStream(currentFile);
			if(fip.available() > 0)
		    {
				try
				{
				    HSSFWorkbook workbook = new HSSFWorkbook(fip);
					String sheets[] = getAllXLSSheet(workbook);
					String sheetSelected;
					if(theSheetName.equals(""))
				    {
					    	sheetSelected = (String)JOptionPane.showInputDialog(null, "Select a sheet to edit:",
			        						"Select a sheet", JOptionPane.PLAIN_MESSAGE,null, sheets,null);
					}
				    else
				    {
				    		sheetSelected = theSheetName;
				    }
					if(sheetSelected != null)
					{
						if(gui != null)
						{
							gui.setCursor(new Cursor(Cursor.WAIT_CURSOR));
						}
						sheetName = sheetSelected;
						HSSFSheet sheet = workbook.getSheet(sheetSelected);
						readTheXLSSheet(sheet);
						if(gui != null)
						{
							gui.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						}
					}
					else
					{
						error = true;
					}
					addRowLabel();
				}
				catch(Exception e)
				{
					JOptionPane.showConfirmDialog
							(null, "Can't open the file!\nPlease click \"Open \" or \"Locate\" to edit the file", 
				         "Error9", JOptionPane.CLOSED_OPTION);
					error = true;
				}
				fip.close();
			}
			else
			{
				JOptionPane.showConfirmDialog(null,
			    			"Can't open the file!\nPlease click \"Open \" or \"Locate\" to edit the file", 
			                    "Error006", JOptionPane.CLOSED_OPTION); 
			    	error = true;
			}
		} 
		catch ( Exception e) 
		{
			e.printStackTrace();
			JOptionPane.showConfirmDialog(null,
	    				"Can't open the file!\nPlease click \"Open \" or \"Locate\" to edit the file", 
	                    "Error007", JOptionPane.CLOSED_OPTION); 
	   		error = true;
		}
        return error;
    }
    
    public String[] getAllXLSSheet(HSSFWorkbook workbook)
    {
    		int length = workbook.getNumberOfSheets();
		String sheets[] = new String[length];
		for(int i = 0 ; i < length; i++)
		{
			sheets[i] = workbook.getSheetAt(i).getSheetName();
		}
		return sheets;
    }
    
    public void readTheXLSSheet(HSSFSheet spreadsheet)
    {
    		DataFormatter dataFormatter = new DataFormatter();
		Iterator < Row >  rowIterator = spreadsheet.iterator();
		HSSFRow row;
		int index = 0;
	    while (rowIterator.hasNext()) 
	    {
	    		rowNum++;
	    		fileArray.add(new ArrayList<String>());
	    		row = (HSSFRow) rowIterator.next();
	    		
	    		int lastColumn = row.getLastCellNum();
        		for(int cn = 0; cn < lastColumn;cn++)
        		{
        			Cell cell = row.getCell(cn);
        			String cellValue = " ";
        			if(cell != null)
        			{
        				cellValue = dataFormatter.formatCellValue(cell);
        			}
        			index++;
        			fileArray.get(rowNum-1).add(cellValue);
        		}
	    		if(index > columnNum)
	    		{
	    			columnNum = index;
	    		}
	    		index = 0;
	    }
    }
    
    //***********save all changes that happens on the file(write back to the original file)*************
    public File writeBack(String rename)
    {
    		String newFileName = currentFile.getAbsolutePath();
    		int reply = -1;
    		String message;
    		BufferedWriter bufferedWriter;
    		if(!rename.equals(""))
    		{
    			message = "The file is open as an "+rename+" file. Do you want to add the extension?";
    			reply = JOptionPane.showConfirmDialog(null, message, "Rename", JOptionPane.YES_NO_OPTION);
    			if(reply == JOptionPane.YES_OPTION)
    			{
    				newFileName += "."+rename;
    			}
    			reply = -1;
    		}
    		try
    		{
    			message = "Do you want to add the row number into the file?";
			reply = JOptionPane.showConfirmDialog(null, message, "Add Row Number", JOptionPane.YES_NO_OPTION);
			if(getMyFileExtension().equals("xlsx") || rename.equals("xlsx"))
	    		{
				if(reply == JOptionPane.YES_OPTION)
				{
	    				fileArrayToXLSXFile(ADD_ROW_NUMBER_OPTION);
				}
				else
				{
					fileArrayToXLSXFile(DONT_ADD_ROW_NUMBER_OPTION);
				}
	    		}
	    		else if(getMyFileExtension().equals("xls") || rename.equals("xls"))
	    		{
	    			if(reply == JOptionPane.YES_OPTION)
				{
	    				fileArrayToXLSFile(ADD_ROW_NUMBER_OPTION);
				}
				else
				{
					fileArrayToXLSFile(DONT_ADD_ROW_NUMBER_OPTION);
				}
	    		}
	    		else
	    		{	
	    			bufferedWriter = new BufferedWriter(new FileWriter(currentFile.getAbsolutePath()));
	    			if(reply == JOptionPane.YES_OPTION)
	    			{
	    				fileArrayToFileString(ADD_ROW_NUMBER_OPTION);
	    			}
	    			else
	    			{
	    				fileArrayToFileString(DONT_ADD_ROW_NUMBER_OPTION);
	    			}
	    			bufferedWriter.write(fileString);
	    			bufferedWriter.close();
	    		}
			if(!rename.equals(""))
			{
				File newFile = new File(newFileName);
	    			File tempFile = new File("temp");
	    			Files.copy(currentFile, tempFile);
	    			currentFile.delete();
	    			Files.copy(tempFile, newFile);
	    			tempFile.delete();
	    			currentFile = newFile;
			}
    		}
    		catch (IOException e)
    		{
			e.printStackTrace();
		}
    		return currentFile;
    }
    
    //write the fileArray back to the original xlsx file
    public void fileArrayToXLSXFile(int keepRowIndex) 
    {
    		try 
    		{
    			FileInputStream fileInput = new FileInputStream(currentFile);
    			if(fileInput.available()>0)
    			{
    				try
    				{
		    			XSSFWorkbook workbook = new XSSFWorkbook(fileInput);
					XSSFSheet spreedsheet = workbook.getSheet(sheetName);
					int start = 0;
					if(keepRowIndex == DONT_ADD_ROW_NUMBER_OPTION)
					{
						start = 1;
					}
					int length = fileArray.size();
					int lastRow = spreedsheet.getLastRowNum();
					for(int i = 0; i <= lastRow;i++)
					{
						XSSFRow row = spreedsheet.getRow(i);
						if(row != null)
						{
							spreedsheet.removeRow(row);
						}
						if(i<length)
						{
							row = spreedsheet.createRow(i);
							for(int j = start; j < fileArray.get(i).size(); j++)
							{
								XSSFCell cell = row.createCell(j-start);
								String cellValue = fileArray.get(i).get(j);
								try
								{
									int cellInt = Integer.parseInt(cellValue);
									cell.setCellValue(cellInt);
								}
								catch(NumberFormatException er)
								{
									try
									{
										double cellDouble = Double.parseDouble(cellValue);
										cell.setCellValue(cellDouble);
									}
									catch(NumberFormatException e)
									{
										cell.setCellValue(cellValue);
									}
								}
							}
						}
					}
					FileOutputStream outFile = new FileOutputStream(currentFile);
					workbook.write(outFile);
					outFile.close();
					workbook.close();
    				}
    				catch(Exception e)
    				{
    					JOptionPane.showConfirmDialog(null,
    			    			"Can't open the file!\nPlease click \"Open \" or \"Locate\" to edit the file", 
    			                    "Error008", JOptionPane.CLOSED_OPTION); 
    				}
    				fileInput.close();
    			}
		} 
    		catch (IOException e1) 
    		{
			e1.printStackTrace();
		}	
    }
    
    //write the fileArray back to the original xls file
    public void fileArrayToXLSFile(int keepRowIndex)
    {
	    	try 
		{
			FileInputStream fileInput = new FileInputStream(currentFile);
			if(fileInput.available()>0)
			{
				try
				{
		    			HSSFWorkbook workbook = new HSSFWorkbook(fileInput);
					HSSFSheet spreedsheet = workbook.getSheet(sheetName);
					int start = 0;
					if(keepRowIndex == DONT_ADD_ROW_NUMBER_OPTION)
					{
						start = 1;
					}
					int length = fileArray.size();
					int lastRow = spreedsheet.getLastRowNum();
					for(int i = 0; i <= lastRow;i++)
					{
						HSSFRow row = spreedsheet.getRow(i);
						if(row != null)
						{
							spreedsheet.removeRow(row);
						}
						if(i<length)
						{
							row = spreedsheet.createRow(i);
							for(int j = start; j < fileArray.get(i).size(); j++)
							{
								HSSFCell cell = row.createCell(j-start);
								String cellValue = fileArray.get(i).get(j);
								try
								{
									int cellInt = Integer.parseInt(cellValue);
									cell.setCellValue(cellInt);
								}
								catch(NumberFormatException er)
								{
									try
									{
										double cellDouble = Double.parseDouble(cellValue);
										cell.setCellValue(cellDouble);
									}
									catch(NumberFormatException e)
									{
										cell.setCellValue(cellValue);
									}
								}
							}
						}
					}
					FileOutputStream outFile = new FileOutputStream(currentFile);
					workbook.write(outFile);
					outFile.close();
					workbook.close();
				}
				catch(Exception e)
				{
					JOptionPane.showConfirmDialog(null,
			    			"Can't open the file!\nPlease click \"Open \" or \"Locate\" to edit the file", 
			                    "Error009", JOptionPane.CLOSED_OPTION); 
				}
				fileInput.close();
			}
		} 
		catch (IOException e1) 
		{
			e1.printStackTrace();
		}	
    }
    
  //*************find the missing data and replace them with new characters************
    public boolean replaceMissingData()
    {
    		boolean error = false;
    		if(!fileArray.isEmpty())
    		{
	        	for(int i = 0; i < fileArray.size(); i++)
	        	{
	        		for(int j = 0; j < fileArray.get(i).size(); j++)
	        		{
	        			if ((fileArray.get(i).get(j)).equals(missingCh))
	        			{
	        				fileArray.get(i).set(j, replaceCh);
	        			}
	        		}
	        	}
	        	fileArrayToFileString(ADD_ROW_NUMBER_OPTION);
	        	keepChangedFile = true;
    		}
    		else 
    		{
    			error = true;
    		}
    		return error;
    }
    
    //*********replace spaces in headers with underscores***********
    public boolean replaceSpaceInColumn(int columnPosition)
    {
    		boolean error = false;
    		if(columnPosition<=columnNum)
    		{
	    		for(int i = 0; i < fileArray.size(); i++)
	    		{
	    			if(fileArray.get(i)!= null && columnPosition<fileArray.get(i).size() && fileArray.get(i).get(columnPosition) != null)
	    			{
			    		String data = fileArray.get(i).get(columnPosition).replaceAll(" ", "_");
			    		fileArray.get(i).set(columnPosition,data);
	    			}
	    		}
	    		keepChangedFile = true;
	    		fileArrayToFileString(ADD_ROW_NUMBER_OPTION);
    		}
    		else
    		{
    			error = true;
    			JOptionPane.showConfirmDialog(null,"This column is empty!", "Error", JOptionPane.CLOSED_OPTION); 
    		}
    		return error;
    }
    
    //move the given column to the end of the file
    public void moveColumn(int columnPosition)
    {
    		keepChangedFile = true;
    		ArrayList<String> move = new ArrayList<String>();
    		for(int i = 0;i<fileArray.size();i++)
    		{
    			for(int j = 1; j < fileArray.get(i).size(); j++)
    			{
    				if(j == columnPosition)
    				{
    					if(fileArray.get(i).get(j)!= null)
    					{
    						move.add(fileArray.get(i).get(j));
    						fileArray.get(i).remove(j);
    					}
    				}
    			}
    		}
    		for(int i = 0; i< move.size(); i++)
    		{
    			fileArray.get(i).add(move.get(i));
    		}
    		fileArrayToFileString(ADD_ROW_NUMBER_OPTION);
    }
    
    //delete given rows in the file
    public boolean deleteRow(List<String> selectedChoicesRow)
    {
    		boolean error = false;
    		int length = selectedChoicesRow.size();
    		int[] rowIndex = new int[length];
    		if(!selectedChoicesRow.isEmpty())
    		{
    			resetLabel = true;
	    		for(int j = 0; j < length;j++)
	    		{
	    			if(selectedChoicesRow.get(j)!= null &&!(selectedChoicesRow.get(j)).equals(""))
	    			{
	    				rowIndex[j] = (Integer.parseInt(selectedChoicesRow.get(j).substring(3)))-1;
	    			}
	    		}
	    		Arrays.sort(rowIndex);
	    		removeDuplicate(rowIndex);
	    		for(int k = length-1; k>=0; k--)
	    		{
		    		for(int i = 0; i < fileArray.size(); i++)
		    		{
		    			if(rowIndex[k]==i && rowIndex[k] != -1)
		    			{
		    				fileArray.remove(i);
		    			}
		    		}
	    		}
	    		rowNum -= rowIndex.length;
	    		for(int i = 0;i<fileArray.size();i++) //reset label
	    		{
	    			if(!fileArray.get(i).isEmpty())
	    			{
	    				fileArray.get(i).set(0, "row"+(i+1));
	    			}
	    		}
	    		fileArrayToFileString(ADD_ROW_NUMBER_OPTION);
    		}
    		return error;
    }
    
    //delete given column in the file
    public boolean deleteColumn(List<String> selectedChoicesColumn)
    {
    		boolean error = false;
		int length = selectedChoicesColumn.size();
		int[] columnIndex = new int[length];
		if(!selectedChoicesColumn.isEmpty())
		{
			resetLabel = true;
	    		for(int j = 0; j < length;j++)
	    		{
	    			if(selectedChoicesColumn.get(j)!= null &&!(selectedChoicesColumn.get(j)).equals(""))
	    			{
	    				columnIndex[j] = (Integer.parseInt(selectedChoicesColumn.get(j).substring(6)));
	    			}
	    		}
	    		Arrays.sort(columnIndex);
	    		removeDuplicate(columnIndex);
	    		for(int k = length-1; k>=0; k--)
	    		{
		    		for(int i = 0; i< fileArray.size();i++)
		    		{
		    			for(int p = 0; p<fileArray.get(i).size(); p++)
		    			{
		    				if(columnIndex[k]==p && columnIndex[k] != -1)
			    			{
		    					fileArray.get(i).remove(p);
			    			}
		    			}
		    		}
	    		}
	    		columnNum -= columnIndex.length;
		}
		return error;
    }
    
    //remove the duplicate selected rows or columns
    public int[] removeDuplicate(int[] indexArray)
    {
    		for(int j = 0; j < indexArray.length-1; j++)
        {
           if(indexArray[j] == indexArray[j+1])
           {
               int index = j+1;
               while(indexArray[index] == indexArray[j] && index<indexArray.length)
               {
            	   indexArray[index] = -1;
                   index++;
               }
           }
        }
    		return indexArray;
    }
    
    //delete the every second headers in the given row.
    public boolean editHeadersFormat(int start, int end, int rowIndex)
    {
    		boolean error = false;
    		int[] list = new int[end];
    		int index = 0;
    		rowIndex--;
    		for(int i = start+1; i<=end;i = i+2)
    		{
    			list[index] = i;
    			index++;
    		}
    		Arrays.sort(list);
    		
    		for(int i = list.length-1; i >= 0; i--)
    		{
    			if(list[i]!= 0 && list[i]<fileArray.get(rowIndex).size())
    			{
    				if(!(fileArray.get(rowIndex).get(list[i]-1).isEmpty()))
    				{
	    				fileArray.get(rowIndex).remove(list[i]);
    				}
    			}
    		}
    		return error;
    }
    
    //add the given text to the end of every cell in the given column
    public boolean addTextToColumn(int columnIndex, String text, boolean header)
    {
    		boolean error = false;
    	
    			int start = 0;
    			if(header)
    			{
    				start = 1;
    			}
	    		for(int i = start; i < fileArray.size(); i++)
	    		{
	    			if(fileArray.get(i)!= null && columnIndex<fileArray.get(i).size() && fileArray.get(i).get(columnIndex) != null)
	    			{
			    		String data = fileArray.get(i).get(columnIndex)+text;
			    		fileArray.get(i).set(columnIndex,data);
	    			}
	    		}
	    		keepChangedFile = true;
	    		fileArrayToFileString(ADD_ROW_NUMBER_OPTION);
    		return error;
    }
    
    public void setCurrentFile(File currentFile)
    {
    		this.currentFile = currentFile;
    }
    
    public String getSplitExpression()
    {
    	 	return splitExpression;
    }
    
    public void setMissingCh(String data)
    {
    		missingCh = data;
    }
    
    public void setReplaceCh(String data)
    {
    		replaceCh = data;
    }
    
    public String getMissingCh()
    {
    		return missingCh;
    }
    
    public String getReplaceCh()
    {
    		return replaceCh;
    }
    
    public int getColumnNum()
    {
        return columnNum;
    }
    
    public int getRowNum()
    {
        return rowNum;
    }
    
    public File getCurrentFile()
    {
        return currentFile;
    }
    
    public String getSheetName()
    {
    		return sheetName;
    }
    
    public List<List<String>> getFileArray()
    {
        return fileArray;
    }
    
    public String getRename()
    {
    		return rename;
    }
    
    public void setRename(String newName)
    {
    		rename = newName;
    }
    
    //add the label to every row (row1, row2, row3......)
    public void addRowLabel()
    {
        for(int i = 0; i < rowNum; i++)
        {
            fileArray.get(i).add(0, "row"+(i+1));
        }
        columnNum++;
    }
    
    //get the current file extension
    public String getMyFileExtension()
    {
    		String extenssion = "";
    		if(currentFile != null)
    		{
	        String fileName = currentFile.getName();
	        int index = -1;
	        index = fileName.lastIndexOf('.');
	        if(index > -1)
	        {
	        		extenssion = fileName.substring(index + 1);
	        }
    		}
        return extenssion;
    }
    
    public void setKeepChangedFile(boolean b)
    {
    		keepChangedFile = b;
    }
}

//library: detect the file's type by content for files lack of extension
class TikaFileTypeDetector extends FileTypeDetector 
{
    private final Tika tika = new Tika();
    public TikaFileTypeDetector() 
    {
        super();
    }
    
    @Override
    public String probeContentType(Path path) throws IOException 
    {   
        String fileContentDetect = tika.detect(path.toFile());
        if(!fileContentDetect.equals(MimeTypes.OCTET_STREAM)) 
        {
            return fileContentDetect;
        }
        return null;
    }
}