package cinema;

import java.util.Scanner;

public class Cinema {


    private static final char FREE = 'S';
    private static final char BUSY = 'B';
    private final int n;
    private final int m;
    private final char [][] hall;
    private int row;
    private int seat;
    private int totalIncome;
    private int currentIncome;

    Scanner scanner = new Scanner(System.in);
    private int tickets;
    private double percentage;

    public Cinema() {
        System.out.println("Enter the number of rows: ");
        this.n = scanner.nextInt();
        System.out.println("Enter the number of seats in each row: ");
        this.m = scanner.nextInt();
        hall = new char[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                hall[i][j] = FREE;
            }
        }
        totalIncome = 0;
        currentIncome = 0;
        totalIncome = calculateTotal();
    }

    public void statistics() {
        System.out.printf(new StringBuilder().append("Number of purchased tickets: %d\n").append("Percentage: %.2f%%\n").append("Current income: $%d\n").append("Total income: $%d\n").toString(),
                tickets,
                percentage,
                currentIncome,
                totalIncome);
    }

    public void inputPlaces() {
        System.out.println("Enter a row number: ");
        row = scanner.nextInt();
        System.out.println("Enter a seat number in that row: ");
        seat = scanner.nextInt();
    }

    public void menu() {
        while (true ) {
            System.out.println();
            System.out.println("1. Show the seats");
            System.out.println("2. Buy a ticket");
            System.out.println("3. Statistics");
            System.out.println("0. Exit");
            int x = scanner.nextInt();
            if (x == 1) {
                print();
            } else if (x == 2) {
                calculateOne();
            } else if (x == 3) {
                statistics();
            } else if (x == 0) {
                break;
            }
        }
    }

    public void bookSeat(){
        hall[row - 1][seat- 1] = BUSY;
    }

    public int calculateOne() {
        inputPlaces();
        if (row <= 0 || seat <= 0 || row > n || seat > m ) {
            System.out.println("Wrong input!");
            return 0;
        }
        if (hall[row - 1][seat - 1] == BUSY) {
            System.out.println("That ticket has already been purchased!");
            calculateOne();
            return 0;
        }
        int total = 0;
        tickets++;
        if (n * m <= 60) {
            total = 10;
        } else {
            if (row <= n / 2) {
                total = 10;
            } else {
                total = 8;
            }
        }
        System.out.println("Ticket price:\n$" + total);
        bookSeat();
        currentIncome += total;
        percentage =  100 * (((double)tickets )/ (double)(m * n));
        return total;
    }

    public int calculateTotal() {
        int total = 0;
        if (m * n <= 60) {
            total = n * n * 10;
        } else {
            int half = n / 2;
            for (int i = 1; i <= half; i++) {
                total += m * 10;
            }
            for (int i = half + 1; i <= n; i++) {
                total += m * 8;
            }
        }
        return total;
    }

    public void print() {
        System.out.println("Cinema:");
        System.out.print("  ");
        for (int i = 1; i <= m; i++) {
            System.out.print(i + " ");
        }
        System.out.println();
        for (int i = 0; i < n; i++) {
            System.out.print(i + 1 + " ");
            for (int j = 0; j < m; j++) {
                System.out.print(hall[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Cinema cinema = new Cinema();
        cinema.menu();
    }
}