
public class Fragment {

    private int fragmentSize;
    private int currentlyDownloaded;
    private int quality;
    private boolean isDone;




    public Fragment(int quality){
        currentlyDownloaded = 0;
        isDone = false;
        switch (quality) {
            case 0:
                this.quality = 250;
                break;
            case 1:
                this.quality = 500;
                break;
            case 2:
                this.quality = 850;
                break;
            case 3:
                this.quality = 1300;
                break;
            default:
                break;
        }
        fragmentSize = this.quality * 4;
    }

    public int getFragmentSize() {
        return fragmentSize;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public int getQuality() {
        return  quality;
    }

    public int getCurrentlyDownloaded() {
        return currentlyDownloaded;
    }

    public void setCurrentlyDownloaded(int currentlyDownloaded) {
        this.currentlyDownloaded = currentlyDownloaded;
    }

    public boolean isDone() {
        return currentlyDownloaded >= fragmentSize;
    }

    public void setDone(boolean isDone) {
        this.isDone = isDone;
    }

}
