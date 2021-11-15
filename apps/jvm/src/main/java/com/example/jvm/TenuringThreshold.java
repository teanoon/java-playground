package com.example.jvm;

public class TenuringThreshold {

    private static final int _1MB = 1024 * 1024;

    public static void main(String[] args) {
        System.out.println("start");
        byte[] allocation1, allocation2, allocation3;
        allocation1 = new byte[_1MB / 4];
        System.out.println("1 allocated " + allocation1.length);
        allocation2 = new byte[4 * _1MB];
        System.out.println("2 allocated " + allocation2.length);
        allocation3 = new byte[4 * _1MB];
        System.out.println("3 allocated " + allocation3.length);
        allocation3 = null;
        System.out.println("3 cleared");
        allocation3 = new byte[4 * _1MB];
        System.out.println("3 allocated " + allocation3.length);
    }

}
