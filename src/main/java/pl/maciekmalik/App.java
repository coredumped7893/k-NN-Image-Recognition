package pl.maciekmalik;

import pl.maciekmalik.GUI.MainGUI;
import pl.maciekmalik.GUI.StartGUI;

/**
 * @author Maciek Malik
 * @version v0.6
 */
public class App {

    public static Model model = new Model();
    public static String lastPath="";

    public static void main(String args[]){
        new App();
    }

    /**
     * Startuje początkowy ekran: okno pustego modelu
     */
    public App() {

        java.awt.EventQueue.invokeLater(()->new MainGUI().setVisible(true));

    }
}
