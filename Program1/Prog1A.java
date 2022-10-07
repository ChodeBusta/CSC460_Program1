/**
 * Author: Raul Davila 
 * Course: CSc 460 Database Design
 * Professor: Lester McCann
 * Program: Prog1A.java
 * Description: The purpose of this file is to correctly read our CSV file
 *              and correctly format its output.As of right now,  I am only
 *              correctly reading the file. I still need to figure out a way
 *              to correctly determine the size of each column.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Prog1A {

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

    // These are variables that we will be referencing throughout our code
    private static ArrayList<ArrayList<String>> data;   // Houses our csv data (line by line)
    private static String line;                         // Reads our csv file (line by line)
    private static String[] split;                      // Splits our line
    private static BufferedReader br;                   // Also reference our csv file
    private static RandomAccessFile dataStream;         // How we will write to our bin file

    public static void main(String[] args) {
        File file = new File(args[0]);
        data = new ArrayList<ArrayList<String>>();
        ArrayList<String> temp = new ArrayList<String>();

        // Will try to open the file from the first command line argument
        try {
            br = new BufferedReader(new FileReader(new File(args[0]))); // This is how we'll open the file.
            // br = new BufferedReader(new FileReader(new File("Offsets-Database.csv")));
            // Will try to read each line within the file
            try {
                // Skips over the metadata
                br.readLine();
                while ((line = br.readLine()) != null) {
                    // This helps normalize any special characters to regular characters
                    line = Normalizer.normalize(line, Normalizer.Form.NFKD).replaceAll("[^\\p{ASCII}]", "");

                    // This also helps keep the formatting for the integers
                    // It keeps the commas for the numbers
                    split = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                    // We then create a temporary arrayList which houses each line of data
                    temp = new ArrayList<String>();
                    for (int i = 0; i < split.length; i++) {temp.add(split[i]);}

                    // We then add that temporary arrayList to a another array
                    data.add(temp);

                    // Determines max size of each parameter (and if it exceeds any)
                    checkLengths(split);
                }
                // Closes the file 
                br.close();

                // This allows us to compare the each item with their respecitve credits issues
                // which allows us to sort the data
                Collections.sort(data, new Comparator<ArrayList<String>> () {
                   @Override
                   public int compare(ArrayList<String> a, ArrayList<String> b) {
                       String tempA = a.get(9).replaceAll("\"", "").replaceAll(",", "");
                       String tempB = b.get(9).replaceAll("\"", "").replaceAll(",", "");
                       if (tempA.length() == 0) {tempA = "0";}
                       if (tempB.length() == 0) {tempB = "0";}
                       int intA = Integer.valueOf(tempA);
                       int intB = Integer.valueOf(tempB);
                       if (intA < intB) {return -1;}
                       else if (intA > intB) {return 1;}
                       else {return 1;}
                   }
                });
            } catch (IOException e) {System.out.println("Something went wrong reading file."); System.exit(-1);}
        } catch (FileNotFoundException e1) {System.out.println("Could not open file."); System.exit(-1);}

        String fileName = file.getName().substring(0, file.getName().indexOf('.'));
        File binFile = new File(fileName + ".bin");
        // File binFile = new File("Offsets-Database.bin");
        
        // We will create a bin file based on the file we are reading
        try {dataStream = new RandomAccessFile(binFile, "rw");}
        catch (IOException e) {
            System.out.println("I/O ERROR: Something went wrong with the "
                             + "creation of the RandomAccessFile object.");
            System.exit(-1);
        }
    
        // This will then begin to write onto the bin file
        writing();
        writingEnd();

        // We then close the file we are writing onto to save any changes we've made
        try {dataStream.close();}
        catch (IOException e) {System.out.println("SOmething went wrong trying to close the file."); System.exit(-1);}
    }

    /**
     * The purpose of this function is to check each column srting length. We always
     * want to take the highest value in each category.
     * 
     * @param tempArr - Checks whether any of the lengths have been exceeded
     */
    private static void checkLengths(String[] tempArr) {
        // Checks the Project ID length
        if (tempArr[0].length() > id) {id = tempArr[0].length();}
        // Checks the Project Name length
        if (tempArr[1].length() > name) {name = tempArr[1].length();}
        // Checks the Voluntary Status length
        if (tempArr[2].length() > stat) {stat = tempArr[2].length();}
        // Checks the Scope length
        if (tempArr[3].length() > scope) {scope = tempArr[3].length();}
        // Checks the Type length
        if (tempArr[4].length() > type) {type = tempArr[4].length();}
        // Checks the Methodology/Protocol length
        if (tempArr[5].length() > protocol) {protocol = tempArr[5].length();}
        // Checks the Region length
        if (tempArr[6].length() > region) {region = tempArr[6].length();}
        // Checks the Country length
        if (tempArr[7].length() > country) {country = tempArr[7].length();}
        // Checks the State length
        if (tempArr[8].length() > state) {state = tempArr[8].length();}
    }

    /**
     * The purpose of this function is to write onto the bin file while adding padding
     * if it doesn't meet the max size of each parameter. 
     */
    private static void writing() {
        // The for loop is to iterate through each entire record (id, name... etc.)
        for (int i = 0; i < data.size(); i++) {
            // We always want to try to write at the end of the file
            try {dataStream.seek(dataStream.length());}
            catch (IOException e) {
                System.out.println("Something went wrong going to end of file.");
                System.exit(-1);
            }
            try {
                int j = 0;
                // Determines if 'id' criteria needs padding (adds spaces)                  ID
                for (j = data.get(i).get(0).length(); j < id; j++) {data.get(i).set(0, data.get(i).get(0) + " ");}
                dataStream.writeBytes(data.get(i).get(0));

                // Determines if 'name' criteria needs padding (adds spaces)                NAME
                for (j = data.get(i).get(1).length(); j < name; j++) {data.get(i).set(1, data.get(i).get(1) + " ");}
                dataStream.writeBytes(data.get(i).get(1));

                // Determines if 'stat' criteria needs padding (adds spaces)                STAT
                for (j = data.get(i).get(2).length(); j < stat; j++) {data.get(i).set(2, data.get(i).get(2) + " ");}
                dataStream.writeBytes(data.get(i).get(2));

                // Determines if 'scope' criteria needs padding (adds spaces)               SCOPE
                for (j = data.get(i).get(3).length(); j < scope; j++) {data.get(i).set(3, data.get(i).get(3) + " ");}
                dataStream.writeBytes(data.get(i).get(3));

                // Determines if 'type' criteria needs padding (adds spaces)                TYPE
                for (j = data.get(i).get(4).length(); j < type; j++) {data.get(i).set(4, data.get(i).get(4) + " ");}
                dataStream.writeBytes(data.get(i).get(4));

                // Determines if 'protocol' criteria needs padding (adds spaces)            PROTOCOL
                for (j = data.get(i).get(5).length(); j < protocol; j++) {data.get(i).set(5, data.get(i).get(5) + " ");}
                dataStream.writeBytes(data.get(i).get(5));

                // Determines if 'region' criteria needs padding (adds spaces)              REGION
                for (j = data.get(i).get(6).length(); j < region; j++) {data.get(i).set(6, data.get(i).get(6) + " ");}
                dataStream.writeBytes(data.get(i).get(6));

                // Determines if 'country' criteria needs padding (adds spaces)             COUNTRY
                for (j = data.get(i).get(7).length(); j < country; j++) {data.get(i).set(7, data.get(i).get(7) + " ");}
                dataStream.writeBytes(data.get(i).get(7));

                // Determines if 'state' criteria needs padding (adds spaces)               STATE
                for (j = data.get(i).get(8).length(); j < state; j++) {data.get(i).set(8, data.get(i).get(8) + " ");}
                dataStream.writeBytes(data.get(i).get(8));

                // Removes any ',' or '"' to make our data easier to process
                data.get(i).set(9, data.get(i).get(9).replaceAll("\"", "").replaceAll(",", ""));
                data.get(i).set(10, data.get(i).get(10).replaceAll("\"", "").replaceAll(",", ""));
                data.get(i).set(11, data.get(i).get(11).replaceAll("\"", "").replaceAll(",", ""));
                data.get(i).set(12, data.get(i).get(12).replaceAll("\"", "").replaceAll(",", ""));

                // Determines if 'credits issued' is not empty (if so, makes it '0')        CREDITS ISSUES
                if (data.get(i).get(9).length() == 0) {data.get(i).set(9, "0");}
                dataStream.writeInt(Integer.valueOf(data.get(i).get(9)));

                // Determines if 'credits retired' is not empty (if so, makes it '0')       CREDITS RETIRED
                if (data.get(i).get(10).length() == 0) {data.get(i).set(10, "0");}
                dataStream.writeInt(Integer.valueOf(data.get(i).get(10)));

                // Determines if 'credits remaining' is not empty (if so, makes it '0')     CREDITS REMAINING
                if (data.get(i).get(11).length() == 0) {data.get(i).set(11, "0");}
                dataStream.writeInt(Integer.valueOf(data.get(i).get(11)));

                // Determines if 'first year of project' is not empty (if so, makes it '0') FIRST YEAR OF PROJECT
                if (data.get(i).get(12).length() == 0) {data.get(i).set(12, "0");}
                dataStream.writeInt(Integer.valueOf(data.get(i).get(12)));
            } catch (IOException e) {
                System.out.println("Something went wrong trying to write to file.");
                System.exit(-1);
            }
        }
    }

    /**
     * The purpose of this function is to write the max size of each parameter 
     * onto the end of the file that we've created. Our record size doesn't 
     * account for the data we are writing at the end (the max sizes of each
     * parameter).
     */
    private static void writingEnd() {
        try {
            // We need to account for the current data written and the data we will write
            // at the end
            int records = (int) (dataStream.length() / (id + name + stat + scope + type + 
                                                        protocol + region + country + state +
                                                        4 * 4));
                                                        
            // We want to write any other data at the end of our file
            dataStream.seek(dataStream.length());
            dataStream.writeInt(id);
            dataStream.writeInt(name);
            dataStream.writeInt(stat);
            dataStream.writeInt(scope);
            dataStream.writeInt(type);
            dataStream.writeInt(protocol);
            dataStream.writeInt(region);
            dataStream.writeInt(country);
            dataStream.writeInt(state);

            // We then write the size of the record at the very end of our record
            dataStream.writeInt(records);
        } catch (IOException e) {
            System.out.println("Something went wrong trying to write to file.");
            System.exit(-1);
        }
    }
}
