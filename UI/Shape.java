package UI;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Shape{
    private String type;
    private ArrayList<Point> points;
    private Color color;
    private Point[] trianglePoints;
    private Point startPoint;
    private Point endPoint;
    private String textInput;

    // Overloading for pen
    public Shape(ArrayList<Point> points, Color color){
        this.points = points;
        this.type = "pen";
        this.color = color;
    }

    // Overloading for triangle
    public Shape(Point[] points, Color color){
        this.trianglePoints = points;
        this.type = "triangle";
        this.color = color;
    }

    // Overloading for line, circle, rectangle
    public Shape(Point startPoint, Point endPoint, Color color, String type) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.color = color;
        this.type = type;
    }

    // Overloading for text
    public Shape(String textInput, Point startPoint, Color color){
        this.startPoint = startPoint;
        this.textInput = textInput;
        this.type = "text";
        this.color = color;
    }

    // Paint according to the type
    public void shapePaint(Graphics g){
        g.setColor(color);
//        System.out.println("type is " + type);
        switch (type){
            case "pen":
                for (int i = 1; i < points.size(); i++){
                    g.drawLine(points.get(i - 1).x, points.get(i - 1).y, points.get(i).x, points.get(i).y);
                }
                break;

            case "line":
                try{
                    g.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
                }
                catch (Exception e){
                    System.out.println(startPoint);
                    System.out.println(endPoint);
                    e.printStackTrace();
                }
                break;

            case "circle":
                int max = Math.max(Math.abs(startPoint.x - endPoint.x), Math.abs(endPoint.y - startPoint.y));
                g.drawOval(Math.min(startPoint.x, endPoint.x), Math.min(startPoint.y, endPoint.y), max, max);
                break;

            case "rectangle":
                g.drawRect(Math.min(startPoint.x, endPoint.x), Math.min(startPoint.y, endPoint.y), Math.abs(startPoint.x - endPoint.x), Math.abs(endPoint.y - startPoint.y));
                break;

            case "triangle":
                for (int i = 0; i < 3; i++){
                    System.out.println(i+"th point is " +trianglePoints[i]);
                }

                int[] points_x = {trianglePoints[0].x, trianglePoints[1].x,trianglePoints[2].x};
                int[] points_y = {trianglePoints[0].y, trianglePoints[1].y,trianglePoints[2].y};
                g.drawPolygon(points_x, points_y, 3);
                break;

            case "text":
                g.drawString(textInput, startPoint.x, startPoint.y);
                break;
        }

    }

}
