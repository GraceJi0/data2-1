package project;

import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.*;
import org.apache.log4j.chainsaw.Main;

import java.util.List;
import java.awt.*;

public class InterfaceDirectories 
{
	private JFrame mainFrame;
    private Desktop desktop;
    private FileSystemView fileSystemView;
    private JProgressBar progressBar;
    private JSplitPane splitPane;
    private JPanel fileView;
    private DefaultMutableTreeNode currentNode;
    private JPanel gui;
    private File currentFile; //Current file we are looking at
    
	private JEditorPane metaDataTextArea;
    private JEditorPane readmeTextArea;

    private JTree tree; //File tree
    private DefaultTreeModel treeModel;
    private JScrollPane treeScroll;
    
    private JTable table; //File table
    private FileTableModel fileTableModel;
    private ListSelectionListener listSelectionListener;
    private boolean cellSizesSet = false;
    private int rowIconPadding = 6;
    
    private JButton openFile;
    private JButton editFile;
    private JButton unzipFile;
    private JButton deleteBtn;
    private JButton moveToTrashBtn;
    private JLabel fileName;
    
    private LogFile logFile;
    private String logChangesFilePath;
    private String logDeleteFilePath;
    
    public InterfaceDirectories()
    {
    	logFile= new LogFile();
    	logDeleteFilePath = "";
		logChangesFilePath = "";
   		mainFrame = new JFrame();
   		getGui();
   		addMenu();
    }
    
    public Container getGui() 
    {
        if (gui==null) 
        {
            gui = new JPanel(new BorderLayout());
            gui.setBorder(new EmptyBorder(5,5,5,5));
            
            fileSystemView = FileSystemView.getFileSystemView();
            desktop = Desktop.getDesktop();
            
            //******************************Set file table (righ side of GUI)***************************
            JPanel tableAndFileDetails = new JPanel();
            JPanel tablePanel = new JPanel();
            tablePanel.setLayout(new GridLayout(1,1));
            tableAndFileDetails.setLayout(new BorderLayout(5,5));
            table = new JTable();
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.setAutoCreateRowSorter(true);
            table.setShowVerticalLines(false);
            listSelectionListener = new ListSelectionListener() 
            {
                @Override
                public void valueChanged(ListSelectionEvent lse) 
                {
                    editFile.setEnabled(true);
                    unzipFile.setEnabled(true);
                    int row = table.getSelectionModel().getLeadSelectionIndex();
                    RowSorter sorter = table.getRowSorter();
                    if ( sorter != null ) 
                    {
                        row = sorter.convertRowIndexToModel( row );                            
                    }
                    setFileDetails( ((FileTableModel)table.getModel()).getFile(row) );
                    gui.revalidate();
                    gui.repaint();
                }
            };
            table.getSelectionModel().addListSelectionListener(listSelectionListener);
            JScrollPane tableScroll = new JScrollPane(table);
            Dimension d = tableScroll.getPreferredSize();
            tableScroll.setPreferredSize(new Dimension((int)d.getWidth(), (int)d.getHeight()/2));
            tablePanel.add(tableScroll);
            
            //***********************Set the file tree (left side of GUI)**********************
            DefaultMutableTreeNode root = new DefaultMutableTreeNode();
            treeModel = new DefaultTreeModel(root);
            
            TreeSelectionListener treeSelectionListener = new TreeSelectionListener() 
            {
                public void valueChanged(TreeSelectionEvent tse)
                {
                    unzipFile.setEnabled(true); 
                    editFile.setEnabled(true);
                    DefaultMutableTreeNode node =
                        (DefaultMutableTreeNode)tse.getPath().getLastPathComponent();
                    currentNode = node;
                    showChildren(node);
                    setFileDetails((File)node.getUserObject());
                    gui.revalidate();
                    gui.repaint(); 
                }
            };
            
            //*************************Show the file system roots.**************************
            File[] roots = fileSystemView.getRoots();
            for (File fileSystemRoot : roots) 
            {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileSystemRoot);
                root.add( node );
                File[] files = fileSystemView.getFiles(fileSystemRoot, true);
                for (File file : files) 
                {
                    if (file.isDirectory()) 
                    {
                        node.add(new DefaultMutableTreeNode(file));
                    }
                }
            }
            
            tree = new JTree(treeModel);
            tree.setRootVisible(false);
            tree.setShowsRootHandles(true);//////
            tree.addTreeSelectionListener(treeSelectionListener);
            tree.setCellRenderer(new FileTreeCellRenderer());
            tree.expandRow(0);
            treeScroll = new JScrollPane(tree);
            
            tree.setVisibleRowCount(15);
            
            Dimension preferredSize = treeScroll.getPreferredSize();
            Dimension widePreferred = new Dimension(200, (int)preferredSize.getHeight());
            treeScroll.setPreferredSize( widePreferred );
            
            //************************Set file panel with detail components************************
            JPanel fileMainDetails = new JPanel(new BorderLayout(2,0));
            fileMainDetails.setBorder(new EmptyBorder(0,6,0,6));
            
            JPanel fileDetailsLabels = new JPanel(new GridLayout(0,1,0,0));
            fileMainDetails.add(fileDetailsLabels, BorderLayout.WEST);
            
            JPanel fileDetailsValues = new JPanel(new GridLayout(0,1,0,0));
            fileMainDetails.add(fileDetailsValues, BorderLayout.CENTER);
            
            fileDetailsLabels.add(new JLabel("File", JLabel.TRAILING));
            fileName = new JLabel();
            fileDetailsValues.add(fileName);
            
            //**********************Set mete data and README panel********************
            JPanel fileMetaDataPanel = new JPanel();
            fileMetaDataPanel.setLayout(new GridLayout(1,1));
            JTabbedPane metaDataPane = new JTabbedPane();
            
            metaDataTextArea = new JEditorPane();
            metaDataTextArea.setContentType("text/html");
            metaDataTextArea.setEditable(false);
            metaDataTextArea.setOpaque(false);
            metaDataTextArea.addHyperlinkListener(new HyperlinkListener()
            {
				@Override
				public void hyperlinkUpdate(HyperlinkEvent e) 
				{
					if(HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType()))
					{
						try
						{
							desktop.browse(e.getURL().toURI());
						}
						catch(Exception e1)
						{
							e1.printStackTrace();
						}
					}			
				}		
            	});
            JScrollPane metaDataScroll = new JScrollPane(metaDataTextArea,
                                             JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                             JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            Dimension dd = tableScroll.getPreferredSize();
            metaDataScroll.setPreferredSize(new Dimension((int)dd.getWidth(), (int)dd.getHeight()+50));
            
            readmeTextArea = new JEditorPane();
            readmeTextArea.setContentType("text/html");
            readmeTextArea.setEditable(false);
            readmeTextArea.setOpaque(false);
            JScrollPane readmeScroll = new JScrollPane(readmeTextArea,
                                             JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                             JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            readmeScroll.setPreferredSize(new Dimension((int)dd.getWidth(), (int)dd.getHeight()+50));
            
            metaDataPane.add("Meta Data", metaDataScroll);
            metaDataPane.addTab("README", readmeScroll);
            fileMetaDataPanel.add(metaDataPane);
            fileMetaDataPanel.setBorder(new EmptyBorder(10,0,0,0));
            
            //*****************************Set tools panel*******************************
            JPanel toolPanel = new JPanel();
            toolPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

            //If the "Edit" button has been clicked, open the editor window.
            editFile = new JButton("Editor");
            editFile.setSize(new Dimension(100, 50)); 
            editFile.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent ae)
                {
	                	gui.setCursor(new Cursor(Cursor.WAIT_CURSOR));

	            	    JFrame frame = new InterfaceMain(currentFile,gui,logFile).getMainFrame();
	            	    if(frame != null)
	            	    {
	            	        frame.setVisible(true);
	            	        frame.setPreferredSize(new Dimension(900, 700));
	            	    }
	            	    gui.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            });
            toolPanel.add(editFile);
            
            //Open the current file or directory
            openFile = new JButton("Open");
            openFile.setSize(new Dimension(300, 100)); 
            openFile.addActionListener(new ActionListener()
                                           {
                public void actionPerformed(ActionEvent ae) 
                {
                    try 
                    {
                        desktop.open(currentFile);
                    } 
                    catch(Throwable t) 
                    {
                    		JOptionPane.showConfirmDialog(null, "Can't open the file!", 
                         		"Error", JOptionPane.CLOSED_OPTION);
                        showThrowable(t);
                    }
                    gui.revalidate();
                    gui.repaint();
                }
            });
            toolPanel.add(openFile);
            openFile.setEnabled(desktop.isSupported(Desktop.Action.OPEN));
            
            //Open the parent directory of the file
            JButton locateFile = new JButton("Locate");
            locateFile.addActionListener(new ActionListener()
                                             {
                public void actionPerformed(ActionEvent ae) 
                {
                    try 
                    {
                        desktop.open(currentFile.getParentFile());
                    } 
                    catch(Throwable t)
                    {
                    	 	JOptionPane.showConfirmDialog(null, "Can't locate the file!", 
                         		"Error", JOptionPane.CLOSED_OPTION);
                    }
                    gui.repaint();
                }
            });
            toolPanel.add(locateFile);
            
            //Extract a compressed file
            unzipFile = new JButton("Decompress");
            unzipFile.setSize(new Dimension(100, 50)); 
            unzipFile.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent ae) 
                {
					gui.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                		Decompress decompress = new Decompress();
                    String zipFile = currentFile.getAbsolutePath();
                    String destinationFolder = currentFile.getParentFile().getAbsolutePath();
                    String extension = getFileExtension(currentFile);
                    if(extension.equals("zip"))
                    {
                    		decompress.unzip(destinationFolder,zipFile);
                    }
                    else if(extension.equals("tar"))
                    {
						decompress.unTar(currentFile, destinationFolder);
                    }
                    updateFileTreeAndTable();
                    gui.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            });
            toolPanel.add(unzipFile);
            
            //Delete a file or directory
            deleteBtn = new JButton("Delete");
            deleteBtn.setSize(new Dimension(100, 50)); 
            deleteBtn.addActionListener(new ActionListener()
                                            {
                public void actionPerformed(ActionEvent ae) 
                {
                	if(currentFile!= null)
                	{
                		String deleteMessage = "Are you sure you want to permanently delete "+currentFile.getName()+" ?";
                		int option = JOptionPane.showConfirmDialog(null, deleteMessage, "Delete", JOptionPane.OK_CANCEL_OPTION);
                		if(option == 0)
                		{
		                	logFile.setCurrentFile(currentFile);
		                	logFile.writeToLogDeleteFile("Delete File: ");
		                    deleteDrectoriesAndFiles(currentFile);
		                    updateFileTreeAndTable();
                		}
                	}
                }
            });
            toolPanel.add(deleteBtn);
            
          //Move a file to trash
            moveToTrashBtn = new JButton("Move to Trash");
            moveToTrashBtn.setSize(new Dimension(100, 50)); 
            moveToTrashBtn.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent ae) 
                {
                	if(currentFile != null)
                	{
	                	logFile.setCurrentFile(currentFile);
	                	logFile.writeToLogDeleteFile("Move to Trash: ");
	                	moveDrectoriesAndFilesToTrash(currentFile);
	                    updateFileTreeAndTable();
                	}
                }
            });
            toolPanel.add(moveToTrashBtn);
            
            //***********************Add all panels to main frame*************************
            JPanel details = new JPanel();
            details.setLayout(new GridLayout(2,1));
            fileMainDetails.setBorder(new EmptyBorder(0,15,0,0));
            details.add(fileMainDetails);
            details.add(toolPanel);
            tableAndFileDetails.add(details, BorderLayout.NORTH);
            
            tableAndFileDetails.add(tablePanel, BorderLayout.CENTER);
            tableAndFileDetails.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
            
            fileView = new JPanel(new GridLayout(2,1));
            fileView.add(tableAndFileDetails);
            fileView.add(fileMetaDataPanel);
            
            splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,treeScroll, fileView);
            gui.add(splitPane, BorderLayout.CENTER);
            
            JPanel simpleOutput = new JPanel(new BorderLayout(3,3));
            progressBar = new JProgressBar();
            simpleOutput.add(progressBar, BorderLayout.EAST);
            progressBar.setVisible(false);
            
            gui.add(simpleOutput, BorderLayout.SOUTH);
            mainFrame.add(gui);
            mainFrame.addWindowListener(new WindowAdapter() 
            { 
                @Override
                public void windowActivated(WindowEvent e)
                {
                		updateFileTreeAndTable();
                }
            });
        }
        return gui;
    }
    
    public void showRootFile() 
    {
        tree.setSelectionInterval(0,0);
    }
    
    private void showThrowable(Throwable t) 
    {
        t.printStackTrace();
        JOptionPane.showMessageDialog(gui, t.toString(), t.getMessage(), JOptionPane.ERROR_MESSAGE);
        gui.repaint();
    }
    
    //Set file table on the right side of gui 
    private void setTableData(final File[] files) 
    {
        SwingUtilities.invokeLater(new Runnable() 
                                       {
            public void run() 
            {
                if (fileTableModel==null) 
                {
                    fileTableModel = new FileTableModel();
                    table.setModel(fileTableModel);
                }
                table.getSelectionModel().removeListSelectionListener(listSelectionListener);
                fileTableModel.setFiles(files);
                table.getSelectionModel().addListSelectionListener(listSelectionListener);
                if (!cellSizesSet) 
                {
                    Icon icon = fileSystemView.getSystemIcon(files[0]);
                    table.setRowHeight( icon.getIconHeight()+rowIconPadding );
                    table.getColumnModel().getColumn(0).setMaxWidth(30);;
                    table.getColumnModel().getColumn(1).setPreferredWidth(400);
                    
                    //if the file is not editable, change the font color to gray
                    if(table.getColumnModel().getColumnCount()>0)
                    {
                    	table.getColumnModel().getColumn(1).setCellRenderer(new TableCellRenderer()  
                    	{
                    		@Override
                    	    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                    	            int column) 
                    	    {
                    			Component component=null;
        		            	String extenssion = getExtenssion((String)value);
        		            	component = table.getTableHeader().getDefaultRenderer().getTableCellRendererComponent(table, value, false, false, -1, -2);
        		            	component.setBackground(Color.white);
        		            	if(!extenssion.equals(""))
        		                {
        		            		 if((!extenssion.equals("txt") && !extenssion.equals("csv") && !extenssion.equals("xlsx")
        		        	        		&& !extenssion.equals("xls") && !extenssion.equals("tar")&& !extenssion.equals("zip") )
        		        	        		|| ((String)value).contains("metaData") || ((String)value).contains("README")|| ((String)value).contains("readme"))
        		        	        {
        		            			component.setForeground(new Color(155,155,155)); // change the font color to gray
        		        	        }
        		                }
        		                if (isSelected)  //if the cell is selected, change the background color to blue
        		                {
        		                	component.setBackground(new Color(0, 82, 204));
        		                	component.setForeground(Color.white);
        		                }
        		                return component;
                    	    }
                    	});
                    }
                    cellSizesSet = true;
                }
            }
        });
    }
    
    //Add the files that are contained within the directory of this node.(for the left side file tree)
    private void showChildren(final DefaultMutableTreeNode node) 
    {
        tree.setEnabled(false);
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);
        
        SwingWorker<Void, File> worker = new SwingWorker<Void, File>() 
        {
            @Override
            public Void doInBackground() 
            {
                File file = (File) node.getUserObject();
                if (file.isDirectory()) 
                {
                    File[] files = fileSystemView.getFiles(file, true); 
                    if (node.isLeaf()) 
                    {
                        for (File child : files) 
                        {
                            if (child.isDirectory()) 
                            {
                                publish(child);
                            }
                        }
                    }
                    setTableData(files);
                }
                return null;
            }
            
            @Override
            protected void process(List<File> chunks) 
            {
                for (File child : chunks) {
                    node.add(new DefaultMutableTreeNode(child));
                }
            }
            
            @Override
            protected void done() 
            {
                progressBar.setIndeterminate(false);
                progressBar.setVisible(false);
                tree.setEnabled(true);
            }
        };
        worker.execute();
    }
    
    //Update the File details view includes icon and buttons like "Edit" are able to click or not.
    private void setFileDetails(File file) 
    {
        currentFile = file;
        Icon icon = fileSystemView.getSystemIcon(file);
        fileName.setIcon(icon);
        fileName.setText(fileSystemView.getSystemDisplayName(file));
        JFrame f = (JFrame)gui.getTopLevelAncestor();
        if (f!=null) 
        {
            String fName = file.getName();
            String extension = getFileExtension(currentFile);
            if (fName.contains("metaData") || file.isDirectory() || fName.contains("README") || 
            		extension.equals("zip") || extension.equals("tar") || extension.equals("gz")) 
            {
                editFile.setEnabled(false);
            }
            findMetaData(file);
            if(currentFile.isDirectory() || (extension.equals("zip")||extension.equals("tar"))==false)
            {
                unzipFile.setEnabled(false); 
            }
        }
        gui.repaint();
    }
    
    //Read the meta data file by line
    public String readTheFile(File file) throws FileNotFoundException
    {
        String theFile = "<br><br>";
        if(file != null)
        {
	        try
	        {
	        	BufferedReader br = new BufferedReader(new FileReader(file));
	            String line = br.readLine();
	            while(line != null)
	            {
	            		if(line.contains("http://")) //check if there's a URL
	            		{
	            			int i = line.indexOf(':');
	            			String front = line.substring(0, i+1);
	            			String back = line.substring(i+1, line.length());
	            			back = "<a href='"+back+"'>"+back+"</a>";
	            			line = front+back+"<br>";
	            		}
	                theFile += line+"<br>";
	                line = br.readLine();
	            }
	            br.close();
	        }
	        catch(IOException e)
	        {
	            JOptionPane.showConfirmDialog(null, e.getMessage(), "Can't open the file!", JOptionPane.CLOSED_OPTION); 
	            e.printStackTrace();
	        }
        }
        return theFile;
    }
    
    //Find the meta data file in the current directory
    public boolean findMetaData(File file)
    {
        boolean found = false;
        String metaDataFile = "";
        String readmeFile = "";
        if (file != null && file.isDirectory()) 
        {
            File[] files = fileSystemView.getFiles(file, true); 
            for (File child : files) 
            {
                String childName = child.getName();
                if (childName.contains("metaData")) 
                {
                		metaDataFile += child.getName()+"<br>";
                    try 
                    {
                    		metaDataFile += readTheFile(child)+
                            "----------------------------------------------------------------------------<br>";
                    }
                    catch (FileNotFoundException e) 
                    {
                        e.printStackTrace();
                    }
                    found = true;
                }
                else if(childName.contains("README"))
                {
                	if(childName.contains("txt"))
                	{
	                	readmeFile += child.getName()+"<br>";
		                try 
		                {
		                	readmeFile += readTheFile(child)+
		                		"----------------------------------------------------------------------------<br>";
		                } 
		                catch (FileNotFoundException e) 
		                {
		                    e.printStackTrace();
		                }
		                found = true;
                	}
                	else
                	{
                			readmeFile+="*****"+
                					"The README file can't be displayed at this area, please click \"open\" or \"loacate\" to open the file."
                					+"*****" + "<br><br>----------------------------------------------------------------------------<br><br>";
                	}
                }
            }
            metaDataTextArea.setText(metaDataFile);
            metaDataTextArea.setCaretPosition(0);
            readmeTextArea.setText(readmeFile);
            readmeTextArea.setCaretPosition(0);
        }
        return found;
    }
    
    public void deleteDrectoriesAndFiles(File theFile)
    {
        if(theFile!=null)
        {
            File[] contents = theFile.listFiles();
            if (contents != null) 
            {
                for (File f : contents) 
                {
                	deleteDrectoriesAndFiles(f);
                }
            }
            theFile.delete();
        }
    }
    
    public void moveDrectoriesAndFilesToTrash(File theFile)
    {
        if(theFile!=null)
        {
            File[] contents = theFile.listFiles();
            if (contents != null) 
            {
                for (File f : contents) 
                {
                	deleteDrectoriesAndFiles(f);
                }
            }
            desktop.moveToTrash(theFile);
        }
    }
    
    public String getFileExtension(File file)
    {
    	String extenssion = "";
    	if(file != null)
    	{
	       String fileName = file.getName();
	       int index = -1;
	       index = fileName.lastIndexOf('.');
	       if(index > -1)
	       {
	    		extenssion = fileName.substring(index + 1);
	       }
    	}
        return extenssion;
    }
    
    public String getExtenssion(String value)
    {
    	String extenssion = "";
    	int index = -1;
	    index = value.lastIndexOf('.');
	    if(index > -1)
	    {
	    	extenssion = value.substring(index + 1);
	    }
	    return extenssion;
    }
    
    public void addMenu()
    {
    	final JMenuBar menuBar = new JMenuBar();
    	//**********create menus**********
        JMenu fileMenu = new JMenu("File");
        JMenu helpMenu = new JMenu("Help");
        JMenuItem logFile = new JMenuItem("Log file");
        JMenuItem helpMenuItem = new JMenuItem("Help...");
        helpMenu.add(helpMenuItem);
        fileMenu.add(logFile);
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        mainFrame.setJMenuBar(menuBar);
        logFile.addActionListener(new MenuItemListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				setLogFile();
			}
		});
        helpMenuItem.addActionListener(new MenuItemListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				HelpWindow help = new HelpWindow("directory");
			}
		});
    }
    
    //Set dialog to select location for log files
    public void setLogFile()
    {
    		String title = "Please set the locations that you want to save the log file.";
    		
    		JButton logDeleteBtn = new JButton("Log file that records all the deleted file.");
    		if(!logDeleteFilePath.equals("")) //if the log file location has already been set.
    		{
    			logDeleteBtn.setIcon(new ImageIcon(Main.class.getResource("/Resources/checkmark.png")));
    			logDeleteBtn.setText("logDelete.txt");
    		}
    		
    		JButton logChangeBtn = new JButton("Log file that records all the changes that happens on a file.");
    		if(!logChangesFilePath.equals("")) //if the log file location has already been set.
    		{
    			logChangeBtn.setIcon(new ImageIcon(Main.class.getResource("/Resources/checkmark.png")));
    			logChangeBtn.setText("logEdit.txt");
    		}
    		
    		logDeleteBtn.addActionListener(new ActionListener()
    		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				String fileContent = "LogDelete.txt\nThis log file records all the files that has been deleted.\n\n";
				logDeleteFilePath = logFile.saveLogFile(fileContent);
				logFile.setLogDeleteFilePath(logDeleteFilePath);
				if(!logDeleteFilePath.equals(""))
				{
					logDeleteBtn.setText("logDelete.txt");
					logDeleteBtn.setIcon(new ImageIcon(Main.class.getResource("/Resources/checkmark.png")));
				}
			}
    		});
    		
    		logChangeBtn.addActionListener(new ActionListener()
    		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				String fileContent = "LogEdit.txt\nThis log file records all the changes that happened on each file.\n\n";
				logChangesFilePath = logFile.saveLogFile(fileContent);
				logFile.setLogChangesFilePath(logChangesFilePath);
				if(!logChangesFilePath.equals(""))
				{
					logChangeBtn.setText("logEdit.txt");
					logChangeBtn.setIcon(new ImageIcon(Main.class.getResource("/Resources/checkmark.png")));
				}
			}
    		});
    		
    		Object message[] = {title, logDeleteBtn, logChangeBtn};
        	Object[] closeMessage= {"Close"};
        	JOptionPane.showOptionDialog(null,message, "Set location for log files",
               JOptionPane.CLOSED_OPTION, -1, null, closeMessage, null);
    }
    
    //Update the file tree and table GUI after we delete some files
    public void updateFileTreeAndTable()
    {     
    	if(!currentNode.toString().equals("/")) //if the path is empty, that means we are first time to open the program, no need to refresh gui
    	{
	        currentNode.removeAllChildren();
	        showChildren(currentNode);
	        treeModel.reload(currentNode);
	        gui.repaint();
   		}
    }
    
    public JFrame getMainFrame()
    {
    		return mainFrame;
    }
}
