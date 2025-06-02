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

    private static final long SEED = 2873125;
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


    /**
     * Draw the world of hexagon in some pattern
     * @param world : he world to draw
     * @param p : the starting point to draw the hexagon
     * @param size : the size of hexagon
     */
    public static void HexagonWorld(TETile[][] world, Position p, int size) {
        //ascending
        Position nextPosition = new Position(p.getX(), p.getY());
        for (int x = 0; x < 3; x++) {
            drawColumnHexagons(world, nextPosition, 3 + x, size);
            nextPosition.moveX(2 * size -1);
            nextPosition.moveY(size);
        }

        nextPosition.moveY(-2 * size);
        //descending
        for (int x = 2; x > 0; x--) {
            drawColumnHexagons(world, nextPosition, 2 + x, size);
            nextPosition.moveX(2 * size -1);
            nextPosition.moveY(-size);
        }


    }


    /**
     * draw a column of hexagons
     * @param world : he world to draw
     * @param p : the starting point to draw the hexagon
     * @param number : the number of hexagon need to draw
     * @param size : the size of hexagon
     */
    public static void drawColumnHexagons(TETile[][] world, Position p, int number, int size) {
        addHexagon(world, randomTile(),  p , size);
        number = number - 1;
        if (number > 0) {
            Position nextPosition = new Position(p.getX(), p.getY() - 2 * size);
            drawColumnHexagons(world, nextPosition,  number , size);
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
            Position nextPosition = new Position(p.getX(), p.getY() - 1);
            addHexagonHelper(world, tile, nextPosition, bricks + 2, blank - 1);
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
    /**
     * Draw a horizontal Row of some kind of Tile in the TETile World start from a Position P
     * with Length.
     * @param world : the world to draw
     * @param tile : type of tile to draw
     * @param p : the starting position
     * @param length : the length of the Row
     */
    public static void drawCol(TETile[][] world, TETile tile, Position p, int length) {
        for (int y = p.getY(); y < p.getY() + length; y++) {
            world[p.getX()][y] = tile;
        }
    }


    /** Picks a RANDOM tile with a 33% change of being
     *  a wall, 33% chance of being a flower, and 33%
     *  chance of being empty space.
     */
    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(5);
        switch (tileNum) {
            case 0: return Tileset.GRASS;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.MOUNTAIN;
            case 3: return Tileset.TREE;
            case 4: return Tileset.SAND;
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

        Position p = new Position(10, 30);
        HexagonWorld(hexTiles, p, 3);
        ter.renderFrame(hexTiles);
    }

}
