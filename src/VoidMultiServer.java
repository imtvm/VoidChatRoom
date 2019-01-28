import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class VoidMultiServer {

    private ServerSocket serverSocket;

    /**
     * Starts the server
     *
     * @param port is a port in which the clients will try to connect to
     * @throws IOException
     */
    public void start(int port) throws IOException{
        serverSocket = new ServerSocket(port);

        while (true){
            VoidClientHandler tmpClient = new VoidClientHandler(serverSocket.accept());
            tmpClient.start();

        }
    }

    /**
     * Stops the server
     *
     * @throws IOException
     */
    public void stop() throws IOException{
        serverSocket.close();
    }

    private static class VoidClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private String name;
        private static ArrayList<VoidClientHandler> clients = new ArrayList<VoidClientHandler>();

        private VoidClientHandler(Socket socket){
            clientSocket = socket;
            name = nameGen();
            System.out.println("Client Socket Created for User: " + name);
            clients.add(this);
        }

        private String nameGen(){
            Random rnd = new Random();
            int length = rnd.nextInt(7) + 3;
            String  name = "";
            char[] lexicon = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

            for (int i = 0; i < length; i++){
                int pos = rnd.nextInt(lexicon.length);
                name += String.valueOf(lexicon[pos]);
            }

            return name;
        }

        public void run(){
            try {

                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                sendMessageToUser("You have joind the chat as " + name);
                sendMessage(name + " has joined the chat", 2);

                String input = in.readLine();

                while (true){

                    if (input.equals("server.exit")){
                        stopConnection();
                        break;
                    }

                    sendMessage(input, 0);

                    input = in.readLine();
                }
            }catch (IOException e){
                System.out.println(e.toString());
            }

            stopConnection();

        }

        private void stopConnection(){
            try {
                clients.remove(this);
                clientSocket.close();
                out.close();
                in.close();
            }catch (IOException e){
                System.out.println(e.toString());
            }

        }

        /**
         * Send the input string to only the client that is calling the method
         * @param input a string of characters representing the message to be sent to the client
         */
        public void sendMessageToUser(String input){
            this.out.println("System: " + input);
        }

        /**
         * Send the input string to the clients depnding on the opt integer provided. If the
         * opt provided is a 0 (zero), it will send a User message to all the clients excluding the one the method
         * is being called by. If 1 (one) is the opt provided, then the method will send a System message to every
         * client including the one calling the method. If 2 (two) is the opt provided, then the method will send
         * a System message to all clients excluding the one calling the method.
         *
         * @param input a string of characters representing the message to be sent to the clients
         * @param opt an integer to determine which clients to send the input to
         */
        public void sendMessage(String input, int opt){
            if (opt == 1){
                for (VoidClientHandler c : clients){
                    c.out.println("System: " + input);
                }
            }else if (opt == 2){
                for (VoidClientHandler c : clients){
                    if (c != this){
                        c.out.println("System: " + input);
                    }
                }
            }else{
                for (VoidClientHandler c : clients){
                    if (c != this){
                        c.out.println(this.name + ": " + input);
                    }
                }
            }

        }
    }

    public static void main(String[] args) throws IOException{
        VoidMultiServer server = new VoidMultiServer();

        server.start(9999);
    }
}
