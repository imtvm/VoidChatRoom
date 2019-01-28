import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.net.*;
import java.io.*;
import java.sql.SQLOutput;
import java.util.Scanner;

public class VoidClient {

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    /**
     * Starts the client and connects to the server specified at the port and ip address
     *
     * @param ip the ip address of the server to connect to
     * @param port is the port number that the server is listening on and the client will connect too
     * @throws IOException
     */
    public void start(String ip, int port) throws IOException{
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        new RecieveMsgs(in).start();
    }

    /**
     * Send the msg string to the server at the specified port and ip in the clientSocket
     *
     * @param msg a string of characters representing the message to be sent to the server
     * @throws IOException
     */
    public void sendMsg(String msg) throws IOException {
        out.println(msg);
    }

    private class RecieveMsgs extends Thread{
        private BufferedReader in;

        private RecieveMsgs(BufferedReader inputStream){
            in = inputStream;
        }

        public void run() {
            String input;
            try{
                while (true){
                    if ((input = in.readLine()) != null){
                        System.out.println(input);
                    }
                }
            }catch (IOException e){
                System.out.println(e.toString());
            }
        }
    }

    /**
     * Stops the client I/O streams and closes the connection.
     * @throws IOException
     */
    public void stop() throws IOException{
        clientSocket.close();
        out.close();
        in.close();
    }

    public static void main(String[] args) throws IOException{
        VoidClient client = new VoidClient();
        client.start("localhost", 9999);

        Scanner in = new Scanner(System.in);
        String input;
        while (true){
            if ((input = in.nextLine()) != null){
                client.sendMsg(input);
            }
        }

    }
}
