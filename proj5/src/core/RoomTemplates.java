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
        // Starting room
        templates.add(makeStartingRoom());

        // Square rooms
        templates.add(makeSmallSquareRoom());
        templates.add(makeBigSquareRoom());

        // Horizontal rooms
        templates.add(makeSmallHorizontalRoom());
        templates.add(makeBigHorizontalRoom());

        // Vertical rooms
        templates.add(makeSmallVerticalRoom());
        templates.add(makeBigVerticalRoom());

        // Small L-shaped rooms
        templates.add(makeSmallLRoom());
        templates.add(makeSmallLRoomMirroredH());
        templates.add(makeSmallLRoomMirroredV());
        templates.add(makeSmallLRoomRotated180());

        // Big L-shaped rooms
        templates.add(makeBigLRoom());
        templates.add(makeBigLRoomMirroredH());
        templates.add(makeBigLRoomMirroredV());
        templates.add(makeBigLRoomRotated180());

        // Ring shaped rooms
        // Small ring-shaped rooms
        templates.add(makeSmallSquareThinRingRoom());

        // Big ring-shaped rooms
        // TODO: Implement makeBigSquareRingRoom()
        // templates.add(makeBigSquareRingRoom());


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

        fill(layout, Tileset.SAND);
        addBoundaryWalls(layout);

        // Doors on all 4 sides
        Point topDoor = new Point(w / 2, h - 1);      // (2, 4) - top
        Point bottomDoor = new Point(w / 2, 0);       // (2, 0) - bottom
        Point leftDoor = new Point(0, h / 2);         // (0, 2) - left
        Point rightDoor = new Point(w - 1, h / 2);    // (4, 2) - right
        List<Point> doors = List.of(topDoor, bottomDoor, leftDoor, rightDoor);
        drawLockedDoors(doors, layout);

        Set<Direction> dirs = EnumSet.of(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT);
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
        drawLockedDoors(doors, layout);

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
        drawLockedDoors(doors, layout);

        Set<Direction> dirs = EnumSet.of(Direction.LEFT, Direction.RIGHT, Direction.UP, Direction.DOWN);
        Set<RoomType> roomTypes = EnumSet.of(RoomType.SQUARE);

        return new RoomTemplate(w, h, layout, doors, dirs, roomTypes);
    }

    /**
     * [7, 3]
     * Long horizontally oriented room/corridor with doors on left, right, and top.
     */
    private static RoomTemplate makeSmallHorizontalRoom() {
        int w = 9;
        int h = 5;
        TETile[][] layout = new TETile[w][h];

        fill(layout, Tileset.FLOOR);
        addBoundaryWalls(layout);

        // Doors centered vertically on left and right walls.
        Point leftDoor = new Point(0, h / 2);        // (0, 2)
        Point rightDoor = new Point(w - 1, h / 2);      // (8, 2)
        Point topDoor = new Point(w / 2, h - 1);        // (4, 4)
        List<Point> doors = List.of(leftDoor, rightDoor, topDoor);
        drawLockedDoors(doors, layout);

        Set<Direction> dirs = EnumSet.of(Direction.LEFT, Direction.RIGHT, Direction.UP);
        Set<RoomType> roomTypes = EnumSet.of(RoomType.HORIZONTAL);

        return new RoomTemplate(w, h, layout, doors, dirs, roomTypes);
    }


    /**
     * [11, 5]
     * Long horizontally oriented room/corridor with doors on left, right, and bottom.
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
        Point bottomDoor = new Point(w / 2, 0);      // (6, 0)
        List<Point> doors = List.of(leftDoor, rightDoor, bottomDoor);
        drawLockedDoors(doors, layout);

        Set<Direction> dirs = EnumSet.of(Direction.LEFT, Direction.RIGHT, Direction.DOWN);
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
        drawLockedDoors(doors, layout);

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
        drawLockedDoors(doors, layout);

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
     * <p>
     * Doors: (2,7) [UP], (7,2) [RIGHT]
     */
    private static RoomTemplate makeSmallLRoom() {
        int w = 8;
        int h = 8;
        TETile[][] layout = new TETile[w][h];

        fill(layout, Tileset.NOTHING);

        // Vertical leg: x=1..3, y=1..6 (width=3, height=6)
        fillRect(layout, 1, 1, 3, 6, Tileset.FLOOR);

        // Horizontal leg: x=1..6, y=1..3 (width=6, height=3)
        fillRect(layout, 1, 1, 6, 3, Tileset.FLOOR);

        // Add walls around floor tiles
        addWallsAroundFloor(layout);

        // Door positions
        Point upperLeftDoor = new Point(2, 7);  // Middle of upper wall
        Point lowerRightDoor = new Point(7, 2);  // Middle of right wall
        List<Point> doors = List.of(upperLeftDoor, lowerRightDoor);
        drawLockedDoors(doors, layout);

        Set<Direction> dirs = EnumSet.of(Direction.UP, Direction.RIGHT);
        Set<RoomType> roomTypes = EnumSet.of(RoomType.L);

        return new RoomTemplate(w, h, layout, doors, dirs, roomTypes);
    }

    /**
     * Large L-shaped room – thicker version of {@link #makeSmallLRoom()}.
     * <p>
     * Whole box: 12 x 12 (including walls).
     * Interior coordinates: x = 1..10, y = 1..10.
     * - Vertical leg:   x = 1..5,  y = 1..10
     * - Horizontal leg: x = 1..10, y = 1..5
     * So the upper-right 5 x 5 interior (x = 6..10, y = 6..10) stays NOTHING,
     * giving an L of thickness 5.
     * <p>
     * Doors:
     * - (3, 11): top side, centered on the vertical leg  → Direction.UP
     * - (11, 3): right side, centered on the horizontal leg → Direction.RIGHT
     */
    private static RoomTemplate makeBigLRoom() {
        int w = 12;
        int h = 12;
        TETile[][] layout = new TETile[w][h];

        fill(layout, Tileset.NOTHING);

        // Vertical leg: x = 1..5, y = 1..10 (width=5, height=10)
        fillRect(layout, 1, 1, 5, 10, Tileset.FLOOR);

        // Horizontal leg: x = 1..10, y = 1..5 (width=10, height=5)
        fillRect(layout, 1, 1, 10, 5, Tileset.FLOOR);

        // Add walls around the L-shaped floor region
        addWallsAroundFloor(layout);

        // Doors on the top and right sides.
        Point upperLeftDoor = new Point(3, 11);
        Point lowerRightDoor = new Point(11, 3);
        List<Point> doors = List.of(upperLeftDoor, lowerRightDoor);
        drawLockedDoors(doors, layout);

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

        // Vertical leg on right: x=4..6, y=1..6 (width=3, height=6)
        fillRect(layout, 4, 1, 3, 6, Tileset.FLOOR);

        // Horizontal leg on bottom: x=1..6, y=1..3 (width=6, height=3)
        fillRect(layout, 1, 1, 6, 3, Tileset.FLOOR);

        // Add walls around the L-shaped floor region
        addWallsAroundFloor(layout);

        Point upperRightDoor = new Point(5, 7);
        Point lowerLeftDoor = new Point(0, 2);
        List<Point> doors = List.of(upperRightDoor, lowerLeftDoor);
        drawLockedDoors(doors, layout);

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

        // Vertical leg on left: x=1..3, y=1..6 (width=3, height=6)
        fillRect(layout, 1, 1, 3, 6, Tileset.FLOOR);

        // Horizontal leg on top: x=1..6, y=4..6 (width=6, height=3)
        fillRect(layout, 1, 4, 6, 3, Tileset.FLOOR);

        // Add walls around the L-shaped floor region
        addWallsAroundFloor(layout);

        Point lowerLeftDoor = new Point(2, 0);
        Point upperRightDoor = new Point(7, 5);
        List<Point> doors = List.of(lowerLeftDoor, upperRightDoor);
        drawLockedDoors(doors, layout);

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

        // Vertical leg on right: x=4..6, y=1..6 (width=3, height=6)
        fillRect(layout, 4, 1, 3, 6, Tileset.FLOOR);

        // Horizontal leg on top: x=1..6, y=4..6 (width=6, height=3)
        fillRect(layout, 1, 4, 6, 3, Tileset.FLOOR);

        // Add walls around the L-shaped floor region
        addWallsAroundFloor(layout);

        Point lowerRightDoor = new Point(5, 0);
        Point upperLeftDoor = new Point(0, 5);
        List<Point> doors = List.of(lowerRightDoor, upperLeftDoor);
        drawLockedDoors(doors, layout);

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

        // Vertical leg on right: x = 6..10, y = 1..10 (width=5, height=10)
        fillRect(layout, 6, 1, 5, 10, Tileset.FLOOR);

        // Horizontal leg on bottom: x = 1..10, y = 1..5 (width=10, height=5)
        fillRect(layout, 1, 1, 10, 5, Tileset.FLOOR);

        // Add walls around the L-shaped floor region
        addWallsAroundFloor(layout);

        Point upperRightDoor = new Point(8, 11);
        Point lowerLeftDoor = new Point(0, 3);
        List<Point> doors = List.of(upperRightDoor, lowerLeftDoor);
        drawLockedDoors(doors, layout);

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

        // Vertical leg on left: x = 1..5, y = 1..10 (width=5, height=10)
        fillRect(layout, 1, 1, 5, 10, Tileset.FLOOR);

        // Horizontal leg on top: x = 1..10, y = 6..10 (width=10, height=5)
        fillRect(layout, 1, 6, 10, 5, Tileset.FLOOR);

        // Add walls around the L-shaped floor region
        addWallsAroundFloor(layout);

        Point lowerLeftDoor = new Point(3, 0);
        Point upperRightDoor = new Point(11, 8);
        List<Point> doors = List.of(lowerLeftDoor, upperRightDoor);
        drawLockedDoors(doors, layout);

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

        // Vertical leg on right: x = 6..10, y = 1..10 (width=5, height=10)
        fillRect(layout, 6, 1, 5, 10, Tileset.FLOOR);

        // Horizontal leg on top: x = 1..10, y = 6..10 (width=10, height=5)
        fillRect(layout, 1, 6, 10, 5, Tileset.FLOOR);

        // Add walls around the L-shaped floor region
        addWallsAroundFloor(layout);

        Point lowerRightDoor = new Point(8, 0);
        Point upperLeftDoor = new Point(0, 8);
        List<Point> doors = List.of(lowerRightDoor, upperLeftDoor);
        drawLockedDoors(doors, layout);

        Set<Direction> dirs = EnumSet.of(Direction.DOWN, Direction.LEFT);
        Set<RoomType> roomTypes = EnumSet.of(RoomType.L);

        return new RoomTemplate(w, h, layout, doors, dirs, roomTypes);
    }

    /**
     * Small square thin ring-shaped (donut-shaped) room
     * Have one empty space surrounded by wall (total 3 X 3 occupied) in the middle.
     * <p>
     * Doors: (0, 3) [LEFT], (6, 3) [RIGHT], (3, 6) [UP], (3, 0) [DOWN]
     */
    private static RoomTemplate makeSmallSquareThinRingRoom() {
        int w = 7;
        int h = 7;
        TETile[][] layout = new TETile[w][h];

        fill(layout, Tileset.NOTHING);

        // Fill outer layer will wall

        Point leftDoor = new Point(0, 3);
        Point rightDoor = new Point(6, 3);
        Point topDoor = new Point(3, 6);
        Point bottomDoor = new Point(3, 0);
        List<Point> doors = List.of(leftDoor, rightDoor, topDoor, bottomDoor);
        drawLockedDoors(doors, layout);

        Set<Direction> dirs = EnumSet.of(Direction.LEFT, Direction.RIGHT, Direction.UP, Direction.DOWN);
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

    /**
     * Fill a rectangular area in the layout with the provided tile.
     * 
     * @param layout The layout to modify
     * @param startX Starting x coordinate (inclusive)
     * @param startY Starting y coordinate (inclusive)
     * @param width Width of the rectangle
     * @param height Height of the rectangle
     * @param tile The tile to fill with
     */
    private static void fillRect(TETile[][] layout, int startX, int startY, int width, int height, TETile tile) {
        int w = layout.length;
        int h = layout[0].length;
        for (int x = startX; x < startX + width && x < w; x++) {
            for (int y = startY; y < startY + height && y < h; y++) {
                if (x >= 0 && y >= 0) {
                    layout[x][y] = tile;
                }
            }
        }
    }

    /**
     * Create a diamond shape in the layout centered at the given position.
     * The length parameter determines the size: if length=2, the diamond will have
     * 4 vertical and horizontal length (area is 4 * 4 / 2 = 8).
     * 
     * @param layout The layout to modify
     * @param centerX Center x coordinate
     * @param centerY Center y coordinate
     * @param length The length parameter (diamond extends length units in each direction)
     * @param tile The tile to fill with
     */
    private static void createDiamond(TETile[][] layout, int centerX, int centerY, int length, TETile tile) {
        int w = layout.length;
        int h = layout[0].length;
        
        for (int dx = -length; dx <= length; dx++) {
            for (int dy = -length; dy <= length; dy++) {
                if (Math.abs(dx) + Math.abs(dy) <= length) {
                    int x = centerX + dx;
                    int y = centerY + dy;
                    if (x >= 0 && x < w && y >= 0 && y < h) {
                        layout[x][y] = tile;
                    }
                }
            }
        }
    }

    /**
     * Create a ring (donut) shape in the layout.
     * Fills the area between innerRadius and outerRadius from the center.
     * 
     * @param layout The layout to modify
     * @param centerX Center x coordinate
     * @param centerY Center y coordinate
     * @param innerRadius Inner radius (exclusive - this area stays unchanged)
     * @param outerRadius Outer radius (inclusive - fills up to this distance)
     * @param tile The tile to fill with
     */
    private static void fillRing(TETile[][] layout, int centerX, int centerY, int innerRadius, int outerRadius, TETile tile) {
        int w = layout.length;
        int h = layout[0].length;
        
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int dx = x - centerX;
                int dy = y - centerY;
                int dist = Math.max(Math.abs(dx), Math.abs(dy)); // Chebyshev distance for square rings
                if (dist > innerRadius && dist <= outerRadius) {
                    layout[x][y] = tile;
                }
            }
        }
    }

    /**
     * Add walls around all floor tiles in the layout.
     * Any NOTHING tile that is a 4-neighbor to a FLOOR tile becomes WALL.
     */
    private static void addWallsAroundFloor(TETile[][] layout) {
        int w = layout.length;
        int h = layout[0].length;
        
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (layout[x][y] == Tileset.FLOOR) {
                    for (Direction d : Direction.values()) {
                        int nx = x + d.dx;
                        int ny = y + d.dy;
                        if (nx >= 0 && nx < w && ny >= 0 && ny < h) {
                            if (layout[nx][ny] == Tileset.NOTHING) {
                                layout[nx][ny] = Tileset.WALL;
                            }
                        }
                    }
                }
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

    private static void drawLockedDoors(List<Point> doors, TETile[][] layout) {
        for (Point door : doors) {
            layout[door.x][door.y] = Tileset.LOCKED_DOOR;
        }
    }

    /**
     * Add a room type to a given RoomTemplate
     */
    private static void addRoomType(RoomTemplate roomTemplate, RoomType rt) {
        roomTemplate.roomTypes.add(rt);
    }

}
