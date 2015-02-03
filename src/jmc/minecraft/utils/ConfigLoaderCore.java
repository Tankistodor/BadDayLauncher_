/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmc.minecraft.utils;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author DimanA90
 */
/*
 * Load core config for launcher
 */

public class ConfigLoaderCore  {
  
public void saveUserConfig()
{      
JSONObject obj = new JSONObject();
	obj.put("UserName", GlobalVar.userName);
        obj.put("Password", GlobalVar.password);
	obj.put("LastClient", GlobalVar.CurrentServer);
	try {
 
		FileWriter file = new FileWriter(new File(Utils.getWorkingDirectory(), "JMCLauncher.json"));
		file.write(obj.toJSONString());
		file.flush();
		file.close();
 
	} catch (IOException e) {
		e.printStackTrace();
	}
}
public void saveCurrentClientConfig()
{      
JSONObject obj = new JSONObject();
	obj.put("JavaPath", GlobalVar.JavaPath);
        obj.put("VMArg", GlobalVar.JavaVMArg);
	obj.put("ExpertMode", GlobalVar.ExpertSettings);
	try {
 
		FileWriter file = new FileWriter(new File(Utils.getWorkingDirectory() + File.separator + GlobalVar.itemsServers[GlobalVar.CurrentServer] + File.separator, "config.json"));
		file.write(obj.toJSONString());
		file.flush();
		file.close();
 
	} catch (IOException e) {
		e.printStackTrace();
	}
}

private static CharsetDecoder decoder(String encoding) {
    return Charset.forName(encoding).newDecoder()
        .onMalformedInput(CodingErrorAction.REPORT)
        .onUnmappableCharacter(CodingErrorAction.REPORT);
}

public void loadCurrentClientConfig()
{ 
JSONParser parser = new JSONParser();
	try {
                InputStream in = new FileInputStream(new File(Utils.getWorkingDirectory() + File.separator + GlobalVar.itemsServers[GlobalVar.CurrentServer] + File.separator, "config.json"));
		Object obj = parser.parse(new InputStreamReader(in, decoder("UTF-8")));
		JSONObject jsonObject = (JSONObject) obj;
 
                GlobalVar.JavaPath = (String) jsonObject.get("JavaPath");
                GlobalVar.JavaVMArg = (String) jsonObject.get("VMArg");
                GlobalVar.ExpertSettings = (boolean) jsonObject.get("ExpertMode");
                System.out.println(GlobalVar.JavaPath);
                System.out.println(GlobalVar.JavaVMArg);

	} catch (IOException e) {
		System.out.println("User config not found");
                GlobalVar.ExpertSettings = false;
	} catch (ParseException e) {
		e.printStackTrace();
                GlobalVar.ExpertSettings = false;
	}     
}   
/*
 * Load user config for launcher
 */    
public void LoadUserConfig()
{
JSONParser parser = new JSONParser();
	try {
                InputStream in = new FileInputStream(new File(Utils.getWorkingDirectory()+File.separator+"JMCLauncher.json"));
		Object obj = parser.parse(new InputStreamReader(in));
		JSONObject jsonObject = (JSONObject) obj;
 
                GlobalVar.userName = (String) jsonObject.get("UserName");
                GlobalVar.password = (String) jsonObject.get("Password");
                GlobalVar.setCurrentServer((long) jsonObject.get("LastClient"));

	} catch (IOException e) {
		System.out.println("User config not found");
	} catch (ParseException e) {
		e.printStackTrace();
	} 
}

   /*
    * Load current client info
    */ 
public void LoadClientConfig(String clientName)
{

       JSONParser parser = new JSONParser();
	try {
                InputStream in = this.getClass().getResourceAsStream("/Configs/clientinfo/"+clientName+".json");
		Object obj = parser.parse(new InputStreamReader(in, decoder("UTF-8")));
		JSONObject jsonObject = (JSONObject) obj;
 
                //GlobalVar.oldminecarft =(boolean) jsonObject.get("RunAsOlderMinecraft");
                GlobalVar.fmlVersion = (String) jsonObject.get("RunMinecraftAsFML");
                GlobalVar.clientinfo = (String) jsonObject.get("info");
                    JSONArray ZipListTemp = (JSONArray) jsonObject.get("DownloadZipList");
                    GlobalVar.ArchivesList = new String[ZipListTemp.size()];  //Init array size
                ZipListTemp.toArray(GlobalVar.ArchivesList);  //Puts strings array
                GlobalVar.clinetShaderModName = (String) jsonObject.get("ShaderModName");
                GlobalVar.Java64 = (boolean) jsonObject.get("Java64");
                GlobalVar.JavaVer17 = (boolean) jsonObject.get("JavaVer17");
                GlobalVar.lightLoader = (boolean)  jsonObject.get("LightLoader");

	} catch (IOException e) {
		System.out.println("IO error on load client info config =" + e);
	} catch (ParseException e) {
		e.printStackTrace();
	} 
}

public void LoadCoreConfig()
{
JSONParser parser = new JSONParser();
	try {
                InputStream in = this.getClass().getResourceAsStream("/Configs/CoreConfig.json");
		Object obj = parser.parse(new InputStreamReader(in));
		JSONObject jsonObject = (JSONObject) obj;
 
                GlobalVar.Version = (String) jsonObject.get("Version");
                GlobalVar.WorkDir = (String) jsonObject.get("LauncherRootDir");
                GlobalVar.LauncherFileName = (String) jsonObject.get("LauncherFileName");
                GlobalVar.MainWndTitle = (String) jsonObject.get("WindowTitle");
                GlobalVar.HostUrl = (String) jsonObject.get("HostUrl");
                GlobalVar.NewsURL = (String) jsonObject.get("NewsUrl");
                    JSONArray ClNametemp = (JSONArray) jsonObject.get("clientnames");
                    GlobalVar.ClientNames = new String[ClNametemp.size()];  //Init array size
                ClNametemp.toArray(GlobalVar.ClientNames);  //Puts strings array
                    JSONArray ClDirNametemp = (JSONArray) jsonObject.get("clientdirnames");
                    GlobalVar.itemsServers = new String[ClDirNametemp.size()]; //Init array size
                ClDirNametemp.toArray(GlobalVar.itemsServers); //Puts strings array
                GlobalVar.AuthURL = (String) jsonObject.get("AuthUrl");
                GlobalVar.RegURL = (String) jsonObject.get("RegUrl"); 
                GlobalVar.DownloadClientRootURL = (String) jsonObject.get("ClientDownloadRootFolder");

	} catch (IOException e) {
		System.out.println("IO error on load core config =" + e);
	} catch (ParseException e) {
		e.printStackTrace();
	} 
}
public boolean LoadModeConfig()
{
        JSONParser parser = new JSONParser();
	try {
            String path = (new File(ConfigLoaderCore.class.getProtectionDomain().getCodeSource().getLocation().getPath())).getAbsolutePath();
            File filetemp = new File(path.substring(0, path.length()-4)+".json");
                System.out.println(filetemp.getAbsolutePath());
                InputStream in = new FileInputStream(filetemp);
		Object obj = parser.parse(new InputStreamReader(in));
		JSONObject jsonObject = (JSONObject) obj;
 
                GlobalVar.LauncherStandalonePath = (String) jsonObject.get("InstallPath");
                GlobalVar.LauncherMode = (String) jsonObject.get("LauncherMode");
                
             return true;
	} catch (IOException e) {
		System.out.println("Launcher mode config not found");
                return false;
	} catch (ParseException e) {
		e.printStackTrace();
                return false;
	} 
}
public void saveModeConfig()
{      
JSONObject obj = new JSONObject();
        obj.put("InstallPath", GlobalVar.LauncherStandalonePath);
        obj.put("LauncherMode", GlobalVar.LauncherMode);
	try 
        {
                String path = (new File(ConfigLoaderCore.class.getProtectionDomain().getCodeSource().getLocation().getPath())).getAbsolutePath();
    File filetemp = new File(path.substring(0, path.length()-4)+".json");
		FileWriter file = new FileWriter(filetemp);
		file.write(obj.toJSONString());
		file.flush();
		file.close();
 
	} catch (IOException e) 
        {
		e.printStackTrace();
	}
}

}
