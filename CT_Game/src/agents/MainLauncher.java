package agents;

public class MainLauncher {
    public static void main(String[] args) {
        jade.Boot.main(new String[]{
            "-gui",
            "-agents", "env:agents.EnvironmentAgent;player1:agents.PlayerAgent;player2:agents.PlayerAgent"
        });
    }
}
