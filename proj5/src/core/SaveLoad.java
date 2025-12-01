package core;

import tileengine.TETile;
import tileengine.Tileset;
import utils.FileUtils;

public class SaveLoad {

    private static final String PATH = "savefile.txt";

    public static void save(TETile[][] world, Player p) {
        StringBuilder sb = new StringBuilder();

        sb.append(p.pos.x).append(",").append(p.pos.y).append(",").append(p.facing.name()).append("\n");

        int width = world.length;
        int height = world[0].length;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                sb.append(world[x][y].character());
            }
            sb.append("\n");
        }

        FileUtils.writeFile(PATH, sb.toString());
    }

    public static SaveState load() {
        if (!FileUtils.fileExists(PATH)) return null;

        String[] lines = FileUtils.readFile(PATH).split("\n");

        String[] header = lines[0].split(",");
        int px = Integer.parseInt(header[0]);
        int py = Integer.parseInt(header[1]);
        Direction facing = Direction.valueOf(header[2]);

        int width = lines[1].length();
        int height = lines.length - 1;
        TETile[][] world = new TETile[width][height];

        for (int y = 0; y < height; y++) {
            String row = lines[y + 1];
            for (int x = 0; x < width; x++) {
                world[x][y] = decode(row.charAt(x));
            }
        }

        Player p = new Player(px, py);
        p.facing = facing;

        return new SaveState(world, p);
    }

    private static TETile decode(char c) {
        if (c == Tileset.WALL.character()) return Tileset.WALL;
        if (c == Tileset.FLOOR.character()) return Tileset.FLOOR;
        if (c == Tileset.AVATAR.character()) return Tileset.AVATAR;
        if (c == Tileset.LOCKED_DOOR.character()) return Tileset.LOCKED_DOOR;
        if (c == Tileset.UNLOCKED_DOOR.character()) return Tileset.UNLOCKED_DOOR;
        return Tileset.NOTHING;
    }

    public static class SaveState {
        public final TETile[][] world;
        public final Player player;

        public SaveState(TETile[][] w, Player p) {
            world = w;
            player = p;
        }
    }
}
