package converter;

import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {

    private final static String alphabet = "abcdefghijklmnopqrstuvwxyz";

    private final static Scanner scanner = new Scanner(System.in);

    public static String reverse(String str)
    {
        StringBuilder result = new StringBuilder();
        for (int i = str.length() - 1; i >= 0; i--) {
            result.append(str.charAt(i));
        }
        return result.toString();
    }

    public static String convert(int a, int base)
    {
        StringBuilder answer = new StringBuilder();
        int remainder;
        if (a == 0)
            answer = new StringBuilder("0");
        while (a != 0)
        {
            remainder = a % base;
            answer.append(Character.forDigit(remainder, base));
            a = a / base;
        }
        answer = new StringBuilder(reverse(answer.toString()));
        return answer.toString();
    }

//    private static int getCharValue(char a)
//    {
//        if (a <= '9' && a >= '0')
//            return (a - '0');
//        else {
//            return (a - (alphabet.indexOf(a) + 10));
//        }
//    }

    private static double convertToDec(String number, int sourceRadix) {
        double result = 0;
        int i = Integer.parseInt(number.split("\\.")[0], sourceRadix);
        result += i;
        String fractional = number.split("\\.")[1];
        for (int j = 0; j < fractional.length(); j++) {
            result += ((double)Character.digit(fractional.charAt(j), sourceRadix)) / (Math.pow(sourceRadix, j + 1));
        }
        return result;
    }


    public static void main(String[] args) {
        int num = 0;
        int sourceRadix = 0;
        String number = "";
        int targetRadix = 0;
        try {
            sourceRadix = scanner.nextInt();
            number = scanner.next();
            targetRadix = scanner.nextInt();
        } catch (NoSuchElementException exception)
        {
            System.out.println("error");
            System.exit(1);
        }
        if (sourceRadix <= 0 || targetRadix <= 0 || targetRadix >= 37 || sourceRadix >= 37) {
            System.out.println("error");
            System.exit(2);
        }
        if (!number.contains(".")) {
            if (sourceRadix != 1) {
                num = Integer.parseInt(number, sourceRadix);
            } else {
                int x = Integer.parseInt(number);
                while (x != 0) {
                    x /= 10;
                    num++;
                }
            }
            if (targetRadix != 1) {
                System.out.println(Integer.toString(num, targetRadix));
            } else {
                System.out.println("1".repeat(Math.max(0, num)));
            }
        } else {
            if (targetRadix <= 10) {
                double d = Double.parseDouble(number);
                int i = (int) d;
                if (sourceRadix == 10) {
                    System.out.print(convert(i, targetRadix));
                    System.out.println(convertDoubleToBaseX(d - i, targetRadix));
                }
            } else {
                double d = convertToDec(number, sourceRadix);
                int i = (int)d;
                System.out.print(convert(i, targetRadix));
                System.out.println(convertDoubleToBaseX(d - i, targetRadix));
            }
        }
    }

    private static String convertDoubleToBaseX(double d, int targetRadix) {
        StringBuilder result = new StringBuilder(".");
        //int n = String.valueOf(d).split("\\.")[1].length();
        int n = 5;
        double []doubles = new double[n];
        for (int i = 0; i < n; i++) {
            doubles[i] = d * targetRadix;
            d = d * targetRadix - (int) doubles[i];
        }
        for (int i = 0; i < n; i++) {
            result.append(Character.forDigit((int)doubles[i], targetRadix));
        }
        return result.toString();
    }
}
