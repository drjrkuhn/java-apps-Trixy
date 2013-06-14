/*
 * XDomEchoPanel.java
 *
 * Created on May 1, 2005, 6:03 PM
 */

package kuhnlab.xml;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.util.*;


import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.xml.sax.*;

/**
 *
 * @author jkuhn
 */
public class XDomEchoPanel extends JPanel {
    
    // Global value so it can be ref'd by the tree-adapter
    static Document document;
    
    static final int windowHeight = 460;
    static final int leftWidth = 300;
    static final int rightWidth = 340;
    static final int windowWidth = leftWidth + rightWidth;
    
    public static JDialog makeDialog(Document document) {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { }
        
        // Set up a GUI framework
        JDialog frame = new JDialog((JFrame)null, "DOM Echo", true);
        //frame.addWindowListener(new WindowAdapter() {
        //    public void windowClosing(WindowEvent e) {System.exit(0);}
        //});
        
        // Set up the tree, the views, and display it all
        final XDomEchoPanel echoPanel = new XDomEchoPanel(document);
        frame.getContentPane().add("Center", echoPanel );
        frame.pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int w = windowWidth + 10;
        int h = windowHeight + 10;
        frame.setLocation(screenSize.width/3 - w/2, screenSize.height/2 - h/2);
        frame.setSize(w, h);
        frame.setVisible(true);
        return frame;
    } // makeFrame
    
    /** Creates a new instance of XDomEchoPanel */
    public XDomEchoPanel(Document document) {
        this.document = document;
        EmptyBorder eb = new EmptyBorder(5,5,5,5);
        BevelBorder bb = new BevelBorder(BevelBorder.LOWERED);
        CompoundBorder CB = new CompoundBorder(eb,bb);
        this.setBorder(new CompoundBorder(CB,eb));
        
        // Set up the tree
        JTree tree = new JTree(new DomToTreeModelAdapter());
        
        // Build left-side view
        JScrollPane treeView = new JScrollPane(tree);
        treeView.setPreferredSize(
                new Dimension( leftWidth, windowHeight ));
        
        // Build right-side view
        JEditorPane htmlPane = new JEditorPane("text/html","");
        htmlPane.setEditable(false);
        JScrollPane htmlView = new JScrollPane(htmlPane);
        htmlView.setPreferredSize(
                new Dimension( rightWidth, windowHeight ));
        
        // Build split-pane view
        JSplitPane splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT,
                treeView,
                htmlView );
        splitPane.setContinuousLayout( true );
        splitPane.setDividerLocation( leftWidth );
        splitPane.setPreferredSize(
                new Dimension( windowWidth + 10, windowHeight+10 ));
        
        // Add GUI components
        this.setLayout(new BorderLayout());
        this.add("Center", splitPane );
    }
    
    // An array of names for DOM node-types
    // (Array indexes = nodeType() values.)
    static final String[] typeName = {
        "none",
                "Element",
                "Attr",
                "Text",
                "CDATA",
                "EntityRef",
                "Entity",
                "ProcInstr",
                "Comment",
                "Document",
                "DocType",
                "DocFragment",
                "Notation",
    };
    
    public class AdapterNode {
        org.w3c.dom.Node domNode;
        
        // Construct an Adapter node from a DOM node
        public AdapterNode(org.w3c.dom.Node node) {
            domNode = node;
        }
        
        // Return a string that identifies this node in the tree
        // *** Refer to table at top of org.w3c.dom.Node ***
        public String toString() {
            String s = typeName[domNode.getNodeType()];
            String nodeName = domNode.getNodeName();
            if (! nodeName.startsWith("#")) {
                s += ": " + nodeName;
            }
            if (domNode.getNodeValue() != null) {
                if (s.startsWith("ProcInstr"))
                    s += ", ";
                else
                    s += ": ";
                // Trim the value to get rid of NL's at the front
                String t = domNode.getNodeValue().trim();
                int x = t.indexOf("\n");
                if (x >= 0) t = t.substring(0, x);
                s += t;
            }
            return s;
        }
        
        public int index(AdapterNode child) {
            //System.err.println("Looking for index of " + child);
            int count = childCount();
            for (int i=0; i<count; i++) {
                AdapterNode n = this.child(i);
                if (child == n) return i;
            }
            return -1; // Should never get here.
        }
        
        public AdapterNode child(int searchIndex) {
            //Note: JTree index is zero-based.
            org.w3c.dom.Node node = domNode.getChildNodes().item(searchIndex);
            return new AdapterNode(node);
        }
        
        public int childCount() {
            return domNode.getChildNodes().getLength();
        }
    } // AdapterNode
    
    // This adapter converts the current Document (a DOM) into
    // a JTree model.
    public class DomToTreeModelAdapter implements javax.swing.tree.TreeModel {
        // Basic TreeModel operations
        public Object  getRoot() {
            //System.err.println("Returning root: " +document);
            return new AdapterNode(document);
        }
        public boolean isLeaf(Object aNode) {
            // Determines whether the icon shows up to the left.
            // Return true for any node with no children
            AdapterNode node = (AdapterNode) aNode;
            if (node.childCount() > 0) return false;
            return true;
        }
        public int     getChildCount(Object parent) {
            AdapterNode node = (AdapterNode) parent;
            return node.childCount();
        }
        public Object  getChild(Object parent, int index) {
            AdapterNode node = (AdapterNode) parent;
            return node.child(index);
        }
        public int     getIndexOfChild(Object parent, Object child) {
            AdapterNode node = (AdapterNode) parent;
            return node.index((AdapterNode) child);
        }
        public void    valueForPathChanged(TreePath path, Object newValue) {
            // Null. We won't be making changes in the GUI
            // If we did, we would ensure the new value was really new
            // and then fire a TreeNodesChanged event.
        }
        
        private Vector listenerList = new Vector();
        public void addTreeModelListener( TreeModelListener listener ) {
            if ( listener != null && ! listenerList.contains( listener ) ) {
                listenerList.addElement( listener );
            }
        }
        public void removeTreeModelListener( TreeModelListener listener ) {
            if ( listener != null ) {
                listenerList.removeElement( listener );
            }
        }
        public void fireTreeNodesChanged( TreeModelEvent e ) {
            Enumeration listeners = listenerList.elements();
            while ( listeners.hasMoreElements() ) {
                TreeModelListener listener = (TreeModelListener) listeners.nextElement();
                listener.treeNodesChanged( e );
            }
        }
        public void fireTreeNodesInserted( TreeModelEvent e ) {
            Enumeration listeners = listenerList.elements();
            while ( listeners.hasMoreElements() ) {
                TreeModelListener listener = (TreeModelListener) listeners.nextElement();
                listener.treeNodesInserted( e );
            }
        }
        public void fireTreeNodesRemoved( TreeModelEvent e ) {
            Enumeration listeners = listenerList.elements();
            while ( listeners.hasMoreElements() ) {
                TreeModelListener listener = (TreeModelListener) listeners.nextElement();
                listener.treeNodesRemoved( e );
            }
        }
        public void fireTreeStructureChanged( TreeModelEvent e ) {
            Enumeration listeners = listenerList.elements();
            while ( listeners.hasMoreElements() ) {
                TreeModelListener listener = (TreeModelListener) listeners.nextElement();
                listener.treeStructureChanged( e );
            }
        }
        
    } // DomToTreeModelAdapter
}
