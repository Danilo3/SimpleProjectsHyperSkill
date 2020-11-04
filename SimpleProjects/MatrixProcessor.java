package processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        printMenu();
        int choice = -1;
        while (choice != 0)
        {
            System.out.print("Your choice: ");
            choice = scanner.nextInt();
            processChoice(choice);
            if (choice != 0) {
                System.out.println();
                printMenu();
            }
        }
    }

    public static void processChoice(int choice)
    {
        try {
            switch (choice) {
                case 1: {
                    Matrix[] matrices = inputMatrices();
                    add(matrices).print();
                    break;
                }
                case 2: {
                    Matrix matrix = inputMatrix("");
                    int n = inputConstant();
                    System.out.println("The result is:");
                    matrix.multiply(n).print();
                    break;
                }
                case 3: {
                    Matrix[] matrices = inputMatrices();
                    multiply(matrices).print();
                    break;
                }
                case 4: {

                    printTransposeMenu();
                    int transposeChoice = new Scanner(System.in).nextInt();
                    processTransposeChoice(transposeChoice);
                    break;
                }
                case 5: {
                    Matrix matrix = inputMatrix("");
                    System.out.println("The result is: \n" + matrix.determinant());
                    break;
                }
                case 6 : {
                    Matrix matrix = inputMatrix("");
                    System.out.println("The result is: \n");
                    matrix.inverse().print();
                }
                case 0: {
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("The operation cannot be performed.");
        }
    }

    private static void processTransposeChoice(int transposeChoice) {
        Matrix matrix = inputMatrix("");
        switch (transposeChoice){
            case 1: {
                matrix.transposeMainDiagonal().print();
                break;
            }
            case 2 : {
                matrix.transposeSideDiagonal().print();
                break;
            }
            case 3: {
                matrix.transposeVerticalLine().print();
                break;
            }
            case 4 :{
                matrix.transposeHorizontalLine().print();
                break;
            }
        }
    }

    private static int inputConstant() {
        return new Scanner(System.in).nextInt();
    }

    private static Matrix multiply(Matrix[] matrices) {
        System.out.println("The result is:");
        return matrices[0].multiply(matrices[1]);
    }

    private static Matrix add(Matrix[] matrices) {
        System.out.println("The result is:");
        return matrices[0].sum(matrices[1]);
    }

    private static Matrix[] inputMatrices() {
        Matrix [] matrices = new Matrix[2];
        matrices[0] = inputMatrix("first");
        matrices[1] = inputMatrix("second");
        return matrices;
    }

    private static Matrix inputMatrix(String num) {
        Scanner scanner = new Scanner(System.in);
        System.out.printf("Enter size of %s matrix:", num);
        int x = scanner.nextInt();
        int y = scanner.nextInt();
        System.out.printf("\nEnter %s matrix:\n", num);

        Matrix m = new Matrix(x, y);
        m.input();
        return m;
    }

    public static void printMenu() {
        System.out.println("1. Add matrices\n" +
                "2. Multiply matrix by a constant\n" +
                "3. Multiply matrices\n" +
                "4. Transpose matrix\n" +
                "5. Calculate a determinant\n" +
                "6. Inverse matrix\n" +
                "0. Exit");
    }

    public static void printTransposeMenu() {
        System.out.println("1. Main diagonal\n" +
                "2. Side diagonal\n" +
                "3. Vertical line\n" +
                "4. Horizontal line");
    }
}

class Matrix {

    private final ArrayList<ArrayList<Double>> m;
    private final int x;
    private final int y;
    private boolean isDouble;


    Matrix(int x, int y)
    {
        this.x = x;
        this.y = y;
        m = new ArrayList<>(x);
        for (int i = 0; i < x; i++) {
            m.add(new ArrayList<>());
        }
    }

    public void input()
    {
        Scanner scanner = new Scanner(System.in);
        for (int i = 0; i < x; i++) {
            String str = scanner.nextLine();
            isDouble = str.contains(".");
            for (int j = 0; j < y; j++) {
                m.get(i).add(Double.parseDouble(str.split("[ \n]")[j]));
            }
        }
    }

    public void print() {
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                if (isDouble)
                    System.out.printf("%.2f ", get(i, j));
                else {
                    System.out.printf("%d ", get(i, j).intValue());
                }
            }
            System.out.println();
        }
    }

    private Double get(int i, int j)
    {
        return m.get(i).get(j);
    }

    private void set(int i, int j, double value)
    {
        m.get(i).set(j, value);
    }

    public Matrix sum(Matrix a)
    {

        Matrix b = new Matrix(a.x, a.y);
        b.isDouble = a.isDouble;
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                b.m.get(i).add(a.get(i, j) + this.get(i, j));
            }
        }
        return b;
    }

    public Matrix multiply(double n)
    {

        Matrix b = new Matrix(this.x, this.y);
        b.isDouble = this.isDouble;
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                b.m.get(i).add(n * this.get(i, j));
            }
        }
        return b;
    }

    private double multiplyCell(Matrix a, int i, int j)
    {
        double result = 0;

        List<Double> row = this.m.get(i);
        List<Double> column = a.getColumn(j);

        for (int k = 0; k < row.size(); k++){
            result += column.get(k) * row.get(k);
        }
        return result;
    }


    private ArrayList<Double> getColumn(int j)
    {
        ArrayList<Double> column = new ArrayList<>(y);
        for (int i = 0; i < x; i++) {
            column.add(get(i, j));
        }
        return column;
    }

    public Matrix multiply(Matrix a)
    {
        Matrix b = new Matrix(this.x, a.y);
        b.isDouble = a.isDouble;
        for (int i = 0; i < this.x; i++) {
            for (int j = 0; j < a.y; j++) {
                b.m.get(i).add(multiplyCell(a, i, j));
            }
        }
        return b;
    }

    public Matrix transposeMainDiagonal() {
        Matrix b = new Matrix(this.y, this.x);
        b.isDouble = this.isDouble;
        for (int i = 0; i < this.y; i++) {
            for (int j = 0; j < this.x; j++) {
                b.m.get(i).add(getColumn(i).get(j));
            }
        }
        return b;
    }

    public Matrix transposeSideDiagonal() {
        Matrix b = new Matrix(this.x, this.y);
        b.isDouble = this.isDouble;
        for (int i = this.y - 1, ii = 0; i >= 0; i--, ii++) {
            for (int j = 0, jj = this.x - 1; j < this.x; j++, jj--) {
                b.m.get(ii).add(getColumn(i).get(jj));
            }
        }
        return b;
    }

    public Matrix transposeVerticalLine() {
        Matrix b = new Matrix(this.x, this.y);
        b.isDouble = this.isDouble;
        for (int i = 0; i < this.x; i++) {
            for (int j = this.y - 1; j >= 0; j--) {
                b.m.get(i).add(this.get(i, j));
            }
        }
        return b;
    }

    public Matrix transposeHorizontalLine() {
        Matrix b = new Matrix(this.x, this.y);
        b.isDouble = this.isDouble;
        for (int i = this.x - 1, ib = 0; i >= 0; i--, ib++) {
            for (int j = 0; j < this.y; j++) {
                b.m.get(ib).add(this.get(i, j));
            }
        }
        return b;
    }

    public double determinant() {
        double d = 0;
        if (this.x != this.y)
            throw new NullPointerException("Not a square matrix");
        if (this.x == 2) {
            return (get(0,0 ) * get(1, 1) - get(0, 1) * get(1, 0));
        }
        else if (this.x >= 3) {
            for (int i = 0; i < this.x; i++)
            {
                if (i % 2 == 0)
                    d += this.subMatrix(0, i).determinant() * get(0, i);
                else
                    d -= this.subMatrix(0, i).determinant() * get(0, i);
            }
        }
        return d;
    }

    public Matrix subMatrix(int i, int j) {
        Matrix b = new Matrix(this.x - 1, this.y - 1);
        b.isDouble = this.isDouble;
        for (int k = 0, bk = 0; k < this.x; k++, bk++) {
            if (k != i) {
                for (int l = 0; l < this.y; l++) {
                    if (l != j) {
                        b.m.get(bk).add(get(k, l));
                    }
                }
            }
            else {
                bk--;
            }
        }
        return b;
    }

    public Matrix cofactorMatrix(){
        Matrix c = new Matrix(this.x, this.y);
        c.isDouble =  this.isDouble;
        for (int i = 0; i < this.x; i++) {
            for (int j = 0; j < this.y; j++) {
                c.m.get(i).add(this.subMatrix(i, j).determinant() * Math.pow(-1, i + j + 2));
            }
        }
        return c;
    }

    public Matrix inverse() {
        Matrix i = new Matrix(this.x, this.y);
        i.isDouble = true;
        double d = this.determinant();
        if (d == 0)
            throw new NullPointerException("This matrix doesn't have an inverse.");
        i = this.cofactorMatrix().transposeMainDiagonal().multiply((1.0 / d));
        i.isDouble = true;
        return i;

    }
}
