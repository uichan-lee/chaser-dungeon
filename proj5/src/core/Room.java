package core;

import tileengine.TETile;
import tileengine.Tileset;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a concrete room instance placed in the world.
 *
 * A {@code Room} wraps a {@link RoomTemplate} with world-space coordinates
 * for its origin (bottom-left tile). All drawing and intersection checks are
 * performed in world coordinates.
 */
public class Room {

    /** Blueprint from which this room was instantiated. */
    public final RoomTemplate template;

    /** World-space x-coordinate of the room's bottom-left corner. */
    public final int worldX;

    /** World-space y-coordinate of the room's bottom-left corner. */
    public final int worldY;


    /**
     * Creates a new placed room in world coordinates.
     *
     * @param template template that defines the room layout
     * @param worldX   world x-coordinate of the room origin (bottom-left)
     * @param worldY   world y-coordinate of the room origin (bottom-left)
     */
    public Room(RoomTemplate template, int worldX, int worldY) {
        this.template = template;
        this.worldX = worldX;
        this.worldY = worldY;
    }

    /* =====================================================
     *  Geometry helpers
     * ===================================================== */

    /**
     * @return the world-space x-coordinate of the room's right edge
     *         (exclusive).
     */
    public int maxX() {
        return worldX + template.width;
    }

    /**
     * @return the world-space y-coordinate of the room's top edge
     *         (exclusive).
     */
    public int maxY() {
        return worldY + template.height;
    }

    /**
     * @return the door positions converted into world-space coordinates.
     */
    public List<Point> getWorldDoorPositions() {
        List<Point> positions = new ArrayList<>();

        // Convert each points to world-space coordinates
        for (Point p : template.doorPositions) {
            int newX = p.x + worldX;
            int newY = p.y + worldY;
            positions.add(new Point(newX, newY));
        }

        return positions;
    }

    /**
     * Checks whether this room fits entirely within the world bounds.
     * World coordinates are assumed to be valid in
     * {@code [1, worldWidth - 1)} × {@code [1, worldHeight - 1)}.
     *
     * @param worldWidth  total world width
     * @param worldHeight total world height
     * @return true if every tile of this room lies inside the bounds
     */
    public boolean fitsWithinWorld(int worldWidth, int worldHeight) {
        return worldX >= 1
                && worldY >= 1
                && maxX() <= worldWidth - 1
                && maxY() <= worldHeight - 1;
    }

    /**
     * Axis-aligned bounding-box overlap test with another room.
     *
     * @param other another placed room
     * @return true if the bounding boxes of the two rooms overlap
     */
    public boolean overlaps(Room other) {
        return !(maxX() <= other.worldX
                || other.maxX() <= worldX
                || maxY() <= other.worldY
                || other.maxY() <= worldY);
    }

    /**
     * Checks this room against a collection of existing rooms.
     *
     * @param existing collection of rooms already placed in the world
     * @return true if this room overlaps any of the existing rooms
     */
    public boolean overlapsAny(Collection<Room> existing) {
        for (Room r : existing) {
            if (overlaps(r)) {
                return true;
            }
        }
        return false;
    }

    /* =====================================================
     *  Drawing logic
     * ===================================================== */

    /**
     * Draws the room into the global world tile map.
     * Assumes {@link #fitsWithinWorld(int, int)} has already been checked.
     *
     * @param world the global {@code TETile[][]} map
     */
    public void drawInto(TETile[][] world) {
        for (int dx = 0; dx < template.width; dx++) {
            for (int dy = 0; dy < template.height; dy++) {
                TETile tile = template.layout[dx][dy];
                int x = worldX + dx;
                int y = worldY + dy;
                TETile existing = world[x][y];

                if (tile == Tileset.NOTHING) {
                    continue;
                }

                if (existing == Tileset.NOTHING) {
                    world[x][y] = tile;
                    continue;
                }

                if (canOverwrite(existing, tile)) {
                    world[x][y] = tile;
                }
            }
        }
    }

    private boolean canOverwrite(TETile existing, TETile incoming) {
        if (existing == Tileset.WALL && incoming != Tileset.NOTHING) {
            return true;
        }
        if (incoming == Tileset.LOCKED_DOOR || incoming == Tileset.UNLOCKED_DOOR) {
            return existing == Tileset.FLOOR;
        }
        return false;
    }
}
