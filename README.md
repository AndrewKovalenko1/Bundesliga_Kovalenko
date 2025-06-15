🏆 Aufgabe: "Die Bundesliga-Simulation"
🎯 Ziel:
Modelliere die Fußball-Bundesliga mit objektorientierten Konzepten in Java. Die Simulation soll Teams, Spieler und Spieltage verwalten und einfache Spielausgänge simulieren können.
📋 Anforderungen:
1. Klassenstruktur
Erstelle mindestens folgende Klassen:
Team
Attribute: Name, Liste von Spielern, Punkte, Tore, Gegentore
Player
Attribute: Name, Position, Stärke (1–100)
Match
Attribute: Heimteam, Auswärtsteam, Tore Heim, Tore Auswärts
Methode: simulate() – zufällige Spielausgänge basierend auf Teamstärke
League
Attribute: Liste von Teams, Liste von Spieltagen
Methode: playMatchday() – simuliert alle Spiele eines Spieltags
Methode: showTable() – zeigt aktuelle Tabelle nach Punkten, Tordifferenz
2. Funktionale Anforderungen
Erstelle 6–10 Teams mit je 5–11 Spielern
Simuliere mindestens 5 Spieltage (Hinrunde)
Nach jedem Spieltag wird die Tabelle aktualisiert
Teams erhalten:
3 Punkte für einen Sieg
1 Punkt bei Unentschieden
0 Punkte bei Niederlage
💡 Zusatzideen (für Fortgeschrittene):
Implementiere eine Spieler-Transferfunktion
Speichere Ergebnisse und Tabelle in einer Datei (z. B. JSON)
UI-Variante: Konsolenmenü zur Steuerung der Liga
Team-Manager mit Budget für Transfers
🧪 Beispielausgabe:
 
📅 Spieltag 1
- Bayern 2:1 Dortmund
- Leipzig 1:1 Leverkusen
...
 
📊 Tabelle nach Spieltag 1:
1. Bayern         3 Pkt  2:1
2. Leipzig        1 Pkt  1:1
3. Leverkusen     1 Pkt  1:1
4. Dortmund       0 Pkt  1:2
