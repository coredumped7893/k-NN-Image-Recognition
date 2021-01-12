package pl.maciekmalik.GUI;

import pl.maciekmalik.App;
import pl.maciekmalik.Model;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class NewObject extends JFrame {
    public static final Logger LOGGER = Logger.getLogger(App.class.getName());
    private Boolean type;
    private MainGUI gui;
    private File file = null;

    public NewObject() throws HeadlessException {
        initComponents();
    }

    /**
     * Konstruktor okna do dodawania nowych obiektów
     * @param type_
     * @param gui
     */
    public NewObject(Boolean type_,MainGUI gui){
        this.type = type_;
        this.gui = gui;
        initComponents();
        if(type_){
            //Nie potrzebujemy pola tekstowego
            jTextField1.setVisible(false);
            jTName.setVisible(false);
            jTName.setText("Nowe");
        }

    }

    private void initComponents() {

        jPIMGPreview = new javax.swing.JPanel();
        jBChooseFile = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jTName = new javax.swing.JTextField();
        jBOK = new javax.swing.JButton();
        jBCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPIMGPreview.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout jPIMGPreviewLayout = new javax.swing.GroupLayout(jPIMGPreview);
        jPIMGPreview.setLayout(jPIMGPreviewLayout);
        jPIMGPreviewLayout.setHorizontalGroup(
                jPIMGPreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 130, Short.MAX_VALUE)
        );
        jPIMGPreviewLayout.setVerticalGroup(
                jPIMGPreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 130, Short.MAX_VALUE)
        );

        jBChooseFile.setText("Wybierz Plik");
        jBChooseFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBChooseFileActionPerformed(evt);
            }
        });

        jTextField1.setEditable(false);
        jTextField1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField1.setText("Nazwa klasy");
        jTextField1.setEnabled(false);

        jTName.setToolTipText("...............");
        jTName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTNameActionPerformed(evt);
            }
        });

        jBOK.setText("OK");
        jBOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBOKActionPerformed(evt);
            }
        });

        jBCancel.setText("Anuluj");
        jBCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPIMGPreview, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jBChooseFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jTextField1)
                                        .addComponent(jTName)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addGap(0, 108, Short.MAX_VALUE)
                                                .addComponent(jBCancel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jBOK)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jBChooseFile)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jTName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jBOK)
                                                        .addComponent(jBCancel)))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jPIMGPreview, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 29, Short.MAX_VALUE)))
                                .addContainerGap())
        );

        pack();
    }// </editor-fold>

    /**
     * Dodanie nowego obiektu i zamknięcie okna
     * @param evt -
     */
    private void jBOKActionPerformed(java.awt.event.ActionEvent evt) {
        boolean cont = true;
        if(this.file == null){
            cont = false;
        }
        if(!this.type){
            //Jeżeli dodajemy obiekt modelu
            if(jTName.getText().equals("")){
                cont = false;
            }
        }

        if(cont){
            App.model.addObject(file.getPath(),this.type,jTName.getText());
            gui.getjPChart().removeAll();
            gui.getjPChart().add(DataDisplay.generateChart(),BorderLayout.NORTH);
            gui.getjPChart().revalidate();
            gui.populateList();
            gui.getjCXType().setEnabled(true);
            gui.getjCYType().setEnabled(true);

            if(App.model.getAllObjects().size() == 1){
                gui.getjCXType().setModel(App.model.getObject("").getPropertiesModel());
                gui.getjCYType().setModel(App.model.getObject("").getPropertiesModel());

                gui.getjCXType().setSelectedIndex(0);
                gui.getjCYType().setSelectedIndex(1);

                App.model.setXTypeName(gui.getjCXType().getSelectedItem().toString());
                App.model.setYTypeName(gui.getjCYType().getSelectedItem().toString());
            }

            gui.getjLObjList().setSelectedIndex(0);
            gui.getjLObjList1().setSelectedIndex(0);

            this.dispose();
        }else{
            JOptionPane.showMessageDialog(new JFrame(),
                    "Wypełnij wszystkie pola",
                    "Err",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Anuluje dodawanie nowego obiektu
     * @param evt -
     */
    private void jBCancelActionPerformed(java.awt.event.ActionEvent evt) {
        this.dispose();
    }

    /**
     * Wyświetlenie okna do wyboru pliku
     * @param evt
     */
    private void jBChooseFileActionPerformed(java.awt.event.ActionEvent evt) {
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        if(App.lastPath.equals("")){
            App.lastPath = s;
        }else{
            s= App.lastPath;
        }
        final JFileChooser fc = new JFileChooser(s);
        fc.addChoosableFileFilter(new ImageFilter());
        fc.setAcceptAllFileFilterUsed(false);
        int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();
            App.lastPath = file.getPath();
                    LOGGER.info("Opening: " + file.getPath() + ".\n");

            //Usawienie ikony
            JLabel p = new JLabel(new ImageIcon());
            jPIMGPreview.removeAll();
            jPIMGPreview.setLayout(new BorderLayout());
            jPIMGPreview.add(p,BorderLayout.NORTH);

            try {
                p.setIcon(new ImageIcon(ImageIO.read(file).getScaledInstance(jPIMGPreview.getWidth(),jPIMGPreview.getHeight(),Image.SCALE_SMOOTH)));
            } catch (IOException e) {
                e.printStackTrace();
            }



        } else {
            LOGGER.info("Open command cancelled by user.\n");
        }



        jPIMGPreview.revalidate();

    }

    private void jTNameActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }


    // Variables declaration - do not modify
    private javax.swing.JButton jBCancel;
    private javax.swing.JButton jBChooseFile;
    private javax.swing.JButton jBOK;
    private javax.swing.JPanel jPIMGPreview;
    private javax.swing.JTextField jTName;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration
}
