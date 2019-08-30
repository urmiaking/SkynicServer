import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

@SuppressWarnings("ALL")
public class SocketHub implements Runnable {

    private Socket socketHub;
    private SocketPhone socketPhone;
    private String id;
    private String numberOfDevices;
    private Queue<SocketPhone> socketPhoneQueue = new ArrayDeque<>();
    public ArrayList<SocketPhone> socketPhonesArrays = new ArrayList<>();

    public void addSocketPhonesArrays(SocketPhone socketPhone) {
        socketPhonesArrays.add(socketPhone);
    }

    public void setSocketPhone(SocketPhone socketPhone) {
        this.socketPhone = socketPhone;
    }

    public void setSocketHub(Socket socketHub) {
        this.socketHub = socketHub;
    }

    public Socket getSocketHub() {
        return socketHub;
    }

    public String getId() {
        return id;
    }

    public SocketHub(Socket socket, String id) {
        this.socketHub = socket;
        this.id = id;
    }

    @Override
    public void run() {
        String tempIp = this.socketHub.getInetAddress().toString();
        StringBuilder sb = new StringBuilder(tempIp);
        sb.deleteCharAt(0);
        String ipAddress = sb.toString();
        DBClass dbClass = new DBClass(this.getId());
        while (true) {
            String line = readLine(); //chizie ke hub az vorodi khodesh mikhone mifreste be phone
            try {
                if (line == null) {
                    socketHub.close();
                    System.out.println("Socket HUB " + socketHub.getPort() + " Closed");
                    ClientManager.getInstance().socketHubs.remove(this);
                    dbClass.setOffline();
                    dbClass.setClientNumbers(0);
                    dbClass.logSocketHubClosed(ipAddress, id, this.socketPhone.getPass());
                    if (socketPhonesArrays != null) {
                        for (SocketPhone socketPhone : socketPhonesArrays) {
                            socketPhone.getSocket().getOutputStream().write("Hub Disconnected! Disconnecting...\r\n".getBytes());
                            socketPhone.getSocket().close();
                        }
                    }
                    socketPhonesArrays.clear();
                    System.out.println("=====================================");
                    break;
                } else if (line.startsWith("DEV+COUNT")) {
                    String[] temp = line.split("=");
                    numberOfDevices = temp[1];
                    socketPhone = socketPhoneQueue.poll();
                    socketPhone.getSocket().getOutputStream().write((line + "\r\n").getBytes());
                    for (int i = 0; i < Integer.parseInt(numberOfDevices); i++) {
                        line = readLine();
                        socketPhoneQueue.add(socketPhone);
                        if (socketPhonesArrays != null) {
                            synchronized (socketPhoneQueue) {
                                socketPhone = socketPhoneQueue.poll();
                                if (socketPhone != null) {
                                    socketPhone.getSocket().getOutputStream().write((line + "\r\n").getBytes());
                                    System.out.println("Message From Hub " + socketHub.getPort()+ " To Phone " + socketPhone.getSocket().getPort()+ " :"+ line);
                                }
                            }
                        }
                    }
                } else if (line.equals("PASS ERROR")) {
                    try {
                        socketPhonesArrays.remove(socketPhone);
                        synchronized (socketPhoneQueue) {
                            socketPhone = socketPhoneQueue.poll();
                            if (socketPhone != null) {
                                socketPhone.getSocket().getOutputStream().write((line + "\r\n").getBytes());
                                socketPhone.getSocket().close();
                                System.out.println("HUB " + socketHub.getPort()+ " : Wrong Password Sent by Client " + socketPhone.getSocket().getPort());
                                dbClass.logPassError(ipAddress, id, this.socketPhone.getPass());
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (line.equals("PASS OK")) {
                    try {
                        synchronized (socketPhoneQueue) {
                            socketPhone = socketPhoneQueue.poll();
                            if (socketPhone != null) {
                                socketPhone.getSocket().getOutputStream().write((line + "\r\n").getBytes());
                                String passCode = socketPhone.getPass();
                                String serial = socketPhone.getSerial();
                                dbClass.addPhone(ipAddress,passCode,serial);
                                dbClass.logPhoneConnected(ipAddress, passCode, serial);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (line.equals("NEW PASS OK")) {
                    try {
                        synchronized (socketPhoneQueue) {
                            socketPhone = socketPhoneQueue.poll();
                            if (socketPhone != null) {
                                socketPhone.getSocket().getOutputStream().write((line + "\r\n").getBytes());
                                String newPassword = socketPhone.getPass();
                                String serial = socketPhone.getSerial();
                                dbClass.updatePassword(ipAddress,newPassword,serial);
                                dbClass.logPasswordUpdated(ipAddress, serial, newPassword);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    if (socketPhonesArrays != null) {
                        synchronized (socketPhoneQueue) {
                            socketPhone = socketPhoneQueue.poll();
                            if (socketPhone != null) {
                                socketPhone.getSocket().getOutputStream().write((line + "\r\n").getBytes());
                                System.out.println("Message From Hub " + socketHub.getPort()+ " To Phone " + socketPhone.getSocket().getPort()+ " :"+ line);
                            }
                        }
                    }
                }
                Thread.sleep(100);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                try {
                    ClientManager.getInstance().socketHubs.remove(this);
                    dbClass.setOffline();
                    socketHub.close();
                    break;
                } catch (IOException e1) {
                    e1.printStackTrace();
                    break;
                }
            }
        }
    }


    public String readLine() {
        try {
            String line = "";

            while (true) {
                int in = socketHub.getInputStream().read();
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

    public void write(String command, SocketPhone socketPhone) {
        synchronized (socketPhoneQueue) {
            socketPhoneQueue.add(socketPhone);
            try {
                socketHub.getOutputStream().write(command.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
