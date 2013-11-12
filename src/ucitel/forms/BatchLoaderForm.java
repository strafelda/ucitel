/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * BatchLoaderForm.java
 *
 * Created on Nov 21, 2010, 1:42:29 PM
 */
package ucitel.forms;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import ucitel.ConnectionToDB;
import ucitel.DirectionOfTranslation;
import ucitel.Example;
import ucitel.Jazyky;
import ucitel.Language;
import ucitel.XML;

/**
 *
 * @author petr
 */
public class BatchLoaderForm extends javax.swing.JDialog {

    ConnectionToDB conn;
    List<Example> examples;
    boolean loaded = false;

    /** Creates new form BatchLoaderForm */
    public BatchLoaderForm() {
        initComponents();
    }

    public BatchLoaderForm(java.awt.Frame parent, boolean modal) throws Exception {
        super(parent, modal);
        conn = ConnectionToDB.getInstance();

        initComponents();
	swapWords.setText("Prohodit jazyky");
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.showOpenDialog(parent);
        if (fc.getSelectedFile() != null) {
            chosenFile.setText(fc.getSelectedFile().toString());
            //List listOfLanguages = Jazyky.getLanguageList();

            loadThemes();
            loadTable(chosenFile.getText());

        } else {
            loaded = false;
            return;
        }

        loaded = true;

    }

    public boolean isLoaded(){
        return loaded;
    }

    public void loadThemes(){

        try{
            themeCB.removeAllItems();
            themeCB.addItem((String) GUI.vsechno);

            ArrayList<String> themes = ConnectionToDB.getInstance().getThemes();
            Iterator i = themes.iterator();
            while (true == i.hasNext()){
                themeCB.addItem(i.next());
            }
        } catch (Exception e){
            //JOptionPane.showMessageDialog(GUI.getInstance().getComponent(), e.toString(), "Error" ,JOptionPane.OK_OPTION);
            e.toString();
            //e.printStackTrace();
        }

        themeCB.setSelectedItem(XML.getInstance().getLastTheme());
    }

    private void loadTable(String fileName) throws Exception {

        try {
            String sCurrentLine;

            BufferedReader br = new BufferedReader(new FileReader(fileName));
            int index;
            String first = null,second= null;
            examples = new ArrayList<Example>();

            
            while ((sCurrentLine = br.readLine()) != null) {
                //String UTF8Str = new String(sCurrentLine.getBytes("UTF-8"));// = new String(sCurrentLine.getBytes(),"UTF-8"));

		index = sCurrentLine.indexOf(";");
                if (index == -1){
                    index = sCurrentLine.indexOf("/");
                }
                
                if (index == -1){
                    throw new Exception("Neplatný formát dokumentu");
                }
                first = sCurrentLine.substring(0,index).trim();
                second = sCurrentLine.substring(index+1).trim();
                examples.add(new Example(first, second));

            }

	    br.close();

	    table.setModel(new javax.swing.table.DefaultTableModel(new Object[examples.size()][1], new String[]{Language.CZECH.toString(), GUI.getInstance().direction.getForeignLanguage().toString()}));

            Iterator i = examples.iterator();
            //String question = null;
            //String answer = null;
            int iterator = 0;
            while (i.hasNext()){
                Example example = (Example) i.next();
                table.setValueAt(example.getCzech(), iterator, 0);
                table.setValueAt(example.getForeign(), iterator, 1);
                iterator++;
            }

        } catch (FileNotFoundException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
        private void initComponents() {

                jFileChooser1 = new javax.swing.JFileChooser();
                load = new javax.swing.JButton();
                storno = new javax.swing.JButton();
                chosenFile = new javax.swing.JLabel();
                jScrollPane1 = new javax.swing.JScrollPane();
                table = new javax.swing.JTable();
                themeCB = new javax.swing.JComboBox();
                jLabel3 = new javax.swing.JLabel();
                swapWords = new javax.swing.JButton();

                jFileChooser1.setName("jFileChooser1"); // NOI18N

                setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
                setName("Form"); // NOI18N

                org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ucitel.Ucitel.class).getContext().getResourceMap(BatchLoaderForm.class);
                load.setText(resourceMap.getString("load.text")); // NOI18N
                load.setName("load"); // NOI18N
                load.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                loadActionPerformed(evt);
                        }
                });

                storno.setText(resourceMap.getString("storno.text")); // NOI18N
                storno.setName("storno"); // NOI18N
                storno.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                stornoActionPerformed(evt);
                        }
                });

                chosenFile.setText(resourceMap.getString("chosenFile.text")); // NOI18N
                chosenFile.setName("chosenFile"); // NOI18N

                jScrollPane1.setName("jScrollPane1"); // NOI18N

                table.setModel(new javax.swing.table.DefaultTableModel(
                        new Object [][] {
                                {null, null},
                        },
                        new String [] {
                                "Jazyk 1", "Jazyk 2"
                        }
                ));
                table.setName("table"); // NOI18N
                jScrollPane1.setViewportView(table);

                themeCB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
                themeCB.setEnabled(false);
                themeCB.setName("themeCB"); // NOI18N

                jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
                jLabel3.setName("jLabel3"); // NOI18N

                swapWords.setText(resourceMap.getString("swapWords.text")); // NOI18N
                swapWords.setName("swapWords"); // NOI18N
                swapWords.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                swapWordsActionPerformed(evt);
                        }
                });

                javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
                getContentPane().setLayout(layout);
                layout.setHorizontalGroup(
                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(chosenFile)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel3)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(themeCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(swapWords)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(storno)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(load)))
                                .addContainerGap())
                );
                layout.setVerticalGroup(
                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(chosenFile)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 572, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel3)
                                        .addComponent(themeCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(load)
                                        .addComponent(storno)
                                        .addComponent(swapWords))
                                .addContainerGap())
                );

                pack();
        }// </editor-fold>//GEN-END:initComponents

    private void stornoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stornoActionPerformed
        this.dispose();
    }//GEN-LAST:event_stornoActionPerformed

    public void ChangeName(JTable table, int col_index, String col_name) {
        table.getColumnModel().getColumn(col_index).setHeaderValue(col_name);
        this.repaint();
    }

    private void loadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadActionPerformed
        try {
	    Language from = Language.CZECH;
	    Language to = GUI.getInstance().direction.getForeignLanguage();
	    
	    
            conn.addBatch(examples);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.toString());
            Logger.getLogger(BatchLoaderForm.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.dispose();
    }//GEN-LAST:event_loadActionPerformed

    private void swapWordsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_swapWordsActionPerformed
	    int rowCount = table.getRowCount();
	    for (int i=0;i<rowCount;i++){
		String word1 = table.getValueAt(i, 0).toString();
		String word2 = table.getValueAt(i, 1).toString();
		table.setValueAt(word1, i, 1);
		table.setValueAt(word2, i, 0);
	    }
	    
	    for(int i=0;i<examples.size();i++){
		String foreign = examples.get(i).getForeign();
		String czech = examples.get(i).getCzech();
		examples.get(i).setCzech(foreign);
		examples.get(i).setForeign(czech);
	    }  
	    
	    
	   

    }//GEN-LAST:event_swapWordsActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new BatchLoaderForm().setVisible(true);
            }
        });
    }
        // Variables declaration - do not modify//GEN-BEGIN:variables
        private javax.swing.JLabel chosenFile;
        private javax.swing.JFileChooser jFileChooser1;
        private javax.swing.JLabel jLabel3;
        private javax.swing.JScrollPane jScrollPane1;
        private javax.swing.JButton load;
        private javax.swing.JButton storno;
        private javax.swing.JButton swapWords;
        private javax.swing.JTable table;
        private javax.swing.JComboBox themeCB;
        // End of variables declaration//GEN-END:variables
}
