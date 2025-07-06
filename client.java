import java.io.*;   // for io operations 
import java.net.*;   //for implementing network applicatons
public class client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;   //port  number of client

    public static void main(String[] args) {   
        try (                          //creates a TCP connection
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            BufferedReader serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))  //reads message from server
        ) {
            System.out.println("Connected to chat server.");

            // Thread to read messages from server
            Thread readThread = new Thread(() -> {
                String msgFromServer;
                try {
                    while ((msgFromServer = serverInput.readLine()) != null) {
                        System.out.println(msgFromServer);
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server.");
                }
            });

            readThread.start();

            // Main thread reads user input
            String userMessage;
            while ((userMessage = userInput.readLine()) != null) {
                out.println(userMessage);
                if (userMessage.equalsIgnoreCase("exit")) {
                    break;          // to break loop and disconnection
                }
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
