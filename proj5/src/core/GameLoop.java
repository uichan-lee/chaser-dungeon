package core;

import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.Point;

public class GameLoop {

    private TERenderer renderer = new TERenderer();

    public void run(TETile[][] world, Player p) {
        int width = world.length;
        int height = world[0].length;

        renderer.initialize(width, height);

        while (true) {
            handleInput(world, p);
            renderer.renderFrame(world);
        }
    }

    private void handleInput(TETile[][] world, Player p) {
        if (!StdDraw.hasNextKeyTyped()) return;

        char c = Character.toUpperCase(StdDraw.nextKeyTyped());
        switch (c) {
            case 'W' -> move(p, Direction.UP, world);
            case 'A' -> move(p, Direction.LEFT, world);
            case 'S' -> move(p, Direction.DOWN, world);
            case 'D' -> move(p, Direction.RIGHT, world);
            case 'I' -> interact(p, world);
            case ':' -> {
                while (!StdDraw.hasNextKeyTyped()) { }
                char q = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (q == 'Q') {
                    SaveLoad.save(world, p);
                    System.exit(0);
                }
            }
        }
    }

    private void move(Player p, Direction dir, TETile[][] world) {
        p.facing = dir;

        int nx = p.pos.x + dir.dx;
        int ny = p.pos.y + dir.dy;

        // Allowed floor types
        if (world[nx][ny] == Tileset.FLOOR || world[nx][ny] == Tileset.UNLOCKED_DOOR) {

            // clear old location
            world[p.pos.x][p.pos.y] = Tileset.FLOOR;

            // move player
            p.pos = new Point(nx, ny);

            // place avatar tile
            world[nx][ny] = Tileset.AVATAR;
        }
    }

    private void interact(Player p, TETile[][] world) {
        Point f = p.frontTile();
        if (world[f.x][f.y] == Tileset.LOCKED_DOOR) {
            world[f.x][f.y] = Tileset.UNLOCKED_DOOR;
        }
    }
}
