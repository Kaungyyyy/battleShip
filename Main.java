import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Initializing the game board
        Object[][] board = new Object[11][11];
        for (int rows = 0; rows < 11; rows++) {
            for (int cols = 0; cols < 11; cols++) {
                board[0][cols] = cols;  // Set column labels
                board[rows][0] = (char) ('@' + rows);  // Set row labels
            }
        }
        board[0][0] = ' ';  // Top-left cell

        // Fill the board with empty cells
        for (int rows = 1; rows < 11; rows++) {
            for (int cols = 1; cols < 11; cols++) {
                board[rows][cols] = "  ";  // Empty cells
            }
        }

        // Create a list of ships
        List<Ship> ships = new ArrayList<>();
        ships.add(new Ship("Carrier", 5));
        ships.add(new Ship("Battleship", 4));
        ships.add(new Ship("Cruiser", 3));
        ships.add(new Ship("Submarine", 3));
        ships.add(new Ship("Destroyer", 2));

        // Place ships randomly on the board
        Object[][] updatedBoard = placeShipsRandomly(board, ships);

        // Initializing user input
        Scanner input = new Scanner(System.in);
        int guessCount = 0;

        // Main game loop
        while (!allShipsSunk(board, ships)) {
            showBoard(updatedBoard);

            System.out.println("Enter your guess: ");
            String guesses = input.next();
            guessCount++;  // Count the number of guesses the user made

            // Validate the guess format
            if (guessValidation(guesses)) {
                processTheGuesses(board, ships, guesses);
            }
        }

        // Game over
        System.out.println("Victory! You sunk all ships in " + guessCount + " guesses. Thank you for playing the game.");
    }

    // Place ships randomly on the game board
    public static Object[][] placeShipsRandomly(Object[][] gameBoard, List<Ship> battleShips) {
        Random randomPlacement = new Random();
        for (Ship ship : battleShips) {
            boolean isNotPlaced = true;

            while (isNotPlaced) {
                int rows = (int) (Math.random() * (11 - 1) + 1);
                int cols = (int) (Math.random() * (11 - 1) + 1);
                int orientation = randomPlacement.nextInt(2);

                if (orientation == 0) {
                    if (validation(gameBoard, rows, cols, orientation, ship)) {
                        for (int i = 0; i < ship.getShipSize(); i++) {
                            gameBoard[rows][cols + i] = ship;
                        }
                        isNotPlaced = false;
                    }
                } else {
                    if (validation(gameBoard, rows, cols, orientation, ship)) {
                        for (int i = 0; i < ship.getShipSize(); i++) {
                            gameBoard[rows + i][cols] = ship;
                        }
                        isNotPlaced = false;
                    }
                }
            }
        }
        return gameBoard;
    }

    // Display the game board
    public static void showBoard(Object[][] board) {
        for (int rows = 0; rows < 11; rows++) {
            for (int cols = 0; cols < 11; cols++) {
                System.out.print(board[rows][cols] + "\t");
            }
            System.out.println();
        }
    }

    // Validate if it's possible to place the ship at the given location
    public static boolean validation(Object[][] gameBoard, int rows, int cols, int orientation, Ship ship) {
        int numRows = gameBoard.length;
        int numCols = gameBoard[0].length;

        for (int i = 0; i < ship.getShipSize(); i++) {
            if (orientation == 0) {  // Horizontal
                if (cols + i >= numCols || !gameBoard[rows][cols + i].equals("  ")) {
                    return false;
                }
            } else {  // Vertical
                if (rows + i >= numRows || !gameBoard[rows + i][cols].equals("  ")) {
                    return false;
                }
            }
        }
        return true;
    }

    // Check if all ships are sunk
    public static boolean allShipsSunk(Object[][] gameBoard, List<Ship> ships) {
        for (Ship ship : ships) {
            if (!ship.isSunk()) {
                return false;  // If all the ships are not sunk, the game is not over
            }
        }
        return true;
    }

    // Validate the guess format
    public static boolean guessValidation(String guesses) {
        if (guesses.length() < 2 || guesses.length() > 3) {
            System.out.println("Invalid guess format. Please enter a valid coordinate (e.g., A1 to J10).");
            return false;
        }
        return true;
    }

    // Process the user's guesses
    public static void processTheGuesses(Object[][] gameBoard, List<Ship> ships, String guesses) {
        if (guesses.length() == 3) {
            System.out.println("Invalid guess format. Please enter a valid coordinate (e.g., A1 to J10).");
            return;
        }

        char rowChar = guesses.charAt(0);
        int col;

        if (guesses.length() == 2) {
            col = Integer.parseInt(guesses.substring(1));
        } else {
            col = 10;  // If it's "A10", set col to 10.
        }

        if (rowChar >= 'A' && rowChar <= 'J' && col >= 1 && col <= 10) {
            int row = rowChar - 'A' + 1;

            if (gameBoard[row][col] instanceof Ship) {
                Ship hitShip = (Ship) gameBoard[row][col];
                System.out.println("You hit the " + hitShip.getShipType() + "!");

                // Mark the hit on the board
                gameBoard[row][col] = "X";

                // Update the ship's hit count
                hitShip.hit();

                if (hitShip.isSunk()) {
                    System.out.println("You sunk the " + hitShip.getShipType() + "!");
                }
            } else if (gameBoard[row][col].equals("  ")) {
                System.out.println("You missed!");
                // Mark the miss on the board
                gameBoard[row][col] = ".";
            } else {
                System.out.println("You've already guessed this location.");
            }
        } else {
            System.out.println("Invalid guess. Please enter a valid coordinate (e.g., A1 to J10).");
        }
    }
}
