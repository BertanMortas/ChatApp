package Swing;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import dataBaseConnection.DataBase;
import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ServerEnd {

	private JFrame frame;
	private JTextField textField;
	private static JTextArea textArea ;
	static ServerSocket server;
	static Socket con; //connection
	private static JLabel lblStatus;
	private JList JuserList ;
	private JLabel lblSelectedUser ;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) throws IOException {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerEnd window = new ServerEnd();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		serverConnection();
	}

	private static void serverConnection() throws IOException {
		server = new ServerSocket(8080); // port tanımladık örn: mysql 3606 dan 
		con = server.accept();
		lblStatus.setText("Client bağlandı");
		while(true) {
			try
			{
				DataInputStream input = new DataInputStream(con.getInputStream());
				String string = input.readUTF();
				textArea.setText(textArea.getText() + "\n"+" Client: "+string);
				
			}catch(Exception ex) {
				
			}
		}
	}
	
	/**
	 * Create the application.
	 */
	public ServerEnd() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		lblStatus = new JLabel("Status:");
		lblStatus.setBounds(22, 16, 136, 34);
		frame.getContentPane().add(lblStatus);
		
		textField = new JTextField();
		textField.setBounds(255, 192, 351, 64);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		JButton btnGonder = new JButton("Gönder");
		btnGonder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//server button
				if(textField.getText().equals("")) {
					JOptionPane.showMessageDialog(null, "Lütfen mesaj yazınız");
				}
				else
				{
					textArea.setText(textArea.getText()+"\n"+ " Server: "+textField.getText());
					saveMessagetoDataBase();
					try {
						DataOutputStream output = new DataOutputStream(con.getOutputStream());
						output.writeUTF(textField.getText());
						textField.setText("");
					} catch (IOException ex) {
						try {
							Thread.sleep(2000);
							System.exit(0);
						}catch(InterruptedException ex2) {
							ex2.getMessage();
						}
						
					}
				}
				
			}
		});
		btnGonder.setBounds(677, 211, 117, 29);
		frame.getContentPane().add(btnGonder);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(255, 266, 510, 287);
		frame.getContentPane().add(scrollPane);
		
		textArea = new JTextArea();
		textArea.setFont(new Font("Comic Sans MS",Font.PLAIN,22));
		textArea.setForeground(new Color(255,255,255));
		textArea.setBackground(new Color(0,100,200));
		scrollPane.setViewportView(textArea);
		
		JuserList = new JList();
		JuserList.setModel(new AbstractListModel() {
			String[] values = new String[] {"Seciniz", "Server", "Client", "Hüseyin", "Hakan", "İbrahim", "Metin", "Hasan"};
			public int getSize() {
				return values.length;
			}
			public Object getElementAt(int index) {
				return values[index];
			}
		});
		JuserList.setBounds(204, 25, 117, 150);
		frame.getContentPane().add(JuserList);
		
		lblSelectedUser = new JLabel("Seçilen kişi:");
		lblSelectedUser.setBounds(372, 25, 117, 16);
		frame.getContentPane().add(lblSelectedUser);
		
		JButton btnGorusme = new JButton("Görüşme başlat");
		btnGorusme.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			//	Class c = Class.forName(null)
				
			}
		});
		btnGorusme.setBounds(591, 20, 136, 29);
		frame.getContentPane().add(btnGorusme);
		
	}
	public void saveMessagetoDataBase() 
	{
		DataBase db = new DataBase(); // connection kısmı
		//	
		
		try {
			PreparedStatement preparedStatement = db.connection.prepareStatement("insert into message (message,formUserId,toUserId) values(?,?,?)");
			preparedStatement.setString(1, textField.getText());
			preparedStatement.setInt(2, 1); // from user kısmı server 1
			lblSelectedUser.setText(Integer.toString(JuserList.getSelectedIndex()));
			preparedStatement.setInt(3, JuserList.getSelectedIndex()); // to user kısmı
			preparedStatement.executeUpdate();
			preparedStatement.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
