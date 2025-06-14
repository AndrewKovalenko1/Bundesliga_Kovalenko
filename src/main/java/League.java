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
        this.teams = readTeamsFromFile();
        this.splieltaege = new HashSet<>();
        getSpielern();
    }

    //region Simulate
    public void playMatchday() {
        boolean esGibtMatch = true;
        int spieltagNumber = splieltaege.size() + 1;
        ArrayList <Match> matches = new ArrayList<>();
        //матчі, які при додаванні яких ми не можемо вибрати 5 за один тур
        ArrayList <Match> verbotenMatches = new ArrayList<>();
        //array mit teams ohne matches für heute
        ArrayList <Team> teamsToPlan = new ArrayList<>(teams);

        //10 teams - 5 matches
        while (esGibtMatch && matches.size() <= 5) {
            esGibtMatch = false;
            for (Team team1 : teamsToPlan) {
                boolean warHeim, warAuswaerts;
                for (Team team2 : teamsToPlan) {
                    if (team1 == team2) {
                        continue;
                    }
                    //перевіряємо чи була team1 господарем а team2 гостями
                    warHeim = isWarHeim(team1, team2, verbotenMatches);
                    // якщо team1 вже була господарем в матчі з team2, то треба перевірити, чи не було ще гостьового матчу
                    if (warHeim) {
                        warAuswaerts = isWarHeim(team2, team1, verbotenMatches);
                    }  else {
                        // якщо team1 ще не була господарем в матчі з team2, значить можемо організовувати матч
                        matches.add(new Match(team1, team2));
                        //esGibtMatch = simulateMatch(team1, team2, matches, teamsToPlan);
                        esGibtMatch = true;
                        teamsToPlan.remove(team1);
                        teamsToPlan.remove(team2);
                        break;
                    }
                    //те ж саме, але якщо наша перша команда є гостьова
                    if (!warAuswaerts) {
                        matches.add(new Match(team2, team1));
                        //esGibtMatch = simulateMatch(team2, team1, matches, teamsToPlan);
                        esGibtMatch = true;
                        teamsToPlan.remove(team1);
                        teamsToPlan.remove(team2);
                        break;
                    }
                }
                //матч відбувся, то почнемо з початку списку команд, що ще не грали.
                if (esGibtMatch) {
                    break;
                }
            }

            //компенсація звужувального та повторювального вибору
            // застосовуємо цю умову, якщо базово по виборці маємо хоч один матч
            if (!esGibtMatch && matches.size() < 5 && !matches.isEmpty()) {
                //ми маємо залишитись в циклі
                esGibtMatch = true;
                //ми поступово накопичуємо незручні матчі. Перевіримо, можливо їх вже стільки що нам вистачить на цілий тур
                if(verbotenMatches.size() >= 5 - matches.size()){
                    ArrayList <Match> additionalMatches = new ArrayList<>();
                    for (Match match : verbotenMatches) {
                        // перевіряємо що у відібраних нема ще такого матчу
                        boolean isNewMatch = matches.stream()
                                .anyMatch(n->n.getHeimteam()== match.getHeimteam() && n.getAuswaertsream() == match.getAuswaertsream());
                        if(!isNewMatch) {
                            //спочатку додаємо в проміжний список, адже може підходящих матчів не вистачить
                            //для формування дня і треба буде піднабрати ще
                            additionalMatches.add(match);
                            //перевіряємо, якщо у нас вже достатньо додаткових матчів для формування туру
                            //то формуємо і вивалюємось звідусіль
                            if (additionalMatches.size() == 5 - matches.size()){
                                matches.addAll(additionalMatches);
                                break;
                            }
                        }
                    }
                }
                if (matches.size() < 5) {
                    Match lastMatch = matches.getLast();
                    teamsToPlan.add(lastMatch.getHeimteam());
                    teamsToPlan.add(lastMatch.getAuswaertsream());
                    matches.remove(lastMatch);
                    verbotenMatches.add(lastMatch);
                }
            }
        }
        //якщо відбувся хоча б один матч, значить тур відбувся
        if(!matches.isEmpty()) {
            for (Match match : matches) {
                match.simulate();
            }
            Spieltag spieltag = new Spieltag(matches, spieltagNumber);
            this.splieltaege.add(spieltag);
            malenSpieltagTabelle(spieltag);
        } else {
            System.out.println("Es gibt keine Spielen mehr");
        }
    }

    private boolean isWarHeim(Team team1, Team team2, ArrayList<Match> verbotenMatches) {
        boolean warHeim = false;
        for (Match match : verbotenMatches) {
            if(team1 == match.getHeimteam() && team2 == match.getAuswaertsream()){
                return true;
            }
        }
        for (Spieltag spieltag : splieltaege){
             warHeim = spieltag.getSpiele().stream()
                    .anyMatch(n->n.getHeimteam()== team1 && n.getAuswaertsream() == team2);
            if (warHeim) {
                //якщо так то далі не перевіряємо.
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
        System.out.print("Enter name first player: ");
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
            System.out.println("First player is no exist. Try to enter his name correctly");
            return;
        }

        System.out.print("Enter name second player: ");
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
            System.out.println("Second player is no exist. Try to enter his name correctly");
            return;
        }

        if (team1 == team2) {
            System.out.println("Your players are in one team (" + team1.getName() + "). You cant transfer them ");
            return;
        }

        boolean istFortgesetzt = true;
        System.out.printf("Do you want to transfer %s from %s to %s from %s? y/n: ",
                name1, team1.getName(), name2, team2.getName());
        do {

            String wahl = scanner.nextLine();
            switch (wahl) {
                case "y":
                    team1.setSpieler(player2);
                    team2.setSpieler(player1);
                    team1.removeSpieler(player1);
                    team2.removeSpieler(player2);
                    System.out.println("Transfer ist fertig!");
                    break;
                case "n":
                   System.out.println("Ok, keine Transfer");
                    istFortgesetzt = false;
                    break;
               default:
                   System.out.println("Enter only 'y' for 'yes' or 'n' for 'no'");
                   break;
            }
        } while (istFortgesetzt);
        scanner.close();


    }
    //endregion

    //region Show tables

    public void showSpiele() {
        Scanner scanner = new Scanner(System.in);
        String wahl;
        boolean istFortgesetzt = true;
        String menue = """                    
                    Choose option to print plays:
                    1 - Print one Spielertag
                    2 - Print all games
                    Jede andere Eingabe - Print beenden
                \s""";
        System.out.print(menue);
        do {
            System.out.print("\nIhre Wahl: ");
            wahl = scanner.nextLine();
            switch (wahl) {
                case "1":
                    System.out.print("\nEnter number Spieltag: ");
                    if (scanner.hasNextInt()) {
                        int numberSpieltag = scanner.nextInt();
                        Optional<Spieltag> optMatch = splieltaege.stream().filter(n->n.getNumber()==numberSpieltag).findAny();
                        if(optMatch.isPresent()) {
                            Spieltag spieltag = optMatch.get();
                            malenSpieltagTabelle(spieltag);
                        }
                        else {
                            System.out.println("We didnt find Spieltag with number " + numberSpieltag);
                        }
                    }
                    else{
                        System.out.println("\nYou entered incorrect value. Operation beendet");
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
        StringBuilder table = new StringBuilder();
        table.append("Spieltag ").append(spieltag.getNumber()).append('\n');
        for (Match match : spieltag.getSpiele()) {
            table.append("\n -").append(match.getHeimteam().getName())
                    .append(' ').append(match.getToreHeim()).append(':').append(match.getToreAuswaerts())
                    .append(' ').append(match.getAuswaertsream().getName());
        }
        System.out.println(table);
    }

    public void showSpielern() {
        Scanner scanner = new Scanner(System.in);
        String wahl;
        boolean istFortgesetzt = true;
        String menue = """                    
                    Choose option to print plays:
                    1 - Print Spielern von ein Team
                    2 - Print all Spielern
                    Jede andere Eingabe - Print beenden
                \s""";
        System.out.print(menue);
        do {
            System.out.print("\nIhre Wahl: ");
            wahl = scanner.nextLine();
            switch (wahl) {
                case "1":
                    System.out.print("\nEnter Team name: ");
                    String teamName = scanner.nextLine();
                    Optional<Team> optTeam = teams.stream().filter(n->n.getName().equals(teamName)).findAny();
                    if(optTeam.isPresent()) {
                        Team team = optTeam.get();
                        malenSpeilernTabelle(team);
                    }
                    else {
                        System.out.println("We didnt find team with name " + teamName);
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

    public void showTeamsTable(){
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
    private ArrayList<Team> readTeamsFromFile() {
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

    public void writeTeamsInFile(boolean istNeueTabelle) {
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
