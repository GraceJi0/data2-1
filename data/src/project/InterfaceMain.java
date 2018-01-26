package project;

import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class InterfaceMain 
{
    private JFrame mainFrame;
    private JPanel controlPanel;
    private String fileName;
    private File currentFile;
    
    private JCheckBox replaceCheckBox;
    private JCheckBox replaceSpaceInHeaders;
    
    private List<String> selectedChoicesRow;
    private List<String> selectedChoicesColumn;
    private int columnNum;
    private int rowNum;
    private JTable rowTable;
    private JTable columnTable;
    private JTable fileTable;
    
    private EditFile editFile;
    private String[][] fileData;
    private String[] columnLabel;
    private JScrollPane fileScroll;
    private JPanel textPanel;
    
    private String[] choices1;
    private String[] choices2;
    private JComboBox<String> columnCombo;
    private JComboBox<String> rowCombo;
    private JPanel columnOperationPanel;
    private JPanel rowOperationPanel;
    private JPanel columnRowPanel;
    private JPanel leftPanel;
    private JPanel columnControlPanel;
    private JPanel rowControlPanel;
    
    
    public InterfaceMain(File currentFile, JPanel gui) 
    {
    		
    		editFile = new EditFile(currentFile);
        controlPanel = new JPanel();
        columnNum = 0;
        rowNum = 0;
        this.currentFile = currentFile;
        fileName = this.currentFile.getName();
        selectedChoicesColumn = new ArrayList<String>();
        selectedChoicesRow = new ArrayList<String>();
        if(!setFileTable("\t",gui)) //if there's no error when set the file table
        {
	        setInterface();
	        addMenuToFrame();
        }
        if(mainFrame != null)
        {
        		mainFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }
    
    //set editor interface
    public void setInterface() 
    {
        //**********************set main frame******************************
    	mainFrame = new JFrame("Editor");
        //mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setPreferredSize(new Dimension(900, 700));
        mainFrame.setMinimumSize(new Dimension(900, 700));
        
        //***************set components at the top contain file name and split help information
        String splitInformation;
        if(editFile.getMyFileExtension().equals("xlsx"))
        {
        		splitInformation = editFile.getSheetName();
        }
        else
        {
         splitInformation = 
            "\nIf the file is not well displayed, please try other ways in top menu \"split\"";
        }
        JPanel fileInformation = new JPanel();
        fileInformation.setLayout(new GridLayout(2,1));
        JLabel fileNameLabel = new JLabel(fileName);
        fileNameLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        JLabel splitLabel = new JLabel(splitInformation);
        splitLabel.setForeground(Color.red);
        fileInformation.add(fileNameLabel);
        fileInformation.add(splitLabel);
        
        //*******************set file panel************************
        textPanel = new JPanel();
        textPanel.setLayout(new BorderLayout(2,2));
        textPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 8)); 
        
        //********************set components in checkbox panel(edit file operations)*******************
        //JCheckBox test1 = new JCheckBox("Remove header");
        replaceCheckBox = new JCheckBox("Replace Missing Data");
        replaceSpaceInHeaders = new JCheckBox("Edit headers ");
        replaceCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) 
                {
                		int option = showReplaceDataDialog();
                		if(option != 0)
                		{
                			replaceCheckBox.setSelected(false);
                		}
                }
                else
                	{
                		editFile.setMissingCh(null);
                		editFile.setReplaceCh(null);
                	};
            }
        });
        replaceSpaceInHeaders.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) 
                {
                		int option = showReplaceSpaceInHeaderDialog();
                		if(option != 0)
                		{
                			
                		}
                }
                else
                	{
                		
                	};
            }
        });
        
        //**************set components in columns panel******************* 
        String[] columnNames = {"Columns to remove"};
        String[][] columnData = null;
        columnTable = new JTable(new DefaultTableModel(columnData, columnNames));
        columnTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        columnTable.setPreferredScrollableViewportSize(new Dimension(12, 10));
        columnTable.setFillsViewportHeight(true);
        JScrollPane columnScroll = new JScrollPane(columnTable);
        JPanel columnPanel = new JPanel();
        columnPanel.setLayout(new GridLayout(1,1));
        columnPanel.add(columnScroll);
        
        columnOperationPanel = new JPanel();
        columnOperationPanel.setLayout(new GridLayout(2,1));
        setColumnComoboBox();
        
        JButton deleteColumnButton = new JButton("Delete");
        columnOperationPanel.add(deleteColumnButton);
        columnOperationPanel.add(columnCombo);
        deleteColumnButton.addActionListener(new ActionListener()
                                                 {
            public void actionPerformed(ActionEvent ae) 
            {
                //delete the selected column in table.
                int row = columnTable.getSelectedRow();
                if(row > -1)
                {
                    String selectedItem = ((DefaultTableModel) columnTable.getModel()).getValueAt(row, 0).toString();
                    selectedChoicesColumn.remove(selectedItem);
                    ((DefaultTableModel)columnTable.getModel()).removeRow(row);
                }
            }
        });
        
        //*******************set select row panel*********************
        String[] rowNames = {"Rows to remove"};
        String[][] rowData = null;
        rowTable = new JTable(new DefaultTableModel(rowData, rowNames));
        rowTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rowTable.setPreferredScrollableViewportSize(new Dimension(12, 10));
        rowTable.setFillsViewportHeight(true);
        JScrollPane rowScroll = new JScrollPane(rowTable);
        JPanel rowPanel= new JPanel();
        rowPanel.setLayout(new GridLayout(1,1));
        rowPanel.add(rowScroll);
        
        rowOperationPanel = new JPanel();
        rowOperationPanel.setLayout(new GridLayout(2,1));
        setRowComboBox();
        
        JButton deleteRowButton = new JButton("Delete");
        rowOperationPanel.add(deleteRowButton);
        rowOperationPanel.add(rowCombo);
        deleteRowButton.addActionListener(new ActionListener()
                                              {
            public void actionPerformed(ActionEvent ae) 
            {
                //delete the selected row in table.
                int row = rowTable.getSelectedRow();
                if(row >-1)
                {
                    String selectedItem = ((DefaultTableModel) rowTable.getModel()).getValueAt(row, 0).toString();
                    selectedChoicesRow.remove(selectedItem);
                    ((DefaultTableModel)rowTable.getModel()).removeRow(row);
                }
            }
        });
        
        //*****************set components in bottom panel*****************
        JButton saveBtn = new JButton("Save");
        saveBtn.setPreferredSize(new Dimension(160, 40));
        saveBtn.setToolTipText("Click to save the file");
        JButton clearBtn = new JButton("Clear");
        clearBtn.setPreferredSize(new Dimension(160, 40));
        JButton saveAsBtn = new JButton("Save as");
        saveAsBtn.setPreferredSize(new Dimension(160, 40));
        JButton convertBtn = new JButton("Convert");
        convertBtn.setPreferredSize(new Dimension(160, 40));
        JButton closeBtn = new JButton("Close");
        closeBtn.setPreferredSize(new Dimension(160, 40));
        
        saveBtn.addActionListener(new ActionListener()
                                      {
            public void actionPerformed(ActionEvent ae) 
            {
                if(showConfirmBox("Do you want to save the changes?", "Save") == JOptionPane.YES_OPTION)
                {
                		//editFile.writeBack();
                		refreshGUI(editFile.getSplitExpression());
                		//rename function JOPtionPane
                		
                }
            }
        });
        clearBtn.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ae) 
            {
                if(showConfirmBox("Do you want to clear all changes?", "Clear") == JOptionPane.YES_OPTION)
                {
                    clearAllChanges();
                }
                
            }
        });
        saveAsBtn.addActionListener(new ActionListener()
                                        {
            public void actionPerformed(ActionEvent ae) 
            {
                saveAsFile();
            }
        });
        closeBtn.addActionListener(new ActionListener()
                                         {
            public void actionPerformed(ActionEvent ae) 
            {
               mainFrame.dispose();
            }
        });
        convertBtn.addActionListener(new ActionListener()
        {
			public void actionPerformed(ActionEvent ae) 
			{
				try 
				{
					String command = "java -Xmx1024m -Xms512m -jar /Users/dinghanji/Downloads/PGDSpider_2.1.1.3/PGDSpider2.jar";
					Runtime.getRuntime().exec(command);
				}
				catch (IOException e) 
				{
					
				}
			}
		});
        
        //*****************set left side panel(check box, select row, select column)*****************
        JPanel checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new GridLayout(4, 1));
        checkboxPanel.setBorder(BorderFactory.createTitledBorder("test"));
        checkboxPanel.add(replaceCheckBox);
        checkboxPanel.add(replaceSpaceInHeaders);
        
        columnControlPanel = new JPanel();
        columnControlPanel.setLayout(new BorderLayout());
        columnControlPanel.setBorder(BorderFactory.createTitledBorder("Select Columns"));
        columnControlPanel.add(columnOperationPanel, BorderLayout.SOUTH);
        columnControlPanel.add(columnPanel,BorderLayout.CENTER);
        
        rowControlPanel = new JPanel();
        rowControlPanel.setLayout(new BorderLayout());
        rowControlPanel.setBorder(BorderFactory.createTitledBorder("Select Rows"));
        rowControlPanel.add(rowOperationPanel, BorderLayout.SOUTH);
        rowControlPanel.add(rowPanel, BorderLayout.CENTER);
        
        leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        columnRowPanel = new JPanel();
        columnRowPanel.setLayout(new GridLayout(2,1));
        columnRowPanel.add(columnControlPanel);
        columnRowPanel.add(rowControlPanel);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(2, 8, 10, 0));
        leftPanel.add(checkboxPanel,BorderLayout.NORTH);
        leftPanel.add(columnRowPanel, BorderLayout.CENTER);
        
        //*****************set bottom panel(buttons)*****************
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(1,5,0,20));
        bottomPanel.setBorder(new EmptyBorder(0,0,5,0));
        bottomPanel.add(saveBtn);
        bottomPanel.add(saveAsBtn);
        bottomPanel.add(clearBtn);
        bottomPanel.add(convertBtn);
        bottomPanel.add(closeBtn);
        
        //********************set text panel(file name,file, buttons)***************
        textPanel.add(fileInformation,BorderLayout.NORTH);
        textPanel.add(fileScroll, BorderLayout.CENTER);
        textPanel.add(bottomPanel,BorderLayout.SOUTH);
        
        //****************set control panel*****************
        Border blackBorder = BorderFactory.createLineBorder(Color.black);
        controlPanel.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(5,5,5,5),blackBorder));
        controlPanel.setLayout(new BorderLayout(10,10));
        controlPanel.add(leftPanel, BorderLayout.WEST);
        controlPanel.add(textPanel,BorderLayout.CENTER);
        
        //*************add control panel to main frame***************
        mainFrame.add(controlPanel);
        mainFrame.setVisible(true);
    }
    
    //set components in top menu
    public void addMenuToFrame() 
    {
        final JMenuBar menuBar = new JMenuBar();
        //**********create menus**********
        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");
        JMenu splitMenu = new JMenu("Split");
        JMenu helpMenu = new JMenu("Help");
        
        //**********create menu items**********
        JMenuItem openMenuItem = new JMenuItem("Open");
        openMenuItem.setActionCommand("Open");
        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.setActionCommand("Save");
        /*JMenuItem cutMenuItem = new JMenuItem("Cut");
         cutMenuItem.setActionCommand("Cut");*/
        JMenuItem replaceMenuItem = new JMenuItem("Replace");
        JMenuItem splitByCommaMenuItem = new JMenuItem("Comma");
        JMenuItem splitBySpaceMenuItem = new JMenuItem("Space");
        JMenuItem splitByTabMenuItem = new JMenuItem("Tab");
        JMenuItem splitBySemicolonMenuitem = new JMenuItem("Semicolon");
        JMenuItem splitByLineMenuItem = new JMenuItem("Line");
        
        //**********add menu items to menus**********
        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        // fileMenu.addSeparator();
        //editMenu.add(cutMenuItem);
        editMenu.add(replaceMenuItem);
        splitMenu.add(splitByCommaMenuItem);
        splitMenu.add(splitBySpaceMenuItem);
        splitMenu.add(splitByTabMenuItem);
        splitMenu.add(splitBySemicolonMenuitem);
        splitMenu.add(splitByLineMenuItem);
        
        //**********add menu to menu bar**********
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        if(!editFile.getMyFileExtension().equals("xlsx"))
        {
        		menuBar.add(splitMenu);
        }
        menuBar.add(helpMenu);
        
        //**********add menu bar to the frame**********
        mainFrame.setJMenuBar(menuBar);
        mainFrame.setVisible(true); 
        
        //*********set menu item listener********
        splitBySpaceMenuItem.addActionListener(new MenuItemListener()
                                                   {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                refreshGUI("\\s+");
            }
        });
        
        splitByCommaMenuItem.addActionListener(new MenuItemListener()
                                                   {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                refreshGUI(",");
            }
        });
        
        splitByTabMenuItem.addActionListener(new MenuItemListener()
                                                 {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                refreshGUI("\t");
            }
        });
        
        splitBySemicolonMenuitem.addActionListener(new MenuItemListener()
                                                       {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                refreshGUI(";");
            }
        });
        
        splitByLineMenuItem.addActionListener(new MenuItemListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				refreshGUI("line"); // if the file split by line, just display the file by line
			}
		});
        
        replaceMenuItem.addActionListener(new MenuItemListener()
                                              {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                
            }
        });
    }
    
    public JFrame getMainFrame() 
    {
        return mainFrame;
    }
    
    public int showConfirmBox(String confirmMessage, String title)
    {
        return JOptionPane.showConfirmDialog (null, confirmMessage, title, JOptionPane.YES_NO_OPTION);
    }
    
    public void duplicatSelectedAlert(String alertMessage)
    {
        JOptionPane.showConfirmDialog(null, alertMessage, "Error", JOptionPane.CLOSED_OPTION);
    }
    
    //*********clear all changes that will happen on the file***********
    public void clearAllChanges()
    {
        ((DefaultTableModel)rowTable.getModel()).setRowCount(0);
        ((DefaultTableModel)columnTable.getModel()).setRowCount(0);
        selectedChoicesRow.removeAll(selectedChoicesRow);
        selectedChoicesColumn.removeAll(selectedChoicesColumn);
        replaceCheckBox.setSelected(false);
        editFile.setMissingCh(""); //clear missing data and replace data
		editFile.setReplaceCh("");
    }
    
    public void saveAsFile()
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("."));
        chooser.setDialogTitle("Save as");
        if(chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
        {
            ///////////////////////////////////////////////////////
        }
    }
    
    //******call method to edit the file and move it form ArrayList to array*******
    public boolean getTableFileData(String expression, JPanel gui)
    {
        boolean error = editFile.editTheFile(expression, gui);
        if(!error)
        {
	        //gui.setCursor(new Cursor(Cursor.WAIT_CURSOR));
	        rowNum = editFile.getRowNum();
	        columnNum = editFile.getColumnNum();
	        List<List<String>> fileArray = editFile.getFileArray();
	        columnLabel = new String[columnNum];
	        columnLabel[0] = "";
	        for(int i = 1; i < columnNum; i++)
	        {
	            columnLabel[i] = "column" + i;
	        }
	        
	        fileData = new String[rowNum][columnNum];
	        for(int i = 0; i < rowNum; i++ )//copy data from ArrayList to Array 
	        {
	            fileArray.get(i).toArray(fileData[i]);
	        }
        }
        return error;
    }
    
    //******set the JTable with existing file array ********
    public boolean setFileTable(String expression, JPanel gui)
    {
        boolean error = getTableFileData(expression, gui);
        if(!error)
        {
	        //SwingUtilities.invokeLater(new Runnable() 
	        //{
	        // public void run() 
	        // {
	        fileTable = new JTable();
	        fileTable.setModel(new DefaultTableModel(fileData, columnLabel));
	        fileTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	        fileTable.setFillsViewportHeight(true);
	        fileTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	        fileTable.setPreferredScrollableViewportSize(fileTable.getPreferredSize());
	        fileScroll = new JScrollPane(fileTable,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
	                                     JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	        //  }
	        //});
        }
        return error;
    }
    
    //********dynamically set the column combo box(select the column)*********
    public void setColumnComoboBox()
    {
        choices1 = new String[columnNum-1];
        for(int i = 0;i<columnNum-1; i++)
        {
            choices1[i] = "column"+(i+1);
        }
        columnCombo = new JComboBox<String>(choices1);
        columnCombo.setSelectedIndex(-1);
        columnCombo.addItemListener(new ItemListener() 
        {
            @Override
            public void itemStateChanged(ItemEvent e) 
            {
                if(e.getStateChange() == ItemEvent.SELECTED) 
                {
                    String sColumn = columnCombo.getSelectedItem().toString();
                    if(selectedChoicesColumn.contains(sColumn))
                    {
                        duplicatSelectedAlert("This column has already been selected!");
                    }
                    else
                    {
                        //add the selected column to table.
                        DefaultTableModel model = (DefaultTableModel) columnTable.getModel();
                        model.addRow(new String[]{sColumn});
                        selectedChoicesColumn.add(sColumn);
                    }
                }
            }
        });
        
    }
    
  //********dynamically set the row combo box(select the row)*********
    public void setRowComboBox()
    {
        choices2 = new String[rowNum];
        for(int i = 1;i<=rowNum; i++)
        {
            choices2[i-1] = "row"+i;
        }
        rowCombo = new JComboBox<String>(choices2);
        rowCombo.setSelectedIndex(-1);
        rowCombo.addItemListener(new ItemListener() 
                                     {
            @Override
            public void itemStateChanged(ItemEvent e) 
            {
                if(e.getStateChange() == ItemEvent.SELECTED) 
                {
                    String sRow = rowCombo.getSelectedItem().toString();
                    if(selectedChoicesRow.contains(sRow))
                    {
                        duplicatSelectedAlert("This row has already been selected!");
                    }
                    else
                    {
                        //add the selected row to table.
                        DefaultTableModel model = (DefaultTableModel) rowTable.getModel();
                        model.addRow(new String[]{sRow});
                        selectedChoicesRow.add(sRow);
                    }
                }
            }
        });
    }
    
    //*****refresh the GUI after make changes to the file or switch the split model.*****
    public void refreshGUI(String expression)
    {
        textPanel.remove(fileScroll); 
        columnOperationPanel.remove(columnCombo);
        rowOperationPanel.remove(rowCombo);
        columnControlPanel.remove(columnOperationPanel);
        rowControlPanel.remove(rowOperationPanel);
        columnRowPanel.remove(columnControlPanel);
        columnRowPanel.remove(rowControlPanel);
        leftPanel.remove(columnRowPanel);
        controlPanel.remove(leftPanel);
        controlPanel.remove(textPanel);
        mainFrame.remove(controlPanel);
        
        setFileTable(expression, null);
        clearAllChanges();
        setRowComboBox();
        setColumnComoboBox();
        
        columnOperationPanel.add(columnCombo);
        rowOperationPanel.add(rowCombo);
        columnControlPanel.add(columnOperationPanel, BorderLayout.SOUTH);
        rowControlPanel.add(rowOperationPanel,BorderLayout.SOUTH);
        columnRowPanel.add(columnControlPanel);
        columnRowPanel.add(rowControlPanel);
        leftPanel.add(columnRowPanel, BorderLayout.CENTER);
        textPanel.add(fileScroll, BorderLayout.CENTER);
        controlPanel.add(textPanel,BorderLayout.CENTER);
        controlPanel.add(leftPanel, BorderLayout.WEST);
        mainFrame.add(controlPanel);
        mainFrame.setVisible(true);
        mainFrame.revalidate();
        mainFrame.repaint();
    }
    
    //show the replace dialog when user click the replace check box to replace the missing data with other characters
    public int showReplaceDataDialog()
    {
        JTextField missingCh = new JTextField();
        JTextField replaceCh = new JTextField();
        Object[] message = {"(Only one data in a cell)\nReplace missing Value(no comma, space, semicolon)    ",
        		missingCh, " with ", replaceCh,"   "};
        int option = JOptionPane.showConfirmDialog(null, message, "Replace", JOptionPane.OK_CANCEL_OPTION);
        if(option == 0)
        {
        		String missing=missingCh.getText().trim();
        		String replace = replaceCh.getText().trim();
	        if(validReplaceData(missing) && validReplaceData(replace))
	        {
	        		editFile.setMissingCh(missing);
		        editFile.setReplaceCh(replace);
		        editFile.replaceMissingData();
	        }
	        else
	        {
	        		JOptionPane.showConfirmDialog(null,
    				"Can't replace the missing data!\nPlease make sure it's not empty and there's no space, coma and semicolon.", 
                    "Error", JOptionPane.CLOSED_OPTION);
	        		replaceCheckBox.setSelected(false);
	        }
        }
        else
        {
        		replaceCheckBox.setSelected(false);
        }
        return option;
    }
    
    public int showReplaceSpaceInHeaderDialog()
    {
        JTextField selectedHeaderRows = new JTextField();
        JComboBox<String> headerRows  = new JComboBox<String>(choices2);
        JScrollBar headerRowsScroll = new JScrollBar(JScrollBar.HORIZONTAL);
        headerRowsScroll.add(selectedHeaderRows);
        JTextArea replaceCh = new JTextArea();
        Object[] message = {"Select the header position:\n", headerRowsScroll, "\n", headerRows};
        int option = JOptionPane.showConfirmDialog(null, message, "Headers", JOptionPane.OK_CANCEL_OPTION);
        if(option == 0)
        {
        		
        }
        else
        {
        		replaceSpaceInHeaders.setSelected(false);
        }
        return option;
    }
    
    public boolean validReplaceData(String data)
    {
    		boolean valid = true;
    		if(data != null && !data.equals(""))
    		{
    			for(int i = 0; i < data.length(); i++)
    			{
    				char ch = data.charAt(i);
    				if(ch == ',' || ch == ';' || ch ==' ')
    				{
    					valid = false;
    				}
    			}
    		}
    		else
    		{
    			valid = false;
    		}
    		return valid;
    }
}

class MenuItemListener implements ActionListener 
{
    @Override
    public void actionPerformed(ActionEvent e) {} 
}
