package project;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.swing.JOptionPane;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;

/*
 * This class include 2 methods for extracting zip and tar files. 
 */
public class Decompress 
{
	public void unTar(final File inputFile, String destinationFolder)
	{
		FileInputStream fis;
		try 
		{
			fis = new FileInputStream(inputFile);
			TarArchiveInputStream tis = new TarArchiveInputStream(fis);
		     TarArchiveEntry tarEntry = null;
		     try 
		     {
		    	 	while ((tarEntry = tis.getNextTarEntry()) != null) 
				{
		    	 		File outputFile = new File(destinationFolder + File.separator + tarEntry.getName());
				    if(tarEntry.isDirectory())
				    {
				            if(!outputFile.exists())
				            {
				                outputFile.mkdirs();
				            }
				     }
				     else
				     {
				            outputFile.getParentFile().mkdirs();
				            FileOutputStream fos = new FileOutputStream(outputFile); 
				            IOUtils.copy(tis, fos);
				            fos.close();
				     }
				 }
				tis.close();
			} 
		    catch (IOException e) 
		    {
		    	 	JOptionPane.showConfirmDialog(null, e.getMessage(), "Can't decomprass the file!", JOptionPane.CLOSED_OPTION); 			
		    } 
		} 
		catch (FileNotFoundException e) 
		{
			JOptionPane.showConfirmDialog(null, e.getMessage(), "Can't decomprass the file!", JOptionPane.CLOSED_OPTION); 
		}
	}
	
	
	public void unzip(String destinationFolder, String zipFile)
    {
        File directory = new File(destinationFolder);
        if(!directory.exists()) 
        {
            directory.mkdirs();
        }
        byte[] buffer = new byte[100000];
        try 
        {
            FileInputStream fInput = new FileInputStream(zipFile);
            	ZipInputStream zipInput = new ZipInputStream(fInput);
            
            ZipEntry entry = zipInput.getNextEntry();
            
            while(entry != null)
            {
                String entryName = entry.getName();
                File file = new File(destinationFolder + File.separator + entryName);
                if(entry.isDirectory()) 
                {
                    File newDir = new File(file.getAbsolutePath());
                    if(!newDir.exists()) 
                    {
                    		newDir.mkdirs();
                    }
                }
                else 
                {
                    FileOutputStream fOutput = new FileOutputStream(file);
                    int count = 0;
                    while ((count = zipInput.read(buffer)) > 0) 
                    {
                        fOutput.write(buffer, 0, count);
                    }
                    fOutput.close();
                }
                zipInput.closeEntry();
                entry = zipInput.getNextEntry();
            }
            zipInput.closeEntry();
            zipInput.close();
            fInput.close();
        } 
        catch (IOException e) 
        {
            JOptionPane.showConfirmDialog(null, e.getMessage(), "Can't decomprass the file!", JOptionPane.CLOSED_OPTION); 
            e.printStackTrace();
        }
    }
}
