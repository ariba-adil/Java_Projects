package flightmanagementsystem;

import java.sql.*;

public class Conn {
    Connection c;
    Statement s;
	
    public Conn() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");  
            c =DriverManager.getConnection("jdbc:mysql:///flightmanagementsystem","root","Allah@786");    
            s =c.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error connecting to database:");
            e.printStackTrace();
        }
    }

    // Add other methods for database operations here

    public void close() {
        try {
            if (s != null) {
                s.close();
            }
            if (c != null) {
                c.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection:");
            e.printStackTrace();
        }
    }
}

