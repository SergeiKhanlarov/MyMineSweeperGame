package Miner;

import javax.swing.*;
import java.awt.*;

public class Canvas extends JPanel{
    GameMiner gameMiner = new GameMiner();
    public void paint(Graphics g){
        super.paint(g);
        for (int x=0; x < gameMiner.FIELD_SIZE; x++){
            for (int y=0; y < gameMiner.FIELD_SIZE; y++){
                //gameMiner.gameField[y][x].paint(g, x, y);
            }
        }
    }
}
