package agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.*;

public class PlayerAgent extends Agent {
    private List<String> tokens;
    private int[] currentPosition;
    private int[] goalPosition;

    @Override
    protected void setup() {
        System.out.println(getLocalName() + " started.");

        // Init
        tokens = new ArrayList<>(List.of("Red", "Blue")); // Example set
        if (getLocalName().equals("player1"))
            currentPosition = new int[]{0, 0};
        else
            currentPosition = new int[]{6, 4};
        goalPosition = new int[]{3, 2}; // Example goal

        addBehaviour(new ReceiveBehaviour());
    }

    private class ReceiveBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            ACLMessage msg = receive();
            if (msg != null) {
                System.out.println(getLocalName() + " received: " + msg.getContent());
                // You can handle messages here: proposals, transfers, etc.
            } else {
                block();
            }
        }
    }
}
