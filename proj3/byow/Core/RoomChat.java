package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RoomChat {
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
    }

    private static int WIDTH;
    private static int HEIGHT;
    private static Random RANDOM;
    private static final List<Position> roomCenters = new ArrayList<>();


    public RoomChat (int w, int h, Random r) {
        WIDTH = w;
        HEIGHT = h;
        RANDOM = r;

    }


    public void generateWorld(TETile[][] world) {
        // Step 1: 填滿 NOTHING
        TERenderer ter = new TERenderer();

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        generateRooms(world, 20, 0.8);
        connectRooms(world);
        addWalls(world);
    }


    public static void addRoomHelper(TETile[][] world, Position p, int l, int h) {
        drawInterior(world, p, l, h);

        int centerX = p.getX() + l / 2;
        int centerY = p.getY() + h / 2;
        roomCenters.add(new Position(centerX, centerY));
    }

    public static void addWalls(TETile[][] world) {
        for (int x = 1; x < WIDTH - 1; x++) {
            for (int y = 1; y < HEIGHT - 1; y++) {
                if (world[x][y] == Tileset.FLOOR) { // 地板
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dy = -1; dy <= 1; dy++) {
                            int nx = x + dx;
                            int ny = y + dy;
                            if (world[nx][ny] == Tileset.NOTHING) { // 邊緣空白 -> 補牆
                                world[nx][ny] = Tileset.WALL;
                            }
                        }
                    }
                }
            }
        }
    }

    private static void drawInterior(TETile[][] world, Position p, int l, int h) {
        for (int i = 1; i <= h; i++) {
            drawRow(world, Tileset.FLOOR, new Position(p.getX() + 1, p.getY() + i), l);
        }
    }

    private static void drawHallwayRow(TETile[][] world, Position p, int length) {
        drawRow(world, Tileset.FLOOR, new Position(p.getX(), p.getY()), length);
    }

    private static void drawHallwayCol(TETile[][] world, Position p, int length) {
        drawCol(world, Tileset.FLOOR, new Position(p.getX(), p.getY()), length);
    }

    public static void drawRow(TETile[][] world, TETile tile, Position p, int length) {
        for (int x = p.getX(); x < p.getX() + length; x++) {
            if (x >= 0 && x < WIDTH && p.getY() >= 0 && p.getY() < HEIGHT) {
                world[x][p.getY()] = tile;
            }
        }
    }

    public static void drawCol(TETile[][] world, TETile tile, Position p, int length) {
        for (int y = p.getY(); y < p.getY() + length; y++) {
            if (p.getX() >= 0 && p.getX() < WIDTH && y >= 0 && y < HEIGHT) {
                world[p.getX()][y] = tile;
            }
        }
    }

    public static void generateRooms(TETile[][] world, int maxRooms, double fillRatio) {
        int totalArea = WIDTH * HEIGHT;
        int filledArea = 0;
        int roomCount = 0;

        while (roomCount < maxRooms && (double) filledArea / totalArea < fillRatio) {
            int roomWidth = RANDOM.nextInt(5) + 3;
            int roomHeight = RANDOM.nextInt(5) + 3;
            Position randomPos = new Position(RANDOM.nextInt(WIDTH - roomWidth - 2), RANDOM.nextInt(HEIGHT - roomHeight - 2));

            if (canPlaceRoom(world, randomPos, roomWidth, roomHeight)) {
                addRoomHelper(world, randomPos, roomWidth, roomHeight);
                filledArea += (roomWidth + 2) * (roomHeight + 2);
                roomCount++;
            }
        }
    }

    private static boolean canPlaceRoom(TETile[][] world, Position p, int l, int h) {
        for (int x = p.getX() - 1; x <= p.getX() + l + 1; x++) {
            for (int y = p.getY() - 1; y <= p.getY() + h + 1; y++) {
                if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT || world[x][y] != Tileset.NOTHING) {
                    return false;
                }
            }
        }
        return true;
    }

    static void connectRooms(TETile[][] world) {
        List<Position> connectedRooms = new ArrayList<>();
        connectedRooms.add(roomCenters.get(0)); // 將第一個房間標記為已連接

        while (connectedRooms.size() < roomCenters.size()) {
            Position closestRoom = null;
            Position closestConnected = null;
            double minDistance = Double.MAX_VALUE;

            for (Position connected : connectedRooms) {
                for (Position unconnected : roomCenters) {
                    if (connectedRooms.contains(unconnected)) {
                        continue; // 已連接的房間跳過
                    }
                    double distance = Math.sqrt(Math.pow(connected.getX() - unconnected.getX(), 2) +
                            Math.pow(connected.getY() - unconnected.getY(), 2));
                    if (distance < minDistance) {
                        minDistance = distance;
                        closestRoom = unconnected;
                        closestConnected = connected;
                    }
                }
            }

            if (closestRoom != null && closestConnected != null) {
                connectTwoRooms(world, closestConnected, closestRoom);
                connectedRooms.add(closestRoom); // 標記為已連接
            }
        }
    }

    private static void connectTwoRooms(TETile[][] world, Position p1, Position p2) {
        // 連接兩個房間的水平和垂直走廊，並確保有牆壁包圍
        if (RANDOM.nextBoolean()) {
            // 先水平再垂直
            drawHallwayRow(world, new Position(Math.min(p1.getX(), p2.getX()), p1.getY()), Math.abs(p1.getX() - p2.getX()) + 1);
            drawHallwayCol(world, new Position(p2.getX(), Math.min(p1.getY(), p2.getY())), Math.abs(p1.getY() - p2.getY()) + 1);
        } else {
            // 先垂直再水平
            drawHallwayCol(world, new Position(p1.getX(), Math.min(p1.getY(), p2.getY())), Math.abs(p1.getY() - p2.getY()) + 1);
            drawHallwayRow(world, new Position(Math.min(p1.getX(), p2.getX()), p2.getY()), Math.abs(p1.getX() - p2.getX()) + 1);
        }
    }

    public static void main(String[] args) {



    }
}
