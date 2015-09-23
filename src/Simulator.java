import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by dennisdufback on 15-09-23.
 */
public class Simulator {
    private int[] bandwidthHistory = new int[565];
    private int[] requestedQuality = new int[565];

    // Method that reads the file and splits up the String.
    // The last two columns will be used in another method to determine
    // the download speed.
    private void ReadFile() throws IOException{

        BufferedReader reader = new BufferedReader(new FileReader(
                "log.log"));
        String line = null;
        int index = 0;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\\s+");
            bandwidthHistory[index] = CalculateBandwidth(Integer
                    .parseInt(parts[4]), Integer.parseInt(parts[5]));
            index++;
        }
        PrintResults(bandwidthHistory);
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
}
