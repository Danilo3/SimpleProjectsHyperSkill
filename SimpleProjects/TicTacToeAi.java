package tictactoe;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;

public class Main {

    public enum Difficulty {
        EASY, MEDIUM, HARD
    }

    final static Scanner scanner = new Scanner(System.in);

    final static char EMPTY = ' ';

    public static void main(String[] args) {
        String command;
        while (true) {
            System.out.print("Input command: ");
            command = scanner.nextLine();
            if (command.equals("exit"))
                break;
            parseCommand(command);
        }
    }

    private static Difficulty chooseDifficulty(String param)
    {
        return Difficulty.valueOf(param.toUpperCase());
    }

    private static void parseCommand(String command) {
        String []args = command.split(" ");
        Difficulty difficulty1;
        Difficulty difficulty2;
        if (args.length != 3 || !args[0].equals("start")) {
            System.out.println("Bad parameters!");
        } else if (args[1].equals("user") && args[2].equals("user")){
            gameUserUser();
        } else if ((args[1].equals("user") && ((difficulty2 = chooseDifficulty(args[2])) != null)) ||
                ((difficulty2 = chooseDifficulty(args[1]))!= null && args[2].equals("user"))) {
            gameUserComp(difficulty2);
        } else if (((difficulty1 = chooseDifficulty(args[1])) != null) &&
                ((difficulty2 = chooseDifficulty(args[2])) != null)) {
            gameCompComp(difficulty1, difficulty2);
        }
    }

    private static void gameCompComp(Difficulty difficulty1, Difficulty difficulty2) {
        char [][] battleField = new char[3][3];
        initBattleField(battleField);
        while (!checkField(battleField)) {
            printBattleField(battleField);
            System.out.println("Making move level \"" +difficulty1.name().toLowerCase() +"\"");
            insertCompMark(battleField, difficulty1, 'X');
            if (checkField(battleField))
                return;
            printBattleField(battleField);
            System.out.println("Making move level \"" +difficulty2.name().toLowerCase() +"\"");
            insertCompMark(battleField, difficulty2, 'O');
        }
    }

    private static void gameUserComp(Difficulty difficulty) {
        char [][] battleField = new char[3][3];
        Coordinate userCoordinate = new Coordinate(0, 0);
        initBattleField(battleField);
        while (!checkField(battleField)) {
            printBattleField(battleField);
            getCoordinates(userCoordinate);
            insertMark(battleField, userCoordinate, 'X');
            if (checkField(battleField))
                return;
            printBattleField(battleField);
            System.out.println("Making move level \"" +difficulty.name().toLowerCase() +"\"");
            insertCompMark(battleField, difficulty, 'O');
        }
    }

    private static void gameUserUser() {
        char [][] battleField = new char[3][3];
        Coordinate coordinate = new Coordinate(0,0);
        int count  = 0;
        initBattleField(battleField);
        while (!checkField(battleField)) {
            printBattleField(battleField);
            getCoordinates(coordinate);
            insertMark(battleField, coordinate, count % 2 == 0 ? 'X' : 'O');
            count++;
        }
    }

    private static Coordinate getEasyCoordinate() {
        Coordinate coordinate = new Coordinate(0,0);
        Random random = new Random(System.currentTimeMillis());
        coordinate.i = random.nextInt(3) + 1;
        coordinate.j = random.nextInt(3) + 1;
        return coordinate;
    }

    private static Coordinate getCompCoordinates(char [][] battleField, Difficulty difficulty) {
        switch (difficulty) {
            case EASY: {
                return getEasyCoordinate();
            }
            case MEDIUM: {
                return getMediumCoordinate(battleField);
            }
            case HARD: {
                return getHardCoordinate(battleField);
            }
        }
        return new Coordinate(0,0);
    }

    private static Coordinate getHardCoordinate(char[][] battleField) {
        Move bestMove = minimax(battleField, 2, 2);
        return  bestMove.coordinate;
    }

    private static void insertCompMark(char[][] battleField, Difficulty difficulty, char mark) {
        Coordinate transCoordinate = new Coordinate(0,0);
        Coordinate coordinate;
        coordinate = getCompCoordinates(battleField, difficulty);
        if (difficulty.equals(Difficulty.EASY))
            transformCoordinate(coordinate, transCoordinate);
        else
            transCoordinate = coordinate;
        while (battleField[transCoordinate.i][transCoordinate.j] != EMPTY) {
            coordinate = getCompCoordinates(battleField, difficulty);
            if (difficulty.equals(Difficulty.EASY))
                transformCoordinate(coordinate, transCoordinate);
            else
                transCoordinate = coordinate;
        }
        battleField[transCoordinate.i][transCoordinate.j] = mark;
    }

    private static Coordinate getMediumCoordinate(char[][] battleField) {
        Coordinate coordinate;
        coordinate = checkIsAlmostRow(battleField, 'O');
        if (coordinate == null)
            coordinate = checkIsAlmostRow(battleField, 'X');
        if (coordinate == null)
            coordinate = findNextEmpty(battleField);
        return coordinate;
    }

    private static void insertMark(char [][]battleField, Coordinate coordinate, char mark) {
        Coordinate transCoordinate = new Coordinate(0,0 );
        transformCoordinate(coordinate, transCoordinate);
        if (battleField[transCoordinate.i][transCoordinate.j] != EMPTY) {
            System.out.println("This cell is occupied! Choose another one!");
            getCoordinates(coordinate);
            insertMark(battleField, coordinate, mark);
        }
        else
            battleField[transCoordinate.i][transCoordinate.j] = mark;
    }
    /*
    (1, 3) (2, 3) (3, 3)         (0, 2) (1, 2) (2, 2)                 (0, 0) (0, 1) (0, 2)
    (1, 2) (2, 2) (3, 2)  --->   (0, 1) (1, 1) (2, 1)     ---->       (1, 0) (1, 1) (1, 2)
    (1, 1) (2, 1) (3, 1)         (0, 0) (1, 0) (2, 0)                 (2, 0) (2, 1) (2, 2)
     */
    private static void transformCoordinate(Coordinate coordinate, Coordinate transformedCoordinate) {
        coordinate.i --;
        coordinate.j --;
        if (coordinate.j == 1)  {
            transformedCoordinate.i = coordinate.j;
        } else if (coordinate.j == 0)
        {
            transformedCoordinate.i = 2;
        }
        else if (coordinate.j == 2)
        {
            transformedCoordinate.i = 0;
        }
        transformedCoordinate.j = coordinate.i;
    }

    private static void getCoordinates(Coordinate coordinate) {
        System.out.print("Enter the coordinates: ");
        String str = scanner.nextLine();
        if (!str.matches("[0-9]+ [0-9]+")) {
            System.out.println("You should enter numbers!");
            getCoordinates(coordinate);
        } else {
            coordinate.i = Integer.parseInt(str.split(" ")[0]);
            coordinate.j = Integer.parseInt(str.split(" ")[1]);
            if (coordinate.i < 1 || coordinate.i > 3 || coordinate.j < 1 || coordinate.j > 3) {
                System.out.println("Coordinates should be from 1 to 3!");
                getCoordinates(coordinate);
            }
        }
    }

    private static void initBattleField(char[][] battleField) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                battleField[i][j] = EMPTY;
            }
        }
    }

    private static boolean checkField(char[][] battleField) {
        var xWins = checkWins(battleField, 'X');
        var oWins = checkWins(battleField, 'O');
        if (printWins(battleField, oWins, xWins)) {
            return true;
        }
        if (findEmpty(battleField)){
            return false;
        } else {
            printBattleField(battleField);
            System.out.println("Draw");
            return true;
        }
    }

    private static Coordinate findNextEmpty(char [][] battleField)
    {
        Coordinate nextEmpty = new Coordinate(0,0);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (battleField[i][j] == EMPTY)
                {
                    nextEmpty.i = i;
                    nextEmpty.j = j;
                    return nextEmpty;
                }
            }
        }
        return nextEmpty;
    }

//    private static char whatNext(char [][]battleField)
//    {
//        int countX = 0;
//        int countO = 0;
//        for (int i = 0; i < 3; i++) {
//            for (int j = 0; j < 3; j++) {
//                if (battleField[i][j] == 'X')
//                    countX++;
//                else if (battleField[i][j] == 'O')
//                    countO++;
//            }
//        }
//        if (countO - countX == 0)
//            return 'X';
//        return (countO > countX ? 'X' : 'O');
//    }
//
//    private static boolean isImpossible(char[][] battleField) {
//        int countX = 0;
//        int countO = 0;
//        for (int i = 0; i < 3; i++) {
//            for (int j = 0; j < 3; j++) {
//                if (battleField[i][j] == 'X')
//                    countX++;
//                else if (battleField[i][j] == 'O')
//                    countO++;
//            }
//        }
//        if (countO - countX == 0)
//            return false;
//        return (countX > countO ? countX - countO != 1 : countO - countX != 1);
//    }


    private static boolean findEmpty(char[][] battleField) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (battleField[i][j] == EMPTY)
                    return true;
            }
        }
        return false;
    }

    private static boolean printWins(char [][]battleField, boolean oWins, boolean xWins) {
        if (oWins && xWins) {
            System.out.println("Impossible");
            return true;
        }
        else if (oWins) {
            printBattleField(battleField);
            System.out.println("O wins");
            return true;
        }
        else if (xWins) {
            printBattleField(battleField);
            System.out.println("X wins");
            return true;
        }
        return false;
    }

    private static boolean checkWins(char[][] battleField, char x) {
        int count  = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (battleField[i][j] == x) {
                    count++;
                }
            }
            if (count == 3)
                return true;
            else
                count = 0;
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (battleField[j][i] == x)
                    count++;
            }
            if (count == 3)
                return true;
            else
                count = 0;
        }
        for (int i = 0; i < 3; i++) {
            if (battleField[i][i] == x) {
                count++;
            }
        }
        if (count == 3)
            return true;
        else
            count = 0;
        for (int i = 0, j = 2; i < 3 && j >= 0; i++, j--) {
            if (battleField[i][j] == x) {
                count++;
            }
        }
        return count == 3;
    }


    private static Coordinate checkIsAlmostRow(char [][]battleField, char mark) {
        int count  = 0;
        Coordinate coordinateToPlace = new Coordinate(0,0);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (battleField[i][j] == mark) {
                    count++;
                } else if (battleField[i][j] == EMPTY){
                    coordinateToPlace.i = i;
                    coordinateToPlace.j = j;
                    if (count == 2)
                        return coordinateToPlace;
                    else
                        count = 0;
                }
            }
            if (count == 2 && battleField[coordinateToPlace.i][coordinateToPlace.j] == EMPTY)
                return coordinateToPlace;
            else
                count = 0;
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (battleField[j][i] == mark) {
                    count++;
                } else if (battleField[i][j] == EMPTY){
                    coordinateToPlace.i = j;
                    coordinateToPlace.j = i;
                    if (count == 2)
                        return coordinateToPlace;
                    else
                        count = 0;
                }
            }
            if (count == 2 && battleField[coordinateToPlace.i][coordinateToPlace.j] == EMPTY)
                return coordinateToPlace;
            else
                count = 0;
        }

        for (int i = 0; i < 3; i++) {
            if (battleField[i][i] == mark) {
                count++;
            } else if (battleField[i][i] == EMPTY){
                coordinateToPlace.i = i;
                coordinateToPlace.j = i;
            }
        }
        if (count == 2 && battleField[coordinateToPlace.i][coordinateToPlace.j] == EMPTY)
            return coordinateToPlace;
        else
            count = 0;
        for (int i = 0, j = 2; i < 3 && j >= 0; i++, j--) {
            if (battleField[i][j] == mark) {
                count++;
            } else if (battleField[i][j] == EMPTY){
                coordinateToPlace.i = i;
                coordinateToPlace.j = j;
            }
        }
        if (count == 2 && battleField[coordinateToPlace.i][coordinateToPlace.j] == EMPTY)
            return coordinateToPlace;
        else
            return null;
    }

//    public static void fillBattleField(String strField, char [][] battleField) {
//        int x = 0;
//        for (int i = 0; i < 3; i++) {
//            for (int j = 0; j < 3; j++) {
//                battleField[i][j] = strField.charAt(x);
//                x++;
//            }
//        }
//    }

    public static void printBattleField(char [][] battleField) {
        System.out.println("---------");
        for (int i = 0; i < 3; i++) {
            System.out.print("| ");
            for (int j = 0; j < 3; j++) {
                System.out.print(battleField[i][j] + " ");
            }
            System.out.println("|");
        }
        System.out.println("---------");
    }

    private static ArrayList<Coordinate> getEmptyCoordinates(char[][] battleField) {
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (battleField[i][j] == EMPTY) {
                    coordinates.add(new Coordinate(i, j));
                }
            }
        }
        return coordinates;
    }

    private static Move minimax(char[][] battleField, int callingPlayer, int currentPlayer) {
        char enemySymbol = ' ';
        char callingSymbol = ' ';
        if (callingPlayer == 1) {
            callingSymbol = 'X';
            enemySymbol = 'O';
        } else if (callingPlayer == 2) {
            callingSymbol = 'O';
            enemySymbol = 'X';
        }

        char symbol = ' ';
        int enemyNumber = 0;
        if (currentPlayer == 1) {
            symbol = 'X';
            enemyNumber = 2;
        } else if (currentPlayer == 2) {
            symbol = 'O';
            enemyNumber = 1;
        }

        // find available spots
        var availableSpots = getEmptyCoordinates(battleField);

        if (checkWins(battleField, enemySymbol)) {
            return new Move(-10);
        } else if (checkWins(battleField, callingSymbol)) {
            return new Move(10);
        } else if (availableSpots.isEmpty()) {
            return new Move(0);
        }

        var moves = new ArrayList<Move>();

        for (int k = 0; k < availableSpots.size(); k++) {
            // let's make a possible move
            Move move = new Move();
            int i = availableSpots.get(k).i;
            int j = availableSpots.get(k).j;
            move.coordinate = new Coordinate(i, j);
            battleField[i][j] = symbol;
            Move result = minimax(battleField, callingPlayer, enemyNumber);
            // save the score for the minimax
            move.score = result.score;
            // then revert the occupied place back to empty, so next guesses can go on
            battleField[i][j] = EMPTY;
            moves.add(move);
        }
        // when the moves loop has ended, choose the move with the highest score
        int bestMove = 0;
        if (currentPlayer == callingPlayer) {
            int bestScore = -10000;
            for (int i = 0; i < moves.size(); i++) {
                if (moves.get(i).score > bestScore) {
                    bestScore = moves.get(i).score;
                    bestMove = i;
                }
            }
        } else {
            int bestScore = 10000;
            for (int i = 0; i < moves.size(); i++) {
                if (moves.get(i).score < bestScore) {
                    bestScore = moves.get(i).score;
                    bestMove = i;
                }
            }
        }
        // minimax returns the best move to the latest function caller
        return moves.get(bestMove);
    }

}

class Move {
    Coordinate coordinate;
    int score;

    public Move(Coordinate coordinate, int score) {
        this.coordinate = coordinate;
        this.score = score;
    }

    public Move() {

    }
    public Move (int score) {
        this.score = score;
    }
}

class Coordinate {
    int i;
    int j;

    public Coordinate(int i, int j) {
        this.i = i;
        this.j = j;
    }
}