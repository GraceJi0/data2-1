package project;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class FilePrograssBar {

	public FilePrograssBar() {
		// TODO Auto-generated constructor stub
	}
	
	public void setInterface()
	{
		JFrame mainFrame = new JFrame("In prograss....");
		JPanel mainPanel = new JPanel();
		mainFrame.add(mainPanel);
		
		JProgressBar progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
        progressBar.setStringPainted(true);
        mainPanel.add(progressBar);
        
	}

}
