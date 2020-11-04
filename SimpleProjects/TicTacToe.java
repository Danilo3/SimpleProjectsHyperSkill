package tictactoe;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    final static Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {
        System.out.print("Enter cells: ");

        String strField = scanner.nextLine();

        char [][] battleField = new char[3][3];
        int  [] coord = new int[2];
        //fillBattleField(strField, battleField);
        initBattleField(battleField, '_');
        int count = 0;
        while (!checkField(battleField)) {
            printBattleField(battleField);
            getCoordinates(coord);
            insertMark(battleField, coord, count % 2 == 0 ? 'X' : 'O');
            count++;
        }
    }

    private static void initBattleField(char[][] battleField, char empty) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                battleField[i][j] = empty;
            }
        }
    }

    private static void insertMark(char [][]battleField, int[] coord, char mark) {
        int []transCoord = new int[2];
        transformCoord(coord, transCoord);
        if (battleField[transCoord[0]][transCoord[1]] != '_') {
            System.out.println("This cell is occupied! Choose another one!");
            getCoordinates(coord);
            insertMark(battleField, coord, mark);
        }
        else
            battleField[transCoord[0]][transCoord[1]] = mark;
    }
/*
(1, 3) (2, 3) (3, 3)         (0, 2) (1, 2) (2, 2)                 (0, 0) (0, 1) (0, 2)
(1, 2) (2, 2) (3, 2)  --->   (0, 1) (1, 1) (2, 1)     ---->       (1, 0) (1, 1) (1, 2)
(1, 1) (2, 1) (3, 1)         (0, 0) (1, 0) (2, 0)                 (2, 0) (2, 1) (2, 2)
 */
    private static void transformCoord(int[] coord, int[] transformedCoord) {
        coord[0] --;
        coord[1] --;
        if (coord[1] == 1)  {
            transformedCoord[0] = coord[1];
        } else if (coord[1] == 0)
        {
            transformedCoord[0] = 2;
        }
        else if (coord[1] == 2)
        {
            transformedCoord[0] = 0;
        }
        transformedCoord[1] = coord[0];
    }

    private static void getCoordinates(int[] coord) {
        System.out.print("Enter the coordinates: ");
        try {
            coord[0] = scanner.nextInt();
            coord[1] = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("You should enter numbers!");
            getCoordinates(coord);
        }
        for (int i = 0; i < 2; i++) {
            if (coord[i] < 1 || coord[i] > 3) {
                System.out.println("Coordinates should be from 1 to 3!");
                getCoordinates(coord);
            }
        }
    }

    private static boolean checkField(char[][] battleField) {
        var xWins = checkWins(battleField, 'X');
        var oWins = checkWins(battleField, 'O');
        /*if (isImpossible(battleField)) {
            System.out.println("Impossible");
            return;
        }*/
        if (printWins(battleField, oWins, xWins)) {
            return true;
        }
        if (findEmpty(battleField, '_')){
            return false;//System.out.println("Game not finished");
        } else {
            printBattleField(battleField);
            System.out.println("Draw");
            return true;
        }
    }

    private static boolean isImpossible(char[][] battleField) {
        int countX = 0;
        int countO = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (battleField[i][j] == 'X')
                    countX++;
                else if (battleField[i][j] == 'O')
                    countO++;
            }
        }
        if (countO - countX == 0)
            return false;
        return (countX > countO ? countX - countO != 1 : countO - countX != 1);
    }


    private static boolean findEmpty(char[][] battleField, char empty) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (battleField[i][j] == empty)
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
        if (count == 3)
            return true;
        else
            return false;
    }


    public static void fillBattleField(String strField, char [][] battleField) {
        int x = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                battleField[i][j] = strField.charAt(x);
                x++;
            }
        }

    }
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
}