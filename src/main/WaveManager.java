package main;

import enemies.*;
import java.util.ArrayList;
import java.util.Arrays;

public class WaveManager {

    /**
     * Returns the enemies for a given wave (1-based).
     * 4 waves total with increasingly difficult enemy compositions.
     */
    public static ArrayList<Enemy> getWave(int waveNumber) {
        switch (waveNumber) {
            case 1:
                return new ArrayList<>(Arrays.asList(
                    new Goblin(),
                    new Goblin()
                ));
            case 2:
                return new ArrayList<>(Arrays.asList(
                    new Skeleton(),
                    new Goblin()
                ));
            case 3:
                return new ArrayList<>(Arrays.asList(
                    new DarkKnight(),
                    new Skeleton()
                ));
            case 4:
                return new ArrayList<>(Arrays.asList(
                    new DragonBoss(),
                    new DarkKnight()
                ));
            default:
                return new ArrayList<>();
        }
    }

    public static int getTotalWaves() {
        return 4;
    }
}
