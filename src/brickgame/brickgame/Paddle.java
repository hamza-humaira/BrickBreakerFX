package brickgame;

import javafx.scene.paint.Color; //imports color class from javafx
                                // used to set fill or stroke shapes
import javafx.scene.shape.Rectangle; //imports rec class from javafx that can be
                                    // added to scene and pane

public class Paddle  extends Rectangle{

    public Paddle(double x, double y, double width, double height)
    {
        super(width, height, Color.BLUE);
        setX(x); //cant write this.x as it is private inside the rec class
        setY(y);
    }

    public void moveLeft() //when called, moves the paddle to left
    {
        setX(Math.max(getX() - 15, 0)); //gets the current x coordinate of the object
                                        // and moves it to the left by 15 pixels
                                        // the 0 makes sure that it stops at the left edge
    }

    public void moveRight(double sceneWidth)
    {
        setX(Math.min(getX() + 15, sceneWidth - getWidth()));
    }
}
