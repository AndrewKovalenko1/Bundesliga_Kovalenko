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
                  Bundesliga simulation
                    Wählen Sie die Aktion, die Sie ausführen möchten:
                    1 - Simulate one tour
                    2 - New championship
                    3 - Transfer
                    4 - Print teams table
                    5 - Print players
                    6 - print Matches
                    7 - Write team results in file
                    m - Menü anzeigen
                    Jede andere Eingabe - Programm beenden
                \s""";
        System.out.print(menue);
        do {
            System.out.print("\nIhre Wahl: ");
            wahl = scanner.nextLine();
            switch (wahl) {
                case "1":
                    league.playMatchday();
                    break;
                case "2":
                    league = new League();
                    league.writeTeamsInFile(true);
                    break;
                case "3":
                    league.transfer();
                    break;
                case "4":
                    league.showTeamsTable();
                    break;
                case "5":
                    league.showSpielern();
                    break;
                case "6":
                    league.showSpiele();
                    break;
                case "7":
                    league.writeTeamsInFile(false);
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