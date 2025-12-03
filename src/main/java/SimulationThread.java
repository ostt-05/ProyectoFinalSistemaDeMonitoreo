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
                // Simular datos 0-100 como en tu código Arduino
                int x = random.nextInt(101);
                int y = random.nextInt(101);
                int z = random.nextInt(101);

                // Enviar a la UI
                listener.onDataReceived(x, y, z);

                // Esperar 1 segundo como el Arduino
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            }
        }
    }
}