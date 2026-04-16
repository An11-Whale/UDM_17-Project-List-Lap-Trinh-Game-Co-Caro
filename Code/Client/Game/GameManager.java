package Code.Client.Game;


import Code.Client.Network.SocketHandler;

public class GameManager {
    private Board board;
    private SocketHandler client;

    private int currentPlayer;   //player đang đi
    private int myPlayerId;      //player của mình

    private boolean isMyTurn;
    private boolean isGameOver;

    public interface GameListener {
        void onWin(int player);
        void onLose();
        void onMove(int row, int col, int player);
        void onDraw();
        void onReset();
        void onTurnChanged(int playerId);
    }
    private GameListener listener;

    public void setListener(GameListener listener) {
        this.listener = listener;
    }

    public GameManager(SocketHandler client, int myPlayerId) {
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

        // UPDATE: Khóa turn ngay lập tức để tránh người chơi spam click gửi 2 nước liên tiếp
        isMyTurn = false;

        client.sendMove(row, col);

        return true;
    }
    
    //nhận move từ server
    public void onServerMove(int row, int col, int player) {
        if (isGameOver) return;

        // UPDATE: Chặn nước cờ nếu server trả về player không trùng với lượt hiện tại (lọc lỗi đồng bộ lặp nước cờ)
        if (player != currentPlayer) {
            System.err.println("Cảnh báo: Dữ liệu nhận sai lượt! Lượt hiện tại là " + currentPlayer + " nhưng nhận được nước đi của " + player);
            return;
        }

        boolean success = board.place(row, col, player);
        if (!success) {
            // Nước đi không hợp lệ (ô đã đánh)
            return;
        }

        if (listener != null) {
            listener.onMove(row, col, player);
        }

        if (checkWin(row, col)) {
            isGameOver = true;

            if (listener != null) {
                if (player == myPlayerId) {
                    listener.onWin(player);
                } else {
                    listener.onLose();
                }
            }

        } else if (isBoardFull()) {
            isGameOver = true;

            if (listener != null) {
                listener.onDraw();
            }
        }

        if (!isGameOver) {
            switchTurn();
        }
    }


    //đổi lượt
    public void switchTurn() {
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
        isMyTurn = (currentPlayer == myPlayerId);

        if (listener != null) {
            listener.onTurnChanged(currentPlayer);
        }
    }
    
    //check win
    public boolean checkWin(int row, int col) {
        int player = board.getCell(row, col);
        if (player == 0) return false; //tránh player == 0

        return count(row, col, 1, 0, player)   //ngang
             + count(row, col, -1, 0, player) >= 4 ||

               count(row, col, 0, 1, player)   //dọc
             + count(row, col, 0, -1, player) >= 4 ||

               count(row, col, 1, 1, player)   //chéo \
             + count(row, col, -1, -1, player) >= 4 ||

               count(row, col, 1, -1, player)  //chéo /
             + count(row, col, -1, 1, player) >= 4;
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

    //check hòa
    private boolean isBoardFull() {
        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                if (board.getCell(i, j) == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    // Đánh dấu game over (cho timeout / surrender)
    public void forceGameOver() {
        isGameOver = true;
        isMyTurn = false;
    }

    //reset game cho UI
    public void resetGame() {
        board.reset();
        isGameOver = false;

        currentPlayer = 1;
        isMyTurn = (myPlayerId == currentPlayer);

        if (listener != null) {
            listener.onReset();
        }
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

    public int[][] getBoardData() {
        return board.getBoard();
    }

    public int getMyPlayerId() {
        return myPlayerId;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }
}
