package Swing;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import dataBaseConnection.DataBase;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.AbstractListModel;

public class ClientEnd {

	public JFrame frame;
	private JTextField textField;
	private static JTextArea textArea;
	DataInputStream input;
	DataOutputStream output;
	private static Socket con;
	private JLabel lblNewLabel;
	private JList JuserList ;
	private JLabel lblSelectedUser ;
	
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) throws IOException {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientEnd window = new ClientEnd();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		clientConnection();
	}

	public static void clientConnection() throws IOException{
		con = new Socket("127.0.0.1",8080); // burada konuştuğumuzu için bizim makinan ip sini vrdik
		while (true) {
			try {
				DataInputStream input = new DataInputStream(con.getInputStream());
				String string = input.readUTF(); // ASCİİ
				textArea.setText(textArea.getText() + "\n"+" Server: "+string);
			} catch(Exception ex) {
				
			}
		}
	}
	
	
	/**
	 * Create the application.
	 */
	public ClientEnd() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	public void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(251, 52, 353, 67);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		JButton btnGonder = new JButton("Gönder");
		btnGonder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//client button
				if(textField.getText().equals("")) {
					JOptionPane.showMessageDialog(null, "Lütfen mesaj yazınız");
				}
				else
				{
					textArea.setText(textArea.getText()+"\n"+ " Client: "+textField.getText());
				//	lblSelectedUser.setText(Integer.toString(JuserList.getSelectedIndex()));
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
		btnGonder.setBounds(616, 72, 117, 29);
		frame.getContentPane().add(btnGonder);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(220, 151, 513, 285);
		frame.getContentPane().add(scrollPane);
		
		textArea = new JTextArea();
		textArea.setFont(new Font("Comic Sans MS",Font.PLAIN,22));
		textArea.setForeground(new Color(255,255,255));
		textArea.setBackground(new Color(100,100,200));
		scrollPane.setViewportView(textArea);
		
		lblNewLabel = new JLabel("Kullanıcılar :");
		lblNewLabel.setBounds(6, 52, 110, 16);
		frame.getContentPane().add(lblNewLabel);
		
		JuserList = new JList();
		JuserList.setModel(new AbstractListModel() {
			String[] values = new String[] {"Seciniz", "Server", "Mehmet", "Hüseyin", "Hakan", "İbrahim", "Metin", "Hasan"};
			public int getSize() {
				return values.length;
			}
			public Object getElementAt(int index) {
				return values[index];
			}
		});
		JuserList.setBounds(33, 152, 117, 150);
		frame.getContentPane().add(JuserList);
		
		lblSelectedUser = new JLabel("Seçilen kişi:");
		lblSelectedUser.setBounds(33, 340, 117, 16);
		frame.getContentPane().add(lblSelectedUser);
	}
	// main dışı metodlar
	
	public void saveMessagetoDataBase() 
	{
		DataBase db = new DataBase(); // connection kısmı
		//	
		
		try {
			PreparedStatement preparedStatement = db.connection.prepareStatement("insert into message (message,formUserId,toUserId) values(?,?,?)");
			preparedStatement.setString(1, textField.getText());
			preparedStatement.setInt(2, 2); // from user kısmı
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
