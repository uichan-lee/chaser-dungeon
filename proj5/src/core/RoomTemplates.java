package core;

import tileengine.TETile;
import tileengine.Tileset;

import java.awt.Point;
import java.util.*;

/**
 * Static repository of predefined {@link RoomTemplate} objects used during
 * world generation.
 *
 */
public final class RoomTemplates {

    /**
     * Master list of all predefined room templates.
     */
    public static final List<RoomTemplate> ALL_TEMPLATES;

    /**
     * Maps each direction (UP, DOWN, LEFT, RIGHT) to a list of templates that
     * have at least one doorway in that direction.
     */
    public static final Map<Direction, List<RoomTemplate>> BY_DIRECTION;

    static {
        List<RoomTemplate> templates = new ArrayList<>();

        // ------------------------------------------------------------------
        // Register templates here
        // ------------------------------------------------------------------
        templates.add(makeSimpleSquareRoom());
        templates.add(makeHorizontalCorridorRoom());
        templates.add(makeVerticalCorridorRoom());
        // TODO: add more interesting room shapes if desired.

        ALL_TEMPLATES = Collections.unmodifiableList(templates);

        // Build direction index.
        Map<Direction, List<RoomTemplate>> byDir = new EnumMap<>(Direction.class);
        for (Direction d : Direction.values()) {
            byDir.put(d, new ArrayList<>());
        }
        for (RoomTemplate t : ALL_TEMPLATES) {
            for (Direction d : t.doorDirections) {
                byDir.get(d).add(t);
            }
        }
        // Wrap lists as unmodifiable.
        for (Direction d : Direction.values()) {
            byDir.put(d, Collections.unmodifiableList(byDir.get(d)));
        }
        BY_DIRECTION = Collections.unmodifiableMap(byDir);
    }

    /** Utility class; no instances allowed. */
    private RoomTemplates() { }

    /* =====================================================
     *  Template definitions
     * ===================================================== */

    /**
     * Simple 5×5 square room with walls on the boundary and floor inside.
     * Single door on the middle of the right wall.
     */
    private static RoomTemplate makeSimpleSquareRoom() {
        int w = 5;
        int h = 5;
        TETile[][] layout = new TETile[w][h];

        // Fill with floor, then overwrite boundary with walls.
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                layout[x][y] = Tileset.FLOOR;
            }
        }
        for (int x = 0; x < w; x++) {
            layout[x][0] = Tileset.WALL;
            layout[x][h - 1] = Tileset.WALL;
        }
        for (int y = 0; y < h; y++) {
            layout[0][y] = Tileset.WALL;
            layout[w - 1][y] = Tileset.WALL;
        }

        // Door in the middle of the right wall.
        Point door = new Point(w - 1, h / 2);

        List<Point> doors = List.of(door);
        Set<Direction> dirs = EnumSet.of(Direction.RIGHT);

        return new RoomTemplate(w, h, layout, doors, dirs, RoomType.SQUARE);
    }

    /**
     * Long horizontally oriented room/corridor with doors on left and right.
     */
    private static RoomTemplate makeHorizontalCorridorRoom() {
        int w = 7;
        int h = 3;
        TETile[][] layout = new TETile[w][h];

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                layout[x][y] = Tileset.FLOOR;
            }
        }
        // Top and bottom walls.
        for (int x = 0; x < w; x++) {
            layout[x][0] = Tileset.WALL;
            layout[x][h - 1] = Tileset.WALL;
        }
        // Side walls.
        for (int y = 0; y < h; y++) {
            layout[0][y] = Tileset.WALL;
            layout[w - 1][y] = Tileset.WALL;
        }

        Point leftDoor = new Point(0, 1);
        Point rightDoor = new Point(w - 1, 1);
        List<Point> doors = List.of(leftDoor, rightDoor);
        Set<Direction> dirs = EnumSet.of(Direction.LEFT, Direction.RIGHT);

        return new RoomTemplate(w, h, layout, doors, dirs, RoomType.HORIZONTAL);
    }

    /**
     * Long vertically oriented room/corridor with doors on bottom and top.
     */
    private static RoomTemplate makeVerticalCorridorRoom() {
        int w = 3;
        int h = 7;
        TETile[][] layout = new TETile[w][h];

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                layout[x][y] = Tileset.FLOOR;
            }
        }
        // Left and right walls.
        for (int y = 0; y < h; y++) {
            layout[0][y] = Tileset.WALL;
            layout[w - 1][y] = Tileset.WALL;
        }
        // Top and bottom walls.
        for (int x = 0; x < w; x++) {
            layout[x][0] = Tileset.WALL;
            layout[x][h - 1] = Tileset.WALL;
        }

        Point bottomDoor = new Point(1, 0);
        Point topDoor = new Point(1, h - 1);
        List<Point> doors = List.of(bottomDoor, topDoor);
        Set<Direction> dirs = EnumSet.of(Direction.DOWN, Direction.UP);

        return new RoomTemplate(w, h, layout, doors, dirs, RoomType.VERTICAL);
    }
}
