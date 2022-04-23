package test;

import main.DatabaseImportData;

import static main.DatabaseEngine.*;
import static org.junit.jupiter.api.Assertions.*;

class DatabaseEngineTest {

    String dataFileLocation = "src/test/pokemon.data";
    String indexFileLocation = "src/test/pokemon.index";

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        writeBinaryFile(dataFileLocation, indexFileLocation, DatabaseImportData.readFile("src/test/Pokemon.csv"));
    }

    @org.junit.jupiter.api.Test
    void readBinaryFileTest() {
        Pokemon pokemon = new Pokemon(100,"Voltorb","Electric",330,40,30,50,55,55,100,1,false);
        assertEquals(pokemon, readBinaryFile(dataFileLocation, indexFileLocation, 100));
    }

    @org.junit.jupiter.api.Test
    void readBinaryFileTestLeg(){
        Pokemon pokemon = new Pokemon(144,"Articuno","Ice",580,90,85,100,95,125,85,1,true);
        assertEquals(pokemon, readBinaryFile(dataFileLocation, indexFileLocation, 144));
    }

    @org.junit.jupiter.api.Test
    void readBinaryFileTestNull() {
        assertNull(readBinaryFile(dataFileLocation, indexFileLocation, 999));
    }
}