/**
 * Author: Raul Davila 
 * Course: CSc 460 Database Design
 * Professor: Lester McCann
 * Program: Prog1B.java
 * Description: The purpose of this program is to read a bin file we've created
 *              in Prog1A.java. We can then see how it prints out the first
 *              five, middle four/five (depends on how much data there is),
 *              and last five records. It then gives the user the option to look
 *              up any credits that were issued and any associated project name
 *              and Id. The way it searches the bin file is through a ternary
 *              search algorithm.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

public class Prog1B {
    
    // These variables are to store their max lengths
    // from the columns of the csv file
    private static int id;
    private static int name;
    private static int stat;
    private static int scope;
    private static int type;
    private static int protocol;
    private static int region;
    private static int country;
    private static int state;
    private static int records;
    private static int block;       // This helps us track how much bytes each line of data is

    // We only reference these variables to make it easier
    // for us to reference when reading the bin file bytes
    private static byte[] projectId;
    private static byte[] projectName;
    private static byte[] projectStat;
    private static byte[] projectScope;
    private static byte[] projectType;
    private static byte[] projectProtocol;
    private static byte[] projectRegion;
    private static byte[] projectCountry;
    private static byte[] projectState;

    // How we will reference our bin file.
    private static RandomAccessFile dataStream;

    public static void main(String[] args) {
        File file = new File(args[0]);
        // File file = new File("Offsets-Database.bin");
        try {dataStream = new RandomAccessFile(file, "r");
        } catch (FileNotFoundException e) {
            System.out.println("Something went wrong trying to read file.");
            System.exit(-1);
        }
        try {
            // Since we've written each parameter max size at the end, we can easily 
            // read and assign them to our respective variables
            dataStream.seek(dataStream.length() - 40);
            id          = dataStream.readInt();
            name        = dataStream.readInt();
            System.out.println(name);
            stat        = dataStream.readInt();
            scope       = dataStream.readInt();
            type        = dataStream.readInt();
            protocol    = dataStream.readInt();
            region      = dataStream.readInt();
            country     = dataStream.readInt();
            state       = dataStream.readInt();
            records     = dataStream.readInt();
            block       = id + name + stat + scope + type + protocol + region + 
                        country + state + 16;
        } catch (IOException e) {
            System.out.println("Something went wrong reading file size");
            System.exit(-1);
        }
        // We assign these variables to of byte[] type so we can easily reference
        // them when reading our bin file.
        assigning();

        // We will then print out the beginning, middle, and ending sections of 
        // our bin file.
        beginning();
        middle();
        ending();

        // We will then start to take user input. If the user inputs '-1', it ends
        // the program. Otherwise, it constantly takes input.
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter a valid number...");
        String str = sc.next();
        while (!(str.equals("-1"))) {
            // Checks to see if the user inputted a valid integer.
            try {
                int temp = Integer.parseInt(str);

                // 'x' determines if what the user inputted exists
                int x = search(temp, 0, records);
                if (x == 0) {System.out.println("Could not find specified value...\n");}
            } catch (NumberFormatException e) {System.out.println("Input was not a valid number...\n");}
            System.out.println("Please enter a valid number...");
            str = sc.next();
        }

        // We then close our file and scanner
        try {dataStream.close(); sc.close();} 
        catch (IOException e) {
            System.out.println("Something went wrong with closing the file");
            System.exit(-1);
        }
        System.out.println("Exiting... Goodbye!");
    }

    /**
     * The purpose of this function is to assign how long each parameter type
     * is into a byte array. This will make reading the bin file a lot easier
     * on my end.
     */
    private static void assigning() {
        projectId       = new byte[id];
        projectName     = new byte[name];
        projectStat     = new byte[stat];
        projectScope    = new byte[scope];
        projectType     = new byte[type];
        projectProtocol = new byte[protocol];
        projectRegion   = new byte[region];
        projectCountry  = new byte[country];
        projectState    = new byte[state];
    }

    /**
     * The purpose of this function is to print out the first 5 records. It
     * should be able to handle less than 5 records easily. If there are
     * less than 5 records, it will print all of the records it can.
     */
    private static void beginning() {
        try {
            // Helps us only print <= 5 records and checks if there are valid amounts of data
            long x = 0; int y = 5;
            dataStream.seek(0);
            System.out.println("Printing first five reccords...");

            // The purpose of this while loop is make sure that we do not go out of bounds when
            // reading the bin file. Also to only print out either 5 records, or the maximum 
            // amount of records there are if there are less than 5 records.
            while ((x < dataStream.length() - 40) && (y > 0)) {
                dataStream.read(projectId);
                String theId = new String(projectId);       // We want to store it to print it
                dataStream.read(projectName);
                String theName = new String(projectName);
                dataStream.read(projectStat);
                dataStream.read(projectScope);
                dataStream.read(projectType);
                dataStream.read(projectProtocol);
                dataStream.read(projectRegion);
                dataStream.read(projectCountry);
                dataStream.read(projectState);
                int issued = dataStream.readInt();
                dataStream.readInt(); dataStream.readInt(); dataStream.readInt();
                System.out.println("[" + theId + "][" + theName + "][" + issued +"]");

                // We add up the size of each parameter type and 16 since the last 4 types are integers
                // We've made it the variable block :)
                x += block;
                y -= 1;
            }
        } catch (IOException e) {
            System.out.println("Something went wrong trying to print first five records");
            System.exit(-1);
        }
        System.out.println();
    }

    /**
     * The purpose of this function is to print out the middle 4 or 5 records. It
     * should be able to handle less than 5 records easily. If there are
     * less than 5 records, it will print all of the records it can. Whether it's 
     * 4 or 5 depends on the amount of records that exist.
     */
    private static void middle() {
        try {
            // Helps us only print <= 5 records and checks if there are valid amounts of data
            long x = 0; int y = 5;
            boolean flag = (records % 2 == 0);
            if (flag) {System.out.println("Printing middle four records..."); y -= 1;}
            else {System.out.println("Printing middle five records...");}

            // If there are less than 
            if (records <= 3) {dataStream.seek(0);}
            else {dataStream.seek(((records / 2) * block) -  (2 * block));}

            // The purpose of this while loop is make sure that we do not go out of bounds when
            // reading the bin file. Also to only print out either 5 records, or the maximum 
            // amount of records there are if there are less than 5 records.
            while ((x < dataStream.length() - 40) && (y > 0)) {
                dataStream.read(projectId);
                String theId = new String(projectId);
                dataStream.read(projectName);
                String theName = new String(projectName);
                dataStream.read(projectStat);
                dataStream.read(projectScope);
                dataStream.read(projectType);
                dataStream.read(projectProtocol);
                dataStream.read(projectRegion);
                dataStream.read(projectCountry);
                dataStream.read(projectState);
                int issued = dataStream.readInt();
                dataStream.readInt(); dataStream.readInt(); dataStream.readInt();
                System.out.println("[" + theId + "][" + theName + "][" + issued +"]");
                
                // We add up the size of each parameter type and 16 since the last 4 types are integers
                // We've made it the variable block :)
                x += block;
                y -= 1;
            }
        } catch (IOException e) {
            System.out.println("Something went wrong trying to print the middle few records");
            System.exit(-1);
        }
        System.out.println();
    }

    /**
     * The purpose of this function is to print out the first 5 records. It
     * should be able to handle less than 5 records easily. If there are
     * less than 5 records, it will print all of the records it can.
     */
    private static void ending() {
        try {
            // Helps us only print <= 5 records and checks if there are valid amounts of data
            long x = 0; int y = 0;

            // The purpose of this for-loop is to determine if there are more than 5 records.
            // If there are less than 5 records, it will be able to account for that.
            // This is kind of like a sanity check, just in case.
            for (long i = dataStream.length() - 40; i > 0; i -= block) {
                if (y == 5) {break;} 
                y += 1;
            }
            System.out.println("Printing last five records...");
            if (y == 0) {System.out.println(); return;}
            dataStream.seek(dataStream.length() - 40 - (y * block));

            // The purpose of this while loop is make sure that we do not go out of bounds when
            // reading the bin file. Also to only print out either 5 records, or the maximum 
            // amount of records there are if there are less than 5 records.
            while ((x < dataStream.length() - 40) && (y > 0)) {
                dataStream.read(projectId);
                String theId = new String(projectId);
                dataStream.read(projectName);
                String theName = new String(projectName);
                dataStream.read(projectStat);
                dataStream.read(projectScope);
                dataStream.read(projectType);
                dataStream.read(projectProtocol);
                dataStream.read(projectRegion);
                dataStream.read(projectCountry);
                dataStream.read(projectState);
                int issued = dataStream.readInt();
                dataStream.readInt(); dataStream.readInt(); dataStream.readInt();
                System.out.println("[" + theId + "][" + theName + "][" + issued +"]");
                
                // We add up the size of each parameter type and 16 since the last 4 types are integers
                // We've made it the variable block :)
                x += block;
                y -= 1;
            }
        } catch (IOException e) {
            System.out.println("Something went wrong trying to print last five records");
            System.exit(-1);
        }
        System.out.println();
    }

    /**
     * The purpose of this function is to search for the specified number inputted by the user.
     * It uses a ternary seach algorithm by searching two points of access.
     * 
     * @param number - The number we are trying to look for
     * @param index - Where we are starting 
     * @param length - The maximum in which where we're looking for
     * 
     * @return - Returns whether it was able to find the number or not
     */
    private static int search(int number, int index, int length) {
        if (length >= 1 && index < length) {
            // This allows us to determine where we'll check each line of data
            int sec1 = index + ((length - index) / 3);
            int sec2 = length - ((length - index) / 3);
            try {
                // This specific line will jump us to that specific record
                dataStream.seek((block * sec1) - block);
                dataStream.read(projectId);
                String theId = new String(projectId);       // We then store ID
                dataStream.read(projectName);               //
                String theName = new String(projectName);   // and name 
                dataStream.read(projectStat);               // just in case it matches
                dataStream.read(projectScope);
                dataStream.read(projectType);
                dataStream.read(projectProtocol);
                dataStream.read(projectRegion);
                dataStream.read(projectCountry);
                dataStream.read(projectState);
                int issued1 = dataStream.readInt();         // Store credits issued

                // We check to see if that line of data matched what the user inputted
                if (number == issued1) {
                    while (sec1 >= 0) {
                        sec1 -= 1;
                        if (sec1 <= 0) {break;}
                        dataStream.seek((block * sec1) - block);
                        dataStream.read(projectId);
                        dataStream.read(projectName);               //
                        dataStream.read(projectStat);               // just in case it matches
                        dataStream.read(projectScope);
                        dataStream.read(projectType);
                        dataStream.read(projectProtocol);
                        dataStream.read(projectRegion);
                        dataStream.read(projectCountry);
                        dataStream.read(projectState);
                        issued1 = dataStream.readInt();         // Store credits issued
                        if (issued1 != number) {break;}
                    }
                    while (true) {
                        sec1 += 1;
                        if (sec1 > records) {break;}
                        dataStream.seek((block * sec1) - block);
                        dataStream.read(projectId);
                        theId = new String(projectId);              // We then store ID
                        dataStream.read(projectName);               //
                        theName = new String(projectName);          // and name 
                        dataStream.read(projectStat);               // just in case it matches
                        dataStream.read(projectScope);
                        dataStream.read(projectType);
                        dataStream.read(projectProtocol);
                        dataStream.read(projectRegion);
                        dataStream.read(projectCountry);
                        dataStream.read(projectState);
                        issued1 = dataStream.readInt();             // Store credits issued
                        if (issued1 != number) {break;}
                        System.out.println("[" + theId + "][" + theName + "][" + issued1 +"]\n");
                    }
                    return 1;
                }

                // This specific line will jump us to that specific record
                dataStream.seek((block * sec2) - block);
                dataStream.read(projectId);
                theId = new String(projectId);              // We then store ID
                dataStream.read(projectName);               //
                theName = new String(projectName);          // and name
                dataStream.read(projectStat);               // just in case it matches
                dataStream.read(projectScope);
                dataStream.read(projectType);
                dataStream.read(projectProtocol);
                dataStream.read(projectRegion);
                dataStream.read(projectCountry);
                dataStream.read(projectState);
                int issued2 = dataStream.readInt();         // Store credits issued

                // We check to see if that line of data matched what the user inputted
                if (number == issued2) {
                    while (sec1 >= 0) {
                        sec2 -= 1;
                        if (sec2 <= 0) {break;}
                        dataStream.seek((block * sec2) - block);
                        dataStream.read(projectId);
                        dataStream.read(projectName);               //
                        dataStream.read(projectStat);               // just in case it matches
                        dataStream.read(projectScope);
                        dataStream.read(projectType);
                        dataStream.read(projectProtocol);
                        dataStream.read(projectRegion);
                        dataStream.read(projectCountry);
                        dataStream.read(projectState);
                        issued2 = dataStream.readInt();         // Store credits issued
                        if (issued2 != number || sec2 == 0) {break;}
                    }
                    while (true) {
                        sec2 += 1;
                        if (sec2 > records) {break;}
                        dataStream.seek((block * sec2) - block);
                        dataStream.read(projectId);
                        theId = new String(projectId);              // We then store ID
                        dataStream.read(projectName);               //
                        theName = new String(projectName);          // and name 
                        dataStream.read(projectStat);               // just in case it matches
                        dataStream.read(projectScope);
                        dataStream.read(projectType);
                        dataStream.read(projectProtocol);
                        dataStream.read(projectRegion);
                        dataStream.read(projectCountry);
                        dataStream.read(projectState);
                        issued2 = dataStream.readInt();         // Store credits issued
                        if (issued2 != number) {break;}
                        System.out.println("[" + theId + "][" + theName + "][" + issued2 +"]\n");
                    }
                    return 1;
                }
                // We then determine where to go depending on what the user inputted.
                // We either go lower, middle, or higher based on the numbers found
                if (number < issued1) {return search(number, index, sec1 - 1);}
                else if (number > issued2) {return search(number, sec2 + 1, length);}
                else {return search(number, sec1 + 1, sec2 - 1);}

            } catch (IOException e) {
                System.out.println("Something went wrong trying to print these records");
                System.exit(-1);
            }
        }
        // In case nothing was found
        return 0;
    }
}
