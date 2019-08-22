import java.sql.*;

@SuppressWarnings("ALL")
public class DBClass {

    private String id;
    private static String url = "jdbc:mysql://localhost:3306/my_scheme?useSSL=false";
    private static String user = "root";
    private static String password = "root";

    public DBClass(String id) {
        this.id = id;
    }

    public static void createDB() {
        try {
            Connection myConn = DriverManager.getConnection(url, user, password);
            Statement myStmt = myConn.createStatement();
            String sql = "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = 'my_scheme'";
            ResultSet rs = myStmt.executeQuery(sql);
            if (!rs.next()) {
                Statement createStmt = myConn.createStatement();
                String createSql = "CREATE DATABASE `my_db`;" +
                        "" +
                        "CREATE TABLE `tbl_hub` (" +
                        "  `id` int(11) NOT NULL AUTO_INCREMENT," +
                        "  `serial` varchar(45) NOT NULL," +
                        "  `isOnline` int(11) DEFAULT NULL," +
                        "  `clientNumbers` int(11) DEFAULT NULL," +
                        "  `clientName` varchar(45) DEFAULT NULL," +
                        "  `phone` varchar(15) DEFAULT NULL," +
                        "  PRIMARY KEY (`id`)," +
                        "  UNIQUE KEY `serial_UNIQUE` (`serial`)" +
                        ") ENGINE=InnoDB AUTO_INCREMENT=102 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;" +
                        "" +
                        "CREATE TABLE `tbl_phone` (" +
                        "  `ipaddress` varchar(45) NOT NULL," +
                        "  `serial` varchar(45) NOT NULL," +
                        "  `password` varchar(45) NOT NULL," +
                        "  `time` varchar(45) NOT NULL," +
                        "  PRIMARY KEY (`ipaddress`,`serial`)" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;";
                createStmt.executeUpdate(createSql);
                System.out.println("Database Created Successfully");
            }
            else {
                System.out.println("Database Exists. Initiating Connection...");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isRegistered() {
        try {
            Connection myConn = DriverManager.getConnection(url, user, password);
            Statement myStmt = myConn.createStatement();
            String sql = "Select * from my_scheme.tbl_hub where serial="+id;
            ResultSet rs = myStmt.executeQuery(sql);
            if (!rs.next()) {
                return false;
            }
            else {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setOnline() {
        try {
            Connection myConn = DriverManager.getConnection(url, user, password);
            Statement myStmt = myConn.createStatement();
            String sql = "UPDATE my_scheme.tbl_hub SET isOnline=1 WHERE serial="+id;
            myStmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setOffline() {
        try {
            Connection myConn = DriverManager.getConnection(url, user, password);
            Statement myStmt = myConn.createStatement();
            String sql = "UPDATE my_scheme.tbl_hub SET isOnline=0 WHERE serial="+id;
            myStmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setClientNumbers(int number) {
        try {
            Connection myConn = DriverManager.getConnection(url, user, password);
            Statement myStmt = myConn.createStatement();
            String sql = "UPDATE my_scheme.tbl_hub SET clientNumbers="+number+" WHERE serial="+id;
            myStmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getClientNumbers() {
        try {
            int serial = 0;
            Connection myConn = DriverManager.getConnection(url, user, password);
            Statement myStmt = myConn.createStatement();
            String sql = "SELECT clientNumbers from my_scheme.tbl_hub where serial="+id;
            ResultSet result = myStmt.executeQuery(sql);
            while (result.next()) {
                serial = result.getInt("clientNumbers");
            }
            return serial;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void addOrUpdatePhone(String ipAddress, String passcode, String serial, String time) {
        try {
            Connection myConn = DriverManager.getConnection(url, user, password);
            Statement myStmt = myConn.createStatement();
            String sql = "SELECT * from my_scheme.tbl_phone where ipaddress='" + ipAddress + "' AND serial='" + serial + "'";
            ResultSet result = myStmt.executeQuery(sql);
            if (result.next()) {
                Statement myStmtUpdate = myConn.createStatement();
                String sqlUpdate = "UPDATE my_scheme.tbl_phone SET time='" + time + "' WHERE ipaddress='" + ipAddress + "'";
                myStmtUpdate.executeUpdate(sqlUpdate);
                myConn.close();
            } else {
                String query = " insert into my_scheme.tbl_phone (ipaddress, serial, password, time)"
                        + " values (?, ?, ?, ?)";

                PreparedStatement preparedStmt = myConn.prepareStatement(query);
                preparedStmt.setString (1, ipAddress);
                preparedStmt.setString (2, serial);
                preparedStmt.setString (3, passcode);
                preparedStmt.setString (4, time);
                preparedStmt.execute();
                myConn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void purgeHubs() {
        try {
            Connection myConn = DriverManager.getConnection(url, user, password);
            Statement myStmt = myConn.createStatement();
            String sql = "UPDATE my_scheme.tbl_hub SET isOnline = 0, clientNumbers = 0";
            myStmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}