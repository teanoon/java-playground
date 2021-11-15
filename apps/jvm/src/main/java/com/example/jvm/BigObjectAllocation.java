package com.example.jvm;

public class BigObjectAllocation {

    private static final int _1MB = 1025 * 1025;

    public static void main(String[] args) {
        System.out.println("start");
        // for (int i = 0; i < 4 * 15; i++) {
        //     byte[] allocation1 = new byte[_1MB / 4];
        //     System.out.println(i + " allocated " + allocation1.length / 1024);
        // }
        byte[] allocation1 = new byte[_1MB / 4];
        System.out.println("1 allocated " + allocation1.length / 1024);
        // byte[] allocation2 = new byte[_1MB / 4];
        // System.out.println("2 allocated " + allocation2.length / 1024);
        byte[] allocation3 = new byte[4 * _1MB];
        System.out.println("3 allocated " + allocation3.length / 1024);
        byte[] allocation4 = new byte[4 * _1MB];
        System.out.println("4 allocated " + allocation4.length / 1024);
    }

}
