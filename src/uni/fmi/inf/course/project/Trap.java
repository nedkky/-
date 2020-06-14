package uni.fmi.inf.course.project;

/**
 *
 * @author Nedko Gospodinov
 *
 */

public class Trap {

    private int trapPosition;
    private String trapOwner;
    private int trapType;

    public Trap() {
        trapPosition = -1;
        trapOwner = "";
        trapType = 0;
    }

    public int getTrapType() { return trapType; }

    public int getTrapPosition() { return trapPosition; }

    public String getTrapOwner() { return trapOwner; }

    public void setTrapPosition(int newTrapPosition) { trapPosition = newTrapPosition; }

    public void setTrapOwner(String newTrapOwner) { trapOwner = newTrapOwner; }

    public void setTrapType(int newTrapType) { trapType = newTrapType; }

    public void setupTrap(int Position, int Type, String Owner) {
        trapPosition = Position;
        trapType = Type;
        trapOwner = Owner;
    }

    public void resetTrap() {
        trapPosition = -1;
        trapOwner = "";
        trapType = 0;
    }

    public void activateTrap(Player currentPlayer) {
        String playerName = currentPlayer.getPlayerName();
        if (playerName == trapOwner) {
            if (Game.throwDice(11) % 3 == 0) {
                System.out.printf("%s се отърва от собствения си капан без последствия!\n", playerName);
            } else {
                parserTrapType(trapType, currentPlayer);
            }
        } else if (playerName != trapOwner) {
            parserTrapType(trapType, currentPlayer);
        }
    }

    private void parserTrapType(int typeOfTrap, Player currentPlayer) {
        System.out.printf("%s попадна в капан на %s! ", currentPlayer.getPlayerName(), trapOwner);
        switch (typeOfTrap) {
            case 0: {
                currentPlayer.taxRevision++;
                System.out.printf("На %s ще му правят данъчна ревизия %d път/и!\n", currentPlayer.getPlayerName(),
                        currentPlayer.taxRevision);
            } break;
            case 1: {
                System.out.printf("%s има шанс да не получи нищо в края на този цикъл!\n", currentPlayer.getPlayerName());
                currentPlayer.wontReceiveMoney = true;
            } break;
            case 2: {
                System.out.printf("%s няма право да поставя повече капани този цикъл!\n", currentPlayer.getPlayerName());
                currentPlayer.cannotPleaseTraps = true;
            } break;
            case 3: {
                System.out.printf("%s няма да може да реализира следващия си зъл план!\n", currentPlayer.getPlayerName());
                currentPlayer.cannotSteel = true;
            } break;
            case 4: {
                System.out.printf("%s ще получи само негативи от следващия си шанс!\n", currentPlayer.getPlayerName());
                currentPlayer.onlyNegativeChances = true;
            } break;
        }
        resetTrap();
    }
}
