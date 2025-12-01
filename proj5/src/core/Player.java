package core;

import java.awt.Point;

public class Player {
    public Point pos;
    public Direction facing;

    public Player(int x, int y) {
        pos = new Point(x, y);
        facing = Direction.UP;
    }

    public Point frontTile() {
        return new Point(pos.x + facing.dx, pos.y + facing.dy);
    }
}
