package pl.maciekmalik.OpenCV;

import org.opencv.core.Core;
import pl.maciekmalik.App;

import java.util.logging.Logger;

public class CVAction {

    public static final Logger LOGGER = Logger.getLogger(App.class.getName());

    /**
     * Minimalna wartosć piksela w obrazie w danym kanale
     */
    protected static final int L_MIN = 0;


    /**
     * Maksymalna wartosć piksela w obrazie w danym kanale
     */
    protected static final int L_MAX = 255;



    static {
        LOGGER.fine("Trying to load OCV lib");
        nu.pattern.OpenCV.loadLocally();
        System.out.println("OpenCV loaded: " + Core.VERSION);
    }

}
