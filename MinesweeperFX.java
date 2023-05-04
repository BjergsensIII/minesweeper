import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.input.MouseButton;

import java.util.Random;

public class MinesweeperFX extends Application {
    private static final int ROWS = 10;
    private static final int COLS = 10;
    private static final int NUM_MINES = 10;
    private static final int MINE = -1;
    private static final int UNREVEALED = -2;
    private static final int FLAGGED = -3;

    private int[][] board;
    private boolean[][] revealed;
    private boolean[][] flagged;
    private boolean gameOver;

    private GridPane gridPane;
    private Label statusLabel;

    @Override
    public void start(Stage primaryStage) {
        // Initialize board
        board = new int[ROWS][COLS];
        revealed = new boolean[ROWS][COLS];
        flagged = new boolean[ROWS][COLS];
        gameOver = false;

        Random random = new Random();
        int numMines = 0;
        while (numMines < NUM_MINES) {
            int row = random.nextInt(ROWS);
            int col = random.nextInt(COLS);
            if (board[row][col] != MINE) {
                board[row][col] = MINE;
                numMines++;

                // Update adjacent cells
                for (int i = row - 1; i <= row + 1; i++) {
                    for (int j = col - 1; j <= col + 1; j++) {
                        if (i >= 0 && i < ROWS && j >= 0 && j < COLS && board[i][j] != MINE) {
                            board[i][j]++;
                        }
                    }
                }
            }
        }

        // Create UI
        gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.setPadding(new Insets(10));

        for (int i = 0; i < ROWS; i++) {
            final int row = i;
            for (int j = 0; j < COLS; j++) {
                final int col = j;
                Button button = new Button();
                button.setPrefSize(30, 30);
                button.setOnAction(event -> handleButtonClick(row, col));
                button.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.SECONDARY) {
                        handleRightClick(row, col);
                    }
                });
                gridPane.add(button, j, i);
            }
        }

        statusLabel = new Label("Playing...");
        statusLabel.setAlignment(Pos.CENTER);

        VBox vbox = new VBox(10, gridPane, statusLabel);
        vbox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vbox);
        scene.getStylesheets().add("style.css"); // Load the CSS file
        primaryStage.setScene(scene);
        primaryStage.setTitle("Minesweeper");
        primaryStage.show();
    }

    private void handleButtonClick(int row, int col) {
        if (gameOver) {
            return;
        }

        if (flagged[row][col]) {
            return;
        }

        if (board[row][col] == MINE) {
            gameOver = true;
            revealAll();
            statusLabel.setText("Game over!");
            return;
        }

        revealCell(row, col);

        if (checkWin()) {
            gameOver = true;
            revealAll();
            statusLabel.setText("You win!");
        }
    }

    private void handleRightClick(int row, int col) {
        if (gameOver) {
            return;
        }

        flagged[row][col] = !flagged[row][col];

        Button button = (Button) gridPane.getChildren().get(row * COLS + col);
        if (flagged[row][col]) {
            button.setText("F");
        } else {
            button.setText("");
        }
    }

    private void revealCell(int row, int col) {
        if (revealed[row][col]) {
            return;
        }

        revealed[row][col] = true;

        Button button = (Button) gridPane.getChildren().get(row * COLS + col);
        button.setDisable(true);

        if (board[row][col] == 0) {
            for (int i = row - 1; i <= row + 1; i++) {
                for (int j = col - 1; j <= col + 1; j++) {
                    if (i >= 0 && i < ROWS && j >= 0 && j < COLS) {
                        revealCell(i, j);
                    }
                }
            }
        } else if (board[row][col] != MINE) {
            button.setText(Integer.toString(board[row][col]));
            }
    }

    private void revealAll() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (board[row][col] == MINE) {
                    Button button = (Button) gridPane.getChildren().get(row * COLS + col);
                    button.setText("X");
                    button.setDisable(true);
                } else if (board[row][col] != UNREVEALED) {
                    Button button = (Button) gridPane.getChildren().get(row * COLS + col);
                    button.setText(Integer.toString(board[row][col]));
                    button.setDisable(true);
                }
            }
        }
    }
    private boolean checkWin() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (!revealed[row][col] && board[row][col] != MINE) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}