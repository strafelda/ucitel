/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * zkousejZnova.java
 *
 * Created on 13.12.2010, 11:50:15
 */

package ucitel.forms;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import ucitel.ConnectionToDB;

/**
 *
 * @author strafeldap
 */
public class ZkousejZnova extends javax.swing.JDialog {

    /** Creates new form zkousejZnova */
    public ZkousejZnova() {
        initComponents();
    }

     public ZkousejZnova(java.awt.Frame parent) {
        super(parent);
        initComponents();
	answeredWrongly.setText("Zodpovězené špatně");
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
        private void initComponents() {

                jLabel1 = new javax.swing.JLabel();
                jPanel1 = new javax.swing.JPanel();
                _1x = new javax.swing.JCheckBox();
                _2x = new javax.swing.JCheckBox();
                _3x = new javax.swing.JCheckBox();
                _4x = new javax.swing.JCheckBox();
                _5x = new javax.swing.JCheckBox();
                _6x = new javax.swing.JCheckBox();
                _7x = new javax.swing.JCheckBox();
                _8x = new javax.swing.JCheckBox();
                _9x = new javax.swing.JCheckBox();
                _10x = new javax.swing.JCheckBox();
                jPanel3 = new javax.swing.JPanel();
                _11x = new javax.swing.JCheckBox();
                _12x = new javax.swing.JCheckBox();
                _13x = new javax.swing.JCheckBox();
                _14x = new javax.swing.JCheckBox();
                _15x = new javax.swing.JCheckBox();
                _16x = new javax.swing.JCheckBox();
                _17x = new javax.swing.JCheckBox();
                _18x = new javax.swing.JCheckBox();
                _19x = new javax.swing.JCheckBox();
                _20x = new javax.swing.JCheckBox();
                answeredWrongly = new javax.swing.JButton();
                jButton2 = new javax.swing.JButton();
                jButton1 = new javax.swing.JButton();

                setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
                setName("Form"); // NOI18N

                org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ucitel.Ucitel.class).getContext().getResourceMap(ZkousejZnova.class);
                jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
                jLabel1.setName("jLabel1"); // NOI18N

                jPanel1.setName("jPanel1"); // NOI18N

                _1x.setText(resourceMap.getString("_1x.text")); // NOI18N
                _1x.setName("_1x"); // NOI18N

                _2x.setText(resourceMap.getString("_2x.text")); // NOI18N
                _2x.setName("_2x"); // NOI18N

                _3x.setText(resourceMap.getString("_3x.text")); // NOI18N
                _3x.setName("_3x"); // NOI18N

                _4x.setText(resourceMap.getString("_4x.text")); // NOI18N
                _4x.setName("_4x"); // NOI18N

                _5x.setText(resourceMap.getString("_5x.text")); // NOI18N
                _5x.setName("_5x"); // NOI18N

                _6x.setText(resourceMap.getString("_6x.text")); // NOI18N
                _6x.setName("_6x"); // NOI18N

                _7x.setText(resourceMap.getString("_7x.text")); // NOI18N
                _7x.setName("_7x"); // NOI18N

                _8x.setText(resourceMap.getString("_8x.text")); // NOI18N
                _8x.setName("_8x"); // NOI18N

                _9x.setText(resourceMap.getString("_9x.text")); // NOI18N
                _9x.setName("_9x"); // NOI18N

                _10x.setText(resourceMap.getString("_10x.text")); // NOI18N
                _10x.setName("_10x"); // NOI18N

                javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
                jPanel1.setLayout(jPanel1Layout);
                jPanel1Layout.setHorizontalGroup(
                        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(_1x)
                                        .addComponent(_2x)
                                        .addComponent(_3x)
                                        .addComponent(_4x)
                                        .addComponent(_5x)
                                        .addComponent(_6x)
                                        .addComponent(_7x)
                                        .addComponent(_8x)
                                        .addComponent(_9x)
                                        .addComponent(_10x))
                                .addContainerGap(60, Short.MAX_VALUE))
                );
                jPanel1Layout.setVerticalGroup(
                        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(_1x)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(_2x)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(_3x)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(_4x)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(_5x)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(_6x)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(_7x)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(_8x)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(_9x)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(_10x)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );

                jPanel3.setName("jPanel3"); // NOI18N

                _11x.setText(resourceMap.getString("_11x.text")); // NOI18N
                _11x.setName("_11x"); // NOI18N

                _12x.setText(resourceMap.getString("_12x.text")); // NOI18N
                _12x.setName("_12x"); // NOI18N

                _13x.setText(resourceMap.getString("_13x.text")); // NOI18N
                _13x.setName("_13x"); // NOI18N

                _14x.setText(resourceMap.getString("_14x.text")); // NOI18N
                _14x.setName("_14x"); // NOI18N

                _15x.setText(resourceMap.getString("_15x.text")); // NOI18N
                _15x.setName("_15x"); // NOI18N

                _16x.setText(resourceMap.getString("_16x.text")); // NOI18N
                _16x.setName("_16x"); // NOI18N

                _17x.setText(resourceMap.getString("_17x.text")); // NOI18N
                _17x.setName("_17x"); // NOI18N

                _18x.setText(resourceMap.getString("_18x.text")); // NOI18N
                _18x.setName("_18x"); // NOI18N

                _19x.setText(resourceMap.getString("_19x.text")); // NOI18N
                _19x.setName("_19x"); // NOI18N

                _20x.setText(resourceMap.getString("_20x.text")); // NOI18N
                _20x.setName("_20x"); // NOI18N

                javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
                jPanel3.setLayout(jPanel3Layout);
                jPanel3Layout.setHorizontalGroup(
                        jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(_15x)
                                        .addComponent(_13x)
                                        .addComponent(_14x)
                                        .addComponent(_11x)
                                        .addComponent(_12x)
                                        .addComponent(_16x)
                                        .addComponent(_17x)
                                        .addComponent(_18x)
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(_19x)
                                                .addComponent(_20x)))
                                .addContainerGap(14, Short.MAX_VALUE))
                );
                jPanel3Layout.setVerticalGroup(
                        jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(_15x)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(_13x)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(_14x)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(_11x)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(_12x)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(_16x)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(_17x)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(_18x)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(_19x)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(_20x)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );

                answeredWrongly.setText(resourceMap.getString("answeredWrongly.text")); // NOI18N
                answeredWrongly.setName("answeredWrongly"); // NOI18N
                answeredWrongly.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                answeredWronglyActionPerformed(evt);
                        }
                });

                jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
                jButton2.setName("jButton2"); // NOI18N
                jButton2.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                jButton2ActionPerformed(evt);
                        }
                });

                jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
                jButton1.setName("jButton1"); // NOI18N
                jButton1.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                jButton1ActionPerformed(evt);
                        }
                });

                javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
                getContentPane().setLayout(layout);
                layout.setHorizontalGroup(
                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                        .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING))
                                                .addContainerGap())
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(answeredWrongly, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addGap(97, 97, 97))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jButton2)
                                                .addGap(93, 93, 93)
                                                .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE)
                                                .addContainerGap())))
                );
                layout.setVerticalGroup(
                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(answeredWrongly)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jButton2)
                                        .addComponent(jButton1))
                                .addContainerGap())
                );

                pack();
        }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
	    ConnectionToDB conn = null;

	    conn = ConnectionToDB.getInstance();

	    if (_1x.isSelected()){
		    conn.resetLastAsked(1);
	    }
	    if (_2x.isSelected()){
		    conn.resetLastAsked(2);
	    }
	    if (_3x.isSelected()){
		    conn.resetLastAsked(3);
	    }
	    if (_4x.isSelected()){
		    conn.resetLastAsked(4);
	    }
	    if (_5x.isSelected()){
		    conn.resetLastAsked(5);
	    }
	    if (_6x.isSelected()){
		    conn.resetLastAsked(6);
	    }
	    if (_7x.isSelected()){
		    conn.resetLastAsked(7);
	    }
	    if (_8x.isSelected()){
		    conn.resetLastAsked(8);
	    }
	    if (_9x.isSelected()){
		    conn.resetLastAsked(9);
	    }
	    if (_10x.isSelected()){
		    conn.resetLastAsked(10);
	    }
	    if (_11x.isSelected()){
		    conn.resetLastAsked(11);
	    }
	    if (_12x.isSelected()){
		    conn.resetLastAsked(12);
	    }
	    if (_13x.isSelected()){
		    conn.resetLastAsked(13);
	    }
	    if (_14x.isSelected()){
		    conn.resetLastAsked(14);
	    }
	    if (_15x.isSelected()){
		    conn.resetLastAsked(15);
	    }
	    if (_16x.isSelected()){
		    conn.resetLastAsked(16);
	    }
	    if (_17x.isSelected()){
		    conn.resetLastAsked(17);
	    }
	    if (_18x.isSelected()){
		    conn.resetLastAsked(18);
	    }
	    if (_19x.isSelected()){
		    conn.resetLastAsked(19);
	    }
	    if (_20x.isSelected()){
		    conn.resetLastAsked(20);
	    }

	    GUI.getInstance().askTeacher();
	    this.dispose();
}//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
	    this.dispose();
}//GEN-LAST:event_jButton2ActionPerformed

    private void answeredWronglyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_answeredWronglyActionPerformed
	    ConnectionToDB.getInstance().updateLastAsked();
	    GUI.getInstance().askTeacher();
	    this.dispose();
    }//GEN-LAST:event_answeredWronglyActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ZkousejZnova().setVisible(true);
            }
        });
    }

        // Variables declaration - do not modify//GEN-BEGIN:variables
        private javax.swing.JCheckBox _10x;
        private javax.swing.JCheckBox _11x;
        private javax.swing.JCheckBox _12x;
        private javax.swing.JCheckBox _13x;
        private javax.swing.JCheckBox _14x;
        private javax.swing.JCheckBox _15x;
        private javax.swing.JCheckBox _16x;
        private javax.swing.JCheckBox _17x;
        private javax.swing.JCheckBox _18x;
        private javax.swing.JCheckBox _19x;
        private javax.swing.JCheckBox _1x;
        private javax.swing.JCheckBox _20x;
        private javax.swing.JCheckBox _2x;
        private javax.swing.JCheckBox _3x;
        private javax.swing.JCheckBox _4x;
        private javax.swing.JCheckBox _5x;
        private javax.swing.JCheckBox _6x;
        private javax.swing.JCheckBox _7x;
        private javax.swing.JCheckBox _8x;
        private javax.swing.JCheckBox _9x;
        private javax.swing.JButton answeredWrongly;
        private javax.swing.JButton jButton1;
        private javax.swing.JButton jButton2;
        private javax.swing.JLabel jLabel1;
        private javax.swing.JPanel jPanel1;
        private javax.swing.JPanel jPanel3;
        // End of variables declaration//GEN-END:variables

}