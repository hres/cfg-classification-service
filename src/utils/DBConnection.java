import java.io.*;
import java.util.Properties;
import java.sql.*;

class DBConnection {
    public static Connection getConnections() throws SQLException, Exception, IOException {

        Connection conn = null;

        Properties props = new Properties();
        FileInputStream in = new FileInputStream("../../resources/db.properties");
        props.load(in);
        in.close();

        String driver = props.getProperty("jdbc.driver");
        if (driver == null) {
            throw new Exception("jdbc.driver is not defined");
        }

        String url = props.getProperty("jdbc.url");
        String username = props.getProperty("jdbc.username");
        String password = props.getProperty("jdbc.password");

        conn = DriverManager.getConnection(url, username, password);
        return conn;
    }
}
