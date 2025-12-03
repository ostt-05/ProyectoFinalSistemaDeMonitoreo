public interface DataListener {
    void onDataReceived(int x, int y, int z);
    void onError(String message);
}
