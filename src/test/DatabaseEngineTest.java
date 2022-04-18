package test;

import main.DatabaseImportData;

import static main.DatabaseEngine.*;
import static org.junit.jupiter.api.Assertions.*;

class DatabaseEngineTest {

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        writeBinaryFile("src/test/pokemon.data", DatabaseImportData.readFile("src/test/Pokemon.csv"));
    }

    @org.junit.jupiter.api.Test
    void readBinaryFileTest() {
        Pokemon pokemon = new Pokemon(100,"Voltorb","Electric",330,40,30,50,55,55,100,1,false);
        assertEquals(pokemon, readBinaryFile("src/test/pokemon.data", 100));
    }

    @org.junit.jupiter.api.Test
    void readBinaryFileTestNull() {
        assertNull(readBinaryFile("src/test/pokemon.data", 999));
    }
}