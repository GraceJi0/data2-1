package project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JOptionPane;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;

public class Decompress 
{
	
	public Decompress(File currentFile) {}

	public void unTar(final File inputFile, String destinationFolder) throws FileNotFoundException, IOException, ArchiveException 
	{
		 FileInputStream fis = new FileInputStream(inputFile);
	     TarArchiveInputStream tis = new TarArchiveInputStream(fis);
	     TarArchiveEntry tarEntry = null;
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
	                System.out.println("outputFile File ---- " + outputFile.getAbsolutePath());
	                outputFile.getParentFile().mkdirs();
	                FileOutputStream fos = new FileOutputStream(outputFile); 
	                IOUtils.copy(tis, fos);
	                fos.close();
	            }
	        }
	        tis.close();
	}
	
	public File unGZipFile(File gZippedFile, File tarFile) throws IOException
	{
        FileInputStream fis = new FileInputStream(gZippedFile);
        GZIPInputStream gZIPInputStream = new GZIPInputStream(fis);
        
        FileOutputStream fos = new FileOutputStream(tarFile);
        byte[] buffer = new byte[1024];
        int len;
        while((len = gZIPInputStream.read(buffer)) > 0)
        {
            fos.write(buffer, 0, len);
        }
        
        fos.close();
        gZIPInputStream.close();
        return tarFile;        
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
                        boolean success = newDir.mkdirs();
                        if(success == false) 
                        {
                            System.out.println("Problem creating Folder");
                        }
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
