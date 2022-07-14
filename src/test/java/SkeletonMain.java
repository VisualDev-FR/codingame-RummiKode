import com.codingame.gameengine.runner.MultiplayerGameRunner;

public class SkeletonMain {
    public static void main(String[] args) {

        MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();

        gameRunner.addAgent(Agent1.class);
        gameRunner.addAgent(Agent2.class);

        //gameRunner.setSeed(6335831626512785165L);

        gameRunner.start();
    }
}
