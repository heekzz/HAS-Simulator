/**
 * Created by dennisdufback on 15-09-23.
 */
public class fragment {
    private int fragmentsSize;

        public int getFragmentSize(){
            return fragmentsSize;
        }
    public void setFragmentsSize(int fragmentsSize){
        this.fragmentsSize = fragmentsSize;
    }
    private int currentlyDownloaded;
    private int quality;
    public int getQuality(){
        return quality;
    }
    public void setQuality(int quality){
        this.quality = quality;
    }
    private boolean isDone;

    public fragment(int quality, int size){
        fragmentsSize = quality * size;
        currentlyDownloaded = 0;
        isDone = false;
        switch (quality){
            case 0:
                quality = 250;
                break;
            case 1:
                quality = 500;
                break;
            case 2:
                quality = 850;
                break;
            case 3:
                quality = 1300;
                break;
            default:
                break;
        }
    }

    public int getCurrentlyDownloaded(){
        return currentlyDownloaded;
    }
    public void setCurrentlyDownloaded(int currentlyDownloaded){
        this.currentlyDownloaded = currentlyDownloaded;
    }
    public boolean isDone(){
        return isDone;
    }
    public void setDone(boolean isDone){
        this.isDone = isDone;
    }
}
