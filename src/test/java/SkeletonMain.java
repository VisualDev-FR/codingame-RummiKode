import com.codingame.gameengine.runner.MultiplayerGameRunner;

public class SkeletonMain {
    public static void main(String[] args) {

        MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();

        gameRunner.addAgent(Solution.class);
        gameRunner.addAgent(Solution.class);

        //gameRunner.setSeed(6335831626512785165L);

        gameRunner.start();
    }
}
