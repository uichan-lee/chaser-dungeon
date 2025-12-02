package core;

import tileengine.TETile;
import tileengine.Tileset;
import utils.FileUtils;

/**
 * Handles saving and loading game state to/from a file.
 */
public class SaveLoad {

    private static final String PATH = "savefile.txt";

    /**
     * Saves the game state to a file.
     * 
     * @param world the world tile map
     * @param p the player
     * @param chaser the chaser (can be null)
     */
    public static void save(TETile[][] world, Player p, Chaser chaser) {
        StringBuilder sb = new StringBuilder();

        // Save player position, facing, tileUnderPlayer, and pushAbilityCount
        sb.append(p.pos.x).append(",").append(p.pos.y).append(",")
          .append(p.facing.name()).append(",")
          .append(p.tileUnderPlayer.character()).append(",")
          .append(p.pushAbilityCount);
        
        // Save chaser position and tileUnderChaser if chaser exists
        if (chaser != null) {
            sb.append(",").append(chaser.pos.x).append(",").append(chaser.pos.y)
              .append(",").append(chaser.tileUnderChaser.character());
        }
        sb.append("\n");

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

    /**
     * Loads the game state from a file.
     * 
     * @return SaveState containing world, player, and chaser, or null if file doesn't exist
     */
    public static SaveState load() {
        if (!FileUtils.fileExists(PATH)) return null;

        String[] lines = FileUtils.readFile(PATH).split("\n");

        String[] header = lines[0].split(",");
        int px = Integer.parseInt(header[0]);
        int py = Integer.parseInt(header[1]);
        Direction facing = Direction.valueOf(header[2]);
        char tileUnderPlayerChar = header.length > 3 ? header[3].charAt(0) : Tileset.FLOOR.character();
        int pushAbilityCount = header.length > 4 ? Integer.parseInt(header[4]) : 1; // Default to 1 for old saves

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
        p.tileUnderPlayer = decode(tileUnderPlayerChar);
        p.pushAbilityCount = pushAbilityCount;
        
        // Load chaser from header if present (new format), otherwise find in world (old format)
        Chaser chaser = null;
        if (header.length >= 8) {
            // New format: chaser position and tileUnderChaser are in header
            int chaserX = Integer.parseInt(header[5]);
            int chaserY = Integer.parseInt(header[6]);
            char tileUnderChaserChar = header[7].charAt(0);
            chaser = new Chaser(chaserX, chaserY);
            chaser.tileUnderChaser = decode(tileUnderChaserChar);
        } else {
            // Old format: find chaser in world for backward compatibility
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (world[x][y] != null && world[x][y].equals(Tileset.CHASER)) {
                        chaser = new Chaser(x, y);
                        chaser.tileUnderChaser = Tileset.FLOOR; // Default for old saves
                        break;
                    }
                }
                if (chaser != null) break;
            }
        }

        return new SaveState(world, p, chaser);
    }

    /**
     * Decodes a character into the corresponding TETile.
     * Note: CELL and LOCKED_DOOR share the same character, so LOCKED_DOOR is prioritized.
     */
    private static TETile decode(char c) {
        // Entity tiles
        if (c == Tileset.AVATAR.character()) return Tileset.AVATAR;
        if (c == Tileset.CHASER.character()) return Tileset.CHASER;
        
        // Walkable tiles
        if (c == Tileset.FLOOR.character()) return Tileset.FLOOR;
        if (c == Tileset.GRASS.character()) return Tileset.GRASS;
        if (c == Tileset.FLOWER.character()) return Tileset.FLOWER;
        if (c == Tileset.SAND.character()) return Tileset.SAND;
        if (c == Tileset.SNOW.character()) return Tileset.SNOW;
        
        // Unwalkable/Blocking tiles
        if (c == Tileset.WALL.character()) return Tileset.WALL;
        if (c == Tileset.WATER.character()) return Tileset.WATER;
        if (c == Tileset.MOUNTAIN.character()) return Tileset.MOUNTAIN;
        if (c == Tileset.TREE.character()) return Tileset.TREE;
        if (c == Tileset.BUSH.character()) return Tileset.BUSH;
        if (c == Tileset.STATUE.character()) return Tileset.STATUE;
        if (c == Tileset.CRATE.character()) return Tileset.CRATE;
        if (c == Tileset.BOOKSHELF.character()) return Tileset.BOOKSHELF;
        if (c == Tileset.SNOWMAN.character()) return Tileset.SNOWMAN;
        
        // Doors (CELL uses same character as LOCKED_DOOR, prioritize LOCKED_DOOR)
        if (c == Tileset.LOCKED_DOOR.character()) return Tileset.LOCKED_DOOR;
        if (c == Tileset.UNLOCKED_DOOR.character()) return Tileset.UNLOCKED_DOOR;
        if (c == Tileset.CELL.character()) return Tileset.CELL;
        
        // Interactable tiles
        if (c == Tileset.TREASURE.character()) return Tileset.TREASURE;
        if (c == Tileset.PORTAL.character()) return Tileset.PORTAL;
        
        // Damaging tiles
        if (c == Tileset.LAVA.character()) return Tileset.LAVA;
        if (c == Tileset.SPIKE.character()) return Tileset.SPIKE;
        
        // Empty space
        if (c == Tileset.NOTHING.character()) return Tileset.NOTHING;
        
        // Unknown character defaults to NOTHING
        return Tileset.NOTHING;
    }

    public static class SaveState {
        public final TETile[][] world;
        public final Player player;
        public final Chaser chaser;

        public SaveState(TETile[][] w, Player p, Chaser c) {
            world = w;
            player = p;
            chaser = c;
        }
    }
}
