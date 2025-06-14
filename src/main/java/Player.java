public class Player {


    private final String name;
    private final int staerke;


    private final Positions position;

    public Player(String name, int staerke, Positions position) {
        this.name = name;
        this.staerke = staerke;
        this.position = position;
    }

    public int getStaerke() {
        return staerke;
    }

    public String getName() {
        return name;
    }

    public Positions getPosition() {
        return position;
    }


}
