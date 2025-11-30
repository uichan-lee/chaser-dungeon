package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

import java.util.List;

public class WorldTest {
    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;
    private static final List<RoomTemplate> ROOM_TEMPLATES = RoomTemplates.ALL_TEMPLATES;
    private static final long seed = 379;

    public static void main(String[] args) {
//        SingleRoomTest(18);
    }

    private static void customTest() {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        WorldGenerator gen = new WorldGenerator(WIDTH, HEIGHT, seed);
        TETile[][] world = gen.generate();
        ter.renderFrame(world);
    }

    private static void SingleRoomTest(int roomIndex) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        TETile[][] world = new TETile[WIDTH][HEIGHT];

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        Room diamondRoom = new Room(ROOM_TEMPLATES.get(roomIndex), WIDTH / 2, HEIGHT / 2);
        diamondRoom.drawInto(world);

        ter.renderFrame(world);
    }
}

