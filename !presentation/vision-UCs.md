# E09 Modellflug Logbuch

![Beispielbild für den Modellflugclub](/!presentation\images\beispielbild2.jpg "Beispielbild für den Modellflugclub")

<div style="page-break-after: always;"></div>

## Aufgabenstellung

* Modellflugclub Rossendorf
* Logbuchpflicht für Piloten
* bisherigen Bemühungen waren zu umständlich
    * --> Loggen wurde vernachlässigt
* Produkt verfolgt moderner Ansatz
    * Webapp für Smartphones
        * jede Person besitzt Smartphone
    * effektivere, funktionellere Lösung

<div style="page-break-after: always;"></div>

## Use Cases und Akteure

* Pilot
    * regulärer Benutzer
* Admin
    * verwaltet Software und Benutzer

<div style="page-break-after: always;"></div>

* Use-Cases:
    * Pilot anmelden
    * Flugeintrag anlegen
    * Flugleiter anmelden
    * Flugeinträge einsehen
    * Pilot abmelden

![UC-Diagramm](/!presentation\images\UC-model.png "Use Cases des mobilen Logbuchs")


==========================================================================


# Sprachnotizen Laurin
* Einleitung
    * wir sind Jakob und Laurin
    * präsentieren euch heute das Projekt der Gruppe "E09 Modellflug Logbuch"  
    * vor Demonstration der Software, beginne ich mit der Aufgabenstellung und der Erklärung des Sachverhaltes anhand von Akteuern und Use-Cases

* Aufgabenstellung
    * unser Auftraggeber: Modellflugclub Rossendorf
    * da auf Flugplatz Luftverkehr mit Modellfliegern, Dronen und Raketen herrscht:
        * durch Gesetzgeber vorgeschriebene Logbuchpflicht
    * bisherige Bemühungen dem nachzukommen konnten nicht zufriedenstellend durchgesetzt werden
        * zwei Probleme: 
            * das Clubgebäude befindet sich nicht direkt am Flugplatz
            * daher Vernachlässigung der Logbuchpflicht mit Stift und Papier
            * außerdem: vorhandenes Terminal für elektronisches Logbuch ist aufgrund des Anschlusses an hissige Wetterstation nur alle 10 Minuten kurz online
    * um diese Probleme zu vermeiden und das Führen des Logbuchs zu fördern, gab der Auftraggeber dieses Projekt in Auftrag:
        * Logbuch als vom Smartphone benutzbare App
        * wir entschieden uns diese als Webapp zu verwirklichen
            * Plattformunabhängigkeit von Android, iOS, etc.

* Akteure
    * Pilot
        * regulärer Benutzer der App, der Logbuch führen soll
    * Admin
        * Deployment des Produkts
        * verwaltet Software und Benutzer

* Use Cases
    * Piloten können
        * sich anmelden
            * Benutzername und Passwort
        * Flugeinträge anlegen
        * eigene Flugeinträge einsehen
        * eigene Flugeinträge bearbeiten
        * Anmelden als Flugleiter
            * spezielle Anforderung des Auftraggebers
            * ab einer Anzahl von drei Piloten muss ein Flugleiter anwesend sein
            * dieser koordiniert Flugverkehr
        * sich abmelden
    * Admin hat alle Berechtigungen wie Pilot, kann aber zusätzlich
        * alle Flugeinträge einsehen
        * alle Flugeinträge bearbeiten
        * Piloten erstellen, modifizieren und deaktivieren

* Jakob fährt mit Präsentation des Produktes fort