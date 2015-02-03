/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmc.minecraft.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
//import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jmc.minecraft.MainForm;

//import jmc.minecraft.RunGame;

/**
 * 
 * @author DimanA90
 */
public class MCGameRuner {
	private String ClientFolderPath = Utils.getWorkingDirectory()
			+ File.separator + GlobalVar.itemsServers[GlobalVar.CurrentServer]
			+ File.separator + ".minecraft" + File.separator;
	private String AppDataPath = Utils.getWorkingDirectory() + File.separator
			+ GlobalVar.itemsServers[GlobalVar.CurrentServer] + File.separator;

	public void LetsGame(boolean Online) {
		ArrayList<String> params = new ArrayList<String>();
		File log = new File(ClientFolderPath + "game.log");
		
		Utils.LogPrintConsole("System.getProperty('os.name') == '"+System.getProperty("os.name")+"'");
		Utils.LogPrintConsole("System.getProperty('os.version') == '"+System.getProperty("os.version")+"'");
		Utils.LogPrintConsole("System.getProperty('os.arch') == '"+System.getProperty("os.arch")+"'");
		Utils.LogPrintConsole("System.getProperty('java.version') == '"+System.getProperty("java.version")+"'");
		Utils.LogPrintConsole("System.getProperty('java.vendor') == '"+System.getProperty("java.vendor")+"'");
		Utils.LogPrintConsole("System.getProperty('sun.arch.data.model') == '"+System.getProperty("sun.arch.data.model")+"'");

		params.add("java");
		
		Utils.ClearLog();
		Utils.LogPrintConsole("Подбор оптимальных опций ява машины:");
	
		Utils.LogPrintConsole("Улучшение чистки памяти -Xincgc");
		params.add("-Xincgc");
		Utils.LogPrintConsole("Улучшение сборщика мусора -XX:+UseParNewGC");
		params.add("-XX:+UseParNewGC");
		
		Utils.LogPrintConsole("Улучшение чистки памяти -XX:-UseAdaptiveSizePolicy");
		params.add("-XX:-UseAdaptiveSizePolicy");
	
		params.add("-XX:+CICompilerCountPerCPU");
		
		Utils.LogPrintConsole("Двух уровневая компиляция -XX:+TieredCompilation");
		params.add("-XX:+TieredCompilation");
		
		// Memory
		Utils.LogPrintConsole("Установка памяти");
		String rmax = Utils.getRamMax();
        boolean memorySet = false;
        try {
            int min = 256;
            if (rmax != null && Integer.parseInt(rmax) > 0) {
            	params.add("-Xms" + min + "M");
            	Utils.LogPrintConsole("Ставим MinMemory " + min);
            	params.add("-Xmx" + rmax + "M");
                Utils.LogPrintConsole("Ставим MaxMemory " + rmax);
                memorySet = true;
            }
        } catch (Exception e) {
        	Utils.LogPrintConsole("Error parsing memory settings "+ e);
        }
        if (!memorySet) {
        	params.add("-Xms" + 256 + "M");
        	Utils.LogPrintConsole("Defaulting MinMemory to " + 256);
        	params.add("-Xmx" + 1024 + "M");
            Utils.LogPrintConsole("Defaulting MaxMemory to " + 1024);
        }
		
        // PermSize
        String maxPermSize = null;
        if (Utils.getPlatform().equals(Utils.OS.windows)) {
            if (!Utils.is64BitWindows()) {
                if (maxPermSize == null || maxPermSize.isEmpty()) {
                    if (Utils.getOSTotalMemory() > 2046) {
                        maxPermSize = "192m";
                        Utils.LogPrintConsole("Defaulting PermSize to 192m");
                    } else {
                        maxPermSize = "128m";
                        Utils.LogPrintConsole("Defaulting PermSize to 128m");
                    }
                }
            }
        }
        
        if (maxPermSize == null || maxPermSize.isEmpty()) {
            // 64-bit or Non-Windows
            maxPermSize = "256m";
            Utils.LogPrintConsole("Defaulting PermSize to 256m");
        }

        params.add("-XX:PermSize=" + maxPermSize);
		
		params.add("-Djava.library.path="+ClientFolderPath + "bin" + File.separator + "natives");
		params.add("-Dorg.lwjgl.librarypath="+ClientFolderPath + "bin" + File.separator + "natives");
		params.add("-Dnet.java.games.input.librarypath="+ClientFolderPath + "bin" + File.separator + "natives");
		params.add("-Duser.home=" + ClientFolderPath);
		
		params.add("-Dfml.ignoreInvalidMinecraftCertificates=true");
		params.add("-Dfml.ignorePatchDiscrepancies=true");
		
		/*params.add("-cp");
		params.add(
				ClientFolderPath + "bin" + File.separator + "launchwrapper-1.8.jar"+Utils.getJavaDelimiter()+
				ClientFolderPath + "bin" + File.separator + "minecraftforge-9.11.1.965.jar"+Utils.getJavaDelimiter()+
				ClientFolderPath + "bin" + File.separator + "asm-all-4.1.jar"+Utils.getJavaDelimiter()+
				ClientFolderPath + "bin" + File.separator + "scala-library-2.10.2.jar"+Utils.getJavaDelimiter()+
				ClientFolderPath + "bin" + File.separator + "scala-compiler-2.10.2.jar"+Utils.getJavaDelimiter()+
				ClientFolderPath + "bin" + File.separator + "lzma-0.0.1.jar"+Utils.getJavaDelimiter()+
				ClientFolderPath + "bin" + File.separator + "1.6.4-Forge9.11.1.965.jar"+Utils.getJavaDelimiter()+
				ClientFolderPath + "bin" + File.separator + "jopt-simple-4.5.jar"+Utils.getJavaDelimiter()+
				ClientFolderPath + "bin" + File.separator + "codecjorbis-20101023.jar"+Utils.getJavaDelimiter()+
				ClientFolderPath + "bin" + File.separator + "codecwav-20101023.jar"+Utils.getJavaDelimiter()+
				ClientFolderPath + "bin" + File.separator + "libraryjavasound-20101123.jar"+Utils.getJavaDelimiter()+
				ClientFolderPath + "bin" + File.separator + "librarylwjglopenal-20100824.jar"+Utils.getJavaDelimiter()+
				ClientFolderPath + "bin" + File.separator + "soundsystem-20120107.jar"+Utils.getJavaDelimiter()+
				ClientFolderPath + "bin" + File.separator + "argo-2.25_fixed.jar"+Utils.getJavaDelimiter()+
				ClientFolderPath + "bin" + File.separator + "bcprov-jdk15on-1.47.jar"+Utils.getJavaDelimiter()+
				ClientFolderPath + "bin" + File.separator + "guava-14.0.jar"+Utils.getJavaDelimiter()+
				ClientFolderPath + "bin" + File.separator + "commons-lang3-3.1.jar"+Utils.getJavaDelimiter()+
				ClientFolderPath + "bin" + File.separator + "commons-io-2.4.jar"+Utils.getJavaDelimiter()+
				ClientFolderPath + "bin" + File.separator + "jinput-2.0.5.jar"+Utils.getJavaDelimiter()+
				ClientFolderPath + "bin" + File.separator + "jutils-1.0.0.jar"+Utils.getJavaDelimiter()+
				ClientFolderPath + "bin" + File.separator + "gson-2.2.2.jar"+Utils.getJavaDelimiter()+
				ClientFolderPath + "bin" + File.separator + "lwjgl-2.9.1.jar"+Utils.getJavaDelimiter()+
				ClientFolderPath + "bin" + File.separator + "lwjgl_util-2.9.1.jar"+Utils.getJavaDelimiter()+
				ClientFolderPath + "bin" + File.separator + "lwjgl-platform-2.9.1.jar"+Utils.getJavaDelimiter()+
				ClientFolderPath + "bin" + File.separator + "jinput-platform-2.0.5.jar"
				);*/
		
		if (GlobalVar.fmlVersion.equals("16x")) {

			params.add("-cp");
			params.add(
					ClientFolderPath + "bin" + File.separator + "launchwrapper-1.8.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "minecraftforge-9.11.1.965.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "asm-all-4.1.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "scala-library-2.10.2.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "scala-compiler-2.10.2.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "lzma-0.0.1.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "1.6.4-Forge9.11.1.965.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "jopt-simple-4.5.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "codecjorbis-20101023.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "codecwav-20101023.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "libraryjavasound-20101123.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "librarylwjglopenal-20100824.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "soundsystem-20120107.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "argo-2.25_fixed.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "bcprov-jdk15on-1.47.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "guava-14.0.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "commons-lang3-3.1.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "commons-io-2.4.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "jinput-2.0.5.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "jutils-1.0.0.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "gson-2.2.2.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "lwjgl-2.9.1.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "lwjgl_util-2.9.1.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "lwjgl-platform-2.9.1.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "jinput-platform-2.0.5.jar"
					);

			
			//--tweakClass cpw.mods.fml.common.launcher.FMLTweaker --username ${auth_player_name} --session ${auth_session} --version ${version_name} --gameDir ${game_directory} --assetsDir ${game_assets}
			params.add("net.minecraft.launchwrapper.Launch");
			params.add("--tweakClass");
			params.add("cpw.mods.fml.common.launcher.FMLTweaker");
			params.add("--username");
			params.add(GlobalVar.userName);
			params.add("--session");
			if (Online) {
				params.add(GlobalVar.sessionId);
			} else {
				params.add("0000");
			}
			
			params.add("--version");
			params.add("1.6.4");
			
			params.add("--gameDir");
			params.add(ClientFolderPath);
			params.add("--assetsDir");
			params.add(ClientFolderPath + "assets");
			
		} else if (GlobalVar.fmlVersion.equals("1.7.10")) {
			
			params.add("-cp");
			String libs = ClientFolderPath + "bin" + File.separator + "forge-1.7.10.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "launchwrapper-1.11.jar"+Utils.getJavaDelimiter()+
					
					ClientFolderPath + "bin" + File.separator + "1.7.10.jar"+Utils.getJavaDelimiter()+
					
					ClientFolderPath + "bin" + File.separator + "akka-actor_2.11-2.3.3.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "asm-all-5.0.3.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "authlib-1.5.16.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "codecjorbis-20101023.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "codecwav-20101023.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "commons-codec-1.9.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "commons-compress-1.8.1.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "commons-io-2.4.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "commons-lang3-3.2.1.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "commons-logging-1.1.3.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "config-1.2.1.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "gson-2.2.4.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "guava-16.0.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "httpclient-4.3.3.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "httpcore-4.3.2.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "icu4j-core-mojang-51.2.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "jinput-2.0.5.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "jinput-platform-2.0.5-natives-windows.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "jopt-simple-4.5.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "jutils-1.0.0.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "libraryjavasound-20101123.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "librarylwjglopenal-20100824.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "log4j-api-2.0-beta9.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "log4j-core-2.0-beta9.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "lwjgl-2.9.1.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "lwjgl-platform-2.9.1-natives-windows.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "lwjgl_util-2.9.1.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "lzma-0.0.1.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "netty-all-4.0.10.Final.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "realms-1.3.5.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "scala-actors-migration_2.11-1.1.0.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "scala-compiler-2.11.1.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "scala-continuations-library_2.11-1.0.2.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "scala-continuations-plugin_2.11.1-1.0.2.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "scala-library-2.11.1.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "scala-parser-combinators_2.11-1.0.1.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "scala-reflect-2.11.1.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "scala-swing_2.11-1.0.1.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "scala-xml_2.11-1.0.2.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "soundsystem-20120107.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "trove4j-3.0.3.jar"+Utils.getJavaDelimiter()+
					ClientFolderPath + "bin" + File.separator + "vecmath-1.3.1.jar";
			if (GlobalVar.lightLoader)
				libs = ClientFolderPath + "bin" + File.separator + "liteloader-1.7.10.jar"+Utils.getJavaDelimiter() + libs;
			params.add(
					libs
					);
					
			//--username ${auth_player_name} --version ${version_name} --gameDir ${game_directory} --assetsDir ${assets_root} --assetIndex ${assets_index_name} --uuid ${auth_uuid} --accessToken ${auth_access_token} --userProperties ${user_properties} --userType ${user_type}",
			params.add("net.minecraft.launchwrapper.Launch");
			
			if (GlobalVar.lightLoader) {
				params.add("--tweakClass");
				params.add("com.mumfrey.liteloader.launch.LiteLoaderTweaker");
			}
			
			params.add("--tweakClass");
			params.add("cpw.mods.fml.common.launcher.FMLTweaker");
			
			
			
			params.add("--username");
			params.add(GlobalVar.userName);
			
			params.add("--version");
			params.add("1.7.10");
			
			params.add("--gameDir");
			params.add(ClientFolderPath);
			params.add("--assetsDir");
			params.add(ClientFolderPath + "assets");
			params.add("--assetIndex");
			params.add("1.7.10");
			
			params.add("--uuid");
			if (Online) {
				params.add(GlobalVar.uuid);
			} else {
				params.add("0000");
			}

			params.add("--accessToken");
			if (Online) {
				params.add(GlobalVar.sessionId);
			} else {
				params.add("0000");
			}
			
			params.add("--userPropertie");
			params.add("{}");
			
			params.add("--userType");
			params.add("legacy");
			
		} else {
			params.add("net.minecraft.client.main.Main");
		}

		ProcessBuilder pb = new ProcessBuilder(params);

		pb.environment().remove("_JAVA_OPTIONS");
		pb.environment().remove("JAVA_TOOL_OPTIONS");
		pb.environment().remove("JAVA_OPTIONS");
		
		if (Utils.getPlatform() == Utils.OS.windows) {
			pb.environment().put("PATH",
					ClientFolderPath + "bin" + File.separator + "natives");
			pb.environment().put("APPDATA", AppDataPath); // То что надо
		} else if (Utils.getPlatform() == Utils.OS.linux) { // Unix like
			pb.environment().put("LD_LIBRARY_PATH",
					ClientFolderPath + "bin" + File.separator + "natives");
			pb.environment().put("user.home", AppDataPath);
		} else if (Utils.getPlatform() == Utils.OS.macos) {
			pb.environment().put("DYLD_LIBRARY_PATH",
					ClientFolderPath + "bin" + File.separator + "natives");
			pb.environment().put("user.home", AppDataPath);
		}
		
		Utils.LogPrintConsole("Params: " + params.toString());
		Utils.LogPrintConsole("Env: " + pb.environment().toString());
		
		pb.directory(new File(ClientFolderPath));
		pb.redirectErrorStream(true);
		pb.redirectOutput(log);
		try {
			pb.start();
			// Process process = pb.start();
		} catch (IOException ex) {
			Logger.getLogger(MCGameRuner.class.getName()).log(Level.SEVERE,
					null, ex);
		}
	}
}
