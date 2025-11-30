package core;

import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

import java.util.List;

public class World {
    private static final int WIDTH = 100;
    private static final int HEIGHT = 60;
    private static final List<RoomTemplate> ROOM_TEMPLATES = RoomTemplates.ALL_TEMPLATES;

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        long seed = 56125848385792635L; 
        WorldGenerator gen = new WorldGenerator(WIDTH, HEIGHT, seed);
        TETile[][] world = gen.generate();

        ter.renderFrame(world);
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