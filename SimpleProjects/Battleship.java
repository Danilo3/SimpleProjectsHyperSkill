package battleship;

import java.util.*;

public class Main {

   static Ship []ships1 = { new Ship("Aircraft Carrier", 5),
                    new Ship("Battleship", 4),
                    new Ship("Submarine", 3),
                    new Ship("Cruiser", 3),
                    new Ship("Destroyer", 2)};

    static Ship []ships2 = { new Ship("Aircraft Carrier", 5),
            new Ship("Battleship", 4),
            new Ship("Submarine", 3),
            new Ship("Cruiser", 3),
            new Ship("Destroyer", 2)};

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Player 1, place your ships on the game field");
        Battlefield battlefield1= new Battlefield();
        ConsoleInputUtil.setShips(battlefield1, ships1);

        System.out.println("Press Enter and pass the move to another player\n...");
        scanner.nextLine();
        Battlefield battlefield2 = new Battlefield();
        ConsoleInputUtil.setShips(battlefield2, ships2);


        boolean isWin = false;
        while (!isWin) {
            System.out.println("Press Enter and pass the move to another player\n...\n");
            scanner.nextLine();
            battlefield1.printBattleFields();
            System.out.println("\nPlayer 1, it's your turn: ");
            Shot shot = ConsoleInputUtil.getShot();
            isWin = battlefield1.makeShot(shot, new ArrayList<>(Arrays.asList(ships2)), battlefield2);
            if (isWin)
                break;
            System.out.println("Press Enter and pass the move to another player\n...\n");
            scanner.nextLine();
            battlefield2.printBattleFields();
            System.out.println("Player 2, it's your turn: ");
            shot = ConsoleInputUtil.getShot();
            isWin = battlefield2.makeShot(shot, new ArrayList<>(Arrays.asList(ships1)), battlefield1);
        }
    }
}

class ConsoleInputUtil {
    private final static Scanner scanner = new Scanner(System.in);

    static void setShips(Battlefield battlefield, Ship []ships) {
        String msg = "";
        for (int i = 0; i < ships.length; i++) {
            try {
                if (msg.equals("") || msg.equals("Success"))
                System.out.println("Enter the coordinates of the " + ships[i] + ": ");
                String coordinates = scanner.nextLine();
                Coordinate coordinate1 = new Coordinate(coordinates.split(" ")[0]);
                Coordinate coordinate2 = new Coordinate(coordinates.split(" ")[1]);
                ships[i].start = coordinate1;
                ships[i].end = coordinate2;
                msg = battlefield.tryToFit(ships[i]);
                if (!msg.equals("Success")) {
                    System.out.println(msg);
                    i--;
                } else {
                    battlefield.printBattleField();
                }
            } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException | StringIndexOutOfBoundsException e) {
                System.out.println("Error! You entered the wrong coordinates! Try again:");
                i--;
            }
        }
    }

    public static Shot getShot() {
        Shot shot = null;
        while (shot == null) {
            try {
                String line = scanner.nextLine();
                Coordinate coordinate = new Coordinate(line);
                shot = new Shot(coordinate);
            } catch (ArrayIndexOutOfBoundsException | StringIndexOutOfBoundsException | IllegalArgumentException e) {
                System.out.println("Error! You entered the wrong coordinate! Try again:");
                shot = null;
            }
        }
        return shot;
    }
}

class Battlefield {

    char [][]battleField;

    char [][]enemyBattleField;

    private final static int size = 10;

    private final static char SEA = '~';

    private final static char SHIP = 'O';

    private final static char HIT = 'X';

    private final static char MISS = 'M';

    public void initBattleField() {
        battleField = new char[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                battleField[i][j] = SEA;
            }
        }
    }

    public Battlefield() {
        enemyBattleField = newBattleField();
        initBattleField();
        printBattleField();
    }

    public char [][]newBattleField() {
        char [][]field = new char[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                field[i][j] = SEA;
            }
        }
        return field;
    }

    public void printBattleField() {
        char ch = 'A';
        System.out.print(" ");
        for (int i = 1; i <= size; i++) {
            System.out.print(i + " ");
        }
        System.out.println();
        for (int i = 0; i < size; i++) {
            System.out.print(ch + " ");
            ch++;
            for (int j = 0; j < size; j++) {
                System.out.print(battleField[i][j] + " ");
            }
            System.out.println();
        }
//        System.out.println();
    }

    public void printBattleField(char[][] field) {
        char ch = 'A';

        System.out.print(" ");
        for (int i = 1; i <= size; i++) {
            System.out.print(i + " ");
        }
        System.out.println();
        for (int i = 0; i < size; i++) {
            System.out.print(ch + " ");
            ch++;
            for (int j = 0; j < size; j++) {
                System.out.print(field[i][j] + " ");
            }
            System.out.println();
        }
       // System.out.println();
    }

    public String tryToFit(Ship ship) {
        int x1 = ship.start.x - 1;
        int y1 = ship.start.y - 1;
        int x2 = ship.end.x - 1;
        int y2 = ship.end.y - 1;
        boolean isHorizontal = y2 == y1;
        boolean isOccupied = false;
        int swap;

        if (x1 > x2) {
            swap = x2;
            x2 = x1;
            x1 = swap;
        }
        if (y1 > y2) {
            swap = y2;
            y2 = y1;
            y1 = swap;
        }

//        System.out.printf("x1 = %d, y1 = %d, x2 = %d, y2 = %d", x1, y1, x2, y2);

        if (y1 != y2 && x1 != x2)
            return "Error! Wrong ship location! Try again:";
        if (x1 < 0 || y1 < 0 || x2 < 0 || y2 < 0 || x1 > size || x2 > size || y1 > size || y2 > size) {
            return "Error! Wrong length of the " + ship.name + "! Try again: ";
        }
        if (isHorizontal) {
            if (x2 - x1 != ship.cells - 1)
                return "Error! Wrong length of the " + ship.name + "! Try again: ";
        } else {
            if (y2 - y1 != ship.cells - 1)
                return "Error! Wrong length of the " + ship.name + "! Try again: ";
        }

        if (isHorizontal) {
            for (int i = 0; i < ship.cells; i++) {
                try {
                    if (battleField[y1][x1 + i] == SHIP) {
                        isOccupied = true;
                        break;
                    } else {
                        Cell cell = new Cell(x1 + i, y1);
                        for (Cell c : getNeighbours(cell)) {
                            if (battleField[c.y][c.x] == SHIP) {
                                isOccupied = true;
                                break;
                            }
                        }
                    }
                } catch (IndexOutOfBoundsException e) {
                    isOccupied = true;
                    break;
                }
            }
        } else {
            for (int i = 0; i < ship.cells; i++) {
                try {
                    if (battleField[y1 + i][x1] == SHIP) {
                        isOccupied = true;
                        break;
                    } else {
                        Cell cell = new Cell(x1, y1 + i);
                        for (Cell c : getNeighbours(cell)) {
                            if (battleField[c.y][c.x] == SHIP) {
                                isOccupied = true;
                                break;
                            }
                        }
                    }
                } catch (IndexOutOfBoundsException e) {
                    isOccupied = true;
                    break;
                }
            }
        }
        if (isOccupied) {
            return "Error! You placed it too close to another one. Try again:";
        }
        if (isHorizontal) {
            for (int i = 0; i < ship.cells; i++) {
                battleField[y1][x1 + i] = SHIP;
                ship.shots.add(new Shot(new Coordinate(x1 +i, y1), Shot.State.NONE));
            }
        } else {
            for (int i = 0; i < ship.cells; i++) {
                battleField[y1 + i][x1] = SHIP;
                ship.shots.add(new Shot(new Coordinate(x1, y1 + i), Shot.State.NONE));
            }
        }
        return "Success";
    }
    public ArrayList<Cell> getNeighbours(Cell c) {
        ArrayList<Cell> cells = new ArrayList<>();
        cells.add(new Cell(c.x - 1, c.y -1));
        cells.add(new Cell(c.x, c.y - 1));
        cells.add(new Cell(c.x + 1 < size ? c.x + 1 : -1,  c.y - 1));
        cells.add(new Cell(c.x - 1, c.y));
        cells.add(new Cell(c.x + 1 < size ? c.x + 1 : -1, c.y));
        cells.add(new Cell(c.x - 1, c.y + 1 < size ? c.y + 1 : -1));
        cells.add(new Cell(c.x, c.y + 1 < size ? c.y + 1 : -1));
        cells.add(new Cell(c.x + 1 < size ? c.x + 1 : -1, c.y + 1 < size ? c.y + 1 : -1));
        cells.removeIf(cell -> cell.x == -1 || cell.y == -1);
        return cells;
    }

    public void startGame() {

    }

    public boolean makeShot(Shot shot, ArrayList<Ship> ships, Battlefield battlefield2) {
        int y = shot.coordinate.y - 1;
        int x = shot.coordinate.x - 1;
        boolean isShipSank = false;
        if (battlefield2.battleField[y][x] == SHIP) {
            battlefield2.battleField[y][x] = HIT;
            enemyBattleField[y][x] = HIT;
            Coordinate coordinate = new Coordinate(x, y);
            for (Ship ship :ships) {
                if (ship.isThatShip(coordinate)) {
                    ship.shotHit(coordinate);
                    isShipSank = ship.isSank();
                }
            }
            if(!isShipSank)
                System.out.println("You hit a ship!");
            else if (Ship.allShipsSank(ships)) {
                System.out.println("You sank the last ship. You won. Congratulations!");
                return true;
            } else {
                System.out.println("You sank a ship! Specify a new target:");
            }
        }
        else if (battlefield2.battleField[y][x] == SEA) {
            battlefield2.battleField[y][x] = MISS;
            enemyBattleField[y][x] = MISS;
            System.out.println("You missed!");
        }
        else
        {
            System.out.println("Already shoot here! Try again: ");
        }
        return false;
    }

    public void printBattleFields() {
        printBattleField(enemyBattleField);
        System.out.println("---------------------");
        printBattleField();
    }
}

class Ship {
    String          name;
    Coordinate      start;
    Coordinate      end;
    ArrayList<Shot> shots;
    int             cells;

    public static boolean allShipsSank(ArrayList<Ship> ships) {
        for (Ship ship: ships) {
            if (!ship.isSank())
                return false;
        }
        return true;
    }

//    public Ship(String name, Coordinate start, Coordinate end, int cells) {
//        this.name = name;
//        this.start = start;
//        this.end = end;
//        this.cells = cells;
//        shots = new ArrayList<>(cells);
////        for (int i = 0; i < cells; i++) {
////            shots.add(new Shot(null,Shot.State.NONE));
////        }
//    }

    public Ship(String name, int cells) {
        this.name = name;
        this.cells = cells;
        shots = new ArrayList<>(cells);
//        for (int i = 0; i < cells; i++) {
//            shots.add(new Shot(null,Shot.State.NONE));
//        }
    }

    public void shotHit(Coordinate coordinate) {
        for (Shot shot : shots) {
            if (shot.coordinate.equals(coordinate)) {
                shot.state = Shot.State.HIT;
            }
        }
    }

    public boolean isThatShip(Coordinate coordinate) {
        for (Shot s : shots) {
            if (s.coordinate.equals(coordinate)) {
                return true;
            }
        }
        return false;
    }

    public boolean isSank() {
        for (Shot s: shots) {
            if(s.state == Shot.State.NONE)
                return false;
        }
        return true;
    }

    @Override
    public String toString(){
        return name + " (" + cells + " cells)";
    }
}

class Coordinate {

    public char a;
    public int  x;
    public int  y;

//    public Coordinate(char a, int x) {
//        this.a = a;
//        this.x = x;
//        this.y = a - 64;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate that = (Coordinate) o;
        return x == that.x &&
                y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public Coordinate(String str) throws IllegalArgumentException, StringIndexOutOfBoundsException{
        try {
            str = str.trim();
            if (str.length() > 3) throw new IllegalArgumentException();
            if (!Character.isAlphabetic(str.charAt(0))) throw new IllegalArgumentException();
            if (!Character.isDigit(str.charAt(1))) throw new IllegalArgumentException();
            if (str.charAt(0) < 'A' || str.charAt(0) > 'J') throw  new IllegalArgumentException();
            int x = Integer.parseInt(str.substring(1));
            if (x > 10 || x < 0) throw  new IllegalArgumentException();
            this.a = str.charAt(0);
            this.x = x;
            this.y = a - 64;
        } catch (StringIndexOutOfBoundsException | IllegalArgumentException e) {
//            System.out.println("Error! You entered the wrong coordinates! Try again:");
            throw e;
        }
    }

    public Coordinate(int x, int y) {
        this.x = x + 1;
        this.y = y + 1;
    }
}

class Cell {
    int     x;
    int     y;


    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

class Shot {
    Coordinate coordinate;
    State state;

    enum State {
        HIT, MISSED, NONE
    }

    Shot(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public Shot(Coordinate coordinate, State state) {
        this.coordinate = coordinate;
        this.state = state;
    }
}