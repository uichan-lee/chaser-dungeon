package core;

import tileengine.TERenderer;
import tileengine.TETile;

public class Main {
    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        int WIDTH = 80;
        int HEIGHT = 40;

        ter.initialize(WIDTH, HEIGHT);

       
        long seed = 123456789L;

        TETile[][] world = World.generateWorld(seed);
        ter.renderFrame(world);
    }
}
