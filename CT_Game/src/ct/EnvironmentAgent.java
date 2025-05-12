package ct;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class EnvironmentAgent extends Agent {
    private int width, height;

    @Override
    protected void setup() {
        Object[] args = getArguments();
        width = (int) args[0];
        height = (int) args[1];
        System.out.println(getLocalName() + ": Plateau " + width + "x" + height + " initialisé");
        addBehaviour(new MovementValidator());
    }

    private class MovementValidator extends CyclicBehaviour {
        @Override
        public void action() {
            ACLMessage msg = receive();
            if (msg != null && msg.getPerformative() == ACLMessage.REQUEST) {
                // Content: "MOVE:x,y"
                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.INFORM);
                reply.setContent("OK");
                send(reply);
            } else {
                block();
            }
        }
    }
}