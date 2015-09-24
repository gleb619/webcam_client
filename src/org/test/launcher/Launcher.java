package org.test.launcher;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.test.config.Project;
import org.test.config.Settings;
import org.test.data.types.InformationStore;
import org.test.data.types.Logger;
import org.test.util.FileUtil;
import org.test.util.LoginUtil;
import org.test.util.PropertiesUtil;
import org.test.util.RandomUtil;
import org.test.util.SecurityUtil;
import org.test.util.Utils;
import org.test.view.LogPane;
import org.test.view.LoginPane;
import org.test.view.TrayUtil;
import org.test.worker.DetectWorker;

public class Launcher {

	private static InformationStore informationStore;
	
	public static void main(String[] args) throws Exception {
		
//		testHashTest("root1");
		
		style();
		work(args);
		start();
		
//		try {
//			testLocation();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		System.out.println("Launcher.main()#pass: " + testComplex("_PAssWORD1234567890ABC_"));
		
//		testDecrypt(testEncrypt());
		
//		String password = "_PAssWORD1234567890ABC_";
//		String name = FileUtil.randomFilename("db");
//		String path = FileUtil.currentPath() + name;
//		System.out.println("Launcher.main()#path: " + path);
//		
//		System.out.println("Launcher.main()#"
//				+ "\n\t password: " + password
//				+ "\n\t size: " + password.length()
//		);
//		
//		Instant start = Instant.now();
//		
//		byte[] encryptedText = null;
//		try {
//			encryptedText = testJunk(password);
//		} catch (GeneralSecurityException e) {
//			e.printStackTrace();
//		}
//		
//		System.out.println("\tIt take: " + Duration.between(start, Instant.now()).toMillis() + " ms");
//		
//		System.out.println("-------------------");
//		String decryptedText = "";
////		decryptedText = testDecrypt(encryptedText);
//		
//		start = Instant.now();
//		try {
//			decryptedText = testReadJunk(password);
//		} catch (UnsupportedEncodingException | GeneralSecurityException e1) {
//			e1.printStackTrace();
//		}
//		System.out.println("-------------------");
//		try {
//			readPasswordFromJunk(decryptedText);
//		} catch (GeneralSecurityException e) {
//			e.printStackTrace();
//		}
//		System.out.println("\tIt take: " + Duration.between(start, Instant.now()).toMillis() + " ms");
		
//		testChunck();
//		try {
//			testJunk();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		try {
//			testJunk();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		try {
//			work(args);
//			try {
//				motionTest();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
	}

	private static void start() {
		Logger logger = null;
		if (informationStore.getShowLogPane()) {
			logger = new LoggerX(new LogPane());
		} else {
			logger = new LoggerX();
		}

		DetectWorker detectWorker = new DetectWorker(informationStore, logger);
		TrayUtil trayUtil = new TrayUtil(informationStore, detectWorker
				.preStart(), logger).start();

		if (!trayUtil.loadKey()) {
			trayUtil.loginDialog();
		}

	}

	private static void startWithoutProtection() throws Exception {
		Logger logger = null;
		if (informationStore.getShowLogPane()) {
			logger = new LoggerX(new LogPane());
		} else {
			logger = new LoggerX();
		}
		
		LoginUtil.loadKey(informationStore);
		
		DetectWorker detectWorker = new DetectWorker(informationStore, logger);
		TrayUtil trayUtil = new TrayUtil(informationStore, detectWorker.preStart().init(), logger)
			.start();
		trayUtil.getDetectWorker()
			.start();
	}
	
	private static void style() {
		try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
        } catch (InstantiationException ex) {
        } catch (IllegalAccessException ex) {
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
        }
	}
	
	private static void work(String... args) throws Exception {
		String path = "";
		Boolean defaultSettings = true;
		
		if (args.length == 0) {
			if (Settings.DEBUG.getValue()) {
				path = "resources/config.properties";
			} else {
				path = "config.properties";
			}
		}
		else {
			path = args[0];
			defaultSettings = false;
		}

		if (!FileUtil.fileExist(path) && !defaultSettings) {
			throw new Exception("File doesn't exist: " + path);
		}
		
		PropertiesUtil propertiesUtil = new PropertiesUtil(new InformationStore()).init(path, defaultSettings);
		informationStore = propertiesUtil.getInformationStore();
		
		if (Settings.DEBUG.getValue()) {
			System.out.println("Use debug settings");
			informationStore.setUrlTemplate("http://localhost:8081/webcam-server/");
			informationStore.setHeartbeat("localhost");
			informationStore.setHeartbeatPort(8081);
//			informationStore.setKey("31999ada52811514c518aa9cc7432677e350e977f555da4f468dfefcff14295a");
//			informationStore.setPath("D:/TEMP/TEST2/");
			informationStore.setPath(FileUtil.currentPath() + Project.PHOTOS.value());
			informationStore.setShowLogPane(true);
		}
		
		informationStore.validate();
		
		System.out.println("Launcher.work()prepare work with:" + propertiesUtil.getInformationStore());
		System.out.println("--------------------------");
	}
	
	private static class LoggerX implements Logger {

		private LogPane logPane;
				
		public LoggerX() {
			super();
		}
		
		public LoggerX(LogPane logPane) {
			super();
			this.logPane = logPane;
		}

		@Override
		public LogPane getLogPane() {
			return logPane;
		}

		@Override
		public void log(String message) {
			System.out.println("Launcher.Logger.log()# " + message);
			if (logPane != null) {
				logPane.addText(message);
			}
		}

		@Override
		public void debug(String message) {
			if (informationStore == null) {
				informationStore = new InformationStore();
			}
			
			if (informationStore.getAllowDebug()) {
				System.out.println("Launcher.Logger.log()#[DEBUG] " + message);
				if (logPane != null) {
					logPane.addText("[DEBUG]" + message);
				}
			}
		}

		@Override
		public void error(String message) {
			System.err.println("[ERROR]::[" + new SimpleDateFormat("YYYY-MM-dd hh:mm:ss").format(new Date()) + "]# " + message);
			if (logPane != null) {
				logPane.addText("[ERROR]" + message);
			}
		}

	}

	/* #----------------------- May 13, 2015 ------------------------ */
	
	private static String testHashTest(String username) {
		String result = SecurityUtil.hashSha256(username + username);
		System.out.println("Launcher.testHashTest()#result: " + result);
		
		return result;
	}
	
	private static String testComplex(String key) {
		try {
			SecurityUtil.saveKey(key);
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
		System.out.println("--------------------------");
		return SecurityUtil.loadKey();
	}

	private static byte[] testEncrypt() {
		byte[] output = null;
		try {
			output = SecurityUtil.encrypt("password", "ENCODE this text");
			System.out.println("Launcher.testEncrypt()" + new String(output));
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
		
		return output;
	}
	
	private static String testDecrypt(byte[] text) {
		String output = "";
		try {
			output = SecurityUtil.decrypt("password", text);
			System.out.println("Launcher.testDecrypt()" + output);
		} catch (GeneralSecurityException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return output;
	}
	
	private static void testChunck() {
		List<Integer> result = Utils.chunkNumber(10203, 1000);
		System.out.println("Launcher.testChunck()#result: " + result.size());
		for (Integer integer : result) {
			System.out.println("Launcher.testChunck()#result: " + integer);
		}
	}
	
	private static String testReadJunk(String password) throws GeneralSecurityException, UnsupportedEncodingException {
		byte[] text = FileUtil.readFromFile2("D:/TEMP/test.txt");
//		String password = "PAssWORD";
		
		String decryptedText = SecurityUtil.decrypt(password, text);
		FileUtil.saveToFile("D:/TEMP/test2.txt", decryptedText);
		
		return decryptedText;
	}
	
	private static void readPasswordFromJunk(String text) throws GeneralSecurityException {
		String[] information = text.substring(text.indexOf(Project.MANIFEST.value()) + Project.MANIFEST.value().length(), text.length() - 1).split("\\$");
		Integer position = new Integer(information[0]);
		Integer size = new Integer(information[1]);
		
		System.out.println("Launcher.readPasswordFromJunk()#information: " + Arrays.asList(information));
		
		String password = text.substring(position, position + size);
		
		System.out.println("Launcher.testJunk()#"
				+ " position: " + position 
				+ ", text.size: " + text.length()
				+ ", password.size: " + size
				+ ", password: " + password
		);
		
	}
	
	private static byte[] testJunk(String password) throws GeneralSecurityException {
		System.out.println("Launcher.testJunk()");
		String text = RandomUtil.generateTrashSuperEasy();
		Integer position = RandomUtil.randomPosition(text);
		byte[] output = null;
		
		System.out.println("Launcher.testJunk()#"
				+ " position: " + position 
				+ ", text.size: " + text.length()
				+ ", password.size: " + password.length()
				+ ", new position: " + (position.toString().length() + password.length() + 2 + position)
		);
		
		String information = Project.MANIFEST.value() + position + "$" + password.length() + "#";
		
		text = text.substring(0, position) + password + text.substring(position);
		text = text + information;
		
		output = SecurityUtil.encrypt(password, text);
		FileUtil.saveToFile("D:/TEMP/test.txt", output);
		System.out.println("Launcher.testJunk()#done");
		
		return output;
	}
	
	private static void testLocation() throws Exception {
		URL location = Launcher.class.getProtectionDomain().getCodeSource().getLocation();
		File file = new File(location.getFile());
        System.out.println(file.getAbsolutePath());
        System.out.println(file.getParent());
	}
	
	private static void initialization() throws Exception {
		FileUtil.checkDirExist(FileUtil.currentPath() + Project.DATA.value());
	}
	
}
