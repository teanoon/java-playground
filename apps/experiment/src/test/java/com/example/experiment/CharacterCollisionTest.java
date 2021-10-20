package com.example.experiment;

import org.junit.jupiter.api.Test;

import com.example.experiment.CharacterCollision.Pair;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CharacterCollisionTest {

    private CharacterCollision collision = new CharacterCollision();

    @Test
    public void hasNonEnglishCharacterSuccess() {
        assertFalse(collision.hasNonEnglishCharacter("input"));
        assertTrue(collision.hasNonEnglishCharacter("123input"));
        assertTrue(collision.hasNonEnglishCharacter("1234"));
    }

    @Test
    public void collideSuccessToRecursive1() {
        Pair collided = collision.collide("Mdssdccd", 1, 2);
        assertEquals(collided.getLeft(), 1);
        assertEquals(collided.getRight(), 2);
    }

    @Test
    public void collideSuccessToRecursive2() {
        Pair collided = collision.collide("Mdssdccd", 2, 3);
        assertEquals(collided.getLeft(), 0);
        assertEquals(collided.getRight(), 5);
    }

    @Test
    public void collideSuccessToCollide1() {
        assertEquals(collision.collide("input"), 5);
        assertEquals(collision.collide("mMbccbc"), 3);
        assertEquals(collision.collide("Mdssdccd"), 2);
        assertEquals(collision.collide("MdssdccM"), 0);
        assertEquals(collision.collide("MdssdccMMdssdccM"), 0);
    }

    @Test
    public void collideSuccessToCollide2() {
    }

}
