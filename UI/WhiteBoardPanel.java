package UI;

import javafx.scene.shape.Line;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class WhiteBoardPanel extends JPanel {

    private String toolSelected;
    private Graphics2D graphics2D;
    private Point startPoint;
    private Point endPoint;
    private Integer countTriPoints;
    private Point[] trianglePointList;
    private ArrayList<Point> penPointList;
    private ArrayList<Shape> shapeList;
    private BufferedImage image;
    private Color colorSelected;

    public WhiteBoardPanel() {
        this.setBackground(Color.WHITE);
        this.toolSelected = "pen";
        this.colorSelected = Color.BLACK;
        this.penPointList = new ArrayList<>() ;     // Keep all the points in the trace
        this.shapeList  = new ArrayList<>();
        this.trianglePointList = new Point[3];     // Keep three points
        this.countTriPoints = 0;
        this.image = null;
    }

    public void startListen(){
        switch (toolSelected){
            // Record all passed points in an array
            case "pen":
                removeAllListeners();
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        penPointList.add(e.getPoint());
                    }

                    public void mouseReleased(MouseEvent e) {
                        // Add new pen draw
                        // System.out.println("enter here");
                        shapeList.add(new Shape(penPointList, colorSelected));
                        penPointList = new ArrayList<>();}
                });


                addMouseMotionListener(new MouseMotionAdapter() {
                    public void mouseDragged(MouseEvent e) {
                        if(Objects.equals(toolSelected, "pen")){
                            Point newPoint = e.getPoint();
                            penPointList.add(newPoint);
                            repaint();
                        }
                    }
                });
                break;

            // Require two points to locate
            case "line":
            case "circle":
            case "rectangle":
                removeAllListeners();
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        startPoint = e.getPoint();
                    }

                    public void mouseReleased(MouseEvent e) {
                        endPoint = e.getPoint();
                        shapeList.add(new Shape(startPoint, endPoint, colorSelected, toolSelected));
                        startPoint = new Point();
                        endPoint = new Point();
                        repaint();
                    }
                });
                break;

            // Require three points to locate
            case "triangle":
                removeAllListeners();
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        trianglePointList[countTriPoints] = e.getPoint();
                        countTriPoints = countTriPoints + 1;
                        if(countTriPoints == 3){
                            shapeList.add(new Shape(trianglePointList, colorSelected));
                            countTriPoints = 0;
                            trianglePointList = new Point[3];
                            repaint();
                        }
                    }
                });
                break;

            // Require input text and a location
            case "text":
                removeAllListeners();
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        String inputString = JOptionPane.showInputDialog("Please input the text.");
                        shapeList.add(new Shape(inputString, e.getPoint(), colorSelected));
                        repaint();
                    }
                });
        }
    }

    public synchronized void paint(Graphics g) {
        super.paint(g);
        if(image!=null){
            g.drawImage(image, 0,0, this);
        }

        // Draw pen stroke as moved
        for (int i = 1; i < penPointList.size(); i++){
            g.setColor(colorSelected);
            g.drawLine(penPointList.get(i - 1).x, penPointList.get(i - 1).y, penPointList.get(i).x, penPointList.get(i).y);
        }

        for (Shape eachShape : shapeList){
            eachShape.shapePaint(g);
        }
    }

    public void setToolSelected(String toolSelected) {
        // System.out.println("tool set to " + toolSelected);
        this.toolSelected = toolSelected;
        startListen();
    }

    public void setColorSelected(Color colorSelected) {
        this.colorSelected = colorSelected;
    }

    public void removeAllListeners(){
        if (getMouseListeners() != null){
            for (MouseListener eachListener : getMouseListeners()){
                removeMouseListener(eachListener);
            }
        }

        if (getMouseMotionListeners() != null){
            for (MouseMotionListener eachMotionListener : getMouseMotionListeners()){
                removeMouseMotionListener(eachMotionListener);
            }
        }
    }

    public synchronized byte[] saveStatustoBytes() throws IOException {
        BufferedImage image = new BufferedImage(this.getWidth(),this.getHeight(),BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        this.paint(graphics);
        graphics.dispose();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        byte[] imageInByte = baos.toByteArray();
        // System.out.println("byte output is " + Arrays.toString(imageInByte));
        return imageInByte;
    }

    public synchronized void readBytetoStatus(byte[] imageInByte) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(imageInByte);
        // clearBoard();
        this.image = ImageIO.read(bais);
        repaint();
    }

    public synchronized void setImage(BufferedImage image) {
        clearBoard();
        this.image = image;
        repaint();
    }

    public synchronized void clearBoard(){
        this.image = null;
        this.shapeList = new ArrayList<>();
        repaint();
    }

    public void saveToLocal(File file) throws IOException {
        BufferedImage image = new BufferedImage(this.getWidth(),this.getHeight(),BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        this.paint(graphics);
        graphics.dispose();
        ImageIO.write(image, "jpg", file);
    }
}
