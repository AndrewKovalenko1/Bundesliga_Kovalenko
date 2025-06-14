import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class League {
    private final ArrayList<Team> teams;

    private final HashSet<Spieltag> splieltaege;

    public League() {
        this.teams = lesenTeamsAusDatei();
        this.splieltaege = new HashSet<>();
        getSpielern();
    }

    //region Simulate
    public void austragenSpieltag() {
        boolean esGibtSpiel = true;
        int spieltagNummer = splieltaege.size() + 1;
        ArrayList <Match> spiele = new ArrayList<>();
        // Spiele, bei deren Hinzufügung wir nicht 5 pro Spieltag auswählen können
        ArrayList <Match> verbotenSpiele = new ArrayList<>();
        //array mit teams ohne matches für heute
        ArrayList <Team> teamsZuPlanen = new ArrayList<>(teams);

        //10 teams - 5 spiele
        while (esGibtSpiel && spiele.size() <= 5) {
            esGibtSpiel = false;
            for (Team team1 : teamsZuPlanen) {
                boolean warHeim, warAuswaerts;
                for (Team team2 : teamsZuPlanen) {
                    if (team1 == team2) {
                        continue;
                    }
                    // Wir prüfen, ob team1 Gastgeber und team2 Gast war
                    warHeim = istWarHeim(team1, team2, verbotenSpiele);
                    // Wenn team1 bereits Gastgeber im Spiel gegen team2 war,
                    // muss geprüft werden, ob es kein Auswärtsspiel gab
                    if (warHeim) {
                        warAuswaerts = istWarHeim(team2, team1, verbotenSpiele);
                    }  else {
                        // Wenn team1 noch nicht Gastgeber im Spiel gegen team2 war, können wir das Spiel organisieren
                        spiele.add(new Match(team1, team2));
                        esGibtSpiel = true;
                        teamsZuPlanen.remove(team1);
                        teamsZuPlanen.remove(team2);
                        break;
                    }
                    // Dasselbe, aber wenn unser erstes Team Gast ist
                    if (!warAuswaerts) {
                        spiele.add(new Match(team2, team1));
                        esGibtSpiel = true;
                        teamsZuPlanen.remove(team1);
                        teamsZuPlanen.remove(team2);
                        break;
                    }
                }
                // Das Spiel hat stattgefunden, daher beginnen wir wieder am Anfang der Liste der Teams,
                // die noch nicht gespielt haben.
                if (esGibtSpiel) {
                    break;
                }
            }

            // Kompensation der einschränkenden und wiederholenden Auswahl
            // Diese Bedingung wird angewendet, wenn in der Grundauswahl mindestens ein Spiel vorhanden ist
            if (!esGibtSpiel && spiele.size() < 5 && !spiele.isEmpty()) {
                // Wir müssen in der Schleife bleiben
                esGibtSpiel = true;
                // Wir sammeln nach und nach unangenehme Spiele.
                // Prüfen wir, ob es bereits genug sind, um einen ganzen Spieltag zu füllen
                if(verbotenSpiele.size() >= 5 - spiele.size()){
                    ArrayList <Match> zusaetzlicheSpiele = new ArrayList<>();
                    for (Match spiel : verbotenSpiele) {
                        // Wir prüfen, dass es in den ausgewählten noch kein solches Spiel gibt
                        boolean isNewMatch = spiele.stream()
                                .anyMatch(n->n.getHeimteam()== spiel.getHeimteam() && n.getAuswaertsteam() == spiel.getAuswaertsteam());
                        if(!isNewMatch) {
                            // Zuerst fügen wir in die Zwischenauswahl hinzu, da möglicherweise
                            // nicht genügend passende Spiele für die Spieltagbildung vorhanden sind
                            // und wir noch ergänzen müssen
                            zusaetzlicheSpiele.add(spiel);
                            // Wir prüfen, ob wir bereits genügend zusätzliche Spiele für die Spieltagbildung haben
                            // Dann bilden wir den Spieltag und steigen aus allen Schleifen aus
                            if (zusaetzlicheSpiele.size() == 5 - spiele.size()){
                                spiele.addAll(zusaetzlicheSpiele);
                                break;
                            }
                        }
                    }
                }
                if (spiele.size() < 5) {
                    Match lastMatch = spiele.getLast();
                    teamsZuPlanen.add(lastMatch.getHeimteam());
                    teamsZuPlanen.add(lastMatch.getAuswaertsteam());
                    spiele.remove(lastMatch);
                    verbotenSpiele.add(lastMatch);
                }
            }
        }
        // Wenn mindestens ein Spiel stattgefunden hat, hat der Spieltag stattgefunden
        if(!spiele.isEmpty()) {
            for (Match match : spiele) {
                match.simulate();
            }
            Spieltag spieltag = new Spieltag(spiele, spieltagNummer);
            this.splieltaege.add(spieltag);
            malenSpieltagTabelle(spieltag);
        } else {
            System.out.println("Es gibt keine Spielen mehr");
        }
    }

    private boolean istWarHeim(Team team1, Team team2, ArrayList<Match> verbotenMatches) {
        boolean warHeim = false;
        for (Match match : verbotenMatches) {
            if(team1 == match.getHeimteam() && team2 == match.getAuswaertsteam()){
                return true;
            }
        }
        for (Spieltag spieltag : splieltaege){
             warHeim = spieltag.getSpiele().stream()
                    .anyMatch(n->n.getHeimteam()== team1 && n.getAuswaertsteam() == team2);
            if (warHeim) {
                // Wenn ja, prüfen wir nicht weiter
                break;
            }
        }
        return warHeim;
    }
    //endregion

    //region Transfer
    public void transfer() {
        Scanner scanner = new Scanner(System.in);
        Player player1 = null,player2 = null;
        Team team1 = null, team2 = null;
        System.out.print("Name des ersten Spielers eingeben: ");
        String name1 = scanner.nextLine();
        for (Team team : teams) {
            Optional<Player>  optSpieler = team.getSpielern().stream().filter(n->n.getName().equals(name1)).findAny();
            if (optSpieler.isPresent()) {
                player1 = optSpieler.get();
                team1 = team;
                break;
            }
        }
        if (player1 == null) {
            System.out.println("Der erste Spieler existiert nicht. Bitte geben Sie seinen Namen korrekt ein.");
            return;
        }

        System.out.print("Name des zweiten Spielers eingeben: ");
        String name2 = scanner.nextLine();
        for (Team team : teams) {
            Optional<Player>  optSpieler = team.getSpielern().stream().filter(n->n.getName().equals(name2)).findAny();
            if (optSpieler.isPresent()) {
                player2 = optSpieler.get();
                team2 = team;
                break;
            }
        }
        if (player2 == null) {
            System.out.println("Der zweite Spieler existiert nicht. Bitte geben Sie seinen Namen korrekt ein.");
            return;
        }

        if (team1 == team2) {
            System.out.println("Ihre Spieler sind im selben Team (" + team1.getName() + "). Sie können sie nicht transferieren ");
            return;
        }

        boolean istFortgesetzt = true;
        System.out.printf("Möchten Sie %s von %s zu %s von %s transferieren? j/n: ",
                name1, team1.getName(), name2, team2.getName());
        do {

            String wahl = scanner.nextLine();
            switch (wahl) {
                case "j":
                    team1.setSpieler(player2);
                    team2.setSpieler(player1);
                    team1.entfernenSpieler(player1);
                    team2.entfernenSpieler(player2);
                    System.out.println("Transfer ist fertig!");
                    break;
                case "n":
                   System.out.println("Ok, keine Transfer");
                    istFortgesetzt = false;
                    break;
               default:
                   System.out.println("Geben Sie nur 'j' für 'ja' oder 'n' für 'nein' ein");
                   break;
            }
        } while (istFortgesetzt);
        scanner.close();


    }
    //endregion

    //region Show tables

    public void anzeigenSpiele() {
        Scanner scanner = new Scanner(System.in);
        String wahl;
        boolean istFortgesetzt = true;
        String menue = """                    
                    Wählen Sie die Option zum Drucken der Spiele:
                       1 - Einen Spieltag drucken
                       2 - Alle Spiele drucken
                       Jede andere Eingabe - Drucken beenden
                \s""";
        System.out.print(menue);
        do {
            System.out.print("\nIhre Wahl: ");
            wahl = scanner.nextLine();
            switch (wahl) {
                case "1":
                    System.out.print("\nSpieltag-Nummer eingeben: ");
                    if (scanner.hasNextInt()) {
                        int numberSpieltag = scanner.nextInt();
                        Optional<Spieltag> optMatch = splieltaege.stream().filter(n->n.getNumber()==numberSpieltag).findAny();
                        if(optMatch.isPresent()) {
                            Spieltag spieltag = optMatch.get();
                            malenSpieltagTabelle(spieltag);
                        }
                        else {
                            System.out.println("Wir haben keinen Spieltag mit der Nummer " + numberSpieltag + " gefunden");
                        }
                    }
                    else{
                        System.out.println("\nSie haben einen falschen Wert eingegeben. Vorgang beendet.");
                    }
                    break;
                case "2":
                    for (Spieltag spieltag : splieltaege) {
                        System.out.println();
                        malenSpieltagTabelle(spieltag);
                    }
                    break;
                default:
                    istFortgesetzt = false;
                    scanner.close();
                    break;
            }
        } while (istFortgesetzt);
    }

    public void malenSpieltagTabelle(Spieltag spieltag) {
        StringBuilder tabelle = new StringBuilder();
        tabelle.append("Spieltag ").append(spieltag.getNumber()).append('\n');
        for (Match match : spieltag.getSpiele()) {
            tabelle.append("\n -").append(match.getHeimteam().getName())
                    .append(' ').append(match.getToreHeim()).append(':').append(match.getToreAuswaerts())
                    .append(' ').append(match.getAuswaertsteam().getName());
        }
        System.out.println(tabelle);
    }

    public void anzeigenSpielern() {
        Scanner scanner = new Scanner(System.in);
        String wahl;
        boolean istFortgesetzt = true;
        String menue = """                    
                    Wählen Sie die Option zum Drucken der Spieler:
                       1 - Spieler eines Teams drucken
                       2 - Alle Spieler drucken
                       Jede andere Eingabe - Drucken beenden
                \s""";
        System.out.print(menue);
        do {
            System.out.print("\nIhre Wahl: ");
            wahl = scanner.nextLine();
            switch (wahl) {
                case "1":
                    System.out.print("\nTeamnamen eingeben: ");
                    String teamName = scanner.nextLine();
                    Optional<Team> optTeam = teams.stream().filter(n->n.getName().equals(teamName)).findAny();
                    if(optTeam.isPresent()) {
                        Team team = optTeam.get();
                        malenSpeilernTabelle(team);
                    }
                    else {
                        System.out.println("WWir haben kein Team mit dem Namen gefunden " + teamName);
                    }
                    break;
                case "2":
                    for (Team team : teams) {
                        System.out.println();
                        malenSpeilernTabelle(team);
                    }
                    break;
                default:
                    istFortgesetzt = false;
                    scanner.close();
                    break;
            }
        } while (istFortgesetzt);
    }

    public void malenSpeilernTabelle(Team team) {
        StringBuilder table = new StringBuilder();
        table.append("Team ").append(team.getName()).append('\n');
        for (Player player : team.getSpielern()) {
            table.append("\n -").append(player.getName())
                    .append('\t').append(player.getPosition().zeigeName()).append('\t').append(player.getStaerke());
        }
        System.out.println(table);
    }

    public void anzeigenMannschaftstabelle(){
        teams.sort(Comparator.comparing(Team::getPunkte).reversed());

        StringBuilder table = new StringBuilder();

        //erstellen die Tabelle
        int platz = 0;
        for(Team team: teams) {
            ++platz;
            table.append("\n ").append(platz).append('\t').append(team.getName());
            table.append('\t').append(team.getPunkte()).append(" Pkt");
            table.append('\t').append(team.getTore()).append(':').append(team.getGegentore());
        }
        System.out.println(table);
    }

    //endregion

    //region Arbeit mit Dateien
    private ArrayList<Team> lesenTeamsAusDatei() {
        ArrayList<Team> teams = new ArrayList<>();
        int punkte, tore, gegentore;
        String name;
        try {
            ObjectMapper mapper = new ObjectMapper();
            File fileTeams = new File("src/main/resources/Teams.json");
            JsonNode datenTeams = mapper.readTree(fileTeams);
            for (JsonNode jsonTeam : datenTeams) {
                name = jsonTeam.get("name").asText();
                punkte = jsonTeam.get("punkte").asInt();
                tore = jsonTeam.get("tore").asInt();
                gegentore = jsonTeam.get("gegentore").asInt();
                teams.add(new Team(name, punkte, tore, gegentore));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
       return teams;
    }

    public void schreibenTeamsInDatei(boolean istNeueTabelle) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.createArrayNode();

        for (Team team : this.teams) {
            ObjectNode teamNode = mapper.createObjectNode();
            teamNode.put("name", team.getName());
            teamNode.put("punkte", istNeueTabelle ? 0 : team.getPunkte());
            teamNode.put("tore",  istNeueTabelle ? 0 : team.getTore());
            teamNode.put("gegentore",  istNeueTabelle ? 0 : team.getGegentore());
            arrayNode.add(teamNode);
        }
        File fileTeams = new File("src/main/resources/Teams.json");

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(fileTeams, arrayNode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void getSpielern() {
        String name, positionText;
        int staerke;

        try {
            ObjectMapper mapper = new ObjectMapper();
            File fileSpielern = new File("src/main/resources/Spielern.json");
            JsonNode datenSpielernTeams = mapper.readTree(fileSpielern);
            for (JsonNode jsonSpieler : datenSpielernTeams) {
                name = jsonSpieler.get("name").asText();
                staerke = jsonSpieler.get("staerke").asInt();
                positionText = jsonSpieler.get("position").asText();
                Positions position = Positions.valueOf(positionText.toUpperCase());
                String teamName = jsonSpieler.get("team").asText();
                Player spieler = new Player(name, staerke, position);
                Optional<Team> optionalTeam = teams.stream().filter(n->n.getName().equals(teamName)).findAny();
                optionalTeam.ifPresent(team -> team.setSpieler(spieler));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //endregion
}
