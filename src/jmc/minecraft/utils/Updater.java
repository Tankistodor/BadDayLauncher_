/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmc.minecraft.utils;

import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;









//import javax.swing.JLabel;
import javax.swing.JProgressBar;









//import jmc.minecraft.utils.GlobalVar;
//import jmc.minecraft.utils.mdsummcheker;
//import jmc.minecraft.utils.Utils;
import static jmc.minecraft.utils.Utils.getWorkingDirectory;

/**
 *
 * @author DimanA90
 */
public class Updater {
     /*
     * 1) Генерируем список файлов на загрузку. Сверяем мд5 архива у клиента и на сервере, не совпал добаляем в filelist
     * 2) Размер файлов
     * 3) Качаем файлы
     * 4) Удаляем необходимые директории относительно списка загрузок
     * 5) Распаковываем архивы по filelist

     */
private String[] FileList = new String[255];  //File list Список архивов
private long[] FileSize = new long[255];
private URL path ; //Url path to current client on server УРЛ к папке с файлами клиента на сервере
private URL[] URLList = new URL[255]; //Full url list Полный путь до каждого архива
private int FileCount = 0; 
private String ClientFolderPath; //Full path to curr client Полный путь до текущего клиента

private static long totalsize = 0;
private static int CurentTotalProgress =0;

public void Init(JProgressBar progressCurr,JProgressBar progressBarTotal)
{
	Utils.LogPrintConsole("Проверка обновления. Checking update.");
    GenerateFileList();  //1
    
    Utils.LogPrintConsole("Подсчет размера обновления. Checking update size.");
    DownloadFilesSize(); //2
    
    Utils.LogPrintConsole("Скачивание обновления. Downloading update.");
    DownloadFiles(progressCurr,progressBarTotal);     //3
    
    Utils.LogPrintConsole("Установка обновления. Installing update.");
    UnpackArchives(progressCurr,progressBarTotal);    //5
}
/*
 * 1) Генерируем список файлов на загрузку. Сверяем мд5 архива у клиента и на сервере, не совпал добаляем в filelist
 */
private void GenerateFileList()
{
    //ClientFolderPath = Utils.getWorkingDirectory() + File.separator + GlobalVar.itemsServers[GlobalVar.CurrentServer] + File.separator;
    ClientFolderPath = Utils.getWorkingDirectory() + File.separator + GlobalVar.itemsServers[GlobalVar.CurrentServer] + File.separator + ".minecraft" + File.separator;
    
		File dir = new File(ClientFolderPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		if (GlobalVar.debug)
			System.out.println("Client root folder: " + ClientFolderPath);
		try {
			path = new URL(
					(GlobalVar.DownloadClientRootURL + GlobalVar.itemsServers[GlobalVar.CurrentServer]) + '/');
			if (GlobalVar.debug)
				System.out.println("Download root: " + path.toString());
		} catch (MalformedURLException ex) {
			Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
		}
		mdsummcheker mdchk = new mdsummcheker();

    for(int i=0;i<GlobalVar.ArchivesList.length;i++)
    {
        if(!mdchk.CheckMD5Matches(GlobalVar.ArchivesList[i]))
        {
            try 
            {
                FileList[FileCount]=GlobalVar.ArchivesList[i];
                URLList[FileCount] = new URL(path,FileList[FileCount]);
                FileCount++;
                Utils.LogPrintConsole("Обнаружено обновление файла: "+GlobalVar.ArchivesList[i]);
            } catch (MalformedURLException ex) 
            {
                //System.out.println("Исключение на итерации "+ FileCount + "\nFile name = "+FileList[FileCount]);
                Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception e)
            {
                System.out.println("Error on generate links "+e);
                Utils.LogPrintConsole("Ошибка проверки обновления файла: "+GlobalVar.ArchivesList[i]);
            }
          
        }
    }
}
/*
 * 2) Размер файлов
 */
protected void DownloadFilesSize()
{
    for(int i=0;i<FileCount;i++)
    {
        long size = (Utils.downloadFilesSize(URLList[i].toString()))*2/1024;
        totalsize +=size;
        FileSize[i] = size/2;
        Utils.LogPrintConsole("Размер обновления для файла: "+FileList[i]+" ="+FileSize[i]+"Kb");
    }
}
/*
 * 3) Качаем файлы
 */
protected void DownloadFiles(JProgressBar progressCurr,JProgressBar progressBarTotal)
{
    //System.out.println(FileCount);
    for(int i=0;i<FileCount;i++)
    {
    	Utils.LogPrintConsole(i+" Качаю "+ URLList[i].toString()+"\n"+ClientFolderPath);
        downloadFiles(URLList[i].toString(), ClientFolderPath,FileList[i], 65536,FileSize[i], progressCurr, progressBarTotal);
    }
}
/*
 * 4) Удаляем необходимые директории относительно списка загрузок
 * УПС
 */

/*
 * 5) Распаковываем архивы по filelist
 */
protected void UnpackArchives(JProgressBar progressCurr,JProgressBar progressBarTotal)
{
    for(int i=0;i<FileCount;i++)
    {
        System.out.println(i+" Unzip "+ FileList[i]);
        try {
                   Utils.deleteDirectory(getWorkingDirectory() + File.separator + GlobalVar.itemsServers[GlobalVar.CurrentServer]+ File.separator+".minecraft"+ File.separator+FileList[i].substring(0, FileList[i].length()-4));   //Рекурсивно вычищаем ее
                   if (FileList[i].equals("other.zip")) {
                	   Utils.deleteDirectory(getWorkingDirectory() + File.separator + GlobalVar.itemsServers[GlobalVar.CurrentServer]+ File.separator+".minecraft"+ File.separator+"config");   //Рекурсивно вычищаем ее
                	   File t = new File(getWorkingDirectory() + File.separator + GlobalVar.itemsServers[GlobalVar.CurrentServer]+ File.separator+".minecraft"+ File.separator+"idfixminus.txt");
                	   if (t.exists()) t.delete();
                	   t = new File(getWorkingDirectory() + File.separator + GlobalVar.itemsServers[GlobalVar.CurrentServer]+ File.separator+".minecraft"+ File.separator+"servers.dat");
                	   if (t.exists()) t.delete();
                   }
                   Utils.LogPrintConsole("Начинаю установку: "+FileList[i]);
                   UnZip(FileList[i],FileSize[i],progressCurr,progressBarTotal);
        } catch (PrivilegedActionException ex) {
            Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}


private void downloadFiles(String strURL, String strPath, String filename, int buffSize, long fileSize, JProgressBar progressCurr,JProgressBar progressBarTotal ) {
        try {
            progressCurr.setValue(0);
            
            URL connection = new URL(strURL);
            HttpURLConnection urlconn;
            urlconn = (HttpURLConnection) connection.openConnection();
            urlconn.setRequestMethod("GET");
            urlconn.connect();
            InputStream in = null;
            in = urlconn.getInputStream();
            OutputStream writer = new FileOutputStream(strPath+filename);
            byte buffer[] = new byte[buffSize];
            int c = in.read(buffer);
            int dwnloaded =0;
            int oldpercent = 0;
            while (c > 0) {
                writer.write(buffer, 0, c);
                dwnloaded +=c;
                try{
                progressCurr.setValue((int)((dwnloaded/1024)*100/fileSize));
                progressCurr.setString("Скачивается "+filename + "  "+progressCurr.getValue()+"%  " +(dwnloaded/1048576)+"/"+(fileSize/1024)+"Mb");
                  CurentTotalProgress += (int)(  ((dwnloaded/1024)*100/totalsize)-oldpercent  ) ;
                progressBarTotal.setValue(CurentTotalProgress);
                    oldpercent = (int)((dwnloaded/1024)*100/totalsize);
                }catch(ArithmeticException ae){
                   //И такое тоже бывает
                }
                c = in.read(buffer);
            }
            writer.flush();
            writer.close();
            in.close();
        } catch (IOException e) {
            System.out.println("Неведомая хуйня при загрузке файла в "+ strPath+filename);
            System.out.println(e);
        }
}

/**
 * Разархивирует файл client.zip из папки bin в .minecraft
 * @author ddark008
 * @throws PrivilegedActionException
 */
private static void UnZip(String ZipName,long fileSize, JProgressBar progressCurr,JProgressBar progressBarTotal) throws PrivilegedActionException
  {
     
    String szZipFilePath;
    String szExtractPath;
    String path = (String)AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
        public Object run() throws Exception {
          return getWorkingDirectory() + File.separator + GlobalVar.itemsServers[GlobalVar.CurrentServer] + File.separator + ".minecraft" + File.separator;
        }
      }); 
    int i;
    
    szZipFilePath = path + ZipName;
      
    File f = new File(szZipFilePath);
    
    if(!f.exists())
    {
      System.out.println(
	"\nNot found: " + szZipFilePath);
    }
      
    if(f.isDirectory())
    {
      System.out.println(
	"\nNot file: " + szZipFilePath);
    }
    
    //System.out.println("Enter path to extract files: ");
    szExtractPath = path;
    
    File f1 = new File(szExtractPath);
    if(!f1.exists())
    {
      System.out.println(
	"\nNot found: " + szExtractPath);
    }
      
    if(!f1.isDirectory())
    {
      System.out.println(
	"\nNot directory: " + szExtractPath);
    }
    
    ZipFile zf; 
    Vector zipEntries = new Vector();
       
    try
    {  
      zf = new ZipFile(szZipFilePath); 
      
      
      Enumeration en = zf.entries();
      
      while(en.hasMoreElements())
      {
        zipEntries.addElement(
	  (ZipEntry)en.nextElement());
      }
      int sizetemp = 1;
      int oldpercentTotal = 0;
      int currFilePercent = 0;
      for (i = 0; i < zipEntries.size(); i++)
      {
        ZipEntry ze = 
	  (ZipEntry)zipEntries.elementAt(i);
        
        
        sizetemp +=ze.getCompressedSize();
        try{
        currFilePercent = (int)(    (sizetemp /1024) *100/fileSize    );
        progressCurr.setValue(currFilePercent);
        progressCurr.setString("Извлечение "+ZipName + "  "+progressCurr.getValue()+"%");
        CurentTotalProgress += (int)(  ((sizetemp/1024)*100/totalsize)-oldpercentTotal  ) ;
        progressBarTotal.setValue(CurentTotalProgress);
        oldpercentTotal = (int)((sizetemp/1024)*100/totalsize);
        }catch(ArithmeticException ae){
            
        }
        
        extractFromZip(szZipFilePath, szExtractPath,
	  ze.getName(), zf, ze);
      }
      
      zf.close();
      //System.out.println("Done!");
    }
    catch(Exception ex)
    {
      System.out.println(ex.toString());
    }
    f.delete();
  }
  
  // ============================================
  // extractFromZip
  // ============================================
  private static void extractFromZip(
    String szZipFilePath, String szExtractPath,
    String szName,
    ZipFile zf, ZipEntry ze)
  {
    if(ze.isDirectory())
      return;
      
    String szDstName = slash2sep(szName);
    
    String szEntryDir;
    
    if(szDstName.lastIndexOf(File.separator) != -1)
    {
      szEntryDir =
        szDstName.substring(
	  0, szDstName.lastIndexOf(File.separator));
    }
    else	  
      szEntryDir = "";
    
    //System.out.print(szDstName);
    long nSize = ze.getSize();
    long nCompressedSize = 
      ze.getCompressedSize();
    
    //System.out.println(" " + nSize + " (" +      nCompressedSize + ")");  
  
    try
    {
       File newDir = new File(szExtractPath +
	 File.separator + szEntryDir);
	 
       newDir.mkdirs();	 
       
       FileOutputStream fos = 
	 new FileOutputStream(szExtractPath +
	 File.separator + szDstName);

       InputStream is = zf.getInputStream(ze);
       byte[] buf = new byte[1024];

       int nLength;
       
       while(true)
       {
         try
         {
	   nLength = is.read(buf);
         }	 
         catch (EOFException ex)
         {
	   break;
	 }  
	 
         if(nLength < 0) 
	   break;
         fos.write(buf, 0, nLength);
       }
       
       is.close();
       fos.close();
    }   
    catch(Exception ex)
    {
      System.out.println(ex.toString());
      //System.exit(0);
    }
  }  
  // ============================================
  // slash2sep
  // ============================================
  private static String slash2sep(String src)
  {
    int i;
    char[] chDst = new char[src.length()];
    String dst;
    
    for(i = 0; i < src.length(); i++)
    {
      if(src.charAt(i) == '/')
        chDst[i] = File.separatorChar;
      else
        chDst[i] = src.charAt(i);
    }
    dst = new String(chDst);
    return dst;
  }
//end
}
