package Miner;
/**
 * Java. Classic Game Minesweeper
 *
 * @author Sergey
 * @version 0.1.1 dated November 1, 2020
 */


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;


public class GameMiner extends JFrame {

    final String TITLE_OF_PROGRAMM = "Miner";
    final String SIGN_OF_FLAG = "f";
    final int BLOCK_SIZE = 30;
    final int FIELD_SIZE = 9;
    final int FIELD_DX = 6;
    final int FIELD_DY = 28+17;
    final int START_LOCATION = 200;
    final int MOUSE_BUTTON_LEFT = 1;
    final int MOUSE_BUTTON_RIGHT = 3;
    final int NUMBER_OF_MINES = 10;
    final int[] COLOR_OF_NUMBERS = {0x00008B, 0x00FF00, 0xFF0000, 0x6A5ACD, 0x0};
    Cell[][] gameField = new Cell[FIELD_SIZE][FIELD_SIZE];
    Random random = new Random();
    int countOpenedCells;
    boolean youWon, bangMine;
    int bangX, bangY;


    public static void main(String[] args) {
        new GameMiner();
    }

    GameMiner() {
        setTitle(TITLE_OF_PROGRAMM);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBounds(START_LOCATION, START_LOCATION, FIELD_SIZE * BLOCK_SIZE + FIELD_DX, FIELD_SIZE * BLOCK_SIZE + FIELD_DY);
        setResizable(false);
        //final TimerLabel timeLabel = new TimerLabel();
        //timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        final Canvas canvas = new Canvas();
        canvas.setBackground(Color.white);
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                int x = e.getX()/BLOCK_SIZE;
                int y = e.getY()/BLOCK_SIZE;
                if (!bangMine && !youWon) {
                    if (e.getButton() == MOUSE_BUTTON_LEFT) // left button mouse
                        if (gameField[y][x].isNotOpen()) {
                            openCells(x, y);
                            youWon = countOpenedCells == FIELD_SIZE*FIELD_SIZE - NUMBER_OF_MINES; // winning check
                            if (bangMine) {
                                bangX = x;
                                bangY = y;
                            }
                        }
                    if (e.getButton() == MOUSE_BUTTON_RIGHT) gameField[y][x].inverseFlag(); // right button mouse
                    //if (bangMine || youWon) timeLabel.stopTimer(); // game over
                    canvas.repaint();
                }
            }
        });
        add(BorderLayout.CENTER, canvas);
        //add(BorderLayout.SOUTH, timeLabel);
        setVisible(true);
        initField();
    }

    private void openCells(int x, int y) { // recursive procedure of opening the cells
        if (x < 0 || x > FIELD_SIZE - 1 || y < 0 || y > FIELD_SIZE - 1) return; // wrong coordinates
        if (!gameField[y][x].isNotOpen()) return; // cell is already open
        gameField[y][x].open();
        if (gameField[y][x].getCountedBomb() > 0 || bangMine) return; // the cell is not empty
        for (int dx = -1; dx < 2; dx++)
            for (int dy = -1; dy < 2; dy++) openCells(x + dx, y + dy);
    }

    private     void initField() { // initialization of the playing field
        int x, y, countMines = 0;
        // create cells for the field
        for (x = 0; x < FIELD_SIZE; x++)
            for (y = 0; y < FIELD_SIZE; y++)
                gameField[y][x] = new Cell();
        // to mine field
        while (countMines < NUMBER_OF_MINES) {
            do {
                x = random.nextInt(FIELD_SIZE);
                y = random.nextInt(FIELD_SIZE);
            } while (gameField[y][x].isMined());
            gameField[y][x].mine();
            countMines++;
        }
        // to count dangerous neighbors
        for (x = 0; x < FIELD_SIZE; x++)
            for (y = 0; y < FIELD_SIZE; y++)
                if (!gameField[y][x].isMined()) {
                    int count = 0;
                    for (int dx = -1; dx < 2; dx++)
                        for (int dy = -1; dy < 2; dy++) {
                            int nX = x + dx;
                            int nY = y + dy;
                            if (nX < 0 || nY < 0 || nX > FIELD_SIZE - 1 || nY > FIELD_SIZE - 1) {
                                nX = x;
                                nY = y;
                            }
                            count += (gameField[nY][nX].isMined()) ? 1 : 0;
                        }
                    gameField[y][x].setCountedBomb(count);
                }
    }

    public class Cell {
        private boolean isOpen, isMine, isFlag;
        private int countBombNear;

        public void mine(){isMine = true;}

        public void open() {
            isOpen = true;
            bangMine = isMine;
            if (!isMine) countOpenedCells++;
        }

        public void inverseFlag(){isFlag = !isFlag;}

        public boolean isNotOpen() {return !isOpen;}

        public void paint(Graphics g, int x, int y) {
            g.setColor(Color.gray);
            g.drawRect(x*BLOCK_SIZE, y*BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
            if (!isOpen){
                if ((bangMine || youWon) && isMine){
                    paintBomb(g, x , y, Color.black);
                }else {
                    g.setColor(Color.gray);
                    g.fill3DRect(x*BLOCK_SIZE, y*BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE, true);
                    if (isFlag) paintString(g,SIGN_OF_FLAG, x, y, Color.red);
                }
            }else {
                if (isMine) paintBomb(g, x, y, bangMine ? Color.red:Color.black);
                else if (countBombNear > 0){
                    paintString(g, Integer.toString(countBombNear), x, y, new Color(COLOR_OF_NUMBERS[countBombNear-1]));
                }
            }
        }

        public void paintBomb(Graphics g, int x, int y, Color color) {
            g.setColor(color);
            g.fillRect(x*BLOCK_SIZE + 7, y*BLOCK_SIZE + 10, 18, 10);
            g.fillRect(x*BLOCK_SIZE + 11, y*BLOCK_SIZE + 6, 10, 18);
            g.fillRect(x*BLOCK_SIZE + 9, y*BLOCK_SIZE + 8, 14, 14);
            g.setColor(Color.white);
            g.fillRect(x*BLOCK_SIZE + 11, y*BLOCK_SIZE + 10, 4, 4);
        }

        public void paintString(Graphics g, String str, int x, int y, Color color) {
            g.setColor(color);
            g.setFont(new Font("", Font.BOLD, BLOCK_SIZE));
            g.drawString(str, x*BLOCK_SIZE + 8, y*BLOCK_SIZE + 26);
        }

        public boolean isMined() {return isMine;}

        public void setCountedBomb(int count) {countBombNear = count;}

        public int getCountedBomb(){return countBombNear;}
    }

    public class Canvas extends JPanel{
        //GameMiner gameMiner = new GameMiner();
        @Override
        public void paint(Graphics g){
            super.paint(g);
            for (int x=0; x < FIELD_SIZE; x++){
                for (int y=0; y < FIELD_SIZE; y++){
                    gameField[y][x].paint(g, x, y);
                }
            }
        }
    }
}
