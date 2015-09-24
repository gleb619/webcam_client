package org.test.view;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import org.test.data.types.InformationStore;
import org.test.data.types.Logger;
import org.test.util.LoginUtil;

public class LoginPane extends JDialog {

	private final InformationStore informationStore;
	private final Logger logger;
	
	private static final long serialVersionUID = -433962183367844610L;
	private JTextField tfUsername;
	private JPasswordField pfPassword;
	private JLabel lbUsername;
	private JLabel lbPassword;
	private JButton btnLogin;
	private JButton btnCancel;
	private Boolean succeeded = false;
	
	private Integer currentX = -1; 
	private Integer currentY = -1;
	private GridBagConstraints cs; 

	public LoginPane(InformationStore informationStore, Logger logger, Frame parent) {
		super(parent, "Login", true);
		this.informationStore = informationStore;
		this.logger = logger;
		init(parent);
	}

	private void init(Frame parent) {
		JPanel panel = new JPanel(new GridBagLayout());
		cs = new GridBagConstraints();

		cs.fill = GridBagConstraints.HORIZONTAL;

		addMargin(panel);
		lbUsername = new JLabel("Username: ");
		addRow().addCell();
		panel.add(lbUsername, cs);

		addCell();
		tfUsername = new JTextField(20);
		panel.add(tfUsername, cs);
/* #----------------------- May 8, 2015 ------------------------ */
		addMargin(panel);
/* #----------------------- May 8, 2015 ------------------------ */
		lbPassword = new JLabel("Password: ");
		addRow().addCell();
		panel.add(lbPassword, cs);

		pfPassword = new JPasswordField(20);
		addCell();
		panel.add(pfPassword, cs);
		addMargin(panel);
		
		panel.setBorder(new LineBorder(Color.GRAY));

		btnLogin = new JButton("Login");

		pfPassword.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doLogin();
				
			}
		});
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doLogin();
			}
		});
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		
		JPanel bp = new JPanel();
		bp.add(btnLogin);
		bp.add(btnCancel);

		getContentPane().add(panel, BorderLayout.CENTER);
		getContentPane().add(bp, BorderLayout.PAGE_END);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setResizable(false);
		setLocationRelativeTo(parent);
//		setVisible(true);
	}

	public void doLogin() {
		Boolean result = false;
		
		try {
			result = LoginUtil.login(tfUsername.getText(), new String(pfPassword.getPassword()), informationStore, logger);
			System.out.println("LoginPane.doLogin()#result: " + result);
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		if (result) {
			logger.log("LoginPane.LoginUtil2(...).new ActionListener() {...}.actionPerformed()#allow aceess");
			succeeded = true;
			setVisible(false);
			dispose();
		} else {
			logger.log("LoginPane.LoginUtil2(...).new ActionListener() {...}.actionPerformed()#disallow aceess");
			succeeded = false;
			tfUsername.setText("");
			pfPassword.setText("");
		}
	}
	
	private void addMargin(JPanel panel) {
		JLabel label = new JLabel();
		addRow().addCell();
		label.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
		panel.add(label, cs);
		
		label = new JLabel();
		addCell();
		label.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
		panel.add(label, cs);
	}

	private LoginPane addCell() {
		cs.gridx = currentX++;
		cs.gridy = currentY;
		cs.gridwidth = 1;
		
		if (currentX == 2) {
			currentX = 0;
		}
		
		return this;
	}
	
	private LoginPane addRow() {
		currentX = 0;
		
		cs.gridx = currentX;
		cs.gridy = currentY++;
		cs.gridwidth = 1;
		
		return this;
	}
	
	public String getUsername() {
		return tfUsername.getText().trim();
	}

	public String getPassword() {
		return new String(pfPassword.getPassword());
	}

	public Boolean isSucceeded() {
		return succeeded;
	}
}
