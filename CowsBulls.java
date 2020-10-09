package bullscows;

import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Please, enter the secret code's length: ");
        Scanner scanner = new Scanner(System.in);
        SecretCode sc = new SecretCode(scanner.nextInt());
        sc.game();
    }
}

class SecretCode {

    private int size;

    private int []code;

    public SecretCode(int size)
    {
        if (size > 10) {
            System.out.printf("Error: can't generate a secret number with a length of " +
                    "%d because there aren't enough unique digits.\n", size);
            System.exit(0);
        }
        Random random = new Random(System.nanoTime());
        this.size = size;
        code = new int[size];
        int nextInt = 0;
        boolean isAccepted = false;
        for (int i = 0; i < size; i++) {
            while (!isAccepted) {
                nextInt = random.nextInt(10);
                if (nextInt == 0 && i == 0)
                    continue;
                if (!inArray(code, nextInt)) {
                    code[i] = nextInt;
                    isAccepted = true;
                }
            }
            isAccepted = false;
        }
    }

    public void codeFromNumber(int number) {
        for (int i = 0; i < size; i++) {
            this.code[i] = getNthDigit(number, i);
        }
    }

    public int getCode()
    {
        int secret = 0;
        for (int i = 0; i < size; i++) {
            secret += Math.pow(10, size - i - 1) * code[i];
        }
        return secret;
    }

    public void game()
    {
        int turnCount = 0;
        int bulls = 0;
        int cows = 0;
        Scanner scanner = new Scanner(System.in);
        int answer = 0;
        begin();
        while (answer != getCode())
        {
            turnCount++;
            System.out.printf("\nTurn %d:\n", turnCount);
            answer = scanner.nextInt();
            bulls = countBulls(answer, getCode());
            cows = countCows(answer, getCode());
            printGrade(bulls, cows);
        }
        end();
    }

    public void easyGame()
    {
        int bulls = 0;
        int cows = 0;
        Scanner scanner = new Scanner(System.in);
        int answer = scanner.nextInt();
        bulls = countBulls(answer, getCode());
        cows = countCows(answer, getCode());
        printGrade(bulls, cows);
    }

    private void printGrade(int bulls, int cows) {
        System.out.print("Grade: ");
        if(bulls == 0 && cows == 0)
            System.out.print("None. ");
        else if (bulls == 0)
            System.out.printf("%d cow%c ", cows, cows > 1 ?'s' : '.');
        else if (cows == 0)
            System.out.printf("%d bull%c ", bulls, bulls > 1 ? 's': '.');
        else
            System.out.printf("%d bull%c and %d cow%c. ", bulls, bulls > 1 ? 's': '.', cows, cows > 1 ?'s' : '.');
    }

    public void printRandomCode() {
        System.out.printf("The random secret code is %d.\n", getCode());
    }
/*
    9305
    1239
 */
    private int countCows(int answer, int code) {
        int cows = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i != j && this.code[j] == getNthDigit(answer, i)) {
                    cows++;
                }
            }
        }
        return cows;
    }

    private static int getDigitsCount(int num)
    {
        int all_digits = 1;
        int x = 9;
        while (x <= num)
        {
            x *= 10;
            x += 9;
            all_digits++;
        }
        return all_digits;
    }

    private static int getNthDigit(int num, int n)
    {
        int digitsCount = getDigitsCount(num);
        int digit = 0;
        for (int i = 0; i < digitsCount - n  - 1; i++) {
            num /= 10;
        }
        digit = num % 10;
        return digit;
    }

    private static boolean inArray(int arr[], int x){
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == x)
                return true;
        }
        return false;
    }
    private int countBulls(int answer, int code) {
        int bulls = 0;
        for (int i = 0; i < size; i++) {
           if (this.code[i] == getNthDigit(answer, i)) {
               bulls++;
           }
        }
        return bulls;
    }

    public static void begin() {
        System.out.println("Okay, let's start a game!");
    }

    public void end() {
        System.out.println("\nCongratulations! You guessed the secret code.");
    }
}
