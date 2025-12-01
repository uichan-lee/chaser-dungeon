package core;

import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

import java.util.List;

public class World {
    public static void main(String[] args) {
        int WIDTH = 100;
        int HEIGHT = 60;
        long seed = 12345L;

        WorldGenerator gen = new WorldGenerator(WIDTH, HEIGHT, seed);
        TETile[][] world = gen.generate();

        // Make a player at the center
        Player p = new Player(WIDTH / 2, HEIGHT / 2);

        // Replace tile with avatar
        world[p.pos.x][p.pos.y] = Tileset.AVATAR;

        new GameLoop().run(world, p);
    }
}


/*
Testing seeds:
4713243392582896162
6653139552634618902
8808195048176907092
7861102110841635892
5612584838579522635
*/