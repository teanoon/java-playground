package com.example.experiment;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class CharacterSorter {

    private String run(String input1, String input2) {
        Set<Character> chars = new HashSet<>();
        for (int index = 0; index < input1.length(); index++) {
            chars.add(input1.charAt(index));
        }
        TreeSet<Character> chars2 = new TreeSet<>();
        for (int index = 0; index < input2.length(); index++) {
            if (chars.contains(input2.charAt(index))) {
                chars2.add(input2.charAt(index));
            }
        }
        return chars2.stream().map(String::valueOf).collect(Collectors.joining());
    }

    public static void main(String[] args) {
        CharacterSorter app = new CharacterSorter();
        try (Scanner in = new Scanner(System.in)) {
            while (in.hasNextLine()) {
                String input = in.nextLine();
                String input2 = in.nextLine();
                System.out.println(app.run(input, input2));
            }
        } catch (Exception e) {
        }
    }

}
