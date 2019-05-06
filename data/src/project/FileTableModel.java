package project;

import java.awt.Color;
import java.awt.Component;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

// A TableModel to hold File[] for file table at the right side of the file browser.
public class FileTableModel extends AbstractTableModel 
{
    
    private File[] files;
    private FileSystemView fileSystemView = FileSystemView.getFileSystemView();
    private String[] columns = {"Icon","File"};
    
    FileTableModel() 
    {
        this(new File[0]);
    }
    
    FileTableModel(File[] files) 
    {
        this.files = files;
    }
    
    public Object getValueAt(int row, int column) 
    {
        File file = files[row];
        switch (column) 
        {
            case 0:
                return fileSystemView.getSystemIcon(file);
            case 1:
                return fileSystemView.getSystemDisplayName(file);
            default:
                System.out.println("***ERROR****");
        }
        return "";
    }
    
    public int getColumnCount() 
    {
        return columns.length;
    }
    
    public Class<?> getColumnClass(int column) 
    {
        if(column == 0)
        {
            return ImageIcon.class;
        }
        else
        {
            return String.class;
        }
        
    }
    
    public String getColumnName(int column) 
    {
        return columns[column];
    }
    
    public int getRowCount() 
    {
        return files.length;
    }
    
    public File getFile(int row) 
    {
        return files[row];
    }
    
    public void setFiles(File[] files) 
    {
        this.files = files;
        fireTableDataChanged();
    }
}
