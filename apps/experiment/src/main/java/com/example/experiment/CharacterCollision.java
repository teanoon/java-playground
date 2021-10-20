package com.example.experiment;

import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CharacterCollision {

    private static final Pattern CHAR_PATTERN = Pattern.compile("^[a-zA-Z]+?$");

    boolean hasNonEnglishCharacter(String input) {
        Matcher matcher = CHAR_PATTERN.matcher(input);
        return !matcher.find();
    }

    int collide(String input) {
        if (hasNonEnglishCharacter(input)) {
            return 0;
        }
        for (int index = 0; index < input.length() - 1; index++) {
            Pair collided = collide(input, index, index + 1);
            if (Objects.equals(collided.getLeft() + 1, collided.getRight())) {
                continue;
            } else {
                String prev = collided.getLeft() <= 0
                    ? collided.getLeft() == 0 ? input.substring(0, 1) : ""
                    : input.substring(0, collided.getLeft() + 1);
                String next = collided.getRight() > input.length() ? "" : input.substring(collided.getRight(), input.length());
                return collide(prev + next);
            }
        }
        return input.length();
    }

    Pair collide(String input, int startIndex, int endIndex) {
        char prev = input.charAt(startIndex);
        char next = input.charAt(endIndex);
        if (prev != next) {
            return Pair.of(startIndex, endIndex);
        }
        if (startIndex - 1 < 0 || endIndex + 1 >= input.length()) {
            return Pair.of(startIndex - 1, endIndex + 1);
        }
        return collide(input, startIndex - 1, endIndex + 1);
    }

    public static void main(String[] args) {
        CharacterCollision app = new CharacterCollision();
        try (Scanner in = new Scanner(System.in)) {
            while (in.hasNextLine()) {
                String input = in.nextLine();
                System.out.println(app.collide(input));
            }
        } catch (Exception e) {
        }

    }

    static class Pair {

        int left;
        int right;

        public static Pair of(int left, int right) {
            Pair pair = new Pair();
            pair.left = left;
            pair.right = right;
            return pair;
        }

        public int getLeft() {
            return left;
        }

        public int getRight() {
            return right;
        }

    }

}
