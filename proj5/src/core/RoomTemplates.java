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

        /*
         * =====================================================
         * Templates
         * =====================================================
         */
        // Starting room
        templates.add(makeStartingRoom());  // 0

        // Square rooms
        templates.add(makeSmallSquareRoom()); // 1
        templates.add(makeBigSquareRoom()); // 2

        // Horizontal rooms
        templates.add(makeSmallHorizontalRoom()); // 3
        templates.add(makeBigHorizontalRoom()); // 4

        // Vertical rooms
        templates.add(makeSmallVerticalRoom()); // 5
        templates.add(makeBigVerticalRoom()); // 6

        // Small L-shaped rooms
        templates.add(makeSmallLRoom()); // 7
        templates.add(makeSmallLRoomMirroredH()); // 8
        templates.add(makeSmallLRoomMirroredV()); // 9
        templates.add(makeSmallLRoomRotated180()); // 10

        // Big L-shaped rooms
        templates.add(makeBigLRoom()); // 11
        templates.add(makeBigLRoomMirroredH()); // 12
        templates.add(makeBigLRoomMirroredV()); // 13
        templates.add(makeBigLRoomRotated180()); // 14

        // Ring shaped rooms
        // Small ring-shaped rooms
        templates.add(makeSmallSquareRingRoom()); // 15

        // Big ring-shaped rooms
        templates.add(makeBigSquareRingRoom()); // 16

        // T-shaped rooms
        templates.add(makeSmallTRoom()); // 17

        // Round rooms
         templates.add(makeMediumRoundTreasureRoom()); // 18
         templates.add(makeLargeRoundTreasureRoom()); // 19

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

    /*
     * =====================================================
     * Template definitions
     * =====================================================
     */

    /**
     * [3, 3]
     * <p>
     * Starting room uses 3x3 square.
     * Every new game will start with player in this room.
     * Index in ALL_TEMPLATES: 0.
     */
    private static RoomTemplate makeStartingRoom() {
        int w = 5;
        int h = 5;
        TETile[][] layout = new TETile[w][h];

        fill(layout, Tileset.FLOOR);
        addBoundaryWalls(layout);

        // Doors on all 4 sides
        Point topDoor = new Point(w / 2, h - 1); // (2, 4) - top
        Point bottomDoor = new Point(w / 2, 0); // (2, 0) - bottom
        Point leftDoor = new Point(0, h / 2); // (0, 2) - left
        Point rightDoor = new Point(w - 1, h / 2); // (4, 2) - right
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
     * Index in ALL_TEMPLATES: 1.
     */
    private static RoomTemplate makeSmallSquareRoom() {
        int w = 7;
        int h = 7;
        TETile[][] layout = new TETile[w][h];

        fill(layout, Tileset.SAND);
        addBoundaryWalls(layout);

        // Add decorative elements
        layout[3][3] = Tileset.STATUE;
        layout[1][5] = Tileset.FLOWER;
        layout[5][1] = Tileset.FLOWER;

        // Four doors, one centered on each wall.
        Point leftDoor = new Point(0, h / 2); // left wall center
        Point rightDoor = new Point(w - 1, h / 2); // right wall center
        Point bottomDoor = new Point(w / 2, 0); // bottom wall center
        Point topDoor = new Point(w / 2, h - 1); // top wall center

        List<Point> doors = List.of(leftDoor, rightDoor, bottomDoor, topDoor);
        drawLockedDoors(doors, layout);

        Set<Direction> dirs = EnumSet.of(Direction.LEFT, Direction.RIGHT, Direction.UP, Direction.DOWN);
        Set<RoomType> roomTypes = EnumSet.of(RoomType.SQUARE, RoomType.SMALL);

        return new RoomTemplate(w, h, layout, doors, dirs, roomTypes);
    }

    /**
     * [9, 9]
     * <p>
     * Simple 9×9 square room with walls on the boundary and floor inside.
     * Index in ALL_TEMPLATES: 2.
     */
    private static RoomTemplate makeBigSquareRoom() {
        int w = 11;
        int h = 11;
        TETile[][] layout = new TETile[w][h];

        fill(layout, Tileset.GRASS);
        addBoundaryWalls(layout);

        // Add decorative elements - library/study room
        layout[2][2] = Tileset.BOOKSHELF;
        layout[8][2] = Tileset.BOOKSHELF;
        layout[2][8] = Tileset.BOOKSHELF;
        layout[8][8] = Tileset.BOOKSHELF;
        layout[5][5] = Tileset.STATUE;
        layout[3][5] = Tileset.FLOWER;
        layout[7][5] = Tileset.FLOWER;
        layout[5][3] = Tileset.FLOWER;
        layout[5][7] = Tileset.FLOWER;

        // Four doors, one centered on each wall.
        Point leftDoor = new Point(0, h / 2); // left wall center
        Point rightDoor = new Point(w - 1, h / 2); // right wall center
        Point bottomDoor = new Point(w / 2, 0); // bottom wall center
        Point topDoor = new Point(w / 2, h - 1); // top wall center

        List<Point> doors = List.of(leftDoor, rightDoor, bottomDoor, topDoor);
        drawLockedDoors(doors, layout);

        Set<Direction> dirs = EnumSet.of(Direction.LEFT, Direction.RIGHT, Direction.UP, Direction.DOWN);
        Set<RoomType> roomTypes = EnumSet.of(RoomType.SQUARE, RoomType.BIG);

        return new RoomTemplate(w, h, layout, doors, dirs, roomTypes);
    }

    /**
     * [7, 3]
     * Long horizontally oriented room/corridor with doors on left, right, and top.
     * Index in ALL_TEMPLATES: 3.
     */
    private static RoomTemplate makeSmallHorizontalRoom() {
        int w = 9;
        int h = 5;
        TETile[][] layout = new TETile[w][h];

        fill(layout, Tileset.SAND);
        addBoundaryWalls(layout);

        // Add decorative elements
        layout[2][2] = Tileset.CRATE;
        layout[6][2] = Tileset.CRATE;
        layout[4][1] = Tileset.FLOWER;
        layout[4][3] = Tileset.FLOWER;

        // Doors centered vertically on left and right walls.
        Point leftDoor = new Point(0, h / 2); // (0, 2)
        Point rightDoor = new Point(w - 1, h / 2); // (8, 2)
        Point topDoor = new Point(w / 2, h - 1); // (4, 4)
        List<Point> doors = List.of(leftDoor, rightDoor, topDoor);
        drawLockedDoors(doors, layout);

        Set<Direction> dirs = EnumSet.of(Direction.LEFT, Direction.RIGHT, Direction.UP);
        Set<RoomType> roomTypes = EnumSet.of(RoomType.HORIZONTAL, RoomType.SMALL);

        return new RoomTemplate(w, h, layout, doors, dirs, roomTypes);
    }

    /**
     * [11, 5]
     * Long horizontally oriented room/corridor with doors on left, right, and
     * bottom.
     * Index in ALL_TEMPLATES: 4.
     */
    private static RoomTemplate makeBigHorizontalRoom() {
        int w = 13;
        int h = 7;
        TETile[][] layout = new TETile[w][h];

        fill(layout, Tileset.GRASS);
        addBoundaryWalls(layout);

        // Add trees and flowers along the corridor
        layout[2][3] = Tileset.TREE;
        layout[4][3] = Tileset.FLOWER;
        layout[6][3] = Tileset.TREE;
        layout[8][3] = Tileset.FLOWER;
        layout[10][3] = Tileset.TREE;
        layout[3][2] = Tileset.BUSH;
        layout[9][2] = Tileset.BUSH;
        layout[3][4] = Tileset.BUSH;
        layout[9][4] = Tileset.BUSH;

        // Doors centered vertically on left and right walls.
        Point leftDoor = new Point(0, h / 2); // (0, 3)
        Point rightDoor = new Point(w - 1, h / 2); // (12, 3)
        Point bottomDoor = new Point(w / 2, 0); // (6, 0)
        List<Point> doors = List.of(leftDoor, rightDoor, bottomDoor);
        drawLockedDoors(doors, layout);

        Set<Direction> dirs = EnumSet.of(Direction.LEFT, Direction.RIGHT, Direction.DOWN);
        Set<RoomType> roomTypes = EnumSet.of(RoomType.HORIZONTAL, RoomType.BIG);

        return new RoomTemplate(w, h, layout, doors, dirs, roomTypes);
    }

    /**
     * [3, 7]
     * Long vertically oriented room/corridor with doors on bottom, top, left, and right.
     * Index in ALL_TEMPLATES: 5.
     */
    private static RoomTemplate makeSmallVerticalRoom() {
        int w = 5;
        int h = 9;
        TETile[][] layout = new TETile[w][h];

        fill(layout, Tileset.SNOW);
        addBoundaryWalls(layout);

        // Add decorative elements
        layout[2][2] = Tileset.STATUE;
        layout[2][6] = Tileset.STATUE;
        layout[1][4] = Tileset.FLOWER;
        layout[3][4] = Tileset.FLOWER;

        // Doors centered horizontally on bottom and top walls.
        Point bottomDoor = new Point(w / 2, 0); // (2, 0)
        Point topDoor = new Point(w / 2, h - 1); // (2, 8)
        Point leftDoor = new Point(0, h / 2); // (0, 4)
        Point rightDoor = new Point(w - 1, h / 2); // (4, 4)
        List<Point> doors = List.of(bottomDoor, topDoor, leftDoor, rightDoor);
        drawLockedDoors(doors, layout);

        Set<Direction> dirs = EnumSet.of(Direction.DOWN, Direction.UP, Direction.LEFT, Direction.RIGHT);
        Set<RoomType> roomTypes = EnumSet.of(RoomType.VERTICAL, RoomType.SMALL);

        return new RoomTemplate(w, h, layout, doors, dirs, roomTypes);
    }

    /**
     * [5, 11]
     * Long vertically oriented room/corridor with doors on bottom, top, left, and right.
     * Index in ALL_TEMPLATES: 6.
     */
    private static RoomTemplate makeBigVerticalRoom() {
        int w = 7;
        int h = 13;
        TETile[][] layout = new TETile[w][h];

        fill(layout, Tileset.FLOOR);
        addBoundaryWalls(layout);

        // Add decorative elements along the road
        layout[3][2] = Tileset.STATUE;
        layout[3][6] = Tileset.STATUE;
        layout[3][10] = Tileset.STATUE;
        layout[1][4] = Tileset.TREE;
        layout[5][4] = Tileset.TREE;
        layout[1][8] = Tileset.TREE;
        layout[5][8] = Tileset.TREE;
        layout[2][6] = Tileset.FLOWER;
        layout[4][6] = Tileset.FLOWER;

        // Doors centered horizontally on bottom and top walls.
        Point bottomDoor = new Point(w / 2, 0); // (3, 0)
        Point topDoor = new Point(w / 2, h - 1); // (3, 12)
        Point leftDoor = new Point(0, h / 2); // (0, 6)
        Point rightDoor = new Point(w - 1, h / 2); // (6, 6)
        List<Point> doors = List.of(bottomDoor, topDoor, leftDoor, rightDoor);
        drawLockedDoors(doors, layout);

        Set<Direction> dirs = EnumSet.of(Direction.DOWN, Direction.UP, Direction.LEFT, Direction.RIGHT);
        Set<RoomType> roomTypes = EnumSet.of(RoomType.VERTICAL, RoomType.BIG);

        return new RoomTemplate(w, h, layout, doors, dirs, roomTypes);
    }

    /**
     * Small L-shaped room.
     * Whole Box: 8 x 8 (Including Wall)
     * Inner coordinates: x=1..6, y=1..6
     * - Vertical leg: x=1..3, y=1..6
     * - Horizontal leg: x=1..6, y=1..3
     * => Upper Right 3 x 3 becomes NOTHING
     * <p>
     * Doors: (2,7) [UP], (7,2) [RIGHT], (0, 2) [LEFT]
     * Index in ALL_TEMPLATES: 7.
     */
    private static RoomTemplate makeSmallLRoom() {
        int w = 8;
        int h = 8;
        TETile[][] layout = new TETile[w][h];

        fill(layout, Tileset.NOTHING);

        // Vertical leg: x=1..3, y=1..6 (width=3, height=6)
        fillRect(layout, 1, 1, 3, 6, Tileset.SAND);

        // Horizontal leg: x=1..6, y=1..3 (width=6, height=3)
        fillRect(layout, 1, 1, 6, 3, Tileset.SAND);

        // Add decorative elements
        layout[2][3] = Tileset.CRATE;
        layout[4][2] = Tileset.FLOWER;

        // Add walls around floor tiles
        addWallsAroundFloor(layout, Tileset.SAND);

        // Door positions
        Point upperLeftDoor = new Point(2, 7); // Middle of upper wall
        Point bottomRightDoor = new Point(7, 2); // Middle of right wall
        Point leftDoor = new Point(0, 2);
        List<Point> doors = List.of(upperLeftDoor, bottomRightDoor, leftDoor);
        drawLockedDoors(doors, layout);

        Set<Direction> dirs = EnumSet.of(Direction.UP, Direction.RIGHT, Direction.LEFT);
        Set<RoomType> roomTypes = EnumSet.of(RoomType.L, RoomType.SMALL);

        return new RoomTemplate(w, h, layout, doors, dirs, roomTypes);
    }

    /**
     * Large L-shaped room – thicker version of {@link #makeSmallLRoom()}.
     * <p>
     * Whole box: 12 x 12 (including walls).
     * Interior coordinates: x = 1..10, y = 1..10.
     * - Vertical leg: x = 1..5, y = 1..10
     * - Horizontal leg: x = 1..10, y = 1..5
     * So the upper-right 5 x 5 interior (x = 6..10, y = 6..10) stays NOTHING,
     * giving an L of thickness 5.
     * <p>
     * Doors:
     * - (3, 11): top side, centered on the vertical leg → Direction.UP
     * - (11, 3): right side, centered on the horizontal leg → Direction.RIGHT
     * - (3, 0): bottom side → Direction.DOWN
     * - (0, 3) left side → Direction.LEFT
     * Index in ALL_TEMPLATES: 11.
     */
    private static RoomTemplate makeBigLRoom() {
        int w = 12;
        int h = 12;
        TETile[][] layout = new TETile[w][h];

        fill(layout, Tileset.NOTHING);

        // Vertical leg: x = 1..5, y = 1..10 (width=5, height=10)
        fillRect(layout, 1, 1, 5, 10, Tileset.GRASS);

        // Horizontal leg: x = 1..10, y = 1..5 (width=10, height=5)
        fillRect(layout, 1, 1, 10, 5, Tileset.GRASS);

        // Add decorative elements
        layout[3][3] = Tileset.TREE;
        layout[3][7] = Tileset.TREE;
        layout[7][3] = Tileset.TREE;
        layout[2][2] = Tileset.FLOWER;
        layout[4][2] = Tileset.FLOWER;
        layout[6][2] = Tileset.FLOWER;
        layout[8][2] = Tileset.FLOWER;

        // Add walls around the L-shaped floor region
        addWallsAroundFloor(layout, Tileset.GRASS);

        // Doors on the top and right sides.
        Point upperLeftDoor = new Point(3, 11);
        Point bottomRightDoor = new Point(11, 3);
        Point bottomDoor = new Point(3, 0);
        Point leftDoor = new Point(0, 3);
        List<Point> doors = List.of(upperLeftDoor, bottomRightDoor, bottomDoor, leftDoor);
        drawLockedDoors(doors, layout);

        Set<Direction> dirs = EnumSet.of(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT);
        Set<RoomType> roomTypes = EnumSet.of(RoomType.L, RoomType.SMALL);

        return new RoomTemplate(w, h, layout, doors, dirs, roomTypes);
    }

    /**
     * Small L-shaped room mirrored horizontally (┘ shape).
     * Vertical leg on right, horizontal leg on bottom.
     * Doors: (5, 7) [UP], (0, 2) [LEFT], (5, 0) [DOWN]
     * Index in ALL_TEMPLATES: 8.
     */
    private static RoomTemplate makeSmallLRoomMirroredH() {
        int w = 8;
        int h = 8;
        TETile[][] layout = new TETile[w][h];

        fill(layout, Tileset.NOTHING);

        // Vertical leg on right: x=4..6, y=1..6 (width=3, height=6)
        fillRect(layout, 4, 1, 3, 6, Tileset.SNOW);

        // Horizontal leg on bottom: x=1..6, y=1..3 (width=6, height=3)
        fillRect(layout, 1, 1, 6, 3, Tileset.SNOW);

        // Add decorative elements
        layout[5][3] = Tileset.STATUE;
        layout[3][2] = Tileset.FLOWER;

        // Add walls around the L-shaped floor region
        addWallsAroundFloor(layout, Tileset.SNOW);

        Point upperRightDoor = new Point(5, 7);
        Point lowerLeftDoor = new Point(0, 2);
        Point bottomDoor = new Point(5, 0);
        List<Point> doors = List.of(upperRightDoor, lowerLeftDoor, bottomDoor);
        drawLockedDoors(doors, layout);

        Set<Direction> dirs = EnumSet.of(Direction.UP, Direction.LEFT, Direction.DOWN);
        Set<RoomType> roomTypes = EnumSet.of(RoomType.L, RoomType.SMALL);

        return new RoomTemplate(w, h, layout, doors, dirs, roomTypes);
    }

    /**
     * Small L-shaped room mirrored vertically (┌ shape).
     * Vertical leg on left, horizontal leg on top.
     * Doors: (2, 0) [DOWN], (0, 5) [LEFT], (7, 5) [RIGHT]
     * Index in ALL_TEMPLATES: 9.
     */
    private static RoomTemplate makeSmallLRoomMirroredV() {
        int w = 8;
        int h = 8;
        TETile[][] layout = new TETile[w][h];

        fill(layout, Tileset.NOTHING);

        // Vertical leg on left: x=1..3, y=1..6 (width=3, height=6)
        fillRect(layout, 1, 1, 3, 6, Tileset.SAND);

        // Horizontal leg on top: x=1..6, y=4..6 (width=6, height=3)
        fillRect(layout, 1, 4, 6, 3, Tileset.SAND);

        // Add decorative elements
        layout[2][5] = Tileset.CRATE;
        layout[4][5] = Tileset.FLOWER;
        layout[2][3] = Tileset.FLOWER;

        // Add walls around the L-shaped floor region
        addWallsAroundFloor(layout, Tileset.SAND);

        Point lowerLeftDoor = new Point(2, 0);
        Point upperRightDoor = new Point(7, 5);
        Point upperLeftDoor = new Point(0, 5);
        List<Point> doors = List.of(lowerLeftDoor, upperRightDoor, upperLeftDoor);
        drawLockedDoors(doors, layout);

        Set<Direction> dirs = EnumSet.of(Direction.DOWN, Direction.RIGHT, Direction.LEFT);
        Set<RoomType> roomTypes = EnumSet.of(RoomType.L, RoomType.SMALL);

        return new RoomTemplate(w, h, layout, doors, dirs, roomTypes);
    }

    /**
     * Small L-shaped room rotated 180 degrees (┐ shape).
     * Vertical leg on right, horizontal leg on top.
     * Doors: (5, 0) [DOWN], (0, 5) [LEFT], (5, 7) [UP]
     * Index in ALL_TEMPLATES: 10.
     */
    private static RoomTemplate makeSmallLRoomRotated180() {
        int w = 8;
        int h = 8;
        TETile[][] layout = new TETile[w][h];

        fill(layout, Tileset.NOTHING);

        // Vertical leg on right: x=4..6, y=1..6 (width=3, height=6)
        fillRect(layout, 4, 1, 3, 6, Tileset.GRASS);

        // Horizontal leg on top: x=1..6, y=4..6 (width=6, height=3)
        fillRect(layout, 1, 4, 6, 3, Tileset.GRASS);

        // Add decorative elements
        layout[5][5] = Tileset.TREE;
        layout[3][5] = Tileset.FLOWER;
        layout[5][3] = Tileset.BUSH;

        // Add walls around the L-shaped floor region
        addWallsAroundFloor(layout, Tileset.GRASS);

        Point bottomRightDoor = new Point(5, 0);
        Point upperLeftDoor = new Point(0, 5);
        Point topDoor = new Point(5, 7);
        List<Point> doors = List.of(bottomRightDoor, upperLeftDoor, topDoor);
        drawLockedDoors(doors, layout);

        Set<Direction> dirs = EnumSet.of(Direction.DOWN, Direction.LEFT, Direction.UP);
        Set<RoomType> roomTypes = EnumSet.of(RoomType.L, RoomType.SMALL);

        return new RoomTemplate(w, h, layout, doors, dirs, roomTypes);
    }

    /**
     * Large L-shaped room mirrored horizontally (┘ shape).
     * Vertical leg on right, horizontal leg on bottom.
     * Doors: (8, 11) [UP], (0, 3) [LEFT], (8, 0) [DOWN]
     * Index in ALL_TEMPLATES: 12.
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

        // Add decorative elements
        layout[8][3] = Tileset.STATUE;
        layout[8][7] = Tileset.STATUE;
        layout[4][3] = Tileset.TREE;
        layout[7][3] = Tileset.FLOWER;
        layout[3][3] = Tileset.FLOWER;

        // Add walls around the L-shaped floor region
        addWallsAroundFloor(layout, Tileset.FLOOR);

        Point upperRightDoor = new Point(8, 11);
        Point lowerLeftDoor = new Point(0, 3);
        Point bottomDoor = new Point(8, 0);
        List<Point> doors = List.of(upperRightDoor, lowerLeftDoor, bottomDoor);
        drawLockedDoors(doors, layout);

        Set<Direction> dirs = EnumSet.of(Direction.UP, Direction.LEFT, Direction.DOWN);
        Set<RoomType> roomTypes = EnumSet.of(RoomType.L, RoomType.BIG);

        return new RoomTemplate(w, h, layout, doors, dirs, roomTypes);
    }

    /**
     * Large L-shaped room mirrored vertically (┌ shape).
     * Vertical leg on left, horizontal leg on top.
     * Doors: (3, 0) [DOWN], (3, 11) [UP], (11, 8) [RIGHT]
     * Index in ALL_TEMPLATES: 13.
     */
    private static RoomTemplate makeBigLRoomMirroredV() {
        int w = 12;
        int h = 12;
        TETile[][] layout = new TETile[w][h];

        fill(layout, Tileset.NOTHING);

        // Vertical leg on left: x = 1..5, y = 1..10 (width=5, height=10)
        fillRect(layout, 1, 1, 5, 10, Tileset.SNOW);

        // Horizontal leg on top: x = 1..10, y = 6..10 (width=10, height=5)
        fillRect(layout, 1, 6, 10, 5, Tileset.SNOW);

        // Add decorative elements
        layout[3][3] = Tileset.STATUE;
        layout[3][7] = Tileset.STATUE;
        layout[6][8] = Tileset.FLOWER;
        layout[8][8] = Tileset.FLOWER;

        // Add walls around the L-shaped floor region
        addWallsAroundFloor(layout, Tileset.SNOW);

        Point lowerLeftDoor = new Point(3, 0);
        Point upperRightDoor = new Point(11, 8);
        Point upperDoor = new Point(3, 11);
        List<Point> doors = List.of(lowerLeftDoor, upperRightDoor, upperDoor);
        drawLockedDoors(doors, layout);

        Set<Direction> dirs = EnumSet.of(Direction.DOWN, Direction.RIGHT);
        Set<RoomType> roomTypes = EnumSet.of(RoomType.L, RoomType.BIG);

        return new RoomTemplate(w, h, layout, doors, dirs, roomTypes);
    }

    /**
     * Large L-shaped room rotated 180 degrees (┐ shape).
     * Vertical leg on right, horizontal leg on top.
     * Doors: (8, 0) [DOWN], (0, 8) [LEFT], (11, 3) [RIGHT]
     * Index in ALL_TEMPLATES: 14.
     */
    private static RoomTemplate makeBigLRoomRotated180() {
        int w = 12;
        int h = 12;
        TETile[][] layout = new TETile[w][h];

        fill(layout, Tileset.NOTHING);

        // Vertical leg on right: x = 6..10, y = 1..10 (width=5, height=10)
        fillRect(layout, 6, 1, 5, 10, Tileset.GRASS);

        // Horizontal leg on top: x = 1..10, y = 6..10 (width=10, height=5)
        fillRect(layout, 1, 6, 10, 5, Tileset.GRASS);

        // Add decorative elements
        layout[8][3] = Tileset.TREE;
        layout[8][7] = Tileset.TREE;
        layout[4][8] = Tileset.TREE;
        layout[7][8] = Tileset.TREE;
        layout[3][8] = Tileset.FLOWER;
        layout[6][8] = Tileset.FLOWER;
        layout[9][8] = Tileset.FLOWER;

        // Add walls around the L-shaped floor region
        addWallsAroundFloor(layout, Tileset.GRASS);

        Point bottomRightDoor = new Point(8, 0);
        Point upperLeftDoor = new Point(0, 8);
        Point lowerRightDoor = new Point(11, 3);

        List<Point> doors = List.of(bottomRightDoor, upperLeftDoor, lowerRightDoor);
        drawLockedDoors(doors, layout);

        Set<Direction> dirs = EnumSet.of(Direction.DOWN, Direction.LEFT, Direction.RIGHT);
        Set<RoomType> roomTypes = EnumSet.of(RoomType.L, RoomType.BIG);

        return new RoomTemplate(w, h, layout, doors, dirs, roomTypes);
    }

    /**
     * Small square thin ring-shaped (donut-shaped) room
     * Have one empty space surrounded by wall (total 3 X 3 occupied) in the middle.
     * <p>
     * Doors: (0, 3) [LEFT], (6, 3) [RIGHT], (3, 6) [UP], (3, 0) [DOWN]
     * Index in ALL_TEMPLATES: 15.
     */
    private static RoomTemplate makeSmallSquareRingRoom() {
        int w = 7;
        int h = 7;
        TETile[][] layout = new TETile[w][h];

        fill(layout, Tileset.NOTHING);

        // Fill outer layer will wall
        fillRing(layout, w / 2, h / 2, 1, 2, Tileset.SAND);
        fillRing(layout, w / 2, h / 2, 2, 3, Tileset.WALL); // outer wall
        fillRing(layout, w / 2, h / 2, 0, 1, Tileset.WALL); // inner wall
        
        // Add decorative elements
        // Removed STATUE at (1, 3) and (5, 3) - they are directly in front of doors
        layout[3][1] = Tileset.FLOWER;
        layout[3][5] = Tileset.FLOWER;

        Point leftDoor = new Point(0, 3);
        Point rightDoor = new Point(6, 3);
        Point topDoor = new Point(3, 6);
        Point bottomDoor = new Point(3, 0);
        List<Point> doors = List.of(leftDoor, rightDoor, topDoor, bottomDoor);
        drawLockedDoors(doors, layout);

        Set<Direction> dirs = EnumSet.of(Direction.LEFT, Direction.RIGHT, Direction.UP, Direction.DOWN);
        Set<RoomType> roomTypes = EnumSet.of(RoomType.RING, RoomType.SMALL);

        return new RoomTemplate(w, h, layout, doors, dirs, roomTypes);
    }

    /**
     * Large square ring-shaped room with a wider walkway around the center void.
     * Index in ALL_TEMPLATES: 16.
     */
    private static RoomTemplate makeBigSquareRingRoom() {
        int w = 13;
        int h = 13;
        TETile[][] layout = new TETile[w][h];

        fill(layout, Tileset.NOTHING);

        fillRing(layout, w / 2, h / 2, 2, 5, Tileset.GRASS);
        fillRing(layout, w / 2, h / 2, 5, 6, Tileset.WALL); // outer wall
        fillRing(layout, w / 2, h / 2, 1, 2, Tileset.WALL); // inner wall
        
        // Add decorative elements
        layout[2][6] = Tileset.TREE;
        layout[10][6] = Tileset.TREE;
        layout[6][2] = Tileset.TREE;
        layout[6][10] = Tileset.TREE;
        layout[4][6] = Tileset.STATUE;
        layout[8][6] = Tileset.STATUE;
        layout[6][4] = Tileset.FLOWER;
        layout[6][8] = Tileset.FLOWER;

        Point leftDoor = new Point(0, 6);
        Point rightDoor = new Point(12, 6);
        Point topDoor = new Point(6, 0);
        Point bottomDoor = new Point(6, 12);

        List<Point> doors = List.of(leftDoor, rightDoor, topDoor, bottomDoor);
        drawLockedDoors(doors, layout);

        Set<Direction> dirs = EnumSet.of(Direction.LEFT, Direction.RIGHT, Direction.UP, Direction.DOWN);
        Set<RoomType> roomTypes = EnumSet.of(RoomType.RING, RoomType.BIG);

        return new RoomTemplate(w, h, layout, doors, dirs, roomTypes);
    }

    /**
     * T-shaped room.
     * Width = 7, Height = 8
     * <p>
     * Has a horizontal bar at the top and a vertical stem going down.
     * Doors: (2, 0) [DOWN], (0, 6) [LEFT], (6, 6) [RIGHT]
     * Index in ALL_TEMPLATES: 17.
     */
    private static RoomTemplate makeSmallTRoom() {
        int w = 11;
        int h = 8;
        TETile[][] layout = new TETile[w][h];

        fill(layout, Tileset.NOTHING);

        // Horizontal bar at top: x=1 to x=9, y=4 to y=6
        fillRect(layout, 1, 4, 9, 3, Tileset.FLOOR);

        // Square at the bottom: x=4 to x=6, y=1 to y=6
        fillRect(layout, 4, 1, 3, 3, Tileset.FLOOR);

        // Add decorative elements
        layout[3][5] = Tileset.STATUE;
        layout[7][5] = Tileset.STATUE;
        layout[5][2] = Tileset.FLOWER;
        layout[2][5] = Tileset.TREE;
        layout[8][5] = Tileset.TREE;
        
        // Add walls around floor tiles
        addWallsAroundFloor(layout, Tileset.FLOOR);

        // Doors at specified positions
        Point bottomDoor = new Point(5, 0); // Bottom center
        Point leftDoor = new Point(0, 5);   // Left side of horizontal bar
        Point rightDoor = new Point(10, 5); // Right side of horizontal bar
        List<Point> doors = List.of(bottomDoor, leftDoor, rightDoor);
        drawLockedDoors(doors, layout);

        Set<Direction> dirs = EnumSet.of(Direction.DOWN, Direction.LEFT, Direction.RIGHT);
        Set<RoomType> roomTypes = EnumSet.of(RoomType.T, RoomType.SMALL);

        return new RoomTemplate(w, h, layout, doors, dirs, roomTypes);
    }

    /**
     * Medium round room (diameter 10).
     * Index in ALL_TEMPLATES: 18.
     * Treasure in the middle of the room.
     */
    private static RoomTemplate makeMediumRoundTreasureRoom() {
        int radius = 5;
        int w = radius * 2 - 1; // 9
        int h = w;
        int cx = radius - 1;
        int cy = radius - 1;

        TETile[][] layout = new TETile[w][h];
        fill(layout, Tileset.NOTHING);

        fillCircle(layout, cx, cy, radius, Tileset.WALL);
        fillCircle(layout, cx, cy, radius - 1, Tileset.SNOW);

        // Add treasure in the middle
        layout[w/2][h/2] = Tileset.TREASURE;

        // 4 Pilars around the treasure
        for (int x = w/2 - 1; x <= w/2 + 1; x += 2) {
            for (int y = h/2 - 1; y <= h/2 + 1; y += 2) {
                layout[x][y] = Tileset.WALL;
            }
        }

        Point topDoor = new Point(cx, h - 1);
        Point bottomDoor = new Point(cx, 0);
        Point leftDoor = new Point(0, cy);
        Point rightDoor = new Point(w - 1, cy);
        List<Point> doors = List.of(topDoor, bottomDoor, leftDoor, rightDoor);
        drawLockedDoors(doors, layout);

        Set<Direction> dirs = EnumSet.of(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT);
        Set<RoomType> roomTypes = EnumSet.of(RoomType.ROUND, RoomType.TREASURE);

        return new RoomTemplate(w, h, layout, doors, dirs, roomTypes);
    }

    /**
     * Large round room (diameter 14).
     * Index in ALL_TEMPLATES: 19.
     * Treasure in the middle.
     * Grass in the inner wall.
     */
    private static RoomTemplate makeLargeRoundTreasureRoom() {
        int radius = 7;
        int w = radius * 2 - 1; // 13
        int h = w;
        int cx = radius - 1;
        int cy = radius - 1;

        TETile[][] layout = new TETile[w][h];
        fill(layout, Tileset.NOTHING);

        // Outer wall
        fillCircle(layout, cx, cy, radius, Tileset.WALL);
        fillCircle(layout, cx, cy, radius - 1, Tileset.FLOOR);

        // Inner wall
        fillCircle(layout, cx, cy, radius - 3, Tileset.WALL);
        fillCircle(layout, cx, cy, radius - 4, Tileset.GRASS);
        layout[2][h / 2] = Tileset.FLOOR;
        layout[radius - 1][2] = Tileset.FLOOR;
        layout[radius - 1][h - 3] = Tileset.FLOOR;
        layout[w - 3][h / 2] = Tileset.FLOOR;

        // Treasure in the middle
        layout[w/2][h/2] = Tileset.TREASURE;

        Point topDoor = new Point(cx, h - 1);
        Point bottomDoor = new Point(cx, 0);
        Point leftDoor = new Point(0, cy);
        Point rightDoor = new Point(w - 1, cy);
        List<Point> doors = List.of(topDoor, bottomDoor, leftDoor, rightDoor);
        drawLockedDoors(doors, layout);

        Set<Direction> dirs = EnumSet.of(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT);
        Set<RoomType> roomTypes = EnumSet.of(RoomType.BIG, RoomType.ROUND, RoomType.TREASURE);

        return new RoomTemplate(w, h, layout, doors, dirs, roomTypes);
    }

    /*
     * =====================================================
     * Helper methods
     * =====================================================
     */

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
     * @param width  Width of the rectangle
     * @param height Height of the rectangle
     * @param tile   The tile to fill with
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
     * Create a ring (donut) shape in the layout.
     * Fills the area between innerRadius and outerRadius from the center.
     * 
     * @param layout      The layout to modify
     * @param centerX     Center x coordinate
     * @param centerY     Center y coordinate
     * @param innerRadius Inner radius (exclusive - this area stays unchanged)
     * @param outerRadius Outer radius (inclusive - fills up to this distance)
     * @param tile        The tile to fill with
     */
    private static void fillRing(TETile[][] layout, int centerX, int centerY, int innerRadius, int outerRadius,
            TETile tile) {
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

    private static void fillCircle(TETile[][] layout, int centerX, int centerY, int radius, TETile tile) {
        if (radius < 0) {
            return;
        }
        int w = layout.length;
        int h = layout[0].length;

        int radiusSq = radius * radius;
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int dx = x - centerX;
                int dy = y - centerY;
                if (dx * dx + dy * dy <= radiusSq) {
                    layout[x][y] = tile;
                }
            }
        }
    }

    /**
     * Add walls around all floor tiles in the layout.
     * Any NOTHING tile that is a 4-neighbor to the specified floor tile becomes WALL.
     * 
     * @param layout The layout to modify
     * @param floorTile The tile type to treat as floor (e.g., Tileset.FLOOR, Tileset.SAND, Tileset.GRASS, Tileset.SNOW)
     */
    private static void addWallsAroundFloor(TETile[][] layout, TETile floorTile) {
        int w = layout.length;
        int h = layout[0].length;

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (layout[x][y] == floorTile) {
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

}
