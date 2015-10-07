
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class Simulator {

    private int[] bandwidthHistory = new int[565];
    private int[] requestedQuality = new int[565];

    private int oldEst = 0;
    private final double alpha = 0.5;
    private boolean currentlyDownloading;
    private int previousQuality = -1;



    //Method that reads the file and splits up the Strings.
    //The last two columns will be used in another method to determine the download speed.
    public void read(String fileName) throws IOException{
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String s;
        int index = 0;
        while((s = br.readLine()) != null) {
            String[] data = s.split(" ");
            bandwidthHistory[index] = CalculateBandwidth(Integer.parseInt(data[4]),Integer.parseInt(data[5]));
            index++;
        }
        br.close();

    }

    private void PrintResults(int [] a) {
        System.out.println(Arrays.toString(a));
    }


    //Method that determines the download speed by looking at the
    //size and the time it took the packet to be recieved.
    private int CalculateBandwidth(int bytes, int time) {
        int newEst = (bytes/time) * 8;

        double bandwidth = (1-alpha) * oldEst + alpha * newEst;

        oldEst = (int)bandwidth;

        return oldEst;

    }

    //Method that sets the quality of the streaming.
    //It makes it only possible to change the streaming one step up and one or two steps down.
//    private void setQuality(VideoPlayer player) {
//        int previousQuality = -1;
//        for (int i = 0; i < bandwidthHistory.length; i++) {
//            if (player.checkQuality(bandwidthHistory[i]) > previousQuality){
//                requestedQuality[i] = previousQuality + 1;
//            }else if (player.checkQuality(bandwidthHistory[i]) < previousQuality - 2) {
//                requestedQuality[i] = previousQuality - 2;
//            } else {
//                requestedQuality[i] = player.checkQuality(bandwidthHistory[i]);
//            }
//            previousQuality = requestedQuality[i];
//        }
//    }

    private Fragment requestNewFragment(int index, VideoPlayer player) {
        if (player.checkQuality(bandwidthHistory[index]) > previousQuality){
            requestedQuality[index] = previousQuality + 1;
        }else if (player.checkQuality(bandwidthHistory[index]) < previousQuality - 2) {
            requestedQuality[index] = previousQuality - 2;
        } else {
            requestedQuality[index] = player.checkQuality(bandwidthHistory[index]);
        }
        previousQuality = requestedQuality[index];
        Fragment fragment = new Fragment(requestedQuality[index]);
        currentlyDownloading = true;
        return fragment;
    }

    //Method that determines if the streaming is going to pause or play by looking at the buffersize.
    //If the buffer is less than 4 the streaming will pause and the next arriving packet will fill the buffer.
    //If the buffer is bigger than 4 but less than 6 the streaming will play and download another packet.
    //However, if the buffer is bigger than 6 the streaming will play but it will NOT download/request another packet.

    private void bufferOperation(VideoPlayer player) throws IOException {
        //buffersize
        BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/Fredrik/Desktop/TDDD66/input1.txt"));
        //current quality
        BufferedWriter writer2 = new BufferedWriter(new FileWriter("/Users/Fredrik/Desktop/TDDD66/input2.txt"));
        //requested quality
        BufferedWriter writer3 = new BufferedWriter(new FileWriter("/Users/Fredrik/Desktop/TDDD66/input3.txt"));

        boolean waitForMinBuf = false;
        int currentBandwidth = 0;
        int currentBuffSize = 0;
        Fragment newFragment = null;
        currentlyDownloading = false;

        for (int i = 0; i < bandwidthHistory.length; i++) {
            currentBandwidth = bandwidthHistory[i];

            player.bufferHistory[i] = currentBuffSize = player.currentBufSize;

            if (currentBuffSize == 0)
                waitForMinBuf = true;

            // Request a new fragment
            if (currentBuffSize < player.maxBuf && !currentlyDownloading) {
                newFragment = requestNewFragment(i, player);
                writer3.write(i + " " + requestedQuality[i] + "\n");
            } else {
                writer3.write(i + " \n" );
            }

            if(currentlyDownloading) {
                newFragment.setCurrentlyDownloaded(newFragment.getCurrentlyDownloaded() + currentBandwidth);
            }

            if(newFragment.isDone()) {
                player.setCurrentBufSize(currentBuffSize + 4);
                currentlyDownloading = false;
                newFragment.setCurrentlyDownloaded(0);
            }

            if(waitForMinBuf && currentBuffSize < player.minBuf) {
//                System.out.println("Pause");
                waitForMinBuf = true;
            } else {
                player.playFrame(); // Minimize buffer by 1
//                System.out.println("Play");
                waitForMinBuf = false;
            }

            requestedQuality[i] = previousQuality;

            // Write down result
            writer.write(i + " " + player.bufferHistory[i] + "\n");
            writer2.write(i + " " + player.checkQuality(newFragment.getQuality()) + "\n");
//            if(i == 0)
//                writer3.write(i + " " + requestedQuality[i] + "\n");
        }

        writer.close();
        writer2.close();
        writer3.close();
    }

    public static void main(String[] args) throws IOException {
        Simulator sim = new Simulator();
        VideoPlayer player = new VideoPlayer();
        sim.read("log.log");
//        sim.setQuality(player);
        sim.bufferOperation(player);
        sim.PrintResults(sim.bandwidthHistory);
        sim.PrintResults(sim.requestedQuality);
        sim.PrintResults(player.bufferHistory);
//        sim.writeResults(player, sim);
    }

}
