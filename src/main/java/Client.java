import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 5000); // Connect to server
            System.out.println("Connected to store");

            DataInputStream in = new DataInputStream(socket.getInputStream());
            Scanner scanner = new Scanner(System.in);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            String line;
            do {
                line = scanner.nextLine();
                out.writeUTF(line); // Send user input to server
                System.out.println(in.readUTF());
            } while (!line.equals("Over"));

//            input.close();
            scanner.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
