package Server;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class CreateWhiteBoard{

    public static void main(String[] args) throws RemoteException{
        String serverIPAddress = args[0];
        String serverPort = args[1];
        String managerName = args[2];
        ServerBoard serverBoard = new ServerBoard(serverIPAddress, serverPort, managerName);

        try{
            //Publish the remote object's stub in the registry under the name "WhiteBoard"
            Registry registry = LocateRegistry.createRegistry(Integer.parseInt(serverPort));
            registry.bind("WhiteBoard", serverBoard);
            System.out.println("WhiteBoard server ready");
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Port already in use", "error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
            e.printStackTrace();
        }

    }

}
