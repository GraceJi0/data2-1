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
import javax.swing.tree.*;

import org.apache.log4j.chainsaw.Main;

import java.util.List;
import java.awt.*;

public class InterfaceDirectories 
{
	private JFrame mainFrame;
	private JEditorPane metaDataTextArea;
    private JEditorPane readmeTextArea;
    private Desktop desktop;
    
    /** Provides nice icons and names for files. */
    private FileSystemView fileSystemView;
    
    private File currentFile;
    private JPanel gui;
    private JTree tree;
    private DefaultTreeModel treeModel;
    private JScrollPane treeScroll;
    
    private JSplitPane splitPane;
    private JPanel fileView;
    
    /** Directory listing */
    private JTable table;
    private JProgressBar progressBar;
    
    /** Table model for File[]. */
    private FileTableModel fileTableModel;
    private ListSelectionListener listSelectionListener;
    private boolean cellSizesSet = false;
    private int rowIconPadding = 6;
    
    private JButton openFile;
    private JButton editFile;
    private JButton unzipFile;
    private JButton deleteBtn;
    private JLabel fileName;
    
    private LogFile logFile;
    private String logChangesFilePath;
    private String logDeleteFilePath;
    
    private DefaultMutableTreeNode currentNode;
    
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
            
            //*******************set right side directory table *********************
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
            
            //********************the File tree**********************
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
            
            //*************************show the file system roots.**************************
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
            
            //***********set file panel with detail components***************
            JPanel fileMainDetails = new JPanel(new BorderLayout(2,0));
            fileMainDetails.setBorder(new EmptyBorder(0,6,0,6));
            
            JPanel fileDetailsLabels = new JPanel(new GridLayout(0,1,0,0));
            fileMainDetails.add(fileDetailsLabels, BorderLayout.WEST);
            
            JPanel fileDetailsValues = new JPanel(new GridLayout(0,1,0,0));
            fileMainDetails.add(fileDetailsValues, BorderLayout.CENTER);
            
            fileDetailsLabels.add(new JLabel("File", JLabel.TRAILING));
            fileName = new JLabel();
            fileDetailsValues.add(fileName);
            
            //**********************set mete data and README panel********************
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
						//Desktop d = Desktop.getDesktop();
						try
						{
							desktop.browse(e.getURL().toURI());
							//d.browse(e.getURL().toURI());
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
            
            //***********************set tools panel***********************
            JPanel toolBar = new JPanel();
            toolBar.setLayout(new FlowLayout(FlowLayout.LEFT));
            
            //***************open the current file or directory****************
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
            toolBar.add(openFile);
            openFile.setEnabled(desktop.isSupported(Desktop.Action.OPEN));
            
            //****************open the parent directory of the file**************
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
            toolBar.add(locateFile);
            
            //************If the "Edit" button has been clicked, open the editor window.*********
            editFile = new JButton("Editor");
            editFile.setSize(new Dimension(100, 50)); 
            editFile.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent ae)
                {
                    JFrame frame = new InterfaceMain(currentFile,gui,logFile).getMainFrame();
                    if(frame != null)
                    {
	                    frame.setVisible(true);
	                    frame.setPreferredSize(new Dimension(900, 700));
                    }
                }
            });
            toolBar.add(editFile);
            
            //*************extract a zip file*****************
            unzipFile = new JButton("Decompress");
            unzipFile.setSize(new Dimension(100, 50)); 
            unzipFile.addActionListener(new ActionListener()
                                            {
                public void actionPerformed(ActionEvent ae) 
                {
                		Decompress decompress = new Decompress();
                    String zipFile = currentFile.getAbsolutePath();
                    String destinationFolder = currentFile.getParentFile().getAbsolutePath();
                    if(getMyFileExtension().equals("zip"))
                    {
                    		decompress.unzip(destinationFolder,zipFile);
                    }
                    else if(getMyFileExtension().equals("tar"))
                    {
						decompress.unTar(currentFile, destinationFolder);
                    }
                    else if(getMyFileExtension().equals("gz"))
                    {
                    		decompress.decompressGzip(currentFile,destinationFolder);
                    }
                    updateFileTreeAndTable();
                }
            });
            toolBar.add(unzipFile);
            
            //****************delete a file or directory******************
            deleteBtn = new JButton("Delete");
            deleteBtn.setSize(new Dimension(100, 50)); 
            deleteBtn.addActionListener(new ActionListener()
                                            {
                public void actionPerformed(ActionEvent ae) 
                {
                		logFile.setCurrentFile(currentFile);
                		logFile.writeToLogDeleteFile();
                    deleteDrectoriesAndFiles(currentFile);
                    updateFileTreeAndTable();
                }
            });
            toolBar.add(deleteBtn);
            
            //***********************set up main panel*************************
            JPanel details = new JPanel();
            details.setLayout(new GridLayout(2,1));
            fileMainDetails.setBorder(new EmptyBorder(0,15,0,0));
            details.add(fileMainDetails);
            details.add(toolBar);
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
    
    //*************************************************************************************************
    //*************************************************************************************************
    
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
    
    //set directory table on the right side of interface
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
                    
                    // size adjustment to better account for icons
                    table.setRowHeight( icon.getIconHeight()+rowIconPadding );
                    
                    table.getColumnModel().getColumn(0).setMaxWidth(30);;
                    table.getColumnModel().getColumn(1).setPreferredWidth(400);
                    
                    cellSizesSet = true;
                }
            }
        });
    }
    
    /*Add the files that are contained within the directory of this node.*/
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
    
    /*Update the File details view.*/
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
            String extension = getMyFileExtension();
            if (fName.contains("metaData") || file.isDirectory() || fName.contains("README") || 
            		extension.equals("zip") || extension.equals("gz") || extension.equals("tar")) 
            {
                editFile.setEnabled(false);
            }
            findMetaData(file);
            if(currentFile.isDirectory() || (extension.equals("zip")||extension.equals("tar") /*||
            		extension.equals("gz")*/)==false)
            {
                unzipFile.setEnabled(false); 
            }
        }
        gui.repaint();
    }
    
    /*read the meta data file by line*/
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
    
    /*find the meta data file in the current directory*/
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
                					+"*****";
                			break;
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
    
    public String getMyFileExtension()
    {
	       String fileName = currentFile.getName();
	       int index = fileName.lastIndexOf('.');
	       return fileName.substring(index + 1);
    }
    
    public void addMenu()
    {
    		final JMenuBar menuBar = new JMenuBar();
    		//**********create menus**********
        JMenu fileMenu = new JMenu("File");
        JMenu helpMenu = new JMenu("Help");
        JMenuItem logFile = new JMenuItem("Log file");
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
    }
    
    //set dialog to select location for log files
    public void setLogFile()
    {
    		String title = "Please set the locations that you want to save the log file.";
    		JButton logDeleteBtn = new JButton("Log file that records all the deleted file.");
    		JButton logChangeBtn = new JButton("Log file that records all the changes that happens on a file.");
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
