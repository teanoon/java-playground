package com.example.hotswap;

import java.util.concurrent.TimeUnit;

public class Application {

    private void run() {
        while (true) {
            System.out.println("halo!");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
            }
        }
    }

    public static void main(String[] args) {
        var app = new Application();
        app.run();
    }

}
