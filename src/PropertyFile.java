import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@SuppressWarnings("ALL")
/* This Class Used for Extracting config.properties file and using its key, value pairs*/
public class PropertyFile {

    private static String path = "./config.properties";

    public static int getServerPort() throws IOException {
        Properties prop = new Properties();
        FileInputStream file;
        file = new FileInputStream(path);
        prop.load(file);
        file.close();
        int port = Integer.parseInt(prop.getProperty("server.port"));
        return port;
    }
    public static String getdbUserName() throws IOException {
        Properties prop = new Properties();
        FileInputStream file;
        file = new FileInputStream(path);
        prop.load(file);
        file.close();
        String dbUserName = prop.getProperty("db.username");
        return dbUserName;
    }
    public static String getdbPassword() throws IOException {
        Properties prop = new Properties();
        FileInputStream file;
        file = new FileInputStream(path);
        prop.load(file);
        file.close();
        String dbPassword = prop.getProperty("db.password");
        return dbPassword;
    }
    public static String getMySqlDriver() throws IOException {
        Properties prop = new Properties();
        FileInputStream file;
        file = new FileInputStream(path);
        prop.load(file);
        file.close();
        String mySqlDriver = prop.getProperty("mysql.driver.package");
        return mySqlDriver;
    }
    public static String getConnectionString() throws IOException {
        Properties prop = new Properties();
        FileInputStream file;
        file = new FileInputStream(path);
        prop.load(file);
        file.close();
        String connectionString = prop.getProperty("connection.string");
        return connectionString;
    }
    public static String getConnectionStrinWithoutDb() throws IOException {
        Properties prop = new Properties();
        FileInputStream file;
        file = new FileInputStream(path);
        prop.load(file);
        file.close();
        String connectionStringWithoutDb = prop.getProperty("connection.string.without.db");
        return connectionStringWithoutDb;
    }
}