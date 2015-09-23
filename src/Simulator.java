import java.io.*;
import java.util.Arrays;

/**
 * Created by dennisdufback on 15-09-23.
 */
public class Simulator {
    private int[] bandwidthHistory = new int[565];
    private int[] requestedQuality = new int[565];


    public static void main (String args[]) throws IOException {
        Simulator sim = new Simulator();
        VideoPlayer player = new VideoPlayer();
        sim.read("log.log");
        sim.setQuality(player);
        sim.PrintResults(sim.requestedQuality);
        sim.bufferOperation(player);
        sim.PrintResults(player.bufferHistory);
    }

    public void read(String fileName) throws IOException{
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String s;
        int index = 0;
        while((s = br.readLine()) != null) {
            String[] data = s.split(" ");
            bandwidthHistory[index] = (Integer.parseInt(data[4]) * 8) / Integer.parseInt(data[4]);
        }
        br.close();
    }

    private void writeResults(VideoPlayer player,Simulator sim){
        try {
            //buffersize
            BufferedWriter writer = new BufferedWriter(new FileWriter("/home/hughe531/TDDD66/input1.txt"));
            //current quality
            BufferedWriter writer2 = new BufferedWriter(new FileWriter("/home/hughe531/TDDD66/input2.txt"));
            //requested quality
            BufferedWriter writer3 = new BufferedWriter(new FileWriter("/home/hughe531/TDDD66/input3.txt"));
            for (int i = 0; i < player.bufferHistory.length; i++) {
                writer.write(i + " " + player.bufferHistory[i] + "\n");
                if (i == 0) {
                    writer2.write(i + " " + 0 + "\n");
                }else
                    writer2.write(i + " " +  sim.requestedQuality[i-1] +  "\n");
                writer3.write(i + " " +sim.requestedQuality[i] + "\n");
            }
            writer.close();
            writer2.close();
            writer3.close();


        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private void PrintResults(int[] a){
        System.out.println(Arrays.toString(a));
    }

    // Determines the download speed by looking at the size and
    // the time it took the packed to be received.
    private int CalculateBandwidth(int bytes, int time){
     int bandwidth = 0;
        bandwidth = (bytes / time) * 8;
        return bandwidth;
    }
    // Method that sets the quality of the streaming.
    // It makes it only possible to change the streaming one step up and
    // one or two steps down.
    private void setQuality(VideoPlayer player){
        int previousQuality = -1;
        for(int i = 0; i < bandwidthHistory.length; i++){
            if(player.checkQuality(bandwidthHistory[i]) > previousQuality){
                requestedQuality[i] = previousQuality + 1;
            } else if(player.checkQuality(bandwidthHistory[i]) < previousQuality -2){
                requestedQuality[i] = previousQuality - 2;
            } else {
                requestedQuality[i] = player.checkQuality(bandwidthHistory[i]);
            }
            previousQuality = requestedQuality[i];
        }
    }
    private void bufferOperation(VideoPlayer player) {
        boolean currentlyDownloading = false;
        boolean waitForMinBuf = false;
        int currentBandwidth = 0;
        int currentFragmentSize = 0;
        fragment newfragment = null;

        for (int i = 0; i < requestedQuality.length; i++) {
            currentBandwidth = bandwidthHistory[i];
            player.bufferHistory[i] = player.currentBufferSize;
            int currentSize = player.getCurrentBufSize();
            System.out.println("Current buffer size = " + player.currentBufferSize);

            if (currentSize > 6) {
                waitForMinBuf = true;
            }
            if (currentlyDownloading == false && waitForMinBuf == false) {
                newfragment = new fragment(requestedQuality[i], 4);
                currentlyDownloading = true;
            }
            if (newfragment.getCurrentlyDownloaded() >= newfragment.getFragmentSize()) {
                player.setCurrentBufSize(currentSize + 4);
                currentlyDownloading = false;
            }
            if (currentlyDownloading) {
                newfragment.setCurrentlyDownloaded(currentBandwidth / newfragment.getQuality());
            }

            player.setCurrentBufSize(player.currentBufferSize - 1);

        }
    }

}
