package com.codegym.games.minesweeper;

import com.codegym.engine.cell.*;

import java.util.ArrayList;

public class MinesweeperGame extends Game {
    private static final int SIDE = 9;
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private int countMinesOnField;
    private int countFlags;
    private int countClosedTiles = SIDE * SIDE;
    private int score;
    private boolean isGameStopped;

    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    private void createGame() {
        for (int x = 0; x < SIDE; x++) {
            for (int y = 0; y < SIDE; y++) {
                boolean isMine = getRandomNumber(10) < 1;
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[x][y] = new GameObject(y, x, isMine);
                setCellColor(x, y, Color.WHITE);
                setCellValue(x, y, "");
            }
        }
        countMineNeighbors();
        countFlags = countMinesOnField;
    }

    private ArrayList<GameObject> getNeighbors(GameObject gameObject) {
        ArrayList<GameObject> neighbourList = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                neighbourList.add(gameField[y][x]);
            }
        }
        return neighbourList;
    }

    private void countMineNeighbors() {
        for (int x = 0; x < SIDE; x++) {
            for (int y = 0; y < SIDE; y++) {
                if (!gameField[x][y].isMine) {
                    for (GameObject cell : getNeighbors(gameField[x][y])) {
                        if (cell.isMine)
                            gameField[x][y].countMineNeighbors++;
                    }
                }
            }
        }
    }

    private void openTile(int x, int y) {
//        GameObject gameObject = gameField[y][x];
        if (gameField[y][x].isOpen || gameField[y][x].isFlag || isGameStopped)
            return;

        gameField[y][x].isOpen = true;
        countClosedTiles--;

        if (countClosedTiles == countMinesOnField && !gameField[y][x].isMine)
            win();

        if (gameField[y][x].isMine) {
            setCellValue(x, y, MINE);
            setCellValueEx(x, y, Color.RED, MINE);
            gameOver();
            return;

        } else if (!gameField[y][x].isMine) {
            setScore(score += 5);
            setCellNumber(x, y, gameField[y][x].countMineNeighbors);
        }
        setCellColor(x, y, Color.LIGHTGREEN);

        if (gameField[y][x].countMineNeighbors >= 1) {
            setCellNumber(x, y, gameField[y][x].countMineNeighbors);
            setCellColor(x, y, Color.YELLOWGREEN);

        } else if (gameField[y][x].countMineNeighbors == 0) {
            setCellValue(x, y, "");

            for (GameObject cell : getNeighbors(gameField[y][x])) {
                if (!gameField[cell.y][cell.x].isOpen)
                    openTile(cell.x, cell.y);
            }
        }
    }

    private void markTile(int x, int y) {
        GameObject gameObject = gameField[y][x];
        if (!gameObject.isOpen && !isGameStopped) {
            if (!gameObject.isFlag && countFlags > 0) {
                gameObject.isFlag = true;
                countFlags--;
                setCellValue(x, y, FLAG);
                setCellColor(x, y, Color.YELLOW);
            } else if (gameObject.isFlag) {
                gameObject.isFlag = false;
                countFlags++;
                setCellValue(x, y, "");
                setCellColor(x, y, Color.WHITE);
            }
        }
    }

    @Override
    public void onMouseLeftClick(int x, int y) {
        if (isGameStopped) {
            restart();
            return;
        }
        this.openTile(x, y);
    }

    @Override
    public void onMouseRightClick(int x, int y) {
        markTile(x, y);
    }

    private void gameOver() {
        showMessageDialog(Color.RED, "You lost", Color.BLACK, 40);
        isGameStopped = true;
    }

    private void win() {
        showMessageDialog(Color.GREEN, "You win! Congratulations!" + "\nYour score is: " + score, Color.BLACK, 30);
        isGameStopped = true;
    }

    private void restart() {
        isGameStopped = false;
        countMinesOnField = 0;
        countClosedTiles = SIDE * SIDE;
        score = 0;
        setScore(score);
        countMinesOnField = 0;
        createGame();
    }
}