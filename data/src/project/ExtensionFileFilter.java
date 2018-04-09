package project;

import java.io.File;

import javax.swing.filechooser.FileFilter;

//Used in "save as" function in editor, when users trying to "save as" a the file, 
//the JChooser window will show the correct file type at the bottom  
class ExtensionFileFilter extends FileFilter 
{
	String description;
	String extensions[];

	public ExtensionFileFilter(String description, String extension) 
	{
		this(description, new String[] { extension });
	}	

	public ExtensionFileFilter(String description, String extensions[]) 
	{
		if (description == null) 
		{
			this.description = extensions[0];
	    }
		else 
	    {
			this.description = description;
	    }
	    this.extensions = (String[]) extensions.clone();
	    toLower(this.extensions);
	}

	private void toLower(String array[])
	{
		for (int i = 0, n = array.length; i < n; i++) 
		{
			array[i] = array[i].toLowerCase();
		}
	}

	public String getDescription() 
	{
		return description;
	}

	public boolean accept(File file) 
	{	
		boolean result;
		if (file.isDirectory()) 
		{
			return true;
		} 
		else 
		{
			String path = file.getAbsolutePath().toLowerCase();
			for (int i = 0, n = extensions.length; i < n; i++) 
			{
				String extension = extensions[i];
				if ((path.endsWith(extension) && (path.charAt(path.length() - extension.length() - 1)) == '.')) 
				{
					return true;
				}
			 }
		 }
		 return false;
	 }
}