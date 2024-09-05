package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;

    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);

    /**
     * Represent the Position in the TETile World
     */
    public static class Position {
        private int x;
        private int y;
        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
        public int getX() {
            return x;
        }
        public int getY() {
            return y;
        }
        public void moveX(int steps) {
            x = x + steps;
        }
        public void moveY(int steps) {
            y = y + steps;
        }
    }



    /** using the drawRow method to draw a hexagon in the given World
     * @param world : the world to draw
     * @param tile : type of tile to draw
     * @param p : the starting position
     * @param size : the size of hexagon
     */
    public static void addHexagon(TETile[][] world, TETile tile, Position p, int size) {
        if (size < 2) {
            return;
        }
        addHexagonHelper(world, tile, p, size, size - 1);
    }
    

    /** using the drawRow method to draw a hexagon in the given World
     * @param world : the world to draw
     * @param tile : type of tile to draw
     * @param p : the starting position
     * @param bricks : the number of tile need to draw
     * @param blank : the number of tile that do not need to draw
     *
     */
    public static void addHexagonHelper(TETile[][] world, TETile tile, Position p, int bricks, int blank) {

        Position startTile = new Position(p.getX(), p.getY());
        startTile.moveX(blank);

        //draw the first Row
        drawRow(world, tile, startTile, bricks);

        //recursive call
        if (blank > 0) {
            p.moveY(-1);
            addHexagonHelper(world, tile, p, bricks + 2, blank - 1);
        }

        //draw the Row in the other side of the hexagon
        startTile.moveY(-(2 * blank + 1));
        drawRow(world, tile, startTile, bricks);
    }

    /**
     * Draw a horizontal Row of some kind of Tile in the TETile World start from a Position P
     * with Length.
     * @param world : the world to draw
     * @param tile : type of tile to draw
     * @param p : the starting position
     * @param length : the length of the Row
     */
    public static void drawRow(TETile[][] world, TETile tile, Position p, int length) {
        for (int x = p.getX(); x < p.getX() + length; x++) {
            world[x][p.getY()] = tile;
        }
    }

    /** Picks a RANDOM tile with a 33% change of being
     *  a wall, 33% chance of being a flower, and 33%
     *  chance of being empty space.
     */
    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(3);
        switch (tileNum) {
            case 0: return Tileset.WALL;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.NOTHING;
            default: return Tileset.NOTHING;
        }
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] hexTiles = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                hexTiles[x][y] = Tileset.NOTHING;
            }
        }

        Position p = new Position(20, 20);
        addHexagon(hexTiles, randomTile(), p, 3);
        ter.renderFrame(hexTiles);
    }

}
