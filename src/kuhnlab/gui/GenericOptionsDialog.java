/*
 * GenericInputDialog.java
 *
 * Created on March 15, 2006, 12:14 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package kuhnlab.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import org.jdesktop.application.SingleFrameApplication;

/**
 *
 * @author drjrkuhn
 */
public class GenericOptionsDialog extends JDialog {
    
    protected JButton okButton;
    protected JButton cancelButton;
    JTabbedPane tabs;
    
    protected boolean canceled;
    protected static final int GAP_RELATED = 3;
    protected static final int GAP_CONTAINER = 6;
    
    /** Creates a new instance of GenericInputDialog */
    public GenericOptionsDialog(SingleFrameApplication app, String title) {
        this(app.getMainFrame(), title);
    }
    
    /** Creates a new instance of GenericInputDialog */
    public GenericOptionsDialog(Frame parent, String title) {
        super(parent, true);
        canceled = true;
        
        this.setLocationRelativeTo(parent);
        this.setTitle(title);
        this.setLayout(new BorderLayout());
        
        tabs = new JTabbedPane(JTabbedPane.TOP);

        this.add(tabs, BorderLayout.CENTER);

        okButton = new JButton(new AbstractAction("OK") {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
        
        cancelButton = new JButton(new AbstractAction("Cancel") {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });
        
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, GAP_RELATED, 0));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(GAP_CONTAINER, 0, 0, 0));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        this.add(buttonPanel, BorderLayout.SOUTH);
        
        getRootPane().setDefaultButton(okButton);
    }
    
    public void addOptionsTab(String tabText, GenericOptionsPanel optionsPanel) {
        tabs.add(tabText, optionsPanel);
    }
    
    //-----------------------------------------------------------------------
    // Actions
    //-----------------------------------------------------------------------

    protected void onOK() {
        canceled = false;
        int numTabs = tabs.getTabCount();
        for (int i=0; i<numTabs; i++) {
            Component comp = tabs.getComponentAt(i);
            if (comp instanceof GenericOptionsPanel) {
                ((GenericOptionsPanel)comp).retrieveItems();
            }
        }
        dispose();
    }
    
    protected void onCancel() {
        canceled = true;
        dispose();
    }
    
    // Allow the ESCAPE key to close
    protected JRootPane createRootPane() {
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                onCancel();
            }
        };
        JRootPane rootPane = new JRootPane();
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        rootPane.registerKeyboardAction(actionListener, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
        return rootPane;
    }
    
    
    public boolean showDialog() {
        pack();
        setVisible(true);
        // wait for modal exit;
        return !wasCanceled();
    }
    
    public boolean wasCanceled() {
        return canceled;
    }
    
    //=======================================================================
    // TEST
    //=======================================================================
    
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Object[] valuesA = {"Hello, World!", new Integer(100), new Boolean(true),
                    new Double(200), new Boolean(false), new Boolean (true)};
                GenericOptionsPanel opA = new GenericOptionsPanel("Options A", "Enter Test Values");
                opA.addString("String:", valuesA, 0, 12);
                opA.addSeparator("Separator");
                opA.addInteger("Integer:", valuesA, 1, 4);
                opA.addBoolean("Boolean:", valuesA, 2, true);
                opA.addDouble("Double:", valuesA, 3, 6);
                opA.addBoolean("Option 1", valuesA, 4, false);
                opA.addBoolean("Option 2", valuesA, 5, false);
                
                
                Object[] valuesB = {new Integer(500), new Boolean(true),
                    "Foobar!"};
                GenericOptionsPanel opB = new GenericOptionsPanel("Options B", null);
                opB.addInteger("Integer:", valuesB, 0, 10);
                opB.addBoolean("Boolean:", valuesB, 1, true);
                opB.addSeparator("Separator");
                opB.addString("String:", valuesB, 2, 15);
                
                
                GenericOptionsDialog gd = new GenericOptionsDialog((Frame)null, "GenericOptionsDialog");
                gd.addOptionsTab("Options A", opA);
                gd.addOptionsTab("Options B", opB);
                
                if (gd.showDialog()) {
                    System.out.println("Dialog input was approved");
                } else {
                    System.out.println("Dialog input was canceled");
                }
                System.out.println("Options A");
                System.out.println("  String = " + valuesA[0]);
                System.out.println("  Integer = " + valuesA[1]);
                System.out.println("  Boolean = " + valuesA[2]);
                System.out.println("  Double = " + valuesA[3]);
                System.out.println("  Option 1 = " + valuesA[4]);
                System.out.println("  Option 2 = " + valuesA[5]);
                
                System.out.println("Options B");
                System.out.println("  Integer = " + valuesB[0]);
                System.out.println("  Boolean = " + valuesB[1]);
                System.out.println("  String = " + valuesB[2]);
            }
        });
    }
    
}
