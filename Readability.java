package readability;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        String text = "";
        try(FileInputStream fis = new FileInputStream(args[0]);
        Scanner scanner = new Scanner(fis)){
            text = scanner.nextLine();
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
        TextScore textScore = new TextScore(text);
        System.out.println(textScore.toString());
        System.out.println("Enter the score you want to calculate:(ARI, FK, SOG, CL, all): ");
        Scanner scanner = new Scanner(System.in);
        String score = scanner.nextLine();
        switch (score) {
            case "all": {
                textScore.printARIScore();
                textScore.printFKScore();
                textScore.printSMOGScore();
                textScore.printCLScore();
                break;
            }
            case "ARI": {
                textScore.printARIScore();
                break;
            }
            case "FK": {
                textScore.printFKScore();
                break;
            }
            case "SMOG": {
                textScore.printSMOGScore();
                break;
            }
            case "CL": {
                textScore.printCLScore();
                break;
            }
            default: {
                break;
            }
        }
        textScore.summary();
    }
}
class TextScore {

    private double ariScore;
    private double smogScore;
    private double fkScore;
    private double clScore;

    private final static int []yearsArr = {6, 7, 9, 10, 11, 12, 13,
                                14, 15, 16, 17, 18, 24};
    private double charactersNum;
    private double sentencesNum;
    private double wordsNum;
    private double syllablesNum;
    private double polysyllablesNum;
    private double avgYear;
    private final String text;
    private double avgChPer100Words;
    private double avgSentencesPer100Words;

    TextScore(String text) {
        this.text = text;
        this.charactersNum = 0;
        this.sentencesNum = 0;
        this.wordsNum = 0;
        count();
        ariScore = getARIScore();
        fkScore = getFKScore();
        smogScore = getSMOGScore();
        clScore = getCLScore();
        avgYear = (countYear(ariScore) + countYear(fkScore) + countYear(smogScore) + countYear(clScore)) / 4.0;
    }

    private double getCLScore() {
        clScore = (0.0588 * avgChPer100Words) - (0.296 * avgSentencesPer100Words) - 15.8;
        return clScore;
    }

    private double getSMOGScore() {
        smogScore = (1.043 * Math.sqrt(polysyllablesNum * (30 / sentencesNum))) + 3.1291;
        return smogScore;
    }

    private double getFKScore() {
        fkScore = (0.39 * (wordsNum / sentencesNum)) + (11.8 * (syllablesNum / wordsNum)) - 15.59;
        return fkScore;
    }

    private int countYear(double score)
    {
        int year;
        for (int i = 0; i < yearsArr.length; i++) {
            if (i >= Math.round(score)) {
                year = yearsArr[i - 1];
                return year;
            }
        }
        year = yearsArr[yearsArr.length - 1];
        return year;
    }

    private void count()
    {
        int syllables = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) != ' ' && text.charAt(i)!= '\t' && text.charAt(i)!= '\n')
                charactersNum++;
        }
        for (String sentence : text.split("[.!?]")){
            wordsNum += sentence.trim().split("\\s+").length;
            for (String word: sentence.trim().split("\\s+")) {
                syllables = countSyllables(word);
                if (syllables > 2)
                    polysyllablesNum ++;
                syllablesNum += syllables;
            }
            sentencesNum ++;
        }
        avgChPer100Words = (charactersNum / (wordsNum / 100));
        avgSentencesPer100Words = (sentencesNum / (wordsNum / 100));
    }

    private double getARIScore() {
        ariScore = (4.71 * (charactersNum / wordsNum)) + (0.5 * (wordsNum / sentencesNum)) -21.43;
        return ariScore;
    }

    public void printARIScore() {
        System.out.println("Automated Readability Index: " +
                String.format("%.2f", ariScore) +
                String.format("(about %s year olds).", countYear(ariScore)));
    }
    public void printFKScore() {
        System.out.println("Flesch–Kincaid readability tests: " +
                String.format("%.2f", fkScore) +
                String.format("(about %s year olds).", countYear(fkScore)));
    }
    public void printSMOGScore() {
        System.out.println("Simple Measure of Gobbledygook: " +
                String.format("%.2f", smogScore) +
                String.format("(about %s year olds).", countYear(smogScore)));
    }

    public void printCLScore() {
        System.out.println("Coleman–Liau index: " +
                String.format("%.2f", clScore) +
                String.format("(about %s year olds).", countYear(clScore)));
    }

    public void summary() {
        System.out.printf("This text should be understood in average by %.2f year olds.\n", avgYear);
    }

    @Override
    public String toString() {
        return "The text is:\n"+
                text +
                "\n" +
                "Words: " +
                (int)wordsNum +
                "\n" +
                "Sentences: " +
                (int)sentencesNum +
                "\n" +
                "Characters: " +
                (int)charactersNum +
                "\n" +
                "Syllables: " +
                (int)syllablesNum +
                "\n" +
                "Polysyllables: " +
                (int)polysyllablesNum +
                "\n";
    }

    protected static int countSyllables(String word)
    {
        int count = 0;
        word = word.toLowerCase();

        if (word.charAt(word.length()-1) == 'e') {
            if (silente(word)){
                String newword = word.substring(0, word.length()-1);
                count = count + countit(newword);
            } else {
                count++;

            }
        } else {
            count = count + countit(word);
        }
        return count;
    }

    private static int countit(String word) {
        int count = 0;
        Pattern splitter = Pattern.compile("[^aeiouy]*[aeiouy]+");
        Matcher m = splitter.matcher(word);

        while (m.find()) {
            count++;
        }
        return count;
    }

    private static boolean silente(String word) {
        word = word.substring(0, word.length()-1);

        Pattern yup = Pattern.compile("[aeiouy]");
        Matcher m = yup.matcher(word);

        if (m.find()) {
            return true;
        } else
            return false;
    }
}