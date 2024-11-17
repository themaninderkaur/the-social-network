package com.example.the_social_network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

public class UserClient {

    public static void main(String [] args) {
        Socket socket;
        BufferedReader reader;
        PrintWriter writer;
         try {
            socket = new Socket("localhost", 5000);
            reader = new BufferedReader (new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream());
        } catch (UnknownHostException e) {
            JOptionPane.showMessageDialog(null, "Invalid socket, quitting program", 
                "socketError", JOptionPane.ERROR_MESSAGE);
            return;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "IO Error, quitting program", 
                "fileError", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(null, "Congratulations! You are connected :D", 
            "connectionSuccess", JOptionPane.INFORMATION_MESSAGE);

    }
}
