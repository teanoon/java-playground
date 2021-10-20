package com.example.experiment;

import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BinaryTree {

    private static final Pattern TREE_PATTERN = Pattern.compile("^(\\w+?)\\{(.*)\\}$");

    Node buildNode(String input) {
        Matcher matcher = TREE_PATTERN.matcher(input);
        if (matcher.find()) {
            String value = matcher.group(1);
            Node node = new Node();
            node.value = value;

            String children = matcher.group(2);
            if (children.length() > 0) {
                int index = findChildDelimiterIndex(children);
                String left = index + 1 == children.length()
                    ? children.substring(0, index + 1)
                    : children.substring(0, index);
                if (!left.trim().equals("")) {
                    node.left = buildNode(left);
                }
                String right = children.substring(index + 1);
                if (!right.trim().equals("")) {
                    node.right = buildNode(right);
                }
            }
            return node;
        }
        // it's a leaf
        Node node = new Node();
        node.value = input;
        return node;
    }

    int findChildDelimiterIndex(String input) {
        if (input.charAt(0) == ',') {
            return 0;
        }

        int seenLeftCurve = 0;
        int seenRightCurve = 0;
        for (int index = 0; index < input.length(); index++) {
            if ('{' == input.charAt(index)) {
                seenLeftCurve++;
            }
            if ('}' == input.charAt(index)) {
                seenRightCurve++;
            }
            if (Objects.equals(seenLeftCurve, seenRightCurve)) {
                if (index + 1 == input.length()) {
                    return index;
                } else if (input.charAt(index + 1) == ',') {
                    return index + 1;
                }
            }
        }
        throw new IllegalArgumentException("No delimiter was found");
    }

    public static void main(String[] args) {
        BinaryTree app = new BinaryTree();
        try (Scanner in = new Scanner(System.in)) {
            while (in.hasNextLine()) {
                String input = in.nextLine();
                System.out.println(app.buildNode(input));
            }
        } catch (Exception e) {
        }
    }

    static class Node {
        String value;
        Node left;
        Node right;

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            if (left != null) {
                builder.append(left.toString());
            }
            builder.append(value);
            if (right != null) {
                builder.append(right.toString());
            }
            return builder.toString();
        }

    }

}
