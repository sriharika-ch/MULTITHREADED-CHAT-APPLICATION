import java.io.*;         //for i/o applications 
import java.net.*;           //for network applications
import java.util.*;          //for data structures

public class ser{
    private static final int PORT = 12345;    //server will listen to this port
    private static Set<ClientHandler> clientHandlers = Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started. Listening on port " + PORT + "...");

            while (true) {         //continuously waits for client to connect
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected!");

                ClientHandler handler = new ClientHandler(clientSocket);  //creates a new ClientHandler thread
                clientHandlers.add(handler); // adding handler to the set 
                handler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Broadcast message to all connected clients
    public static void broadcast(String message, ClientHandler sender) {
        synchronized (clientHandlers) {
            for (ClientHandler handler : clientHandlers) {
                if (handler != sender) {
                    handler.sendMessage(message);
                }
            }
        }     //message to all clients except to the sender
    }

    // Removes a client when they disconnect
    public static void removeClient(ClientHandler handler) {
        clientHandlers.remove(handler);
    }

    static class ClientHandler extends Thread {
        private Socket socket;   //socket for client connection
        private PrintWriter out;         //to send messages to client
        private BufferedReader in;      //to read messages from client

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                out.println("Welcome to the chat! Type 'exit' to quit.");

                String message;
                while ((message = in.readLine()) != null) {
                    if (message.equalsIgnoreCase("exit")) {
                        break;
                    }
                    System.out.println("Received: " + message);
        ser.broadcast(message, this);
                }
            } catch (IOException e) {
                System.out.println("Error with client: " + e.getMessage());
            } finally {
                try {
                    socket.close();            //To catch any exceptions and close the socket
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ser.removeClient(this);
                System.out.println("Client disconnected.");
            }
        }

        public void sendMessage(String message) {
            out.println(message);
        }
    }
}
