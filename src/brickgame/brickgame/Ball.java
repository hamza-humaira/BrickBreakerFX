package brickgame;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Ball extends Circle{

    public double dx = 4;
    public double dy = -4;

    public Ball(double x, double y, double radius)
    {
        super(radius, Color.BLACK);
        setCenterX(x);
        setCenterY(y);
    }

    public void move()
    {
        setCenterX(getCenterX() + dx);
        setCenterY(getCenterY() + dy);
    }
}
