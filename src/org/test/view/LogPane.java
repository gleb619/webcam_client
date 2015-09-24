package org.test.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class LogPane implements Runnable {

	private JFrame frame;
	private JTextArea textArea;
	private String content = "";

	public LogPane() {
		super();
		
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public void start() {
		new Thread(this).start();
	}
	
	public void stop() {
		if (frame != null) {
			frame.setVisible(false);
			frame.dispose();
		}
	}
	
	@Override
	public void run() {
		init();
	}
	
	public LogPane init() {
		if (frame == null) {
			frame = new JFrame("Logs");
			
			textArea = new JTextArea(content);
	
			frame.setLayout(new BorderLayout());
			frame.add(new JScrollPane(textArea), BorderLayout.CENTER);
			
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			frame.setSize((int)(screenSize.getWidth() / 2), (int) (screenSize.getHeight() - 50));
			frame.setLocation(0, 0);
	//		frame.setLocationByPlatform(true);
	
//			frame.pack();
			frame.setVisible(true);
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		}

		return this;
	}
	
	public LogPane addText(String message) {
		content += "\n" + message;
		if (frame == null || !frame.isVisible()) {
			
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					if (textArea != null) {
//						textArea.setText(textArea.getText() + message);
						textArea.append("\n" + message);
//					    textArea.setCaretPosition(textArea.getDocument().getLength());
						// textArea.update(textArea.getGraphics());
					}
				}
			});
		}
		
		return this;
	}
	
}
