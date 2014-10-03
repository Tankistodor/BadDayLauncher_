/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmc.minecraft;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import jmc.minecraft.utils.GlobalVar;

import jmc.minecraft.utils.Utils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author DimanA90
 */
public class LauncherPreloader {

    /**
     * @param args the command line arguments
     */
public static void main(String[] args) {
 

 File AppPath = new File(LauncherPreloader.class.getProtectionDomain().getCodeSource().getLocation().getPath());       
        ArrayList<String> params = new ArrayList<String>();

            params.add("java");
 
        params.add("-classpath");
        params.add(AppPath.getPath());
        params.add("jmc.minecraft.MainForm");
        

        ProcessBuilder pb = new ProcessBuilder(params);
        jmc.minecraft.utils.ConfigLoaderCore cf = new jmc.minecraft.utils.ConfigLoaderCore();

        //pb.environment().clear();
        if(Utils.getPlatform()==Utils.OS.windows)
        {
            pb.environment().put("APPDATA", GlobalVar.LauncherStandalonePath);  
        }else if (Utils.getPlatform()==Utils.OS.linux) 
        {  
            pb.environment().put("user.home", GlobalVar.LauncherStandalonePath);
        }else if(Utils.getPlatform()==Utils.OS.macos)
        {
            pb.environment().put("user.home", GlobalVar.LauncherStandalonePath);
        }
     
        //pb.directory(new File(ClientFolderPath));
        pb.redirectErrorStream(true);
        //pb.redirectOutput(log);     
    try {
        pb.start();
    } catch (IOException ex) {
        Logger.getLogger(LauncherPreloader.class.getName()).log(Level.SEVERE, null, ex);
    }
 }
}
