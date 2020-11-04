package correcter;

import java.io.*;
import java.util.*;
import java.util.stream.Stream;

public class Main {

    public static String byteToString(byte b)
    {
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append((b & (1 << i)) > 0 ? '1' : '0');
        }
        return sb.reverse().toString();
    }
/*

*/
    public static void main(String[] args) {
        String mode;
        Scanner scanner = new Scanner(System.in);
        System.out.print("Write a mode: ");
        mode = scanner.nextLine();
        switch (mode)
        {
            case "encode":
            {
                encode();
                break;
            }
            case "send":
            {
                send();
                break;
            }
            case "decode":
            {
                //decode();
                //break;
            }
        }
//        byte [] bytes;
//        try (FileInputStream fis = new FileInputStream("send.txt");
//             FileOutputStream fos = new FileOutputStream("received.txt")) {
//            bytes = fis.readAllBytes();
//            for (byte aByte : bytes) fos.write(aByte);
//        } catch (I+OException e) {
//            System.out.println(e.getMessage());
//        }
    }

    private static void send() {
        File receivedFile  = new File("received.txt");
        File encodedFile  = new File("encoded.txt");
        try(FileInputStream fis = new FileInputStream(encodedFile);
            OutputStream os = new FileOutputStream(receivedFile)){
            int i = 0;
            for (byte b : fis.readAllBytes())
            {
                i++;
                System.out.print(byteToString(b)+ " ");
            }
            System.out.printf("i ======== %d !!!!\n", i);
            String encodedText = "11110000 11111000 11000000";
            encodedText = removeZeros(encodedText);
            encodedText = String.valueOf(fromBinary(prepareBinStr(encodedText)));
            System.out.printf("encoded.txt: %s\n", encodedText);
            System.out.printf("hex view: %s\n", hexView(encodedText));
            System.out.printf("bin view: %s\n", binView(encodedText));
            System.out.println("\nreceived.txt:");
            String scrambled = scramble(binView(encodedText));
            System.out.printf("bin view: %s\n", splitStrToBytes(scrambled));
            System.out.printf("hex view: %s\n", hexView2(splitStrToBytes(scrambled)));
            os.write(binStrToRealByteArray(scrambled));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static String removeZeros(String encodedText) {
        encodedText = binView(encodedText);
        System.out.printf("промежуточный вариант: %s\n", encodedText);
        ArrayList<String> stringList = new ArrayList<>(Arrays.asList(encodedText.split(" ")));
        for (int i = stringList.size() - 1; i >= 0; i--) {
            if (stringList.get(i).equals("00000000")){
                stringList.remove(i);
            }
            else {
                break;
            }
        }
        int i = 0;
        StringBuilder sb = new StringBuilder();
        for (String s: stringList) {
            sb.append(s);
            sb.append(" ");
            i++;
        }
        System.out.printf("после удаления : %d, %s\n", i, sb.toString());
        return sb.toString();
    }

    private static String scramble(String bin) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random(System.currentTimeMillis());
        for (String str : bin.split(" ")){
            int error = random.nextInt(8);
            sb.append(str);
            sb.deleteCharAt(sb.length() - 1 - error);
            sb.insert(sb.length() - error, str.charAt(error) == '1' ? '0' : '1');
        }
        return sb.toString();
    }

    private static String prepareBinStr(String str)
    {
        return str.replaceAll(" ", "");
    }

    private static void encode() {
        File sendFile  = new File("send.txt");
        File encodedFile  = new File("encoded.txt");
        try(FileInputStream fis = new FileInputStream(sendFile);
            OutputStream os = new FileOutputStream(encodedFile)){
            String sendText = new String(fis.readAllBytes());
            System.out.printf("send.txt: %s\n", sendText);
            System.out.printf("hex view: %s\n", hexView(sendText));
            System.out.printf("bin view: %s\n", binView(sendText));
            System.out.println("\nencoded.txt:");
            String expanded = expand(binView(sendText));
            String encoded = encodeFile(prepareBinStr(binView(sendText)));
            System.out.printf("expand: %s\n", expanded);
            expanded = parity(expanded);
            System.out.printf("parity: %s\n", expanded);
            System.out.printf("parity 2: %s\n", splitStrToBytes(encoded));
            System.out.printf("hex view: %s\n", hexView2(expanded));
            os.write(binStrToRealByteArray(expanded));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static String splitStrToBytes(String encoded){
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < encoded.length(); i += 8) {
            builder.append(encoded, i, Math.min(i + 8, encoded.length()));
            builder.append(" ");
        }
        return builder.toString();
    }

    private static int charXor(char ch1, char ch2, char ch3)
    {
        byte b1 = (byte)(ch1 == '0' ? 0b0 : 0b1);
        byte b2 = (byte)(ch2 == '0' ? 0b0 : 0b1);
        byte b3 = (byte)(ch3 == '0' ? 0b0 : 0b1);

        return b1 ^ b2 ^ b3;
    }

    private static String encodeFile(String binaryString) {

        String encoded = "";

        for (int i = 0; i < binaryString.length(); i += 3) {

            int startSubIndex = i;
            int stopSubIndex = Math.min(i+3, binaryString.length());

            String currSub = binaryString.substring(startSubIndex, stopSubIndex);

            String encodedPart;

            if (currSub.length() == 3) {
                encodedPart =
                        currSub.substring(0, 1).repeat(2) +
                                currSub.substring(1, 2).repeat(2) +
                                currSub.substring(2, 3).repeat(2);
            } else if (currSub.length() == 2) {
                encodedPart =
                        currSub.substring(0, 1).repeat(2) +
                                currSub.substring(1, 2).repeat(2) + "00";
            } else if (currSub.length() == 1) {
                encodedPart =
                        currSub.substring(0, 1).repeat(2) + "0000";
            } else {
                encodedPart = "000000";
            }

            int parityCounts = 0;

            if (encodedPart.charAt(0) == '1') {
                parityCounts++;
            }

            if (encodedPart.charAt(2) == '1') {
                parityCounts++;
            }

            if (encodedPart.charAt(4) == '1') {
                parityCounts++;
            }

            if (parityCounts % 2 == 1) {
                encodedPart += "11";
            } else {
                encodedPart += "00";
            }

            encoded += encodedPart;
        }

        return encoded;
    }

    private static byte[] fromBinary(String s) {
        int sLen = s.length();
        byte[] toReturn = new byte[(sLen + Byte.SIZE - 1) / Byte.SIZE];
        char c;
        for (int i = 0; i < sLen; i++)
            if ((c = s.charAt(i)) == '1')
                toReturn[i / Byte.SIZE] = (byte) (toReturn[i / Byte.SIZE] | (0x80 >>> (i % Byte.SIZE)));
            else if (c != '0')
                throw new IllegalArgumentException();
        return toReturn;
    }

    private static byte getHalfByte(String str, int from, int to)
    {
        return Byte.parseByte(str.substring(from, to), 2);
    }

    private static byte[] binStrToRealByteArray(String binStr)
    {
        byte []arr = new byte[2048];
        int count = 0;
        int max = binStr.split(" ").length - 2;
        int dest = 0;
        for (String s : binStr.split(" ")) {
            byte [] chunck = fromBinary(s);
            System.arraycopy(chunck, 0, arr, dest, chunck.length);
            dest += chunck.length;
        }
        return arr;
    }

    private static String hexView2(String parity)
    {
        StringBuilder builder = new StringBuilder();
        byte b;
        for (String word: parity.split(" ")) {
            b = getHalfByte(word, 0, 4);
            builder.append(halfByteToHex(b, 1));
            b = getHalfByte(word, 4, 8);
            builder.append(halfByteToHex(b, 1));
            builder.append(" ");
        }
        return builder.toString();
    }

    private static String parity(String expanded) {
        StringBuilder sb = new StringBuilder(expanded);
        for (int i = 0; i < expanded.length(); i++) {
            if (sb.charAt(i) == '.') {
                char ch = (charXor(sb.charAt(i - 5),
                        sb.charAt(i - 3), sb.charAt(i - 1)) == 1) ? '1' : '0';
                sb.deleteCharAt(i);
                sb.insert(i, ch);
                sb.deleteCharAt(i + 1);
                sb.insert(i + 1, ch);
            }
        }
        return sb.toString();
    }

    private static int getBit(byte b, int n)
    {
        byte x = (byte) 0b10000000;
        return b & (x >> n);
    }

    private static byte setBit(byte b, int n, int bit)
    {
        byte x = (byte) 0b10000000;
        if (bit == 1)
            return (byte) (b | (x >> n));
        else
            return (byte) (b & ~(x >> n));
    }

    private static String expand(String str)
    {
        int count  = 0;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == ' ')
                continue;
            builder.append(str.charAt(i));
            builder.append(str.charAt(i));
            count += 2;
            if (count % (6) == 0) {
                builder.append(".. ");
            }
        }
        if (!builder.toString().endsWith(".. "))
        {
            int i = builder.length() - 1;
            while (builder.charAt(i) != '.') {
                i--;
            }
            builder.append("0".repeat(Math.max(0, builder.length() - 4 - i)));
            builder.append("..");
        }
        return builder.toString();
    }

    private static String binView(String str) {
        StringBuilder sb = new StringBuilder();
        for (byte b: str.getBytes()) {
            sb.append(byteToString(b));
            sb.append(" ");
        }
        return sb.toString();
    }

    private static char halfByteToHex(byte b, int half){
        int n = 0;
        if (half == 1) {
            for (int i = 0; i < 4; i++) {
                n += ((b & (1 << i)) > 0 ? Math.pow(2, i) : 0);
            }
        }
        else if (half == 2) {
            for (int i = 4, j = 0; i < 8; i++, j++) {
                n += ((b & (1 << i)) > 0 ? Math.pow(2, j) : 0);
            }
        }
        return Character.forDigit(n, 16);
    }

    private static String byteToHex(byte b)
    {
        return String.valueOf(new char[]{halfByteToHex(b, 2), halfByteToHex(b, 1)});
    }

    private static String hexView(String sendText) {
        StringBuilder hex = new StringBuilder();
        for (byte b: sendText.getBytes()) {
            hex.append(byteToHex(b));
            hex.append(" ");
        }
        return hex.toString();
    }
}
