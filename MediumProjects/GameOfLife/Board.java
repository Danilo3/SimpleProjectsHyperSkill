package life;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.Consumer;

public class Board {

    private final char [][]board;

    private final int n;

    private int currentGeneration;

    private final int maxGeneration;

    public final static  char EMPTY = ' ';

    public final static char ALIVE = 'O';

    private final static char NEIGHBOUR = 'n';

    private Generation gen;

    public Board(int n, int maxGeneration, int seed) {
        board = new char[n][n];
        this.n = n;
        this.currentGeneration = 0;
        this.maxGeneration = maxGeneration;
        initBoard(board, n, seed);
        gen = new Generation(n);
    }

    public Generation game() {
        gen = new Generation(n);
        if (currentGeneration < maxGeneration) {
            currentGeneration ++;
            gen = new Generation(n);
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    Point p = new Point(i, j);
                    int aliveNeighbours = countAliveNeighbours(board, p);
                    if (isAlive(board, p)) {
                        if (aliveNeighbours == 3 || aliveNeighbours == 2)
                            gen.b[i][j] = ALIVE;
                        else
                            gen.b[i][j] = EMPTY;
                    } else {
                        if (aliveNeighbours == 3)
                            gen.b[i][j] = ALIVE;
                    }
                }
            }
            copyBoard(gen.b, board);
            gen.countAlive = countAllAlive(board);
            gen.genNum = currentGeneration;
            System.out.println("Generation #" + currentGeneration);
            System.out.println("Alive: " + countAllAlive(board));
            return gen;
        }
        return null;
    }

    private static void printBoard(char [][]board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                System.out.print(board[i][j]);
            }
            System.out.println();
        }
    }

    private static void copyBoard(char [][]source, char [][]dest) {
        for (int i = 0; i < source.length; i++) {
            System.arraycopy(source[i], 0, dest[i], 0, source.length);
        }
    }

    private static ArrayList<Point> getNeighbours(Point p, int n) {
        ArrayList<Point> points = new ArrayList<>(8);
        points.add(new Point(p.x - 1 >= 0 ? p.x - 1 : n - 1, p.y - 1 >= 0 ? p.y - 1 : n - 1));
        points.add(new Point(p.x, p.y - 1 >= 0 ? p.y - 1 : n - 1 ));
        points.add(new Point(p.x + 1 < n ? p.x + 1 : 0,  p.y - 1 >= 0 ? p.y - 1 : n - 1));
        points.add(new Point(p.x - 1 >= 0 ? p.x - 1 : n - 1, p.y));
        points.add(new Point(p.x + 1 < n ? p.x + 1 : 0, p.y));
        points.add(new Point(p.x - 1 >= 0 ? p.x - 1 : n - 1,p.y + 1 < n ? p.y + 1 : 0));
        points.add(new Point(p.x, p.y + 1 < n ? p.y + 1 : 0));
        points.add(new Point(p.x + 1 < n ? p.x + 1 : 0, p.y + 1 < n ? p.y + 1 : 0));
        return points;
    }

    private static boolean isAlive(char [][]board, Point p) {
        return board[p.x][p.y] == ALIVE;
    }

    private static int countAliveNeighbours(char [][]board, Point p) {
        int count = 0;
        for (Point point : getNeighbours(p, board.length)) {
            count += isAlive(board, point) ? 1 : 0;
        }
        return count;
    }
    private static  int countAllAlive(char [][]board) {
        int count = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (board[i][j] == ALIVE)
                    count++;
            }
        }
        return count;
    }

    private static void initBoard(char [][]board, int n, int seed) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                board[i][j] =  EMPTY;
            }
        }
        Random random = new Random(seed);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if(random.nextBoolean()) {
                    board[i][j] = 'O';
                }
            }
        }
    }
}

class Generation
{
    char [][] b;
    int genNum;
    int countAlive;

    Generation(int n) {
        b = new char[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                b[i][j] = Board.EMPTY;
            }
        }
        genNum = 0;
    }
}

class Point {
    int x;
    int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
}