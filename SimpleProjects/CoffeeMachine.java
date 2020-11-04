package machine;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        CoffeeMachine coffeeMachine = new CoffeeMachine();
        coffeeMachine.run();
    }
}


class CoffeeMachine {
    private static Scanner scanner;

    private static int money;
    private static int water;
    private static int milk;
    private static int beans;
    private static int dis_cups;

    public CoffeeMachine()
    {
        scanner = new Scanner((System.in));
        money = 550;
        water = 400;
        milk = 540;
        beans = 120;
        dis_cups = 9;
    }

    private static void print() {
        System.out.printf("\nThe coffee machine has:\n" +
                "%d of water\n" +
                "%d of milk\n" +
                "%d of coffee beans\n" +
                "%d of disposable cups\n" +
                "%d of money\n\n", water, milk, beans, dis_cups, money);
    }

    private static void buy(int choice) {
        System.out.println("I have enough resources, making you a coffee!\n");
        switch (choice) {
            case 1: {
                money += 4;
                break;
            }
            case 2: {
                money += 7;
                break;
            }
            case 3: {
                money += 6;
                break;
            }
        }
        dis_cups -= 1;
    }

    private static void fill() {
        System.out.println("\nWrite how many ml of water do you want to add:");
        water += scanner.nextInt();
        System.out.println("Write how many ml of milk do you want to add:");
        milk += scanner.nextInt();
        System.out.println("Write how many grams of coffee beans do you want to add:");
        beans += scanner.nextInt();
        System.out.println("Write how many disposable cups of coffee do you want to add:");
        dis_cups += scanner.nextInt();
        System.out.println();
    }

    private static boolean isEnough(int choice) {
        int prevWater = water;
        int prevBeans = beans;
        int prevMilk = milk;
        switch (choice) {
            case 1: {
                water -= 250;
                beans -= 16;
                break;
            }
            case 2: {
                water -= 350;
                milk -= 75;
                beans -= 20;
                break;
            }
            case 3: {
                water -= 200;
                milk -= 100;
                beans -= 12;
                break;
            }
        }
        if (water < 0) {
            System.out.println("Sorry, not enough water!\n");
            water = prevWater;
            milk = prevMilk;
            beans = prevBeans;
            return false;
        }
        if (milk < 0) {
            System.out.println("Sorry, not enough milk!\n");
            milk = prevMilk;
            water = prevWater;
            beans = prevBeans;
            return false;
        }
        if (beans < 0) {
            System.out.println("Sorry, not enough coffee beans!\n");
            beans = prevBeans;
            milk = prevMilk;
            water = prevWater;
            return false;
        }
        if (dis_cups == 0) {
            System.out.println("Sorry, not enough disposable cups!\n");
            return false;
        }
        return true;
    }

    public void run() {
        while (true) {
            System.out.println("Write action (buy, fill, take, remaining, exit):");
            String action = scanner.next();
            switch (action) {
                case "take": {
                    System.out.printf("\nI gave you $%d\n\n", money);
                    money = 0;
                    break;
                }
                case "fill": {
                    fill();
                    break;
                }
                case "buy": {
                    System.out.println("\nWhat do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino:");
                    try {
                        int choice = scanner.nextInt();
                        if (isEnough(choice))
                            buy(choice);
                    } catch (InputMismatchException e) {
                        break;
                    }
                    break;
                }
                case "remaining": {
                    print();
                    break;
                }
                case "exit": {
                    System.exit(0);
                    break;
                }
            }
        }
    }
}