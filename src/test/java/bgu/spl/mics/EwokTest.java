package bgu.spl.mics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import bgu.spl.mics.application.passiveObjects.Ewok;

import static org.junit.jupiter.api.Assertions.*;

public class EwokTest {

     Ewok ewok;

    @BeforeEach
    void setUp() {
        ewok = new Ewok(1, true);
    }

    @Test
    void acquire() {
        assertTrue(ewok.isAvailable());
        ewok.acquire();
        assertFalse(ewok.isAvailable());
    }

    @Test
    void release() {
        ewok.acquire();
        assertFalse(ewok.isAvailable());
        ewok.release();
        assertTrue(ewok.isAvailable());
    }
}

