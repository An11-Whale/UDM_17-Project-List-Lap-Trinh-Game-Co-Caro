package Code.Client.Game;

public class Board {
    private int[][] board;
    private int size=15;

    public Board() {
        board = new int[this.size][this.size];
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                board[i][j] = 0;
            }
        }
    }

    public void reset() {
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                board[i][j] = 0;
            }
        }
    }

    public boolean place(int row, int col, int player) {
        if (board[row][col] == 0) {
            board[row][col] = player;
            return true;
        }
        return false;
    }

    public int getCell(int row, int col) {
        return board[row][col];
    }
}
