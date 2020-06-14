package uni.fmi.inf.course.project;

/**
 *
 * @author Nedko Gospodinov
 *
 */

public class Player {
    private int Money = 0;
    private int Position = 0;
    private int oldPosition = 0;
    private String playerName = "";
    public int taxRevision = 0;
    public boolean cannotPleaseTraps = false;
    public boolean onlyNegativeChances = false;
    public boolean cannotSteel = false;
    public boolean wontReceiveMoney = false;
    public boolean isPlayerOnStart = false;
    public boolean showInformation = true;
    public boolean activatedSteal = false;
    public int typeOfSteal = 0;

    public Player () {
        Money = 1000;
        Position = 0;
    }

    public void resetPlayerFlags() {
        isPlayerOnStart = false;
        showInformation = true;
        cannotPleaseTraps = false;
        wontReceiveMoney = false;
        activatedSteal = false;
        taxRevision = 0;
    }

    public void setPlayerName(String newPlayerName) { playerName = newPlayerName; }

    public void setMoney(int newMoney) {
        Money = newMoney;
    }

    public void setPosition(int newPosition) {
        oldPosition = Position;
        Position = newPosition;
    }

    public int getOldPosition() { return oldPosition; }

    public String getPlayerName() {
        return playerName;
    }

    public int getMoney() {
        return Money;
    }

    public int getPosition() {
        return Position;
    }
}
