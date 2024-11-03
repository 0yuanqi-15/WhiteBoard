package Client;

import Remote.IRemoteServerBoard;
import UI.MainGUI;

import javax.swing.*;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Objects;

public class ClientThread implements Runnable{
    IRemoteServerBoard serverRemoteBoard;
    MainGUI peerGUI;
    String peerName;

    public ClientThread(IRemoteServerBoard serverRemoteBoard, MainGUI peerGUI, String peerName){
        this.serverRemoteBoard = serverRemoteBoard;
        this.peerGUI = peerGUI;
        this.peerName = peerName;
    }

    @Override
    public void run() {
        while (true){
            try {
                Thread.sleep(300);
                // Update username list
                peerGUI.updateUsernames(serverRemoteBoard.getPeerNames());
                // Update chat messages
                peerGUI.updateChatMessages(serverRemoteBoard.getChatMessages());
                // Update server white board to clients
                peerGUI.getWhiteBoardPanel().readBytetoStatus(serverRemoteBoard.getBoardStatus());
                // Update client white board to server
                serverRemoteBoard.updateBoard(peerGUI.getWhiteBoardPanel().saveStatustoBytes());
                // Update to check if kicked out
                if (Objects.equals(serverRemoteBoard.getKickOutName(), peerName)){
                    JOptionPane.showMessageDialog(null, "You've been removed by the manager", "No access", JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }

                if (serverRemoteBoard.getClear()){
                    peerGUI.getWhiteBoardPanel().clearBoard();
                    serverRemoteBoard.setClear(false);
                }
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "The manager has closed the room", "Terminated", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
