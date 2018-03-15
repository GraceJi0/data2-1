package project;
import java.awt.Dimension;
import java.io.File;

import javax.swing.*;

public class Interface 
{
	
	
    private static void showGUI() 
    { 
        //00003932-S_File_4_mtDNA_partitions_for_RAxML
        //aaa
        /*JFrame f = new InterfaceMain(new File("/Users/dinghanji/Desktop/output/aaa.txt")).getMainFrame();
         f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         f.pack();
         f.setMinimumSize(f.getSize());
         f.setVisible(true);*/
        
        
    		InterfaceDirectories fileBrowser = new InterfaceDirectories();
    		JFrame f = fileBrowser.getMainFrame();
        f.setPreferredSize(new Dimension(1000, 700));
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //f.setContentPane(fileBrowser.getGui());
        
        f.pack();
        f.setLocationByPlatform(true);
        f.setMinimumSize(f.getSize());
        f.setVisible(true);
        
        fileBrowser.showRootFile();
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
