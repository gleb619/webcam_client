package org.test.worker;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.test.converter.HttpResponseConverter;
import org.test.data.types.InformationStore;
import org.test.data.types.Logger;
import org.test.data.types.Tray;
import org.test.util.HeartbeatUtil;
import org.test.util.ImageUtil;
import org.test.util.RandomUtil;
import org.test.util.UploadUtil;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamMotionDetector;
import com.github.sarxos.webcam.WebcamPanel;

public class DetectWorker extends JFrame implements Runnable {

	private static final long serialVersionUID = 1782856718370603189L;
	
	private final Logger logger;
	private InformationStore informationStore;
	private Tray tray;
	
	private Timer timer;
	private TimerTask hourlyTask;
	private ExecutorService executor;
	
	private Webcam webcam = Webcam.getDefault();
	private Integer threshold = WebcamMotionDetector.DEFAULT_PIXEL_THREASHOLD;
	private Integer inertia = 10;

	private Thread updater;
	private WebcamMotionDetector detector;
	
	private Boolean isNetworkAvailable = true;
	private Boolean isInit = false;
	private List<String> picturesForSync = new ArrayList<String>();

	public DetectWorker(InformationStore informationStore, Logger logger)
			throws HeadlessException {
		super();
		this.informationStore = informationStore;
		this.logger = logger;
		this.executor = Executors.newWorkStealingPool(2);
	}

	public Tray getTray() {
		return tray;
	}

	public void setTray(Tray tray) {
		this.tray = tray;
	}

	public InformationStore getInformationStore() {
		return informationStore;
	}

	public DetectWorker setInformationStore(InformationStore informationStore) {
		this.informationStore = informationStore;
		return this;
	}

	public Boolean getIsNetworkAvailable() {
		return isNetworkAvailable;
	}

	public DetectWorker setIsNetworkAvailable(Boolean isNetworkAvailable) {
		this.isNetworkAvailable = isNetworkAvailable;
		return this;
	}

	public List<String> getPicturesForSync() {
		return picturesForSync;
	}

	public void setPicturesForSync(List<String> picturesForSync) {
		this.picturesForSync = picturesForSync;
	}

	public DetectWorker preStart() {
		updater = new Thread(this, "updater-thread");
		updater.setDaemon(true);
		
		return this;
	}
	
	public DetectWorker start() {
		updater.start();
		detector.start();
		isNetworkAvailable = HeartbeatUtil.heartbeat(informationStore);
		
		return this;
	}
	
	@Override
	public void run() {
		work();
		schedule();
	}
	
	public void stop() {
		if (hourlyTask != null) {
			try {
				hourlyTask.cancel();
			} catch (Exception e) {
				e.printStackTrace();
				tray.error();
			}
		}
		
		if (timer != null) {
			try {
				timer.cancel();
				timer.purge();
			} catch (Exception e) {
				e.printStackTrace();
				tray.error();
			}
		}
		
		if (informationStore.getShowPane()) {
			setVisible(false);
			dispose();
		}
		
		logger.log("MotionDetector.stop()");
	}
	
/* #----------------------- May 8, 2015 ------------------------ */
	
	public DetectWorker init() {
		logger.log("DetectWorker.init()#start at " + new Date());
		if(!isInit){
			
			Dimension dimension = new Dimension(informationStore.getCaptureWidth(), informationStore.getCaptureHeight());
			webcam.setCustomViewSizes(new Dimension[] { dimension });
			webcam.setViewSize(dimension);
	
			WebcamPanel panel = new WebcamPanel(webcam);
	
			detector = new WebcamMotionDetector(webcam, threshold, inertia);
			detector.setInterval(informationStore.getFrequency());
			
			if (informationStore.getShowPane()) {
				createVisual(panel);
			}
		}
		
		isInit = true;
		
		return this;
	}
	
	private void createVisual(WebcamPanel panel) {
		logger.log("DetectWorker.createVisual()#create pane: " + this);
		setTitle("Motion Detector");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new FlowLayout());
		
		add(panel);
		pack();
		setVisible(true);
		logger.log("DetectWorker.createVisual()#create pane done");
	}
	
	private void schedule() {
		logger.log("DetectWorker.schedule()#compare pictures with "
				+ informationStore.getFrequency() + " ms frequency ");
		timer = new Timer();
		hourlyTask = new TimerTask() {
			@Override
			public void run() {
				try {
					work();
				} catch (Exception e) {
					e.printStackTrace();
					tray.error();
				}
			}
		};
		timer.schedule(hourlyTask, 0l, informationStore.getFrequency());
	}

	private void work() {
		if (detector.isMotion()) {
			tray.activate();
			logger.log("DetectWorker.run()#" + "motion");
			String path = saveToFile();
			
			if (!isNetworkAvailable) {
				logger.error("Network is unreachable: " + informationStore.getHeartbeat() + " on " + informationStore.getHeartbeatPort() + " port");
				isNetworkAvailable = HeartbeatUtil.heartbeat(informationStore);
			}
			
			if (isNetworkAvailable) {
				upload(path);
			}
			
			tray.deactivate();
		} else {
			logger.debug("DetectWorker.run()#" + "notnig");
		}
	}

	private String saveToFile() {
		String name = informationStore.getPath() + RandomUtil.jpg();
		logger.log("DetectWorker.saveToFile()#Save file to: " + name);
		try {
			ImageUtil.compress(ImageUtil.convertToGray(webcam.getImage()), name);
			logger.debug("DetectWorker.saveToFile()#compress file");
		} catch (IOException e) {
			e.printStackTrace();
			tray.error();
		}
		
		return name;
	}
	
	private void upload(String path) {
		if (isNetworkAvailable) {
			String url = String.format(informationStore.getUrlTemplate() + informationStore.getUrlUpload(), informationStore.getKey());
			UploadUtil.upload(path, url, logger, informationStore.getAllowDebug(), executor);
			synchronizePictures();
		}
		else {
			picturesForSync.add(path);
			logger.error("MotionDetector.compare()#Network is unreachable, synchronization will be done when it is possible\n\t" + path);
		}
	}
	
	private void synchronizePictures() {
		List<Integer> idsForRemove = new ArrayList<Integer>();
		
		for (int index = 0; index < picturesForSync.size(); index++) {
			if (UploadUtil
					.upload(picturesForSync.get(index)
							, informationStore.getUrl() + informationStore.getUrlUpload()
							, logger, informationStore.getAllowDebug(), executor)
					.getCode() == 200) {
				idsForRemove.add(index);
			}
		}
		
		for (Integer index : idsForRemove) {
			picturesForSync.remove(index);
		}
	}
	

}