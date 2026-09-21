package com.Figuren;

/**
 * Einstieg in die Figuren-Demo. Enthält keine Spiellogik.
 */
public class start {

    public static void main(String[] args) {
        Kreis kreis = new Kreis();

        kreis.farbeAendern("orange");

        kreis.sichtbarMachen();

        kreis.nachObenBewegen();

        kreis.nachObenBewegen();

        kreis.nachObenBewegen();
    }
}
