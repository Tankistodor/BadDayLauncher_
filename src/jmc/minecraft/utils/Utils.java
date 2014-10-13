/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmc.minecraft.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import jmc.minecraft.MainForm;

/**
 * 
 * @Copy from notch launcher
 */
public class Utils {

	private static File workDir = null;

	public static enum OS {
		linux, solaris, windows, macos, unknown;
	}

	
	/**
	 * Used to get the current operating system
	 * 
	 * @return OS enum representing current operating system
	 */
	public static OS getPlatform() {

		String osName = System.getProperty("os.name").toLowerCase();

		if (osName.contains("win"))
			return OS.windows;
		if (osName.contains("mac"))
			return OS.macos;
		if (osName.contains("solaris"))
			return OS.solaris;
		if (osName.contains("sunos"))
			return OS.solaris;
		if (osName.contains("linux"))
			return OS.linux;
		if (osName.contains("unix"))
			return OS.linux;

		return OS.unknown;
	}
	
	/**
     * Used to get the java delimiter for current OS
     * @return string containing java delimiter for current OS
     */
    public static String getJavaDelimiter () {
        switch (getPlatform()) {
        case windows:
            return ";";
        case linux:
            return ":";
        case macos:
            return ":";
        default:
            return ";";
        }
    }
	

	/**
	 * Used to check if Windows is 64-bit
	 * 
	 * @return true if 64-bit Windows
	 */
	public static boolean is64BitWindows() {
		String arch = System.getenv("PROCESSOR_ARCHITECTURE");
		String wow64Arch = System.getenv("PROCESSOR_ARCHITEW6432");
		return (arch.endsWith("64") || (wow64Arch != null && wow64Arch
				.endsWith("64")));
	}

	/**
	 * Used to check if a posix OS is 64-bit
	 * 
	 * @return true if 64-bit Posix OS
	 */
	public static boolean is64BitPosix() {
		String line, result = "";
		try {
			Process command = Runtime.getRuntime().exec("uname -m");
			BufferedReader in = new BufferedReader(new InputStreamReader(
					command.getInputStream()));
			while ((line = in.readLine()) != null) {
				result += (line + "\n");
			}
		} catch (Exception e) {
			Utils.LogPrintConsole("Posix bitness check failed" + e);
		}
		// 32-bit Intel Linuces, it returns i[3-6]86. For 64-bit Intel, it says
		// x86_64
		return result.contains("_64");
	}

	public static long getOSTotalMemory() {
		return getOSMemory("getTotalPhysicalMemorySize",
				"Could not get RAM Value");
	}

	public static long getOSFreeMemory() {
		return getOSMemory("getFreePhysicalMemorySize",
				"Could not get free RAM Value");
	}

	private static long getOSMemory(String methodName, String warning) {
		long ram = 0;

		OperatingSystemMXBean operatingSystemMXBean = ManagementFactory
				.getOperatingSystemMXBean();
		Method m;
		try {
			m = operatingSystemMXBean.getClass().getDeclaredMethod(methodName);
			m.setAccessible(true);
			Object value = m.invoke(operatingSystemMXBean);
			if (value != null) {
				ram = Long.valueOf(value.toString()) / 1024 / 1024;
			} else {
				Utils.LogPrintConsole(warning);
				ram = 1024;
			}
		} catch (Exception e) {
			Utils.LogPrintConsole("Error while getting OS memory info" + e);
		}

		return ram;
	}

	
	 /*public String getJavaPath () {
		 JavaInfo javaVersion;

	        if (Utils.getPlatform() == OS.macos) {
	            javaVersion = JavaFinder.parseJavaVersion();

	            if (javaVersion != null && javaVersion.path != null)
	                return javaVersion.path;
	        } else if (Utils.getPlatform() == OS.windows) {
	            javaVersion = JavaFinder.parseJavaVersion();

	            if (javaVersion != null && javaVersion.path != null)
	                return javaVersion.path.replace(".exe", "w.exe");
	        }

	        // Windows specific code adds <java.home>/bin/java no need mangle javaw.exe here.
	        return System.getProperty("java.home") + "/bin/java";
	    }
	*/
	
	/**
	 * Returns user selected or automatically selected JVM's JavaInfo object.
	 */
	public static String getCurrentJava() {
		return System.getProperty("java.version");
	}
	
	/**
	 * Returns user selected or automatically selected JVM's JavaInfo object.
	 */
	public static boolean getCurrentJavaIs64() {
		return System.getProperty("sun.arch.data.model").equals("64");
		//return is64BitWindows();
	}

	public static String getRamMax() {
		if (getCurrentJavaIs64() && Utils.getOSTotalMemory() > 6144)// 6gb or more
																// default to
																// 2gb of ram
																// for MC
			return Integer.toString(2048);
		else if (getCurrentJavaIs64())// on 64 bit java default to 1.5gb newer
									// pack's need more than a gig
			return Integer.toString(1536);
		return Integer.toString(1024);
	}

	public static File getWorkingDirectory() {
		if (workDir == null)
			workDir = getWorkingDirectory(GlobalVar.WorkDir);
		return workDir;
	}

	/*
	 * Get working dir to current OS
	 */
	public static File getWorkingDirectory(String applicationName) {
		String userHome = System.getProperty("user.home", ".");
		File workingDirectory;
		switch (getPlatform().ordinal()) {
		case 0:
			System.out.println("Current system linux");
			workingDirectory = new File(userHome, '.' + applicationName + '/');
			break;
		case 1: {
			System.out.println("Current system solaris");
			workingDirectory = new File(userHome, '.' + applicationName + '/');
			break;
		}
		case 2: {
			System.out.println("Current system windows");
			String applicationData = System.getenv("APPDATA");
			if (applicationData != null)
				workingDirectory = new File(applicationData, "."
						+ applicationName + '/');
			else
				workingDirectory = new File(userHome,
						'.' + applicationName + '/');
			break;
		}
		case 3: {
			System.out.println("Current system mac os");
			/*workingDirectory = new File(userHome,
					"Library/Application Support/" + applicationName);*/
			workingDirectory = new File(userHome, '.' + applicationName + '/'); // Tanke Test linux
			break;
		}

		default: {
			System.out.println("Unknown system =(");
			workingDirectory = new File(userHome, applicationName + '/');
		}
		}
		if ((!workingDirectory.exists()) && (!workingDirectory.mkdirs()))
			throw new RuntimeException(
					"The working directory could not be created: "
							+ workingDirectory);
		return workingDirectory;
	}

	/*
	 * @Open link in desktop web browser
	 */
	public static void openLink(URI uri) {
		try {
			Object o = Class.forName("java.awt.Desktop")
					.getMethod("getDesktop", new Class[0])
					.invoke(null, new Object[0]);
			o.getClass().getMethod("browse", new Class[] { URI.class })
					.invoke(o, new Object[] { uri });
		} catch (Throwable e) {
			System.out.println("Failed to open link " + uri.toString());
		}
	}

	/*
	 * Execute post and wait answer
	 */
	public static String excutePost(String targetURL, String urlParameters) {
		HttpURLConnection connection = null;
		try {
			URL url = new URL(targetURL);

			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");

			connection.setRequestProperty("Content-Length",
					Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			connection.connect();

			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));

			StringBuffer response = new StringBuffer();
			String line;
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();

			String str1 = response.toString();
			return str1;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (connection != null)
				connection.disconnect();
		}
	}

	public static boolean isEmpty(String str) {
		return (str == null) || (str.length() == 0);
	}

	public static boolean isOnline() {
		Boolean result = false;
		HttpURLConnection con = null;
		try {
			// HttpURLConnection.setFollowRedirects(false);
			// HttpURLConnection.setInstanceFollowRedirects(false)
			con = (HttpURLConnection) new URL(GlobalVar.HostUrl)
					.openConnection();
			con.setRequestMethod("HEAD");
			result = (con.getResponseCode() == HttpURLConnection.HTTP_OK);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.disconnect();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	public static long downloadFilesSize(String strURL) {
		try {
			URL connection = new URL(strURL);
			HttpURLConnection urlconn;
			urlconn = (HttpURLConnection) connection.openConnection();
			urlconn.setRequestMethod("GET");
			urlconn.connect();

			return urlconn.getContentLength();
		} catch (IOException e) {
			System.out.println(e);
			return 0;
		}
	}

	public static boolean login(String userName, String password) {
		String token = UUID.randomUUID().toString();
		try {
			String parameters = "user=" + URLEncoder.encode(userName, "UTF-8")
					+ "&password=" + URLEncoder.encode(password, "UTF-8")
					+ "&clientToken=" + URLEncoder.encode(token, "UTF-8")
					+ "&version="
					+ URLEncoder.encode(GlobalVar.Version, "UTF-8");
			String result = Utils.excutePost(GlobalVar.AuthURL, parameters);
			if (!result.contains(":")) {
				if (result.trim().equals("Bad login")) {
					JOptionPane.showMessageDialog(null,
							"Неправильный логин или пароль!", "Ошибка",
							JOptionPane.WARNING_MESSAGE);
					return false;
				} else if (result.trim().equals("Old version")) {
					JOptionPane.showMessageDialog(null,
							"Нужно обновить лаунчер!", "Ошибка",
							JOptionPane.WARNING_MESSAGE);
					openLink(new URI(GlobalVar.DownloadNewLauncherURL));
					System.exit(0); // Close launcher and open download launcher
									// link
					return false;
				} else {
					JOptionPane.showMessageDialog(null, result,
							"Неизвестная ошибка", JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}
			String[] values = result.split(":");

			
			GlobalVar.latestVersion = values[0].trim();
			GlobalVar.downloadTicket = values[1].trim();
			GlobalVar.userName = values[2].trim();
			GlobalVar.sessionId = values[3].trim();
			GlobalVar.uuid = values[4].trim();

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
		
	public static void ForseUpdate() {
		String FullPath = getWorkingDirectory() + File.separator
				+ GlobalVar.itemsServers[GlobalVar.CurrentServer];
		deleteDirectory(FullPath);

	}

	public static void deleteDirectory(File Folder) {

		if (!Folder.exists()) {
			System.out.println("Folder not found. Return " + Folder.getPath());
			return;
		}
		if (Folder.isDirectory()) {
			for (File f : Folder.listFiles()) {
				deleteDirectory(f); // this is magic
			}
			try {
				System.out.println("Deleting folder: " + Folder.getPath());
				Folder.delete();
			} catch (Exception ex) {
				System.out
						.println("Error deleting folder: " + Folder.getName());
			}
		} else {
			try {
				System.out.println("Deleting file: " + Folder.getName());
				Folder.delete();
			} catch (Exception e) {
				System.out.println("Error deleting file: " + Folder.getName());
			}
		}
	}

	public static void deleteDirectory(String FullPathToFolder) {
		File dir = new File(FullPathToFolder);
		deleteDirectory(dir);

	}

	public static void LogPrintConsole(String Message) {
		//MainForm.Log(Message);
		System.out.println(Message);
	}

	public static void ClearLog() {
		MainForm.LogClear();
	}
	
	/*public static String inttostr(String text)
	{
		String res = "";
		for(int i = 0; i < text.split("-").length; i++) res += (char)Integer.parseInt(text.split("-")[i]);
		return res;
	}
	
	public static String xorencode(String text, String key)
	{
		String res = ""; int j = 0;
		for (int i = 0; i < text.length(); i++)
		{
			res += (char)(text.charAt(i) ^ key.charAt(j));
			j++; if(j==key.length()) j = 0;
		}
		return res;
	}*/
    
	
	public static boolean getShadersEnable(String clientName) {
		
		String ClientFolderPath = Utils.getWorkingDirectory()
				+ File.separator + GlobalVar.itemsServers[GlobalVar.CurrentServer]
				+ File.separator + ".minecraft" + File.separator;
		
		File f = new File(ClientFolderPath + "mods" + File.separator + GlobalVar.clinetShaderModName);
		if(f.exists() && !f.isDirectory()) { return true; }
		
		f = new File(ClientFolderPath + "mods" + File.separator + GlobalVar.clinetShaderModName + ".disable");
		if(f.exists() && !f.isDirectory()) { return false; }
		
		
		return false;
	}

	public static boolean setShaderEnable(String clientName, boolean selected) {
		String ClientFolderPath = Utils.getWorkingDirectory()
				+ File.separator + GlobalVar.itemsServers[GlobalVar.CurrentServer]
				+ File.separator + ".minecraft" + File.separator;
		
		
		if (selected) {

			File f = new File(ClientFolderPath + "mods" + File.separator + GlobalVar.clinetShaderModName);
			if(f.exists() && !f.isDirectory()) { return true; }
			
			File fdis = new File(ClientFolderPath + "mods" + File.separator + GlobalVar.clinetShaderModName + ".disable");
			if(fdis.exists() && !fdis.isDirectory()) { fdis.renameTo(f); return true; }
			return false;
		} else {

			File fdis = new File(ClientFolderPath + "mods" + File.separator + GlobalVar.clinetShaderModName + ".disable");
			//if(fdis.exists() && !fdis.isDirectory()) { fdis.renameTo(f); return; }
			
			File f = new File(ClientFolderPath + "mods" + File.separator + GlobalVar.clinetShaderModName);
			if(f.exists() && !f.isDirectory()) { f.renameTo(fdis); return true; }
			return false;
		}
		
		//return false;
		
	}
}
