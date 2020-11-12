package phonebook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Time;
import java.time.LocalTime;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("Start searching... (linear search)");
        PhoneBook pb = new PhoneBook();
        pb.find(PhoneBook.SortType.NONE);
        LocalTime time = new Time(pb.endSearchTime - pb.startTime).toLocalTime();
        System.out.println("Found 500 / 500 entries. Time taken: " +time.getMinute() + " min. " + time.getSecond() + " sec. " + time.getNano() / 1000000 + "ms.");
        System.out.println();

        search(pb, "bubble sort + jump search", PhoneBook.SortType.BUBBLE, "Sorting");
        search(pb, "quick sort + binary search", PhoneBook.SortType.QUICK, "Sorting");
        search(pb, "hash table", PhoneBook.SortType.HASH, "Creating");
    }

    public static void search(PhoneBook pb, String name, PhoneBook.SortType st, String add) {
        System.out.println("Start searching " + "(" + name + ")...");
        pb.find(st);
        LocalTime timeSort = new Time(pb.endSortTime - pb.startSortTime).toLocalTime();
        LocalTime timeSearch = new Time(pb.endSearchTime - pb.startSearchTime).toLocalTime();
        System.out.println("Found 500 / 500 entries. Time taken: " + (timeSort.getMinute() + timeSearch.getMinute())
                + " min. " + (timeSort.getSecond() + timeSearch.getSecond()) + " sec. " + (timeSort.getNano() + timeSearch.getNano()) / 1000000 + "ms.");
        System.out.println(add +" time: " + timeSort.getMinute() +" min. "+ timeSort.getSecond() +" sec. " +timeSort.getNano() / 1000000 +" ms");
        System.out.println("Searching time:"  + timeSearch.getMinute() +" min. "+ timeSearch.getSecond() +" sec. " +timeSearch.getNano() / 1000000 +" ms");
        System.out.println();
    }
}

class Record {

    String name;
    String surname;
    String phone;


    enum Mode {
        Phone, Find
    }

    Record(String str, Mode mode) {
        try {
            String []fields = str.split(" ");
            if (mode.equals(Mode.Phone)) {
                phone = fields[0];
                name = fields[1];
                surname = fields[2];
            } else if (mode.equals(Mode.Find)) {
                name = fields[0];
                surname = fields[1];
                phone = "";
            }
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            System.err.println("Errors while reading lines: " + e.getMessage() + " mode = " + mode.name());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Record record = (Record) o;
        return Objects.equals(name, record.name) &&
                Objects.equals(surname, record.surname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname);
    }
}


class PhoneBook {
    private final static String  directoryFilePath = "/home/dan/Downloads/directory.txt";
    private final static String  findFilePath = "/home/dan/Downloads/find.txt";
    ArrayList <Record> records;
    ArrayList <Record> found;
    ArrayList <Record> targets;

    long startTime;
    long startSortTime;
    long endSortTime;
    long startSearchTime;
    long endSearchTime;
    HashMap<Integer, Record> recordHashMap;

    enum SortType {
        NONE, BUBBLE, QUICK, HASH
    }

    PhoneBook () {
        startTime = System.currentTimeMillis();
        records = new ArrayList<>(1000000);
        targets = new ArrayList<>(500);
        try (FileInputStream fis = new FileInputStream(new File(directoryFilePath));
            FileInputStream fis2 = new FileInputStream(new File(findFilePath))) {
            String lines = new String(fis.readAllBytes());
            for (String line : lines.split("\n")) {
                records.add(new Record(line, Record.Mode.Phone));
            }
            String targetLines = new String(fis2.readAllBytes());
            for (String line : targetLines.split("\n")) {
                targets.add(new Record(line, Record.Mode.Find));
            }
        } catch (IOException e ) {
            System.err.println("file input error");
        }
    }

    private void createHashTable(){
        recordHashMap = new HashMap<>(1000000);
        for (Record record : records) {
            recordHashMap.put(record.hashCode(), record);
        }
    }

    public void find(SortType type) {
        if (!type.equals(SortType.NONE) && !type.equals(SortType.HASH)) {
            startSortTime = System.currentTimeMillis();
            records.sort(new RecordComparator());
            endSortTime = System.currentTimeMillis();
        }
        startSearchTime = System.currentTimeMillis();
        found = new ArrayList<>(500);
        if (type.equals(SortType.HASH)) {
            startSortTime = System.currentTimeMillis();
            createHashTable();
            endSortTime = System.currentTimeMillis();
            startSearchTime = System.currentTimeMillis();
            for (Record target : targets) {
                    found.add(recordHashMap.get(target.hashCode()));
            }
            endSearchTime = System.currentTimeMillis();
            return;
        }
        if (type.equals(SortType.BUBBLE)) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (Record target : targets) {
            for (Record record : records) {
                    if (target.equals(record))
                        found.add(record);
            }
        }
        endSearchTime = System.currentTimeMillis();
    }


}

class RecordComparator implements Comparator<Record> {
    @Override
    public int compare(Record o1, Record o2) {
        return (o1.name + o1.surname).compareTo(o2.name + o2.surname);
    }

}