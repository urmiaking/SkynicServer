import java.io.IOException;
import java.sql.*;

@SuppressWarnings("ALL")
public class DBClass {

    private String id;
    private static String url;
    private static String user;
    private static String password;
    private static String connectionStringWithoutDb;
    private static String mySqlDriver;

    static {
        try {
            url = PropertyFile.getConnectionString();
            user = PropertyFile.getdbUserName();
            password = PropertyFile.getdbPassword();
            connectionStringWithoutDb = PropertyFile.getConnectionStrinWithoutDb();
            mySqlDriver = PropertyFile.getMySqlDriver();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DBClass(String id) {
        this.id = id;
    }

    public static void createDB() {
        try {
            Class.forName(mySqlDriver);
            Connection myConn = DriverManager.getConnection(connectionStringWithoutDb, user, password);
            Statement myStmt = myConn.createStatement();
            String sql = "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = 'my_scheme'";
            ResultSet rs = myStmt.executeQuery(sql);
            if (!rs.next()) {
                Statement createDBStmt = myConn.createStatement();
                String createDBSql = "CREATE DATABASE `my_scheme`;";
                createDBStmt.executeUpdate(createDBSql);
                System.out.println("Database Created Successfully");

                Statement createHubStmt = myConn.createStatement();
                String createHubSql =
                        "CREATE TABLE my_scheme.tbl_hub ("+
                        "id int(11) NOT NULL AUTO_INCREMENT," +
                        "serial varchar(45) NOT NULL," +
                        "isOnline int(11) DEFAULT NULL," +
                        "clientNumbers int(11) DEFAULT NULL," +
                        "clientName varchar(45) DEFAULT NULL," +
                        "phone varchar(15) DEFAULT NULL," +
                        "PRIMARY KEY (id)," +
                        "UNIQUE KEY serial_UNIQUE (serial)" +
                        ") ENGINE=InnoDB AUTO_INCREMENT=102 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;";
                createHubStmt.executeUpdate(createHubSql);
                System.out.println("tbl_hub Created Successfully");

                Statement createPhoneStmt = myConn.createStatement();
                String createPhoneSql =
                        "CREATE TABLE my_scheme.tbl_phone (" +
                        "  ipaddress varchar(45) NOT NULL," +
                        "  serial varchar(45) NOT NULL," +
                        "  password varchar(45) NOT NULL," +
                        "  time varchar(45) NOT NULL," +
                        "  PRIMARY KEY (ipaddress,serial)" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;";
                createPhoneStmt.executeUpdate(createPhoneSql);
                System.out.println("tbl_phone Created Successfully");

                Statement createUserStmt = myConn.createStatement();
                String createUserSql =
                        "CREATE TABLE my_scheme.tbl_user ( " +
                        "  id int(11) NOT NULL AUTO_INCREMENT," +
                        "  name varchar(80) NOT NULL," +
                        "  email varchar(45) NOT NULL," +
                        "  password varchar(200) NOT NULL," +
                        "  active varchar(10) NOT NULL," +
                        "  PRIMARY KEY (id)," +
                        "  UNIQUE KEY email_UNIQUE (email)" +
                        ") ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='for login and registereation';";
                createUserStmt.executeUpdate(createUserSql);
                System.out.println("tbl_user Created Successfully");

                String addAdminUser = " insert into my_scheme.tbl_user (name, email, password, active)"
                        + " values (?, ?, ?, ?)";

                PreparedStatement preparedStmt = myConn.prepareStatement(addAdminUser);
                preparedStmt.setString (1, "admin");
                preparedStmt.setString (2, "admin@admin.com");
                preparedStmt.setString (3, "admin");
                preparedStmt.setString (4, "true");
                preparedStmt.execute();
                myConn.close();
                System.out.println("admin User Created Successfully: email: admin@admin.com , password: admin");
            }
            else {
                myConn.close();
                System.out.println("Database Exists. Initiating Connection...");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean isRegistered() {
        try {
            Class.forName(mySqlDriver);
            Connection myConn = DriverManager.getConnection(url, user, password);
            Statement myStmt = myConn.createStatement();
            String sql = "Select * from my_scheme.tbl_hub where serial="+id;
            ResultSet rs = myStmt.executeQuery(sql);
            if (!rs.next()) {
                myConn.close();
                return false;
            }
            else {
                myConn.close();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setOnline() {
        try {
            try {
                Class.forName(mySqlDriver);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
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
            Class.forName(mySqlDriver);
            Connection myConn = DriverManager.getConnection(url, user, password);
            Statement myStmt = myConn.createStatement();
            String sql = "UPDATE my_scheme.tbl_hub SET isOnline=0 WHERE serial="+id;
            myStmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setClientNumbers(int number) {
        try {
            Class.forName(mySqlDriver);
            Connection myConn = DriverManager.getConnection(url, user, password);
            Statement myStmt = myConn.createStatement();
            String sql = "UPDATE my_scheme.tbl_hub SET clientNumbers="+number+" WHERE serial="+id;
            myStmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public int getClientNumbers() {
        try {
            Class.forName(mySqlDriver);
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
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void addOrUpdatePhone(String ipAddress, String passcode, String serial, String time) {
        try {
            Class.forName(mySqlDriver);
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
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void purgeHubs() {
        try {
            Class.forName(mySqlDriver);
            Connection myConn = DriverManager.getConnection(url, user, password);
            Statement myStmt = myConn.createStatement();
            String sql = "UPDATE my_scheme.tbl_hub SET isOnline = 0, clientNumbers = 0";
            myStmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void updatePassword(String ipAddress, String newPassword, String serial) {
        try {
            Class.forName(mySqlDriver);
            Connection myConn = DriverManager.getConnection(url, user, password);
            Statement myStmt = myConn.createStatement();
            String sql = "SELECT * from my_scheme.tbl_phone where ipaddress='" + ipAddress + "' AND serial='" + serial + "'";
            ResultSet result = myStmt.executeQuery(sql);
            if (result.next()) {
                Statement myStmtUpdate = myConn.createStatement();
                String sqlUpdate = "UPDATE my_scheme.tbl_phone SET password='" + newPassword + "' WHERE ipaddress='" + ipAddress + "' AND serial='" + serial + "'";
                myStmtUpdate.executeUpdate(sqlUpdate);
                myConn.close();
            } else {
                System.out.println("Update Password Failed");
                myConn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}