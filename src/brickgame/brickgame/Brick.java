package brickgame;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Brick extends Rectangle{

    public boolean destroyed = false; //tracks whether brick has been hit or not

    public Brick(double x, double y, double width, double height, Color color)
    {
        super(width, height, color);
        setX(x); //position of the brick in a particular scene
        setY(y);
    }

    public void destroy()
    {
        destroyed = true; //when ball hits the brick, mark it destroyed
        setVisible(false); //removes the brick from view on the screen
    }
}
