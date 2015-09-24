package org.test.util;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class FileUtil {

	public static void saveToFile(String path, byte[] text) {
		try {
			Files.write(Paths.get(path), text);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveToFile(String path, String text) {
		System.out.println("FileUtil.saveToFile()");
		try {
			Files.write(Paths.get(path), text.getBytes("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String readFromFile(String path) {
		String text = "";
		try {
			text = new String(Files.readAllBytes(Paths.get(path)), "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return text;
	}
	
	public static byte[] readFromFile2(String path) {
		byte[] text = null;
		try {
			text = Files.readAllBytes(Paths.get(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return text;
	}
	
	public static List<String> scanPath(String path, String regular) {
		List<String> list = new ArrayList<String>();
		
		try {
			Files.walk(Paths.get(path)).forEach(filePath -> {
			    if (Files.isRegularFile(filePath)) {
			    	File file = new File(filePath.toString());
			    	String name = file.getName();
			    	
			    	if (name.matches(regular)) {
			    		list.add(file.getAbsolutePath());
					}
			    }
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	public static Boolean openFolderInExplorer(String path) {
		path = path.replace("/", "\\");
		try {
			File file = new File (path);
			Desktop desktop = Desktop.getDesktop();
			desktop.open(file);
			
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static Boolean fileExist(String path) {
		File file = new File(path);
		return file.exists();
	}
	
	public static Boolean checkDirExist(String path) {
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
			System.out.println("Launcher.checkDirExist()#File doesn't exist, create new: " + path);
			return false;
		}
		
		return true;
	}
	
	public static String randomFilename(String extension){
		return SecurityUtil.hashSha256(RandomUtil.filename(extension)) + "." + extension;
	}
	
	public static String currentPath() {
		URL location = FileUtil.class.getProtectionDomain().getCodeSource().getLocation();
		return new File(location.getFile()).getParent() + "/";
	}
	
	public static String humanReadableByteCount2(long bytes) {
		return FileUtils.byteCountToDisplaySize(bytes);
	}
	
	public static String humanReadableByteCount(long bytes, boolean si) {
	    int unit = si ? 1000 : 1024;
	    if (bytes < unit) return bytes + " B";
	    int exp = (int) (Math.log(bytes) / Math.log(unit));
	    String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
	    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
	
}
