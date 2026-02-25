package com.legends;

import com.legends.test.TestGame;
import com.legends.utils.Constants;

public class Main {

    private static WindowManager window;
//    private static EngineManager engine;
    private static TestGame game;

    public static void main(String[] args) {
        window = new WindowManager(Constants.TITLE, 1600, 900, false);
        game = new TestGame();

        EngineManager engine = new EngineManager();

        try {
            engine.start();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static WindowManager getWindow() {
        return window;
    }

    public static TestGame getGame() {
        return game;
    }
}