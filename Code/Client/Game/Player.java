package Code.Client.Game;

public class Player {
    private int playerId;
    private String playerName;
    private int symbol; //1 hoac 2 (x or o)
    public Player(int playerId, String playerName, int symbol) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.symbol = symbol;
    }
    public int getPlayerId() {
        return playerId;
    }
    public String getPlayerName() {
        return playerName;
    }
    public int getSymbol() {
        return symbol;
    }
    public void setSymbol(int symbol) {
        this.symbol = symbol;
    }
}
