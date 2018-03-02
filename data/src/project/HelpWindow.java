package project;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

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
		mainFrame.setMinimumSize(new Dimension(700,300));
		mainFrame.setMaximumSize(new Dimension(900,600));
		
		JTextArea helpTextArea = new JTextArea();
		JScrollPane helpTextScroll = new JScrollPane(helpTextArea,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		String text = getHelpText(type);
		helpTextArea.setText(text);
		helpTextArea.setEditable(false);
		helpTextArea.setLineWrap(true);
		helpTextArea.setWrapStyleWord(true);
		
		JPanel HelpTextPanel = new JPanel();
		HelpTextPanel.setLayout(new GridLayout(1,1));
		HelpTextPanel.setBorder(new EmptyBorder(10,10,10,10));
		HelpTextPanel.add(helpTextScroll);

		mainFrame.add(HelpTextPanel);
	}
	
	public String getHelpText(String type)
	{
		String text = "";
		if(type.equals("directory"))
		{
			text = "1. File: set the log file location.\n"+
				   "2. Open: open a file with it's default application.\n"+
				   "3. Location: open the folder that contain the file.\n"+
				   "4. Editor: open a new window and edit the file's foramt.\n"+
				   "5. Delete: delete the file permanently.\n"+
				   "6. Meta Data tab show all meta data files in this folder.\n"+
				   "7. README tab show all README files in this folder.\n"+
				   "8. The \"icon\" and \"File\" labels in the left side file table allow users to sort files by type or by name\n";
		}
		else if(type.equals("editor"))
		{
			text = ""+
				   ""+
				   "";
		}
		else
		{
			text = "Can't open help page!";
		}
		return text;
	}
}
