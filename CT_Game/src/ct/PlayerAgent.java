package ct;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class PlayerAgent extends Agent {
    private int x, y, goalX, goalY;
    private int[] tokens;
    private String[] colors;
    private int blockedTurns = 0;

    @Override
    protected void setup() {
        Object[] args = getArguments();
        x       = (int) args[0];
        y       = (int) args[1];
        goalX   = (int) args[2];
        goalY   = (int) args[3];
        tokens  = (int[]) args[4];
        colors  = (String[]) args[5];
        System.out.println(getLocalName() + ": départ ("+x+","+y+") -> but ("+goalX+","+goalY+")");

        FSMBehaviour fsm = new FSMBehaviour(this) {
            @Override
            public int onEnd() {
                myAgent.doDelete();
                return super.onEnd();
            }
        };
        fsm.registerFirstState(new MoveBehaviour(), "MOVE");
        fsm.registerState(new NegotiateBehaviour(), "NEGOTIATE");
        fsm.registerState(new TransferBehaviour(), "TRANSFER");
        fsm.registerLastState(new CheckEndBehaviour(), "CHECK_END");

        fsm.registerDefaultTransition("MOVE", "NEGOTIATE");
        fsm.registerDefaultTransition("NEGOTIATE", "TRANSFER");
        fsm.registerDefaultTransition("TRANSFER", "CHECK_END");
        fsm.registerDefaultTransition("CHECK_END", "MOVE");

        addBehaviour(fsm);
    }

    private class MoveBehaviour extends OneShotBehaviour {
        @Override
        public void action() {
            int dx = (goalX > x) ? 1 : (goalX < x) ? -1 : 0;
            int dy = (dx != 0 ? 0 : (goalY > y) ? 1 : (goalY < y) ? -1 : 0);
            int colorIdx = (dx != 0 ? 0 : 1);
            if (tokens[colorIdx] > 0) {
                ACLMessage req = new ACLMessage(ACLMessage.REQUEST);
                req.addReceiver(new AID("EnvironmentAgent", AID.ISLOCALNAME));
                req.setContent("MOVE:" + (x+dx) + "," + (y+dy));
                send(req);
                ACLMessage resp = blockingReceive();
                if ("OK".equals(resp.getContent())) {
                    x += dx; y += dy;
                    tokens[colorIdx]--;
                    System.out.println(getLocalName()+" moved to ("+x+","+y+") using 1 "+colors[colorIdx]);
                    blockedTurns = 0;
                }
            } else {
                blockedTurns++;
                System.out.println(getLocalName()+" blocked: no "+colors[colorIdx]);
            }
        }
    }

    private class NegotiateBehaviour extends OneShotBehaviour {
        @Override
        public void action() {
            if (tokens[0] < 1) {
                ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                cfp.addReceiver(new AID("PlayerB", AID.ISLOCALNAME));
                cfp.setContent("OFFER:give BLUE,request RED");
                send(cfp);
                System.out.println(getLocalName()+" sent CFP");
            }
        }
    }

    private class TransferBehaviour extends OneShotBehaviour {
        @Override
        public void action() {
            ACLMessage msg = blockingReceive();
            if (msg != null && msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                tokens[0]++;
                tokens[1]--;
                System.out.println(getLocalName()+" completed transfer: +1 RED, -1 BLUE");
            }
        }
    }

    private class CheckEndBehaviour extends OneShotBehaviour {
        @Override
        public void action() {
            if ((x==goalX && y==goalY) || blockedTurns >= 3) {
                System.out.println(getLocalName()+" termine la partie.");
                myAgent.doDelete();
            }
        }
    }
}