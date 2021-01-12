package pl.maciekmalik;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import pl.maciekmalik.GUI.MainGUI;
import pl.maciekmalik.OpenCV.CVAction;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * Klasa dla pojedyńczego obiektu
 */
public class ModelObject extends CVAction implements Serializable {

    private transient Mat mat;
    private transient BufferedImage image;
    private String fileName;
    private Boolean type;
    private String className;//Nazwa klasy do której obiekt należy

    private Random r = new Random(System.nanoTime());

    /**
     * Object properties
     * 0 -> area
     * 1 -> number of holes
     * 2 -> M0
     * etc...
     */
    private Map<String,Double> properties = new HashMap<>(4);


    /**
     * Wyliczona metryka względem innych wzorcowych obiektów
     * (Nazwa obiektu,wartość metryki względem niego)
     */
    private Map<String,Double> metrics = new HashMap<>();
    private Map<String,String> reverseMetrics = new HashMap<>();
    /**
     * Wylicza wybraną z listy metryke
     */
    public void calculateMetric(){
        if(App.model.getModelOnlyObjects().size() >= App.model.getK()){
            if(this.type){//Wyliczamy tylko dla obiektów nowych
                for (String name : App.model.getModelOnlyObjects()) {
                    ModelObject tmp = App.model.getObject(name);
                    double dist=0;
                    if(App.model.getSelectedMetricsName().equals("Euclides")){
                        dist = Math.sqrt(Math.pow(this.properties.get(App.model.getXTypeName())-tmp.getSelectedProperty().get("X"),2)
                                +Math.pow(this.properties.get(App.model.getYTypeName())-tmp.getSelectedProperty().get("Y"),2));
                    }else if(App.model.getSelectedMetricsName().equals("Chebyshev")){
                        dist = Math.max(
                                Math.abs(this.properties.get(App.model.getXTypeName())-tmp.getSelectedProperty().get("X")),
                                Math.abs(this.properties.get(App.model.getYTypeName())-tmp.getSelectedProperty().get("Y")) );
                    }
                    metrics.put(name,dist);
                    reverseMetrics.put(String.format("%.3f", dist),name);
                }
                //else .........
            }
        }else{

        }

        for(String m: metrics.keySet()){
            String fN = m.substring(m.lastIndexOf("/") + 1);
            System.out.println( fN+" "+metrics.get(m));
        }
    }



    public ModelObject() {

    }

    /**
     * Konstruktor nowego obiektu, czyta plik, zamienia go na odpowiednie struktury i wylicza momenty i inne właściwości
     * @param fileName
     * @param type
     * @param cN
     */
    public ModelObject(String fileName,Boolean type,String cN) {
        this.fileName = fileName;
        this.className = cN;
        this.type = type;
        //this.image = ImageIO.read(new File(fileName));
        this.mat = Imgcodecs.imread(fileName, Imgcodecs.IMREAD_GRAYSCALE);
        if(this.mat.empty()){
            try {
                JOptionPane.showMessageDialog(new JFrame(),
                        "Błąd przy otwieraniu pliku",
                        "File Error",
                        JOptionPane.ERROR_MESSAGE);
                throw new IOException("Cannot open file :(");
            } catch (IOException e) {
                e.printStackTrace();
            }
            //System.exit(10);
            return;
        }
        //Calculate moments and properties
//        properties.put(r.nextDouble()+className.length());
//        properties.put(r.nextDouble()+className.length());
//        properties.put(r.nextDouble()*10);
        Mat binary = new Mat(mat.rows(), mat.cols(), mat.type(), new Scalar(0));
        Imgproc.threshold(mat, binary, 100, 255, Imgproc.THRESH_BINARY);
        List<MatOfPoint> contours = new ArrayList<>();
        Moments moments;
        Mat hierarchey = new Mat();
        Mat draw = Mat.zeros(mat.size(), CvType.CV_8UC1);
        Imgproc.findContours(binary, contours, hierarchey, Imgproc.RETR_TREE,
                Imgproc.CHAIN_APPROX_SIMPLE);

        double area = 0.0;
        double arcLen = 0.0;
        double aspectRatio = 0.0;
        double extent = 0.0;
        double angle = 0.0;
        float[] minCircleR = new float[3];
        Point circleCenter = new Point();
        if(contours.size() > 0){
            MatOfPoint2f  NewMtx = new MatOfPoint2f( contours.get(0).toArray() );
            Imgproc.minEnclosingCircle(NewMtx,circleCenter,minCircleR);
            Rect r =  Imgproc.boundingRect(contours.get(0));
            aspectRatio = Math.max((double) r.width/r.height,(double)r.height/r.width);
            area = Imgproc.contourArea(contours.get(0));
            arcLen = Imgproc.arcLength(NewMtx,true);
            moments = Imgproc.moments(contours.get(0));
            if(NewMtx.toArray().length >=5){
                RotatedRect rr = Imgproc.fitEllipse(NewMtx);
                angle = rr.angle%90;
            }
            extent = area/(r.width*r.height);
        }else{
            moments = Imgproc.moments(draw);
        }

        for (int i = 1; i < contours.size(); i++) {
            area -= Imgproc.contourArea(contours.get(i));
            //System.out.println("-");
            Scalar color = new Scalar(255);
            Imgproc.drawContours(draw, contours, i, color, 1, Core.LINE_8, hierarchey, 2, new Point());
        }

        //Wyliczanie właściwości obiektów
        properties.put("Pole",area);
        properties.put("Liczba dziur",(double) (contours.size()-1));
        properties.put("Dlugosc luku",arcLen);
        properties.put("Srednica",(double)minCircleR[0]);
        properties.put("Aspect ratio",aspectRatio);
        properties.put("Extent",extent);
        properties.put("Orientacja",angle);

        // spatial moments
        properties.put("M00",moments.get_m00());
        properties.put("M10",moments.get_m10());
        properties.put("M01",moments.get_m01());
        properties.put("M20",moments.get_m20());
        properties.put("M11",moments.get_m11());
        properties.put("M02",moments.get_m02());
        properties.put("M30",moments.get_m30());
        properties.put("M21",moments.get_m21());
        properties.put("M12",moments.get_m12());
        properties.put("M03",moments.get_m03());

        // central moments
        properties.put("MU20",moments.get_mu20());
        properties.put("MU11",moments.get_mu11());
        properties.put("MU02",moments.get_mu02());
        properties.put("MU30",moments.get_mu30());
        properties.put("MU21",moments.get_mu21());
        properties.put("MU12",moments.get_mu12());
        properties.put("MU03",moments.get_mu03());

        // central normalized moments
        properties.put("NU20",moments.get_nu20());
        properties.put("NU11",moments.get_nu11());
        properties.put("NU02",moments.get_nu02());
        properties.put("NU30",moments.get_nu30());
        properties.put("NU21",moments.get_nu21());
        properties.put("NU12",moments.get_nu12());
        properties.put("NU03",moments.get_nu03());


        //Wyliczamy tylko dla obiektów do klasyfikacji
        if(this.type){
            calculateMetric();
        }

        //System.out.println("Holes:"+(contours.size()-1));
        //System.out.println("Total area:"+area);
        //System.out.println("Moments:"+moments.toString());
        String fN = fileName.substring(fileName.lastIndexOf("/") + 1);
        System.out.println("-----------------------------------");
        System.out.println("Added Object: "+fN);
        System.out.println("-----------------------------------");
            //properties = !!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //-------------------

        this.image = MatToBufferedImage(this.mat);

    }

    public Map<String, Double> getProperties() {
        return properties;
    }

    /**
     * @return Zwraca listę możliwych do wyboru właściwości obiektów
     */
    public DefaultComboBoxModel getPropertiesModel(){
        DefaultComboBoxModel m = new DefaultComboBoxModel();
        for (String n: properties.keySet()) {
            m.addElement(n);
        }
        return m;
    }

    public String getClassName() {
        return className;
    }

    /**
     * Konwersja typu Mat na BufferedImage (w celu wyświetlenia w oknie)
     * @param mat
     * @return
     */
    public BufferedImage MatToBufferedImage(Mat mat){
        byte[] data1 = new byte[mat.rows() * mat.cols() * (int)(mat.elemSize())];
        mat.get(0, 0, data1);

        // Creating the buffered image
        BufferedImage bufImage = new BufferedImage(mat.cols(),mat.rows(),
                BufferedImage.TYPE_BYTE_GRAY);
        // Setting the data elements to the image
        bufImage.getRaster().setDataElements(0, 0, mat.cols(), mat.rows(), data1);

        return bufImage;
    }

    public ImageIcon getIcon(){
        return new ImageIcon(image.getScaledInstance(MainGUI.MAXWIDTHICON,MainGUI.MAXHEIGHTICON, Image.SCALE_SMOOTH));
    }

    public Mat getMat() {
        return mat;
    }

    public Boolean getType() {
        return type;
    }

    public Map<String, Double> getMetrics() {
        return metrics;
    }

    public Map<String, String> getReverseMetrics() {
        return reverseMetrics;
    }

    /**
     * @return Zwraca Mape (X,Y) dla aktualnie wybranych cech dla danego obiektu
     */
    public Map<String,Double> getSelectedProperty(){
        Map<String,Double> tmp = new HashMap<>();
        tmp.put("X", this.properties.get(App.model.getXTypeName()));
        tmp.put("Y", this.properties.get(App.model.getYTypeName()));
        return tmp;
    }




    //Obsługa serializacji obiektów (zapis i odczyt z pliku)
    //  |
    //  |
    //  V

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        byte[] bytes = new byte[(int) (mat.total()*mat.elemSize())];
        mat.get(0,0,bytes);
        out.writeInt((int) mat.elemSize());
        out.writeInt(mat.type());
        out.writeInt(mat.rows());
        out.writeInt(mat.cols());
        out.write(bytes);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        int eSize = in.readInt();
        int type = in.readInt();
        final int rows = in.readInt();
        int cols = in.readInt();
        byte[] bytes = new byte[eSize*rows*cols];
        //in.readFully(bytes);//Read rest of the bytes
        in.readFully(bytes);
        //image = ImageIO.read(in);
        mat = new Mat(rows,cols,type);
        mat.put(0,0, bytes);
        image = MatToBufferedImage(mat);

    }



}
