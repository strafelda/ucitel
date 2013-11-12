/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * AddTheme.java
 *
 * Created on 6.3.2010, 21:26:10
 */

package ucitel.forms;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.application.Action;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;
import ucitel.ConnectionToDB;
/**
 *
 * @author strafeldap
 */
public class AddThemeForm extends javax.swing.JDialog {

    ConnectionToDB conn;

    /** Creates new form AddTheme */
    public AddThemeForm(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        
            conn = ConnectionToDB.getInstance();
        
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        quit = new javax.swing.JButton();
        theme = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(ucitel.Ucitel.class).getContext().getActionMap(AddThemeForm.class, this);
        quit.setAction(actionMap.get("exit")); // NOI18N
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ucitel.Ucitel.class).getContext().getResourceMap(AddThemeForm.class);
        quit.setText(resourceMap.getString("quit.text")); // NOI18N
        quit.setName("quit"); // NOI18N

        theme.setText(resourceMap.getString("theme.text")); // NOI18N
        theme.setName("theme"); // NOI18N
        theme.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                themeKeyPressed(evt);
            }
        });

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jButton1.setAction(actionMap.get("addTheme")); // NOI18N
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(theme, javax.swing.GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(quit, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(theme, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(quit))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void themeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_themeKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER){
            evt.consume();
            addTheme();
        }
    }//GEN-LAST:event_themeKeyPressed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                AddThemeForm dialog = new AddThemeForm(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    @Action
    public void exit() {
        this.dispose();
    }

    @Action
    public void addTheme() {
        String themeString = theme.getText();
        if (!themeString.isEmpty()){
            try{
                if (conn.themeExists(themeString)){
                    JOptionPane.showMessageDialog(GUI.getInstance().getComponent(),"Téma už existuje" ,"Error" ,JOptionPane.OK_OPTION);
                } else {
                    conn.addTheme(themeString);
                    GUI.getInstance().loadThemes();
                    GUI.getInstance().setTheme(themeString);
                    this.dispose();
                }
            } catch (Exception e){
                e.toString();
                e.printStackTrace();
            }
        }

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton quit;
    private javax.swing.JTextField theme;
    // End of variables declaration//GEN-END:variables

}