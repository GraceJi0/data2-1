package project;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
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
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.FileChooserUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import com.google.common.io.Files;

public class InterfaceMain 
{
    private JFrame mainFrame;
    private JPanel controlPanel;
    private String fileName;
    private File currentFile;
    
    private JLabel fileNameLabel;
    private JPanel fileInformation;
    
    private List<String> selectedChoicesRow;
    private List<String> selectedChoicesColumn;
    private int columnNum;
    private int rowNum;
    private JTextField columnStartTextField;
    private JTextField columnEndTextField;
    private JTextField rowStartTextField;
    private JTextField rowEndTextField;
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
    private JPanel columnInputAndCombo;
    private JPanel rowInputAndCombo;
    private JPanel columnOperationPanel;
    private JPanel rowOperationPanel;
    private JPanel columnRowPanel;
    private JPanel leftPanel;
    private JPanel columnControlPanel;
    private JPanel rowControlPanel;

    private JCheckBox replaceCheckBox;
    private JCheckBox replaceSpaceInHeaders;
    private JCheckBox moveColumn;
    private JCheckBox editHeadersFormat;
    
    private String replaceData;
    private String missingData;
    private int selectedHeaderRowData;
    private int moveColumnIndex;
    
    private String theSheetName;
    
    private LogFile logFile;
    
    public InterfaceMain(File currentFile, JPanel gui, LogFile logFile) 
    {
    		this.logFile = logFile;
    		logFile.setCurrentFile(currentFile);
    		theSheetName = "";
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
        mainFrame.setPreferredSize(new Dimension(900, 700));
        mainFrame.setMinimumSize(new Dimension(900, 700));
        
        //***************set components at the top contain file name and split help information
        String splitInformation;
        String extenssion = editFile.getMyFileExtension();
        if(extenssion.equals("xlsx") || extenssion.equals("xls") || editFile.getRename().equals("xlsx")|| editFile.getRename().equals("xls"))
        {
        		splitInformation = editFile.getSheetName();
        }
        else
        {
         splitInformation = 
            "\nIf the file is not well displayed, please try other ways in top menu \"split\"";
        }
        fileInformation = new JPanel();
        fileInformation.setLayout(new GridLayout(2,1));
        fileNameLabel = new JLabel(fileName);
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
        replaceCheckBox = new JCheckBox("Replace Missing Data");
        replaceSpaceInHeaders = new JCheckBox("Edit headers ");
        moveColumn = new JCheckBox("Move column");
        editHeadersFormat = new JCheckBox("Edit Headers' Format");
        replaceCheckBox.addItemListener(new ItemListener() 
        {
            @Override
            public void itemStateChanged(ItemEvent e) 
            {
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
        replaceSpaceInHeaders.addItemListener(new ItemListener() 
        {
            @Override
            public void itemStateChanged(ItemEvent e) 
            {
                if(e.getStateChange() == ItemEvent.SELECTED) 
                {
	                		int option = showReplaceSpaceInHeaderDialog();
	                		if(option != 0)
	                		{
	                			replaceSpaceInHeaders.setSelected(false);
	                		}
                };
            }
        });
        moveColumn.addItemListener(new ItemListener() 
        {
            @Override
            public void itemStateChanged(ItemEvent e) 
            {
                if(e.getStateChange() == ItemEvent.SELECTED) 
                {
	                		int option = showMoveCloumnDialog();
	                		if(option != 0)
	                		{
	                			moveColumn.setSelected(false);
	                		}
                };
            }
        });
        editHeadersFormat.addItemListener(new ItemListener() 
        {
            @Override
            public void itemStateChanged(ItemEvent e) 
            {
                if(e.getStateChange() == ItemEvent.SELECTED) 
                {
                		int option = editHeadersFormatDialog();
                		if(option != 0)
                		{
                			editHeadersFormat.setSelected(false);
                		}
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
        
        JLabel c2 = new JLabel("from",JLabel.RIGHT);
        JLabel c3 = new JLabel("to",JLabel.RIGHT);
        columnStartTextField = new JTextField("Integer");
        columnEndTextField = new JTextField("Integer");
        
        JPanel columnInputPanel = new JPanel();
        columnInputPanel.setLayout(new GridLayout(2,2));
        columnInputPanel.add(c2);
        columnInputPanel.add(columnStartTextField);
        columnInputPanel.add(c3);
        columnInputPanel.add(columnEndTextField);
        columnInputPanel.setPreferredSize(new Dimension(20,200));
        
        JButton addColumnBtn = new JButton("Add");
        JPanel columnAddBtnPanel = new JPanel();
        columnAddBtnPanel.setLayout(new GridLayout(1,1));
        columnAddBtnPanel.setPreferredSize(new Dimension(40,40));
        columnAddBtnPanel.add(addColumnBtn);
        addColumnBtn.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ae) 
            {
            		if(!moveColumn.isSelected())
            		{
	            		int columnStart;
	            		int columnEnd;
	            		try
	            		{
		                 columnStart = Integer.parseInt(columnStartTextField.getText());
		                 columnEnd = Integer.parseInt(columnEndTextField.getText());
		                 if(columnStart>0 && columnEnd<=editFile.getColumnNum() && columnStart<=columnEnd)
		                 {
		                	 	addIndexGroup( columnStart, columnEnd, selectedChoicesColumn, columnTable, "column");
		                 }
		                 else
		                 {
		                	 	JOptionPane.showConfirmDialog(null,"Invalid column number!", "Error", JOptionPane.CLOSED_OPTION);
		                 }
	            		}
	            		catch(NumberFormatException er)
	            		{
	            			JOptionPane.showConfirmDialog(null,"Please entre integers!", "Error", JOptionPane.CLOSED_OPTION);
	            			columnStartTextField.setText("Integer");
	            			columnEndTextField.setText("Integer");
	            		}
            		}
            		else
            		{
            			JOptionPane.showConfirmDialog(null, "This function can not be used with \"Move columns\".", 
            					"Error", JOptionPane.CLOSED_OPTION);
            			columnStartTextField.setText("Integer");
            			columnEndTextField.setText("Integer");
            		}
            }
        });
       
        JPanel columnInput = new JPanel();
        columnInput.setLayout(new BorderLayout(6,6));
        columnInput.setBorder(new EmptyBorder(10,0,10,10));
        columnInput.add(columnInputPanel,BorderLayout.CENTER);
        columnInput.add(columnAddBtnPanel,BorderLayout.EAST);
        
        setColumnComoboBox();
        
        columnInputAndCombo = new JPanel();
        columnInputAndCombo.setLayout(new BorderLayout());
        columnInputAndCombo.add(columnInput, BorderLayout.CENTER);
        columnInputAndCombo.add(columnCombo, BorderLayout.NORTH);
        columnOperationPanel = new JPanel();
        columnOperationPanel.setLayout(new GridLayout(2,1));
        
        columnOperationPanel.add(columnPanel);
        columnOperationPanel.add(columnInputAndCombo);

        JButton deleteColumnButton = new JButton("Delete");
        
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
        
        JLabel r2 = new JLabel("from",JLabel.RIGHT);
        JLabel r3 = new JLabel("to",JLabel.RIGHT);
        rowStartTextField = new JTextField("Integer");
        rowEndTextField = new JTextField("Integer");
        
        JPanel rowInputPanel = new JPanel();
        rowInputPanel.setLayout(new GridLayout(2,2));
        rowInputPanel.add(r2);
        rowInputPanel.add(rowStartTextField);
        rowInputPanel.add(r3);
        rowInputPanel.add(rowEndTextField);
        rowInputPanel.setPreferredSize(new Dimension(20,200));
        
        JButton addRowBtn = new JButton("Add");
        JPanel rowAddBtnPanel = new JPanel();
        rowAddBtnPanel.setLayout(new GridLayout(1,1));
        rowAddBtnPanel.setPreferredSize(new Dimension(40,40));
        rowAddBtnPanel.add(addRowBtn);
        addRowBtn.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ae) 
            {
            		if(!replaceSpaceInHeaders.isSelected() && !editHeadersFormat.isSelected())
            		{
	            		int rowStart;
	            		int rowEnd;
	            		try
	            		{
	            			rowStart = Integer.parseInt(rowStartTextField.getText());
	            			rowEnd = Integer.parseInt(rowEndTextField.getText());
		                 if(rowStart >0 && rowEnd <= editFile.getRowNum() && rowStart<=rowEnd)
		                 {
		                	 	addIndexGroup( rowStart, rowEnd, selectedChoicesRow, rowTable, "row");
		                 }
		                 else
		                 {
		                	 	JOptionPane.showConfirmDialog(null,"Invalid row number", "Error", JOptionPane.CLOSED_OPTION);
		                 }
	            		}
	            		catch(NumberFormatException er)
	            		{
	            			JOptionPane.showConfirmDialog(null,"Please entre integers!", "Error", JOptionPane.CLOSED_OPTION);
	            			rowStartTextField.setText("Integer");
	            			rowEndTextField.setText("Integer");
	            		}
            		}
            		else
            		{
            			JOptionPane.showConfirmDialog(null, "This function can not be used with \"Edit headers\" and \"Edit headers' format\".", 
            					"Error", JOptionPane.CLOSED_OPTION);
            			rowStartTextField.setText("Integer");
            			rowEndTextField.setText("Integer");
            		}
            }
        });
       
        JPanel rowInput = new JPanel();
        rowInput.setLayout(new BorderLayout(6,6));
        rowInput.setBorder(new EmptyBorder(10,0,10,10));
        rowInput.add(rowInputPanel,BorderLayout.CENTER);
        rowInput.add(rowAddBtnPanel,BorderLayout.EAST);
        
        setRowComboBox();
        
        rowInputAndCombo = new JPanel();
        rowInputAndCombo.setLayout(new BorderLayout());
        rowInputAndCombo.add(rowInput, BorderLayout.CENTER);
        rowInputAndCombo.add(rowCombo, BorderLayout.NORTH);
        rowOperationPanel = new JPanel();
        rowOperationPanel.setLayout(new GridLayout(2,1));
        
        rowOperationPanel.add(rowPanel);
        rowOperationPanel.add(rowInputAndCombo);

        JButton deleteRowButton = new JButton("Delete");
        
        deleteRowButton.addActionListener(new ActionListener()
                                                 {
            public void actionPerformed(ActionEvent ae) 
            {
                //delete the selected column in table.
                int row = rowTable.getSelectedRow();
                if(row > -1)
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
        JButton fastConvertBtn = new JButton("Fast convert");
        fastConvertBtn.setPreferredSize(new Dimension(160, 40));
        JButton closeBtn = new JButton("Close");
        closeBtn.setPreferredSize(new Dimension(160, 40));
        
        saveBtn.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ae) 
            {
                if(showConfirmBox("Do you want to save the changes?", "Save") == JOptionPane.YES_OPTION)
                {
                		updateFile();
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
        fastConvertBtn.addActionListener(new ActionListener()
        {
			public void actionPerformed(ActionEvent ae) 
			{
				FastConvert fastConvert = new FastConvert(currentFile);
				fastConvert.fastConvertDialog();
			}
		});
        
        //*****************set left side panel(check box, select row, select column)*****************
        JPanel checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new GridLayout(4, 1));
        checkboxPanel.setBorder(BorderFactory.createTitledBorder("Select"));
        checkboxPanel.add(replaceCheckBox);
        checkboxPanel.add(replaceSpaceInHeaders);
        checkboxPanel.add(moveColumn);
        checkboxPanel.add(editHeadersFormat);
        
        columnControlPanel = new JPanel();
        columnControlPanel.setLayout(new BorderLayout());
        columnControlPanel.setBorder(BorderFactory.createTitledBorder("Remove Columns"));
        columnControlPanel.add(deleteColumnButton,BorderLayout.SOUTH);
        columnControlPanel.add(columnOperationPanel,BorderLayout.CENTER);
        
        rowControlPanel = new JPanel();
        rowControlPanel.setLayout(new BorderLayout());
        rowControlPanel.setBorder(BorderFactory.createTitledBorder("Remove Rows"));
        rowControlPanel.add(deleteRowButton, BorderLayout.SOUTH);
        rowControlPanel.add(rowOperationPanel, BorderLayout.CENTER);
        
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
        bottomPanel.add(fastConvertBtn);
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
        JMenu splitMenu = new JMenu("Split");
        JMenu helpMenu = new JMenu("Help");
        
        //**********create menu items**********
        JMenuItem convertMenuItem = new JMenuItem("Convert");
        /*JMenuItem cutMenuItem = new JMenuItem("Cut");
         cutMenuItem.setActionCommand("Cut");*/;
        JMenuItem splitByCommaMenuItem = new JMenuItem("Comma");
        JMenuItem splitBySpaceMenuItem = new JMenuItem("Space");
        JMenuItem splitByTabMenuItem = new JMenuItem("Tab");
        JMenuItem splitBySemicolonMenuitem = new JMenuItem("Semicolon");
        JMenuItem splitByLineMenuItem = new JMenuItem("Line");
        
        //**********add menu items to menus**********
        fileMenu.add(convertMenuItem);
        // fileMenu.addSeparator();
        //editMenu.add(cutMenuItem);
        splitMenu.add(splitByCommaMenuItem);
        splitMenu.add(splitBySpaceMenuItem);
        splitMenu.add(splitByTabMenuItem);
        splitMenu.add(splitBySemicolonMenuitem);
        splitMenu.add(splitByLineMenuItem);
        
        //**********add menu to menu bar**********
        menuBar.add(fileMenu);
        String extenssion = editFile.getMyFileExtension();
        if(!extenssion.equals("xlsx") && !extenssion.equals("xls") && !editFile.getRename().equals("xlsx") && !editFile.getRename().equals("xls"))
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
            			refreshGUI(" ");
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
        
        convertMenuItem.addActionListener(new MenuItemListener()
        {
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				FastConvert detailConvert = new FastConvert(currentFile);
				detailConvert.runDetailConvert();
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
        columnStartTextField.setText("Integer");
        columnEndTextField.setText("Integer");
        rowStartTextField.setText("Integer");
        rowEndTextField.setText("Integer");
        replaceCheckBox.setSelected(false);
        replaceSpaceInHeaders.setSelected(false);
        editHeadersFormat.setSelected(false);
        moveColumn.setSelected(false);
        editFile.setMissingCh(""); //clear missing data and replace data
		editFile.setReplaceCh("");
    }
    
    //save as the file and allow users to select the save location
    public void saveAsFile()
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save as");
        chooser.setSelectedFile(currentFile);
        if(chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
        {
        		try
        		{
        			File newFile = chooser.getSelectedFile();
        			String newPath = newFile.getAbsolutePath();
        			File tempFile;
        			/*if(getFileExtension(newFile).equals(""))
        			{*/
        				 tempFile = new File(newPath+"."+editFile.getMyFileExtension());
        			/*}
        			else
        			{
        				tempFile = new File(newPath);
        			}*/
        			Files.copy(currentFile, tempFile);
        			currentFile = tempFile;
        			if(getFileExtension(currentFile) != null || !getFileExtension(currentFile).equals(""))
        			{
        				editFile.setRename("");
        			}
        			updateFile();
        			newFile.delete();
            }
        		catch (Exception ex) 
        		{
                ex.printStackTrace();
            }
        }
    }
    
    //******call method to edit the file and move it form ArrayList to array*******
    public boolean getTableFileData(String expression, JPanel gui)
    {
        boolean error = editFile.editTheFile(expression, gui, theSheetName);
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
	        fileTable = new JTable();
	        fileTable.setModel(new DefaultTableModel(fileData, columnLabel));
	        fileTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	        fileTable.setFillsViewportHeight(true);
	        fileTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	        fileTable.setPreferredScrollableViewportSize(fileTable.getPreferredSize());
	        fileScroll = new JScrollPane(fileTable,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
	                                     JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	        fileTable.getColumnModel().getColumn(0).setCellRenderer(new TableCellRenderer() 
	        {
	            @Override
	            public Component getTableCellRendererComponent(JTable x, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
	            {
	                Component component = fileTable.getTableHeader().getDefaultRenderer().getTableCellRendererComponent(fileTable, value, false, false, -1, -2);
	                ((JLabel) component).setHorizontalAlignment(SwingConstants.CENTER);
	                return component;
	            }
	        });
        }
        return error;
    }
    
    //add a group of column or row index to the selected table
    public void addIndexGroup(int start, int end, List<String> selectedIndexList, JTable table, String title)
    {
    		for(int i = start; i<=end; i++)
    		{
    			String label = title+i;
    			selectedIndexList.add(label);
    			DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.addRow(new String[]{label});
    		}
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
                		if(!moveColumn.isSelected())
                		{
	                    String sColumn = columnCombo.getSelectedItem().toString();
	                    if(selectedChoicesColumn.contains(sColumn))
	                    {
	                        duplicatSelectedAlert("This column has already been selected!");
	                    }
	                    else
	                    {
	                        DefaultTableModel model = (DefaultTableModel) columnTable.getModel();
	                        model.addRow(new String[]{sColumn});
	                        selectedChoicesColumn.add(sColumn);
	                    }
                		}
                		else
                		{
                			JOptionPane.showConfirmDialog(null, "This function can not be used with \"Move columns\".", 
                					"Error", JOptionPane.CLOSED_OPTION);
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
                		if(!replaceSpaceInHeaders.isSelected() && !editHeadersFormat.isSelected())
                		{
	                    String sRow = rowCombo.getSelectedItem().toString();
	                    if(selectedChoicesRow.contains(sRow))
	                    {
	                        duplicatSelectedAlert("This row has already been selected!");
	                    }
	                    else
	                    {
	                        DefaultTableModel model = (DefaultTableModel) rowTable.getModel();
	                        model.addRow(new String[]{sRow});
	                        selectedChoicesRow.add(sRow);
	                    }
                		}
                		else
                		{
                			JOptionPane.showConfirmDialog(null, "This function can not be used with \"Edit headers\" and \"Edit headers' format\".", 
                					"Error", JOptionPane.CLOSED_OPTION);
                		}
                }
            }
        });
    }
    
    //*****refresh the GUI after make changes to the file or switch the split model.*****
    public void refreshGUI(String expression)
    {
    		fileInformation.remove(fileNameLabel);
    		textPanel.remove(fileInformation);
        textPanel.remove(fileScroll); 
        columnInputAndCombo.remove(columnCombo);
        columnOperationPanel.remove(columnInputAndCombo);
        columnControlPanel.remove(columnOperationPanel);
        rowInputAndCombo.remove(rowCombo);
        rowOperationPanel.remove(rowInputAndCombo);
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
        
        fileNameLabel.setText(currentFile.getName());
        fileInformation.add(fileNameLabel);
        textPanel.add(fileInformation,BorderLayout.NORTH);
        columnInputAndCombo.add(columnCombo, BorderLayout.NORTH);
        columnOperationPanel.add(columnInputAndCombo);
        columnControlPanel.add(columnOperationPanel,BorderLayout.CENTER);
        rowInputAndCombo.add(rowCombo, BorderLayout.NORTH);
        rowOperationPanel.add(rowInputAndCombo);
        rowControlPanel.add(rowOperationPanel,BorderLayout.CENTER);
        columnRowPanel.add(columnControlPanel);
        columnRowPanel.add(rowControlPanel);
        leftPanel.add(columnRowPanel, BorderLayout.CENTER);
        textPanel.add(fileScroll, BorderLayout.CENTER);
        controlPanel.add(textPanel,BorderLayout.CENTER);
        controlPanel.add(leftPanel, BorderLayout.WEST);
        mainFrame.add(controlPanel);
        //mainFrame.setVisible(true);
        mainFrame.revalidate();
        mainFrame.repaint();
    }
    
    //after save or save as, update the file and refresh GUI.
    public void updateFile()
    {
    		editFile.setCurrentFile(currentFile);
    		addLogFileString();
		logFile.writeToLogEditFile();
		mainFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		if(replaceCheckBox.isSelected())
		{
			editFile.setMissingCh(missingData);
			editFile.setReplaceCh(replaceData);
			editFile.replaceMissingData();
		}
		if(replaceSpaceInHeaders.isSelected())
		{
			editFile.replaceSpaceInHeader(selectedHeaderRowData);
		}
		if(moveColumn.isSelected())
		{
			editFile.moveColumn(moveColumnIndex);
		}
		if(!selectedChoicesRow.isEmpty())
		{
			editFile.deleteRow(selectedChoicesRow);
		}
		if(!selectedChoicesColumn.isEmpty())
		{
			editFile.deleteColumn(selectedChoicesColumn);
		}
		String expression = editFile.getSplitExpression();
		theSheetName = editFile.getSheetName();
		currentFile = editFile.writeBack(editFile.getRename());
		editFile = new EditFile(currentFile);
		refreshGUI(expression);
		mainFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    //show the replace dialog when user click the replace check box to replace the missing data with other characters
    public int showReplaceDataDialog()
    {
        JTextField missingCh = new JTextField();
        JTextField replaceCh = new JTextField();
        Object[] message = {"\nReplace missing data\n(no comma, space, semicolon)    ", missingCh, " with ", replaceCh,"   "};
        int option = JOptionPane.showConfirmDialog(null, message, "Replace", JOptionPane.OK_CANCEL_OPTION);
        if(option == 0)
        {
        		missingData = missingCh.getText().trim();
        		replaceData = replaceCh.getText().trim();
	        if(!validReplaceData(missingData) || !validReplaceData(replaceData))
	        {
	        		JOptionPane.showConfirmDialog(null,
	    				"Can't replace the missing data!\nPlease make sure it's not empty and there's no space, coma and semicolon.", 
	                    "Error", JOptionPane.CLOSED_OPTION);
		        		option = -1;
	        }
        }
        return option;
    }
    
    //check if the missing data and replace data are valid
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
    
    //replace the spaces in a specific header
    public int showReplaceSpaceInHeaderDialog()
    {
    		int option;
    		if(selectedChoicesRow.isEmpty())
		{
	        JTextField selectedHeaderRow = new JTextField();
	        selectedHeaderRow.setText("1");
	        Object[] message = {"Replace spaces in header at row", selectedHeaderRow, "(row number, no space)","\nwith underscores"};
	        option = JOptionPane.showConfirmDialog(null, message, "Headers", JOptionPane.OK_CANCEL_OPTION);
	        if(option == 0)
	        {
	        		selectedHeaderRowData = Integer.parseInt(selectedHeaderRow.getText().trim())-1;
	        		if(!(selectedHeaderRowData<rowNum)||(editFile.getFileArray().get(selectedHeaderRowData)== null))
	        		{
	        			option = -1;
	        		}
	        }
	        else
	        {
	        		editFile.setKeepChangedFile(false);
	        }
		}
    		else
    		{
    			option = -1;
    			JOptionPane.showConfirmDialog(null, "This function can not be used with \"Remove rows\".", "Error", JOptionPane.OK_CANCEL_OPTION);
    		}
        return option;
    }
    
    //move the selected column to the end of the file
    public int showMoveCloumnDialog()
    {
    		int option;
    		if(selectedChoicesColumn.isEmpty())
    		{
	    		JTextField selectedColumn = new JTextField();
	    		selectedColumn.setText("1");
	        Object[] message = {"Move column", selectedColumn, "(column number, no space)","\nto the end of the file"};
	        option = JOptionPane.showConfirmDialog(null, message, "Move Cloumn", JOptionPane.OK_CANCEL_OPTION);
	        if(option == 0)
	        {	
	        		moveColumnIndex = Integer.parseInt(selectedColumn.getText().trim());
	        		if(moveColumnIndex>columnNum)
	        		{
	        			JOptionPane.showConfirmDialog(null,
	    	    				"The column number is not valid!", 
	    	                    "Error", JOptionPane.CLOSED_OPTION);
	        			option = -1;
	        		}
	        }
	        else
	        {
	        		editFile.setKeepChangedFile(false);
	        }
    		}
    		else
    		{
    			option = -1;
    			JOptionPane.showConfirmDialog(null, "This function can not be used with \"Remove columns\".", "Error", JOptionPane.CLOSED_OPTION);
    		}
        return option;
    }
    
    //Delete every second headers
    public int editHeadersFormatDialog()
    {
    		int option;
    		if(selectedChoicesColumn.isEmpty() && selectedChoicesRow.isEmpty())
    		{
	        JTextField startColumn = new JTextField();
	        JTextField endColumn = new JTextField();
	        JTextField theRowNumber = new JTextField();
	        Object[] message = {"(Delete every second headers \"ID X1 X2 Y1 Y2\" to \"ID X Y\")",
	        		"Select headers from column (start column number)", startColumn, "to column (end column number)",endColumn,
	        		"at row (row number)",theRowNumber};
	        option = JOptionPane.showConfirmDialog(null, message, "Edit headers' format", JOptionPane.OK_CANCEL_OPTION);
	        if(option == 0)
	        {
	        		String startColumnString = startColumn.getText();
	        		String endColumnString = endColumn.getText();
	        		String theRowNumberString = theRowNumber.getText();
	        		if(!startColumnString.equals("") && !endColumnString.equals("") && !theRowNumberString.equals(""))
	        		{
		        		int startColumnNumber = Integer.parseInt(startColumnString);
		        		int endColumnNumber = Integer.parseInt(endColumnString);
		        		int rowNumber = Integer.parseInt(theRowNumberString);
		        		if(startColumnNumber>=1 && startColumnNumber<columnNum && endColumnNumber>=1 && endColumnNumber<columnNum 
		        				&& rowNumber <= rowNum && endColumnNumber-startColumnNumber>0)
		        		{
		        			editFile.editHeadersFormat(startColumnNumber, endColumnNumber, rowNumber);
			        }
		        		else
		        		{
		        			JOptionPane.showConfirmDialog(null,"The column number or row number is not valid!", 
		    	                    "Error", JOptionPane.CLOSED_OPTION);
		    		        	option = -1;
		        		}
	        		}
	        		else
	        		{
	        			JOptionPane.showConfirmDialog(null,"The column number or row number is not valid!", 
	    	                    "Error", JOptionPane.CLOSED_OPTION);
	    		        	option = -1;
	        		}
	        }
    		}
    		else
    		{
    			option = -1;
    			JOptionPane.showConfirmDialog(null, "This function can not be used with \"Remove columns\" and \"Remove rows\".", 
    					"Error", JOptionPane.CLOSED_OPTION);
    		}
        return option;
    }
    
    //set log file string for all kinds of functions
    public void addLogFileString()
    {
    		logFile.logSelectRows(selectedChoicesRow);
    		logFile.logSelectColumn(selectedChoicesColumn);
    		if(replaceCheckBox.isSelected())
    		{
    			logFile.logMissingData(missingData, replaceData);
    		}
    	    if(replaceSpaceInHeaders.isSelected())
    	    {
    	    		logFile.logEditHeaders(selectedHeaderRowData);
    	    }
    	    if(moveColumn.isSelected())
    	    {
    	    		logFile.logMoveColumn(moveColumnIndex);
    	    }
    	    if(editHeadersFormat.isSelected())
    	    {
    	    		
    	    }
    }
    
    public String getFileExtension(File theFile)
    {
    		String extenssion = "";
    		if(theFile != null)
    		{
	        String fileName = theFile.getName();
	        int index = -1;
	        index = fileName.lastIndexOf('.');
	        if(index > -1)
	        {
	        		extenssion = fileName.substring(index + 1);
	        }
    		}
        return extenssion;
    }
}

class MenuItemListener implements ActionListener 
{
    @Override
    public void actionPerformed(ActionEvent e) {} 
}
