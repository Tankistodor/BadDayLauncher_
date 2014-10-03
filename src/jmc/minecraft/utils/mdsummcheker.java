/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmc.minecraft.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 *
 * @author DimanA90
 */
public class mdsummcheker {
        //Основно метод
    public boolean CheckMD5Matches(String fileName){
    
    try{
        //Check file version on server
        String param = "file=" + URLEncoder.encode( fileName, "UTF-8"); 
        String LinkUrl =GlobalVar.DownloadClientRootURL+GlobalVar.itemsServers[GlobalVar.CurrentServer]+"/md5check.php";
        if(GlobalVar.debug)
                System.out.println("MD5Url: "+ LinkUrl);
        String SFileVersion = excutePostMD5(LinkUrl,param);
        if(GlobalVar.debug)
                System.out.println("Сервер: "+ fileName +"  md5 сумма= "+SFileVersion);
            if (SFileVersion == null) {
                if(GlobalVar.debug)
                System.out.println("Сервер: "+ fileName + "  Не удалось получить хэш сумму от сервера");
            }
        //Check file version on client    
        File dir = new File(Utils.getWorkingDirectory()+ File.separator + GlobalVar.itemsServers[GlobalVar.CurrentServer] + File.separator + "md5" + File.separator);
        if (!dir.exists()) 
                  {
                    dir.mkdirs();
                  }
        File md5versionFile = new File(dir, fileName+".md5");
        String CFileVersion = readMD5File(md5versionFile);
        if(GlobalVar.debug)
        System.out.println("Клиент: "+ fileName +"  md5 сумма= "+CFileVersion);
        //Matching
        if (SFileVersion.matches(CFileVersion) ){
            if(GlobalVar.debug)
            System.out.println("Хэш совпал");
            return true;
        }else{
            //Write new md5 to client
            writeMD5File(md5versionFile, SFileVersion);
            if(GlobalVar.debug)
            System.out.println("Хэш не совпал");
            return false;
        }
        
    }catch (Exception e) {
            //e.printStackTrace();
            //System.out.print(e.toString());
        if(GlobalVar.debug)
            System.out.println("Ошибка при проверке md5, возможно неправильное имя файла или ссылка");
            return true;
    }
    //return false;
    } 
    
private String excutePostMD5(String targetURL, String urlParameters)
  {
    HttpURLConnection connection = null;
    try
    {
      URL url = new URL(targetURL);
      connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      connection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));
      connection.setRequestProperty("Content-Language", "en-US");
      connection.setUseCaches(false);
      connection.setDoInput(true);
      connection.setDoOutput(true);
      connection.connect();

      DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
      wr.writeBytes(urlParameters);
      wr.flush();
      wr.close();

      InputStream is = connection.getInputStream();
      BufferedReader rd = new BufferedReader(new InputStreamReader(is));

      StringBuffer response = new StringBuffer();
      String line;
      while ((line = rd.readLine()) != null)
      {
        response.append(line);
        response.append('\r');
      }
      rd.close();

      String str1 = response.toString();
      return str1;
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return null;
    }
    finally
    {
      if (connection != null)
        connection.disconnect();
    }
  }
      //read file md5
  private String readMD5File(File file) /*throws Exception*/
  {
    try{
        DataInputStream dis = new DataInputStream(new FileInputStream(file));
        String version = dis.readUTF();
        dis.close();
        
        return version;
    
    }catch (Exception e) {
            //e.printStackTrace();
            //System.out.print(e.toString());
        if(GlobalVar.debug)
            System.out.println("Не найден файл "+ file.getName());
         return "0";
    }
    
  }

  private void writeMD5File(File file, String version) /*throws Exception*/ {
    try{  
        if(!file.exists())
        {
            file.createNewFile();
        }
    DataOutputStream dos = new DataOutputStream(new FileOutputStream(file));
    dos.writeUTF(version);
    dos.close();
    }catch(Exception e)
    {
        if(GlobalVar.debug)
        System.out.println("Ошибка при записи файла");
    }
  }
    
    
}
