package com.hxnry.autotyper.ui.timer;

import com.hxnry.autotyper.util.Random;

import java.text.DecimalFormat;

public class HpChecker {

    static double multiplier = 1;

    static double you = 175;
    static double opp = you * multiplier;

    static int youOdds = 46;
    int oppOdds = 56;

    public static void main(String[] args) {

        if(youOdds < 50) {
            multiplier = getMultiplier(youOdds, false);
        }

        opp = you * multiplier;

        DecimalFormat decimalFormat = new DecimalFormat("##.##");
        DecimalFormat betFormat = new DecimalFormat("#.###");

        System.out.println("Note: I hardcoded these odds for learning purposes, but usually you'd have a tool that would calculate these 'hidden odds' for you in the backend. This was done by reverse engineering the combat calculations for osrs. Then you can simulate fake fights between two accounts over millions of generations to calculate your odds of winning against an opp. You then feed your odds through an EV calculator to see at what x value do you get a positive EV score. Then you convince the person to gamble you. The more you get them to stake, the more you're going to win over time, even with less odds of winning! The x is the leverage and over time you will clean the person, even with worse odds per coin flip");
        System.lineSeparator();
        System.out.println("Calculated fair x amount: " + betFormat.format(multiplier));
        System.lineSeparator();
        System.out.println("you Odds: " + "46%");
        System.out.println("opp Odds: " + "54%");
        System.lineSeparator();
        double youEarnings = 0;
        double oppEarnings = 0;
        System.out.println("you Bet: " + you + " with an expected EV of -> " + decimalFormat.format(getEv(youOdds)));
        System.out.println("opp Bet: " + opp + " (youBet * xVal" + (" (" + multiplier + "))"));
        System.out.println("Fair x: " + betFormat.format(multiplier));
        System.lineSeparator();

        int youWins = 0;
        int oppWins = 0;
        int odds = 54;
        int battles = 100000000;
        for(int i = 0; i < battles; i++) {
            if(Random.nextInt(0, 100) < odds) {
                oppWins++;
                oppEarnings += you;
            } else {
                youWins++;
                youEarnings += opp;
            }
        }

        System.out.println("Simulated Number of Battles: " + DecimalFormat.getInstance().format(battles));
        System.out.println("you Wins: " + DecimalFormat.getInstance().format(youWins) + " (roughly matches above 46% winrate)");
        System.out.println("opp Wins: " + DecimalFormat.getInstance().format(oppWins)+ " (roughly matches above 54% winrate)");
        System.lineSeparator();
        System.out.println("you Earnings: " + DecimalFormat.getInstance().format(youEarnings));
        System.out.println("opp Earnings: " + DecimalFormat.getInstance().format(oppEarnings));
    }

    static double edge = 1.174;

    static double getEv(double winChance) {
        double winChanceE = 100 - winChance;
        double desiredEdge = edge / 100D;
        double me = winChance / 100;
        double opp = winChanceE / 100;
        double oppWager = you * multiplier;
        double m  = ((( 1 - desiredEdge ) / (1 - me) - 1 ));
        double x = 1 / m;
        double wager = oppWager / x;
        double ev = (me * oppWager) - (opp * wager);
        return (me * oppWager) - (opp * wager);
    }

    public static double getMultiplier(double odds, boolean include) {
        double theirOdds = 100 - odds;
        double result = theirOdds / odds;
        return (result < 1 ? 1 : result + (include ? .25 : 0));
    }

    public static double getTrueMult(double odds, double over) {
        double theirOdds = 100 - odds;
        double result = theirOdds / odds;
        return (result < 1 ? 1 : result + over);
    }
}
