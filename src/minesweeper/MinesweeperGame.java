package minesweeper;

import com.javarush.engine.cell.*;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {
    private static final int SIDE = 9;
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private static final String EMPTY = "";
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField;
    private int countFlags;
    private boolean isGameStopped;
    private int countClosedTiles = SIDE * SIDE;
    private int score;

    @Override
    public void initialize(){
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    private void createGame(){
        boolean setMine = false;

        for (int i = 0; i < SIDE; i++){
            for (int j = 0; j < SIDE; j++){
                if (getRandomNumber(10) == 1){
                    setMine = true;
                    countMinesOnField++;
                }

                gameField[i][j] = new GameObject(j, i, setMine);
                setCellValue(i, j, "");
                setCellColor(i, j, Color.ORANGE);
                setMine = false;
            }
        }

        countFlags = countMinesOnField;
        countMineNeighbors();
    }

    private List<GameObject> getNeighbors(GameObject gameObject){
        List<GameObject> neighborsList = new ArrayList<>();

        for (int i = 0; i < SIDE; i++){
            for (int j = 0; j < SIDE; j++){
                if (Math.abs(gameField[i][j].x - gameObject.x) <= 1 &&
                Math.abs(gameField[i][j].y - gameObject.y) <= 1)
                    neighborsList.add(gameField[i][j]);
            }
        }

        return neighborsList;
    }

    private void countMineNeighbors(){
        for (int i = 0; i < SIDE; i++){
            for (int j = 0; j < SIDE; j++){
                GameObject currentField = gameField[i][j];

                if (!currentField.isMine){
                    int countNeighborMines = 0;
                    List<GameObject> neighbors = getNeighbors(currentField);
                    for (GameObject gameObject : neighbors){
                        if (gameObject.isMine)
                            countNeighborMines++;
                    }
                    currentField.countMineNeighbors = countNeighborMines;
                }
            }
        }
    }

    private void openTile(int x, int y){
        if (isGameStopped)
            return;

        GameObject currentTile = gameField[y][x];

        if (currentTile.isOpen || currentTile.isFlag)
            return;

        currentTile.isOpen = true;
        countClosedTiles--;

        if (currentTile.isMine){
            setCellValueEx(x, y, Color.RED, MINE);
            gameOver();
        }
        else {
            score += 5;
            setScore(score);
            
            if (countClosedTiles == countMinesOnField) {
                win();
            }
            
            if (currentTile.countMineNeighbors == 0){
                setCellValue(x, y, "");
                for (GameObject go : getNeighbors(currentTile)){
                    if (!go.isOpen){
                        openTile(go.x, go.y);
                    }
                }
            }
            else {
                setCellNumber(x, y, currentTile.countMineNeighbors);
            }

            setCellColor(x, y, Color.GREEN);
        }
    }

    private void markTile(int x, int y) {
        GameObject currentTile = gameField[y][x];

        if (currentTile.isOpen || isGameStopped)
            return;

        if (currentTile.isFlag) {
            currentTile.isFlag = false;
            setCellValue(x, y, EMPTY);
            setCellColor(x, y, Color.ORANGE);
            countFlags++;
        } else {
            if (countFlags > 0) {
                currentTile.isFlag = true;
                setCellValue(x, y, FLAG);
                setCellColor(x, y, Color.YELLOW);
                countFlags--;
            }
        }


    }

    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.BLUE, "Game Over!", Color.YELLOW, 20);
    }

    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.BLUE, "You Win!", Color.YELLOW, 20);
    }

    private void restart() {
        gameField = new GameObject[SIDE][SIDE];
        countClosedTiles = SIDE * SIDE;
        isGameStopped = false;
        countMinesOnField = 0;
        score = 0;
        setScore(score);
        createGame();
    }

    @Override
    public void onMouseLeftClick(int x, int y){
        if (isGameStopped)
            restart();
        else
            openTile(x, y);
    }

    @Override
    public void onMouseRightClick(int x, int y) {
        markTile(x, y);
    }
}
