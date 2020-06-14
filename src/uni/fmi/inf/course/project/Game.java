package uni.fmi.inf.course.project;

import java.util.*;

/**
 *
 * @author Nedko Gospodinov
 *
 */

public class Game {

    private static Reader InvestReader = new Reader("Invest.conf");
    private static int whoIsFirst;
    private static int playersCount;
    private static int Dice;
    private static boolean isGameRunning;
    private static boolean playingVSBot;
    private static Utility Messages;
    public static Player [] Players;
    public static Trap [] Traps;
    public static int [] trapPrice = new int[] {100,200,100,50,100};
    public static Random randomGenerator = new Random();
    public static Invest[] Invests = new Invest[5];
    public static String [] playingBoard = new String[20];

    public static void main(String [] args) {
        initializeGame();
        startGame();
    }

    private static void startGame() {
        isGameRunning = true;
        whoIsFirst = randomGenerator.nextInt(playersCount);
        do {
            System.out.printf("Първи наред е: %s\n", Players[whoIsFirst].getPlayerName());
            movePlayers();
            actionsAfterLoop();
            generateBoard();
        } while (isGameRunning);
    }

    private static void movePlayers() {
        int i = whoIsFirst;
        do {
            if (i == playersCount) i = 0;
            if (!Players[i].isPlayerOnStart) {
                Dice = throwDice(2);
                int playerNewPosition = Players[i].getPosition() + Dice;
                if (playerNewPosition >= 20) {
                    Players[i].isPlayerOnStart = true;
                    Players[i].setPosition(0);
                } else if (playerNewPosition < 20 ){
                    Players[i].setPosition(playerNewPosition);
                }
                parserPosition(Players[i]);
                Utility.pressEnterToContinue();
            }
            i++;
        } while (!checkPlayersLoop());
        System.out.println(Messages.loopCompletedMessage);
        Utility.pressEnterToContinue();
    }

    private static void actionsAfterLoop() {
        taxRevision();
        resetPlayersFlags();
        initializeSteals();
        checkAllInvestition();
    }

    private static void checkAllInvestition() {
        for (int i = 0; i < Invests.length; i++) {
            for(HashMap.Entry<Player, Integer> e : Invests[i].investByPlayer.entrySet()) {
                Player currentPlayer = e.getKey();
                int Price = e.getValue();
                currentPlayer.setMoney((int)(currentPlayer.getMoney()+ Price*Invests[i].getReturnRatio()));
            }
        }
        for (int i = 0; i < playersCount; i++) {
            System.out.printf("Новите пари на %s са: %d\n", Players[i].getPlayerName(), Players[i].getMoney());
        }
    }

    private static void taxRevision() {
        boolean taxRevision = false;
        for (int i = 0; i < playersCount; i++) {
            int playerCurrentMoney = Players[i].getMoney();
            int playerOldMoney = playerCurrentMoney;

            for (int x = 0; x < Players[i].taxRevision; x++) {
                playerCurrentMoney -= playerCurrentMoney*0.10;
                taxRevision = true;
            }

            if (taxRevision) {
                System.out.printf("Данъчната ревизия на %s се отрази зле на бюджета му и е с минус %d ш.п.\n",
                        Players[i].getPlayerName(), playerOldMoney - playerCurrentMoney);
                Players[i].setMoney(playerCurrentMoney);
            }
            taxRevision = false;
        }
    }

    private static void parserPosition(Player currentPlayer) {
        if (currentPlayer.getPosition() == 0) { Utility.printPlayerInformation(currentPlayer, 0); return; }
        System.out.printf("-------------= %s =-------------:\n", currentPlayer.getPlayerName());
        Utility.printBoard();
        System.out.printf("Нова позиция: %d\n", currentPlayer.getPosition());
        boolean alreadyActivatedTrap = false;
        if (checkForTrap(currentPlayer)) alreadyActivatedTrap = true;
        switch (playingBoard[currentPlayer.getPosition()].charAt(0)) {
            case 'T': {
                if (alreadyActivatedTrap || currentPlayer.cannotPleaseTraps) break;
                stepOnTrapBox(currentPlayer);
            } break;
            case 'I': {
                checkForActivatedSteal(currentPlayer);
                stepOnInvestBox(currentPlayer);
            } break;
            case 'P': {
                System.out.printf("%s отиде на яко парти, похарчи 25 ш.п. по малки сладки котки и яко пиене.. <3 :D\n",
                        currentPlayer.getPlayerName());
                int playerMoney = currentPlayer.getMoney();
                currentPlayer.setMoney(playerMoney-25);
            } break;
            case 'C': {
                checkForActivatedSteal(currentPlayer);
                stepOnChanceBox(currentPlayer);
            } break;
            case 'S': {
                checkForActivatedSteal(currentPlayer);
                stepOnSteelBox(currentPlayer);
            } break;
        }
        Utility.printPlayerInformation(currentPlayer, Dice);
    }

    private static void stepOnInvestBox(Player currentPlayer) {
        Utility.showInvestMenu(currentPlayer);
    }

    private static void stepOnChanceBox(Player currentPlayer) {
        int tempDice = throwDice(100);
        if (currentPlayer.onlyNegativeChances || throwDice(10) % 2 != 0) {
            currentPlayer.onlyNegativeChances = false;
            negativeChance(tempDice, currentPlayer);
        } else {
            positiveChance(tempDice, currentPlayer);
        }
    }

    private static void positiveChance(int Dice, Player currentPlayer) {
        if (Dice >= 1 && Dice <= 39) {
            System.out.printf("Днес е радостен ден за %s.\n%s\nПечели %d парички.\n", currentPlayer.getPlayerName(),
                    Messages.positiveChances[0],50);
            currentPlayer.setMoney(currentPlayer.getMoney()+50);
        } else if (Dice >= 40 && Dice <= 64) {
            System.out.printf("Днес е радостен ден за %s.\n%s\nПечели %d парички.\n", currentPlayer.getPlayerName(),
                    Messages.positiveChances[1],100);
            currentPlayer.setMoney(currentPlayer.getMoney()+100);
        } else if (Dice >= 65 && Dice <= 79) {
            System.out.printf("Днес е радостен ден за %s.\n%s\nПечели %d парички.\n", currentPlayer.getPlayerName(),
                    Messages.positiveChances[2],150);
            currentPlayer.setMoney(currentPlayer.getMoney()+150);
        } else if (Dice >= 80 && Dice <= 94) {
            System.out.printf("Днес е радостен ден за %s.\n%s\nПечели %d парички.\n", currentPlayer.getPlayerName(),
                    Messages.positiveChances[3],200);
            currentPlayer.setMoney(currentPlayer.getMoney()+200);
        } else if (Dice >= 95 && Dice <= 100) {
            System.out.printf("Днес е радостен ден за %s.\n%s\nПечели %d парички.\n", currentPlayer.getPlayerName(),
                    Messages.positiveChances[4],250);
            currentPlayer.setMoney(currentPlayer.getMoney()+250);
        }
    }

    private static void negativeChance(int Dice, Player currentPlayer) {
        if (Dice >= 1 && Dice <= 39) {
            System.out.printf("%s изтегли късата клечка.\n%s\nГуби %d парички.\n", currentPlayer.getPlayerName(),
                    Messages.negativeChances[0],50);
            currentPlayer.setMoney(currentPlayer.getMoney()-50);
        } else if (Dice >= 40 && Dice <= 64) {
            System.out.printf("%s изтегли късата клечка.\n%s\nГуби %d парички.\n", currentPlayer.getPlayerName(),
                    Messages.negativeChances[1],100);
            currentPlayer.setMoney(currentPlayer.getMoney()-100);
        } else if (Dice >= 65 && Dice <= 79) {
            System.out.printf("%s изтегли късата клечка.\n%s\nГуби %d парички.\n", currentPlayer.getPlayerName(),
                    Messages.negativeChances[2],150);
            currentPlayer.setMoney(currentPlayer.getMoney()-150);
        } else if (Dice >= 80 && Dice <= 94) {
            System.out.printf("%s изтегли късата клечка.\n%s\nГуби %d парички.\n", currentPlayer.getPlayerName(),
                    Messages.negativeChances[3],200);
            currentPlayer.setMoney(currentPlayer.getMoney()-200);
        } else if (Dice >= 95 && Dice <= 100) {
            System.out.printf("%s изтегли късата клечка.\n%s\nГуби %d парички.\n", currentPlayer.getPlayerName(),
                    Messages.negativeChances[4],250);
            currentPlayer.setMoney(currentPlayer.getMoney()-250);
        }
    }

    private static void stepOnSteelBox(Player currentPlayer) {
        if (currentPlayer.cannotSteel) {
            System.out.println("Не можеш да активираш злия си план, тъй като си бил попаднал в капан..\n");
            currentPlayer.cannotSteel = false;
            return;
        } else if (!currentPlayer.activatedSteal) {
            for (int i = 0; i < playersCount; i++) {
                if (currentPlayer.getPosition() == Players[i].getPosition() &&
                    currentPlayer.getPlayerName() != Players[i].getPlayerName()) {
                    System.out.printf("%s вече е стъпил на това Steal квадратче, за това %s няма да активира злия си план.\n"
                    ,Players[i].getPlayerName(), currentPlayer.getPlayerName());
                    return;
                }
            }
            System.out.printf("%s активира злия си план. Мухахаха!\n", currentPlayer.getPlayerName());
            currentPlayer.activatedSteal = true;
        }
    }

    private static void checkForActivatedSteal(Player currentPlayer) {
        if (currentPlayer.activatedSteal) {
            if (playingBoard[currentPlayer.getPosition()].equals("C") && currentPlayer.typeOfSteal == 0) {
                System.out.printf("%s реализира своя зъл план и получава 100 ш.п.!\n", currentPlayer.getPlayerName());
                currentPlayer.setMoney(currentPlayer.getMoney()+100);
            } else if (playingBoard[currentPlayer.getPosition()].equals("I") && currentPlayer.typeOfSteal == 1) {
                System.out.printf("%s реализира своя зъл план и получава 100 ш.п.!\n", currentPlayer.getPlayerName());
                currentPlayer.setMoney(currentPlayer.getMoney()+100);
            } else if (playingBoard[currentPlayer.getPosition()].equals("S") && currentPlayer.typeOfSteal == 2) {
                System.out.printf("%s реализира своя зъл план и получава 100 ш.п.!\n", currentPlayer.getPlayerName());
                currentPlayer.setMoney(currentPlayer.getMoney()+100);
            }
        }
    }

    private static void stepOnTrapBox(Player currentPlayer) {
            if (currentPlayer.getMoney() > 50) {
                placeTrap(currentPlayer);
            } else if (currentPlayer.getMoney() <= 50) {
                System.out.println("Нямаш достатъчно пари, за да заложиш капан.");
            }
    }

    private static boolean checkForTrap(Player currentPlayer) {
        int playerPos = currentPlayer.getPosition();
        for (int i = 0; i < Traps.length; i++) {
            if (Traps[i].getTrapPosition() == playerPos) {
                Traps[i].activateTrap(currentPlayer);
                return true;
            }
        }
        return false;
    }

    private static void placeTrap(Player currentPlayer) {
        int trapPos = currentPlayer.getPosition();
        int trapType = 0;
        int playerCurrentMoney = currentPlayer.getMoney();
        String trapOwner = currentPlayer.getPlayerName();
        System.out.printf("Желаете ли да заложите капан на позиция - %d ?\n", currentPlayer.getPosition());
        System.out.println(Messages.trapMenu);
        int userChoice = Utility.inputNumberWithRange(1,6);
        if (userChoice != 6) {
            int i = 0;
            do {
                if (Traps[i].getTrapPosition() != -1) { i++; continue; }
                trapType = userChoice - 1;
                Traps[i].setupTrap(trapPos, trapType, trapOwner);
                currentPlayer.setMoney(playerCurrentMoney - trapPrice[userChoice - 1]);
                break;
            } while (true);
        }
    }

    private static boolean checkPlayersLoop() {
        boolean areAllPlayersOnStart = true;
        for (int i = 0; i < playersCount; i++) {
            if (!Players[i].isPlayerOnStart) {
                areAllPlayersOnStart = false;
            }
        }
        return areAllPlayersOnStart;
    }

    public static int throwDice(int N) {
        int dice = randomGenerator.nextInt(N);
        return dice+1;
    }

    private static void initializeGame() {
        Messages = new Utility();
        generateBoard();
        chooseGameMode();
        initializeTraps();
        initializeSteals();
        initializeInvestObjects();
    }

    private static void chooseGameMode() {
        int userChoice = 0;
        System.out.print(Messages.welcomeMessage);
        userChoice = Utility.inputNumberWithRange(1,2);

        if (userChoice == 1) {
            System.out.println(Messages.woFriendsMessage);
            playingVSBot = true;
            initPlayers();
        } else if (userChoice == 2) {
            playingVSBot = false;
            System.out.print(Messages.friendsMessage);
            playersCount = Utility.inputNumberWithRange(2,20);
            initPlayers();
        }
    }

    private static void initPlayers() {
        Utility.gameInput.nextLine();
        if (playingVSBot) {
            playersCount = 2;
            Players = new Player[playersCount];
            System.out.println("А м/д как се казваш?");
            Players[0] = new Player(); Players[1] = new Player();
            Players[0].setPlayerName(Utility.gameInput.nextLine());
            Players[1].setPlayerName("BOT Трушачката");
        }  else if (!playingVSBot) {
            Players = new Player[playersCount];
            for (int i = 0; i < playersCount; i++) {
                Players[i] = new Player();
                System.out.printf("Как се казва играч № %d?\n", i);
                Players[i].setPlayerName(Utility.gameInput.nextLine());
            }
        }
    }

    private static void initializeSteals() {
        int tempInt = 0;
        for (int i = 0; i < playersCount; i++) {
            tempInt = randomGenerator.nextInt(3);
            Players[i].typeOfSteal = tempInt;
            System.out.printf("Злият план на %s е %s.\n", Players[i].getPlayerName(), Messages.stealTypes[tempInt]);
            Utility.pressEnterToContinue();
        }
    }

    private static void initializeInvestObjects() {
        String investName; String tempString;
        int minInvest; int negativeInterval;
        double returnRatio; int positiveInterval;
        for (int i = 0; i < Invests.length; i++) {
            tempString = InvestReader.fileScanner.nextLine();
            investName = tempString.substring(0,tempString.indexOf("|"));
            tempString = tempString.substring(tempString.indexOf("|")+1);
            minInvest = Integer.parseInt(tempString.substring(0,tempString.indexOf("|")));
            tempString = tempString.substring(tempString.indexOf("|")+1);
            returnRatio = Double.parseDouble(tempString.substring(0,tempString.indexOf("|")));
            tempString = tempString.substring(tempString.indexOf("|")+1);
            negativeInterval = Integer.parseInt(tempString.substring(0,tempString.indexOf("|")));
            tempString = tempString.substring(tempString.indexOf("|")+1);
            positiveInterval = Integer.parseInt(tempString.substring(0,tempString.indexOf("|")));
            Invests[i] = new Invest(investName,minInvest, returnRatio, negativeInterval, positiveInterval);
        }
    }

    private static void initializeTraps() {
        Traps = new Trap[20];
        for (int i = 0; i < 20; i++) {
            Traps[i] = new Trap();
        }
    }

    private static void generateBoard() {
        for (int i = 0; i < playingBoard.length; i++) playingBoard[i] = "";
        playingBoard[0] = "B";
        generateObject(7,'T');
        generateObject(3,'P');
        generateObject(3,'I');
        generateObject(3,'C');
        generateObject(3,'S');
    }

    private static void generateObject(int numberOfObjects, char charOfObject) {
        for (int i = 0; i < numberOfObjects; i++) {
            int posOfObject = randomGenerator.nextInt(20);
            if (posOfObject == 0 || playingBoard[posOfObject] != "") {
                i--;
                continue;
            }
            playingBoard[posOfObject] += charOfObject;
        }
    }

    private static void resetPlayersFlags() {
        for (int i = 0; i < playersCount; i++) {
            Players[i].resetPlayerFlags();
        }
    }

}
