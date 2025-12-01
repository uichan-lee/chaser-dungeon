package core;

import tileengine.TETile;
import tileengine.TERenderer;
import tileengine.Tileset;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;

public class GameLoop {

    private TERenderer render = new TERenderer();

    public void run(TETile[][] world, Player p) {
        int width = world.length;
        int height = world[0].length;

        render.initialize(width, height);

        while (true) {
            handleInput(world, p);
            render.renderFrame(world);
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
        }
    }

    private void move(Player p, Direction dir, TETile[][] world) {
        p.facing = dir;

        int nx = p.pos.x + dir.dx;
        int ny = p.pos.y + dir.dy;

        if (world[nx][ny].equals(Tileset.FLOOR) || world[nx][ny].equals(Tileset.UNLOCKED_DOOR)) {
            world[p.pos.x][p.pos.y] = Tileset.FLOOR;

            p.pos.setLocation(nx, ny);
            world[nx][ny] = Tileset.AVATAR;
        }
    }

    private void interact(Player p, TETile[][] world) {
        Point f = p.frontTile();
        if (world[f.x][f.y].equals(Tileset.LOCKED_DOOR)) {
            world[f.x][f.y] = Tileset.UNLOCKED_DOOR;
        }
    }
}
