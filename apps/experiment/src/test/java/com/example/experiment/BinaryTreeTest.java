package com.example.experiment;

import org.junit.jupiter.api.Test;

import com.example.experiment.BinaryTree.Node;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BinaryTreeTest {

    private BinaryTree tree = new BinaryTree();

    @Test
    public void buildNodeSuccess() {
        Node node = tree.buildNode("aa{bb,cc}");
        assertEquals(node.value, "aa");
        assertEquals(node.left.value, "bb");
        assertEquals(node.right.value, "cc");

        node = tree.buildNode("aa{bb{d,cc{f,d{,b}}},cc{a,}}");
        assertEquals(node.left.right.right.right.value, "b");
    }

    @Test
    public void buildNodeSuccess2() {
        Node node = tree.buildNode("aa{bb{d,cc{f,d{,b}}},cc{a}}");
        assertEquals(node.left.right.right.right.value, "b");
    }

    @Test
    public void findChildDelimiterIndexSuccess() {
        assertEquals(tree.findChildDelimiterIndex("{{{}}},"), 6);
    }

    @Test
    public void toStringSuccess() {
        Node node = tree.buildNode("aa{bb,cc}");
        assertEquals(node.toString(), "bbaacc");

        node = tree.buildNode("a{b{c,d{e,f{,g}}},h{i}}");
        assertEquals(node.toString(), "cbedfgaih");

        node = tree.buildNode("a{b{c,d{e,f{,g}}},h{}}");
        assertEquals(node.toString(), "cbedfgah");

        node = tree.buildNode("a{b{d,e{g,h{,i}}},c{f}}");
        assertEquals(node.toString(), "dbgehiafc");
    }

    @Test
    public void toStringSuccess2() {
        Node node = tree.buildNode("a{b{d,e{g,h{i{j{}}}}},c{f}}");
        assertEquals(node.toString(), "dbgejihafc");
    }

}
