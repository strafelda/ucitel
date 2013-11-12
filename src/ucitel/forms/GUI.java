/*
 * UcitelView.java
 */
package ucitel.forms;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.event.MouseEvent;
import org.jdesktop.application.Action;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import javax.swing.JDialog;
import javax.swing.JFrame;
import java.util.*;
import java.sql.*;
import java.awt.event.KeyEvent;
import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.swing.JOptionPane;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.SwingConstants;
import ucitel.Answer;
import ucitel.CommandToGUI;
import ucitel.ConnectionToDB;
import ucitel.DirectionOfTranslation;
import ucitel.ErrorHandler;
import ucitel.Example;
import ucitel.GuiDirection;
import ucitel.Language;
import ucitel.LoginInfo;
import ucitel.Teacher;
import ucitel.Ucitel;
import ucitel.UcitelException;
import ucitel.WavDownloader;
import ucitel.WavPlayer;
import ucitel.Word;
import ucitel.XML;

import javax.help.*;

/**
 * The application's main frame.
 */
public class GUI extends FrameView implements Observer {

        boolean themes_loaded = false;
        String lastTheme = "";
        GuiDirection direction;
        private static GUI uniqueInstance;
        HelpSet mainHS = null;

        public static synchronized GUI getInstance(SingleFrameApplication... app) {
                if (uniqueInstance == null) {
                        uniqueInstance = new GUI(app[0]);
                }
                return uniqueInstance;
        }

        @Override
        public void update(Observable o, Object arg) {
                Teacher teacher = (Teacher) o;
                //language = DirectionOfTranslation.getInstance().getForeignLanguage();

                if (arg instanceof Answer) {
                        if (((Answer) arg).get() == true) {
                                displayCorrectAnswer();
                                answerField.setBackground(Color.white);
                        } else {
                                String english = ucitel.getQuestion(direction);
                                String czech = ((Answer) arg).getCorrectAnswer();
                                resultLabel.setText(english + " = " + czech);
                                answerField.setText(java.util.ResourceBundle.getBundle("ucitel/resources/GUI").getString("Database_error"));
                                //statusLabel.setText(" ");
                        }
                        answerField.setBackground(Color.white);
                }

                if (arg instanceof CommandToGUI) {
                        answerField.setEnabled(true);
                        jScrollPane1.setEnabled(true);
                        if (((CommandToGUI) arg).getAsk() == true) {
                                String question = teacher.getCzech();
                                questionField.setText(question);
                                deleteCurrentButton.setEnabled(true);
                                hint.setText(teacher.getQuestionHint());
                                answerField.requestFocus();
                                if (resultLabel.getText().equalsIgnoreCase(gUI.getString("All_vocabulary_asked_already"))) {
                                        this.allAsked();
                                }
                        } else {
                                try {
                                        if (((CommandToGUI) arg).getCommand().equals(CommandToGUI.ALL_ASKED)) {
                                                resultLabel.setText(gUI.getString("Databáze_slovíček_je_prázdná"));
                                        } else {
                                                this.allAsked();
                                        }
                                        askButton.setEnabled(false);
                                } catch (Exception ex) {
                                        Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                                }

                                try {
                                        conn = ConnectionToDB.getInstance();

                                        if (!teacher.questionWasAsked() && conn.isQuestionAvailable() == false) {
                                                if (0 == conn.getNumberOfWords(direction)) {
                                                        resultLabel.setText(gUI.getString("Databáze_slovíček_je_prázdná"));

                                                } else {
                                                        this.allAsked();
                                                }
                                                askButton.setEnabled(false);
                                                answerField.setText("");
                                                questionField.setText("");
                                                hint.setText("");
                                                deleteCurrentButton.setEnabled(false);
                                                fixCurrentButton.setEnabled(false);
                                        }
                                } catch (Exception e) {
                                        e.printStackTrace();
                                }
                        }
                        if (((CommandToGUI) arg).getDBError() == true) {
                                jScrollPane1.setEnabled(false);
                                answerField.setEnabled(false);
                                resultLabel.setText(gUI.getString("Database_error"));

                        } else {
                                updateProgressBar();
                                jScrollPane1.setEnabled(true);
                                answerField.setEnabled(true);
                        }
                }
        }

        public void updateProgressBar() {
                ErrorHandler.debug("GUI::updateProgressBar(): entered");
                int nowtat = 0;
                try {
                        nowtat = conn.getNumberOfWordsToAskToday();
                        wordCntLabel.setText(Integer.toString(nowtat));
                } catch (Exception e) {
                        this.resultLabel.setText("updateProgressBar() error");
                        e.printStackTrace();
                }
                askAgainL.setText(String.valueOf(conn.getWronglyAnsweredCnt()));
                int percent = ucitel.getPercentDone(nowtat);
                jProgressBar1.setString(Integer.toString(percent) + "%");
                jProgressBar1.setValue(percent);
                wordLeftLabel.setText(Integer.toString(ucitel.getQuestionsLeft()));

                int[] cnts = conn.getWordCnts(0, 20, direction);

                cnt0x.setText(String.valueOf(cnts[0]));
                cnt1x.setText(String.valueOf(cnts[1]));
                cnt2x.setText(String.valueOf(cnts[2]));
                cnt3x.setText(String.valueOf(cnts[3]));
                cnt4x.setText(String.valueOf(cnts[4]));
                cnt5x.setText(String.valueOf(cnts[5]));
                cnt6x.setText(String.valueOf(cnts[6]));
                cnt7x.setText(String.valueOf(cnts[7]));
                cnt8x.setText(String.valueOf(cnts[8]));
                cnt9x.setText(String.valueOf(cnts[9]));
                cnt10x.setText(String.valueOf(cnts[10]));
                cnt11x.setText(String.valueOf(cnts[11]));
                cnt12x.setText(String.valueOf(cnts[12]));
                cnt13x.setText(String.valueOf(cnts[13]));
                cnt14x.setText(String.valueOf(cnts[14]));
                cnt15x.setText(String.valueOf(cnts[15]));
                cnt16x.setText(String.valueOf(cnts[16]));
                cnt17x.setText(String.valueOf(cnts[17]));
                cnt18x.setText(String.valueOf(cnts[18]));
                cnt19x.setText(String.valueOf(cnts[19]));

                int count = ucitel.getAnswerCount();
                //int previousCount = ucitel.getPreviousAnswerCount();
                Font f = cnt0x.getFont();
                if (count == 0) {
                        cnt0x.setFont(f.deriveFont(Font.BOLD));
                        cnt1x.setFont(f.deriveFont(Font.PLAIN));
                        cnt2x.setFont(f.deriveFont(Font.PLAIN));
                        cnt3x.setFont(f.deriveFont(Font.PLAIN));
                        cnt4x.setFont(f.deriveFont(Font.PLAIN));
                        cnt5x.setFont(f.deriveFont(Font.PLAIN));
                        cnt6x.setFont(f.deriveFont(Font.PLAIN));
                        cnt7x.setFont(f.deriveFont(Font.PLAIN));
                        cnt8x.setFont(f.deriveFont(Font.PLAIN));
                        cnt9x.setFont(f.deriveFont(Font.PLAIN));
                        cnt10x.setFont(f.deriveFont(Font.PLAIN));
                        cnt11x.setFont(f.deriveFont(Font.PLAIN));
                        cnt12x.setFont(f.deriveFont(Font.PLAIN));
                        cnt13x.setFont(f.deriveFont(Font.PLAIN));
                        cnt14x.setFont(f.deriveFont(Font.PLAIN));
                        cnt15x.setFont(f.deriveFont(Font.PLAIN));
                        cnt16x.setFont(f.deriveFont(Font.PLAIN));
                        cnt17x.setFont(f.deriveFont(Font.PLAIN));
                        cnt18x.setFont(f.deriveFont(Font.PLAIN));
                        cnt19x.setFont(f.deriveFont(Font.PLAIN));
                } else if (count == 1) {
                        cnt0x.setFont(f.deriveFont(Font.PLAIN));
                        cnt1x.setFont(f.deriveFont(Font.BOLD));
                        cnt2x.setFont(f.deriveFont(Font.PLAIN));
                        cnt3x.setFont(f.deriveFont(Font.PLAIN));
                        cnt4x.setFont(f.deriveFont(Font.PLAIN));
                        cnt5x.setFont(f.deriveFont(Font.PLAIN));
                        cnt6x.setFont(f.deriveFont(Font.PLAIN));
                        cnt7x.setFont(f.deriveFont(Font.PLAIN));
                        cnt8x.setFont(f.deriveFont(Font.PLAIN));
                        cnt9x.setFont(f.deriveFont(Font.PLAIN));
                        cnt10x.setFont(f.deriveFont(Font.PLAIN));
                        cnt11x.setFont(f.deriveFont(Font.PLAIN));
                        cnt12x.setFont(f.deriveFont(Font.PLAIN));
                        cnt13x.setFont(f.deriveFont(Font.PLAIN));
                        cnt14x.setFont(f.deriveFont(Font.PLAIN));
                        cnt15x.setFont(f.deriveFont(Font.PLAIN));
                        cnt16x.setFont(f.deriveFont(Font.PLAIN));
                        cnt17x.setFont(f.deriveFont(Font.PLAIN));
                        cnt18x.setFont(f.deriveFont(Font.PLAIN));
                        cnt19x.setFont(f.deriveFont(Font.PLAIN));
                } else if (count == 2) {
                        cnt0x.setFont(f.deriveFont(Font.PLAIN));
                        cnt1x.setFont(f.deriveFont(Font.PLAIN));
                        cnt2x.setFont(f.deriveFont(Font.BOLD));
                        cnt3x.setFont(f.deriveFont(Font.PLAIN));
                        cnt4x.setFont(f.deriveFont(Font.PLAIN));
                        cnt5x.setFont(f.deriveFont(Font.PLAIN));
                        cnt6x.setFont(f.deriveFont(Font.PLAIN));
                        cnt7x.setFont(f.deriveFont(Font.PLAIN));
                        cnt8x.setFont(f.deriveFont(Font.PLAIN));
                        cnt9x.setFont(f.deriveFont(Font.PLAIN));
                        cnt10x.setFont(f.deriveFont(Font.PLAIN));
                        cnt11x.setFont(f.deriveFont(Font.PLAIN));
                        cnt12x.setFont(f.deriveFont(Font.PLAIN));
                        cnt13x.setFont(f.deriveFont(Font.PLAIN));
                        cnt14x.setFont(f.deriveFont(Font.PLAIN));
                        cnt15x.setFont(f.deriveFont(Font.PLAIN));
                        cnt16x.setFont(f.deriveFont(Font.PLAIN));
                        cnt17x.setFont(f.deriveFont(Font.PLAIN));
                        cnt18x.setFont(f.deriveFont(Font.PLAIN));
                        cnt19x.setFont(f.deriveFont(Font.PLAIN));
                } else if (count == 3) {
                        cnt0x.setFont(f.deriveFont(Font.PLAIN));
                        cnt1x.setFont(f.deriveFont(Font.PLAIN));
                        cnt2x.setFont(f.deriveFont(Font.PLAIN));
                        cnt3x.setFont(f.deriveFont(Font.BOLD));
                        cnt4x.setFont(f.deriveFont(Font.PLAIN));
                        cnt5x.setFont(f.deriveFont(Font.PLAIN));
                        cnt6x.setFont(f.deriveFont(Font.PLAIN));
                        cnt7x.setFont(f.deriveFont(Font.PLAIN));
                        cnt8x.setFont(f.deriveFont(Font.PLAIN));
                        cnt9x.setFont(f.deriveFont(Font.PLAIN));
                        cnt10x.setFont(f.deriveFont(Font.PLAIN));
                        cnt11x.setFont(f.deriveFont(Font.PLAIN));
                        cnt12x.setFont(f.deriveFont(Font.PLAIN));
                        cnt13x.setFont(f.deriveFont(Font.PLAIN));
                        cnt14x.setFont(f.deriveFont(Font.PLAIN));
                        cnt15x.setFont(f.deriveFont(Font.PLAIN));
                        cnt16x.setFont(f.deriveFont(Font.PLAIN));
                        cnt17x.setFont(f.deriveFont(Font.PLAIN));
                        cnt18x.setFont(f.deriveFont(Font.PLAIN));
                        cnt19x.setFont(f.deriveFont(Font.PLAIN));
                } else if (count == 4) {
                        cnt0x.setFont(f.deriveFont(Font.PLAIN));
                        cnt1x.setFont(f.deriveFont(Font.PLAIN));
                        cnt2x.setFont(f.deriveFont(Font.PLAIN));
                        cnt3x.setFont(f.deriveFont(Font.PLAIN));
                        cnt4x.setFont(f.deriveFont(Font.BOLD));
                        cnt5x.setFont(f.deriveFont(Font.PLAIN));
                        cnt6x.setFont(f.deriveFont(Font.PLAIN));
                        cnt7x.setFont(f.deriveFont(Font.PLAIN));
                        cnt8x.setFont(f.deriveFont(Font.PLAIN));
                        cnt9x.setFont(f.deriveFont(Font.PLAIN));
                        cnt10x.setFont(f.deriveFont(Font.PLAIN));
                        cnt11x.setFont(f.deriveFont(Font.PLAIN));
                        cnt12x.setFont(f.deriveFont(Font.PLAIN));
                        cnt13x.setFont(f.deriveFont(Font.PLAIN));
                        cnt14x.setFont(f.deriveFont(Font.PLAIN));
                        cnt15x.setFont(f.deriveFont(Font.PLAIN));
                        cnt16x.setFont(f.deriveFont(Font.PLAIN));
                        cnt17x.setFont(f.deriveFont(Font.PLAIN));
                        cnt18x.setFont(f.deriveFont(Font.PLAIN));
                        cnt19x.setFont(f.deriveFont(Font.PLAIN));
                } else if (count == 5) {
                        cnt0x.setFont(f.deriveFont(Font.PLAIN));
                        cnt1x.setFont(f.deriveFont(Font.PLAIN));
                        cnt2x.setFont(f.deriveFont(Font.PLAIN));
                        cnt3x.setFont(f.deriveFont(Font.PLAIN));
                        cnt4x.setFont(f.deriveFont(Font.PLAIN));
                        cnt5x.setFont(f.deriveFont(Font.BOLD));
                        cnt6x.setFont(f.deriveFont(Font.PLAIN));
                        cnt7x.setFont(f.deriveFont(Font.PLAIN));
                        cnt8x.setFont(f.deriveFont(Font.PLAIN));
                        cnt9x.setFont(f.deriveFont(Font.PLAIN));
                        cnt10x.setFont(f.deriveFont(Font.PLAIN));
                        cnt11x.setFont(f.deriveFont(Font.PLAIN));
                        cnt12x.setFont(f.deriveFont(Font.PLAIN));
                        cnt13x.setFont(f.deriveFont(Font.PLAIN));
                        cnt14x.setFont(f.deriveFont(Font.PLAIN));
                        cnt15x.setFont(f.deriveFont(Font.PLAIN));
                        cnt16x.setFont(f.deriveFont(Font.PLAIN));
                        cnt17x.setFont(f.deriveFont(Font.PLAIN));
                        cnt18x.setFont(f.deriveFont(Font.PLAIN));
                        cnt19x.setFont(f.deriveFont(Font.PLAIN));
                } else if (count == 6) {
                        cnt0x.setFont(f.deriveFont(Font.PLAIN));
                        cnt1x.setFont(f.deriveFont(Font.PLAIN));
                        cnt2x.setFont(f.deriveFont(Font.PLAIN));
                        cnt3x.setFont(f.deriveFont(Font.PLAIN));
                        cnt4x.setFont(f.deriveFont(Font.PLAIN));
                        cnt5x.setFont(f.deriveFont(Font.PLAIN));
                        cnt6x.setFont(f.deriveFont(Font.BOLD));
                        cnt7x.setFont(f.deriveFont(Font.PLAIN));
                        cnt8x.setFont(f.deriveFont(Font.PLAIN));
                        cnt9x.setFont(f.deriveFont(Font.PLAIN));
                        cnt10x.setFont(f.deriveFont(Font.PLAIN));
                        cnt11x.setFont(f.deriveFont(Font.PLAIN));
                        cnt12x.setFont(f.deriveFont(Font.PLAIN));
                        cnt13x.setFont(f.deriveFont(Font.PLAIN));
                        cnt14x.setFont(f.deriveFont(Font.PLAIN));
                        cnt15x.setFont(f.deriveFont(Font.PLAIN));
                        cnt16x.setFont(f.deriveFont(Font.PLAIN));
                        cnt17x.setFont(f.deriveFont(Font.PLAIN));
                        cnt18x.setFont(f.deriveFont(Font.PLAIN));
                        cnt19x.setFont(f.deriveFont(Font.PLAIN));
                } else if (count == 7) {
                        cnt0x.setFont(f.deriveFont(Font.PLAIN));
                        cnt1x.setFont(f.deriveFont(Font.PLAIN));
                        cnt2x.setFont(f.deriveFont(Font.PLAIN));
                        cnt3x.setFont(f.deriveFont(Font.PLAIN));
                        cnt4x.setFont(f.deriveFont(Font.PLAIN));
                        cnt5x.setFont(f.deriveFont(Font.PLAIN));
                        cnt6x.setFont(f.deriveFont(Font.PLAIN));
                        cnt7x.setFont(f.deriveFont(Font.BOLD));
                        cnt8x.setFont(f.deriveFont(Font.PLAIN));
                        cnt9x.setFont(f.deriveFont(Font.PLAIN));
                        cnt10x.setFont(f.deriveFont(Font.PLAIN));
                        cnt11x.setFont(f.deriveFont(Font.PLAIN));
                        cnt12x.setFont(f.deriveFont(Font.PLAIN));
                        cnt13x.setFont(f.deriveFont(Font.PLAIN));
                        cnt14x.setFont(f.deriveFont(Font.PLAIN));
                        cnt15x.setFont(f.deriveFont(Font.PLAIN));
                        cnt16x.setFont(f.deriveFont(Font.PLAIN));
                        cnt17x.setFont(f.deriveFont(Font.PLAIN));
                        cnt18x.setFont(f.deriveFont(Font.PLAIN));
                        cnt19x.setFont(f.deriveFont(Font.PLAIN));
                } else if (count == 8) {
                        cnt0x.setFont(f.deriveFont(Font.PLAIN));
                        cnt1x.setFont(f.deriveFont(Font.PLAIN));
                        cnt2x.setFont(f.deriveFont(Font.PLAIN));
                        cnt3x.setFont(f.deriveFont(Font.PLAIN));
                        cnt4x.setFont(f.deriveFont(Font.PLAIN));
                        cnt5x.setFont(f.deriveFont(Font.PLAIN));
                        cnt6x.setFont(f.deriveFont(Font.PLAIN));
                        cnt7x.setFont(f.deriveFont(Font.PLAIN));
                        cnt8x.setFont(f.deriveFont(Font.BOLD));
                        cnt9x.setFont(f.deriveFont(Font.PLAIN));
                        cnt10x.setFont(f.deriveFont(Font.PLAIN));
                        cnt11x.setFont(f.deriveFont(Font.PLAIN));
                        cnt12x.setFont(f.deriveFont(Font.PLAIN));
                        cnt13x.setFont(f.deriveFont(Font.PLAIN));
                        cnt14x.setFont(f.deriveFont(Font.PLAIN));
                        cnt15x.setFont(f.deriveFont(Font.PLAIN));
                        cnt16x.setFont(f.deriveFont(Font.PLAIN));
                        cnt17x.setFont(f.deriveFont(Font.PLAIN));
                        cnt18x.setFont(f.deriveFont(Font.PLAIN));
                        cnt19x.setFont(f.deriveFont(Font.PLAIN));
                } else if (count == 9) {
                        cnt0x.setFont(f.deriveFont(Font.PLAIN));
                        cnt1x.setFont(f.deriveFont(Font.PLAIN));
                        cnt2x.setFont(f.deriveFont(Font.PLAIN));
                        cnt3x.setFont(f.deriveFont(Font.PLAIN));
                        cnt4x.setFont(f.deriveFont(Font.PLAIN));
                        cnt5x.setFont(f.deriveFont(Font.PLAIN));
                        cnt6x.setFont(f.deriveFont(Font.PLAIN));
                        cnt7x.setFont(f.deriveFont(Font.PLAIN));
                        cnt8x.setFont(f.deriveFont(Font.PLAIN));
                        cnt9x.setFont(f.deriveFont(Font.BOLD));
                        cnt10x.setFont(f.deriveFont(Font.PLAIN));
                        cnt11x.setFont(f.deriveFont(Font.PLAIN));
                        cnt12x.setFont(f.deriveFont(Font.PLAIN));
                        cnt13x.setFont(f.deriveFont(Font.PLAIN));
                        cnt14x.setFont(f.deriveFont(Font.PLAIN));
                        cnt15x.setFont(f.deriveFont(Font.PLAIN));
                        cnt16x.setFont(f.deriveFont(Font.PLAIN));
                        cnt17x.setFont(f.deriveFont(Font.PLAIN));
                        cnt18x.setFont(f.deriveFont(Font.PLAIN));
                        cnt19x.setFont(f.deriveFont(Font.PLAIN));
                } else if (count == 10) {
                        cnt0x.setFont(f.deriveFont(Font.PLAIN));
                        cnt1x.setFont(f.deriveFont(Font.PLAIN));
                        cnt2x.setFont(f.deriveFont(Font.PLAIN));
                        cnt3x.setFont(f.deriveFont(Font.PLAIN));
                        cnt4x.setFont(f.deriveFont(Font.PLAIN));
                        cnt5x.setFont(f.deriveFont(Font.PLAIN));
                        cnt6x.setFont(f.deriveFont(Font.PLAIN));
                        cnt7x.setFont(f.deriveFont(Font.PLAIN));
                        cnt8x.setFont(f.deriveFont(Font.PLAIN));
                        cnt9x.setFont(f.deriveFont(Font.PLAIN));
                        cnt10x.setFont(f.deriveFont(Font.BOLD));
                        cnt11x.setFont(f.deriveFont(Font.PLAIN));
                        cnt12x.setFont(f.deriveFont(Font.PLAIN));
                        cnt13x.setFont(f.deriveFont(Font.PLAIN));
                        cnt14x.setFont(f.deriveFont(Font.PLAIN));
                        cnt15x.setFont(f.deriveFont(Font.PLAIN));
                        cnt16x.setFont(f.deriveFont(Font.PLAIN));
                        cnt17x.setFont(f.deriveFont(Font.PLAIN));
                        cnt18x.setFont(f.deriveFont(Font.PLAIN));
                        cnt19x.setFont(f.deriveFont(Font.PLAIN));
                } else if (count == 11) {
                        cnt0x.setFont(f.deriveFont(Font.PLAIN));
                        cnt1x.setFont(f.deriveFont(Font.PLAIN));
                        cnt2x.setFont(f.deriveFont(Font.PLAIN));
                        cnt3x.setFont(f.deriveFont(Font.PLAIN));
                        cnt4x.setFont(f.deriveFont(Font.PLAIN));
                        cnt5x.setFont(f.deriveFont(Font.PLAIN));
                        cnt6x.setFont(f.deriveFont(Font.PLAIN));
                        cnt7x.setFont(f.deriveFont(Font.PLAIN));
                        cnt8x.setFont(f.deriveFont(Font.PLAIN));
                        cnt9x.setFont(f.deriveFont(Font.PLAIN));
                        cnt10x.setFont(f.deriveFont(Font.PLAIN));
                        cnt11x.setFont(f.deriveFont(Font.BOLD));
                        cnt12x.setFont(f.deriveFont(Font.PLAIN));
                        cnt13x.setFont(f.deriveFont(Font.PLAIN));
                        cnt14x.setFont(f.deriveFont(Font.PLAIN));
                        cnt15x.setFont(f.deriveFont(Font.PLAIN));
                        cnt16x.setFont(f.deriveFont(Font.PLAIN));
                        cnt17x.setFont(f.deriveFont(Font.PLAIN));
                        cnt18x.setFont(f.deriveFont(Font.PLAIN));
                        cnt19x.setFont(f.deriveFont(Font.PLAIN));
                } else if (count == 12) {
                        cnt0x.setFont(f.deriveFont(Font.PLAIN));
                        cnt1x.setFont(f.deriveFont(Font.PLAIN));
                        cnt2x.setFont(f.deriveFont(Font.PLAIN));
                        cnt3x.setFont(f.deriveFont(Font.PLAIN));
                        cnt4x.setFont(f.deriveFont(Font.PLAIN));
                        cnt5x.setFont(f.deriveFont(Font.PLAIN));
                        cnt6x.setFont(f.deriveFont(Font.PLAIN));
                        cnt7x.setFont(f.deriveFont(Font.PLAIN));
                        cnt8x.setFont(f.deriveFont(Font.PLAIN));
                        cnt9x.setFont(f.deriveFont(Font.PLAIN));
                        cnt10x.setFont(f.deriveFont(Font.PLAIN));
                        cnt11x.setFont(f.deriveFont(Font.PLAIN));
                        cnt12x.setFont(f.deriveFont(Font.BOLD));
                        cnt13x.setFont(f.deriveFont(Font.PLAIN));
                        cnt14x.setFont(f.deriveFont(Font.PLAIN));
                        cnt15x.setFont(f.deriveFont(Font.PLAIN));
                        cnt16x.setFont(f.deriveFont(Font.PLAIN));
                        cnt17x.setFont(f.deriveFont(Font.PLAIN));
                        cnt18x.setFont(f.deriveFont(Font.PLAIN));
                        cnt19x.setFont(f.deriveFont(Font.PLAIN));
                } else if (count == 13) {
                        cnt0x.setFont(f.deriveFont(Font.PLAIN));
                        cnt1x.setFont(f.deriveFont(Font.PLAIN));
                        cnt2x.setFont(f.deriveFont(Font.PLAIN));
                        cnt3x.setFont(f.deriveFont(Font.PLAIN));
                        cnt4x.setFont(f.deriveFont(Font.PLAIN));
                        cnt5x.setFont(f.deriveFont(Font.PLAIN));
                        cnt6x.setFont(f.deriveFont(Font.PLAIN));
                        cnt7x.setFont(f.deriveFont(Font.PLAIN));
                        cnt8x.setFont(f.deriveFont(Font.PLAIN));
                        cnt9x.setFont(f.deriveFont(Font.PLAIN));
                        cnt10x.setFont(f.deriveFont(Font.PLAIN));
                        cnt11x.setFont(f.deriveFont(Font.PLAIN));
                        cnt12x.setFont(f.deriveFont(Font.PLAIN));
                        cnt13x.setFont(f.deriveFont(Font.BOLD));
                        cnt14x.setFont(f.deriveFont(Font.PLAIN));
                        cnt15x.setFont(f.deriveFont(Font.PLAIN));
                        cnt16x.setFont(f.deriveFont(Font.PLAIN));
                        cnt17x.setFont(f.deriveFont(Font.PLAIN));
                        cnt18x.setFont(f.deriveFont(Font.PLAIN));
                        cnt19x.setFont(f.deriveFont(Font.PLAIN));
                } else if (count == 14) {
                        cnt0x.setFont(f.deriveFont(Font.PLAIN));
                        cnt1x.setFont(f.deriveFont(Font.PLAIN));
                        cnt2x.setFont(f.deriveFont(Font.PLAIN));
                        cnt3x.setFont(f.deriveFont(Font.PLAIN));
                        cnt4x.setFont(f.deriveFont(Font.PLAIN));
                        cnt5x.setFont(f.deriveFont(Font.PLAIN));
                        cnt6x.setFont(f.deriveFont(Font.PLAIN));
                        cnt7x.setFont(f.deriveFont(Font.PLAIN));
                        cnt8x.setFont(f.deriveFont(Font.PLAIN));
                        cnt9x.setFont(f.deriveFont(Font.PLAIN));
                        cnt10x.setFont(f.deriveFont(Font.PLAIN));
                        cnt11x.setFont(f.deriveFont(Font.PLAIN));
                        cnt12x.setFont(f.deriveFont(Font.PLAIN));
                        cnt13x.setFont(f.deriveFont(Font.PLAIN));
                        cnt14x.setFont(f.deriveFont(Font.BOLD));
                        cnt15x.setFont(f.deriveFont(Font.PLAIN));
                        cnt16x.setFont(f.deriveFont(Font.PLAIN));
                        cnt17x.setFont(f.deriveFont(Font.PLAIN));
                        cnt18x.setFont(f.deriveFont(Font.PLAIN));
                        cnt19x.setFont(f.deriveFont(Font.PLAIN));
                } else if (count == 15) {
                        cnt0x.setFont(f.deriveFont(Font.PLAIN));
                        cnt1x.setFont(f.deriveFont(Font.PLAIN));
                        cnt2x.setFont(f.deriveFont(Font.PLAIN));
                        cnt3x.setFont(f.deriveFont(Font.PLAIN));
                        cnt4x.setFont(f.deriveFont(Font.PLAIN));
                        cnt5x.setFont(f.deriveFont(Font.PLAIN));
                        cnt6x.setFont(f.deriveFont(Font.PLAIN));
                        cnt7x.setFont(f.deriveFont(Font.PLAIN));
                        cnt8x.setFont(f.deriveFont(Font.PLAIN));
                        cnt9x.setFont(f.deriveFont(Font.PLAIN));
                        cnt10x.setFont(f.deriveFont(Font.PLAIN));
                        cnt11x.setFont(f.deriveFont(Font.PLAIN));
                        cnt12x.setFont(f.deriveFont(Font.PLAIN));
                        cnt13x.setFont(f.deriveFont(Font.PLAIN));
                        cnt14x.setFont(f.deriveFont(Font.PLAIN));
                        cnt15x.setFont(f.deriveFont(Font.BOLD));
                        cnt16x.setFont(f.deriveFont(Font.PLAIN));
                        cnt17x.setFont(f.deriveFont(Font.PLAIN));
                        cnt18x.setFont(f.deriveFont(Font.PLAIN));
                        cnt19x.setFont(f.deriveFont(Font.PLAIN));
                } else if (count == 16) {
                        cnt0x.setFont(f.deriveFont(Font.PLAIN));
                        cnt1x.setFont(f.deriveFont(Font.PLAIN));
                        cnt2x.setFont(f.deriveFont(Font.PLAIN));
                        cnt3x.setFont(f.deriveFont(Font.PLAIN));
                        cnt4x.setFont(f.deriveFont(Font.PLAIN));
                        cnt5x.setFont(f.deriveFont(Font.PLAIN));
                        cnt6x.setFont(f.deriveFont(Font.PLAIN));
                        cnt7x.setFont(f.deriveFont(Font.PLAIN));
                        cnt8x.setFont(f.deriveFont(Font.PLAIN));
                        cnt9x.setFont(f.deriveFont(Font.PLAIN));
                        cnt10x.setFont(f.deriveFont(Font.PLAIN));
                        cnt11x.setFont(f.deriveFont(Font.PLAIN));
                        cnt12x.setFont(f.deriveFont(Font.PLAIN));
                        cnt13x.setFont(f.deriveFont(Font.PLAIN));
                        cnt14x.setFont(f.deriveFont(Font.PLAIN));
                        cnt15x.setFont(f.deriveFont(Font.PLAIN));
                        cnt16x.setFont(f.deriveFont(Font.BOLD));
                        cnt17x.setFont(f.deriveFont(Font.PLAIN));
                        cnt18x.setFont(f.deriveFont(Font.PLAIN));
                        cnt19x.setFont(f.deriveFont(Font.PLAIN));
                } else if (count == 17) {
                        cnt0x.setFont(f.deriveFont(Font.PLAIN));
                        cnt1x.setFont(f.deriveFont(Font.PLAIN));
                        cnt2x.setFont(f.deriveFont(Font.PLAIN));
                        cnt3x.setFont(f.deriveFont(Font.PLAIN));
                        cnt4x.setFont(f.deriveFont(Font.PLAIN));
                        cnt5x.setFont(f.deriveFont(Font.PLAIN));
                        cnt6x.setFont(f.deriveFont(Font.PLAIN));
                        cnt7x.setFont(f.deriveFont(Font.PLAIN));
                        cnt8x.setFont(f.deriveFont(Font.PLAIN));
                        cnt9x.setFont(f.deriveFont(Font.PLAIN));
                        cnt10x.setFont(f.deriveFont(Font.PLAIN));
                        cnt11x.setFont(f.deriveFont(Font.PLAIN));
                        cnt12x.setFont(f.deriveFont(Font.PLAIN));
                        cnt13x.setFont(f.deriveFont(Font.PLAIN));
                        cnt14x.setFont(f.deriveFont(Font.PLAIN));
                        cnt15x.setFont(f.deriveFont(Font.PLAIN));
                        cnt16x.setFont(f.deriveFont(Font.PLAIN));
                        cnt17x.setFont(f.deriveFont(Font.BOLD));
                        cnt18x.setFont(f.deriveFont(Font.PLAIN));
                        cnt19x.setFont(f.deriveFont(Font.PLAIN));
                } else if (count == 18) {
                        cnt0x.setFont(f.deriveFont(Font.PLAIN));
                        cnt1x.setFont(f.deriveFont(Font.PLAIN));
                        cnt2x.setFont(f.deriveFont(Font.PLAIN));
                        cnt3x.setFont(f.deriveFont(Font.PLAIN));
                        cnt4x.setFont(f.deriveFont(Font.PLAIN));
                        cnt5x.setFont(f.deriveFont(Font.PLAIN));
                        cnt6x.setFont(f.deriveFont(Font.PLAIN));
                        cnt7x.setFont(f.deriveFont(Font.PLAIN));
                        cnt8x.setFont(f.deriveFont(Font.PLAIN));
                        cnt9x.setFont(f.deriveFont(Font.PLAIN));
                        cnt10x.setFont(f.deriveFont(Font.PLAIN));
                        cnt11x.setFont(f.deriveFont(Font.PLAIN));
                        cnt12x.setFont(f.deriveFont(Font.PLAIN));
                        cnt13x.setFont(f.deriveFont(Font.PLAIN));
                        cnt14x.setFont(f.deriveFont(Font.PLAIN));
                        cnt15x.setFont(f.deriveFont(Font.PLAIN));
                        cnt16x.setFont(f.deriveFont(Font.PLAIN));
                        cnt17x.setFont(f.deriveFont(Font.PLAIN));
                        cnt18x.setFont(f.deriveFont(Font.BOLD));
                        cnt19x.setFont(f.deriveFont(Font.PLAIN));
                } else if (count == 18) {
                        cnt0x.setFont(f.deriveFont(Font.PLAIN));
                        cnt1x.setFont(f.deriveFont(Font.PLAIN));
                        cnt2x.setFont(f.deriveFont(Font.PLAIN));
                        cnt3x.setFont(f.deriveFont(Font.PLAIN));
                        cnt4x.setFont(f.deriveFont(Font.PLAIN));
                        cnt5x.setFont(f.deriveFont(Font.PLAIN));
                        cnt6x.setFont(f.deriveFont(Font.PLAIN));
                        cnt7x.setFont(f.deriveFont(Font.PLAIN));
                        cnt8x.setFont(f.deriveFont(Font.PLAIN));
                        cnt9x.setFont(f.deriveFont(Font.PLAIN));
                        cnt10x.setFont(f.deriveFont(Font.PLAIN));
                        cnt11x.setFont(f.deriveFont(Font.PLAIN));
                        cnt12x.setFont(f.deriveFont(Font.PLAIN));
                        cnt13x.setFont(f.deriveFont(Font.PLAIN));
                        cnt14x.setFont(f.deriveFont(Font.PLAIN));
                        cnt15x.setFont(f.deriveFont(Font.PLAIN));
                        cnt16x.setFont(f.deriveFont(Font.PLAIN));
                        cnt17x.setFont(f.deriveFont(Font.PLAIN));
                        cnt18x.setFont(f.deriveFont(Font.PLAIN));
                        cnt19x.setFont(f.deriveFont(Font.BOLD));
                }

                ErrorHandler.debug("GUI::updateProgressBar(): finished");
        }

        public boolean getGrammar() {
                if (this.grammar_initialized == true) {
                        if (grammarCB.isSelected()) {
                                return true;
                        } else {
                                return false;
                        }
                } else {
                        if (XML.getInstance().getGrammar().equals(XML.getInstance().grammar_true)) {
                                return true;
                        } else {
                                return false;
                        }
                }


        }

        public void loadThemes() {

                try {
                        themeCB.removeAllItems();
                        themeCB.addItem((String) vsechno);

                        ArrayList<String> themes = ConnectionToDB.getInstance().getThemes();
                        Iterator i = themes.iterator();
                        while (true == i.hasNext()) {
                                themeCB.addItem(i.next());
                        }
                } catch (Exception e) {
                        JOptionPane.showMessageDialog(GUI.getInstance().getComponent(), e.toString(), "Error", JOptionPane.OK_OPTION);
                        e.toString();
                        e.printStackTrace();
                }

                themeCB.setSelectedItem(XML.getInstance().getLastTheme());
                themes_loaded = true;
        }
        XML xml;

        private GUI(SingleFrameApplication app) {
                super(app);
                initComponents();
                xml = XML.getInstance();
                //ucitel = Teacher.getInstance();
                this.getFrame().setResizable(false);

                direction = GuiDirection.getInstance();
                conn = ConnectionToDB.getInstance();

                languageCB.setModel(new DefaultComboBoxModel(Language.values()));
                languageCB.removeItem(Language.CZECH);
                xml = XML.getInstance();
                if (direction.isFromCzech()) {
                        languageCB.setSelectedItem(xml.getDirection().getLngTo());
                        fromCzechRB.doClick();
                } else {
                        languageCB.setSelectedItem(xml.getDirection().getLngFrom());
                        toCzechRB.doClick();
                }
                try {
                        //LoginInfo li = new LoginInfo();
                        if (this.conn.connect()) {
                                this.userLogged = true;
                        }
                } catch (Exception ex) {
                        Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                }

                uniqueInstance = this;

        }

        public boolean connectToDB(LoginInfo li) {
                ErrorHandler.debug("GUI:connectToDB() entered");

                try {
                        ConnectionToDB.close();
                        conn = ConnectionToDB.getInstance(li);
                        ErrorHandler.debug("GUI:connectToDB() returning true");
                        return true;
                } catch (SQLException e) {
                        answerField.setEnabled(false);
                        jScrollPane1.setEnabled(false);
                        //statusLabel.setText(gUI.getString("Database_connection_failed,_please_set_the_correct_settings"));
                        ErrorHandler.debug("GUI:connectToDB() database error");
                        JOptionPane.showMessageDialog(GUI.getInstance().getComponent(), e.getMessage(), gUI.getString("Database_error"), JOptionPane.OK_OPTION);
                        return false;
                } catch (IOException e) {
                        ErrorHandler.debug("GUI:connectToDB() login failed");
                        JOptionPane.showMessageDialog(GUI.getInstance().getComponent(), e.toString(), gUI.getString("Login_incorrect"), JOptionPane.OK_OPTION);
                        return false;
                } catch (Exception e) {
                        ErrorHandler.debug("GUI:connectToDB() wrong login data");
                        JOptionPane.showMessageDialog(GUI.getInstance().getComponent(), gUI.getString("Please_check_login_data"), gUI.getString("Login_incorrect"), JOptionPane.OK_OPTION);
                        System.exit(-1);
                        return false;
                }
        }

        public void login() {
                JFrame mainFrame = Ucitel.getApplication().getMainFrame();

                LoginForm lf = new LoginForm(mainFrame);

                lf.login();
        }

        public void showLogin() {
                JFrame mainFrame = Ucitel.getApplication().getMainFrame();

                LoginForm lf = new LoginForm(mainFrame);
                lf.setLocationRelativeTo(mainFrame);
                lf.setVisible(true);
                lf.login();

                lf.dispose();

        }

        public void setUcitel(Teacher teacher) {
                ucitel = teacher;
        }

        /** This method is called from within the constructor to
         * initialize the form.
         * WARNING: Do NOT modify this code. The content of this method is
         * always regenerated by the Form Editor.
         */
        @SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
        private void initComponents() {

                mainPanel = new javax.swing.JPanel();
                jPanel2 = new javax.swing.JPanel();
                wordLeftLabel = new javax.swing.JLabel();
                wordCntLabel = new javax.swing.JLabel();
                jProgressBar1 = new javax.swing.JProgressBar();
                themeCB = new javax.swing.JComboBox();
                jPanel1 = new javax.swing.JPanel();
                jLabel1 = new javax.swing.JLabel();
                jScrollPane4 = new javax.swing.JScrollPane();
                hint = new javax.swing.JTextArea();
                jPanel5 = new javax.swing.JPanel();
                jScrollPane3 = new javax.swing.JScrollPane();
                resultLabel = new javax.swing.JTextArea();
                jPanel6 = new javax.swing.JPanel();
                askButton = new javax.swing.JButton();
                clear = new javax.swing.JButton();
                askAgainL = new javax.swing.JLabel();
                jPanel8 = new javax.swing.JPanel();
                swapButton = new javax.swing.JButton();
                deleteCurrentButton = new javax.swing.JButton();
                fixCurrentButton = new javax.swing.JButton();
                fixButton = new javax.swing.JButton();
                jButton1 = new javax.swing.JButton();
                jPanel9 = new javax.swing.JPanel();
                grammarCB = new javax.swing.JCheckBox();
                fromCzechRB = new javax.swing.JRadioButton();
                toCzechRB = new javax.swing.JRadioButton();
                languageCB = new javax.swing.JComboBox();
                jPanel7 = new javax.swing.JPanel();
                jPanel3 = new javax.swing.JPanel();
                questionLabel = new javax.swing.JLabel();
                jScrollPane1 = new javax.swing.JScrollPane();
                questionField = new javax.swing.JTextArea();
                jPanel14 = new javax.swing.JPanel();
                answerLabel = new javax.swing.JLabel();
                jScrollPane2 = new javax.swing.JScrollPane();
                answerField = new javax.swing.JTextArea();
                jPanel10 = new javax.swing.JPanel();
                cnt1xLabel = new javax.swing.JLabel();
                cnt2xLabel = new javax.swing.JLabel();
                cnt3xLabel = new javax.swing.JLabel();
                cnt1x = new javax.swing.JLabel();
                cnt2x = new javax.swing.JLabel();
                cnt3x = new javax.swing.JLabel();
                cnt0xLabel = new javax.swing.JLabel();
                cnt0x = new javax.swing.JLabel();
                jPanel11 = new javax.swing.JPanel();
                jLabel6 = new javax.swing.JLabel();
                cnt5x = new javax.swing.JLabel();
                jLabel5 = new javax.swing.JLabel();
                cnt4x = new javax.swing.JLabel();
                cnt6xLabel = new javax.swing.JLabel();
                cnt7xLabel = new javax.swing.JLabel();
                cnt7x = new javax.swing.JLabel();
                cnt6x = new javax.swing.JLabel();
                jPanel12 = new javax.swing.JPanel();
                cnt8xLabel = new javax.swing.JLabel();
                cnt8x = new javax.swing.JLabel();
                cnt9xLabel = new javax.swing.JLabel();
                cnt10x = new javax.swing.JLabel();
                cnt9x = new javax.swing.JLabel();
                cnt10xLabel = new javax.swing.JLabel();
                jLabel22 = new javax.swing.JLabel();
                cnt11x = new javax.swing.JLabel();
                jPanel13 = new javax.swing.JPanel();
                cnt13x = new javax.swing.JLabel();
                jLabel24 = new javax.swing.JLabel();
                jLabel25 = new javax.swing.JLabel();
                jLabel26 = new javax.swing.JLabel();
                cnt15x = new javax.swing.JLabel();
                cnt14x = new javax.swing.JLabel();
                jLabel23 = new javax.swing.JLabel();
                cnt12x = new javax.swing.JLabel();
                jPanel4 = new javax.swing.JPanel();
                cnt16xLabel = new javax.swing.JLabel();
                cnt16x = new javax.swing.JLabel();
                cnt17xLabel = new javax.swing.JLabel();
                cnt17x = new javax.swing.JLabel();
                cnt18xLabel = new javax.swing.JLabel();
                cnt18x = new javax.swing.JLabel();
                jLabel2 = new javax.swing.JLabel();
                cnt19x = new javax.swing.JLabel();
                menuBar = new javax.swing.JMenuBar();
                javax.swing.JMenu fileMenu = new javax.swing.JMenu();
                tisk = new javax.swing.JMenuItem();
                changeUser = new javax.swing.JMenuItem();
                importItem = new javax.swing.JMenuItem();
                export = new javax.swing.JMenuItem();
                javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
                jMenu2 = new javax.swing.JMenu();
                jMenuItem2 = new javax.swing.JMenuItem();
                slovnikMenu = new javax.swing.JMenu();
                jMenuItem5 = new javax.swing.JMenuItem();
                jMenuItem1 = new javax.swing.JMenuItem();
                jMenuItem3 = new javax.swing.JMenuItem();
                jMenuItem4 = new javax.swing.JMenuItem();
                smazatSlovicka = new javax.swing.JMenuItem();
                javax.swing.JMenu helpMenu = new javax.swing.JMenu();
                javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
                directionBG = new javax.swing.ButtonGroup();
                languagesBG = new javax.swing.ButtonGroup();

                mainPanel.setAutoscrolls(true);
                mainPanel.setMaximumSize(new java.awt.Dimension(570, 550));
                mainPanel.setMinimumSize(new java.awt.Dimension(570, 570));
                mainPanel.setName("mainPanel"); // NOI18N
                mainPanel.setPreferredSize(new java.awt.Dimension(620, 580));
                mainPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
                        public void componentShown(java.awt.event.ComponentEvent evt) {
                                mainPanelComponentShown(evt);
                        }
                });
                mainPanel.addFocusListener(new java.awt.event.FocusAdapter() {
                        public void focusGained(java.awt.event.FocusEvent evt) {
                                mainPanelFocusGained(evt);
                        }
                });
                mainPanel.addHierarchyListener(new java.awt.event.HierarchyListener() {
                        public void hierarchyChanged(java.awt.event.HierarchyEvent evt) {
                                mainPanelHierarchyChanged(evt);
                        }
                });
                mainPanel.addVetoableChangeListener(new java.beans.VetoableChangeListener() {
                        public void vetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {
                                mainPanelVetoableChange(evt);
                        }
                });

                jPanel2.setMaximumSize(new java.awt.Dimension(537, 32767));
                jPanel2.setMinimumSize(new java.awt.Dimension(537, 0));
                jPanel2.setName("jPanel2"); // NOI18N
                jPanel2.setPreferredSize(new java.awt.Dimension(537, 300));

                wordLeftLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
                org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ucitel.Ucitel.class).getContext().getResourceMap(GUI.class);
                wordLeftLabel.setText(resourceMap.getString("wordLeftLabel.text")); // NOI18N
                wordLeftLabel.setName("wordLeftLabel"); // NOI18N

                wordCntLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
                wordCntLabel.setText(resourceMap.getString("wordCntLabel.text")); // NOI18N
                wordCntLabel.setName("wordCntLabel"); // NOI18N

                jProgressBar1.setBackground(resourceMap.getColor("jProgressBar1.background")); // NOI18N
                jProgressBar1.setForeground(resourceMap.getColor("jProgressBar1.foreground")); // NOI18N
                jProgressBar1.setOrientation(SwingConstants.HORIZONTAL);
                jProgressBar1.setToolTipText(resourceMap.getString("jProgressBar1.toolTipText")); // NOI18N
                jProgressBar1.setMaximumSize(new java.awt.Dimension(28, 16));
                jProgressBar1.setName("jProgressBar1"); // NOI18N
                jProgressBar1.setPreferredSize(new java.awt.Dimension(40, 16));
                jProgressBar1.setString(resourceMap.getString("jProgressBar1.string")); // NOI18N
                jProgressBar1.setStringPainted(true);

                themeCB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Všechno" }));
                themeCB.setEnabled(false);
                themeCB.setName("themeCB"); // NOI18N
                themeCB.addItemListener(new java.awt.event.ItemListener() {
                        public void itemStateChanged(java.awt.event.ItemEvent evt) {
                                themeCBItemStateChanged(evt);
                        }
                });

                javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
                jPanel2.setLayout(jPanel2Layout);
                jPanel2Layout.setHorizontalGroup(
                        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 463, Short.MAX_VALUE)
                                                .addGroup(jPanel2Layout.createSequentialGroup()
                                                        .addComponent(themeCB, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 272, Short.MAX_VALUE)
                                                        .addComponent(wordCntLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addComponent(wordLeftLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                );
                jPanel2Layout.setVerticalGroup(
                        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(wordLeftLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(themeCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(wordCntLabel))
                                .addGap(9, 9, 9))
                );

                jPanel1.setMaximumSize(new java.awt.Dimension(537, 99));
                jPanel1.setName("jPanel1"); // NOI18N
                jPanel1.setPreferredSize(new java.awt.Dimension(537, 99));

                jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
                jLabel1.setName("jLabel1"); // NOI18N

                jScrollPane4.setName("jScrollPane4"); // NOI18N

                hint.setColumns(10);
                hint.setFont(resourceMap.getFont("hint.font")); // NOI18N
                hint.setLineWrap(true);
                hint.setRows(3);
                hint.setWrapStyleWord(true);
                hint.setMaximumSize(new java.awt.Dimension(102, 19));
                hint.setName("hint"); // NOI18N
                hint.addKeyListener(new java.awt.event.KeyAdapter() {
                        public void keyPressed(java.awt.event.KeyEvent evt) {
                                hintKeyPressed(evt);
                        }
                        public void keyReleased(java.awt.event.KeyEvent evt) {
                                hintKeyReleased(evt);
                        }
                });
                jScrollPane4.setViewportView(hint);

                javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
                jPanel1.setLayout(jPanel1Layout);
                jPanel1Layout.setHorizontalGroup(
                        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addContainerGap(547, Short.MAX_VALUE))
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
                );
                jPanel1Layout.setVerticalGroup(
                        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE))
                );

                jPanel5.setMaximumSize(new java.awt.Dimension(537, 146));
                jPanel5.setMinimumSize(new java.awt.Dimension(537, 146));
                jPanel5.setName("jPanel5"); // NOI18N
                jPanel5.setPreferredSize(new java.awt.Dimension(537, 146));

                jScrollPane3.setName("jScrollPane3"); // NOI18N

                resultLabel.setBackground(resourceMap.getColor("resultLabel.background")); // NOI18N
                resultLabel.setColumns(20);
                resultLabel.setEditable(false);
                resultLabel.setFont(resourceMap.getFont("resultLabel.font")); // NOI18N
                resultLabel.setLineWrap(true);
                resultLabel.setRows(2);
                resultLabel.setWrapStyleWord(true);
                resultLabel.setMaximumSize(new java.awt.Dimension(537, 2147483647));
                resultLabel.setMinimumSize(new java.awt.Dimension(537, 24));
                resultLabel.setName("resultLabel"); // NOI18N
                resultLabel.setPreferredSize(new java.awt.Dimension(537, 44));
                jScrollPane3.setViewportView(resultLabel);

                javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
                jPanel5.setLayout(jPanel5Layout);
                jPanel5Layout.setHorizontalGroup(
                        jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 600, Short.MAX_VALUE)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE))
                );
                jPanel5Layout.setVerticalGroup(
                        jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 62, Short.MAX_VALUE)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE))
                );

                jPanel6.setName("jPanel6"); // NOI18N

                askButton.setText(resourceMap.getString("askButton.text")); // NOI18N
                askButton.setName("askButton"); // NOI18N
                askButton.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                askButtonActionPerformed(evt);
                        }
                });

                javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(ucitel.Ucitel.class).getContext().getActionMap(GUI.class, this);
                clear.setAction(actionMap.get("clean")); // NOI18N
                clear.setText(resourceMap.getString("clear.text")); // NOI18N
                clear.setName("clear"); // NOI18N

                askAgainL.setText(resourceMap.getString("askAgainL.text")); // NOI18N
                askAgainL.setName("askAgainL"); // NOI18N

                javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
                jPanel6.setLayout(jPanel6Layout);
                jPanel6Layout.setHorizontalGroup(
                        jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(askButton, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(askAgainL, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 231, Short.MAX_VALUE)
                                .addComponent(clear))
                );
                jPanel6Layout.setVerticalGroup(
                        jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(askButton)
                                .addComponent(clear)
                                .addComponent(askAgainL))
                );

                jPanel8.setName("jPanel8"); // NOI18N

                swapButton.setAction(actionMap.get("swap")); // NOI18N
                swapButton.setText(resourceMap.getString("swapButton.text")); // NOI18N
                swapButton.setName("swapButton"); // NOI18N

                deleteCurrentButton.setAction(actionMap.get("deleteCurrent")); // NOI18N
                deleteCurrentButton.setText(resourceMap.getString("deleteCurrentButton.text")); // NOI18N
                deleteCurrentButton.setToolTipText(resourceMap.getString("deleteCurrentButton.toolTipText")); // NOI18N
                deleteCurrentButton.setName("deleteCurrentButton"); // NOI18N

                fixCurrentButton.setText(resourceMap.getString("fixCurrentButton.text")); // NOI18N
                fixCurrentButton.setName("fixCurrentButton"); // NOI18N
                fixCurrentButton.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                fixCurrentButtonActionPerformed(evt);
                        }
                });

                fixButton.setText(resourceMap.getString("fixButton.text")); // NOI18N
                fixButton.setAlignmentY(0.0F);
                fixButton.setEnabled(false);
                fixButton.setName("fixButton"); // NOI18N
                fixButton.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                fixButtonActionPerformed(evt);
                        }
                });

                jButton1.setAction(actionMap.get("quit")); // NOI18N
                jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
                jButton1.setName("jButton1"); // NOI18N

                javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
                jPanel8.setLayout(jPanel8Layout);
                jPanel8Layout.setHorizontalGroup(
                        jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel8Layout.createSequentialGroup()
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel8Layout.createSequentialGroup()
                                                .addGap(123, 123, 123)
                                                .addComponent(fixCurrentButton, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(deleteCurrentButton, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(swapButton, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(fixButton))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton1)
                                .addContainerGap(133, Short.MAX_VALUE))
                );
                jPanel8Layout.setVerticalGroup(
                        jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(fixButton)
                                .addComponent(fixCurrentButton)
                                .addComponent(deleteCurrentButton)
                                .addComponent(swapButton)
                                .addComponent(jButton1))
                );

                jPanel9.setName("jPanel9"); // NOI18N

                grammarCB.setText(resourceMap.getString("grammarCB.text")); // NOI18N
                grammarCB.setName("grammarCB"); // NOI18N
                grammarCB.addChangeListener(new javax.swing.event.ChangeListener() {
                        public void stateChanged(javax.swing.event.ChangeEvent evt) {
                                grammarCBStateChanged(evt);
                        }
                });

                directionBG.add(fromCzechRB);
                fromCzechRB.setText(resourceMap.getString("fromCzechRB.text")); // NOI18N
                fromCzechRB.setName("fromCzechRB"); // NOI18N
                fromCzechRB.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                fromCzechRBActionPerformed(evt);
                        }
                });

                directionBG.add(toCzechRB);
                toCzechRB.setSelected(true);
                toCzechRB.setText(resourceMap.getString("toCzechRB.text")); // NOI18N
                toCzechRB.setName("toCzechRB"); // NOI18N
                toCzechRB.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                toCzechRBActionPerformed(evt);
                        }
                });

                languageCB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Angličtina" }));
                languageCB.setName("languageCB"); // NOI18N
                languageCB.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                languageCBActionPerformed(evt);
                        }
                });

                javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
                jPanel9.setLayout(jPanel9Layout);
                jPanel9Layout.setHorizontalGroup(
                        jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel9Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(languageCB, 0, 111, Short.MAX_VALUE)
                                        .addGroup(jPanel9Layout.createSequentialGroup()
                                                .addComponent(grammarCB)
                                                .addGap(20, 20, 20))
                                        .addComponent(fromCzechRB)
                                        .addComponent(toCzechRB))
                                .addContainerGap())
                );
                jPanel9Layout.setVerticalGroup(
                        jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel9Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(languageCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(7, 7, 7)
                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(fromCzechRB)
                                        .addGroup(jPanel9Layout.createSequentialGroup()
                                                .addGap(23, 23, 23)
                                                .addComponent(toCzechRB)))
                                .addGap(18, 18, 18)
                                .addComponent(grammarCB)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );

                jPanel7.setName("jPanel7"); // NOI18N

                jPanel3.setMaximumSize(new java.awt.Dimension(537, 234));
                jPanel3.setMinimumSize(new java.awt.Dimension(537, 234));
                jPanel3.setName("jPanel3"); // NOI18N
                jPanel3.setPreferredSize(new java.awt.Dimension(537, 234));

                questionLabel.setText(resourceMap.getString("questionLabel.text")); // NOI18N
                questionLabel.setName("questionLabel"); // NOI18N

                jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
                jScrollPane1.setName("jScrollPane1"); // NOI18N

                questionField.setColumns(20);
                questionField.setFont(resourceMap.getFont("questionField.font")); // NOI18N
                questionField.setLineWrap(true);
                questionField.setRows(3);
                questionField.setWrapStyleWord(true);
                questionField.setAutoscrolls(false);
                questionField.setMaximumSize(new java.awt.Dimension(102, 19));
                questionField.setName("questionField"); // NOI18N
                questionField.addMouseListener(new java.awt.event.MouseAdapter() {
                        public void mouseClicked(java.awt.event.MouseEvent evt) {
                                questionFieldMouseClicked(evt);
                        }
                });
                questionField.addKeyListener(new java.awt.event.KeyAdapter() {
                        public void keyPressed(java.awt.event.KeyEvent evt) {
                                questionFieldKeyPressed(evt);
                        }
                        public void keyReleased(java.awt.event.KeyEvent evt) {
                                questionFieldKeyReleased(evt);
                        }
                });
                jScrollPane1.setViewportView(questionField);

                javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
                jPanel3.setLayout(jPanel3Layout);
                jPanel3Layout.setHorizontalGroup(
                        jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(questionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(115, Short.MAX_VALUE))
                );
                jPanel3Layout.setVerticalGroup(
                        jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(questionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE))
                );

                jPanel14.setName("jPanel14"); // NOI18N

                answerLabel.setText(resourceMap.getString("Czech.text")); // NOI18N
                answerLabel.setName("Czech"); // NOI18N

                jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
                jScrollPane2.setName("jScrollPane2"); // NOI18N

                answerField.setColumns(20);
                answerField.setFont(resourceMap.getFont("answerField.font")); // NOI18N
                answerField.setLineWrap(true);
                answerField.setRows(5);
                answerField.setWrapStyleWord(true);
                answerField.setMaximumSize(new java.awt.Dimension(242, 87));
                answerField.setName("answerField"); // NOI18N
                answerField.addMouseListener(new java.awt.event.MouseAdapter() {
                        public void mouseClicked(java.awt.event.MouseEvent evt) {
                                answerFieldMouseClicked(evt);
                        }
                });
                answerField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
                        public void propertyChange(java.beans.PropertyChangeEvent evt) {
                                answerFieldPropertyChange(evt);
                        }
                });
                answerField.addKeyListener(new java.awt.event.KeyAdapter() {
                        public void keyPressed(java.awt.event.KeyEvent evt) {
                                answerFieldKeyPressed(evt);
                        }
                        public void keyReleased(java.awt.event.KeyEvent evt) {
                                answerFieldKeyReleased(evt);
                        }
                        public void keyTyped(java.awt.event.KeyEvent evt) {
                                answerFieldKeyTyped(evt);
                        }
                });
                jScrollPane2.setViewportView(answerField);

                javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
                jPanel14.setLayout(jPanel14Layout);
                jPanel14Layout.setHorizontalGroup(
                        jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel14Layout.createSequentialGroup()
                                .addComponent(answerLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(193, Short.MAX_VALUE))
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                );
                jPanel14Layout.setVerticalGroup(
                        jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel14Layout.createSequentialGroup()
                                .addComponent(answerLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE))
                );

                answerLabel.getAccessibleContext().setAccessibleName(resourceMap.getString("Czech.AccessibleContext.accessibleName")); // NOI18N

                javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
                jPanel7.setLayout(jPanel7Layout);
                jPanel7Layout.setHorizontalGroup(
                        jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
                );
                jPanel7Layout.setVerticalGroup(
                        jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );

                jPanel10.setBorder(javax.swing.BorderFactory.createEtchedBorder());
                jPanel10.setName("jPanel10"); // NOI18N
                jPanel10.setPreferredSize(new java.awt.Dimension(80, 80));

                cnt1xLabel.setText(resourceMap.getString("cnt1xLabel.text")); // NOI18N
                cnt1xLabel.setName("cnt1xLabel"); // NOI18N

                cnt2xLabel.setText(resourceMap.getString("cnt2xLabel.text")); // NOI18N
                cnt2xLabel.setName("cnt2xLabel"); // NOI18N

                cnt3xLabel.setText(resourceMap.getString("cnt3xLabel.text")); // NOI18N
                cnt3xLabel.setName("cnt3xLabel"); // NOI18N

                cnt1x.setText(resourceMap.getString("cnt1x.text")); // NOI18N
                cnt1x.setName("cnt1x"); // NOI18N

                cnt2x.setText(resourceMap.getString("cnt2x.text")); // NOI18N
                cnt2x.setName("cnt2x"); // NOI18N

                cnt3x.setText(resourceMap.getString("cnt3x.text")); // NOI18N
                cnt3x.setName("cnt3x"); // NOI18N

                cnt0xLabel.setText(resourceMap.getString("cnt0xLabel.text")); // NOI18N
                cnt0xLabel.setName("cnt0xLabel"); // NOI18N

                cnt0x.setText(resourceMap.getString("cnt0x.text")); // NOI18N
                cnt0x.setName("cnt0x"); // NOI18N

                javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
                jPanel10.setLayout(jPanel10Layout);
                jPanel10Layout.setHorizontalGroup(
                        jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel10Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel10Layout.createSequentialGroup()
                                                .addComponent(cnt0xLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                                                .addComponent(cnt0x))
                                        .addGroup(jPanel10Layout.createSequentialGroup()
                                                .addComponent(cnt1xLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                                                .addComponent(cnt1x))
                                        .addGroup(jPanel10Layout.createSequentialGroup()
                                                .addComponent(cnt2xLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                                                .addComponent(cnt2x))
                                        .addGroup(jPanel10Layout.createSequentialGroup()
                                                .addComponent(cnt3xLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                                                .addComponent(cnt3x)))
                                .addContainerGap())
                );
                jPanel10Layout.setVerticalGroup(
                        jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel10Layout.createSequentialGroup()
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(cnt0xLabel)
                                        .addComponent(cnt0x))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(cnt1xLabel)
                                        .addComponent(cnt1x))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(cnt2xLabel)
                                        .addComponent(cnt2x))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(cnt3xLabel)
                                        .addComponent(cnt3x))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );

                jPanel11.setBorder(javax.swing.BorderFactory.createEtchedBorder());
                jPanel11.setName("jPanel11"); // NOI18N

                jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
                jLabel6.setName("jLabel6"); // NOI18N

                cnt5x.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
                cnt5x.setText(resourceMap.getString("cnt5x.text")); // NOI18N
                cnt5x.setName("cnt5x"); // NOI18N

                jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
                jLabel5.setName("jLabel5"); // NOI18N

                cnt4x.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
                cnt4x.setText(resourceMap.getString("cnt4x.text")); // NOI18N
                cnt4x.setName("cnt4x"); // NOI18N

                cnt6xLabel.setText(resourceMap.getString("cnt6xLabel.text")); // NOI18N
                cnt6xLabel.setName("cnt6xLabel"); // NOI18N

                cnt7xLabel.setText(resourceMap.getString("cnt7xLabel.text")); // NOI18N
                cnt7xLabel.setName("cnt7xLabel"); // NOI18N

                cnt7x.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
                cnt7x.setText(resourceMap.getString("cnt7x.text")); // NOI18N
                cnt7x.setName("cnt7x"); // NOI18N

                cnt6x.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
                cnt6x.setText(resourceMap.getString("cnt6x.text")); // NOI18N
                cnt6x.setName("cnt6x"); // NOI18N

                javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
                jPanel11.setLayout(jPanel11Layout);
                jPanel11Layout.setHorizontalGroup(
                        jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel11Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(cnt7xLabel)
                                        .addComponent(cnt6xLabel)
                                        .addComponent(jLabel6)
                                        .addComponent(jLabel5))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(cnt5x, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                                        .addComponent(cnt7x, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                                        .addComponent(cnt6x, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                                        .addComponent(cnt4x, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE))
                                .addContainerGap())
                );
                jPanel11Layout.setVerticalGroup(
                        jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel11Layout.createSequentialGroup()
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel5)
                                        .addComponent(cnt4x))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel6)
                                        .addComponent(cnt5x))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(cnt6xLabel)
                                        .addComponent(cnt6x))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(cnt7xLabel)
                                        .addComponent(cnt7x))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );

                jPanel12.setBorder(javax.swing.BorderFactory.createEtchedBorder());
                jPanel12.setName("jPanel12"); // NOI18N
                jPanel12.setPreferredSize(new java.awt.Dimension(84, 89));

                cnt8xLabel.setText(resourceMap.getString("cnt8xLabel.text")); // NOI18N
                cnt8xLabel.setName("cnt8xLabel"); // NOI18N

                cnt8x.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
                cnt8x.setText(resourceMap.getString("cnt8x.text")); // NOI18N
                cnt8x.setName("cnt8x"); // NOI18N

                cnt9xLabel.setText(resourceMap.getString("cnt9xLabel.text")); // NOI18N
                cnt9xLabel.setName("cnt9xLabel"); // NOI18N

                cnt10x.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
                cnt10x.setText(resourceMap.getString("cnt10x.text")); // NOI18N
                cnt10x.setName("cnt10x"); // NOI18N

                cnt9x.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
                cnt9x.setText(resourceMap.getString("cnt9x.text")); // NOI18N
                cnt9x.setName("cnt9x"); // NOI18N

                cnt10xLabel.setText(resourceMap.getString("cnt10xLabel.text")); // NOI18N
                cnt10xLabel.setName("cnt10xLabel"); // NOI18N

                jLabel22.setText(resourceMap.getString("jLabel22.text")); // NOI18N
                jLabel22.setName("jLabel22"); // NOI18N

                cnt11x.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
                cnt11x.setText(resourceMap.getString("cnt11x.text")); // NOI18N
                cnt11x.setName("cnt11x"); // NOI18N

                javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
                jPanel12.setLayout(jPanel12Layout);
                jPanel12Layout.setHorizontalGroup(
                        jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel12Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(cnt9xLabel)
                                        .addComponent(cnt10xLabel)
                                        .addComponent(jLabel22)
                                        .addComponent(cnt8xLabel))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(cnt8x, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
                                        .addComponent(cnt9x, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
                                        .addComponent(cnt10x, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
                                        .addComponent(cnt11x, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE))
                                .addContainerGap())
                );
                jPanel12Layout.setVerticalGroup(
                        jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel12Layout.createSequentialGroup()
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(cnt8xLabel)
                                        .addComponent(cnt8x))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(cnt9xLabel)
                                        .addComponent(cnt9x))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(cnt10xLabel)
                                        .addComponent(cnt10x))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel22)
                                        .addComponent(cnt11x))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );

                jPanel13.setBorder(javax.swing.BorderFactory.createEtchedBorder());
                jPanel13.setName("jPanel13"); // NOI18N

                cnt13x.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
                cnt13x.setText(resourceMap.getString("cnt13x.text")); // NOI18N
                cnt13x.setName("cnt13x"); // NOI18N

                jLabel24.setText(resourceMap.getString("jLabel24.text")); // NOI18N
                jLabel24.setName("jLabel24"); // NOI18N

                jLabel25.setText(resourceMap.getString("jLabel25.text")); // NOI18N
                jLabel25.setName("jLabel25"); // NOI18N

                jLabel26.setText(resourceMap.getString("jLabel26.text")); // NOI18N
                jLabel26.setName("jLabel26"); // NOI18N

                cnt15x.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
                cnt15x.setText(resourceMap.getString("cnt15x.text")); // NOI18N
                cnt15x.setName("cnt15x"); // NOI18N

                cnt14x.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
                cnt14x.setText(resourceMap.getString("cnt14x.text")); // NOI18N
                cnt14x.setName("cnt14x"); // NOI18N

                jLabel23.setText(resourceMap.getString("jLabel23.text")); // NOI18N
                jLabel23.setName("jLabel23"); // NOI18N

                cnt12x.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
                cnt12x.setText(resourceMap.getString("cnt12x.text")); // NOI18N
                cnt12x.setName("cnt12x"); // NOI18N

                javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
                jPanel13.setLayout(jPanel13Layout);
                jPanel13Layout.setHorizontalGroup(
                        jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel13Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel24)
                                        .addComponent(jLabel23)
                                        .addComponent(jLabel25)
                                        .addComponent(jLabel26))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(cnt15x, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                                        .addComponent(cnt14x, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                                        .addComponent(cnt13x, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                                        .addComponent(cnt12x, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE))
                                .addContainerGap())
                );
                jPanel13Layout.setVerticalGroup(
                        jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel13Layout.createSequentialGroup()
                                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel23)
                                        .addComponent(cnt12x))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel24)
                                        .addComponent(cnt13x))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel25)
                                        .addComponent(cnt14x))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel26)
                                        .addComponent(cnt15x))
                                .addContainerGap(6, Short.MAX_VALUE))
                );

                jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
                jPanel4.setName("jPanel4"); // NOI18N

                cnt16xLabel.setText(resourceMap.getString("cnt16xLabel.text")); // NOI18N
                cnt16xLabel.setName("cnt16xLabel"); // NOI18N

                cnt16x.setText(resourceMap.getString("cnt16x.text")); // NOI18N
                cnt16x.setName("cnt16x"); // NOI18N

                cnt17xLabel.setText(resourceMap.getString("cnt17xLabel.text")); // NOI18N
                cnt17xLabel.setName("cnt17xLabel"); // NOI18N

                cnt17x.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
                cnt17x.setText(resourceMap.getString("cnt17x.text")); // NOI18N
                cnt17x.setName("cnt17x"); // NOI18N

                cnt18xLabel.setText(resourceMap.getString("cnt18xLabel.text")); // NOI18N
                cnt18xLabel.setName("cnt18xLabel"); // NOI18N

                cnt18x.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
                cnt18x.setText(resourceMap.getString("cnt18x.text")); // NOI18N
                cnt18x.setName("cnt18x"); // NOI18N

                jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
                jLabel2.setName("jLabel2"); // NOI18N

                cnt19x.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
                cnt19x.setText(resourceMap.getString("cnt19x.text")); // NOI18N
                cnt19x.setName("cnt19x"); // NOI18N

                javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
                jPanel4.setLayout(jPanel4Layout);
                jPanel4Layout.setHorizontalGroup(
                        jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(cnt16xLabel)
                                        .addComponent(cnt17xLabel)
                                        .addComponent(cnt18xLabel)
                                        .addComponent(jLabel2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(cnt19x, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                                        .addComponent(cnt18x, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                                        .addComponent(cnt16x, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(cnt17x, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE))
                                .addContainerGap())
                );
                jPanel4Layout.setVerticalGroup(
                        jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                .addComponent(cnt16xLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(cnt17xLabel))
                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                .addComponent(cnt16x)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(cnt17x)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(cnt18xLabel)
                                        .addComponent(cnt18x))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel2)
                                        .addComponent(cnt19x))
                                .addContainerGap(6, Short.MAX_VALUE))
                );

                javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
                mainPanel.setLayout(mainPanelLayout);
                mainPanelLayout.setHorizontalGroup(
                        mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 600, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, mainPanelLayout.createSequentialGroup()
                                                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(jPanel2, 0, 473, Short.MAX_VALUE))
                                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, mainPanelLayout.createSequentialGroup()
                                                        .addContainerGap()
                                                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 600, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(14, Short.MAX_VALUE))
                );
                mainPanelLayout.setVerticalGroup(
                        mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(mainPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18))
                );

                menuBar.setMaximumSize(new java.awt.Dimension(150, 150));
                menuBar.setName("menuBar"); // NOI18N
                menuBar.setPreferredSize(new java.awt.Dimension(100, 21));

                fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
                fileMenu.setFocusable(false);
                fileMenu.setName("fileMenu"); // NOI18N
                fileMenu.addFocusListener(new java.awt.event.FocusAdapter() {
                        public void focusGained(java.awt.event.FocusEvent evt) {
                                fileMenuFocusGained(evt);
                        }
                });

                tisk.setAction(actionMap.get("tisk")); // NOI18N
                tisk.setText(resourceMap.getString("tisk.text")); // NOI18N
                tisk.setName("tisk"); // NOI18N
                fileMenu.add(tisk);

                changeUser.setAction(actionMap.get("changeUser")); // NOI18N
                changeUser.setText(resourceMap.getString("changeUser.text")); // NOI18N
                changeUser.setName("changeUser"); // NOI18N
                fileMenu.add(changeUser);

                importItem.setText(resourceMap.getString("importItem.text")); // NOI18N
                importItem.setName("importItem"); // NOI18N
                importItem.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                importItemActionPerformed(evt);
                        }
                });
                fileMenu.add(importItem);

                export.setText(resourceMap.getString("export.text")); // NOI18N
                export.setName("export"); // NOI18N
                export.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                exportActionPerformed(evt);
                        }
                });
                fileMenu.add(export);

                exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
                exitMenuItem.setText(resourceMap.getString("exitMenuItem.text")); // NOI18N
                exitMenuItem.setName("exitMenuItem"); // NOI18N
                fileMenu.add(exitMenuItem);

                menuBar.add(fileMenu);

                jMenu2.setText(resourceMap.getString("jMenu2.text")); // NOI18N
                jMenu2.setName("jMenu2"); // NOI18N

                jMenuItem2.setAction(actionMap.get("showSettingsDialog")); // NOI18N
                jMenuItem2.setText(resourceMap.getString("jMenuItem2.text")); // NOI18N
                jMenuItem2.setName("jMenuItem2"); // NOI18N
                jMenu2.add(jMenuItem2);

                menuBar.add(jMenu2);

                slovnikMenu.setText(resourceMap.getString("slovnikMenu.text")); // NOI18N
                slovnikMenu.setName("slovnikMenu"); // NOI18N

                jMenuItem5.setText(resourceMap.getString("jMenuItem5.text")); // NOI18N
                jMenuItem5.setName("jMenuItem5"); // NOI18N
                jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                jMenuItem5ActionPerformed(evt);
                        }
                });
                slovnikMenu.add(jMenuItem5);

                jMenuItem1.setAction(actionMap.get("showStatsForm")); // NOI18N
                jMenuItem1.setText(resourceMap.getString("jMenuItem1.text")); // NOI18N
                jMenuItem1.setName("jMenuItem1"); // NOI18N
                jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                jMenuItem1ActionPerformed(evt);
                        }
                });
                slovnikMenu.add(jMenuItem1);

                jMenuItem3.setAction(actionMap.get("showThemeManager")); // NOI18N
                jMenuItem3.setText(resourceMap.getString("jMenuItem3.text")); // NOI18N
                jMenuItem3.setActionCommand(resourceMap.getString("jMenuItem3.actionCommand")); // NOI18N
                jMenuItem3.setName("jMenuItem3"); // NOI18N
                slovnikMenu.add(jMenuItem3);

                jMenuItem4.setAction(actionMap.get("batchLoader")); // NOI18N
                jMenuItem4.setText(resourceMap.getString("jMenuItem4.text")); // NOI18N
                jMenuItem4.setActionCommand(resourceMap.getString("jMenuItem4.actionCommand")); // NOI18N
                jMenuItem4.setName("jMenuItem4"); // NOI18N
                slovnikMenu.add(jMenuItem4);

                smazatSlovicka.setText(resourceMap.getString("smazatSlovicka.text")); // NOI18N
                smazatSlovicka.setName("smazatSlovicka"); // NOI18N
                smazatSlovicka.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                smazatSlovickaActionPerformed(evt);
                        }
                });
                slovnikMenu.add(smazatSlovicka);

                menuBar.add(slovnikMenu);

                helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
                helpMenu.setName("helpMenu"); // NOI18N

                aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
                aboutMenuItem.setText(resourceMap.getString("aboutMenuItem.text")); // NOI18N
                aboutMenuItem.setName("aboutMenuItem"); // NOI18N
                helpMenu.add(aboutMenuItem);

                menuBar.add(helpMenu);

                setComponent(mainPanel);
                setMenuBar(menuBar);
                addPropertyChangeListener(new java.beans.PropertyChangeListener() {
                        public void propertyChange(java.beans.PropertyChangeEvent evt) {
                                formPropertyChange(evt);
                        }
                });
        }// </editor-fold>//GEN-END:initComponents

        private void sendAnswer() {
                String answerString = answerField.getText();

                if (!answerField.getText().isEmpty()) {
                        previousAnswer = answerField.getText();
                } else {
                        previousAnswer = "";
                }
                boolean answer = ucitel.checkAnswer(answerString.toLowerCase());
                if (true == answer) {

                        answerField.setText("");
                        previousAnswer = "";
                } else {
                        answerField.setText("");
                        answerField.setBackground(Color.white);
                }
                ucitel.adjustWeight(answer);
                askTeacher();
                this.displayCorrectAnswer();
                fixButton.setEnabled(true);

                //statusLabel.setText("0x: " + this.conn.getWordCnt(0, DirectionOfTranslation.getInstance()));

        }

        public void askTeacher() {

                resultLabel.setText("");
                try {
                        conn = ConnectionToDB.getInstance();
                } catch (Exception e) {
                        e.printStackTrace();
                }
                if (conn == null) {
                        answerField.setEnabled(false);
                        jScrollPane1.setEnabled(false);
                        return;
                }
                if (ucitel.isQuestionAvailable() == true) {
                        deleteCurrentButton.setEnabled(true);
                        fixCurrentButton.setEnabled(true);
                        if (false == ucitel.getQuestionFromDB()) {
                                jScrollPane1.requestFocus();
                                questionField.setText("");
                                answerField.setText("");
                        }
                }
        }

        private void allAsked() {
                resultLabel.setText("Všechna slovíčka vyzkoušena");
                deleteCurrentButton.setEnabled(false);
                questionField.setText("");
                fixCurrentButton.setEnabled(false);
                clear.setEnabled(false);
                hint.setText("");
        }

        private void zkousejZnova() {
                direction.resetAllZeroAsked();

                answerField.setText("");
                resultLabel.setText("");
                askTeacher();
                answerField.requestFocus();
        }

private void askButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_askButtonActionPerformed

        showZkousejZnovaForm();

}//GEN-LAST:event_askButtonActionPerformed

        public String getTheme() {
                String theme = (String) themeCB.getSelectedItem();

                if (theme == null) {
                        return vsechno;
                }

                if (theme.matches(this.vsechno)) {
                        return null;
                } else {
                        return theme;
                }
        }

        private void playWord(String word) {

                word = ucitel.removeTo(word);
                word = ucitel.removeBrackets(word);

                if (XML.getInstance().getSoundOn() == true) {
                        WavPlayer wp = new WavPlayer();
                        wp.setFilename(word);
                        (new Thread(wp)).start();
                }

        }

        private Language getLanguage() {
                Language language = (Language) languageCB.getSelectedItem();
                return language;
        }

        private boolean addExample() {
                Example example = null;
                String answer = answerField.getText();
                String question = questionField.getText();

                if (!ucitel.checkWord(answer)) {
                        this.resultLabel.setText("Chybne zadana odpoved");
                        return false;
                }

                if (!ucitel.checkWord(question)) {
                        this.resultLabel.setText("Chybne zadana otazka");
                        return false;
                }
                Language language = getLanguage();

                if (!answer.isEmpty() && !question.isEmpty()) {

                        String s_hint = hint.getText();
                        String selectedTheme = (String) themeCB.getSelectedItem();
                        if (s_hint.isEmpty()) {

                                if (selectedTheme.matches(this.vsechno)) {
                                        //example = new Example(answer, question);
                                        example = new Example(answer, question, language, selectedTheme, null);
                                } else {
                                        //example = new Example(null, answer, question, selectedTheme);
                                        example = new Example(answer, question, language, selectedTheme, null);
                                }

                        } else {
                                example = new Example(answer, question, language, selectedTheme, s_hint);
                        }

                        if (this.grammarCB.isSelected()) {
                                example.setGrammar(true);
                        }

                        if (true == example.check()) {
                                try {
                                        if (false == Teacher.getInstance().exampleExists(question, answer)) {
                                                WavDownloader.downLoad(example.getForeign());
                                                if (true == conn.addExample(example, true)) {
                                                        this.playWord(example.getForeign());
                                                        //statusLabel.setText("Slovíčko přidáno do databáze");
                                                }
                                        } else {
                                                resultLabel.setText("Slovíčko už v databázi je");
                                                questionField.setText("");
                                                answerField.setText("");
                                                return false;
                                        }

                                } catch (Exception e) {
                                        System.out.println(e.toString());
                                        JOptionPane.showMessageDialog(GUI.getInstance().getComponent(), e.toString(), "Chyba", JOptionPane.OK_OPTION);
                                }
                        } else {
                                JOptionPane.showMessageDialog(GUI.getInstance().getComponent(), "Prosím opravte zadání", "Chyba v zadání", JOptionPane.OK_OPTION);
                                return false;
                        }
                }

                updateProgressBar();
                answerField.setText("");
                questionField.setText("");
                hint.setText("");
                askButton.setEnabled(true);
                return true;
        }

        private void displayCorrectAnswer() {
                if (Teacher.getInstance().isQuestionAvailable() == true) {
                        if (direction.isFromCzech()) {
                                resultLabel.setText(ucitel.doplnMezery(ucitel.getPreviousCzech() + " = " + ucitel.getPreviousQuestion()));
                        } else {
                                resultLabel.setText(ucitel.doplnMezery((ucitel.getPreviousQuestion()) + " = " + ucitel.getPreviousCzech()));
                        }
                } else {
                        allAsked();
                }

        }

private void fixButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fixButtonActionPerformed
        fixForm = new FixForm(ucitel, FixForm.PREVIOUS);
        answerField.setText("");
        fixForm.setVisible(true);
        this.updateProgressBar();
}//GEN-LAST:event_fixButtonActionPerformed

private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
}//GEN-LAST:event_jMenuItem1ActionPerformed

private void mainPanelFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_mainPanelFocusGained
System.out.println("Focus gained");//GEN-LAST:event_mainPanelFocusGained
        }

private void formPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_formPropertyChange
        System.out.print(gUI.getString("property_changed"));
}//GEN-LAST:event_formPropertyChange

private void mainPanelVetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {//GEN-FIRST:event_mainPanelVetoableChange
        System.out.print(gUI.getString("vetoable_change"));
}//GEN-LAST:event_mainPanelVetoableChange

private void fromCzechRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fromCzechRBActionPerformed
        setDirection();
}//GEN-LAST:event_fromCzechRBActionPerformed

private void toCzechRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toCzechRBActionPerformed
        setDirection();
}//GEN-LAST:event_toCzechRBActionPerformed

        private boolean isNew() {

                if (ucitel == null) {
                        return false;
                }

                if (true == ucitel.questionWasAsked()) {
                        if (!(questionField.getText().equalsIgnoreCase(ucitel.getQuestion(direction)))) {
                                if (questionField.getText().isEmpty()) {
                                        return false;
                                } else {
                                        return true;
                                }
                        } else {
                                return false;
                        }
                } else {
                        if (questionField.getText().isEmpty() == true) {
                                return false;
                        } else {
                                return true;
                        }
                }


        }
        private final Set<Character> pressed = new HashSet<Character>();

private void answerFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_answerFieldKeyPressed

        String pressedS = String.valueOf(evt.getKeyChar());
        if (!pressedS.matches("[\\W- \\(\\)]")) {
                pressed.add(evt.getKeyChar());
        }

        if (evt.getKeyCode() == KeyEvent.VK_TAB) {
                evt.consume();
                hint.requestFocus();
        }

        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                pressed.clear();
                evt.consume();

                if (isNew()) {
                        if (!questionField.getText().isEmpty() && !answerField.getText().isEmpty()) {
                                if (true == addExample()) {
                                        resultLabel.setText("");
                                        deleteCurrentButton.setEnabled(false);
                                        fixCurrentButton.setEnabled(false);
                                        wordLeftLabel.setText(Integer.toString(ucitel.getQuestionsLeft()));
                                        if (direction.isToCzech()) {
                                                questionField.requestFocus();
                                        } else {
                                                questionField.requestFocus();
                                        }
                                }
                        }
                } else {
                        answerField.requestFocus();
                        int ataw = conn.getWronglyAnsweredCnt();
                        if (ataw == 50) {
                                conn.updateLastAsked();
                                askAgainL.setText(String.valueOf(ataw));
                        }
                        sendAnswer();
                        if (answerField.getText().isEmpty() == false) {
                                displayCorrectAnswer();
                                answerField.setText("");
                                answerField.setBackground(Color.WHITE);
                        }
                }
                return;
        }
}//GEN-LAST:event_answerFieldKeyPressed

private void answerFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_answerFieldKeyReleased

        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
                if (answerField.getText().matches(" ")) {
                        answerField.setText("");
                        evt.consume();
                        pressed.remove(evt.getKeyChar());
                        return;
                }
        }

        if (!ucitel.questionWasAsked()) {
                pressed.remove(evt.getKeyChar());
                return;
        }

        if (!(questionField.getText()).equalsIgnoreCase(ucitel.getQuestion(direction))) {
                answerField.setBackground(Color.white);
                pressed.remove(evt.getKeyChar());
                return;
        }

        if (answerField.getText().isEmpty() && questionField.getText().isEmpty() && hint.getText().isEmpty()) {
                clear.setEnabled(false);
        } else {
                clear.setEnabled(true);
        }

        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                playWord(ucitel.getPreviousQuestion());
        }

        if (answerField.getText().length() == 0) {
                answerField.setBackground(Color.white);
                pressed.remove(evt.getKeyChar());
                return;
        }

        int partialLen = answerField.getText().length();
        String partialAnswer = answerField.getText();
        List<String> answers = ucitel.getAnswers();
        boolean guess = false;
        try {
                if (ucitel.questionWasAsked()) {
                        for (String answer : answers) {
                                if (this.getGrammar() == true) {
                                        guess = ucitel.checkPartial(partialLen, partialAnswer);
                                } else {
                                        if (partialLen > answer.length() && partialAnswer.substring(0, answer.length()).equals(answer)) {
                                                partialAnswer = partialAnswer.substring(0, answer.length());
                                        }
                                        guess = ucitel.checkPartial(partialLen, partialAnswer.toLowerCase());
                                }
                                if (guess == true) {
                                        break;
                                }
                        }
                }

        } catch (UcitelException e) {
                String connectingChar = e.toString().substring(e.toString().length() - 1);
                answerField.setText(partialAnswer.substring(0, partialAnswer.length() - 1) + connectingChar);
                guess = true;
        } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e.toString());
                guess = false;
        }

        if (guess == true) {
                int answersCnt = ucitel.getAnswersLength().size();
                for (int i = 0; i < answersCnt; i++) {


                        if (ucitel.checkAnswer(partialAnswer.toLowerCase())) {
                                if (direction.isToEnglish()) {
                                        playWord(partialAnswer.toLowerCase());
                                } else if (direction.isFromEnglish()) {
                                        playWord(ucitel.getPreviousQuestion());
                                }

                                if (direction.isToJapanese()) {
                                        if (evt.getKeyCode() == evt.VK_ENTER) {
                                                sendAnswer();
                                        }

                                } else {
                                        sendAnswer();
                                }
                                pressed.clear();

                                break;
                        } else {
                                answerField.setBackground(new Color(11336280)); //zelena
                                if ((fromCzechRB.isSelected() == true) && (direction.isToEnglish())) {
                                        //List<String> answers = ucitel.getAnswers();
                                        for (String ans : answers) {
                                                if (ans.startsWith("to t") && partialAnswer.equals("t")) {
                                                        answerField.setText("to t");
                                                        break;
                                                } else {
                                                        if (ans.startsWith("to ") && partialAnswer.equals("t") && (evt.getKeyCode() != KeyEvent.VK_BACK_SPACE)) {
                                                                answerField.setText("to ");
                                                                break;
                                                        }
                                                }
                                        }
                                }
                        }
                }
        } else {
                if (direction.isToEnglish()) {

                        for (String ans : answers) {
                                if (ans.startsWith("to ") && partialAnswer.matches("to .")) {
                                        answerField.setText("to ");
                                        break;
                                }
                        }
                }

                //kdyz je otazkou nemecke podstatne jmeno a neznam clen
                if ((direction.isToDeutsch()) && (partialLen == 1)) {
                        //kdyz jsem zadal r,e nebo s
                        if (partialAnswer.startsWith("r") || partialAnswer.startsWith("e") || partialAnswer.startsWith("s")) {
                                for (String ans : answers) {
                                        String answ = ans.toLowerCase();

                                        if (answ.startsWith("r ") || answ.startsWith("e ") || answ.startsWith("s ")) {
                                                sendAnswer();
                                                break;
                                        }
                                }
                        }
                }

                if (partialLen == pressed.size()) {
                        answerField.setText("");
                        answerField.setBackground(Color.white);
                        pressed.clear();
                } else if (ucitel.questionWasAsked()) {
                        if (answerField.getText().length() == 1) {
                                pressed.clear();
                        } else {
                                answerField.setBackground(new Color(16099753)); //cervena
                        }
                }


        }

        pressed.remove(evt.getKeyChar());
}//GEN-LAST:event_answerFieldKeyReleased

private void questionFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_questionFieldKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                evt.consume();
                return;
        }

        if (evt.getKeyCode() == KeyEvent.VK_TAB) {
                evt.consume();
                answerField.requestFocusInWindow();
        }

        if (true == ucitel.questionWasAsked() && (questionField.getText().equalsIgnoreCase(ucitel.getQuestion(direction)))) {
        } else {
                if (!questionField.getText().isEmpty() && !answerField.getText().isEmpty()) {
                        answerField.setBackground(Color.WHITE);
                        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                                if (true == addExample()) {
                                        resultLabel.setText("");
                                        askButton.setEnabled(true);
                                        deleteCurrentButton.setEnabled(false);
                                        fixCurrentButton.setEnabled(false);
                                        wordLeftLabel.setText(Integer.toString(ucitel.getQuestionsLeft()));
                                }
                                jScrollPane1.requestFocus();
                        }

                }
        }
}//GEN-LAST:event_questionFieldKeyPressed

private void fileMenuFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_fileMenuFocusGained
}//GEN-LAST:event_fileMenuFocusGained

private void fixCurrentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fixCurrentButtonActionPerformed
        fixForm = new FixForm(ucitel, FixForm.CURRENT);
        answerField.setText("");
        fixForm.setVisible(true);
        this.updateProgressBar();
}//GEN-LAST:event_fixCurrentButtonActionPerformed

private void hintKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_hintKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_TAB) {
                evt.consume();
                questionField.requestFocusInWindow();
        }

        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                evt.consume();
                if (true == ucitel.questionWasAsked() && (questionField.getText().equalsIgnoreCase(ucitel.getQuestion(direction)))) {
                        answerField.requestFocus();
                        sendAnswer();
                        if (answerField.getText().isEmpty() == false) {
                                displayCorrectAnswer();
                                answerField.setText("");
                                answerField.setBackground(Color.WHITE);
                        }
                } else {
                        if (!questionField.getText().isEmpty() && !answerField.getText().isEmpty()) {
                                if (true == addExample()) {
                                        resultLabel.setText("");
                                        deleteCurrentButton.setEnabled(false);
                                        fixCurrentButton.setEnabled(false);
                                        wordLeftLabel.setText(Integer.toString(ucitel.getQuestionsLeft()));
                                }
                                jScrollPane1.requestFocus();
                        }
                }
                return;
        }
}//GEN-LAST:event_hintKeyPressed

private void mainPanelComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_mainPanelComponentShown
}//GEN-LAST:event_mainPanelComponentShown

private void mainPanelHierarchyChanged(java.awt.event.HierarchyEvent evt) {//GEN-FIRST:event_mainPanelHierarchyChanged
}//GEN-LAST:event_mainPanelHierarchyChanged

private void themeCBItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_themeCBItemStateChanged


        String theme = (String) themeCB.getSelectedItem();

        if ((theme == null) || (theme.equals(lastTheme))) {
                return;
        }

        lastTheme = theme;

        this.updateProgressBar();
        if (true == ucitel.isQuestionAvailable()) {

                ucitel.getQuestionFromDB();
                questionField.setText(ucitel.getQuestion(direction));
                this.deleteCurrentButton.setEnabled(true);
                this.fixCurrentButton.setEnabled(true);
        }


        XML xml = XML.getInstance();
        xml.setLastTheme(theme);
        xml.writeXML();
}//GEN-LAST:event_themeCBItemStateChanged

private void questionFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_questionFieldKeyReleased

        if (answerField.getText().isEmpty() && questionField.getText().isEmpty() && hint.getText().isEmpty()) {
                clear.setEnabled(false);
        } else {
                clear.setEnabled(true);
        }
}//GEN-LAST:event_questionFieldKeyReleased

        public void setGrammar(boolean grammar) {
                if (grammar == true) {
                        grammarCB.setSelected(true);
                } else {
                        grammarCB.setSelected(false);
                }
        }

private void hintKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_hintKeyReleased
        if (answerField.getText().isEmpty() && questionField.getText().isEmpty() && hint.getText().isEmpty()) {
                clear.setEnabled(false);
        } else {
                clear.setEnabled(true);
        }
}//GEN-LAST:event_hintKeyReleased

private void grammarCBStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_grammarCBStateChanged
        if (grammarCB.isSelected()) {
                XML.getInstance().setGrammar("true");
                grammar_initialized = true;
        } else {
                XML.getInstance().setGrammar("false");
        }

        /*if (isNew() && this.userLogged == true) {
        this.askTeacher();
        }*/
        //if (isNew()){
        zkousejZnova();
        //}

}//GEN-LAST:event_grammarCBStateChanged

private void answerFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_answerFieldKeyTyped
}//GEN-LAST:event_answerFieldKeyTyped

        public String getClipboardContents() {
                String result = "";
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                //odd: the Object param of getContents is not currently used
                Transferable contents = clipboard.getContents(null);
                boolean hasTransferableText =
                        (contents != null)
                        && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
                if (hasTransferableText) {
                        try {
                                result = (String) contents.getTransferData(DataFlavor.stringFlavor);
                        } catch (UnsupportedFlavorException ex) {
                                //highly unlikely since we are using a standard DataFlavor
                                System.out.println(ex);
                                ex.printStackTrace();
                        } catch (IOException ex) {
                                System.out.println(ex);
                                ex.printStackTrace();
                        }
                }
                return result;
        }

private void questionFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_questionFieldMouseClicked
        if (evt.getButton() == MouseEvent.BUTTON3) {
                String content = getClipboardContents();
                if (!content.isEmpty()) {
                        questionField.setText(content);
                }
        }

        questionField.requestFocus();
}//GEN-LAST:event_questionFieldMouseClicked

private void answerFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_answerFieldMouseClicked
        if (evt.getButton() == MouseEvent.BUTTON3) {
                String content = getClipboardContents();
                if (!content.isEmpty()) {
                        answerField.setText(content);
                }
        }

        answerField.requestFocus();
}//GEN-LAST:event_answerFieldMouseClicked

    private void smazatSlovickaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_smazatSlovickaActionPerformed
            if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(GUI.getInstance().getComponent(), "Všechna slovíčka v tomto jazyce budou vymazána!", "Smazat slovíčka", JOptionPane.YES_NO_OPTION)) {
                    conn.deleteWords();
            }

            questionField.setText("");
            answerField.setText("");
            this.updateProgressBar();
            wordLeftLabel.setText(Integer.toString(ucitel.getQuestionsLeft()));
    }//GEN-LAST:event_smazatSlovickaActionPerformed

    private void languageCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_languageCBActionPerformed

            this.setDirection();

    }//GEN-LAST:event_languageCBActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
            JFrame mainFrame = Ucitel.getApplication().getMainFrame();
            ViewerForm viewerForm = new ViewerForm(mainFrame);
            viewerForm.setLocationRelativeTo(mainFrame);
            viewerForm.setVisible(true);
            viewerForm.pack();
            this.askTeacher();
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void answerFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_answerFieldPropertyChange
            // TODO add your handling code here:
    }//GEN-LAST:event_answerFieldPropertyChange

    private void exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportActionPerformed
            JFileChooser fc = new JFileChooser();

            if (JFileChooser.APPROVE_OPTION == fc.showSaveDialog(menuBar)) {
                    File file = fc.getSelectedFile();
                    String path = file.getAbsolutePath();

                    try {

                            Language language = direction.getForeignLanguage();
                            OutputStream outputStream = new FileOutputStream(path);
                            Writer writer = new OutputStreamWriter(outputStream, "UTF-8");
                            List<Word> words = null;
                            words = conn.getVocabulary();
                            for (Word word : words) {
                                    String czech = word.getCzech();
                                    String foreign = word.getForeign();
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

    private void importItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importItemActionPerformed
            JFileChooser fc = new JFileChooser();

            if (JFileChooser.APPROVE_OPTION == fc.showOpenDialog(menuBar)) {
                    File file = fc.getSelectedFile();
                    String path = file.getAbsolutePath();

                    try {
                            // Open the file that is the first
                            // command line parameter
                            FileInputStream fstream = new FileInputStream(path);
                            // Get the object of DataInputStream
                            DataInputStream in = new DataInputStream(fstream);
                            BufferedReader br = new BufferedReader(new InputStreamReader(in));
                            String strLine;
                            //Read File Line By Line
                            while ((strLine = br.readLine()) != null) {
                                    // Print the content on the console
                                    System.out.println(strLine);
                                    String[] strings = strLine.split(";");
                                    Language language = Language.valueOf(strings[0]);
                                    String czech = strings[1];
                                    String foreign = strings[2];
                                    Example ex = new Example(foreign, czech, language);
                                    try {
                                            conn.addExample(ex, false);
                                    } catch (UcitelException ue) {
                                            System.err.println("Error: " + ue.getMessage());
                                    }
                            }
                            //Close the input stream
                            in.close();
                    } catch (Exception e) {//Catch exception if any
                            System.err.println("Error: " + e.getMessage());
                    }
            }
    }//GEN-LAST:event_importItemActionPerformed

        public void setTheme(String theme) {
                themeCB.setSelectedItem(theme);
        }

        @Action
        public void showStatsForm() {
                JFrame mainFrame = Ucitel.getApplication().getMainFrame();
                StatsForm statsForm = new StatsForm(mainFrame);
                statsForm.setLocationRelativeTo(mainFrame);
                statsForm.setVisible(true);
                statsForm.pack();
        }

        public void showZkousejZnovaForm() {
                JFrame mainFrame = Ucitel.getApplication().getMainFrame();
                ZkousejZnova zkousejZnovaForm = new ZkousejZnova(mainFrame);
                zkousejZnovaForm.setLocationRelativeTo(mainFrame);
                zkousejZnovaForm.setVisible(true);
                zkousejZnovaForm.pack();

        }

        @Action
        public void showAboutBox() {
                if (aboutBox == null) {
                        JFrame mainFrame = Ucitel.getApplication().getMainFrame();
                        aboutBox = new UcitelAboutBox(mainFrame);
                        aboutBox.setLocationRelativeTo(mainFrame);
                }
                Ucitel.getApplication().show(aboutBox);
        }

        @Action
        public void showSettingsDialog() {
                JFrame mainFrame = Ucitel.getApplication().getMainFrame();
                SettingsDialog settingsDialog = new SettingsDialog(mainFrame);
                settingsDialog.setLocationRelativeTo(mainFrame);
                Ucitel.getApplication().show(settingsDialog);
        }

        @Action
        public void examineAgain() {
                ucitel.resetOneAnsweredDate();
                askTeacher();
                resultLabel.setText("");
                //examineAgainButton.setEnabled(false);
        }

        @Action
        public void deleteCurrent() {

                if (true == ucitel.deleteCurrent()) {
                        resultLabel.setText("Slovíčko vymazáno");
                        fixButton.setEnabled(false);
                        ucitel.deleteExample();
                } else {
                        resultLabel.setText("Došlo k chybě při mazání slovíčka");
                }

                if (ucitel.isQuestionAvailable() == true) {
                        ucitel.getQuestionFromDB();
                } else {
                        questionField.setText("");
                        answerField.setText("");
                        hint.setText("");
                }
        }

        public void setUserLogged(boolean logged) {
                this.userLogged = logged;
        }

        @Action
        public void selectProfile() {
                LoginForm lf = new LoginForm(this.getFrame());
                lf.setModal(true);
                lf.setLocationRelativeTo(this.getFrame());
                resultLabel.setText("");
                lf.setVisible(true);
        }

        @Action
        public void setDirection() {
                Language from;
                Language to;
                if (fromCzechRB.isSelected() == true) {
                        from = Language.CZECH;
                        to = (Language) languageCB.getSelectedItem();

                        questionLabel.setText(from.toString());
                        answerLabel.setText(to.toString());
                        xml.setLanguage(to);
                } else {
                        to = Language.CZECH;
                        from = (Language) languageCB.getSelectedItem();
                        questionLabel.setText(from.toString());
                        answerLabel.setText(to.toString());
                        xml.setLanguage(from);
                }
                direction.setDirection(from, to);


                if (this.userLogged == true) {
                        resultLabel.setText("");

                        if (!isNew()) {
                                answerField.setText("");
                                questionField.setText("");
                                hint.setText("");
                        }

                        direction.resetAllZeroAsked();

                        if (this.userLogged == true) {
                                XML.getInstance().setDirection(direction);
                                if ((ucitel != null) && (this.themes_loaded == true) && !isNew()) {
                                        if (ucitel.isQuestionAvailable()) {
                                                ucitel.getQuestionFromDB();
                                                deleteCurrentButton.setEnabled(true);
                                                fixCurrentButton.setEnabled(true);
                                                askButton.setEnabled(true);
                                        }
                                }
                        }

                }

                if (userLogged) {
                        loadThemes();
                }
        }

        @Action
        public void swap() {
                String question = questionField.getText();
                questionField.setText(answerField.getText());
                answerField.setText(question);
        }

        @Action
        public void clean() {
                questionField.setText("");
                answerField.setText("");
                hint.setText("");
                ucitel.cleanExample();
        }

        @Action
        public void showThemeManager() {
                JFrame mainFrame = Ucitel.getApplication().getMainFrame();
                AddThemeForm addThemeForm = new AddThemeForm(mainFrame, true);
                addThemeForm.setLocationRelativeTo(mainFrame);
                addThemeForm.setVisible(true);
                addThemeForm.pack();

        }

        @Action
        public void quit() {
                Ucitel.getApplication().shutdown();
        }

        @Action
        public void batchLoader() {
                JFrame mainFrame = Ucitel.getApplication().getMainFrame();

                BatchLoaderForm batchLoaderForm;
                try {
                        batchLoaderForm = new BatchLoaderForm(mainFrame, true);
                        if (batchLoaderForm.isLoaded()) {
                                batchLoaderForm.setLocationRelativeTo(mainFrame);
                                batchLoaderForm.setVisible(true);
                                batchLoaderForm.pack();
                                direction.setDirection(direction.getLngFrom(), direction.getLngTo());
                                askTeacher();
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                        ErrorHandler.logError(e);
                        JOptionPane.showMessageDialog(GUI.getInstance().getComponent(), e.getMessage(), "Batch loader error", JOptionPane.OK_OPTION);
                }


        }

        @Action
        public void changeUser() {
                showLogin();

        }

        @Action
        public void tisk() {
        }
        // Variables declaration - do not modify//GEN-BEGIN:variables
        private javax.swing.JTextArea answerField;
        private javax.swing.JLabel answerLabel;
        private javax.swing.JLabel askAgainL;
        private javax.swing.JButton askButton;
        private javax.swing.JMenuItem changeUser;
        private javax.swing.JButton clear;
        private javax.swing.JLabel cnt0x;
        private javax.swing.JLabel cnt0xLabel;
        private javax.swing.JLabel cnt10x;
        private javax.swing.JLabel cnt10xLabel;
        private javax.swing.JLabel cnt11x;
        private javax.swing.JLabel cnt12x;
        private javax.swing.JLabel cnt13x;
        private javax.swing.JLabel cnt14x;
        private javax.swing.JLabel cnt15x;
        private javax.swing.JLabel cnt16x;
        private javax.swing.JLabel cnt16xLabel;
        private javax.swing.JLabel cnt17x;
        private javax.swing.JLabel cnt17xLabel;
        private javax.swing.JLabel cnt18x;
        private javax.swing.JLabel cnt18xLabel;
        private javax.swing.JLabel cnt19x;
        private javax.swing.JLabel cnt1x;
        private javax.swing.JLabel cnt1xLabel;
        private javax.swing.JLabel cnt2x;
        private javax.swing.JLabel cnt2xLabel;
        private javax.swing.JLabel cnt3x;
        private javax.swing.JLabel cnt3xLabel;
        private javax.swing.JLabel cnt4x;
        private javax.swing.JLabel cnt5x;
        private javax.swing.JLabel cnt6x;
        private javax.swing.JLabel cnt6xLabel;
        private javax.swing.JLabel cnt7x;
        private javax.swing.JLabel cnt7xLabel;
        private javax.swing.JLabel cnt8x;
        private javax.swing.JLabel cnt8xLabel;
        private javax.swing.JLabel cnt9x;
        private javax.swing.JLabel cnt9xLabel;
        private javax.swing.JButton deleteCurrentButton;
        private javax.swing.ButtonGroup directionBG;
        private javax.swing.JMenuItem export;
        private javax.swing.JButton fixButton;
        private javax.swing.JButton fixCurrentButton;
        private javax.swing.JRadioButton fromCzechRB;
        private javax.swing.JCheckBox grammarCB;
        private javax.swing.JTextArea hint;
        private javax.swing.JMenuItem importItem;
        private javax.swing.JButton jButton1;
        private javax.swing.JLabel jLabel1;
        private javax.swing.JLabel jLabel2;
        private javax.swing.JLabel jLabel22;
        private javax.swing.JLabel jLabel23;
        private javax.swing.JLabel jLabel24;
        private javax.swing.JLabel jLabel25;
        private javax.swing.JLabel jLabel26;
        private javax.swing.JLabel jLabel5;
        private javax.swing.JLabel jLabel6;
        private javax.swing.JMenu jMenu2;
        private javax.swing.JMenuItem jMenuItem1;
        private javax.swing.JMenuItem jMenuItem2;
        private javax.swing.JMenuItem jMenuItem3;
        private javax.swing.JMenuItem jMenuItem4;
        private javax.swing.JMenuItem jMenuItem5;
        private javax.swing.JPanel jPanel1;
        private javax.swing.JPanel jPanel10;
        private javax.swing.JPanel jPanel11;
        private javax.swing.JPanel jPanel12;
        private javax.swing.JPanel jPanel13;
        private javax.swing.JPanel jPanel14;
        private javax.swing.JPanel jPanel2;
        private javax.swing.JPanel jPanel3;
        private javax.swing.JPanel jPanel4;
        private javax.swing.JPanel jPanel5;
        private javax.swing.JPanel jPanel6;
        private javax.swing.JPanel jPanel7;
        private javax.swing.JPanel jPanel8;
        private javax.swing.JPanel jPanel9;
        private javax.swing.JProgressBar jProgressBar1;
        private javax.swing.JScrollPane jScrollPane1;
        private javax.swing.JScrollPane jScrollPane2;
        private javax.swing.JScrollPane jScrollPane3;
        private javax.swing.JScrollPane jScrollPane4;
        private javax.swing.JComboBox languageCB;
        private javax.swing.ButtonGroup languagesBG;
        private javax.swing.JPanel mainPanel;
        private javax.swing.JMenuBar menuBar;
        private javax.swing.JTextArea questionField;
        private javax.swing.JLabel questionLabel;
        private javax.swing.JTextArea resultLabel;
        private javax.swing.JMenu slovnikMenu;
        private javax.swing.JMenuItem smazatSlovicka;
        private javax.swing.JButton swapButton;
        private javax.swing.JComboBox themeCB;
        private javax.swing.JMenuItem tisk;
        private javax.swing.JRadioButton toCzechRB;
        private javax.swing.JLabel wordCntLabel;
        private javax.swing.JLabel wordLeftLabel;
        // End of variables declaration//GEN-END:variables
        private Teacher ucitel;
        //private DirectionOfTranslation direction;
        private ConnectionToDB conn;
        private static final ResourceBundle gUI = java.util.ResourceBundle.getBundle("ucitel/resources/GUI");
        private JDialog aboutBox;
        private FixForm fixForm;
        private String previousAnswer;
        private boolean userLogged = false;
        public static String vsechno = "Všechno";
        boolean grammar_initialized = false;
}
