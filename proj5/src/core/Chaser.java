package core;

import java.awt.Point;
import java.util.List;
import tileengine.TETile;
import tileengine.Tileset;

public class Chaser {
    public Point pos;
    public Point previousPos; // Previous position before last move
    public List<Point> path;
    public List<Point> previousPath; // Previous path for clearing
    public boolean showPath;
    public TETile tileUnderChaser;

    public Chaser(int x, int y) {
        pos = new Point(x, y);
        previousPos = new Point(x, y); // Initialize to same position
        showPath = false;
        path = null;
        previousPath = null;
        tileUnderChaser = Tileset.FLOOR;
    }
}

