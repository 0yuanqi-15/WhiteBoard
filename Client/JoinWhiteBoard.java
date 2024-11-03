package Client;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import Remote.IRemoteServerBoard;
import UI.MainGUI;

import javax.swing.*;

public class JoinWhiteBoard extends UnicastRemoteObject {

    public static void main(String[] args) throws NotBoundException, RemoteException {
        String serverIPAddress = args[0];
        String serverPort = args[1];
        String peerName = args[2];
        IRemoteServerBoard serverRemoteBoard = null;

        // Connect to the port
        try{
            Registry registry = LocateRegistry.getRegistry(serverIPAddress, Integer.parseInt(serverPort));
            serverRemoteBoard = (IRemoteServerBoard) registry.lookup("WhiteBoard");
            System.out.println("Connection established");
        } catch (Exception e){
            JOptionPane.showMessageDialog(null, "Connection failed", "error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        ClientBoard clientRemoteBoard = new ClientBoard(peerName, serverRemoteBoard);

    }

    protected JoinWhiteBoard() throws RemoteException {
    }
}
