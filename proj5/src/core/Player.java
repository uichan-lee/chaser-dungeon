package core;

import java.awt.Point;
import tileengine.TETile;
import tileengine.Tileset;

public class Player {
    public Point pos;
    public Point previousPos; // Previous position before last move
    public Direction facing;
    public TETile tileUnderPlayer;
    public int pushAbilityCount; // Number of push abilities

    public Player(int x, int y) {
        pos = new Point(x, y);
        previousPos = new Point(x, y); // Initialize to same position
        facing = Direction.UP;
        tileUnderPlayer = Tileset.FLOOR;
        pushAbilityCount = 2; // Start with two push abilities
    }

    public Point frontTile() {
        return new Point(pos.x + facing.dx, pos.y + facing.dy);
    }
}
