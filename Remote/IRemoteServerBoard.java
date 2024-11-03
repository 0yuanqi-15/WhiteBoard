package Remote;

import Client.ClientBoard;
import UI.MainGUI;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface IRemoteServerBoard extends Remote {

    String getManagerName() throws RemoteException;
    Boolean checkNameValid(String peerName) throws RemoteException;
    int permission(String username) throws RemoteException, NotBoundException;
    ArrayList<String> getPeerNames() throws RemoteException;
    byte[] getBoardStatus() throws RemoteException, IOException;
    MainGUI getManagerGUI() throws RemoteException;
    void updateBoard(byte[] saveStatustoBytes) throws IOException;
    void removePeer(String username) throws RemoteException;
    String getKickOutName() throws RemoteException;
    void updateChatMessages(String username, String message) throws RemoteException;
    ArrayList<String> getChatMessages() throws RemoteException;
    Boolean getClear() throws RemoteException;
    void setClear(Boolean clear) throws RemoteException;
}
