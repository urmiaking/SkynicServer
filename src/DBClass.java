import com.sun.istack.internal.Nullable;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;

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
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
                createHubStmt.executeUpdate(createHubSql);
                System.out.println("tbl_hub Created Successfully");

                Statement createPhoneStmt = myConn.createStatement();
                String createPhoneSql =
                        "CREATE TABLE my_scheme.tbl_phone (" +
                        "  ipaddress varchar(45) NOT NULL," +
                        "  serial varchar(45) NOT NULL," +
                        "  password varchar(45) NOT NULL," +
                        "  PRIMARY KEY (ipaddress,serial)" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
                createPhoneStmt.executeUpdate(createPhoneSql);
                System.out.println("tbl_phone Created Successfully");

                Statement createLogStmt = myConn.createStatement();
                String createLogSql =
                        "CREATE TABLE my_scheme.tbl_log (" +
                                "  id int(11) NOT NULL AUTO_INCREMENT," +
                                "  ipaddress varchar(45) NOT NULL," +
                                "  serial varchar(45) DEFAULT NULL," +
                                "  password varchar(45) DEFAULT NULL," +
                                "  time varchar(45) DEFAULT NULL," +
                                "  status varchar(45) DEFAULT NULL," +
                                "  description varchar(200) DEFAULT NULL,"+
                                "  PRIMARY KEY (id)" +
                                ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
                createLogStmt.executeUpdate(createLogSql);
                System.out.println("tbl_log Created Successfully");

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
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='for login and registereation';";
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
                System.out.println("Admin User Created Successfully: email: admin@admin.com , password: admin");
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
            String sql = "Select * from my_scheme.tbl_hub where serial='" + id + "'";
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
            String sql = "UPDATE my_scheme.tbl_hub SET isOnline=1 WHERE serial='" + id + "'";
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
            String sql = "UPDATE my_scheme.tbl_hub SET isOnline=0 WHERE serial='" + id + "'";
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
            String sql = "UPDATE my_scheme.tbl_hub SET clientNumbers="+number+" WHERE serial='" + id + "'";
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
            String sql = "SELECT clientNumbers from my_scheme.tbl_hub where serial='" + id + "'";
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

    public void addPhone(String ipAddress, String passcode, String serial) {
        try {
            Class.forName(mySqlDriver);
            Connection myConn = DriverManager.getConnection(url, user, password);
            Statement myStmt = myConn.createStatement();
            String sql = "SELECT * from my_scheme.tbl_phone where ipaddress='" + ipAddress + "' AND serial='" + serial + "'";
            ResultSet result = myStmt.executeQuery(sql);
            if (!result.next()) {
                String query = " insert into my_scheme.tbl_phone (ipaddress, serial, password)"
                        + " values (?, ?, ?)";
                PreparedStatement preparedStmt = myConn.prepareStatement(query);
                preparedStmt.setString (1, ipAddress);
                preparedStmt.setString (2, serial);
                preparedStmt.setString (3, passcode);
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

    public static void logFirstTimeOut(String ipAddress) {
        try {
            Class.forName(mySqlDriver);
            Connection myConn = DriverManager.getConnection(url, user, password);
            String timeDate = LocalDateTime.now().toString();
            String query = " insert into my_scheme.tbl_log (ipaddress, status, description, time)"
                    + " values (?, ?, ?, ?)";
            PreparedStatement preparedStmt = myConn.prepareStatement(query);
            preparedStmt.setString (1, ipAddress);
            preparedStmt.setString (2, "Disconnected - First TimeOut");
            preparedStmt.setString (3, "Socket Closed Due To Not Sending His Identity And Got Into TimeOut or Disconnected By Himself Before TimeOut Arrives");
            preparedStmt.setString (4, timeDate);
            preparedStmt.execute();
            myConn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void logHubAlreadyOnline(String ipAddress, String serial) {
        try {
            Class.forName(mySqlDriver);
            Connection myConn = DriverManager.getConnection(url, user, password);

            String timeDate = LocalDateTime.now().toString();
            String query = " insert into my_scheme.tbl_log (ipaddress, serial, status, description, time)"
                    + " values (?, ?, ?, ?, ?)";
            PreparedStatement preparedStmt = myConn.prepareStatement(query);
            preparedStmt.setString (1, ipAddress);
            preparedStmt.setString (2, serial);
            preparedStmt.setString (3, "Disconnected - Hub Already Online");
            preparedStmt.setString (4, "Socket Closed Because It tried to connect as a hub which is already online");
            preparedStmt.setString (5, timeDate);
            preparedStmt.execute();
            myConn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void logHubNotAvailable(String ipAddress, String serial) {
        try {
            Class.forName(mySqlDriver);
            Connection myConn = DriverManager.getConnection(url, user, password);

            String timeDate = LocalDateTime.now().toString();
            String query = " insert into my_scheme.tbl_log (ipaddress, serial, status, description, time)"
                    + " values (?, ?, ?, ?, ?)";
            PreparedStatement preparedStmt = myConn.prepareStatement(query);
            preparedStmt.setString (1, ipAddress);
            preparedStmt.setString (2, serial);
            preparedStmt.setString (3, "Disconnected - Hub Serial is Invalid");
            preparedStmt.setString (4, "Socket Closed Because It tried to connect as a hub which is not available in Database");
            preparedStmt.setString (5, timeDate);
            preparedStmt.execute();
            myConn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void logSecondTimeOut(String ipAddress, String serial) {
        try {
            Class.forName(mySqlDriver);
            Connection myConn = DriverManager.getConnection(url, user, password);

            String timeDate = LocalDateTime.now().toString();
            String query = " insert into my_scheme.tbl_log (ipaddress, serial, status, description, time)"
                    + " values (?, ?, ?, ?, ?)";
            PreparedStatement preparedStmt = myConn.prepareStatement(query);
            preparedStmt.setString (1, ipAddress);
            preparedStmt.setString (2, serial);
            preparedStmt.setString (3, "Disconnected - Second TimeOut");
            preparedStmt.setString (4, "Socket Closed Because It Does not send password and timed out or it closed it socket before entering password");
            preparedStmt.setString (5, timeDate);
            preparedStmt.execute();
            myConn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void logInvalidCommand(String ipAddress, String serial) {
        try {
            Class.forName(mySqlDriver);
            Connection myConn = DriverManager.getConnection(url, user, password);

            String timeDate = LocalDateTime.now().toString();
            String query = " insert into my_scheme.tbl_log (ipaddress, serial, status, description, time)"
                    + " values (?, ?, ?, ?, ?)";
            PreparedStatement preparedStmt = myConn.prepareStatement(query);
            preparedStmt.setString (1, ipAddress);
            preparedStmt.setString (2, serial);
            preparedStmt.setString (3, "Disconnected - Invalid Command");
            preparedStmt.setString (4, "Socket Closed Because It Send invalid command");
            preparedStmt.setString (5, timeDate);
            preparedStmt.execute();
            myConn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void logHubIsOffline(String ipAddress, String serial) {
        try {
            Class.forName(mySqlDriver);
            Connection myConn = DriverManager.getConnection(url, user, password);

            String timeDate = LocalDateTime.now().toString();
            String query = " insert into my_scheme.tbl_log (ipaddress, serial, status, description, time)"
                    + " values (?, ?, ?, ?, ?)";
            PreparedStatement preparedStmt = myConn.prepareStatement(query);
            preparedStmt.setString (1, ipAddress);
            preparedStmt.setString (2, serial);
            preparedStmt.setString (3, "Disconnected - Hub Offline");
            preparedStmt.setString (4, "Socket Closed Because It Tries to connect to a hub which is offline");
            preparedStmt.setString (5, timeDate);
            preparedStmt.execute();
            myConn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void logRequestedHubNotAvailable(String ipAddress, String serial) {
        try {
            Class.forName(mySqlDriver);
            Connection myConn = DriverManager.getConnection(url, user, password);

            String timeDate = LocalDateTime.now().toString();
            String query = " insert into my_scheme.tbl_log (ipaddress, serial, status, description, time)"
                    + " values (?, ?, ?, ?, ?)";
            PreparedStatement preparedStmt = myConn.prepareStatement(query);
            preparedStmt.setString (1, ipAddress);
            preparedStmt.setString (2, serial);
            preparedStmt.setString (3, "Disconnected - Hub Serial is Invalid");
            preparedStmt.setString (4, "Socket Closed Because It tried to connect to a hub which is not available in Database");
            preparedStmt.setString (5, timeDate);
            preparedStmt.execute();
            myConn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void logPhoneConnected(String ipAddress, String passCode, String serial) {
        try {
            Class.forName(mySqlDriver);
            Connection myConn = DriverManager.getConnection(url, user, password);

            String timeDate = LocalDateTime.now().toString();
            String query = " insert into my_scheme.tbl_log (ipaddress, serial, status, description, time, password)"
                    + " values (?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStmt = myConn.prepareStatement(query);
            preparedStmt.setString (1, ipAddress);
            preparedStmt.setString (2, serial);
            preparedStmt.setString (3, "Connected");
            preparedStmt.setString (4, "Socket Phone Successfully Connected to Hub");
            preparedStmt.setString (5, timeDate);
            preparedStmt.setString (6, passCode);
            preparedStmt.execute();
            myConn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void logSocketPhoneClosed(String ipAddress, String serial, String pass) {
        try {
            Class.forName(mySqlDriver);
            Connection myConn = DriverManager.getConnection(url, user, password);

            String timeDate = LocalDateTime.now().toString();
            String query = " insert into my_scheme.tbl_log (ipaddress, serial, status, description, time, password)"
                    + " values (?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStmt = myConn.prepareStatement(query);
            preparedStmt.setString (1, ipAddress);
            preparedStmt.setString (2, serial);
            preparedStmt.setString (3, "Disconnected - Socket Phone Disconnected");
            preparedStmt.setString (4, "Socket Phone Disconnected with any reason (e.g. by itself, by hub, by connection failure, pass error etc)");
            preparedStmt.setString (5, timeDate);
            preparedStmt.setString (6, pass);
            preparedStmt.execute();
            myConn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void logPasswordUpdated(String ipAddress, String serial, String newPassword) {
        try {
            Class.forName(mySqlDriver);
            Connection myConn = DriverManager.getConnection(url, user, password);

            String timeDate = LocalDateTime.now().toString();
            String query = " insert into my_scheme.tbl_log (ipaddress, serial, status, description, time, password)"
                    + " values (?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStmt = myConn.prepareStatement(query);
            preparedStmt.setString (1, ipAddress);
            preparedStmt.setString (2, serial);
            preparedStmt.setString (3, "Connected - Password Changed");
            preparedStmt.setString (4, "Socket Phone Successfully Changed Hub "+ serial +" Password");
            preparedStmt.setString (5, timeDate);
            preparedStmt.setString (6, newPassword);
            preparedStmt.execute();
            myConn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void logSocketHubClosed(String ipAddress, String serial, @Nullable String pass) {
        try {
            Class.forName(mySqlDriver);
            Connection myConn = DriverManager.getConnection(url, user, password);

            String timeDate = LocalDateTime.now().toString();
            String query = " insert into my_scheme.tbl_log (ipaddress, serial, status, description, time, password)"
                    + " values (?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStmt = myConn.prepareStatement(query);
            preparedStmt.setString (1, ipAddress);
            preparedStmt.setString (2, serial);
            preparedStmt.setString (3, "Disconnected - Socket HUB Disconnected");

            preparedStmt.setString (5, timeDate);
            if (pass == null) {
                preparedStmt.setString(6, "unknown");
                preparedStmt.setString (4, "Socket HUB Disconnected  by itself or by connection failure and its password is unknown because no phone connected to it");
            } else {
                preparedStmt.setString (6, pass);
                preparedStmt.setString (4, "Socket HUB Disconnected  by itself or by connection failure");
            }

            preparedStmt.execute();
            myConn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void logPassError(String ipAddress, String serial, String pass) {
        try {
            Class.forName(mySqlDriver);
            Connection myConn = DriverManager.getConnection(url, user, password);

            String timeDate = LocalDateTime.now().toString();
            String query = " insert into my_scheme.tbl_log (ipaddress, serial, status, description, time, password)"
                    + " values (?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStmt = myConn.prepareStatement(query);
            preparedStmt.setString (1, ipAddress);
            preparedStmt.setString (2, serial);
            preparedStmt.setString (3, "Disconnected - invalid password");
            preparedStmt.setString (4, "Socket Phone Sent Invalid Password");
            preparedStmt.setString (5, timeDate);
            preparedStmt.setString (6, pass);
            preparedStmt.execute();
            myConn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}