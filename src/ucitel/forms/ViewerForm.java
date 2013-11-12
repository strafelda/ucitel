/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ViewerForm.java
 *
 * Created on Apr 12, 2012, 8:50:04 PM
 */
package ucitel.forms;

//import java.awt.Dimension;
//import java.awt.Graphics;
import java.util.List;
import javax.swing.event.TableModelEvent;
import ucitel.ConnectionToDB;
import ucitel.GuiDirection;
import ucitel.Language;
import ucitel.Word;
//import java.awt.print.*;
//import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
//import java.io.FileWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JFileChooser;
import javax.swing.JTable.PrintMode;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author peta
 */
public class ViewerForm extends javax.swing.JDialog implements TableModelListener {

    GuiDirection direction;
    ConnectionToDB con;
    List<Integer> ids;

    /** Creates new form ViewerForm */
    public ViewerForm(java.awt.Frame parent) {
        super(parent);
        initComponents();
        direction = GuiDirection.getInstance();
        this.setModal(true);
        this.setTitle("Prohlížeč");
        this.pack();
        closeB.setText("Zavřít");
        printB.setText("Tisk");
        fromL.setText("Od dne:");
        maxL.setText("Max. správných odpovědí:");
        applyFilter.setText("Použij filtr");
        ceskyL.setText("Česky:");
        ciziL.setText("Cizí:");
        delete.setText("Smazat označené");
        export.setText("Export");
        importB.setText("Import");
        con = ConnectionToDB.getInstance();
        fillTable();

    }

    //valueChanged();
    private void fillTable() {
        Language from = GuiDirection.getInstance().getLngFrom();
        Language to = GuiDirection.getInstance().getLngTo();
        //ConnectionToDB con = ConnectionToDB.getInstance();
        Date fromDate = datePicker.getDate();

        //Date fromDate = dateChooser.getDate();
        int maxAnswers = 100;
        if (!maxAnswersTB.getText().isEmpty()) {
            if (maxAnswersTB.getText().matches("\\d+")) {
                maxAnswers = Integer.valueOf(maxAnswersTB.getText());
            }
        }

        List<Word> words = null;
        ids = new ArrayList<Integer>();
        words = con.getVocabulary(from, to, fromDate, maxAnswers + 1);

        table.setModel(new javax.swing.table.DefaultTableModel(
                new Object[words.size()][2],
                new String[]{
                    from.toString(), to.toString()
                }));
        int row = 0;
        for (Word w : words) {
            table.setValueAt(w.getTo(), row, 1);
            table.setValueAt(w.getFrom(), row, 0);
            ids.add(w.getId());
            row++;
        }

        table.getModel().addTableModelListener(this);

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        closeB = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        printB = new javax.swing.JButton();
        fromL = new javax.swing.JLabel();
        maxL = new javax.swing.JLabel();
        maxAnswersTB = new javax.swing.JTextField();
        datePicker = new com.michaelbaranov.microba.calendar.DatePicker();
        jSeparator1 = new javax.swing.JSeparator();
        ceskyL = new javax.swing.JLabel();
        ciziL = new javax.swing.JLabel();
        applyFilter = new javax.swing.JButton();
        delete = new javax.swing.JButton();
        czechFilter = new javax.swing.JTextField();
        foreignFilter = new javax.swing.JTextField();
        export = new javax.swing.JButton();
        importB = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ucitel.Ucitel.class).getContext().getResourceMap(ViewerForm.class);
        closeB.setText(resourceMap.getString("Zavřít.text")); // NOI18N
        closeB.setName("Zavřít"); // NOI18N
        closeB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeBActionPerformed(evt);
            }
        });

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        table.setFont(resourceMap.getFont("table.font")); // NOI18N
        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null}
            },
            new String [] {
                "Title 1", "Title 2"
            }
        ));
        table.setName("table"); // NOI18N
        table.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                tableInputMethodTextChanged(evt);
            }
        });
        table.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tablePropertyChange(evt);
            }
        });
        jScrollPane1.setViewportView(table);
        table.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("table.columnModel.title0")); // NOI18N
        table.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("table.columnModel.title1")); // NOI18N

        printB.setText(resourceMap.getString("printB.text")); // NOI18N
        printB.setName("printB"); // NOI18N
        printB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printBActionPerformed(evt);
            }
        });

        fromL.setText(resourceMap.getString("fromL.text")); // NOI18N
        fromL.setName("fromL"); // NOI18N

        maxL.setText(resourceMap.getString("maxL.text")); // NOI18N
        maxL.setName("maxL"); // NOI18N

        maxAnswersTB.setText(resourceMap.getString("maxAnswersTB.text")); // NOI18N
        maxAnswersTB.setName("maxAnswersTB"); // NOI18N
        maxAnswersTB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maxAnswersTBActionPerformed(evt);
            }
        });
        maxAnswersTB.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                maxAnswersTBKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                maxAnswersTBKeyReleased(evt);
            }
        });

        try {
            datePicker.setDate(null);
        } catch (java.beans.PropertyVetoException e1) {
            e1.printStackTrace();
        }
        datePicker.setName("datePicker"); // NOI18N
        datePicker.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                datePickerActionPerformed(evt);
            }
        });

        jSeparator1.setName("jSeparator1"); // NOI18N

        ceskyL.setText(resourceMap.getString("ceskyL.text")); // NOI18N
        ceskyL.setName("ceskyL"); // NOI18N

        ciziL.setText(resourceMap.getString("ciziL.text")); // NOI18N
        ciziL.setName("ciziL"); // NOI18N

        applyFilter.setText(resourceMap.getString("applyFilter.text")); // NOI18N
        applyFilter.setName("applyFilter"); // NOI18N
        applyFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyFilterActionPerformed(evt);
            }
        });

        delete.setText(resourceMap.getString("delete.text")); // NOI18N
        delete.setName("delete"); // NOI18N
        delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteActionPerformed(evt);
            }
        });

        czechFilter.setText(resourceMap.getString("czechFilter.text")); // NOI18N
        czechFilter.setName("czechFilter"); // NOI18N

        foreignFilter.setText(resourceMap.getString("foreignFilter.text")); // NOI18N
        foreignFilter.setName("foreignFilter"); // NOI18N

        export.setText(resourceMap.getString("export.text")); // NOI18N
        export.setName("export"); // NOI18N
        export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportActionPerformed(evt);
            }
        });

        importB.setText(resourceMap.getString("importB.text")); // NOI18N
        importB.setName("importB"); // NOI18N
        importB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 525, Short.MAX_VALUE)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 525, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(printB)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(delete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(importB)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(export)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 148, Short.MAX_VALUE)
                        .addComponent(closeB))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(ceskyL)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(czechFilter))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(fromL, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(datePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(maxL)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(maxAnswersTB, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(13, 13, 13)
                                .addComponent(ciziL)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(foreignFilter)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 101, Short.MAX_VALUE)
                        .addComponent(applyFilter)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 488, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(maxL)
                                .addComponent(maxAnswersTB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(datePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(fromL))
                        .addGap(8, 8, 8)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ceskyL)
                                .addComponent(czechFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ciziL)
                                .addComponent(foreignFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(applyFilter))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(printB)
                    .addComponent(closeB)
                    .addComponent(delete)
                    .addComponent(importB)
                    .addComponent(export))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void closeBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeBActionPerformed
        this.dispose();
    }//GEN-LAST:event_closeBActionPerformed

    private void printBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printBActionPerformed
        try {
            if (!table.print(PrintMode.NORMAL, null, null, true, null, true, null)) {
                System.err.println("User cancelled printing");
            }
        } catch (java.awt.print.PrinterException e) {
            System.err.format("Cannot print %s%n", e.getMessage());
        }
    }//GEN-LAST:event_printBActionPerformed

    private void maxAnswersTBKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_maxAnswersTBKeyReleased
        //fillTable();
    }//GEN-LAST:event_maxAnswersTBKeyReleased

    private void maxAnswersTBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maxAnswersTBActionPerformed
    }//GEN-LAST:event_maxAnswersTBActionPerformed

    private void datePickerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_datePickerActionPerformed
        //fillTable();
    }//GEN-LAST:event_datePickerActionPerformed

    private void maxAnswersTBKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_maxAnswersTBKeyPressed
    }//GEN-LAST:event_maxAnswersTBKeyPressed

	private void applyFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyFilterActionPerformed
            fillTable();

            String czech = czechFilter.getText();
            if (czech.isEmpty()) {
                czech = ".*";
            } else {
                czech = ".*" + czech + ".*";
            }

            String foreign = foreignFilter.getText();
            if (foreign.isEmpty()) {
                foreign = ".*";
            } else {
                foreign = ".*" + foreign + ".*";
            }

            for (int i = 0; i < table.getRowCount(); i++) {
                if (!table.getValueAt(i, 0).toString().matches(czech)) {
                    ((DefaultTableModel) table.getModel()).removeRow(i);
                    ids.remove(i);
                    i--;
                } else if (!table.getValueAt(i, 1).toString().matches(foreign)) {
                    ((DefaultTableModel) table.getModel()).removeRow(i);
                    ids.remove(i);
                    i--;
                }
            }
	}//GEN-LAST:event_applyFilterActionPerformed

	private void deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteActionPerformed
            int selectedRows[] = table.getSelectedRows();

            for (int i = 0; i < selectedRows.length; i++) {
                String czech = table.getValueAt(selectedRows[i], 0).toString();
                String foreign = table.getValueAt(selectedRows[i], 1).toString();
                con.deleteWord(czech, foreign);
            }

            if (selectedRows.length > 0) {
                fillTable();
            }

	}//GEN-LAST:event_deleteActionPerformed

	private void tableInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_tableInputMethodTextChanged
            // TODO add your handling code here:
	}//GEN-LAST:event_tableInputMethodTextChanged

	private void tablePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tablePropertyChange
	}//GEN-LAST:event_tablePropertyChange

private void exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportActionPerformed
    JFileChooser fc = new JFileChooser();

    if (JFileChooser.APPROVE_OPTION == fc.showSaveDialog(this)) {
        File file = fc.getSelectedFile();
        String path = file.getAbsolutePath();

        try {
            int tableSize = table.getRowCount();
            Language language = direction.getForeignLanguage();
            OutputStream outputStream = new FileOutputStream(path);
            Writer writer = new OutputStreamWriter(outputStream, "UTF-8");
            for (int i = 0; i < tableSize; i++) {
                String czech = (String) table.getValueAt(i, 0);
                String foreign = (String) table.getValueAt(i, 1);
                writer.write(language.name() + ";" + czech + ";" + foreign + "\n");
                
            }

            writer.flush();
            writer.close();
            outputStream.flush();
            outputStream.close();

        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }

}//GEN-LAST:event_exportActionPerformed

private void importBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importBActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_importBActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton applyFilter;
    private javax.swing.JLabel ceskyL;
    private javax.swing.JLabel ciziL;
    private javax.swing.JButton closeB;
    private javax.swing.JTextField czechFilter;
    private com.michaelbaranov.microba.calendar.DatePicker datePicker;
    private javax.swing.JButton delete;
    private javax.swing.JButton export;
    private javax.swing.JTextField foreignFilter;
    private javax.swing.JLabel fromL;
    private javax.swing.JButton importB;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField maxAnswersTB;
    private javax.swing.JLabel maxL;
    private javax.swing.JButton printB;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables

    @Override
    public void tableChanged(TableModelEvent e) {
        int row = table.getSelectedRow();
        if (row != -1) {
            int id = ids.get(row);
            String czech = (String) table.getValueAt(row, 0);
            String foreign = (String) table.getValueAt(row, 1);
            try {
                if (GUI.getInstance().getGrammar()) {
                    con.fix(id, czech, foreign, null, 1, null);
                } else {
                    con.fix(id, czech, foreign, null, 0, null);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }



        }
    }
}