package main;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class contains methods that manage a database for Pokemon (Pokedex)
 * It does this by writing binary files, one that contains the index of each Pokemon, and one that contains the data.
 *
 * @author Josiah and Michael
 * @version 4/22/22
 */

public class DatabaseEngine {

    /**
     * This record stores the information for a Pokemon
     */
    public record Pokemon(int number, String name, String type, int total, int hp, int attack, int defense, int spAttack, int spDefense, int speed, int generation, boolean legendary){
        @Override
        public int number() {
            return number;
        }
        @Override
        public String name() {
            return name;
        }
        @Override
        public String type() {
            return type;
        }
        @Override
        public int total() {
            return total;
        }
        @Override
        public int hp() {
            return hp;
        }
        @Override
        public int attack() {
            return attack;
        }
        @Override
        public int defense() {
            return defense;
        }
        @Override
        public int spAttack() {
            return spAttack;
        }
        public int spDefense() {
            return spDefense;
        }
        @Override
        public int speed() {
            return speed;
        }
        @Override
        public int generation() {
            return generation;
        }
        @Override
        public boolean legendary() {
            return legendary;
        }
    }

    /**
     * This checks if a String is numeric
     */
    private static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    /**
     * This will write information from a list of strings into a binary file.
     * It uses RandomAccessFile to this. It assumes each property is split by commas (CSV format).
     * It also assumes each separate string is a separate Pokemon.
     *
     * @param dataFileLocation This is the file location of the dataFile
     * @param indexFileLocation This is the file location of the indexFile
     * @param fileData This is a list of strings containing the Pokemon
     */
    public static void writeBinaryFile(String dataFileLocation, String indexFileLocation, List<String> fileData){
        File dataFile = new File(dataFileLocation);
        File indexFile = new File(indexFileLocation);

        try (RandomAccessFile data = new RandomAccessFile(dataFile, "rw");
             RandomAccessFile index = new RandomAccessFile(indexFile, "rw"))
        {
            long byteIndex = 0;
            int entry = 0;
            int entryByteLen;
            for(String line : fileData){
                // properties are split by comma in the csv
                String[] props = line.split(",");
                entryByteLen = 0;
                for(String prop : props){
                    // add up the total length of this record after writing each property to the data file
                    entryByteLen += writeData(prop, data);
                }
                index.writeInt(entry);
                // write the byte position of this record
                index.writeLong(byteIndex);
                // add this record's length to the byteIndex
                byteIndex += entryByteLen;
                entry++;
            }
            // indicates end of index file
            index.writeInt(-1);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This writes a property to a binary data file and returns the entry length in terms of bytes.
     *
     * @param prop The property to be written
     * @param data A RandomAccessFile to write to
     * @return The length of the entry in terms of bytes
     * @throws IOException
     */
    private static int writeData(String prop, RandomAccessFile data) throws IOException {
        int propInt;
        boolean propBool;
        if (isNumeric(prop)) {
            propInt = Integer.parseInt(prop);
            data.writeInt(propInt);
            // ints are 4 bytes
            return 4;
        }
        // sets prop bool to true or false if it is a boolean
        else if ((propBool = "true".equals(prop)) || "false".equals(prop)) {
            data.writeBoolean(propBool);
            // boolean is 1 byte
            return 1;
        }
        else {
            // write and int indicating the length of the string
            data.writeInt(prop.length());
            data.writeChars(prop);
            // a char is 2 bytes, an int is 4
            return (prop.length() * 2) + 4;
        }
    }

    /**
     * This reads binary data into a Pokemon record. It does this by first reading the index file to determine
     * the correct byte to seek. Then reads in the data from the specified data file location.
     *
     * @param dataFileLocation This is the file location of the dataFile
     * @param indexFileLocation This is the file location of the indexFile
     * @param id This is the id of the Pokemon
     * @return a Pokemon record or null
     */
    public static Pokemon readBinaryFile(String dataFileLocation, String indexFileLocation, int id) {
        File file = new File(dataFileLocation);
        File index = new File(indexFileLocation);

        try (RandomAccessFile data = new RandomAccessFile(file, "rw");
             RandomAccessFile in = new RandomAccessFile(index, "rw"))
        {
            int nextInt;
            while ((nextInt = in.readInt()) != -1){
                if (nextInt == id){
                    return readPokemon(in.readLong(), data);
                }
                // read the long even if the id doesn't match to reset the loop, this prevents a false positive
                in.readLong();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Similar to the readBinaryFile method but instead of reading one Pokemon, it reads the entire CSV
     *
     * @param dataFileLocation This is the file location of the dataFile
     * @param indexFileLocation This is the file location of the indexFile
     * @return a list of Pokemon
     */
    public static List<Pokemon> readEntireBinaryFile(String dataFileLocation, String indexFileLocation){
        File dataFile = new File(dataFileLocation);
        File indexFile = new File(indexFileLocation);

        try (RandomAccessFile data = new RandomAccessFile(dataFile, "rw");
             RandomAccessFile index = new RandomAccessFile(indexFile, "rw"))
        {
            List<Pokemon> pokemon = new ArrayList<>();
            // skip the first line which is a header
            index.readInt();
            index.readLong();
            while (index.readInt() != -1){
                pokemon.add(readPokemon(index.readLong(), data));
            }
            return pokemon;
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This reads a Pokemon from a RandomAccessFile object (stream?).
     * It does this by going through each data type by what is expected.
     *
     * @param offset This is the byte offset to seek to
     * @param data This is the RandomAccessFile
     * @return a Pokemon
     * @throws IOException
     */
    private static Pokemon readPokemon(long offset, RandomAccessFile data) throws IOException {
        data.seek(offset);

        int number = data.readInt();
        String name = readString(data);
        String type = readString(data);
        int total = data.readInt();
        int hp = data.readInt();
        int attack = data.readInt();
        int defense = data.readInt();
        int spAttack = data.readInt();
        int spDefense = data.readInt();
        int speed = data.readInt();
        int gen = data.readInt();
        boolean legendary = data.readBoolean();

        return new Pokemon(number, name, type, total, hp,
                attack, defense, spAttack, spDefense,
                speed, gen, legendary);
    }

    /**
     * This reads a string from a binary file by first reading an int to determine how long the string is.
     *
     * @param data This is the RandomAccessFile
     * @return a string from the binary file
     * @throws IOException
     */
    private static String readString(RandomAccessFile data) throws IOException {
        int numChars = data.readInt();
        List<Character> charList = new ArrayList<>();
        for (int i=0;i<numChars;i++){
            charList.add(data.readChar());
        }
        return charListToString(charList);
    }

    /**
     * This converts a List of Characters to a string
     *
     * @param charList a List of Characters to be converted to a string
     * @return the string that was converted from a list of Characters
     */
    private static String charListToString(List<Character> charList){
        return charList.stream().map(String::valueOf).collect(Collectors.joining());
    }
}
