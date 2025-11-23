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

        /* =====================================================
         *  Templates
         * ===================================================== */
        templates.add(makeStartingRoom());
        templates.add(makeSmallSquareRoom());
        templates.add(makeBigSquareRoom());
        templates.add(makeSmallHorizontalRoom());
        templates.add(makeBigHorizontalRoom());
        templates.add(makeSmallVerticalRoom());
        templates.add(makeBigVerticalRoom());

        // Keep adding more templates

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

    /**
     * Utility class; no instances allowed.
     */
    private RoomTemplates() {
    }

    /* =====================================================
     *  Template definitions
     * ===================================================== */

    /**
     * [3, 3]
     * <p>
     * Starting room uses 3x3 square.
     * Every new game will start with player in this room.
     */
    private static RoomTemplate makeStartingRoom() {
        int w = 5;
        int h = 5;
        TETile[][] layout = new TETile[w][h];

        fill(layout, Tileset.FLOOR);
        addBoundaryWalls(layout);

        // Door in the middle of the top wall.
        Point topDoor = new Point(w / 2, h - 1); // (2, 4)
        List<Point> doors = List.of(topDoor);
        Set<Direction> dirs = EnumSet.of(Direction.UP);
        Set<RoomType> roomTypes = EnumSet.of(RoomType.STARTING);

        return new RoomTemplate(w, h, layout, doors, dirs, roomTypes);
    }

    /**
     * [5, 5]
     * <p>
     * Simple 5×5 square room with walls on the boundary and floor inside.
     * Single door in the middle of the right wall.
     */
    private static RoomTemplate makeSmallSquareRoom() {
        int w = 7;
        int h = 7;
        TETile[][] layout = new TETile[w][h];

        fill(layout, Tileset.FLOOR);
        addBoundaryWalls(layout);

        // Door in the middle of the right wall.
        Point door = new Point(w - 1, h / 2);

        List<Point> doors = List.of(door);
        Set<Direction> dirs = EnumSet.of(Direction.LEFT, Direction.RIGHT, Direction.UP, Direction.DOWN);
        Set<RoomType> roomTypes = EnumSet.of(RoomType.SQUARE);

        return new RoomTemplate(w, h, layout, doors, dirs, roomTypes);
    }

    /**
     * [9, 9]
     * <p>
     * Simple 9×9 square room with walls on the boundary and floor inside.
     */
    private static RoomTemplate makeBigSquareRoom() {
        int w = 11;
        int h = 11;
        TETile[][] layout = new TETile[w][h];

        fill(layout, Tileset.FLOOR);
        addBoundaryWalls(layout);

        // Door in the middle of the right wall.
        Point door = new Point(w - 1, h / 2);

        List<Point> doors = List.of(door);
        Set<Direction> dirs = EnumSet.of(Direction.LEFT, Direction.RIGHT, Direction.UP, Direction.DOWN);
        Set<RoomType> roomTypes = EnumSet.of(RoomType.SQUARE);

        return new RoomTemplate(w, h, layout, doors, dirs, roomTypes);
    }

    /**
     * [7, 3]
     * Long horizontally oriented room/corridor with doors on left and right.
     */
    private static RoomTemplate makeSmallHorizontalRoom() {
        int w = 9;
        int h = 5;
        TETile[][] layout = new TETile[w][h];

        fill(layout, Tileset.FLOOR);
        addBoundaryWalls(layout);

        // Doors centered vertically on left and right walls.
        Point leftDoor = new Point(0, h / 2);        // (0, 2)
        Point rightDoor = new Point(w - 1, h / 2);   // (8, 2)
        List<Point> doors = List.of(leftDoor, rightDoor);
        Set<Direction> dirs = EnumSet.of(Direction.LEFT, Direction.RIGHT);
        Set<RoomType> roomTypes = EnumSet.of(RoomType.HORIZONTAL);

        return new RoomTemplate(w, h, layout, doors, dirs, roomTypes);
    }

    /**
     * [11, 5]
     * Long horizontally oriented room/corridor with doors on left and right.
     */
    private static RoomTemplate makeBigHorizontalRoom() {
        int w = 13;
        int h = 7;
        TETile[][] layout = new TETile[w][h];

        fill(layout, Tileset.FLOOR);
        addBoundaryWalls(layout);

        // Doors centered vertically on left and right walls.
        Point leftDoor = new Point(0, h / 2);        // (0, 3)
        Point rightDoor = new Point(w - 1, h / 2);   // (12, 3)
        List<Point> doors = List.of(leftDoor, rightDoor);
        Set<Direction> dirs = EnumSet.of(Direction.LEFT, Direction.RIGHT);
        Set<RoomType> roomTypes = EnumSet.of(RoomType.HORIZONTAL);

        return new RoomTemplate(w, h, layout, doors, dirs, roomTypes);
    }


    /**
     * [3, 7]
     * Long vertically oriented room/corridor with doors on bottom and top.
     */
    private static RoomTemplate makeSmallVerticalRoom() {
        int w = 5;
        int h = 9;
        TETile[][] layout = new TETile[w][h];

        fill(layout, Tileset.FLOOR);
        addBoundaryWalls(layout);

        // Doors centered horizontally on bottom and top walls.
        Point bottomDoor = new Point(w / 2, 0);      // (2, 0)
        Point topDoor = new Point(w / 2, h - 1);     // (2, 8)
        List<Point> doors = List.of(bottomDoor, topDoor);
        Set<Direction> dirs = EnumSet.of(Direction.DOWN, Direction.UP);
        Set<RoomType> roomTypes = EnumSet.of(RoomType.VERTICAL);

        return new RoomTemplate(w, h, layout, doors, dirs, roomTypes);
    }

    /**
     * [5, 11]
     * Long vertically oriented room/corridor with doors on bottom and top.
     */
    private static RoomTemplate makeBigVerticalRoom() {
        int w = 7;
        int h = 13;
        TETile[][] layout = new TETile[w][h];

        fill(layout, Tileset.FLOOR);
        addBoundaryWalls(layout);

        // Doors centered horizontally on bottom and top walls.
        Point bottomDoor = new Point(w / 2, 0);      // (3, 0)
        Point topDoor = new Point(w / 2, h - 1);     // (3, 12)
        List<Point> doors = List.of(bottomDoor, topDoor);
        Set<Direction> dirs = EnumSet.of(Direction.DOWN, Direction.UP);
        Set<RoomType> roomTypes = EnumSet.of(RoomType.VERTICAL);

        return new RoomTemplate(w, h, layout, doors, dirs, roomTypes);
    }


    /* =====================================================
     *  Helper methods
     * ===================================================== */

    /**
     * Fill every tile in the layout with provided tile.
     */
    private static void fill(TETile[][] layout, TETile tile) {
        int w = layout.length;
        int h = layout[0].length;
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                layout[x][y] = tile;
            }
        }
    }


    private static void addBoundaryWalls(TETile[][] layout) {
        int w = layout.length;
        int h = layout[0].length;

        // Top + Bottom
        for (int x = 0; x < w; x++) {
            layout[x][0] = Tileset.WALL;
            layout[x][h - 1] = Tileset.WALL;
        }

        // Left + Right
        for (int y = 0; y < h; y++) {
            layout[0][y] = Tileset.WALL;
            layout[w - 1][y] = Tileset.WALL;
        }
    }

    /**
     * Add a room type to a given RoomTemplate
     */
    private static void addRoomType(RoomTemplate roomTemplate, RoomType rt) {
        roomTemplate.roomTypes.add(rt);
    }

}
