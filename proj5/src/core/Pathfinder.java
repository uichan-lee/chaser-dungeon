package core;

import tileengine.TETile;
import tileengine.Tileset;

import java.awt.Point;
import java.util.*;

/**
 * Pathfinding utility using BFS algorithm.
 */
public class Pathfinder {
    
    /**
     * Finds the shortest path from start to target using BFS.
     * Returns a list of points representing the path, excluding the start point.
     * Returns empty list if no path exists.
     */
    public static List<Point> findPath(Point start, Point target, TETile[][] world) {
        int width = world.length;
        int height = world[0].length;
        
        // If start or target is out of bounds, return empty path
        if (start.x < 0 || start.x >= width || start.y < 0 || start.y >= height ||
            target.x < 0 || target.x >= width || target.y < 0 || target.y >= height) {
            return new ArrayList<>();
        }
        
        // If start equals target, return empty path (already at destination)
        if (start.equals(target)) {
            return new ArrayList<>();
        }
        
        // BFS setup
        Queue<Point> queue = new LinkedList<>();
        Map<Point, Point> parent = new HashMap<>(); // Maps each point to its parent
        Set<Point> visited = new HashSet<>();
        
        queue.offer(start);
        visited.add(start);
        parent.put(start, null);
        
        // BFS traversal
        while (!queue.isEmpty()) {
            Point current = queue.poll();
            
            // Check all 4 directions
            for (Direction dir : Direction.values()) {
                int nx = current.x + dir.dx;
                int ny = current.y + dir.dy;
                Point next = new Point(nx, ny);
                
                // Check bounds
                if (nx < 0 || nx >= width || ny < 0 || ny >= height) {
                    continue;
                }
                
                // Check if already visited
                if (visited.contains(next)) {
                    continue;
                }
                
                // Check if tile is walkable
                // Special case: allow CHASER and AVATAR tiles to be traversed
                // (they represent entities, not obstacles)
                TETile tile = world[nx][ny];
                if (!isWalkableTile(tile) && 
                    !tile.equals(Tileset.CHASER) && 
                    !tile.equals(Tileset.AVATAR)) {
                    continue;
                }
                
                // Mark as visited and set parent
                visited.add(next);
                parent.put(next, current);
                queue.offer(next);
                
                // If we reached the target, reconstruct path
                if (next.equals(target)) {
                    return reconstructPath(parent, start, target);
                }
            }
        }
        
        // No path found
        return new ArrayList<>();
    }
    
    /**
     * Reconstructs the path from start to target using parent map.
     */
    private static List<Point> reconstructPath(Map<Point, Point> parent, Point start, Point target) {
        List<Point> path = new ArrayList<>();
        Point current = target;
        
        // Trace back from target to start
        while (current != null && !current.equals(start)) {
            path.add(0, new Point(current)); // Add to front to maintain order
            current = parent.get(current);
        }
        
        return path;
    }
    
    /**
     * Checks if a tile is walkable.
     * Walkable tiles are all tiles except unwalkable obstacles.
     * Must match WorldGenerator.isWalkableTile and HUDTest.isWalkableTile for consistency.
     */
    private static boolean isWalkableTile(TETile tile) {
        if (tile == null) {
            return false;
        }
        return !tile.equals(Tileset.AVATAR)
                && !tile.equals(Tileset.WALL)
                && !tile.equals(Tileset.NOTHING)
                && !tile.equals(Tileset.WATER)
                && !tile.equals(Tileset.LOCKED_DOOR)
                && !tile.equals(Tileset.MOUNTAIN)
                && !tile.equals(Tileset.BUSH)
                && !tile.equals(Tileset.TREE)
                && !tile.equals(Tileset.PORTAL)
                && !tile.equals(Tileset.TREASURE)
                && !tile.equals(Tileset.OPENED_CHEST)
                && !tile.equals(Tileset.STATUE)
                && !tile.equals(Tileset.CRATE)
                && !tile.equals(Tileset.BOOKSHELF)
                && !tile.equals(Tileset.SNOWMAN);
    }
}

