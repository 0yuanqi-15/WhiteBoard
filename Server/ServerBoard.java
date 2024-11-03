package Server;

import Remote.*;
import UI.MainGUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Objects;

public class ServerBoard extends UnicastRemoteObject implements IRemoteServerBoard {
    private String serverIPAddress;
    private String serverPort;
    private String managerName;
    private ArrayList<String> peerNames;
    private ArrayList<String> chatMessages;
    private MainGUI managerGUI;
    private String kickOutName;
    private Boolean clear = false;


    protected ServerBoard(String serverIPAddress, String serverPort, String managerName) throws RemoteException {
        System.out.println("Creating whiteboard at port: " + serverPort + ", for manager: " + managerName);
        this.serverIPAddress = serverIPAddress;
        this.serverPort = serverPort;
        this.managerName = managerName;
        this.peerNames = new ArrayList<>();
        this.chatMessages = new ArrayList<>();
        this.managerGUI = new MainGUI(managerName, "Manager");
        this.kickOutName = "";

        peerNames.add(managerName);
        managerGUI.updateUsernames(peerNames);

        chatMessages.add("Manager "+managerName+" has entered the room");
        managerGUI.updateChatMessages(chatMessages);

        // Add listener to the click on the username event
        managerGUI.getUsernamesList().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                String userSelected = (String) managerGUI.getUsernamesList().getSelectedValue();
                if (!Objects.equals(userSelected, managerName) && userSelected != null){
                    int kickOut = JOptionPane.showConfirmDialog(null, "Do you want to remove user "+userSelected+" from the room?", "Confirm removal", JOptionPane.YES_NO_OPTION);
                    if(kickOut == 0){
                        kickOutName = userSelected;
                        try {
                            removePeer(userSelected);
                        } catch (RemoteException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });

        managerGUI.getSendChatButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String chatMessgae = managerGUI.getChatInput().getText();
                if(Objects.equals(chatMessgae, "")){return;}
                chatMessages.add(managerName+": "+chatMessgae);
                managerGUI.getChatInput().setText("");
                managerGUI.updateChatMessages(chatMessages);
            }
        });

        managerGUI.getNewMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirmNew = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to create a new board? Current work will ben disposed off", "New whiteboard", JOptionPane.YES_NO_OPTION);
                if (confirmNew == 0){
                    managerGUI.getWhiteBoardPanel().clearBoard();
                    clear = true;
                }
            }
        });

    }

    public String getManagerName() {
        return managerName;
    }

    public Boolean checkNameValid(String peerName){
        return (!peerNames.contains(peerName));
    }

    public int permission(String username) throws RemoteException, NotBoundException {
        int permit = JOptionPane.showConfirmDialog(null, "User "+username+" wants to join", "Ask for permission", JOptionPane.YES_NO_OPTION);
        if(permit == 0){
            this.peerNames.add(username);
            this.managerGUI.updateUsernames(peerNames);
            this.chatMessages.add(username +" has entered the room.");
            this.managerGUI.updateChatMessages(chatMessages);
        }
        return permit;
    }

    public void removePeer(String username) throws RemoteException{
        this.peerNames.remove(username);
        this.managerGUI.updateUsernames(peerNames);
    }

    public ArrayList<String> getPeerNames() {
        return peerNames;
    }

    public MainGUI getManagerGUI() {
        return managerGUI;
    }

    public byte[] getBoardStatus() throws IOException {
        byte[] bytes =  managerGUI.getWhiteBoardPanel().saveStatustoBytes();
//        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
//        BufferedImage bImage2 = ImageIO.read(bais);
//        ImageIO.write(bImage2, "jpeg", new File("bufferIMG.jpeg") );
        return bytes;
    }

    public void updateBoard(byte[] bytes) throws IOException {
        managerGUI.getWhiteBoardPanel().readBytetoStatus(bytes);
    }

    public String getKickOutName() {
        return kickOutName;
    }

    public void updateChatMessages(String username, String message){
        this.chatMessages.add(username +": "+message);
        this.managerGUI.updateChatMessages(chatMessages);
    }

    public ArrayList<String> getChatMessages() {
        return chatMessages;
    }

    public Boolean getClear() {
        return clear;
    }

    public void setClear(Boolean clear) {
        this.clear = clear;
    }
}
