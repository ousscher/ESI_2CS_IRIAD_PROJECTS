package ct;

import jade.core.Runtime;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.StaleProxyException;

public class Launcher {
    public static void main(String[] args) {
        try {
            Runtime runtime = Runtime.instance();
            // Main container (RMA GUI) on port 1099
            ProfileImpl mainProfile = new ProfileImpl(null, 1099, null);
            mainProfile.setParameter(ProfileImpl.GUI, "true");
            AgentContainer mainContainer = runtime.createMainContainer(mainProfile);

            // Secondary containers
            AgentContainer container1 = runtime.createAgentContainer(new ProfileImpl(null, 1100, null));
            AgentContainer container2 = runtime.createAgentContainer(new ProfileImpl(null, 1101, null));

            // Launch agents
            mainContainer.createNewAgent(
                "EnvironmentAgent",
                "ct.EnvironmentAgent",
                new Object[]{7, 5}
            ).start();

            container1.createNewAgent(
                "PlayerA",
                "ct.PlayerAgent",
                new Object[]{1, 1, 7, 5, new int[]{2,1}, new String[]{"RED","BLUE"}}
            ).start();

            container2.createNewAgent(
                "PlayerB",
                "ct.PlayerAgent",
                new Object[]{7, 5, 1, 1, new int[]{1,2}, new String[]{"RED","BLUE"}}
            ).start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}

/*
 * Pour compiler et exécuter :
 * javac -cp lib/jade.jar -d classes src/ct/*.java
 * java -cp "lib/jade.jar;classes" ct.MainContainer
 *
 * Dans l'interface JADE (RMA), tu verras EnvironmentAgent, PlayerA, PlayerB.
 * Ouvre un Sniffer pour observer les ACL messages entre agents.
 *
 * Debug IDE :
 * - Crée une configuration Java Application vers ct.MainContainer
 * - Ajoute jade.jar comme librairie
 * - Place des breakpoints dans MoveBehaviour et TransferBehaviour
 * - Lance en mode Debug pour inspecter x,y,tokens, messages reçus.
 * sniffer : sniffer
Class name: jade.tools.sniffer.Sniffer
inspector : introspector
Class name: jade.tools.introspector.Introspector
 */ 