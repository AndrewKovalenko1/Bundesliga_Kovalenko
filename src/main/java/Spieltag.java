import java.util.ArrayList;

public class Spieltag {


    private final int number;

    public ArrayList<Match> getSpiele() {
        return spiele;
    }

    private final ArrayList<Match> spiele;

    public Spieltag(ArrayList<Match> spiele, int number) {
        this.spiele = spiele;
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
