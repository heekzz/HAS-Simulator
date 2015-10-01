
public class VideoPlayer {

    int currentBufSize = 0;
    int[] bufferHistory = new int[565];

    public int getBufferHistory(int i) {
        return bufferHistory[i];
    }

    public void setBufferHistory(int i, int buf) {
        this.bufferHistory[i] = buf;
    }

    String operation = "Pause";

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public int getCurrentBufSize() {
        return currentBufSize;
    }

    public void setCurrentBufSize(int currentBufSize) {
        this.currentBufSize = currentBufSize;
    }

    public void playFrame() {
        currentBufSize--;
    }

    int maxBuf = 6;
    int minBuf = 4;


    //Method that sets the quality of the next packet that will be streamed
    //by checking the download speed of the previous packet.
    int checkQuality(int bandwidth){
        int currentBandwidth = 0;
        if (bandwidth >= 250 && bandwidth < 500) {
            currentBandwidth = 0;
        }else if (bandwidth >= 500 && bandwidth < 850) {
            currentBandwidth = 1;
        }else if (bandwidth >= 850 && bandwidth < 1300) {
            currentBandwidth = 2;
        }else if (bandwidth >= 1300) {
            currentBandwidth = 3;

        }
        return currentBandwidth;
    }



}
