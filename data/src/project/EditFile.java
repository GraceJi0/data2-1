package project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.awt.Cursor;
import java.io.*;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.*;

public class EditFile 
{
    private File currentFile;
    private List<List<String>> fileArray = new ArrayList<List<String>>(); 
    private int columnNum;
    private int rowNum;
    private String missingCh;
    private String replaceCh;
    private String splitExpression;
    private String sheetName;
    
    public EditFile(File file) 
    {
        currentFile = file;
        missingCh = null;
        replaceCh = null;
        rowNum = 0;
        columnNum = 0;
        sheetName = null;
    }
    
    public boolean editTheFile(String expression, JPanel gui)
    {
    		String extenssion = getMyFileExtension();
    		boolean error = false;
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
            		extenssion.equals("bayescan") || extenssion.equals("hmp"))
            {
            		JOptionPane.showConfirmDialog(null,  "Can't open the file!\nPlease click \"Open \" or \"Locate\" to edit the file", 
            			"Error", JOptionPane.CLOSED_OPTION);
            		error = true;
            }
            else if(extenssion.equals(""))
            {
            		String message = "This file doesn't have an extenssion. Do you want to try to open it as an xlsx or xls file?";
            		int option = JOptionPane.showConfirmDialog(null, message, "Error", JOptionPane.OK_CANCEL_OPTION);
            		if(option  == 0)
            		{
            			error = editXLSXfile(gui);
            			if(error)
            			{
            				error = editXLSfile(gui);
            				if(error)
            				{
            					 JOptionPane.showConfirmDialog(null, "Can't open the file!\nPlease click \"Open \" or \"Locate\" to edit the file", 
                                         "Error", 
                                         JOptionPane.CLOSED_OPTION);
            				}
            				else
            				{
            					int result = JOptionPane.showConfirmDialog(null,
                						"The file is open as an xlsx file, do you want to add the extenssion \"xls\" to it?", 
                						"Change the extenssion", JOptionPane.OK_CANCEL_OPTION);
                				if(result == 0)
                				{
                					//rename
                				}
            				}
            			}
            			else
            			{
            				int result = JOptionPane.showConfirmDialog(null,
            						"The file is open as an xlsx file, do you want to add the extenssion \"xlsx\" to it?", 
            						"Change the extenssion", JOptionPane.OK_CANCEL_OPTION);
            				if(result == 0)
            				{
            					//rename
            				}
            			}
            		}
            		else
            		{
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
            JOptionPane.showConfirmDialog(null, e.getMessage(), 
                                          "Can't open the file!\nPlease click \"Open \" or \"Locate\" to edit the file", 
                                          JOptionPane.CLOSED_OPTION);
            e.printStackTrace();
        }
        return error;
    }
    
    //*********************edit txt files***************************
    public boolean separateFile(String expression) throws FileNotFoundException
    {
    		boolean error = false;
        BufferedReader br = new BufferedReader(new FileReader(currentFile));
        if((missingCh == null && replaceCh == null) || (missingCh.equals("") && replaceCh.equals("")))
        {
        		fileArray.removeAll(fileArray);
	        try
	        {
	            String line = br.readLine();
	            int lineNum = 0;
	            while(line != null)
	            {
	                line.trim();
	                	String[] dataLine = line.split(expression);
	                if(expression.equals("line")) // if it split by line, just display the data by line
	                {
	                		dataLine = new String[1];
	                		dataLine[0] = line;
	                }
	                for(int i = 0; i < dataLine.length; i++)
	                {
	                    fileArray.add(new ArrayList<String>());
	                    fileArray.get(lineNum).add(dataLine[i].trim());
	                }
	                lineNum++;
	                if(dataLine.length > columnNum)
	                {
	                		//keep the biggest columnNum in different rows to be the columnNum
	                		columnNum = dataLine.length;
	                }
	                line = br.readLine();
	            }
	            br.close();
	            if(fileArray.size() == 0)
	            {
	                JOptionPane.showConfirmDialog(null, 
	                		"Can't open the file!\nPlease click \"Open \" or \"Locate\" to edit the file", 
	                		"Error", JOptionPane.CLOSED_OPTION); 
	                error = true;
	            }
	            else
	            {
	                columnNum = fileArray.get(0).size();
	                rowNum = lineNum;
	                splitExpression = expression;
	                addColumnAndRowLabel();
	            }
	        }
	        catch(IOException e)
	        {
	            JOptionPane.showConfirmDialog(null, e.getMessage(), 
	                "Can't open the file!\nPlease click \"Open \" or \"Locate\"", 
	                 JOptionPane.CLOSED_OPTION);            
	            e.printStackTrace();
	            error = true;
	        }
        }
        return error;
    }
    
    //**********************edit xlsx files**************************
    public boolean editXLSXfile(JPanel gui) throws IOException
    {   
	    	Boolean error = false;
	    	if((missingCh == null && replaceCh == null) || (missingCh.equals("") && replaceCh.equals("")))
	    	{
	        FileInputStream fip = new FileInputStream(currentFile);
	        
	        if(fip.available() > 0)
	        {
	        		gui.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		        XSSFWorkbook workbook = new XSSFWorkbook(fip);
			    String sheets[] = getAllXLSXSheet(workbook);
				gui.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
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
				addColumnAndRowLabel();
				fip.close();
	        }
	        else
	        {
	        		JOptionPane.showConfirmDialog(null,
	        			"Can't open the file!\nPlease click \"Open \" or \"Locate\" to edit the file", 
	                    "Error", JOptionPane.CLOSED_OPTION); 
	        		error = true;
	        }
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
        		Iterator < Cell >  cellIterator = row.cellIterator();
           
        		while ( cellIterator.hasNext()) 
        		{
        			Cell cell = cellIterator.next();
        			String cellValue = dataFormatter.formatCellValue(cell);
        			if(!cellValue.equals("\\s+") && !cellValue.isEmpty())
        			{
        				index++;
        				fileArray.get(rowNum-1).add(cellValue);
        			}
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
    		if((missingCh == null && replaceCh == null) || (missingCh.equals("") && replaceCh.equals("")))
    		{
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
					addColumnAndRowLabel();
					fip.close();
			    }
				else
			    {
			    		JOptionPane.showConfirmDialog(null,
			    				"Can't open the file!\nPlease click \"Open \" or \"Locate\" to edit the file", 
			                    "Error", JOptionPane.CLOSED_OPTION); 
			        	error = true;
			     }
			} 
			catch ( Exception e) 
			{
				e.printStackTrace();
				JOptionPane.showConfirmDialog(null,
	    				"Can't open the file!\nPlease click \"Open \" or \"Locate\" to edit the file", 
	                    "Error", JOptionPane.CLOSED_OPTION); 
	        		error = true;
			}
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
	    		Iterator < Cell >  cellIterator = row.cellIterator();
	       
	    		while ( cellIterator.hasNext()) 
	    		{
	    			Cell cell = cellIterator.next();
	    			String cellValue = dataFormatter.formatCellValue(cell);
	    			if(!cellValue.equals("\\s+") && !cellValue.isEmpty())
	    			{
	    				index++;
	    				fileArray.get(rowNum-1).add(cellValue);
	    			}
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
    		boolean replaced = true;
        if(missingCh != null && replaceCh != null && !missingCh.equals("") && !replaceCh.equals(""))
        {
        		//System.out.println("-----------");
        		//System.out.println(missingCh +"--");
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
        }
    }
    
    //***********save all changes that happens on the file*************
    public File writeBack()
    {
    		File file = file = new File(currentFile.getName());
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
				BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file.getName()));
				String fileString = "";
				for(int i = 0; i< fileArray.size(); i++)
				{
					for(int j = 0; j < fileArray.get(i).size(); j++)
					{
						if(j == (fileArray.get(i).size())-1)
						{
							fileString+=fileArray.get(i).get(j)+"\n";
						}
						else
						{
							fileString+=fileArray.get(i).get(j)+	splitExpression;
						}
						
					}
				}
				bufferedWriter.write(fileString);
				bufferedWriter.close();
	    		} 
	    		catch (IOException e) 
	    		{
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
    		}
    		return file;
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
    
    public void addColumnAndRowLabel()
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
    
}
