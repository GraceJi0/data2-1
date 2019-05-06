package project;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.IOException;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.chainsaw.Main;

//Set up the help window's GUI.
public class HelpWindow 
{
	JFrame mainFrame;
	
	public HelpWindow(String type) 
	{
		mainFrame = new JFrame("Help");
		setUpHelpWindow(type);
		mainFrame.setVisible(true);
	}

	public void setUpHelpWindow(String type)
	{
		mainFrame.setLayout(new GridLayout(1,1));
		mainFrame.setPreferredSize(new Dimension(900, 600));
		mainFrame.setMinimumSize(new Dimension(700,500));
		
		JEditorPane helpTextPane = new JEditorPane();
		helpTextPane.setBorder(new EmptyBorder(0,10,10,10));
		helpTextPane.setContentType("text/html");
		try 
		{
			if(type.equals("directory"))//show the content about file browser
			{
				helpTextPane.setPage(Main.class.getResource("/Resources/DirectoryHelpPage.html"));
			}
			else if(type.equals("editor"))//show the content about editor
			{
				helpTextPane.setPage(Main.class.getResource("/Resources/EditorHelpPage.html"));
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		helpTextPane.setEditable(false);
		helpTextPane.setOpaque(false);
		helpTextPane.setCaretPosition(0);
		
		JScrollPane helpScroll = new JScrollPane(helpTextPane,
                 JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                 JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		mainFrame.add(helpScroll);
	}
}
