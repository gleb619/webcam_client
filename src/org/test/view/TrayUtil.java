package org.test.view;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import org.test.config.Project;
import org.test.config.Settings;
import org.test.data.types.InformationStore;
import org.test.data.types.Logger;
import org.test.data.types.Tray;
import org.test.util.FileUtil;
import org.test.util.HeartbeatUtil;
import org.test.util.LoginUtil;
import org.test.worker.DetectWorker;

public class TrayUtil implements Runnable {

	private InformationStore informationStore;
	private DetectWorker detectWorker;
	private Logger logger;

	private TrayIcon icon;
	private Image image;
	private Image image_active;
	private Image image_error;

	public TrayUtil(InformationStore informationStore,
			DetectWorker detectWorker, Logger logger) {
		super();
		this.informationStore = informationStore;
		this.detectWorker = detectWorker;
		this.logger = logger;
		detectWorker.setTray(new TrayManipulator());
	}

	public DetectWorker getDetectWorker() {
		return detectWorker;
	}

	public void setDetectWorker(DetectWorker detectWorker) {
		this.detectWorker = detectWorker;
	}

	public TrayUtil start() {
		new Thread(this).start();
		return this;
	}
	
	public TrayUtil startSync() {
		try {
			init();
		} catch (AWTException e) {
			e.printStackTrace();
		}
		
		return this;
	}
	
	public TrayUtil stop() {
		
		return this;
	}
	
	@Override
	public void run() {
		try {
			init();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	private void init() throws AWTException {
		if (!SystemTray.isSupported()) {
			logger.error("TrayUtil.init()# " + "SystemTray is not supported");
			return;
		}

		SystemTray tray = SystemTray.getSystemTray();
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		
		try {
			if (Settings.DEBUG.getValue()) {
				image = toolkit.getImage("resources/camera.png");
				image_active = toolkit.getImage("resources/camera_active.png");
				image_error = toolkit.getImage("resources/error.png");
			} else {
				image = toolkit.createImage(this.getClass().getResource("/camera.png"));
				image_active = toolkit.createImage(this.getClass().getResource("/camera_active.png"));
				image_error = toolkit.createImage(this.getClass().getResource("/error.png"));
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		PopupMenu menu = new PopupMenu();
		
		MenuItem messageItem = new MenuItem("Login");
		messageItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loginDialog();
			}
		});
		menu.add(messageItem);
		
		messageItem = new MenuItem("Logout");
		messageItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("TrayUtil.init().new ActionListener() {...}.actionPerformed()#LOGOUT");
			}
		});
		menu.add(messageItem);
		
		menu.addSeparator();
		
		messageItem = new MenuItem("Pause");
		messageItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("TrayUtil.init().new ActionListener() {...}.actionPerformed()#LOGOUT");
			}
		});
		menu.add(messageItem);
		
		if (informationStore.getAllowDebug()) {
			menu.addSeparator();
			messageItem = new MenuItem("Open photos folder");
			messageItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					FileUtil.openFolderInExplorer(FileUtil.currentPath() + Project.PHOTOS.value());
				}
			});
			menu.add(messageItem);
			menu.addSeparator();
		}
		
		if (informationStore.getShowLogPane()) {
			messageItem = new MenuItem("Logs");
			messageItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					logger.getLogPane().start();
				}
			});
			menu.add(messageItem);
		}

		MenuItem closeItem = new MenuItem("Close");
		closeItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (detectWorker != null) {
					detectWorker.stop();
				}
				if (logger.getLogPane() != null) {
					logger.getLogPane().stop();
				}
				
				System.exit(0);
			}
		});
		menu.add(closeItem);
		icon = new TrayIcon(image, "WebCam client", menu);
		icon.setImageAutoSize(true);

		tray.add(icon);
		logger.log("TrayUtil.init()#done");
	}
	
	public Boolean loadKey() {
		try {
			LoginUtil.loadKey(informationStore);
			return true;
		} catch (NullPointerException e) {
			logger.error("KEY not found");
		}
		
		return false;
	}
	
	public void loginDialog() {
//		if (informationStore.getKey() == null) {
			LoginPane loginDlg = new LoginPane(informationStore, logger, new JFrame("")); 
			loginDlg.setVisible(true);
			
			if (informationStore.getAllowDebug()) {
				loginDlg.doLogin();
			}
			
			if(loginDlg.isSucceeded()){
	        	logger.log("Launcher.loginDialog()#success");
	        	getDetectWorker()
	        		.setInformationStore(informationStore)
	        		.setIsNetworkAvailable(HeartbeatUtil.heartbeat(informationStore))
	        		.init()
	        		.start();
	        }
			else{
				loginDlg.setTitle("ERROR");
				logger.log("Launcher.loginDialog()#failure");
			}
//		}
//		else {
//			logger.log("Load key from local storage");
//			getDetectWorker()
//				.setInformationStore(informationStore)
//	    		.setIsNetworkAvailable(HeartbeatUtil.heartbeat(informationStore))
//	    		.init()
//	    		.start();
//		}
		
	}

	private class TrayManipulator implements Tray {

		@Override
		public void error() {
			icon.setImage(image_error);
		}

		@Override
		public void activate() {
			icon.setImage(image_active);
		}

		@Override
		public void deactivate() {
			new Thread(() -> {
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}

				icon.setImage(image);
			}).start();
		}
	}
}
