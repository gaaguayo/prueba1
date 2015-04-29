package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;


/**
 *
 * @author Pedro G. Campos
 */
public class PrintUtils {
    public static FileWriter getExistingPrintFile(String path, String fileName){
        String file=path+"/"+fileName;
        FileWriter fstream=null;
        try {
            fstream = new FileWriter(file,true);
        } catch (Exception e){
            System.err.println("Problem with file creation: "+e);
            for (StackTraceElement ste:e.getStackTrace())
                System.err.println(ste);
        }
        return fstream;
    }
    
    public static void cleanFile(String fileName){
        File file=new File(fileName);
        if (file.exists()){
            file.delete();
        }
    }
    
    public static FileWriter getPrintFile(String path, String fileName){
        String file=path+"/"+fileName;
        FileWriter fstream=null;
        try {
            fstream = new FileWriter(file);
        } catch (Exception e){
            System.err.println("Problem with file creation: "+e);
            for (StackTraceElement ste:e.getStackTrace())
                System.err.println(ste);
        }
        return fstream;
    }
    
    public static void print(FileWriter fw, String message){
        BufferedWriter bw = new BufferedWriter (fw);
        try {
            fw.write(message);
            fw.flush();
        } catch (Exception e){
            System.err.println("Problem with printing to file: "+e);
            for (StackTraceElement ste:e.getStackTrace())
                System.err.println(ste);
        }
    }

    public static void println(FileWriter fw, String message){
        BufferedWriter bw = new BufferedWriter (fw);
        try {
            bw.write(message);
            bw.newLine();
            bw.flush();
        } catch (Exception e){
            System.err.println("Problem with printing to file: "+e);
            for (StackTraceElement ste:e.getStackTrace())
                System.err.println(ste);
        }
    }
    
    public static void closeFile(FileWriter fstream){
        try {
            fstream.flush();
            fstream.close();
        } catch (Exception e){
            System.err.println("Problem with printing to file: "+e);
            for (StackTraceElement ste:e.getStackTrace())
                System.err.println(ste);
        }
    }

    public static void toGz(String fileName, String message){
        BufferedWriter writer;
        
        try{
            //Opening
            writer=new BufferedWriter(
                    new OutputStreamWriter(
                    new GZIPOutputStream(new FileOutputStream(fileName, false)))
                    );
                    
            writer.write(message);
            writer.flush();
            writer.close();
        } catch (Exception e){
            System.err.println("Problem with printing to file: "+e);
            for (StackTraceElement ste:e.getStackTrace())
                System.err.println(ste);
        }
    }

    public static void toGz(String fileName, String message, boolean append){
        BufferedWriter writer;
        
        try{
            //Opening
            writer=new BufferedWriter(
                    new OutputStreamWriter(
                    new GZIPOutputStream(new FileOutputStream(fileName, append)))
                    );
                    
            writer.write(message);
            writer.flush();
            writer.close();
        } catch (Exception e){
            System.err.println("Problem with printing to file: "+e);
            for (StackTraceElement ste:e.getStackTrace())
                System.err.println(ste);
        }
    }

    public static void toFile(String fileName, String message){
        BufferedWriter writer;
        
        try{
            //Opening
            writer=new BufferedWriter(
                    (new FileWriter(new File(fileName), false))
                    );
                    
            writer.write(message);
            writer.flush();
            writer.close();
        } catch (Exception e){
            System.err.println("Problem with printing to file: "+e);
            for (StackTraceElement ste:e.getStackTrace())
                System.err.println(ste);
        }
    }
        
    public static void toFile(String fileName, String message, boolean append){
        BufferedWriter writer;
        
        try{
            //Opening
            writer=new BufferedWriter(
                    (new FileWriter(new File(fileName), append))
                    );
                    
            writer.write(message);
            writer.flush();
            writer.close();
        } catch (Exception e){
            System.err.println("Problem with printing to file: "+e);
            for (StackTraceElement ste:e.getStackTrace())
                System.err.println(ste);
        }
    }
        
    public static boolean ftpTransfer(String localfile, String destinationfile){
	String server = "valhalla.ii.uam.es";
	String username = "pedro";
	String password = "princesa@0305";
	try
	{
		FTPClient ftp = new FTPClient();
		ftp.connect(server);
		if(!ftp.login(username, password))
		{
			ftp.logout();
			return false;
		}
		int reply = ftp.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply))
		{
			ftp.disconnect();
			return false;
		}
		InputStream in = new FileInputStream(localfile);
		ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
		boolean Store = ftp.storeFile(destinationfile, in);
		in.close();
		ftp.logout();
		ftp.disconnect();
	}
	catch (Exception ex)
	{
		ex.printStackTrace();
		return false;
	}
	return true;
    }    
}
