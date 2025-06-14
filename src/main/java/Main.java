import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String wahl;
        System.out.println("Initialisierung der Daten aus Dateien gestartet.");
        League league = new League();
        System.out.println("Initialisierung der Daten aus Dateien beendet.");

        boolean istFortgesetzt = true;
        String menue = """
                  Bundesliga-Simulation
                  Wählen Sie die Aktion, die Sie ausführen möchten:
                  1 - Eine Runde simulieren
                  2 - Neue Meisterschaft
                  3 - Transfer
                  4 - Mannschaftstabelle anzeigen
                  5 - Spieler anzeigen
                  6 - Spiele anzeigen
                  7 - Mannschaftsergebnisse in Datei schreiben
                  m - Menü anzeigen
                  Jede andere Eingabe - Programm beenden
                \s""";
        System.out.print(menue);
        do {
            System.out.print("\nIhre Wahl: ");
            wahl = scanner.nextLine();
            switch (wahl) {
                case "1":
                    league.austragenSpieltag();
                    break;
                case "2":
                    league = new League();
                    league.schreibenTeamsInDatei(true);
                    break;
                case "3":
                    league.transfer();
                    break;
                case "4":
                    league.anzeigenMannschaftstabelle();
                    break;
                case "5":
                    league.anzeigenSpielern();
                    break;
                case "6":
                    league.anzeigenSpiele();
                    break;
                case "7":
                    league.schreibenTeamsInDatei(false);
                    break;
                case "m":
                    System.out.println(menue);
                    break;
                default:
                    istFortgesetzt = false;
                    scanner.close();
                    break;
            }
        } while (istFortgesetzt);


    }


}