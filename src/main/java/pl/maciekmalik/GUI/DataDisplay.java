package pl.maciekmalik.GUI;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import pl.maciekmalik.App;
import pl.maciekmalik.Model;
import pl.maciekmalik.ModelObject;

import java.awt.*;

/**
 * Klasa odpowiedzialna za generowanie widoku dla danych,
 * | wykres |
 */
public abstract class DataDisplay {

    private static float[] YData;



    public static ChartPanel generateChart(){
        // Create dataset
        XYDataset dataset = createDataset();

            // Create chart
        JFreeChart chart = ChartFactory.createScatterPlot(
                "---------------",
                App.model.getXTypeName(), App.model.getYTypeName(), dataset,
                PlotOrientation.HORIZONTAL,true,true,false);


        //Changes background color
        XYPlot plot = (XYPlot)chart.getPlot();
        plot.setBackgroundPaint(new Color(165, 165, 165, 255));


        // Create Panel
        ChartPanel panel = new ChartPanel(chart);
        return panel;
        //setContentPane(panel);
    }

    private static XYDataset createDataset() {
        XYSeriesCollection dataset = new XYSeriesCollection();

        for (String className: App.model.getAllClasses()) {
            XYSeries tmpS = new XYSeries(className);
            for (String objName: App.model.getAllClassToNames().get(className)) {
                //Iterowanie po obiektach w każdej z klas
                ModelObject tmpObj = App.model.getObject(objName);
                tmpS.add(tmpObj.getSelectedProperty().get("X"), tmpObj.getSelectedProperty().get("Y"));
            }
            dataset.addSeries(tmpS);
        }


        return dataset;
    }

}
