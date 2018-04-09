package project;
import java.awt.Dimension;
import java.io.File;

import javax.swing.*;

public class Interface 
{
    private static void showGUI() 
    { 
    	InterfaceDirectories fileDirectory = new InterfaceDirectories();
    	JFrame frame = fileDirectory.getMainFrame();
    	frame.setPreferredSize(new Dimension(1000, 700));
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
    	frame.pack();
    	frame.setLocationByPlatform(true);
    	frame.setMinimumSize(frame.getSize());
    	frame.setVisible(true);
        
    	fileDirectory.showRootFile();
    }
    
    public static void main(String[] args) 
    {
        javax.swing.SwingUtilities.invokeLater(new Runnable() 
                                                   {
            public void run() 
            {
                showGUI();
            }
        });
    }
}
