import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

@SuppressWarnings("ALL")
public class  ClientManager {

    public ArrayList<SocketHub> socketHubs = new ArrayList<>();
    public DBClass dbClass;

    private static ClientManager instance;

    public static ClientManager getInstance() {
        if (instance == null) {
            instance = new ClientManager();
        }
        return instance;
    }


    public void run(Socket socket) {
        String identity = null;
        String id = null;
        try {
            identity = readLine(socket);
            if (identity == null) {
                socket.close();
                System.out.println("Socket " + socket.getPort() + " Closed");
                System.out.println("=====================================");
                return;
            }
            else {
                String[] idAndIdentity = identity.split("=");
                if (!(idAndIdentity.length < 2 || idAndIdentity.length > 2)) {
                    identity = idAndIdentity[0];
                    id = idAndIdentity[1];
                    System.out.println("Identity & ID Received from Socket " + socket.getPort() + ": " + identity + " " + id);
                }
            }
            dbClass = new DBClass(id);
            switch (identity) {
                case "AT+HUB":
                    if (findSocketHub(id) != null) {
                        System.out.println("The Hub is Already Connected!");
                        socket.getOutputStream().write("You are already Connected!\r\n".getBytes());
                        socket.close();
                    }
                    else {
                        if (!dbClass.isRegistered()) {
                            System.out.println("This Device is not Registered!");
                            socket.getOutputStream().write("Your Device is not Registered!\r\n".getBytes());
                            socket.close();
                        }
                        else {
                            socket.setSoTimeout(0);
                            SocketHub socketHub = new SocketHub(socket, id);
                            socketHubs.add(socketHub);
                            Thread t = new Thread(socketHub);
                            t.start();
                            dbClass.setOnline();
                            System.out.println(t.getName() + " for Hub Socket " + socket.getPort() + " Created!");
                            socket.getOutputStream().write("CONNECT OK\r\n".getBytes());
                        }
                    }
                    break;
                case "AT+PHONE":
                    SocketHub relativeSocketHub = findSocketHub(id);
                    socket.setSoTimeout(0);
                    SocketPhone socketPhone = new SocketPhone(socket, relativeSocketHub);
                    if (relativeSocketHub != null) {
                        relativeSocketHub.setSocketPhone(socketPhone);
                        socketPhone.getSocket().getOutputStream().write("ENTER PASS\r\n".getBytes());
                        socketPhone.getSocket().setSoTimeout(60*1000); //If Client Don't Send Pass for a minute, an exception will be raised and Thread will be closed
                        String passSentence = socketPhone.readLine();
                        String[] passPhrase = passSentence.split("=");
                        if (passPhrase[0].equals("AT+PASS")) {
                            if (!(passPhrase.length < 2 || passPhrase.length > 2)) {

                                String passcode = passPhrase[1];
                                String ipAddress = socket.getInetAddress().toString();
                                socketPhone.setPass(passcode);
                                socketPhone.setSerial(id);

                                relativeSocketHub.write(passSentence + "\r\n", socketPhone);
                                System.out.println("Message From Phone "+ socket.getPort() + " To Hub "+relativeSocketHub.getSocketHub().getPort()+" :" + passSentence);
                                socketPhone.getSocket().setSoTimeout(0);
                                relativeSocketHub.addSocketPhonesArrays(socketPhone);
                                Thread t = new Thread(socketPhone);
                                dbClass.setClientNumbers(relativeSocketHub.socketPhonesArrays.size());
                                t.start();
                                socket.getOutputStream().write("CONNECT OK\r\n".getBytes());
                                System.out.println(t.getName() + " for Phone Socket " + socket.getPort() + " Created!");
                            } else {
                                socket.getOutputStream().write("Incomplete Password Command. Socket is Closing...\r\n".getBytes());
                                System.out.println("Incomplete Password Command");
                                socket.close();
                            }
                        } else {
                            socketPhone.getSocket().getOutputStream().write("Wrong Command. Socket is Closing...\r\n".getBytes());
                            System.out.println("Wrong Command! Socket " + socket.getPort() + " is Closing...");
                            socket.close();
                        }
                    }
                    else if (dbClass.isRegistered()) {
                        socketPhone.getSocket().getOutputStream().write("Hub is Offline. Socket is Closing...\r\n".getBytes());
                        System.out.println("Hub " + id + " is Offline");
                        socket.close();
                    }
                    else if (relativeSocketHub == null){
                        socketPhone.getSocket().getOutputStream().write("Hub Not Available. Socket is Closing...\r\n".getBytes());
                        System.out.println("Hub " + id + " Not Available");
                        socket.close();
                    }
                    break;
                default: socket.getOutputStream().write("Invalid Value. Socket is Closing...\r\n".getBytes());
                    System.out.println("Wrong Identity by Socket " + socket.getPort());
                    socket.close();
            }
            System.out.println("=====================================");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private SocketHub findSocketHub(String id) {
        for (SocketHub socket : socketHubs) {
            if (socket.getId().equals(id))
                return socket;
        }
        return null;
    }

    private String readLine(Socket socket) {
        try {
            String line = "";

            while (true) {
                int in = socket.getInputStream().read();
                if (in == -1)
                    return null;
                else if (in == '\r')
                    return line;
                else if (in == '\n') {

                }
                else
                    line += (char)in;
            }
        } catch (Exception e) {
            return null;
        }
    }
}