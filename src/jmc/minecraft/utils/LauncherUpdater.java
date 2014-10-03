/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmc.minecraft.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.JOptionPane;


/**
 *
 * @author DimanA90
 */
public class LauncherUpdater {

public static void UpdateLauncher(){
    if(Utils.isOnline())        DownloadNewLauncher();
    
}    
    
private static void DownloadNewLauncher( ) {
        try {
            //progressCurr.setValue(0);
            String strPath = Utils.getWorkingDirectory() + File.separator+GlobalVar.LauncherFileName;
            
            File AppPath = new File(LauncherUpdater.class.getProtectionDomain().getCodeSource().getLocation().getPath());

            URL connection = new URL(GlobalVar.DownloadClientRootURL+"/"+GlobalVar.LauncherFileName);
            HttpURLConnection urlconn;
            urlconn = (HttpURLConnection) connection.openConnection();
            urlconn.setRequestMethod("GET");
            urlconn.connect();
            int fileSize = urlconn.getContentLength();
            //Сверяем размер файла на сервере и локальную запись, не совпали? качаем новую версию
         if(urlconn.getResponseCode() != 404)
         {
            if(fileSize != readMD5File(strPath))
            {
                
                
                JOptionPane.showMessageDialog( null,
                    "Лаунчер будет автоматически обновлен",
                    "Обновлене",
                    JOptionPane.WARNING_MESSAGE);
                
                
            InputStream in = null;
            in = urlconn.getInputStream();
            OutputStream writer = new FileOutputStream(AppPath.getAbsolutePath());
            byte buffer[] = new byte[55000];
            int c = in.read(buffer);
            int dwnloaded =0;
            while (c > 0) {
                writer.write(buffer, 0, c);
                dwnloaded +=c;
                try{
                //progressCurr.setValue((int)((dwnloaded/1024)*100/fileSize));
                //progressCurr.setString("Скачивается "+GlobalVar.LauncherFileName + "  "+progressCurr.getValue()+"%  " +(dwnloaded/1024)+"/"+(fileSize/1024)+"Kb");

                }catch(ArithmeticException ae){
                   //И такое тоже бывает
                }
                c = in.read(buffer);
            }
            writer.flush();
            writer.close();
            in.close();
            
            JOptionPane.showMessageDialog( null,
                    "Лаунчер успешно обновлен\nПосле закрытия этого окна лаунчер будет закрыт",
                    "Обновлене успешно",
                    JOptionPane.INFORMATION_MESSAGE);
            
            writeMD5File(strPath, fileSize); //Записываем новый размер лаунчера т.к. обновились успешно
            System.exit(1);
            }
          }else{
         }
        } catch (IOException e) {
            JOptionPane.showMessageDialog( null,
                    "Ошибка обновления лаунчера\nПосле закрытия этого окна будет запущена текущая версия лаунчера",
                    "Обновлене не удалось",
                    JOptionPane.ERROR_MESSAGE);
            System.out.println(e);
        }
}
private static int readMD5File(String path) /*throws Exception*/
  {
      File file = new File(path+".size");
    try{
        DataInputStream dis = new DataInputStream(new FileInputStream(file));
        String version = dis.readUTF();
        dis.close();
        
        return Integer.parseInt(version);
    
    }catch (Exception e) {
         return 0;
    }
  }

private static void writeMD5File(String path, int size) /*throws Exception*/ {
    File file = new File(path+".size");
      try{  
        if(!file.exists())
        {
            file.createNewFile();
        }
    DataOutputStream dos = new DataOutputStream(new FileOutputStream(file));
    dos.writeUTF(String.valueOf(size) );
    dos.close();
    }catch(Exception e)
    {

    }
}
}

