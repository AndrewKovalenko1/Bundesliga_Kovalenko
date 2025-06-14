import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashSet;

public class Team {


    private final String name;
    private int punkte;
    private int tore;
    private int gegentore;

    @JsonIgnore
    private final HashSet<Player> spielern;


    public Team(String name, int punkte, int tore, int gegentore) {
        this.name = name;
        this.punkte = punkte;
        this.tore = tore;
        this.gegentore = gegentore;
        this.spielern = new HashSet<>();
    }

    public HashSet<Player> getSpielern() {
        return spielern;
    }

    public void setSpieler(Player spieler) {
        this.spielern.add(spieler);
    }

    public void removeSpieler(Player spieler) {
        this.spielern.remove(spieler);
    }

    public int getPunkte() {
        return punkte;
    }

    public void setPunkte(int punkte) {
        this.punkte += punkte;
    }

    public int getTore() {
        return tore;
    }

    public void setTore(int tore) {
        this.tore += tore;
    }

    public int getGegentore() {
        return gegentore;
    }

    public void setGegentore(int gegentore) {
        this.gegentore += gegentore;
    }

    public String getName() {
        return name;
    }
}
