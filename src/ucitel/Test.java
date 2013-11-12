import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Test extends JFrame {
public Test() {
    initComponents();
}

private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    menuBar1 = new JMenuBar();
    menu1 = new JMenu();
    menuItem5 = new JMenuItem();
    menuItem4 = new JMenuItem();
    checkBoxMenuItem1 = new JCheckBoxMenuItem();
    menuItem3 = new JMenuItem();
    menu2 = new JMenu();
    menuItem6 = new JMenuItem();
    tabbedPane1 = new JTabbedPane();
    panel1 = new JPanel();
    scrollPane1 = new JScrollPane();
    textPane1 = new JTextPane();
    button1 = new JButton();
    button2 = new JButton();
    button3 = new JButton();
    scrollPane2 = new JScrollPane();
    textArea1 = new JTextArea();
    scrollPane3 = new JScrollPane();
    tree1 = new JTree();
    progressBar1 = new JProgressBar();
    radioButton1 = new JRadioButton();
    checkBox1 = new JCheckBox();
    panel2 = new JPanel();
    panel3 = new JPanel();

    Container contentPane = getContentPane();
    contentPane.setLayout(null);
    {
        {
            menu1.setText("text");
            menuItem5.setText("text");
            menu1.add(menuItem5);
            menuItem4.setText("text");
            menu1.add(menuItem4);
            menu1.addSeparator();
            checkBoxMenuItem1.setText("text");
            menu1.add(checkBoxMenuItem1);
            menuItem3.setText("text");
            menu1.add(menuItem3);
        }
        menuBar1.add(menu1);
        {
            menu2.setText("text");
            menuItem6.setText("text");
            menu2.add(menuItem6);
        }
        menuBar1.add(menu2);
    }
    setJMenuBar(menuBar1);
    {

        {
            panel1.setLayout(null);

            {
                scrollPane1.setViewportView(textPane1);
            }
            panel1.add(scrollPane1);
            scrollPane1.setBounds(15, 15, 665, scrollPane1.getPreferredSize().height);
            button1.setText("text");
            panel1.add(button1);
            button1.setBounds(15, 45, 300, button1.getPreferredSize().height);
            button2.setText("text");
            panel1.add(button2);
            button2.setBounds(325, 45, 140, 23);
            button3.setText("text");
            panel1.add(button3);
            button3.setBounds(470, 45, 210, 23);
            {
                scrollPane2.setViewportView(textArea1);
            }
            panel1.add(scrollPane2);
            scrollPane2.setBounds(15, 75, 665, 175);
            {
                scrollPane3.setViewportView(tree1);
            }
            panel1.add(scrollPane3);
            scrollPane3.setBounds(15, 260, 140, 150);
            progressBar1.setValue(40);
            panel1.add(progressBar1);
            progressBar1.setBounds(160, 260, 520, 20);
            radioButton1.setText("text");
            panel1.add(radioButton1);
            radioButton1.setBounds(160, 290, 100, radioButton1.getPreferredSize().height);
            checkBox1.setText("text");
            panel1.add(checkBox1);
            checkBox1.setBounds(265, 295, 165, checkBox1.getPreferredSize().height);

            {
                Dimension preferredSize = new Dimension();
                for(int i = 0; i < panel1.getComponentCount(); i++) {
                    Rectangle bounds = panel1.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = panel1.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                panel1.setMinimumSize(preferredSize);
                panel1.setPreferredSize(preferredSize);
            }
        }
        tabbedPane1.addTab("text", panel1);
        {
            panel2.setLayout(null);

            {
                Dimension preferredSize = new Dimension();
                for(int i = 0; i < panel2.getComponentCount(); i++) {
                    Rectangle bounds = panel2.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = panel2.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                panel2.setMinimumSize(preferredSize);
                panel2.setPreferredSize(preferredSize);
            }
        }
        tabbedPane1.addTab("text", panel2);

        {
            panel3.setLayout(null);

            {
                Dimension preferredSize = new Dimension();
                for(int i = 0; i < panel3.getComponentCount(); i++) {
                    Rectangle bounds = panel3.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = panel3.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                panel3.setMinimumSize(preferredSize);
                panel3.setPreferredSize(preferredSize);
            }
        }
        tabbedPane1.addTab("text", panel3);

    }
    contentPane.add(tabbedPane1);
    tabbedPane1.setBounds(10, 10, 700, 450);

    {
        Dimension preferredSize = new Dimension();
        for(int i = 0; i < contentPane.getComponentCount(); i++) {
            Rectangle bounds = contentPane.getComponent(i).getBounds();
            preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
            preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
        }
        Insets insets = contentPane.getInsets();
        preferredSize.width += insets.right;
        preferredSize.height += insets.bottom;
        contentPane.setMinimumSize(preferredSize);
        contentPane.setPreferredSize(preferredSize);
    }
    pack();
    setLocationRelativeTo(getOwner());

}

private JMenuBar menuBar1;
private JMenu menu1;
private JMenuItem menuItem5;
private JMenuItem menuItem4;
private JCheckBoxMenuItem checkBoxMenuItem1;
private JMenuItem menuItem3;
private JMenu menu2;
private JMenuItem menuItem6;
private JTabbedPane tabbedPane1;
private JPanel panel1;
private JScrollPane scrollPane1;
private JTextPane textPane1;
private JButton button1;
private JButton button2;
private JButton button3;
private JScrollPane scrollPane2;
private JTextArea textArea1;
private JScrollPane scrollPane3;
private JTree tree1;
private JProgressBar progressBar1;
private JRadioButton radioButton1;
private JCheckBox checkBox1;
private JPanel panel2;
private JPanel panel3;

public static void main(String args[]){

    try{
          UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");

    }
    catch(Exception e){
        System.out.println("Nimbus isn't available");
    }

    new Test().setVisible(true);


  }
}
