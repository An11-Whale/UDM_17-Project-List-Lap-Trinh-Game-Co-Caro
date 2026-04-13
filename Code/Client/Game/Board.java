package Code.Client.Game;

public class Board {
    private int[][] board;
    private int size=15;

    public int[][] getBoard() {
        int[][] copy = new int[size][size];
        for (int i = 0; i < size; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, size);
        }
        return copy;
    }

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
    // check boundary
    public boolean isValid(int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size; //kiểm tra xem có nằm trong ma trận vùng chơi k
    }

    public boolean place(int row, int col, int player) {
        if (!isValid(row, col)) return false;

        if (board[row][col] == 0) {
            board[row][col] = player;
            return true;
        }
        return false;
    }

    public int getCell(int row, int col) {
        return board[row][col];
    }
    //getSize cho UI
    public int getSize() {
    return size;
}
}
