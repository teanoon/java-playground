package com.example.experiment;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 位操作符
 * https://www.geeksforgeeks.org/bitwise-operators-in-java/
 */
public class BitwiseOperatorTest {

    /**
     * 与
     */
    @Test
    public void andSuccess() {
        assertEquals(1, 99 & 1);
        assertEquals(0, 98 & 1);
    }

    /**
     * 或
     */
    @Test
    public void orSuccess() {
        assertEquals(99, 99 | 1);
        assertEquals(99, 98 | 1);
        assertEquals(99, 99 | 0);
        assertEquals(98, 98 | 0);
    }

    /**
     * 异或
     */
    @Test
    public void xorSuccess() {
        assertEquals(98, 99 ^ 1);
        assertEquals(99, 98 ^ 1);
    }

    /**
     * 取反
     */
    @Test
    public void comSuccess() {
        assertEquals(-6, ~ 5);
        assertEquals(-11, ~ 10);
        assertEquals(-10, ~ 9);
    }

    /**
     * 左移
     */
    @Test
    public void signedLeftShiftSuccess() {
        assertEquals(4, 1 << 2);
        assertEquals(12, 3 << 2);
    }

    /**
     * 右移
     */
    @Test
    public void signedRightShiftSuccess() {
        assertEquals(1, 4 >> 2);
        assertEquals(1, 5 >> 2);
        assertEquals(1, 6 >> 2);
        assertEquals(1, 7 >> 2);
        assertEquals(2, 8 >> 2);
    }

}
