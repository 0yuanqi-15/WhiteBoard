package Client;

import Remote.IRemoteServerBoard;
import UI.MainGUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public class ClientBoard implements Serializable{

    private MainGUI peerGUI;
    public String peerName;

    protected ClientBoard(String peerName, IRemoteServerBoard serverRemoteBoard) throws RemoteException, NotBoundException {
        // Check for same usernames
        if (!serverRemoteBoard.checkNameValid(peerName)){
            JOptionPane.showMessageDialog(null, "This username has been used, please try another name", "error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        this.peerName = peerName;
        this.peerGUI = new MainGUI(peerName, "Peer");

        // Ask for permission
        if (serverRemoteBoard.permission(peerName) == 1){
            JOptionPane.showMessageDialog(null, "You've been rejected by the manager", "Reject", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        // Notify the server if the client exits
        peerGUI.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                try {
                    serverRemoteBoard.removePeer(peerName);
                    serverRemoteBoard.updateChatMessages(peerName, "exit the room");
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        });

        // Notify the server if the client send a new message
        peerGUI.getSendChatButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String chatMessgae = peerGUI.getChatInput().getText();
                try {
                    serverRemoteBoard.updateChatMessages(peerName, chatMessgae);
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        });


        // Listen to server changes
        ClientThread thread = new ClientThread(serverRemoteBoard, peerGUI, peerName);
        new Thread(thread).start();

    }

}
