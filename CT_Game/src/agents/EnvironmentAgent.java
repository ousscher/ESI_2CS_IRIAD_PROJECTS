package agents;

import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.util.*;

public class EnvironmentAgent extends Agent {
    private final int ROWS = 7;
    private final int COLS = 5;
    private String[][] grid = new String[ROWS][COLS];
    private Map<String, int[]> agentPositions = new HashMap<>();

    @Override
    protected void setup() {
        System.out.println("EnvironmentAgent started.");
        initBoard();
        placeAgents();
    }

    private void initBoard() {
        String[] colors = {"Red", "Green", "Blue"};
        Random rand = new Random();

        for (int i = 0; i < ROWS; i++)
            for (int j = 0; j < COLS; j++)
                grid[i][j] = colors[rand.nextInt(colors.length)];
    }

    private void placeAgents() {
        agentPositions.put("player1", new int[]{0, 0});
        agentPositions.put("player2", new int[]{6, 4});
        // You can notify agents of their starting positions if needed
    }

    // Later: add behaviors to validate movements, track progress, detect end
}
