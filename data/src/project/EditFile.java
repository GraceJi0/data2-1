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
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tika.Tika;
import org.apache.tika.mime.MimeTypes;
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
        currentFile = file;
        missingCh = null;
        replaceCh = null;
        rowNum = 0;
        columnNum = 0;
        sheetName = null;
        rename = null;
        keepChangedFile = false;
        resetLabel = false;
        getFileString();
    }
    
    public boolean editTheFile(String expression, JPanel gui)
    {
    		String extenssion = getMyFileExtension();
    		boolean error = false;
    		if(keepChangedFile == false)
        {
            	fileArray.removeAll(fileArray);
            	rowNum =0;
            	columnNum = 0;
	        try 
	        {
	            if(extenssion.equals("xlsx"))
	            {
	                error = editXLSXfile(gui);
	            }
	            else if(extenssion.equals("xls"))
	            {
	            		error = editXLSfile(gui);
	            }
	            else if(extenssion.equals("gff") || extenssion.equals("tped") || 
	            		extenssion.equals("bayescan") || extenssion.equals("hmp") || 
	            		extenssion.equals("pdf") || extenssion.equals("mts") || 
	            		extenssion.equals("doc"))
	            {
	            		JOptionPane.showConfirmDialog(null,  "Can't open the file!\nPlease click \"Open \" or \"Locate\" to edit the file", 
	            			"Error001", JOptionPane.CLOSED_OPTION);
	            		error = true;
	            }
	            else if(extenssion.equals(""))
	            {
	            		String message = null;
	            		FileTypeDetector detector = new TikaFileTypeDetector();
	                String contentType = detector.probeContentType(currentFile.toPath());
	                System.out.println(contentType);
	                if(contentType.equals("application/vnd.ms-excel"))
	                {
	                		message = "This file doesn't have an extenssion. Do you want to try to open it as an xls file?";
	                		int option = JOptionPane.showConfirmDialog(null, message, "Error", JOptionPane.OK_CANCEL_OPTION);
	                		if(option == 0)
	                		{
		                		if(!editXLSfile(gui)) //If no error
		                		{
		                			rename = "xls";
		                		}
	                		}
	                		else
	                		{
	                			error = true;
	                		}
	                }
	                else if(contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
	                {
		                	message = "This file doesn't have an extenssion. Do you want to try to open it as an xlsx file?";
		            		int option = JOptionPane.showConfirmDialog(null, message, "Error", JOptionPane.OK_CANCEL_OPTION);
		            		if(option == 0)
		            		{
		                		if(!editXLSXfile(gui)) // if no error
		                		{
		                			rename = "xlsx";
		                		}
		            		}
		            		else
		            		{
		            			error = true;
		            		}
	                }
	                else if(contentType.equals("text/plain"))
	                {
		                	message = "This file doesn't have an extenssion. Do you want to try to open it as an txt file?";
		            		int option = JOptionPane.showConfirmDialog(null, message, "Error", JOptionPane.OK_CANCEL_OPTION);
		            		if(option == 0)
		            		{
		                		if(!editXLSXfile(gui))  //if no error
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
	            else
	            {
	                error = separateFile(expression);
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
        return error;
    }
    
    //*********************edit txt files***************************
    public boolean separateFile(String expression) throws FileNotFoundException
    {
    		boolean error = false;
    		getFileString();  //get the file string from original file 
	    String lines[] = fileString.split("\n");
	    int lineNum = 0;
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
	            //System.out.println(dataLine[i].trim());
	         }
	         lineNum++;
	         if(dataLine.length > columnNum)
	         {
	               //keep the biggest columnNum in different rows to be the columnNum
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
         return error;
    }
    
    //read the file from original file and put them in variable "fileString"
    public void getFileString()
    {
    		fileString = "";
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
    
    //go through the fileArray and add all strings to a long string for writing back.
    public void fileArrayToFileString(int keepRowNumber)
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
    }
    
    //**********************edit xlsx files**************************
    public boolean editXLSXfile(JPanel gui) throws IOException
    {   
	    	Boolean error = false;
	    FileInputStream fip = new FileInputStream(currentFile);
	        
	    if(fip.available() > 0)
	    {
	        	if(gui != null)
	        	{
	   			gui.setCursor(new Cursor(Cursor.WAIT_CURSOR));
	        	}
	        XSSFWorkbook workbook = new XSSFWorkbook(fip);
		    String sheets[] = getAllXLSXSheet(workbook);
			if(gui != null)
		    {
		    		gui.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		    }
				String sheetSelected = (String)JOptionPane.showInputDialog(null, "Select a sheet to edit:",
	        						"Select a sheet", JOptionPane.PLAIN_MESSAGE,null, sheets,null);
				if(sheetSelected != null)
				{
					//gui.setCursor(new Cursor(Cursor.WAIT_CURSOR));
					sheetName = sheetSelected;
					XSSFSheet sheet = workbook.getSheet(sheetSelected);
					readTheXLSXSheet(sheet);
				}
				else
				{
					error = true;
				}
				addRowLabel();
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
    public boolean editXLSfile(JPanel gui)
    {
    		Boolean error = false;
			try 
			{
				FileInputStream fip = new FileInputStream(currentFile);
				if(fip.available() > 0)
			    {
					gui.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			        HSSFWorkbook workbook = new HSSFWorkbook(fip);
				    String sheets[] = getAllXLSSheet(workbook);
				    gui.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					String sheetSelected = (String)JOptionPane.showInputDialog(null, "Select a sheet to edit:",
		        						"Select a sheet", JOptionPane.PLAIN_MESSAGE,null, sheets,null);
					if(sheetSelected != null)
					{
						sheetName = sheetSelected;
						HSSFSheet sheet = workbook.getSheet(sheetSelected);
						readTheXLSSheet(sheet);
					}
					else
					{
						error = true;
					}
					addRowLabel();
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
    
    //*************find the missing data and replace them with new characters************
    public void replaceMissingData()
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
    
    //***********save all changes that happens on the file*************
    public File writeBack(String rename)
    {
    		String newFileName = currentFile.getAbsolutePath();
    		int reply = -1;
    		String message;
    		if(rename != null)
    		{
    			message = "The file is open as an "+rename+" file. Do you want to add the extension?";
    			reply = JOptionPane.showConfirmDialog(null, message, "Rename", JOptionPane.YES_NO_OPTION);
    			if(reply == JOptionPane.YES_OPTION)
    			{
    				newFileName += "."+rename;
    			}
    			reply = -1;
    		}
    		currentFile = new File(newFileName);
    		if(getMyFileExtension().equals("xlsx"))
    		{
    			
    		}
    		else if(getMyFileExtension().equals("xls"))
    		{
    			
    		}
    		else
    		{
	    		try 
	    		{
	    			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(currentFile.getAbsolutePath()));
					message = "Do you want to add the row number into the file?";
					reply = JOptionPane.showConfirmDialog(null, message, "Add Row Number", JOptionPane.YES_NO_OPTION);
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
	    		catch (IOException e)
	    		{
				e.printStackTrace();
			}
    		}
    		return currentFile;
    }
    
    //*********replace spaces in headers with underscores***********
    public boolean replaceSpaceInHeader(int headerPosition)
    {
    		boolean error = false;
    		if((headerPosition<rowNum)&&(fileArray.get(headerPosition)!= null))
    		{
	    		for(int i = 0; i < fileArray.get(headerPosition).size(); i++)
	    		{
	    			String currentHeader = fileArray.get(headerPosition).get(i);
	    			for(int j = 0; j < currentHeader.length(); j++)
	    			{
	    				if(currentHeader.charAt(j) == ' ')
	    				{
	    					fileArray.get(headerPosition).set(i, 
	    							currentHeader.substring(0, j)+"_"+currentHeader.substring(j+1,currentHeader.length()));
	    					currentHeader = fileArray.get(headerPosition).get(i);
	    				}
	    			}
	    		}
	    		keepChangedFile = true;
	    		fileArrayToFileString(ADD_ROW_NUMBER_OPTION);
    		}
    		else
    		{
    			error = true;
    			JOptionPane.showConfirmDialog(null,"This row is empty!", "Error", JOptionPane.CLOSED_OPTION); 
    		}
    		return error;
    }
    
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
	    		for(int k = length-1; k>=0; k--)
	    		{
		    		for(int i = 0; i < fileArray.size(); i++)
		    		{
		    			if(rowIndex[k]==i)
		    			{
		    				fileArray.remove(i);
		    			}
		    		}
	    		}
	    		rowNum -= rowIndex.length;
	    		fileArrayToFileString(ADD_ROW_NUMBER_OPTION);
	    		
	    		for(int i = 0;i<fileArray.size();i++) //reset label
	    		{
	    			if(!fileArray.get(i).isEmpty())
	    			{
	    				fileArray.get(i).set(0, "row"+(i+1));
	    			}
	    		}
    		}
    		return error;
    }
    
    public boolean deleteColumn(List<String> selectedChoicesColumn)
    {
    		boolean error = false;
		
		return error;
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
    
    public void addRowLabel()
    {
        for(int i = 0; i < rowNum; i++)
        {
            fileArray.get(i).add(0, "row"+(i+1));
        }
        columnNum++;
    }
    
    public String getMyFileExtension()
    {
    		String extenssion = "";
        String fileName = currentFile.getName();
        int index = -1;
        index = fileName.lastIndexOf('.');
        if(index > -1)
        {
        		extenssion = fileName.substring(index + 1);
        }
        return extenssion;
    }
    
    public void setKeepChangedFile(boolean b)
    {
    		keepChangedFile = b;
    }
}

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


