import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class TestClientPhone {
    static Socket socket;

    @SuppressWarnings("Duplicates")
    public static void main(String[] args) {
        Scanner scn = new Scanner(System.in);
        String in = "";
        try {
            socket = new Socket("127.0.0.1", 9898);
            System.out.println("Connected");

            System.out.print("Enter Hub Serial: ");
            String hubSerial = scn.nextLine();
            socket.getOutputStream().write(("AT+PHONE=" + hubSerial + "\r\n").getBytes());
            System.out.println("Identity Sent");
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            while (true) {
                try {
                    int i = socket.getInputStream().available();
                    if (i>0) {
                        byte[] buf = new byte[i];
                        socket.getInputStream().read(buf);
                        String msg = new String(buf);
                        System.out.print("Incoming Message: ");
                        System.out.println(msg);
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        while (!in.equals("exit")) {
            try {
                in = scn.nextLine();
                socket.getOutputStream().write((in + "\r\n").getBytes());
                System.out.println("Message Sent to hub: " + in);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
