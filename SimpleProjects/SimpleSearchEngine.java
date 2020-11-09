package search;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class SimpleSearchEngine {
    public static void main(String[] args) {
        Finder finder = new Finder(args);
        finder.start();

    }
}

class Key {
    private final int hashCode;
    private String s;

    public Key(String s) {
        this.s = s;
        this.hashCode = s.toLowerCase().hashCode();
    }

    public int hashCode() { return hashCode; }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof Key) {
            return this.s.equalsIgnoreCase(((Key)o).s);
        } else {
            return false;
        }

    }
}

class Person {
    String name = "";
    String surname = "";
    String email = "";

    public Person(String str) {
        String []fileds = str.split(" ");
        try {
            this.name =  fileds[0];
            this.surname = fileds[1];
            this.email = fileds[2];
        } catch (ArrayIndexOutOfBoundsException e) {

        }
    }

    @Override
    public String toString() {
        return (name + " " + surname + " " + email).trim();
    }
}

interface FindingMethod {

    enum Method {
        ANY, ALL, NONE
    }

    ArrayList<Person> search(String targets, Map<Key, Set<Integer>> invertedIndex, ArrayList<Person> people);
}

class AnyFindingMethod implements FindingMethod {

    @Override
    public ArrayList<Person> search(String targets, Map<Key, Set<Integer>> invertedIndex, ArrayList<Person> people) {
        Set<Person> found = new HashSet<>();
        for (String target : targets.split(" ")) {
            Key key = new Key(target);
            if (invertedIndex.containsKey(key)) {
                for (int i : invertedIndex.get(key)) {
                    found.add(people.get(i - 1));
                }
            }
        }
        return new ArrayList<>(found);
    }
}

class AllFindingMethod implements FindingMethod {

    @Override
    public ArrayList<Person> search(String targets, Map<Key, Set<Integer>> invertedIndex, ArrayList<Person> people) {
        Set<Person> found = new HashSet<>();
        int count = 0;
        for (String target : targets.split(" ")) {
            if (invertedIndex.containsKey(new Key(target))) {
                count++;
                for (int i : invertedIndex.get(new Key(target))) {
                    found.add(people.get(i - 1));
                }
            }
        }
        if (count == targets.split(" ").length)
            return new ArrayList<>(found);
        else
            return new ArrayList<>();
    }
}

class NoneFindingMethod implements FindingMethod {

    @Override
    public ArrayList<Person> search(String targets, Map<Key, Set<Integer>> invertedIndex, ArrayList<Person> people) {
        ArrayList<Person> found = new ArrayList<>();
        Set<Integer> notInclude = new HashSet<>();
        for (String target : targets.split(" ")) {
            if (invertedIndex.containsKey(new Key(target))) {
                notInclude.addAll(invertedIndex.get(new Key(target)));
            }
        }
        for (int i = 0; i < people.size(); i++) {
            if (!notInclude.contains(i + 1)) {
                found.add(people.get(i));
            }
        }
        return found;
    }
}

class Finder {

    Scanner scanner = new Scanner(System.in);

    String filename;

    ArrayList<Person> people;

    Map<Key, Set<Integer>> invertedIndex;

    FindingMethod method;

    String target;

    ArrayList<Person> find() {
       return method.search(target, invertedIndex, people);
    }

    Finder(String []args) {
        people = new ArrayList<>();
        if (args[0].equals("--data")) {
            filename = args[1];
        }
        try(FileInputStream fis = new FileInputStream(new File(filename))) {
            String allLines = new String(fis.readAllBytes());
            for (String line : allLines.split("\n")) {
                people.add(new Person(line));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int index = 0;
        invertedIndex = new HashMap<>();
        for (Person p : people) {
            index ++;
            addToInvertedIndex(p.name, index);
            addToInvertedIndex(p.surname, index);
            addToInvertedIndex(p.email, index);
        }
    }

    public void addToInvertedIndex(String field, int index) {
        if (!field.isEmpty()) {
            Key key = new Key(field);
            if (invertedIndex.containsKey(key)) {
                invertedIndex.get(key).add(index);
            } else {
                Set<Integer> set = new HashSet<>();
                set.add(index);
                invertedIndex.put(key, set);
            }
        }
    }

    void start() {
        while (true) {
            menu();
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1: {
                    search();
                    break;
                }
                case 2: {
                    print();
                    break;
                }
                case 0: {
                    System.out.println("Bye!");
                    return;
                }
                default:
                    System.out.println("Incorrect option! Try again.");

            }
        }
    }

    private void search() {
        System.out.println("Select a matching strategy: ALL, ANY, NONE");
        String strategy = scanner.nextLine();
        switch (strategy) {
            case "ANY" :
            default:  {
                method = new AnyFindingMethod();
                break;
            }
            case "ALL" : {
                method = new AllFindingMethod();
                break;
            }
            case "NONE": {
                method = new NoneFindingMethod();
                break;
            }
        }
        System.out.println("Enter a name or email to search all suitable people.\n");
        target = scanner.nextLine();
        var found = find();
        if (found.isEmpty()) {
            System.out.println("No matching people found");
        } else {
            System.out.println("Found people:");
            for (Person p: found) {
                System.out.println(p);
            }
        }
    }

    private void print() {
        System.out.println();
        System.out.println("=== List of people ===");
        for (Person p : people) {
            System.out.println(p);
        }
        System.out.println();
    }

    void menu() {
        System.out.println("=== Menu ===\n" +
                "1. Find a person\n" +
                "2. Print all people\n" +
                "0. Exit");
    }
}