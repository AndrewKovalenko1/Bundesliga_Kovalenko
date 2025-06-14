import java.util.Random;

public class Match {


    private final Team heimteam;
    private final Team auswaertsream;

    public int getToreHeim() {
        return toreHeim;
    }

    public int getToreAuswaerts() {
        return toreAuswaerts;
    }

    private int toreHeim;
    private int toreAuswaerts;

    public Match(Team heimteam, Team auswaertsream) {
        this.heimteam = heimteam;
        this.auswaertsream = auswaertsream;
    }

    public Team getHeimteam() {
        return heimteam;
    }

    public Team getAuswaertsteam() {
        return auswaertsream;
    }

    public void simulate() {
        Random random = new Random();

        int staerkeHeimteam = 100; //starten fom 100, weil Heim eine Stärke ist
        for (Player player : heimteam.getSpielern()){
            staerkeHeimteam += player.getStaerke();
        }
        int staerkeAuswaerts = 0;
        for (Player player : auswaertsream.getSpielern()){
            staerkeAuswaerts += player.getStaerke();
        }

        /*Die Wahrscheinlichkeit eines Unentschiedens.
        Je geringer der Unterschied in der Stärke der Teams ist, desto höher ist die Wahrscheinlichkeit.
        1100 ist die Basis – das ist das maximal mögliche Rating eines Teams.
        Wir teilen es durch 2, um die allgemeine Wahrscheinlichkeit eines Unentschiedens etwas zu verringern.*/
        int staerkeUnentschieden = (1100 - Math.abs(staerkeHeimteam - staerkeAuswaerts)) / 2; //m

        int randomResult = random.nextInt(staerkeHeimteam + staerkeAuswaerts + staerkeUnentschieden);

        int toreSieg = random.nextInt(1,6);
        int toreNiederlage = 10;
        int toreUnterschied = random.nextInt(6); //Unterschied Tore
        while (toreNiederlage >= toreSieg) {
            toreNiederlage = random.nextInt(5);//Niederlage Tore
        }
        if (randomResult < staerkeHeimteam) {
            //Heimteam Sieg
            toreHeim = toreSieg;
            toreAuswaerts = toreNiederlage;
            heimteam.setPunkte(3);
            heimteam.setTore(toreSieg);
            heimteam.setGegentore(toreNiederlage);
            auswaertsream.setTore(toreNiederlage);
            auswaertsream.setGegentore(toreSieg);
        } else if (randomResult < staerkeHeimteam + staerkeAuswaerts) {
            //Auswaertsream Sieg
            toreHeim = toreNiederlage;
            toreAuswaerts = toreSieg;
            auswaertsream.setPunkte(3);
            heimteam.setTore(toreNiederlage);
            heimteam.setGegentore(toreSieg);
            auswaertsream.setTore(toreSieg);
            auswaertsream.setGegentore(toreNiederlage);
        } else {
            //Unterschied
            toreHeim = toreUnterschied;
            toreAuswaerts = toreUnterschied;
            heimteam.setPunkte(1);
            auswaertsream.setPunkte(1);
            heimteam.setTore(toreUnterschied);
            heimteam.setGegentore(toreUnterschied);
            auswaertsream.setTore(toreUnterschied);
            auswaertsream.setGegentore(toreUnterschied);
        }

    }
}
