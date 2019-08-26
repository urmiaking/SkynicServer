import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

@SuppressWarnings("ALL")
public class SocketPhone implements Runnable {
    private Socket socketPhone;
    private SocketHub socketHub;
    private String line;
    private String pass;
    private String serial;
    private Commands cmdList = new Commands();

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getPass() {
        return pass;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getSerial() {
        return serial;
    }

    public Socket getSocket() {
        return socketPhone;
    }

    public SocketPhone(Socket socket, SocketHub socketHub) {
        this.socketPhone = socket;
        this.socketHub = socketHub;
    }


    @Override
    public void run() {
        while (true) {
            try {
                line = readLine(); //chizie ke client mifreste
                if (line == null) {
                    socketPhone.close();
                    socketHub.socketPhonesArrays.remove(this);
                    System.out.println("Socket Phone " + socketPhone.getPort() + " Closed");
                    System.out.println("=====================================");
                    ClientManager.getInstance().dbClass.setClientNumbers(socketHub.socketPhonesArrays.size());
                    break;
                } else {
                    if (cmdList.isValidCommand(line)) {
                        if (line.startsWith("AT+NEWPASS")) {
                            String[] passPhrase = line.split("=");
                            if (!(passPhrase.length < 2 || passPhrase.length > 2)) {
                                String passcode = passPhrase[1];
                                this.setPass(passcode);
                            }
                        }
                        socketHub.write((line + "\r\n"), this);
                        System.out.println("Message From Phone " + socketPhone.getPort() + " To Hub " + socketHub.getSocketHub().getPort() + " : " + line);
                    }
                    else {
                        socketPhone.getOutputStream().write("Invalid Command...\r\n".getBytes());
                    }
                }
                Thread.sleep(100);
            } catch(IOException e){
                e.printStackTrace();
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public String readLine() {
        try {
            String line = "";

            while (true) {
                int in = socketPhone.getInputStream().read();
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