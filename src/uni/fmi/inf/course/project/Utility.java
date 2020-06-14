package uni.fmi.inf.course.project;

/**
 *
 * @author Nedko Gospodinov
 *
 */

import java.util.Scanner;

public class Utility {

    public static Scanner gameInput = new Scanner(System.in);
    public static String welcomeMessage;
    public static String errorMessage;
    public static String friendsMessage;
    public static String woFriendsMessage;
    public static String loopCompletedMessage;
    public static String trapMenu;
    public static String [] negativeChances = new String[5];
    public static String [] positiveChances = new String[5];
    public static String [] stealTypes = new String[3];

    private static Reader MessagesReader = new Reader("Messages.conf");

    public Utility() {
        initializeMessages();
    }

    /**
     * Това е метод , който се използва за пауза.
     */

    public static void pressEnterToContinue() {
        System.out.println("Press enter to continue...");
        new java.util.Scanner(System.in).nextLine();
    }

    public static int inputNumberWithRange(int minimumValue, int maxValue) {
        int Value = 0;
        do {
            try {
                Value = gameInput.nextInt();
            } catch (Exception e) {
                System.err.println(errorMessage);
                gameInput.nextLine();
            }
        } while (Value < minimumValue || Value > maxValue);

        return Value;
    }

    public static void printBoard() {
        System.out.print("  10 11 12 13 14 15 16 17\n  ");
        for (int i = 10; i <= 17; i++) {
            System.out.printf("|%s|", Game.playingBoard[i]);
        }
        System.out.println();
        System.out.printf("9 |%s|                  |%s| 18\n", Game.playingBoard[9], Game.playingBoard[18]);
        System.out.printf("8 |%s|                  |%s| 19\n  ", Game.playingBoard[8], Game.playingBoard[19]);
        for (int i = 7; i >= 0; i--) {
            System.out.printf("|%s|", Game.playingBoard[i]);
        }
        System.out.print("\n   7  6  5  4  3  2  1  0\n");
    }

    public static void printPlayerInformation(Player Player, int Dice) {
        if (!Player.showInformation) return;

        if (Player.isPlayerOnStart) {
            System.out.printf("%s направи обиколката, евентуално изчаква останалите.\n", Player.getPlayerName());
            Player.showInformation = false;
            return;
        }
        System.out.printf("Оставащи пари: %d    Зарче: %d   Стара позиция: %d\n", Player.getMoney(), Dice,
                Player.getOldPosition());
    }

    public static void initializeMessages() {
        welcomeMessage = getMessageFromFile("welcomeMessage: ");
        errorMessage = getMessageFromFile("errorMessage: ");
        friendsMessage = getMessageFromFile("playerWithFriends: ");
        woFriendsMessage = getMessageFromFile("playerWithoutFriends: ");
        loopCompletedMessage = getMessageFromFile("loopCompleted: ");
        trapMenu = getMessageFromFile("trapMenu: ");
        negativeChances = getChanceMessages("negativeChances:");
        positiveChances = getChanceMessages("positiveChances:");
        getStealMessages();
    }

    private static String [] getChanceMessages(String Message) {
        String tempArray[] = new String[5];
        String tempString = ""; int Flag = 0;
        while(MessagesReader.fileScanner.hasNextLine()) {
            tempString = MessagesReader.fileScanner.nextLine();
            if (tempString.equals(Message)) {
                tempString = MessagesReader.fileScanner.nextLine();
                tempString = tempString.substring(1);
                tempString += '\n';
                while (MessagesReader.fileScanner.hasNextLine() && !tempString.endsWith("\"\n")) {
                    tempString += MessagesReader.fileScanner.nextLine();
                    tempString += '\n';
                    if (tempString.endsWith("|\n")) {
                        tempArray[Flag] = tempString.substring(0, tempString.length() - 3);
                        Flag++;
                        tempString = "";
                    } else if (tempString.endsWith("\"\n")) {
                        tempArray[Flag] = tempString.substring(0, tempString.length() - 2);
                        return tempArray;
                    }
                }
            }
        }
        return new String[] {"Error"};
    }

    private static void getStealMessages() {
        stealTypes[0] = getMessageFromFile("stealCapturingWorld: ");
        stealTypes[1] = getMessageFromFile("stealHostages: ");
        stealTypes[2] = getMessageFromFile("stealBBHaste: ");
    }

    private static String getMessageFromFile(String Message) {
        String tempString;
        while(MessagesReader.fileScanner.hasNextLine()) {
            tempString = MessagesReader.fileScanner.nextLine();
            if (tempString.startsWith(Message)) {
                tempString = tempString.substring(Message.length()+1);
                while (MessagesReader.fileScanner.hasNextLine() && !tempString.endsWith("\"")) {
                    tempString += '\n';
                    tempString += MessagesReader.fileScanner.nextLine();
                }
                tempString = tempString.substring(0,tempString.length()-1);
                return tempString;
            }
        }
        return "";
    }

    public static void showInvestMenu(Player currentPlayer) {
        int firstInvest = 0;    int secondInvest = 0;   int thirdInvest = 0;    int playerChoice = 0;
        do {
            firstInvest = Game.randomGenerator.nextInt(5);
            secondInvest = Game.randomGenerator.nextInt(5);
            thirdInvest = Game.randomGenerator.nextInt(5);
        } while (firstInvest == secondInvest || firstInvest == thirdInvest || secondInvest == thirdInvest);
        while (playerChoice != 4) {
            printInvestMenu(firstInvest, secondInvest, thirdInvest);
            playerChoice = Utility.inputNumberWithRange(1, 4);
            switch (playerChoice) {
                case 1: {
                    playerInvest(currentPlayer, firstInvest);
                } break;
                case 2: {
                    playerInvest(currentPlayer, secondInvest);
                } break;
                case 3: {
                    playerInvest(currentPlayer, thirdInvest);
                } break;
                case 4: {
                    return;
                }
            }
        }
    }

    public static void playerInvest(Player currentPlayer, int numberOfInvest) {
        System.out.printf("Ти избра да направиш инвестиция в \"%s\" посочи сумата, която си склонен да отделиш:\n",
                Game.Invests[numberOfInvest].getInvestName());
        int donatePrice = 0;
        do {
            donatePrice = Utility.inputNumberWithRange(1, 100000);
        } while (donatePrice < Game.Invests[numberOfInvest].getMinValueForInvest());
        System.out.println("Браво, направи успешна инвестиция.");
        Game.Invests[numberOfInvest].investByPlayer.put(currentPlayer, donatePrice);
        currentPlayer.setMoney(currentPlayer.getMoney()-donatePrice);
    }

    private static void printInvestMenu(int first, int second, int third) {
        System.out.println("Инвестирайте разумно и изберете компания.");
        System.out.printf("* (1) %s | min: %d | risk/reward: %.2f\n", Game.Invests[first].getInvestName(),
                Game.Invests[first].getMinValueForInvest(), Game.Invests[first].getReturnRatio());
        System.out.printf("* (2) %s | min: %d | risk/reward: %.2f\n", Game.Invests[second].getInvestName(),
                Game.Invests[second].getMinValueForInvest(), Game.Invests[second].getReturnRatio());
        System.out.printf("* (3) %s | min: %d | risk/reward: %.2f\n", Game.Invests[third].getInvestName(),
                Game.Invests[third].getMinValueForInvest(), Game.Invests[third].getReturnRatio());
        System.out.println("* (4) Не ми се инвестира повече.\n");
    }
}