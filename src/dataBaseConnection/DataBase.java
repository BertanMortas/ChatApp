package dataBaseConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBase 

	{
    //hocanin gonderdigi url
    //jdbc:mysql://localhost/bilgeadam_uygulama?useUnicode=true&useLegacyDatetimeCode=false&serverTimezone=Turkey&useSSL=false";
    private final String url="jdbc:mysql://localhost:3306/chatApp?useSSL=false";
    private final String userName="root";
    private final String userPass="Bm15481527";
    private final String driver ="com.mysql.cj.jdbc.Driver";
    public Connection connection = null;
    public DataBase() {
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url,userName,userPass);
            if (!connection.isClosed()) {
                System.out.println("Connection Success");
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

