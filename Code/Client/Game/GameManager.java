package Code.Client.Game;

import Code.Client.Network.ClientSocket;

public class GameManager {
    private Board board;
    private ClientSocket client;

    private int currentPlayer;   //player đang đi
    private int myPlayerId;      //player của mình

    private boolean isMyTurn;
    private boolean isGameOver;

    public interface GameListener {
        void onWin(int player);
        void onLose();
        void onMove(int row, int col, int player);
    }
    private GameListener listener;

    public void setListener(GameListener listener) {
        this.listener = listener;
    }

    public GameManager(ClientSocket client, int myPlayerId) {
        this.board = new Board();
        this.client = client;
        this.myPlayerId = myPlayerId;

        this.currentPlayer = 1; //mặc định player 1 đi trước
        this.isMyTurn = (myPlayerId == currentPlayer);
        this.isGameOver = false;
    }
    //player click
    public boolean makeMove(int row, int col) {
        if (isGameOver) return false;
        if (!isMyTurn) return false;

        //đặt quân
        boolean success = board.place(row, col, myPlayerId);
        if (!success) return false;

        //gửi lên server
        client.sendMove(row, col);

        //check win
        if (checkWin(row, col)) {
            isGameOver = true;
            
            if (listener != null) {
                listener.onWin(myPlayerId);
            }
        }
        switchTurn();
        return true;
    }
    //nhận move từ server
    public void onOpponentMove(int row, int col) {
        if (isGameOver) return;

        int opponentId = (myPlayerId == 1) ? 2 : 1;

        board.place(row, col, opponentId);

        if (checkWin(row, col)) {
            isGameOver = true;
            if (listener != null) {
                listener.onLose();
            }
        }
        switchTurn();
    }
    //đổi lượt
    public void switchTurn() {
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
        isMyTurn = (currentPlayer == myPlayerId);
    }
    //check win
    public boolean checkWin(int row, int col) {
        int player = board.getCell(row, col);

        return count(row, col, 1, 0, player)   //ngang
             + count(row, col, -1, 0, player) > 4 ||

               count(row, col, 0, 1, player)   //dọc
             + count(row, col, 0, -1, player) > 4 ||

               count(row, col, 1, 1, player)   //chéo \
             + count(row, col, -1, -1, player) > 4 ||

               count(row, col, 1, -1, player)  //chéo /
             + count(row, col, -1, 1, player) > 4;
    }

    private int count(int row, int col, int dRow, int dCol, int player) {
        int cnt = 0;
        int r = row + dRow;
        int c = col + dCol;

        while (r >= 0 && r < board.getSize() &&
               c >= 0 && c < board.getSize() &&
               board.getCell(r, c) == player) {

            cnt++;
            r += dRow;
            c += dCol;
        }

        return cnt;
    }
    //getter
    public Board getBoard() {
        return board;
    }

    public boolean isMyTurn() {
        return isMyTurn;
    }

    public boolean isGameOver() {
        return isGameOver;
    }
}
