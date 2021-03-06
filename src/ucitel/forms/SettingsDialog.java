/*
 * SettingsDialog.java
 *
 * Created on 28. srpen 2008, 13:34
 */

package ucitel.forms;

import org.jdesktop.application.Action;
import javax.swing.JFileChooser;
import ucitel.ConnectionToDB;
import ucitel.XML;
/**
 *
 * @author  strafeldap
 */
public class SettingsDialog extends javax.swing.JDialog {

    String mysqlpath;
    
    /** Creates new form SettingsDialog */
    private SettingsDialog() {
        initComponents();
        this.pack();
    }

    public SettingsDialog(java.awt.Frame parent) {
        super(parent);
        initComponents();
        try{
            if (ConnectionToDB.getInstance().isLocal()==true){
                localRB.setSelected(true);
            }else{
                serverRB.setSelected(true);
            }
            MySQLPathField.setText(XML.getInstance().getMySQLPath());
            enableControls();
        }catch (Exception e){
            e.printStackTrace();
        }

	String password = String.valueOf(passwordField.getPassword());
        if (password.isEmpty()){
            storePasswordCB.setSelected(false);
        } else {
            storePasswordCB.setSelected(true);
        }
                
        XML ini = XML.getInstance();

        this.downloadWavCB.setSelected(ini.getDownloadWav());
        
        answerCntField.setText(Integer.toString(ini.getAnswerCnt()));
        //origAnswerCnt = ini.getAnswerCnt();

        listLenField.setText(Integer.toString(ini.getListLen()));
        //origListLen = ini.getListLen();

        userField.setText(ini.getLogin());
        //origLogin = ini.getLogin();

        conNameField.setText(ini.getConName());


        passwordField.setText(ini.getPassword());
        //origPassword = ini.getPassword();
        
        portField.setText(ini.getDBport());
        hostField.setText(ini.getDBhost());
        proxyField.setText(ini.getProxy());
        proxyPortField.setText(ini.getProxy_port());

        if (ini.getDownloadWav()) {
            this.downloadWavCB.setSelected(true);
            
            if (ini.getUseProxy().matches("true")){
                this.useProxyCB.setSelected(true);
                this.useProxyCB.setEnabled(true);
                this.proxyField.setEnabled(true);
                this.proxyPortField.setEnabled(true);
            } else {
                this.useProxyCB.setEnabled(false);
                this.proxyField.setEnabled(false);
                this.proxyPortField.setEnabled(false);
            }

        } else {
            this.downloadWavCB.setSelected(false);
            this.useProxyCB.setEnabled(false);
            this.proxyField.setEnabled(false);
            this.proxyPortField.setEnabled(false);
        }

        this.soundOnCB.setSelected(ini.getSoundOn());




        this.setModal(true);
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel5 = new javax.swing.JLabel();
        jFileChooser1 = new javax.swing.JFileChooser();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        passwordField = new javax.swing.JPasswordField();
        localRB = new javax.swing.JRadioButton();
        serverRB = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        userField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        storePasswordCB = new javax.swing.JCheckBox();
        jLabel9 = new javax.swing.JLabel();
        conNameField = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        MySQLPathField = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        portField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        hostField = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        answerCntField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        listLenField = new javax.swing.JTextField();
        shutDownDatabaseCheckBox = new javax.swing.JCheckBox();
        jPanel4 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        useProxyCB = new javax.swing.JCheckBox();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        proxyField = new javax.swing.JTextField();
        proxyPortField = new javax.swing.JTextField();
        downloadWavCB = new javax.swing.JCheckBox();
        soundOnCB = new javax.swing.JCheckBox();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ucitel.Ucitel.class).getContext().getResourceMap(SettingsDialog.class);
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        jFileChooser1.setName("jFileChooser1"); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N
        setResizable(false);

        jPanel1.setName("jPanel1"); // NOI18N

        passwordField.setText(resourceMap.getString("passwordField.text")); // NOI18N
        passwordField.setName("passwordField"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(ucitel.Ucitel.class).getContext().getActionMap(SettingsDialog.class, this);
        localRB.setAction(actionMap.get("enableControls")); // NOI18N
        buttonGroup1.add(localRB);
        localRB.setText(resourceMap.getString("localRB.text")); // NOI18N
        localRB.setName("localRB"); // NOI18N

        serverRB.setAction(actionMap.get("enableControls")); // NOI18N
        buttonGroup1.add(serverRB);
        serverRB.setText(resourceMap.getString("serverRB.text")); // NOI18N
        serverRB.setName("serverRB"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        userField.setEditable(false);
        userField.setText(resourceMap.getString("userField.text")); // NOI18N
        userField.setEnabled(false);
        userField.setName("userField"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        storePasswordCB.setText(resourceMap.getString("storePasswordCB.text")); // NOI18N
        storePasswordCB.setName("storePasswordCB"); // NOI18N

        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N

        conNameField.setText(resourceMap.getString("conNameField.text")); // NOI18N
        conNameField.setName("conNameField"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(storePasswordCB)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(userField, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
                            .addComponent(conNameField)
                            .addComponent(passwordField))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 94, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(localRB)
                            .addComponent(serverRB))
                        .addGap(14, 14, 14))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(conNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(userField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(localRB))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(serverRB)
                    .addComponent(passwordField, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(storePasswordCB)
                .addContainerGap())
        );

        jPanel2.setName("jPanel2"); // NOI18N

        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N

        MySQLPathField.setText(resourceMap.getString("MySQLPathField.text")); // NOI18N
        MySQLPathField.setName("MySQLPathField"); // NOI18N

        jButton2.setAction(actionMap.get("chooseDirectory")); // NOI18N
        jButton2.setName("jButton2"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        portField.setText(resourceMap.getString("portField.text")); // NOI18N
        portField.setEnabled(false);
        portField.setName("portField"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        hostField.setText(resourceMap.getString("hostField.text")); // NOI18N
        hostField.setName("hostField"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addGap(56, 56, 56)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(hostField, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                            .addComponent(portField, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(MySQLPathField, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton2)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(hostField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(portField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jButton2)
                    .addComponent(MySQLPathField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setName("jPanel5"); // NOI18N

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        answerCntField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        answerCntField.setText(resourceMap.getString("answerCntField.text")); // NOI18N
        answerCntField.setName("answerCntField"); // NOI18N

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        listLenField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        listLenField.setText(resourceMap.getString("listLenField.text")); // NOI18N
        listLenField.setName("listLenField"); // NOI18N

        shutDownDatabaseCheckBox.setText(resourceMap.getString("shutDownDatabaseCheckBox.text")); // NOI18N
        shutDownDatabaseCheckBox.setName("shutDownDatabaseCheckBox"); // NOI18N
        shutDownDatabaseCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shutDownDatabaseCheckBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(answerCntField, 0, 0, Short.MAX_VALUE)
                    .addComponent(listLenField, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(95, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(17, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(answerCntField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(listLenField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel4.setName("jPanel4"); // NOI18N

        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        okButton.setAction(actionMap.get("loadSettings")); // NOI18N
        okButton.setName("okButton"); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 202, Short.MAX_VALUE)
                .addComponent(okButton)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton)
                    .addComponent(jButton1))
                .addContainerGap())
        );

        jPanel6.setName("jPanel6"); // NOI18N

        useProxyCB.setText(resourceMap.getString("useProxyCB.text")); // NOI18N
        useProxyCB.setName("useProxyCB"); // NOI18N
        useProxyCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useProxyCBActionPerformed(evt);
            }
        });

        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N

        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N

        proxyField.setText(resourceMap.getString("proxyField.text")); // NOI18N
        proxyField.setName("proxyField"); // NOI18N

        proxyPortField.setText(resourceMap.getString("proxyPortField.text")); // NOI18N
        proxyPortField.setName("proxyPortField"); // NOI18N

        downloadWavCB.setText(resourceMap.getString("downloadWavCB.text")); // NOI18N
        downloadWavCB.setName("downloadWavCB"); // NOI18N
        downloadWavCB.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                downloadWavCBItemStateChanged(evt);
            }
        });

        soundOnCB.setText(resourceMap.getString("soundOnCB.text")); // NOI18N
        soundOnCB.setName("soundOnCB"); // NOI18N
        soundOnCB.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                soundOnCBStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(useProxyCB)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel6Layout.createSequentialGroup()
                            .addComponent(downloadWavCB)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(soundOnCB))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel6Layout.createSequentialGroup()
                            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel10)
                                .addComponent(jLabel11))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(proxyPortField, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(proxyField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(144, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(downloadWavCB)
                    .addComponent(soundOnCB))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(useProxyCB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(proxyField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(proxyPortField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(16, 16, 16))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
    
    String password = String.valueOf(passwordField.getPassword());
    String host = hostField.getText();
    String port = portField.getText();
    boolean useProxy = useProxyCB.isSelected();
    String proxy = proxyField.getText();
    String proxyPort = proxyPortField.getText();

    try{
        
        XML ini = XML.getInstance();
        
        if (useProxy){
            ini.setUseProxy("true");
        }
        else {
            ini.setUseProxy("false");
        }
        
        ini.setProxyPort(proxyPort);
        ini.setProxy(proxy);

        if (this.downloadWavCB.isSelected())
        {
            ini.setDownloadWav(true);
        }
        else
        {
            ini.setDownloadWav(false);
        }
        
        ini.setConName(this.conNameField.getText());
        ini.setDBhost(host);
        ini.setDBport(port);
        ini.setMySQLPath(MySQLPathField.getText());
        
        if (storePasswordCB.isSelected()==true){
            ini.setPassword(password);    
        } else {
            ini.setPassword("");
        }
        ini.setPasswordHash(password);
        int pocetOdpovedi = Integer.parseInt(answerCntField.getText());
        int listLen = Integer.parseInt(listLenField.getText());
        ini.setAnswerCnt(pocetOdpovedi);
        ini.setListLen(listLen);
        ini.setSoundOn(soundOnCB.isSelected());
        
        ini.writeXML();
        //li = new LoginInfo(conNameField.getName(), user,password,host,port,MySQLPathField.getText(),isLocalDB);
    } catch (Exception e){
        e.printStackTrace();
    }
    
    this.dispose();
}//GEN-LAST:event_okButtonActionPerformed

private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    this.dispose();
}//GEN-LAST:event_jButton1ActionPerformed

private void shutDownDatabaseCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shutDownDatabaseCheckBoxActionPerformed
    
        XML ini = XML.getInstance();
        ini.setShutDownDatabaseOnExit(shutDownDatabaseCheckBox.isSelected());

}//GEN-LAST:event_shutDownDatabaseCheckBoxActionPerformed

private void useProxyCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useProxyCBActionPerformed
        proxyField.setEnabled(useProxyCB.isSelected());
        proxyPortField.setEnabled(useProxyCB.isSelected());

}//GEN-LAST:event_useProxyCBActionPerformed

private void downloadWavCBItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_downloadWavCBItemStateChanged
    if (this.downloadWavCB.isSelected()==true) {

            this.useProxyCB.setEnabled(true);
            if (this.useProxyCB.isSelected()==true){
                this.proxyField.setEnabled(true);
                this.proxyPortField.setEnabled(true);
            } else {
                this.proxyField.setEnabled(false);
                this.proxyPortField.setEnabled(false);
            }

        } else {
            this.useProxyCB.setEnabled(false);
            this.proxyField.setEnabled(false);
            this.proxyPortField.setEnabled(false);
        }
}//GEN-LAST:event_downloadWavCBItemStateChanged

private void soundOnCBStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_soundOnCBStateChanged

}//GEN-LAST:event_soundOnCBStateChanged

    @Action
    public void loadSettings() {
    }

    @Action
    public void chooseDirectory() {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.showOpenDialog(this);
        MySQLPathField.setText(fc.getSelectedFile().toString());
                
    }

    @Action
    public void enableControls() {

        MySQLPathField.setEnabled(localRB.isSelected());
        hostField.setEnabled(!localRB.isSelected());
        portField.setEnabled(!localRB.isSelected());

    }

    /**
    * @param args the command line arguments
    */
    /*public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SettingsDialog().setVisible(true);
            }
        });
    }*/

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField MySQLPathField;
    private javax.swing.JTextField answerCntField;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JTextField conNameField;
    private javax.swing.JCheckBox downloadWavCB;
    private javax.swing.JTextField hostField;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JTextField listLenField;
    private javax.swing.JRadioButton localRB;
    private javax.swing.JButton okButton;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JTextField portField;
    private javax.swing.JTextField proxyField;
    private javax.swing.JTextField proxyPortField;
    private javax.swing.JRadioButton serverRB;
    private javax.swing.JCheckBox shutDownDatabaseCheckBox;
    private javax.swing.JCheckBox soundOnCB;
    private javax.swing.JCheckBox storePasswordCB;
    private javax.swing.JCheckBox useProxyCB;
    private javax.swing.JTextField userField;
    // End of variables declaration//GEN-END:variables

}
