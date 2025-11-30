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

//        TETile[][] world = World.generateWorld(seed);
//        ter.renderFrame(world);
    }

    private void drawMenu() {
    StdDraw.clear(StdDraw.BLACK);
    StdDraw.setPenColor(Color.WHITE);
    StdDraw.setFont(new Font("Monaco", Font.BOLD, 40));
    StdDraw.text(width / 2, height / 2 + 10, "CS61B: BYOW");

    StdDraw.setFont(new Font("Monaco", Font.PLAIN, 28));
    StdDraw.text(width / 2, height / 2 - 5, "(N) New Game");
    StdDraw.text(width / 2, height / 2 - 15, "(L) Load Game");
    StdDraw.text(width / 2, height / 2 - 25, "(Q) Quit");
    StdDraw.show();
}
drawMenu();
while (true) {
    if (StdDraw.hasNextKeyTyped()) {
        char c = Character.toUpperCase(StdDraw.nextKeyTyped());
        if (c == 'N') startNewGame();
        if (c == 'L') loadGame();
        if (c == 'Q') System.exit(0);
    }
}

}
