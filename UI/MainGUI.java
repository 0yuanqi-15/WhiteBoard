package UI;

import Remote.IRemoteServerBoard;
import Server.ServerBoard;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.sun.java.accessibility.util.AWTEventMonitor.addActionListener;

public class MainGUI extends JFrame {
    private JList usernamesList;
    private JList chatContentList;
    private JTextField chatInput;
    private JButton sendChatButton;
    private WhiteBoardPanel whiteBoardPanel;
    private HashMap<String, JButton> toolButtons;
    private JMenuItem newMenuItem;
    private File openFile = null;

    public MainGUI(String username, String identity) throws RemoteException {

        // Use swing metal
        try{
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        }catch(Exception e) {
            System.out.println("Error loading UI");
            e.printStackTrace();
        }

        this.setTitle("You are logged in as: " + username + " ("+ identity + ")");
        this.setBounds(100, 100, 1200, 700);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // top bar
        JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);

        // bar - file
        JMenu fileMenu = new JMenu("File");

        if (Objects.equals(identity, "Manager")){
            menuBar.add(fileMenu);
        }

        // bar - file - new
        newMenuItem = new JMenuItem("New");
        fileMenu.add(newMenuItem);

        // bar - file - open
        JMenuItem openMenuItem = new JMenuItem("Open...");
        openMenuItem.setIcon(new ImageIcon(this.getClass().getResource("/img/open.png")));
        fileMenu.add(openMenuItem);
        openMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Pop file chooser after click on open button
                JFileChooser fileChooser = new JFileChooser();
                // Filter non-jpg files
                fileChooser.setFileFilter(new FileFilter() {
                    public String getDescription() {
                        return "JPG Images (*.jpg)";
                    }

                    public boolean accept(File f) {
                        if (f.isDirectory()) {
                            return true;
                        } else {
                            String filename = f.getName().toLowerCase();
                            return filename.endsWith(".jpg") || filename.endsWith(".jpeg") ;
                        }
                    }
                });

                // If file opened
                int result = fileChooser.showOpenDialog(fileMenu);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    BufferedImage bufferedImage = null;
                    try {
                        bufferedImage = ImageIO.read(selectedFile);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    whiteBoardPanel.setImage(bufferedImage);
                    openFile = selectedFile;
                }
            }
        });

        // bar - file - save
        JMenuItem saveMenuItem = new JMenuItem("Save");
        fileMenu.add(saveMenuItem);
        saveMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(openFile == null){
                    JOptionPane.showMessageDialog(null, "Please use \"Save as\" to specify a save location", "Error saving", JOptionPane.ERROR_MESSAGE);
                }else {
                    try {
                        whiteBoardPanel.saveToLocal(openFile);
                        JOptionPane.showMessageDialog(null, "Save successful!", "Save success", JOptionPane.INFORMATION_MESSAGE);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        // bar - file - save as
        JMenuItem saveAsMenuItem = new JMenuItem("Save As...");
        fileMenu.add(saveAsMenuItem);
        saveAsMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Pop file chooser after click on open button
                JFileChooser fileChooser = new JFileChooser();
                // Filter non-jpg files
                fileChooser.setFileFilter(new FileFilter() {
                    public String getDescription() {
                        return "JPG Images (*.jpg)";
                    }

                    public boolean accept(File f) {
                        if (f.isDirectory()) {
                            return true;
                        } else {
                            String filename = f.getName().toLowerCase();
                            return filename.endsWith(".jpg") || filename.endsWith(".jpeg") ;
                        }
                    }
                });

                // If file opened
                int result = fileChooser.showOpenDialog(fileMenu);
                if (result == JFileChooser.APPROVE_OPTION) {
                    openFile = fileChooser.getSelectedFile();
                    try {
                        whiteBoardPanel.saveToLocal(openFile);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        // bar - file - close
        JMenuItem closeMenuItem = new JMenuItem("Close");
        fileMenu.add(closeMenuItem);
        closeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirmClose = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to close the current board? Current work will ben disposed off", "Close whiteboard", JOptionPane.YES_NO_OPTION);
                if (confirmClose == 0){
                    System.exit(0);
                }
            }
        });


        toolButtons = new HashMap<>();

        // bar - pen
        JButton penButton = new JButton(new ImageIcon(this.getClass().getResource("/img/pen.png")));
        penButton.setFocusable(false);
        menuBar.add(penButton);
        toolButtons.put("pen", penButton);

        // bar - text
        JButton textButton = new JButton(new ImageIcon(this.getClass().getResource("/img/text.png")));
        textButton.setFocusable(false);
        menuBar.add(textButton);
        toolButtons.put("text", textButton);

        // bar - line
        JButton lineButton = new JButton(new ImageIcon(this.getClass().getResource("/img/line.png")));
        lineButton.setFocusable(false);
        menuBar.add(lineButton);
        toolButtons.put("line", lineButton);

        // bar - circle
        JButton circleButton = new JButton(new ImageIcon(this.getClass().getResource("/img/circle.png")));
        circleButton.setFocusable(false);
        menuBar.add(circleButton);
        toolButtons.put("circle", circleButton);

        // bar - triangle
        JButton triangleButton = new JButton(new ImageIcon(this.getClass().getResource("/img/triangle.png")));
        triangleButton.setFocusable(false);
        menuBar.add(triangleButton);
        toolButtons.put("triangle", triangleButton);

        // bar - rectangle
        JButton rectangleButton = new JButton(new ImageIcon(this.getClass().getResource("/img/rectangle.png")));
        rectangleButton.setFocusable(false);
        menuBar.add(rectangleButton);
        toolButtons.put("rectangle", rectangleButton);

        // bar - color picker
        JButton colorButton = new JButton(new ImageIcon(this.getClass().getResource("/img/color.png")));

        colorButton.addActionListener(e -> {
            Color initialcolor = Color.BLACK;
            Color color = JColorChooser.showDialog(this,"Select a color",initialcolor);
            colorButton.setBackground(color);
            whiteBoardPanel.setColorSelected(color);
        });
        colorButton.setFocusable(false);
        menuBar.add(colorButton);

        // Create main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(new EmptyBorder(3, 3, 3, 3));
        setContentPane(mainPanel);
        mainPanel.setLayout(null);

        // Add listeners to the tool tuttons
        activateToolButtons();

        // Whiteboard
        whiteBoardPanel = new WhiteBoardPanel();
        whiteBoardPanel.setBounds(15, 15, 770, 580);
        mainPanel.add(whiteBoardPanel);

        // Show online users - Container
        Container usernamesContainer = new Container();
        usernamesContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 0,5));
        usernamesContainer.setBounds(800,0,350,180);
        mainPanel.add(usernamesContainer);

        // Show online users - Heading
        JLabel usernamesLabel = new JLabel("Participants");
        usernamesLabel.setPreferredSize(new Dimension(350,20));
        usernamesContainer.add(usernamesLabel);

        // Show online users - List
        usernamesList = new JList();
        usernamesList.setPreferredSize(new Dimension(350,150));
        usernamesList.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, Color.LIGHT_GRAY));
        usernamesContainer.add(usernamesList);

        // Chat box - Container
        Container chatContainer = new Container();
        chatContainer.setBounds(800,180,350,500);
        chatContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 0,5));
        mainPanel.add(chatContainer);

        // Chat box - Heading
        JLabel chatLabel = new JLabel("Chat");
        chatLabel.setPreferredSize(new Dimension(350,20));
        chatContainer.add(chatLabel);

        // Chat box - Chat content
        chatContentList = new JList();
        chatContentList.setPreferredSize(new Dimension(350,350));
        chatContentList.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, Color.LIGHT_GRAY));
        chatContainer.add(chatContentList);

        // Chat box - Input field
        chatInput = new JTextField();
        chatInput.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, Color.LIGHT_GRAY));
        chatInput.setPreferredSize(new Dimension(280,25));
        chatContainer.add(chatInput);

        // Chat box - Send button
        sendChatButton = new JButton("Send");
        sendChatButton.setPreferredSize(new Dimension(70,25));
        chatContainer.add(sendChatButton);

        whiteBoardPanel.startListen();
        setVisible(true);
    }

    public void updateUsernames(ArrayList<String> usernames){
        this.usernamesList.setListData(usernames.toArray());
    }

    public void updateChatMessages(ArrayList<String> charMessages){
        this.chatContentList.setListData(charMessages.toArray());
    }

    public void activateToolButtons(){
        for (Map.Entry<String, JButton> set : this.toolButtons.entrySet()){
            JButton button = set.getValue();
            button.addActionListener(e -> whiteBoardPanel.setToolSelected(set.getKey()));
        }
    }

    public WhiteBoardPanel getWhiteBoardPanel() {
        return whiteBoardPanel;
    }

    public JList getUsernamesList() {
        return usernamesList;
    }

    public JButton getSendChatButton(){
        return sendChatButton;
    }

    public JTextField getChatInput() {
        return chatInput;
    }

    public JMenuItem getNewMenuItem() {
        return newMenuItem;
    }
}
