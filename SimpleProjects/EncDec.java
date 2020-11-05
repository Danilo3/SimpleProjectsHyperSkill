package encryptdecrypt;

import java.io.*;

class Arguments {
    String mode;
    int key;
    String str;
    String inFilename;
    String outFilename;
    String alg;

    public Arguments(String []args) {
        mode = "enc";
        key = 0;
        str = "";
        inFilename = "";
        outFilename = "";
        alg = "shift";
        try {
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "-mode":
                        mode = args[i + 1];
                        break;
                    case "-key":
                        key = Integer.parseInt(args[i + 1]);
                        break;
                    case "-data":
                        str = args[i + 1];
                        break;
                    case "-out":
                        outFilename = args[i + 1];
                        break;
                    case "-in":
                        inFilename = args[i + 1];
                        break;
                    case "-alg":
                        alg = args[i + 1];
                        break;
                }
            }
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            System.out.println("Error! Wrong args");
            System.exit(-1);
        }
        if (!inFilename.isEmpty() && !str.isEmpty())
            inFilename = "";
    }
}
public class Main {

    public static void main(String[] args) {
        Arguments arguments = new Arguments(args);
        if (!arguments.inFilename.isEmpty()) {
            try (FileInputStream fis = new FileInputStream(new File(arguments.inFilename))) {
                arguments.str = new String(fis.readAllBytes());
            } catch (IOException e) {
                System.out.println("Error! File wrong");
            }
        }
        EncDec encryption = new EncDec(arguments.str, arguments.key, arguments.alg, arguments.mode);
        if (arguments.outFilename.isEmpty()) {
            System.out.println(encryption.encrypt());
        } else {
            try (FileOutputStream fos = new FileOutputStream(new File(arguments.outFilename))) {
                fos.write(encryption.encrypt().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static class EncDec {

        private final String mode;

        public String str;

        public int key;

        public String alg;

        public EncDec(String str, int key, String alg, String mode) {
            this.str = str;
            this.key = key;
            this.alg = alg;
            this.mode = mode;
        }

        public String encrypt() {
            StringBuilder encrypted = new StringBuilder(str.length());
            int a;
            for (int i = 0; i < str.length(); i++) {
                a = str.charAt(i);
                if (alg.equals("shift")) {
                    if (Character.isAlphabetic(a)) {
                        if (mode.equals("enc")) {
                            encrypted.append((char) (a + key > 'z' ? ('a' + key - ('z' - a) - 1) : a + key));
                        } else if (mode.equals("dec")) {
                            encrypted.append((char) (a - key < 'a' ? ('z' - (key - (a - 'a')) + 1) : a - key));
                        }
                    } else {
                        encrypted.append((char)a);
                    }
                } else if (alg.equals("unicode")) {
                    encrypted.append((char) (mode.equals("enc") ? a + key : a - key));
                }
            }
            return encrypted.toString();
        }
    }
}