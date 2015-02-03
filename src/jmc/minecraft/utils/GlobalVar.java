/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmc.minecraft.utils;

/**
 *
 * @author DimanA90
 * Include all need global vars
 */
public class GlobalVar {
    
    public static String LauncherMode = "default";
    public static String LauncherStandalonePath = "";
    
    //Work dir  minecraft/
    public static String WorkDir = new String();
    //Main window title
    public static String MainWndTitle = new String();
    //News url
    public static String NewsURL = new String();
    //Combo box items
    public static String[] itemsServers;
    public static String[] ClientNames;
    public static String[] ArchivesList;
    //dir name to client and updatelink creator
    public static String RegURL;
    public static String DownloadClientRootURL;
    public static String LauncherFileName;
    public static String DownloadNewLauncherURL;
    public static String AuthURL;
    public static String Version;
    public static String HostUrl;
    
    
    
    //Do not touch
    //User info
    public static String userName = "User name";
    public static String password = "password";
    public static String latestVersion;
    public static String downloadTicket;
    public static String sessionId;
    
    public static String clientinfo;
    //public static boolean oldminecarft; //Старый метод запуска игры
    public static String fmlVersion; //Запускать как версию фмл модификацию
    public static boolean lightLoader = false;
    public static String clinetShaderModName;
    public static boolean Java64;
    public static boolean JavaVer17;
    
    public static String[] addons;
    public static String[] addonsinfo;
    
    public static String Ram = "512m";
    public static int ramindex = 3;
    public static String RamPerm = "256m";
    public static int ramPermindex = 3;
    
    public static boolean ForceUpdate = false;
    public static boolean isOnline;
    public static void setCurrentServer(long num){
        CurrentServer = (int) num;
    }
    
    public static int CurrentServer = 0;
    
    //Current client extra settings
    public static boolean ExpertSettings = false;
    public static String JavaPath ;
    public static String JavaVMArg ;
    //
    public static boolean debug = false;
	public static String uuid;
    
}
