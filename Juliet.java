/*
 * Juliet.java
 *
 * Juliet class.  Implements the Juliet subsystem of the Romeo and Juliet ODE system.
 */


import javafx.util.Pair;

import java.io.*;
import java.lang.Thread;
import java.net.*;

public class Juliet extends Thread {

    private ServerSocket ownServerSocket = null; //Juliet's (server) socket
    private Socket serviceMailbox = null; //Juliet's (service) socket

    private double currentLove = 0;
    private double b = 0;


    //Class construtor
    public Juliet(double initialLove) {
        int portNum = 7779;
        currentLove = initialLove;
        b = 0.01;
        try {
            ownServerSocket = new ServerSocket(portNum);
            System.out.println("Juliet: Good pilgrim, you do wrong your hand too much, ...");
        } catch (Exception e) {
            System.out.println("Juliet: Failed to create own socket " + e);
        }
    }

    // Get acquaintance with lover;
    // Receives lover's socket information and share's own socket
    public Pair<InetAddress, Integer> getAcquaintance() {
        System.out.println("Juliet: My bounty is as boundless as the sea,\n" +
                "       My love as deep; the more I give to thee,\n" +
                "       The more I have, for both are infinite.");
        Pair<InetAddress, Integer> pair1 = null;

        try {
            int portNum = 7779;
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
                if (x == 'R') {
                    break;
                }
                stringBuffer.append(x);
            }
            in_msg = Double.parseDouble(stringBuffer.toString());
        } catch (IOException e) {
            System.out.println(e);
        }
        System.out.println("Juliet: Romeo, Romeo! Wherefore art thou Romeo? (<-" + in_msg + ")");
        return in_msg;
    }


    //Love (The ODE system)
    //Given the lover's love at time t, estimate the next love value for Romeo
    public double renovateLove(double partnerLove) {
        System.out.println("Juliet: Come, gentle night, come, loving black-browed night,\n" +
                "       Give me my Romeo, and when I shall die,\n" +
                "       Take him and cut him out in little stars.");
        currentLove = currentLove + (-b * partnerLove);
        return currentLove;
    }


    //Communicate love back to playwriter
    public void declareLove() {
        try {
            OutputStreamWriter requestStreamWriter = new OutputStreamWriter(serviceMailbox.getOutputStream());
            String love = currentLove+"J";
            System.out.println("Juliet: Good night, good night! Parting is such sweet sorrow,    \n That I shall say good night till it be morrow.(->"+love+")");
            requestStreamWriter.write(love);
            requestStreamWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    //Execution
    public void run() {
        try {
            while (!this.isInterrupted()) {
                //Retrieve lover's current love
                double RomeoLove = this.receiveLoveLetter();

                //Estimate new love value
                this.renovateLove(RomeoLove);

                //Communicate back to lover, Romeo's love
                this.declareLove();
            }
        } catch (Exception e) {
            System.out.println("Juliet: " + e);
        }
        if (this.isInterrupted()) {
            System.out.println("Juliet: I will kiss thy lips.\n" +
                    "Haply some poison yet doth hang on them\n" +
                    "To make me die with a restorative.");
        }

    }

}
