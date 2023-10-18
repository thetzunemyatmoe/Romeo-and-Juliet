/*
 * Romeo.java
 *
 * Romeo class.  Implements the Romeo subsystem of the Romeo and Juliet ODE system.
 */


import java.io.*;
import java.lang.Thread;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.InetAddress;

import javafx.util.Pair;

public class Romeo extends Thread {
    private ServerSocket ownServerSocket = null; //Romeo's (server) socket
    private Socket serviceMailbox = null; //Romeo's (service) socket
    private double currentLove = 0;
    private double a = 0; //The ODE constant
    //Class constructor
    public Romeo(double initialLove) {
        int portNum = 7778;
        currentLove = initialLove;
        a = 0.02;
        try {
            ownServerSocket = new ServerSocket(portNum);
            System.out.println("Romeo: What lady is that, which doth enrich the hand\n" +
                    "       Of yonder knight?");
        } catch (Exception e) {
            System.out.println("Romeo: Failed to create own socket " + e);
        }
    }

    //Get acquaintance with lover;
    // Receives lover's socket information and share's own socket
    public Pair<InetAddress, Integer> getAcquaintance() {
        System.out.println("Romeo: Did my heart love till now? forswear it, sight! For I ne'er saw true beauty till this night.");
        Pair<InetAddress, Integer> pair1 = null;
        try {
            int portNum = 7778;
            pair1 = new Pair<>(InetAddress.getByName("127.0.0.1"), portNum);
        } catch (Exception e) {
            System.out.println(e);
        }
        return pair1;
    }


    //Retrieves the lover's love
    public double receiveLoveLetter() {
        double in_msg = 0.0;
        try {
            serviceMailbox = ownServerSocket.accept();
            InputStreamReader socketReader = new InputStreamReader(serviceMailbox.getInputStream());
            StringBuffer stringBuffer = new StringBuffer();
            char x;
            while (true) {
                x = (char) socketReader.read();
                if (x == 'J') {
                    break;
                }
                stringBuffer.append(x);
            }
            in_msg = Double.parseDouble(stringBuffer.toString());
        } catch (IOException e) {
            System.out.println(e);
        }
        System.out.println("Romeo: O sweet Juliet... (<-" + in_msg + ")");
        return in_msg;
    }
    //Love (The ODE system)
    //Given the lover's love at time t, estimate the next love value for Romeo
    public double renovateLove(double partnerLove) {
        System.out.println("Romeo: But soft, what light through yonder window breaks?\n" +
                "       It is the east, and Juliet is the sun.");

        currentLove = currentLove + (a * partnerLove);
        return currentLove;
    }
    //Communicate love back to playwriter
    public void declareLove() {
        try {
            OutputStreamWriter requestStreamWriter = new OutputStreamWriter(serviceMailbox.getOutputStream());
            String love = currentLove+"R";
            System.out.println("Romeo : I would I were thy bird. (->"+ love +")");
            requestStreamWriter.write(love);
            requestStreamWriter.flush();
        } catch (IOException e) {
            System.out.println(e);
        }

    }
    //Execution
    public void run() {
        try {
            while (!this.isInterrupted()) {
                //Retrieve lover's current love
                double JulietLove = this.receiveLoveLetter();

                //Estimate new love value
                this.renovateLove(JulietLove);

                //Communicate love back to playwriter
                this.declareLove();
            }
        } catch (Exception e) {
            System.out.println("Romeo: " + e);
        }
        if (this.isInterrupted()) {
            System.out.println("Romeo: Here's to my love. O true apothecary,\n" +
                    "Thy drugs are quick. Thus with a kiss I die.");
        }
    }

}
