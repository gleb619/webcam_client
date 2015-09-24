package org.test.util;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.test.data.types.InformationStore;

public class HeartbeatUtil implements Runnable {

	private final String url;
	private final Integer port;
	private Boolean alive = false;

	public HeartbeatUtil(String url, Integer port) {
		super();
		this.url = url;
		this.port = port;
	}

	public Boolean getAlive() {
		return alive;
	}

	public void setAlive(Boolean alive) {
		this.alive = alive;
	}

	@Override
	public void run() {
		try {
			init();
		} catch (IOException e) {
//			System.err.println("HeartbeatUtil.init()#ERROR: " + e. getMessage());
		}
	}
	
	public Thread work() {
		Thread thread = new Thread(this);
		
		return thread;
	}

	private void init() throws UnknownHostException, IOException {
		Socket socket = null;
		try {
			socket = new Socket(url, port);
			alive = true;
		} finally {
			if (socket != null)
				try {
					socket.close();
				} catch (IOException e) {
					
				}
		}
	}

	public static Boolean heartbeat(String heartbeat, Integer heartbeatPort) {
		HeartbeatUtil heartbeatUtil = new HeartbeatUtil(heartbeat, heartbeatPort);
		Thread thread = heartbeatUtil.work();
		thread.start();
		
		while (thread.isAlive()) {
			
		}
		
		return heartbeatUtil.getAlive();
	}
	
	public static Boolean heartbeat(InformationStore informationStore) {
		return heartbeat(informationStore.getHeartbeat(), informationStore.getHeartbeatPort());
	}
	
}
