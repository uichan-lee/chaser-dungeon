package core;

import tileengine.TETile;
import tileengine.Tileset;
import utils.RandomUtils;

import java.awt.Point;
import java.util.*;

/**
 * Generates a world composed of rooms connected by randomly
 * turning corridors, starting from the starting room at (1, 1).
 * <p>
 * High-level:
 * - Place starting room at (1, 1).
 * - From each room door, build a random corridor (length 3–10, 0–2 turns).
 * - Look at the corridor's final direction and attach a room whose door
 * is compatible with that direction.
 * - Only commit corridor+room if everything fits, doesn’t overlap, and
 * stays off the outer border.
 */
public class WorldGenerator {

    // Number of tiles reserved at the top for HUD (must match actual HUD drawing height)
    private static final int HUD_HEIGHT = 2;

    private final int width;
    private final int height;
    private final TETile[][] world;
    private final Random rand;

    private final List<Room> rooms = new ArrayList<>();
    /**
     * All floor tiles already used by rooms and corridors.
     */
    private final Set<Point> floorOccupied = new HashSet<>();
    /**
     * World positions of doors that successfully connected to another room.
     */
    private final Set<Point> connectedDoors = new HashSet<>();

    // Tunable parameters
    private static final int MIN_CORRIDOR_LEN = 3;
    private static final int MAX_CORRIDOR_LEN = 5;
    private static final int MAX_TURNS = 3;
    private static final int MAX_EXPANSION_DEPTH = 99; // max recursion / room depth
    private static final int MAX_TRIES_PER_DOOR = 5;


    public WorldGenerator(int width, int height, long seed) {
        this.width = width;
        this.height = height;
        this.world = new TETile[width][height];
        this.rand = new Random(seed);
        fillWithNothing();
    }

    private void fillWithNothing() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                world[x][y] = Tileset.NOTHING;
            }
<<<<<<< HEAD
        }
    }
    
=======

    }


    }
    public List<Room> getRooms() {
        return rooms;
    }
>>>>>>> anikethinteractivity
    /**
     * Entry point: build the world and return the tile map.
     */
    public TETile[][] generate() {
        Room start = placeStartingRoom();
        rooms.add(start);
        drawRoom(start);

        // Place avatar in the center of the starting room
        int avatarX = start.worldX + start.template.width / 2;
        int avatarY = start.worldY + start.template.height / 2;
        world[avatarX][avatarY] = Tileset.AVATAR;

        growFromRoom(start, 0);

        // Update door tiles based on whether they're connected
        updateDoorTiles();

        return world;
    }

    /**
     * Place the starting room in the center of the world. Assumes template 0 is STARTING.
     */
    private Room placeStartingRoom() {
        RoomTemplate t = RoomTemplates.ALL_TEMPLATES.get(0); // makeStartingRoom()
        // Center the room in the usable world area (exclude HUD region at top)
        int usableHeight = height - HUD_HEIGHT;
        int worldX = (width - t.width) / 2;
        int worldY = (usableHeight - t.height) / 2;
        Room r = new Room(t, worldX, worldY);
        registerRoomFloors(r);
        return r;
    }

    /* =====================================================
     *  Recursive expansion
     * ===================================================== */


    private void growFromRoom(Room room, int depth) {
        if (depth >= MAX_EXPANSION_DEPTH) {
            return;
        }

        List<Point> doorWorlds = room.getWorldDoorPositions();
        for (Point doorWorld : doorWorlds) {
            Direction dir = directionForDoor(room, doorWorld);

            for (int attempt = 0; attempt < MAX_TRIES_PER_DOOR; attempt++) {
                CorridorResult corridor = buildRandomCorridorFrom(doorWorld, dir);
                if (corridor == null) {
                    continue;  // Fail making door, another trial.
                }

                Direction neededDoorDir = corridor.lastDir.opposite();
                List<RoomTemplate> candidates = RoomTemplates.BY_DIRECTION.get(neededDoorDir);
                if (candidates == null || candidates.isEmpty()) {
                    continue;
                }

                Room nextRoom = tryPlaceRandomRoomAtCorridorEnd(corridor, candidates, neededDoorDir);
                if (nextRoom == null) {
                    continue;
                }

                // Success → Mark both doors as connected
                connectedDoors.add(new Point(doorWorld)); // Source door
                connectedDoors.add(new Point(corridor.end)); // Destination door
                
                drawCorridor(corridor);
                rooms.add(nextRoom);
                drawRoom(nextRoom);
                growFromRoom(nextRoom, depth + 1);
                break;
            }
        }
    }


    /**
     * Determine which side of the room this door is on,
     * based on its local coordinate within the template.
     */
    private Direction directionForDoor(Room room, Point doorWorld) {
        int localX = doorWorld.x - room.worldX;
        int localY = doorWorld.y - room.worldY;
        int w = room.template.width;
        int h = room.template.height;

        if (localY == h - 1) {
            return Direction.UP;
        } else if (localY == 0) {
            return Direction.DOWN;
        } else if (localX == 0) {
            return Direction.LEFT;
        } else if (localX == w - 1) {
            return Direction.RIGHT;
        } else {
            throw new IllegalStateException(
                    "Door is not on boundary: local=(" + localX + "," + localY + ")");
        }
    }

    /* =====================================================
     *  Corridor generation
     * ===================================================== */

    /**
     * Build a random corridor path from 'start' in initial direction 'startDir'.
     * - length 3–10 per segment
     * - 0–2 turns
     * - last direction is never the exact opposite of the initial direction
     * Returns null if corridor would go out of bounds or hit existing floor.
     */
    private CorridorResult buildRandomCorridorFrom(Point start, Direction startDir) {
        int numTurns = RandomUtils.uniform(rand, MAX_TURNS + 1); // 0, 1, or 2
        int numSegments = numTurns + 1;

        List<Point> path = new ArrayList<>();
        Direction initialDir = startDir;
        Direction currDir = startDir;
        Point curr = new Point(start);

        for (int seg = 0; seg < numSegments; seg++) {
            int len = randomInRange(MIN_CORRIDOR_LEN, MAX_CORRIDOR_LEN);

            for (int i = 0; i < len; i++) {
                curr = step(curr, currDir);

                // Stay off outer border
                if (!isInsideFloorBounds(curr)) {
                    return null;
                }
                // Do not pass through existing floor
                if (floorOccupied.contains(curr)) {
                    return null;
                }
                // Do not carve through blocking tiles like walls/doors
                TETile existing = world[curr.x][curr.y];
                if (isBlockingTile(existing)) {
                    return null;
                }

                // Add copy of curr
                path.add(new Point(curr));
            }

            // Turn between segments
            if (seg < numSegments - 1) {
                List<Direction> candidates = turnCandidates(currDir);
                // Just before final segment, forbid turning into the opposite of the initial direction
                if (seg == numSegments - 2) {
                    candidates.remove(initialDir.opposite());
                }
                if (candidates.isEmpty()) {
                    return null;
                }
                currDir = randomChoice(candidates);
            }
        }

        if (path.isEmpty()) {
            return null;
        }

        Point end = path.getLast();
        return new CorridorResult(end, currDir, path);
    }

    private Point step(Point p, Direction dir) {
        return new Point(p.x + dir.dx, p.y + dir.dy);
    }

    /**
     * Check if a point is not on the edge and stays below the HUD area.
     */
    private boolean isInsideFloorBounds(Point p) {
        return p.x > 0 && p.x < width - 1
                && p.y > 0 && p.y < height - HUD_HEIGHT;
    }

    /**
     * Current direction → 90-degree turn candidates.
     * UP/DOWN → LEFT/RIGHT, LEFT/RIGHT → UP/DOWN
     */
    private List<Direction> turnCandidates(Direction dir) {
        List<Direction> res = new ArrayList<>();
        switch (dir) {
            case UP:
            case DOWN:
                res.add(Direction.LEFT);
                res.add(Direction.RIGHT);
                break;
            case LEFT:
            case RIGHT:
                res.add(Direction.UP);
                res.add(Direction.DOWN);
                break;
        }
        return res;
    }

    private int randomInRange(int lo, int hiInclusive) {
        // returns integer in [lo, hiInclusive]
        if (hiInclusive < lo) {
            throw new IllegalArgumentException("hiInclusive < lo: " + lo + ", " + hiInclusive);
        }
        // RandomUtils.uniform(random, a, b) is [a, b), so use hiInclusive + 1
        return RandomUtils.uniform(rand, lo, hiInclusive + 1);
    }

    private <T> T randomChoice(List<T> list) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException("Cannot choose from empty list");
        }
        int idx = RandomUtils.uniform(rand, list.size());
        return list.get(idx);
    }

    /**
     * Draw corridor:
     * - path tiles become FLOOR
     * - any adjacent NOTHING tiles become WALL (to give a 1-tile wide hallway)
     */
    private void drawCorridor(CorridorResult corridor) {
        for (Point p : corridor.floorPath) {
            world[p.x][p.y] = Tileset.FLOOR;
            floorOccupied.add(new Point(p));

            // Surround with walls (if still NOTHING)
            for (Direction d : Direction.values()) {
                int nx = p.x + d.dx;
                int ny = p.y + d.dy;
                if (nx < 0 || nx >= width || ny < 0 || ny >= height) {
                    continue;
                }
                if (world[nx][ny] == Tileset.NOTHING) {
                    world[nx][ny] = Tileset.WALL;
                }
            }
        }
    }

    /* =====================================================
     *  Attach room at corridor end
     * ===================================================== */

    /**
     * Try to attach a random room at the end of this corridor.
     * Uses candidates that are already filtered by direction via RoomTemplates.BY_DIRECTION.
     * Returns a placed Room or null if nothing fits.
     */
    private Room tryPlaceRandomRoomAtCorridorEnd(CorridorResult corridor,
                                                 List<RoomTemplate> candidates,
                                                 Direction neededDoorDir) {

        List<RoomTemplate> shuffled = new ArrayList<>(candidates);
        Object[] arr = shuffled.toArray();
        RandomUtils.shuffle(rand, arr);
        // write back the shuffled order into the list
        for (int i = 0; i < arr.length; i++) {
            //noinspection unchecked
            shuffled.set(i, (RoomTemplate) arr[i]);
        }

        Point end = corridor.end;

        for (RoomTemplate t : shuffled) {
            if (t.doorPositions.isEmpty()) {
                continue;
            }

            // Collect all door positions in this template that face neededDoorDir
            List<Point> matchingDoors = new ArrayList<>();
            for (Point local : t.doorPositions) {
                Direction doorDir = directionForLocalDoor(t, local);
                if (doorDir == neededDoorDir) {
                    matchingDoors.add(local);
                }
            }

            if (matchingDoors.isEmpty()) {
                // Template was listed as a candidate but has no door actually facing neededDoorDir
                continue;
            }

            // Choose one of the matching doors at random
            Point localDoor = randomChoice(matchingDoors);

            // Align this local door with the corridor end.
            int worldX = end.x - localDoor.x;
            int worldY = end.y - localDoor.y;

            Room candidate = new Room(t, worldX, worldY);

            // 1) fits within world
            if (!candidate.fitsWithinWorld(width, height)) {
                continue;
            }

            // 2) doesn't overlap existing rooms (bounding box check)
            if (candidate.overlapsAny(rooms)) {
                continue;
            }

            // 3) doesn't overlap existing floor tiles
            if (!roomFloorsDisjoint(candidate)) {
                continue;
            }

            // 4) doesn't overlap any non-NOTHING tiles (walls, floors, doors, etc.)
            if (!roomTilesDisjoint(candidate)) {
                continue;
            }

            // Valid placement
            registerRoomFloors(candidate);
            return candidate;
        }
        return null;
    }

    /**
     * Determine which side of the template this local door position lies on.
     */
    private Direction directionForLocalDoor(RoomTemplate t, Point localDoor) {
        int x = localDoor.x;
        int y = localDoor.y;
        int w = t.width;
        int h = t.height;

        if (y == h - 1) {
            return Direction.UP;
        } else if (y == 0) {
            return Direction.DOWN;
        } else if (x == 0) {
            return Direction.LEFT;
        } else if (x == w - 1) {
            return Direction.RIGHT;
        } else {
            throw new IllegalStateException(
                    "Door is not on boundary of template: local=(" + x + "," + y + ")");
        }
    }

    private boolean roomFloorsDisjoint(Room room) {
        for (int dx = 0; dx < room.template.width; dx++) {
            for (int dy = 0; dy < room.template.height; dy++) {
                TETile tile = room.template.layout[dx][dy];
                if (isWalkableTile(tile)) {
                    Point p = new Point(room.worldX + dx, room.worldY + dy);
                    if (floorOccupied.contains(p)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Checks if the candidate room's non-NOTHING tiles don't overlap
     * with any existing non-NOTHING tiles from placed rooms or the world.
     * This prevents walls, floors, and doors from overlapping.
     */
    private boolean roomTilesDisjoint(Room room) {
        for (int dx = 0; dx < room.template.width; dx++) {
            for (int dy = 0; dy < room.template.height; dy++) {
                TETile candidateTile = room.template.layout[dx][dy];
                // Skip NOTHING tiles - they can overlap
                if (candidateTile == Tileset.NOTHING) {
                    continue;
                }
                
                int worldX = room.worldX + dx;
                int worldY = room.worldY + dy;
                
                // Check against existing rooms' tiles
                for (Room existingRoom : rooms) {
                    int localX = worldX - existingRoom.worldX;
                    int localY = worldY - existingRoom.worldY;
                    
                    // Check if this world position is within the existing room's bounds
                    if (localX >= 0 && localX < existingRoom.template.width &&
                        localY >= 0 && localY < existingRoom.template.height) {
                        TETile existingTile = existingRoom.template.layout[localX][localY];
                        // If both tiles are non-NOTHING, they overlap
                        if (existingTile != Tileset.NOTHING) {
                            return false; // Overlap detected
                        }
                    }
                }
                
                // Also check against what's already in the world (for corridors)
                TETile worldTile = world[worldX][worldY];
                if (worldTile != Tileset.NOTHING) {
                    return false; // Overlap detected
                }
            }
        }
        return true;
    }

    private boolean isWalkableTile(TETile tile) {
        if (tile == null) {
            return false;
        }
        return tile != Tileset.AVATAR
                && tile != Tileset.WALL
                && tile != Tileset.NOTHING
                && tile != Tileset.WATER
                && tile != Tileset.LOCKED_DOOR
                && tile != Tileset.MOUNTAIN
                && tile != Tileset.TREE;
    }

    private boolean isBlockingTile(TETile tile) {
        if (tile == null) {
            return false;
        }
        return tile == Tileset.AVATAR
                || tile == Tileset.WALL
                || tile == Tileset.WATER
                || tile == Tileset.LOCKED_DOOR
                || tile == Tileset.UNLOCKED_DOOR
                || tile == Tileset.MOUNTAIN
                || tile == Tileset.TREE;
    }

    private void registerRoomFloors(Room room) {
        for (int dx = 0; dx < room.template.width; dx++) {
            for (int dy = 0; dy < room.template.height; dy++) {
                TETile tile = room.template.layout[dx][dy];
                if (isWalkableTile(tile)) {
                    floorOccupied.add(new Point(room.worldX + dx, room.worldY + dy));
                }
            }
        }
    }

    private void drawRoom(Room room) {
        room.drawInto(world);
        // If drawInto doesn't update floorOccupied,
        // registerRoomFloors(room) already did that on placement.
    }

    /**
     * Updates all door tiles in the world:
     * - Doors that are in connectedDoors become UNLOCKED_DOOR
     * - Doors that are not in connectedDoors become WALL
     */
    private void updateDoorTiles() {
        for (Room room : rooms) {
            List<Point> doorWorlds = room.getWorldDoorPositions();
            for (Point doorWorld : doorWorlds) {
                if (connectedDoors.contains(doorWorld)) {
                    world[doorWorld.x][doorWorld.y] = Tileset.UNLOCKED_DOOR;
                } else {
                    world[doorWorld.x][doorWorld.y] = Tileset.LOCKED_DOOR;
                }
            }
        }
    }



    /* =====================================================
     *  Helper: corridor result
     * ===================================================== */

    private static class CorridorResult {
        final Point end;              // last tile of corridor (where room attaches)
        final Direction lastDir;      // direction of the last segment
        final List<Point> floorPath;  // all floor tiles in corridor

        CorridorResult(Point end, Direction lastDir, List<Point> floorPath) {
            this.end = end;
            this.lastDir = lastDir;
            this.floorPath = floorPath;
        }
    }
}
