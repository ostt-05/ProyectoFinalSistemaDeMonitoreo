import java.util.Random;

public class SimulationThread extends Thread {
    private final DataListener listener;
    private boolean running = false;
    private final Random random = new Random();

    public SimulationThread(DataListener listener) {
        this.listener = listener;
    }

    public void stopSimulation() {
        running = false;
    }

    @Override
    public void run() {
        running = true;
        while (running) {
            try {

                int x = random.nextInt(101);
                int y = random.nextInt(101);
                int z = random.nextInt(101);


                listener.onDataReceived(x, y, z);


                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            }
        }
    }
}