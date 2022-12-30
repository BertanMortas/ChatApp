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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTable;

public class ServerEnd {

	private JFrame frame;
	private JTextField textField;
	private static JTextArea textArea ;
	static ServerSocket server;
	static Socket con; //connection
	private static JLabel lblStatus;
	private JList JuserList ;
	private JLabel lblSelectedUser ;
	private JTable table;
	private DefaultTableModel tableModel;
	public JLabel lblDeneme;
	

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
		textField.setBounds(372, 192, 293, 64);
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
				GetMessagesFromDataBase();
				
			}
		});
		btnGonder.setBounds(677, 211, 117, 29);
		frame.getContentPane().add(btnGonder);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(372, 266, 393, 287);
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

				try {
				
					ClientEnd window1 = new ClientEnd();
					
					window1.clientConnection();
					window1.frame.setVisible(true);
					
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					System.out.println(e1.getMessage());
				}

			}
		});
		btnGorusme.setBounds(591, 20, 136, 29);
		frame.getContentPane().add(btnGorusme);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Chat Rewiev", TitledBorder.CENTER, TitledBorder.TOP, null, null));
		panel.setBounds(22, 192, 344, 361);
		frame.getContentPane().add(panel);
		panel.setLayout(null);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(6, 21, 332, 334);
		panel.add(scrollPane_1);
		
		table = new JTable();
		scrollPane_1.setViewportView(table);
		
		lblDeneme = new JLabel("Deneme");
		lblDeneme.setBounds(22, 62, 61, 16);
		frame.getContentPane().add(lblDeneme);
		
		
		
		GetMessagesFromDataBase();
		
		//getClient();
		
	}
	public void saveMessagetoDataBase() 
	{
		DataBase db = new DataBase(); // connection kısmı
		//	
		// hüseyin id 3
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
	public void GetMessagesFromDataBase() 
	{
		DataBase db = new DataBase();
		try {
			String sorgu= ("select m.messageId, m.message as mesagefromSever, us.userName as fromUser, u.userName as toUser\n"
					+ "from message as m\n"
					+ "inner join user as u\n"
					+ "on m.toUserId = u.userId\n"
					+ "inner join user as us\n"
					+ "on m.formUserId = us.userId");
			Statement st = db.connection.createStatement();
			st.executeQuery(sorgu);
			ResultSet rs = st.executeQuery(sorgu);
			tableModel = new DefaultTableModel();
			
			Object [] coloum = new Object[4];
			coloum[0] ="messageId";
			coloum[1] ="mesage from Sever";
			coloum[2] = "message from user name";
			coloum[3] = "message to user name";
			tableModel.setColumnIdentifiers(coloum);
			
			Object [] row = new Object[4];
			while(rs.next()) {
				row[0] = rs.getInt("messageId");
				row[1] = rs.getString("mesagefromSever");
				row[2] = rs.getString("fromUser");
				row[3] = rs.getString("toUser");
				tableModel.addRow(row);
			}
			table.setModel(tableModel);
		}catch(Exception e)
		{
			e.getMessage();
		}
	}
	public void getMessagesToTextField() { // bu kısım daha bitmedi
		DataBase db = new DataBase();
		textArea.setText("");
		try {
			PreparedStatement prestm = db.connection.prepareStatement("select m.messageId, m.message as mesagefromSever, us.userName as fromUser, u.userName as toUser\n"
					+ "from message as m\n"
					+ "inner join user as u\n"
					+ "on m.toUserId = u.userId\n"
					+ "inner join user as us\n"
					+ "on m.formUserId = us.userId\n"
					+ "where u.userId = ?");
			prestm.setInt(1, JuserList.getSelectedIndex());
			ResultSet rs = prestm.executeQuery();
			while(rs.next()) {
				String userName = rs.getString("fromUser"); // serverın yazdığı mesajlar
				textArea.setText(textArea.getText() + "\n"+userName+" : "+rs.getString("mesagefromSever"));
				System.out.println(textArea.getText() + "\n"+userName+" : "+rs.getString("mesagefromSever"));
			}
			
		} catch (Exception e) {
			e.toString();
		}
	}
	
}
