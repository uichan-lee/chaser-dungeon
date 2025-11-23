package core;

import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

import java.util.List;

public class World {
    private static final int WIDTH = 140;
    private static final int HEIGHT = 70;
    private static final List<RoomTemplate> ROOM_TEMPLATES = RoomTemplates.ALL_TEMPLATES;

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        long seed = 184932; // Update needed
        WorldGenerator gen = new WorldGenerator(WIDTH, HEIGHT, seed);
        TETile[][] world = gen.generate();

        ter.renderFrame(world);
    }

}

