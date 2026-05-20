package server;

import java.sql.Connection;
import java.sql.DriverManager;

public class Database {

    public static Connection connect() {

        try {

            String url = "jdbc:mysql://localhost:3307/chatapp";
            String user = "nartse";
            String password = "Nartse@gmail.com";

            Connection conn = DriverManager.getConnection(url, user, password);

            System.out.println("Database Connected");
            return conn;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
