import com.sun.org.apache.xerces.internal.impl.dv.xs.DateTimeDV;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;

@SuppressWarnings("ALL")
public class Server {
    public static void main(String[] args) {
        try {
            DBClass.purgeHubs();
            ServerSocket serverSocket = new ServerSocket(9898);
            System.out.println("Server Running!");
            while (true) {
                Socket socket = serverSocket.accept();
                socket.setSoTimeout(20*1000); // The Connected Socket Must Send Anything Within 20 Seconds Otherwise it will be closed.
                Thread t = new Thread(() -> ClientManager.getInstance().run(socket));
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}