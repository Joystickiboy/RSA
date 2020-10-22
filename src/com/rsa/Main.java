package com.rsa;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.math.*;
import java.util.Random;

public class Main {

    static Scanner sc = new Scanner(System.in);

    // const variables
    public static BigDecimal SQRT_DIG = new BigDecimal(150);
    public static BigDecimal SQRT_PRE = new BigDecimal(10).pow(SQRT_DIG.intValue());


    public static void main(String[] args)throws Exception {

        /*
        Beginning of main program ENTRY POINT
         */
        System.out.println("1. to generate key pair \n2. to encrypt message \n3. to decrypt message");
        int choice = sc.nextInt();
        if (choice == 1){ // Key generation
            BigInteger[] tb = genKey();
            BigInteger d = tb[0];
            BigInteger n = tb[1];
            BigInteger e = tb[2];

            System.out.println("Would you like to save these to private.key & public.key ? ");
            String save = sc.next();
            if (save.equals("y")){
                String sD = d.toString();
                String sN = n.toString();
                String sE = e.toString();

                BufferedWriter writer = new BufferedWriter(new FileWriter("private.key"));
                writer.write(sD);
                writer.close();
                BufferedWriter wr = new BufferedWriter(new FileWriter("public.key"));
                wr.write(sN+"\n"+sE);
                wr.close();
            }

        } else if (choice == 2){
            System.out.println("Would you like to read public key from file public.key ?");
            String save = sc.next();

            if (save.equals("y")){
                //open file and extract public key
                File file = new File("public.key");

                BufferedReader br = new BufferedReader(new FileReader(file));
                String st;
                BigInteger n = new BigInteger("0");
                BigInteger e = new BigInteger("0");
                int i = 0;
                while ((st = br.readLine()) != null) {
                    if (i == 0)
                        n = new BigInteger(st);
                    if (i == 1)
                        e = new BigInteger(st);
                    i++;
                }
                br.close();

                BigInteger m;
                save="";
                System.out.println("Read file to encrypt ?");
                save = sc.next();
                if (save.equals("y")) {

                    File readFile;
                    byte[] fileContent = Files.readAllBytes(Paths.get("test.file"));
                    m = new BigInteger(fileContent);

                } else {

                    System.out.println("Please enter message to encrypt : ");
                    sc.nextLine();
                    String stri = sc.nextLine();
                    m = getInt(stri);
                }
                // Encryption
                BigInteger c = encrypt(n, e, m);


                save = "";
                System.out.println("Would you like to save encrypted message to file.cry ?");
                save = sc.next();
                if (save.equals("y")){
                    BufferedWriter writer = new BufferedWriter(new FileWriter("file.cry"));
                    writer.write(c.toString());
                    writer.close();
                }
            } else {
                System.out.println("Please enter public key n : ");
                BigInteger n = sc.nextBigInteger();
                System.out.println("Please enter public key e : ");
                BigInteger e = sc.nextBigInteger();
                System.out.println("Please enter message to encrypt : ");
                BigInteger m = sc.nextBigInteger();


                BigInteger c = encrypt(n, e, m);


                save = "";
                System.out.println("Would you like to save encrypted message to file.cry ?");
                save = sc.next();
                if (save.equals("y")){
                    BufferedWriter writer = new BufferedWriter(new FileWriter("file.cry"));
                    writer.write(c.toString());
                    writer.close();
                }
            }
        } else if (choice == 3){
            System.out.println("Would you like to read keys from file private.key & public.key ?");
            String save = sc.next();

            if (save.equals("y")) {
                File file = new File("public.key");

                BufferedReader br = new BufferedReader(new FileReader(file));
                String st;
                BigInteger n = new BigInteger("0");
                BigInteger e = new BigInteger("0");
                int i = 0;
                while ((st = br.readLine()) != null) {
                    if (i == 0)
                        n = new BigInteger(st);
                    if (i == 1)
                        e = new BigInteger(st);
                    i++;
                }
                br.close();

                File file2 = new File("private.key");

                BufferedReader br2 = new BufferedReader(new FileReader(file2));
                String st2;
                st = br2.readLine();
                BigInteger d = new BigInteger(st);
                br2.close();
                save = "";
                System.out.println("Read file from file.cry ?");
                save = sc.next();
                if (save.equals("y")){
                    File file3 = new File("file.cry");

                    BufferedReader br3 = new BufferedReader(new FileReader(file3));
                    String st3;
                    st3 = br3.readLine();

                    BigInteger c = new BigInteger(st3);

                    br3.close();
                    BigInteger decrypted = decrypt(n, d, c);
                    String dec = decrypted.toString();
                    String fin = getString(dec);
                    System.out.println(fin);

                } else if (save.equals("n")){
                    File readsFile = new File("file.cry");
                    byte[] fileContents = Files.readAllBytes(readsFile.toPath());
                    BigInteger h = new BigInteger(fileContents);
                    BigInteger decrypted = decrypt(n, d, h);
                    byte b1[] = decrypted.toByteArray();
                    try (FileOutputStream fos = new FileOutputStream("test.decr")) {
                        fos.write(b1);
                    }

                } else {
                    System.out.println("Please enter message to decrypt : ");
                    BigInteger c = sc.nextBigInteger();
                    BigInteger decrypted = decrypt(n, d, c);
                }

            } else {
                System.out.println("Please enter public key n :");
                BigInteger n = sc.nextBigInteger();
                System.out.println("Please enter super private key d :");
                BigInteger d = sc.nextBigInteger();
                System.out.println("Please enter encrypted message :");
                BigInteger c = sc.nextBigInteger();
                decrypt(n, d, c);
            }

        } else {
            System.out.println("You have chosen nothing you whale looking dumb fuck");
        }
    }

    // END OF MAIN

    public static BigInteger[] genKey(){
        BigInteger p1 = BigInteger.probablePrime(4096, new Random());
        BigInteger p2 = BigInteger.probablePrime(4096, new Random());

        BigInteger n = p1.multiply(p2);
        BigInteger phi = (p1.subtract(BigInteger.valueOf(1))).multiply(p2.subtract(BigInteger.valueOf(1)));
        int e = findE(phi);

        BigDecimal d = BigDecimal.valueOf(0.1);
        int k = 2;
        while (!isInt(d)){
            BigDecimal s1 = new BigDecimal((BigInteger.valueOf(k).multiply(phi)).add(BigInteger.valueOf(1)));
            BigDecimal bigE = new BigDecimal(BigInteger.valueOf(e));
            d = s1.divide(bigE, 2, RoundingMode.HALF_UP);
            if (isInt(d)){
                break;
            }
            k++;
        }

        BigInteger[] bITab = new BigInteger[3];
        bITab[0] = d.toBigInteger();
        bITab[1] = n;
        bITab[2] = BigInteger.valueOf(e);

        return bITab;
    }

    public static BigInteger decrypt(BigInteger n, BigInteger d, BigInteger c){
        BigInteger decrypt = c.modPow(d,n);
        //System.out.println("Decrypted message :  "+decrypt);
        return decrypt;
    }

    public static BigInteger encrypt(BigInteger n, BigInteger e, BigInteger m){
        BigInteger c = m.modPow(e,n);
        System.out.println("Crypted message");
        return c;
    }

    public static boolean isPrime(BigInteger i){
        boolean isPrime = false;
        boolean primar = true;
        BigDecimal bigI = new BigDecimal(i);
        BigDecimal y = bigSqrt(bigI);
        BigDecimal x = y.setScale(0, RoundingMode.FLOOR);
        long sX = x.longValue();
        BigInteger mod;
        if (i.mod(BigInteger.TWO).compareTo(BigInteger.ZERO) != 0) {
            for (int j = 3; j <= sX; j++) {
                mod = i.mod(BigInteger.valueOf(j));
                if (mod.compareTo(BigInteger.valueOf(0)) == 0) {
                    primar = false;
                    break;
                } else if (mod.compareTo(BigInteger.valueOf(0)) != 0) { // i%j != 0
                    primar = true;
                    continue;
                }
            }
            if (primar) {
                return isPrime = true;
            } else {
                return isPrime = false;
            }
        } else
            return false;
    }

    public static boolean isInt(BigDecimal d){
        BigDecimal dFloor = d.setScale(0, RoundingMode.FLOOR);
        BigInteger dInt = d.toBigInteger();
        if (d.compareTo(dFloor) == 0) {
            return true;
        } else {
            return false;
        }
    }

    public static int findE(BigInteger p){
        for (int i = 3; i < 23; i+=2) {
            if (!p.mod(BigInteger.valueOf(i)).equals(BigInteger.valueOf(0))){
                return i;
            }
        }
        return 0;
    }

    /**
     * Private utility method used to compute the square root of a BigDecimal.
     *
     * @author Luciano Culacciatti
     * @url http://www.codeproject.com/Tips/257031/Implementing-SqrtRoot-in-BigDecimal
     */
    private static BigDecimal sqrtNewtonRaphson  (BigDecimal c, BigDecimal xn, BigDecimal precision){
        BigDecimal fx = xn.pow(2).add(c.negate());
        BigDecimal fpx = xn.multiply(new BigDecimal(2));
        BigDecimal xn1 = fx.divide(fpx,2*SQRT_DIG.intValue(),RoundingMode.HALF_DOWN);
        xn1 = xn.add(xn1.negate());
        BigDecimal currentSquare = xn1.pow(2);
        BigDecimal currentPrecision = currentSquare.subtract(c);
        currentPrecision = currentPrecision.abs();
        if (currentPrecision.compareTo(precision) <= -1){
            return xn1;
        }
        return sqrtNewtonRaphson(c, xn1, precision);
    }

    public static BigDecimal bigSqrt(BigDecimal c){
        return sqrtNewtonRaphson(c,new BigDecimal(1),new BigDecimal(1).divide(SQRT_PRE));
    }


    public static BigInteger getRandomBigInteger() {
        BigInteger result = new BigInteger(60, new Random()); // (2^numbits-1) = maximum value
        return result;
    }

    public static BigInteger getInt(String s){

        int[] ch = new int[s.length()];
        for (int i = 0; i < s.length(); i++) {
            ch[i] = s.charAt(i);
            //System.out.println(ch[i]);
        }


        String str = "";
        String tmp = "";
        for (int i = 0; i < ch.length; i++) {
            ch[i] += 100;
            str += String.valueOf(ch[i]);
        }
        return new BigInteger(str);
    }

    public static String getString(String s){
        List<String> sList= getParts(s, 3);
        String m = "";
        String tmpS = "";
        char a;
        int tmp = 0;
        for (int i = 0; i < sList.size(); i++) {
            tmpS = sList.get(i);
            tmp = Integer.parseInt(tmpS) - 100;
            a = (char)tmp;
            m += a;
        }
        return m;
    }

    private static List<String> getParts(String string, int partitionSize) {
        List<String> parts = new ArrayList<String>();
        int len = string.length();
        for (int i=0; i<len; i+=partitionSize)
        {
            parts.add(string.substring(i, Math.min(len, i + partitionSize)));
        }
        return parts;
    }
}
