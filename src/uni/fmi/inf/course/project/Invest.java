package uni.fmi.inf.course.project;

import java.util.HashMap;

/**
 *
 * @author Nedko Gospodinov
 *
 */

public class Invest {
    private String investName;
    private int minValueForInvest;
    private double returnRatio;
    private int negativeInterval;
    private int positiveInterval;
    public HashMap<Player, Integer> investByPlayer = new HashMap<>();

    public Invest(String Name, int minValue, double ratioReturn, int Negative, int Positive) {
        investName = Name;
        minValueForInvest = minValue;
        returnRatio = ratioReturn;
        negativeInterval = Negative;
        positiveInterval = Positive;
    }

    public String getInvestName() { return investName; }

    public int getMinValueForInvest() { return minValueForInvest; }

    public double getReturnRatio() { return returnRatio; }

    public int getNegativeInterval() { return negativeInterval; }

    public int getPositiveInterval() { return positiveInterval; }

}
