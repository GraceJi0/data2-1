package project;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import org.apache.log4j.chainsaw.Main;

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
		mainFrame.setLayout(new GridLayout(2,1));
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
		
		JPanel helpImagePanel = new JPanel();
		try 
		{
			System.out.println(type+".png");
			BufferedImage image = ImageIO.read(Main.class.getResource("/Resources/"+type+".png"));
			JLabel helpImageLable = new JLabel(new ImageIcon(image));
			helpImageLable.setBorder(new EmptyBorder(10,20,10,20));
			helpImagePanel.setLayout(new GridLayout(1,1));
			helpImagePanel.add(helpImageLable);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		mainFrame.add(HelpTextPanel);
		mainFrame.add(helpImagePanel);
	}
	
	public String getHelpText(String type)
	{
		String text = "";
		if(type.equals("directory"))
		{
			text = "1. Click \"File\" and \"Log file\" to set the log file location.\n"+
				   "2. Click \"Open\" to open a file with it's default application.\n"+
				   "3. Click \"Location\" to open the folder that contain the file.\n"+
				   "4. Click \"Editor\" to open a new window and edit the file's foramt.\n"+
				   "5. Click \"Delete\" to delete the file permanently.\n";
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
