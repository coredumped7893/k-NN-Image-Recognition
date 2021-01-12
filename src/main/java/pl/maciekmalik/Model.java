package pl.maciekmalik;

import java.io.*;
import java.util.*;

/**
 * Klasa zawierająca listę obiektów będących modelem(wzorcem) jak i tych klasyfikowanych,
 * wraz z dokładnymi danymi dot. ich momentów i zdjęć podglądowych
 */
public class Model implements Serializable {

    /**
     * Liczba sąsiadów których będziemy brać pod uwagę
     */
    private  Integer k = 1;

    //Które cechy zostały wybrane do klasyfikacji obiektów
    private  Integer XType = 0;
    private  Integer YType = 0;
    private  String XTypeName = "M00";
    private  String YTypeName = "M00";

    public   String modelFileName = "";

    //Wybrana metryka
    private  Integer selectedMetrics = 0;
    private  String selectedMetricsName = "Euclides";


    public Model() {}

    //Struktury mapujące nazwy plików do obiektów jak i do ich klas
    private Map<String,ModelObject> modelObjects = new HashMap<>();//Mapowanie nazwy pliku do obiektu
    private List<String> modelOnlyObjects = new ArrayList<>();//Nazwa obiektu tylko z modelu
    private Set<String> classTypes = new HashSet<>();//Zbiór różnych nazw klas
    private Map<String,List<String>> classToName = new HashMap<>();//Mapowanie nazwy klasy do nazw obiektów


    /**
     * Dodawanie nowego obiektu do listy modelu lub do klasyfikacji
     *
     * @param filename nazwa pliku z obrazem obiektu
     * @param type 0 = model ; 1 = obiekt do klasyfikacji
     */
    public void addObject(String filename, Boolean type,String className){
        filename = _replaceSlash(filename);
        modelObjects.put(filename,new ModelObject(filename,type,className));
        if(!type){
            modelOnlyObjects.add(filename);
        }
        classTypes.add(className);
        if(!classToName.containsKey(className)){
            //Jeżeli nie ma w mapie takiej klasy to stwórz nową i dodaj dla niej pustą listę
            this.classToName.put(className,new ArrayList<>(1));
        }
        this.classToName.get(className).add(filename);//Dodanie nowego obiektu
    }

    /**
     * Usuwanie wcześniej dodanego obiektu,
     * jeżeli był jednynym w swojej klasie to usuwamy też tą klasę
     * @param filename
     */
    public void removeObject(String filename){
        this.classToName.get(modelObjects.get(filename).getClassName()).remove(filename);
        if(this.classToName.get(modelObjects.get(filename).getClassName()).isEmpty()){
            classTypes.remove(modelObjects.get(filename).getClassName());
            this.classToName.remove(modelObjects.get(filename).getClassName());
        }
        if(!modelObjects.get(filename).getType()){
            modelOnlyObjects.remove(filename);
        }
        modelObjects.remove(filename);
    }

    /**
     * zamienia backslash "\" na slash "/",
     * kompatybilność pomiędzy Linuxem a windowsem
     * @param fN
     * @return
     */
    private String _replaceSlash(String fN){
        return fN.replaceAll("\\\\", "/");
    }

    /**
     * Zwraca listę obiektów należących do modelu
     * @return
     */
    public List<String> getModelOnlyObjects() {
        return modelOnlyObjects;
    }

    /**
     * Zwraca liczbę K
     * @return
     */
    public  Integer getK() {
        return k;
    }

    public  void setK(Integer k) {
        App.model.k = k;
    }

    /**
     * Zwraca obecnie wybraną metrykę do liczenia
     * @return
     */
    public  Integer getSelectedMetrics() {
        return selectedMetrics;
    }

    public  void setSelectedMetrics(Integer selectedMetrics) {
        App.model.selectedMetrics = selectedMetrics;
    }

    public  String getSelectedMetricsName() {
        return selectedMetricsName;
    }

    public  void setSelectedMetricsName(String selectedMetricsName) {
        App.model.selectedMetricsName = selectedMetricsName;
    }

    public  void setModelFileName(String modelFileName) {
        App.model.modelFileName = modelFileName;
    }

    public  Integer getXType() {
        return XType;
    }

    public  Integer getYType() {
        return YType;
    }

    public  String getXTypeName() {
        return XTypeName;
    }

    public  void setXTypeName(String XTypeName) {
        App.model.XTypeName = XTypeName;
    }

    public  String getYTypeName() {
        return YTypeName;
    }

    public  void setYTypeName(String YTypeName) {
        App.model.YTypeName = YTypeName;
    }

    public  void setXType(Integer XType) {
        App.model.XType = XType;
    }

    public  void setYType(Integer YType) {
        App.model.YType = YType;
    }

    /**
     * Zwraca obiekt na podstawie jego nazwy
     * @param filename
     * @return
     */
    public ModelObject getObject(String filename){
        if(filename.equals("")){
            return modelObjects.get(modelObjects.keySet().toArray()[0]);
        }else{
            return modelObjects.get(filename);
        }
    }

    public Map<String,ModelObject> getAllObjects(){
        return modelObjects;
    }

    public Set<String> getAllClasses(){
        return this.classTypes;
    }

    public Map<String,List<String>> getAllClassToNames(){
        return classToName;
    }

    //public transient List<BufferedImage> images = new ArrayList<>();


    /**
     * Zapisywanie całęgo modelu na dysk w postaci pojedyńczego pliku
     * @param serObj obiekt modelu do zapisu
     * @see Model
     */
    public static void WriteModelToFile(Object serObj,String fileName) {

        try {
            FileOutputStream fileOut = new FileOutputStream(fileName);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);

            objectOut.writeObject(serObj);
            objectOut.close();
            System.out.println("The Object  was succesfully written to a file");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Ładowanie modelu z pliku
     *
     * @param file nazwa pliku
     * @see Serializable
     * @see FileInputStream
     * @see ObjectInputStream
     * @return Object
     */
    public static Object ReadModelFromFile(String file) {
        Object tmp = null;
        try {
            //modelFileName = file;
            FileInputStream fileOut = new FileInputStream(file);
            ObjectInputStream objectIn = new ObjectInputStream(fileOut);
            tmp = objectIn.readObject();
            objectIn.close();
            System.out.println("The file ("+file+") was succesfully loaded");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return tmp;
    }


}
