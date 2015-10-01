
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
    private void setQuality(VideoPlayer player) {
        int previousQuality = -1;
        for (int i = 0; i < bandwidthHistory.length; i++) {
            if (player.checkQuality(bandwidthHistory[i]) > previousQuality){
                requestedQuality[i] = previousQuality + 1;
            }else if (player.checkQuality(bandwidthHistory[i]) < previousQuality - 2) {
                requestedQuality[i] = previousQuality - 2;
            } else {
                requestedQuality[i] = player.checkQuality(bandwidthHistory[i]);
            }
            previousQuality = requestedQuality[i];
        }
    }

    //Method that determines if the streaming is going to pause or play by looking at the buffersize.
    //If the buffer is less than 4 the streaming will pause and the next arriving packet will fill the buffer.
    //If the buffer is bigger than 4 but less than 6 the streaming will play and download another packet.
    //However, if the buffer is bigger than 6 the streaming will play but it will NOT download/request another packet.

    private void bufferOperation(VideoPlayer player){

        boolean waitForMinBuf = false;
        int currentBandwidth = 0;
//        int currentFragmentSize = 0;
//        int fragmentDownloaded = 0;
        int currentBuffSize = 0;
        Fragment newFragment = null;
        boolean currentlyDownloading = false;

        for (int i = 0; i < bandwidthHistory.length; i++) {
            currentBandwidth = bandwidthHistory[i];

            player.bufferHistory[i] = currentBuffSize = player.currentBufSize;

            if (currentBuffSize == 0)
                waitForMinBuf = true;

            // Request a new fragment
            if (currentBuffSize < player.maxBuf && !currentlyDownloading) {
                newFragment = new Fragment(requestedQuality[i]);
                currentlyDownloading = true;
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

//            System.out.println("Current Buffer: " +  currentBuffSize);
//            System.out.println("Current requested quality: " + requestedQuality[i]);

//            player.bufferHistory[i] = player.currentBufSize;
//            int currentSize = player.getCurrentBufSize();
//            System.out.println("Current buffer size = " + player.currentBufSize);
//
//            if(currentSize > 6)
//                waitForMinBuf = true;
//
//            if(currentlyDownloading == false && waitForMinBuf == false){
//                newfragment = new Fragment(requestedQuality[i],4);
//                currentlyDownloading = true;
//            }
//            if(newfragment.getCurrentlyDownloaded() >= newfragment.getFragmentSize()){
//                player.setCurrentBufSize(currentSize +4);
//                currentlyDownloading = false;
//            }
//            if(currentlyDownloading){
//                newfragment.setCurrentlyDownloaded(newfragment.getCurrentlyDownloaded() + currentBandwidth);
//
//            }
//            System.out.println("Current bandwidth: " + currentBandwidth);
//            System.out.println("Currently Downloaded: " + newfragment.getCurrentlyDownloaded());
//            //System.out.println("Fragment size: "+newfragment.getFragmentSize());
//
//
//            if(player.currentBufSize != 0)
//                player.setCurrentBufSize(player.currentBufSize - 1);
//

        }
    }

    private void writeResults(VideoPlayer player, Simulator sim){
        try {
            //buffersize
            BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/Fredrik/Desktop/TDDD66/input1.txt"));
            //current quality
            BufferedWriter writer2 = new BufferedWriter(new FileWriter("/Users/Fredrik/Desktop/TDDD66/input2.txt"));
            //requested quality
            BufferedWriter writer3 = new BufferedWriter(new FileWriter("/Users/Fredrik/Desktop/TDDD66/input3.txt"));
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

    public static void main(String[] args) throws IOException {
        Simulator sim = new Simulator();
        VideoPlayer player = new VideoPlayer();
        sim.read("log.log");
        sim.setQuality(player);
        sim.PrintResults(sim.requestedQuality);
        sim.bufferOperation(player);
        sim.PrintResults(player.bufferHistory);
        sim.writeResults(player, sim);
    }

}
