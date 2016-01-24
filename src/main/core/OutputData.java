package main.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;

public class OutputData {

    final static String newLine = System.getProperty("line.separator");

    HashMap<String, Performance> players;
    ArrayList<Goal> redGoals, blueGoals;

    public OutputData() {
        players = new HashMap<String, Performance>();
        redGoals = new ArrayList<Goal>();
        blueGoals = new ArrayList<Goal>();
    }

    public void incrementTimeOnIce(ArrayList<GamePlayerStruct> gamePlayers) {
        for (GamePlayerStruct gps : gamePlayers) {
            if (gps.isPlaying()) {
                if (players.containsKey(gps.name)) {
                    players.get(gps.name).toi++;
                } else {
                    players.put(gps.name, new Performance(gps.team));
                }
            }
        }
    }

    public void updatePlusMinus(int teamScored, GamePlayerStruct player) {
        if (player.isPlaying()) {
            if (player.team == teamScored) {
                players.get(player.name).plusminus++;
            } else {
                players.get(player.name).plusminus--;
            }
        }
    }

    public void addGoalsToPerformances() {
        addGoalsToPerformances(redGoals);
        addGoalsToPerformances(blueGoals);
    }

    public void addGoalsToPerformances(ArrayList<Goal> goals) {
        for (Goal g : goals) {
            if (!g.scorer.equals("")) {
                players.get(g.scorer).goals++;
            }
            if (!g.assister.equals("")) {
                players.get(g.assister).assists++;
            }
        }
    }

    public ArrayList<Goal> goals() {
        ArrayList<Goal> goals = new ArrayList<Goal>();
        goals.addAll(redGoals);
        goals.addAll(blueGoals);
        return goals;
    }

    public void addGoal(int team, int time, int period, ArrayList<GamePlayerStruct> gamePlayers, ArrayList<GamePlayerStruct> previousGamePlayers) {
        String scorer = "", assister = "";

        // Find the name of the scorer and the assister, update +/-
        for (GamePlayerStruct currentPlayer : gamePlayers) {

            updatePlusMinus(team, currentPlayer);

            for (GamePlayerStruct previousPlayer : previousGamePlayers) {
                if (currentPlayer.name.equals(previousPlayer.name)) {

                    if (currentPlayer.goals != previousPlayer.goals) {
                        scorer = currentPlayer.name;
                    }

                    if (currentPlayer.assists != previousPlayer.assists) {
                        assister = currentPlayer.name;
                    }
                }
            }
        }
        if (team == 0) {
            redGoals.add(new Goal(scorer, assister, time, period, 0));
        } else {
            blueGoals.add(new Goal(scorer, assister, time, period, 1));
        }
    }

    public void writeGoalsToFile(File file, ArrayList<Goal> goals) throws IOException {
        for (Goal g : goals) {
            FileUtils.writeStringToFile(file, g.toString() + newLine, true);
        }
    }

    public void writePerformancesToFile(File file) throws IOException {
        for (Entry<String, Performance> e : players.entrySet()) {
            FileUtils.writeStringToFile(file, (e.getKey() + ": " + e.getValue().toString() + newLine), true);
        }
    }

    public void outputToFile() {
        Date now = new Date();

        File goalsFile = new File(now.getTime() + "-goals.txt"); 
        File performancesFile = new File(now.getTime() + "-performances.txt"); 

        String gameScore = "RED: " + redGoals.size() + " BLUE: " + blueGoals.size();

        try {
            FileUtils.writeStringToFile(performancesFile, gameScore + newLine, true);
            FileUtils.writeStringToFile(goalsFile, gameScore + newLine, true);

            writeGoalsToFile(goalsFile, redGoals);
            writeGoalsToFile(goalsFile, blueGoals);

            writePerformancesToFile(performancesFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}