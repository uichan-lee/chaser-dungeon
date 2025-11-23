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

        templates.add(makeSmallLRoom());
        templates.add(makeSmallLRoomMirroredH());
        templates.add(makeSmallLRoomMirroredV());
        templates.add(makeSmallLRoomRotated180());
        
        templates.add(makeBigLRoom());
        templates.add(makeBigLRoomMirroredH());
        templates.add(makeBigLRoomMirroredV());
        templates.add(makeBigLRoomRotated180());

        // Keep adding more templates

        ALL_TEMPLATES = Collections.unmodifiableList(templates);

        // Build direction index.
        Map<Direction, List<RoomTemplate>> byDir = new EnumMap<>(Direction.class);
        for (Direction d : Direction.values()) {
            byDir.put(d, new ArrayList<>());
        }
        for (RoomTemplate t : ALL_TEMPLATES) {
            if (t.roomTypes.contains(RoomType.STARTING)) {
                continue;
            }

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
        Point rightDoor = new Point(w - 1, h / 2);
        List<Point> doors = List.of(topDoor, rightDoor);
        for (Point door : doors) {
            layout[door.x][door.y] = Tileset.LOCKED_DOOR;
        }

        Set<Direction> dirs = EnumSet.of(Direction.UP, Direction.RIGHT);
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

        // Four doors, one centered on each wall.
        Point leftDoor = new Point(0, h / 2);          // left wall center
        Point rightDoor = new Point(w - 1, h / 2);     // right wall center
        Point bottomDoor = new Point(w / 2, 0);        // bottom wall center
        Point topDoor = new Point(w / 2, h - 1);       // top wall center

        List<Point> doors = List.of(leftDoor, rightDoor, bottomDoor, topDoor);
        for (Point door : doors) {
            layout[door.x][door.y] = Tileset.LOCKED_DOOR;
        }

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

        // Four doors, one centered on each wall.
        Point leftDoor = new Point(0, h / 2);          // left wall center
        Point rightDoor = new Point(w - 1, h / 2);     // right wall center
        Point bottomDoor = new Point(w / 2, 0);        // bottom wall center
        Point topDoor = new Point(w / 2, h - 1);       // top wall center

        List<Point> doors = List.of(leftDoor, rightDoor, bottomDoor, topDoor);
        for (Point door : doors) {
            layout[door.x][door.y] = Tileset.LOCKED_DOOR;
        }

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
        for (Point door : doors) {
            layout[door.x][door.y] = Tileset.LOCKED_DOOR;
        }

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
        for (Point door : doors) {
            layout[door.x][door.y] = Tileset.LOCKED_DOOR;
        }

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
        for (Point door : doors) {
            layout[door.x][door.y] = Tileset.LOCKED_DOOR;
        }

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
        for (Point door : doors) {
            layout[door.x][door.y] = Tileset.LOCKED_DOOR;
        }

        Set<Direction> dirs = EnumSet.of(Direction.DOWN, Direction.UP);
        Set<RoomType> roomTypes = EnumSet.of(RoomType.VERTICAL);

        return new RoomTemplate(w, h, layout, doors, dirs, roomTypes);
    }


    /**
     * Small L-shaped room.
     * Whole Box: 8 x 8 (Including Wall)
     * Inner coordinates: x=1..6, y=1..6
     * - Vertical leg:  x=1..3, y=1..6
     * - Horizontal leg:  x=1..6, y=1..3
     * => Upper Right 3 x 3 becomes NOTHING
     *
     * Doors: (2,7) [UP], (7,2) [RIGHT]
     */
    private static RoomTemplate makeSmallLRoom() {
        int w = 8;
        int h = 8;
        TETile[][] layout = new TETile[w][h];

        // 1) Fill All with NOTHING
        fill(layout, Tileset.NOTHING);

        // 2) Vertical leg
        for (int x = 1; x <= 3; x++) {
            for (int y = 1; y <= 6; y++) {
                layout[x][y] = Tileset.FLOOR;
            }
        }

        // 3) Horizontal leg
        for (int x = 1; x <= 6; x++) {
            for (int y = 1; y <= 3; y++) {
                layout[x][y] = Tileset.FLOOR;
            }
        }

        // 4) Border WALL
        // Top + Bottom
        for (int x = 0; x < w; x++) {
            layout[x][0] = Tileset.WALL;

            if (x <= 4) {
                layout[x][7] = Tileset.WALL;
            }
            if (x >= 4) {
                layout[x][4] = Tileset.WALL;
            }
        }

        // Left + Right
        for (int y = 0; y < h; y++) {
            layout[0][y] = Tileset.WALL;

            if (y <= 4) {
                layout[7][y] = Tileset.WALL;
            }
            if (y >= 4) {
                layout[4][y] = Tileset.WALL;
            }
        }

        // 5) Door positions (LEFT / RIGHT)
        Point upperLeftDoor  = new Point(2, 7);  // Middle of upper wall
        Point lowerRightDoor = new Point(7, 2);  // Middle of right wall
        List<Point> doors = List.of(upperLeftDoor, lowerRightDoor);
        for (Point door : doors) {
            layout[door.x][door.y] = Tileset.LOCKED_DOOR;
        }

        Set<Direction> dirs = EnumSet.of(Direction.UP, Direction.RIGHT);
        Set<RoomType> roomTypes = EnumSet.of(RoomType.L);

        return new RoomTemplate(w, h, layout, doors, dirs, roomTypes);
    }

    /**
     * Large L-shaped room – thicker version of {@link #makeSmallLRoom()}.
     *
     * Whole box: 12 x 12 (including walls).
     * Interior coordinates: x = 1..10, y = 1..10.
     *   - Vertical leg:   x = 1..5,  y = 1..10
     *   - Horizontal leg: x = 1..10, y = 1..5
     * So the upper-right 5 x 5 interior (x = 6..10, y = 6..10) stays NOTHING,
     * giving an L of thickness 5.
     *
     * Doors:
     *   - (3, 11): top side, centered on the vertical leg  → Direction.UP
     *   - (11, 3): right side, centered on the horizontal leg → Direction.RIGHT
     */
    private static RoomTemplate makeBigLRoom() {
        int w = 12;
        int h = 12;
        TETile[][] layout = new TETile[w][h];

        // Start with all NOTHING.
        fill(layout, Tileset.NOTHING);

        // Vertical leg: x = 1..5, y = 1..10
        for (int x = 1; x <= 5; x++) {
            for (int y = 1; y <= 10; y++) {
                layout[x][y] = Tileset.FLOOR;
            }
        }

        // Horizontal leg: x = 1..10, y = 1..5
        for (int x = 1; x <= 10; x++) {
            for (int y = 1; y <= 5; y++) {
                layout[x][y] = Tileset.FLOOR;
            }
        }

        // Add walls around the L-shaped floor region:
        // any NOTHING tile that is 4-neighbor to a FLOOR tile becomes WALL.
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (layout[x][y] == Tileset.FLOOR) {
                    for (Direction d : Direction.values()) {
                        int nx = x + d.dx;
                        int ny = y + d.dy;
                        if (nx < 0 || nx >= w || ny < 0 || ny >= h) {
                            continue;
                        }
                        if (layout[nx][ny] == Tileset.NOTHING) {
                            layout[nx][ny] = Tileset.WALL;
                        }
                    }
                }
            }
        }

        // Doors on the top and right sides.
        Point upperLeftDoor  = new Point(3, 11);
        Point lowerRightDoor = new Point(11, 3);
        List<Point> doors = List.of(upperLeftDoor, lowerRightDoor);
        for (Point door : doors) {
            layout[door.x][door.y] = Tileset.LOCKED_DOOR;
        }

        Set<Direction> dirs = EnumSet.of(Direction.UP, Direction.RIGHT);
        Set<RoomType> roomTypes = EnumSet.of(RoomType.L);

        return new RoomTemplate(w, h, layout, doors, dirs, roomTypes);
    }

    /**
     * Small L-shaped room mirrored horizontally (┘ shape).
     * Vertical leg on right, horizontal leg on bottom.
     * Doors: (5, 7) [UP], (0, 2) [LEFT]
     */
    private static RoomTemplate makeSmallLRoomMirroredH() {
        int w = 8;
        int h = 8;
        TETile[][] layout = new TETile[w][h];

        fill(layout, Tileset.NOTHING);

        // Vertical leg on right: x=4..6, y=1..6
        for (int x = 4; x <= 6; x++) {
            for (int y = 1; y <= 6; y++) {
                layout[x][y] = Tileset.FLOOR;
            }
        }

        // Horizontal leg on bottom: x=1..6, y=1..3
        for (int x = 1; x <= 6; x++) {
            for (int y = 1; y <= 3; y++) {
                layout[x][y] = Tileset.FLOOR;
            }
        }

        // Add walls around the L-shaped floor region
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (layout[x][y] == Tileset.FLOOR) {
                    for (Direction d : Direction.values()) {
                        int nx = x + d.dx;
                        int ny = y + d.dy;
                        if (nx < 0 || nx >= w || ny < 0 || ny >= h) {
                            continue;
                        }
                        if (layout[nx][ny] == Tileset.NOTHING) {
                            layout[nx][ny] = Tileset.WALL;
                        }
                    }
                }
            }
        }

        Point upperRightDoor = new Point(5, 7);
        Point lowerLeftDoor = new Point(0, 2);
        List<Point> doors = List.of(upperRightDoor, lowerLeftDoor);
        for (Point door : doors) {
            layout[door.x][door.y] = Tileset.LOCKED_DOOR;
        }

        Set<Direction> dirs = EnumSet.of(Direction.UP, Direction.LEFT);
        Set<RoomType> roomTypes = EnumSet.of(RoomType.L);

        return new RoomTemplate(w, h, layout, doors, dirs, roomTypes);
    }

    /**
     * Small L-shaped room mirrored vertically (┌ shape).
     * Vertical leg on left, horizontal leg on top.
     * Doors: (2, 0) [DOWN], (7, 5) [RIGHT]
     */
    private static RoomTemplate makeSmallLRoomMirroredV() {
        int w = 8;
        int h = 8;
        TETile[][] layout = new TETile[w][h];

        fill(layout, Tileset.NOTHING);

        // Vertical leg on left: x=1..3, y=1..6
        for (int x = 1; x <= 3; x++) {
            for (int y = 1; y <= 6; y++) {
                layout[x][y] = Tileset.FLOOR;
            }
        }

        // Horizontal leg on top: x=1..6, y=4..6
        for (int x = 1; x <= 6; x++) {
            for (int y = 4; y <= 6; y++) {
                layout[x][y] = Tileset.FLOOR;
            }
        }

        // Add walls around the L-shaped floor region
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (layout[x][y] == Tileset.FLOOR) {
                    for (Direction d : Direction.values()) {
                        int nx = x + d.dx;
                        int ny = y + d.dy;
                        if (nx < 0 || nx >= w || ny < 0 || ny >= h) {
                            continue;
                        }
                        if (layout[nx][ny] == Tileset.NOTHING) {
                            layout[nx][ny] = Tileset.WALL;
                        }
                    }
                }
            }
        }

        Point lowerLeftDoor = new Point(2, 0);
        Point upperRightDoor = new Point(7, 5);
        List<Point> doors = List.of(lowerLeftDoor, upperRightDoor);
        for (Point door : doors) {
            layout[door.x][door.y] = Tileset.LOCKED_DOOR;
        }

        Set<Direction> dirs = EnumSet.of(Direction.DOWN, Direction.RIGHT);
        Set<RoomType> roomTypes = EnumSet.of(RoomType.L);

        return new RoomTemplate(w, h, layout, doors, dirs, roomTypes);
    }

    /**
     * Small L-shaped room rotated 180 degrees (┐ shape).
     * Vertical leg on right, horizontal leg on top.
     * Doors: (5, 0) [DOWN], (0, 5) [LEFT]
     */
    private static RoomTemplate makeSmallLRoomRotated180() {
        int w = 8;
        int h = 8;
        TETile[][] layout = new TETile[w][h];

        fill(layout, Tileset.NOTHING);

        // Vertical leg on right: x=4..6, y=1..6
        for (int x = 4; x <= 6; x++) {
            for (int y = 1; y <= 6; y++) {
                layout[x][y] = Tileset.FLOOR;
            }
        }

        // Horizontal leg on top: x=1..6, y=4..6
        for (int x = 1; x <= 6; x++) {
            for (int y = 4; y <= 6; y++) {
                layout[x][y] = Tileset.FLOOR;
            }
        }

        // Add walls around the L-shaped floor region
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (layout[x][y] == Tileset.FLOOR) {
                    for (Direction d : Direction.values()) {
                        int nx = x + d.dx;
                        int ny = y + d.dy;
                        if (nx < 0 || nx >= w || ny < 0 || ny >= h) {
                            continue;
                        }
                        if (layout[nx][ny] == Tileset.NOTHING) {
                            layout[nx][ny] = Tileset.WALL;
                        }
                    }
                }
            }
        }

        Point lowerRightDoor = new Point(5, 0);
        Point upperLeftDoor = new Point(0, 5);
        List<Point> doors = List.of(lowerRightDoor, upperLeftDoor);
        for (Point door : doors) {
            layout[door.x][door.y] = Tileset.LOCKED_DOOR;
        }

        Set<Direction> dirs = EnumSet.of(Direction.DOWN, Direction.LEFT);
        Set<RoomType> roomTypes = EnumSet.of(RoomType.L);

        return new RoomTemplate(w, h, layout, doors, dirs, roomTypes);
    }

    /**
     * Large L-shaped room mirrored horizontally (┘ shape).
     * Vertical leg on right, horizontal leg on bottom.
     * Doors: (8, 11) [UP], (0, 3) [LEFT]
     */
    private static RoomTemplate makeBigLRoomMirroredH() {
        int w = 12;
        int h = 12;
        TETile[][] layout = new TETile[w][h];

        fill(layout, Tileset.NOTHING);

        // Vertical leg on right: x = 6..10, y = 1..10
        for (int x = 6; x <= 10; x++) {
            for (int y = 1; y <= 10; y++) {
                layout[x][y] = Tileset.FLOOR;
            }
        }

        // Horizontal leg on bottom: x = 1..10, y = 1..5
        for (int x = 1; x <= 10; x++) {
            for (int y = 1; y <= 5; y++) {
                layout[x][y] = Tileset.FLOOR;
            }
        }

        // Add walls around the L-shaped floor region
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (layout[x][y] == Tileset.FLOOR) {
                    for (Direction d : Direction.values()) {
                        int nx = x + d.dx;
                        int ny = y + d.dy;
                        if (nx < 0 || nx >= w || ny < 0 || ny >= h) {
                            continue;
                        }
                        if (layout[nx][ny] == Tileset.NOTHING) {
                            layout[nx][ny] = Tileset.WALL;
                        }
                    }
                }
            }
        }

        Point upperRightDoor = new Point(8, 11);
        Point lowerLeftDoor = new Point(0, 3);
        List<Point> doors = List.of(upperRightDoor, lowerLeftDoor);
        for (Point door : doors) {
            layout[door.x][door.y] = Tileset.LOCKED_DOOR;
        }

        Set<Direction> dirs = EnumSet.of(Direction.UP, Direction.LEFT);
        Set<RoomType> roomTypes = EnumSet.of(RoomType.L);

        return new RoomTemplate(w, h, layout, doors, dirs, roomTypes);
    }

    /**
     * Large L-shaped room mirrored vertically (┌ shape).
     * Vertical leg on left, horizontal leg on top.
     * Doors: (3, 0) [DOWN], (11, 8) [RIGHT]
     */
    private static RoomTemplate makeBigLRoomMirroredV() {
        int w = 12;
        int h = 12;
        TETile[][] layout = new TETile[w][h];

        fill(layout, Tileset.NOTHING);

        // Vertical leg on left: x = 1..5, y = 1..10
        for (int x = 1; x <= 5; x++) {
            for (int y = 1; y <= 10; y++) {
                layout[x][y] = Tileset.FLOOR;
            }
        }

        // Horizontal leg on top: x = 1..10, y = 6..10
        for (int x = 1; x <= 10; x++) {
            for (int y = 6; y <= 10; y++) {
                layout[x][y] = Tileset.FLOOR;
            }
        }

        // Add walls around the L-shaped floor region
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (layout[x][y] == Tileset.FLOOR) {
                    for (Direction d : Direction.values()) {
                        int nx = x + d.dx;
                        int ny = y + d.dy;
                        if (nx < 0 || nx >= w || ny < 0 || ny >= h) {
                            continue;
                        }
                        if (layout[nx][ny] == Tileset.NOTHING) {
                            layout[nx][ny] = Tileset.WALL;
                        }
                    }
                }
            }
        }

        Point lowerLeftDoor = new Point(3, 0);
        Point upperRightDoor = new Point(11, 8);
        List<Point> doors = List.of(lowerLeftDoor, upperRightDoor);
        for (Point door : doors) {
            layout[door.x][door.y] = Tileset.LOCKED_DOOR;
        }

        Set<Direction> dirs = EnumSet.of(Direction.DOWN, Direction.RIGHT);
        Set<RoomType> roomTypes = EnumSet.of(RoomType.L);

        return new RoomTemplate(w, h, layout, doors, dirs, roomTypes);
    }

    /**
     * Large L-shaped room rotated 180 degrees (┐ shape).
     * Vertical leg on right, horizontal leg on top.
     * Doors: (8, 0) [DOWN], (0, 8) [LEFT]
     */
    private static RoomTemplate makeBigLRoomRotated180() {
        int w = 12;
        int h = 12;
        TETile[][] layout = new TETile[w][h];

        fill(layout, Tileset.NOTHING);

        // Vertical leg on right: x = 6..10, y = 1..10
        for (int x = 6; x <= 10; x++) {
            for (int y = 1; y <= 10; y++) {
                layout[x][y] = Tileset.FLOOR;
            }
        }

        // Horizontal leg on top: x = 1..10, y = 6..10
        for (int x = 1; x <= 10; x++) {
            for (int y = 6; y <= 10; y++) {
                layout[x][y] = Tileset.FLOOR;
            }
        }

        // Add walls around the L-shaped floor region
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (layout[x][y] == Tileset.FLOOR) {
                    for (Direction d : Direction.values()) {
                        int nx = x + d.dx;
                        int ny = y + d.dy;
                        if (nx < 0 || nx >= w || ny < 0 || ny >= h) {
                            continue;
                        }
                        if (layout[nx][ny] == Tileset.NOTHING) {
                            layout[nx][ny] = Tileset.WALL;
                        }
                    }
                }
            }
        }

        Point lowerRightDoor = new Point(8, 0);
        Point upperLeftDoor = new Point(0, 8);
        List<Point> doors = List.of(lowerRightDoor, upperLeftDoor);
        for (Point door : doors) {
            layout[door.x][door.y] = Tileset.LOCKED_DOOR;
        }

        Set<Direction> dirs = EnumSet.of(Direction.DOWN, Direction.LEFT);
        Set<RoomType> roomTypes = EnumSet.of(RoomType.L);

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
