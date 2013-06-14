/*
 * XDomItem.java
 *
 * Created on April 28, 2005, 9:49 AM
 */

package kuhnlab.xml;
import org.w3c.dom.*;
import java.util.*;

/**
 * @author drjrkuhn
 */
public class XDomItem {
    
    public static final String tagNull = "_null_";
    public static final String tagLength = "length";
    public static final String tagSize = "size";

    /** DOM Node */
    public Node node;
    
    //=======================================================================
    // Constructors
    //=======================================================================
    
    /** 
     * Construct an XDomItem with a given DOM node. 
     * NOTE: Only this class and the factory has access to this constructor.
      */
    protected XDomItem(Node node) {
        this.node = node;
    }
    
    /**
     * Construct a root XDomItem corresponding to a root Document
     */
    public XDomItem(Document doc) {
        this.node = doc;
    }
    
    public Document getOwnerDocument() {
        return isRoot() ? (Document)node : node.getOwnerDocument();
    }
    
    //=======================================================================
    // Inner class for constructing new XDomItems from old XDomItems
    //=======================================================================
    public Factory getFactory() {
        if (node == null) return null;
        return new Factory(getOwnerDocument());
    }
    
    public class Factory {
        /** The document used to create new nodes */
        protected Document doc;

        /** Only an XDomItem can create a new Factory object */
        protected Factory(Document doc) {
            this.doc = doc;
        }
        /** Create a new, unattached Element node */
        public XDomItem createElement (String name) {
            return new XDomItem(doc.createElement(name));
        }
        /** Create a new, unattached Comment node */
        public XDomItem createComment (String comment) {
            return new XDomItem(doc.createComment(comment));
        }
        /** Create a new, unattached Text node */
        public XDomItem createText (String data) {
            return new XDomItem(doc.createTextNode(data));
        }
    }
    
    //=======================================================================
    // static helper methods
    //=======================================================================
    
    /** Determine if a DOM Node is of type ELEMENT_NODE. */
    public static boolean isElement(Node node) {
        if (node == null) return false;
        return node.getNodeType() == Node.ELEMENT_NODE;
    }
    
    /** Determine if a DOM Node is of type ELEMENT_NODE and named 'name'. */
    public static boolean isElementNamed(Node node, String name) {
        if (node == null) return false;
        if (isElement(node)) {
            if (name == null) {
                return true;
            } else {
                String nodeName = node.getNodeName();
                return name.equals(nodeName);
            }
        } else {
            return false;
        }
    }
    
    /** Determine if a DOM Node is of type COMMENT_NODE. */
    public static boolean isComment(Node node) {
        if (node == null) return false;
        return node.getNodeType() == Node.COMMENT_NODE;
    }
    
    /** Determine if a DOM Node is of type TEXT_NODE. */
    public static boolean isText(Node node) {
        if (node == null) return false;
        return node.getNodeType() == Node.TEXT_NODE;
    }
    
    //=======================================================================
    // Helper methods
    //=======================================================================
    /** Determine if this DOM Node is of type ELEMENT_NODE. */
    public boolean isElement() {
        return isElement(node);
    }
    
    /** Determine if this DOM Node is of type ELEMENT_NODE and named 'name'. */
    public boolean isElementNamed(String name) {
        return isElementNamed(node, name);
    }
    
    /** Determine if this DOM Node is of type COMMENT_NODE. */
    public boolean isComment() {
        return isComment(node);
    }
    
    /** Determine if this DOM Node is of type TEXT_NODE. */
    public boolean isText() {
        return isText(node);
    }
    
    /** Determine if this DOM Node is the root Document node. */
    public boolean isRoot() {
        if (node == null) return false;
        return node.getNodeType() == Node.DOCUMENT_NODE;
    }

    /** Assuming this node is an element node, set an attribute value.*/
    public boolean setAttribute(String name, String value) {
        if (!isElement()) return false;
        ((Element)node).setAttribute(name, value);
        return true;
    }
    
    /** Assuming this node is an element node, get an attribute value.*/
    public String getAttribute(String name) {
        if (!isElement()) return null;
        String attr = ((Element)node).getAttribute(name);
        return (attr != null && attr.length() > 0) ? attr : null;
    }
    
    /** Adds 'item' as a child of this node */
    public boolean addChild(XDomItem item) {
        if (node == null || item == null) return false;
        if (item.node == null) return false;
        node.appendChild(item.node);
        return true;
    }
    
    /** Remove all child nodes of this node.*/
    public void clearChildren() {
        if (node == null) return;
        NodeList nlc = node.getChildNodes();
        for (int ni = 0; ni < nlc.getLength(); ni++) {
            node.removeChild(nlc.item(0));
        }
    }

    /** Retrieve a list of all child nodes of type ELEMENT_NODE. */
    public Collection<XDomItem> getChildElements() {
        if (node == null) return null;
        NodeList nlc = node.getChildNodes();
        int nc = nlc.getLength();
        ArrayList<XDomItem> lobj = new ArrayList<XDomItem>(nc);
        for (int ic=0; ic<nc; ic++) {
            Node nchild = nlc.item(ic);
            if (isElement(nchild)) {
                lobj.add(new XDomItem(nchild));
            }
        }
        return lobj;
    }
    
    /** Retrieve a list of all child nodes of type ELEMENT_NODE
     *  and named 'name'.*/
    public Collection<XDomItem> getChildElementsNamed(String name) {
        if (node == null) return null;
        NodeList nlc = node.getChildNodes();
        int nc = nlc.getLength();
        ArrayList<XDomItem> lobj = new ArrayList<XDomItem>(nc);
        for (int ic=0; ic<nc; ic++) {
            Node nchild = nlc.item(ic);
            if (isElementNamed(nchild, name)) {
                lobj.add(new XDomItem(nchild));
            }
        }
        return lobj;
    }
    
    /** Get the first unnamed child node of type ELEMENT_NODE */
    public XDomItem getFirstChildElement() {
        if (node == null) return null;
        Node child = node.getFirstChild();
        while (child != null) {
            if (isElement(child)) {
                return new XDomItem(child);
            }
            child = child.getNextSibling();
        }
        return null;
    }
    
    /** Get the first child node of type ELEMENT_NODE and named 'name'. */
    public XDomItem getFirstChildElementNamed(String name) {
        if (node == null) return null;
        Node nchild = node.getFirstChild();
        while (nchild != null) {
            if (isElementNamed(nchild, name)) {
                return new XDomItem(nchild);
            }
            nchild = nchild.getNextSibling();
        }
        return null;
    }
    
    /** Get the first unnamed child node of type TEXT_NODE */
    public XDomItem getFirstChildText() {
        if (node == null) return null;
        Node child = node.getFirstChild();
        while (child != null) {
            if (isText(child)) {
                return new XDomItem(child);
            }
            child = child.getNextSibling();
        }
        return null;
    }
    
    /** A "container" is a node that contains either a single child node
     *  representing a single item or a number of child nodes of the same
     *  type to contain a collection of items.
     *
     *  This method creates a new "container" with the designated
     *  name and set it as a child of this node. If there is already 
     *  a child with this name, assume it is the container and empty
     *  out its child list. */
    public XDomItem createContainer(String name) {
        if (name==null) return null;
        XDomItem container = getFirstChildElementNamed(name);
        if (container == null) {
            // create a new container and add it as a child of this item
            Factory factory = getFactory();
            if (factory == null) return null;
            container = factory.createElement(name);
            if (container == null) return null;
            addChild(container);
        } else {
            // A container with this name already exists. Empty it
            container.clearChildren();
        }
        return container;
    }
    
    public XDomItem getContainer(String name) {
        return getFirstChildElementNamed(name);
    }
    
    //=======================================================================
    // Main interface to XDomItem
    //=======================================================================
    
    /** Creates a new child container and adds a new text node to it */
    public boolean setText(String name, String data) {
        if (node == null) return false;
        if (data == null) return false;
        XDomItem container = createContainer(name);
        if (container == null) return false;
        Factory factory = getFactory();
        if (factory == null) return false;
        XDomItem text = factory.createText(data);
        return container.addChild(text);
    }
    
    /** Gets the first child container of 'name' and retrieves the contents
     *  of its text node child */
    public String getText(String name) {
        if (node == null) return null;
        XDomItem container = getContainer(name);
        if (container == null) return null;
        XDomItem text = container.getFirstChildText();
        if (text == null) return null;
        String data = ((Text)text.node).getData();
        return (data.length() == 0) ? null : data;
    }
    
    /** Add a new node named 'name' with only one child node
     *  node containing an 'node'. */ 
    public boolean setItem(String name, XDomItem item) {
        if (item == null) return false;
        XDomItem container = createContainer(name);
        if (container == null) return false;
        return container.addChild(item);
    }
    
    /** Find a child node named 'name' and return its first unnamed
     *  node node. */
    public XDomItem getItem(String name) {
        if (node == null) return null;
        XDomItem container = getContainer(name);
        return (container == null) ? null : container.getFirstChildElement();
    }
    
    /** Add a new node name 'name', set an attribute 'length' and 
     *  add an array XDomItems to it. */
    public boolean setCollection(String name, Collection<XDomItem> elements) {
        if (node == null) return false;
        XDomItem container = createContainer(name);
        if (container == null) return false;
        Factory factory = getFactory();
        if (factory == null) return false;
        container.setAttribute(tagSize, Integer.toString(elements.size()));
        for (XDomItem obj : elements) {
            if (obj == null) {
                XDomItem nullObj = factory.createElement(tagNull);
                container.addChild(nullObj);
            } else {
                container.addChild(obj);
            }
        }
        return true;
    }
    
    /** Finds an node named 'name' and reads an array of elements from it. */
    public Collection<XDomItem> getCollection(String name) {
        if (node == null) return null;
        XDomItem container = getContainer(name);
        if (container == null) return null;
        int length = Integer.parseInt(container.getAttribute(tagSize));
        Collection<XDomItem> lsrc = container.getChildElements();
        // Translate the child elements to another list with proper nulls
        if (lsrc.size() > length)
            length = lsrc.size();
        Collection<XDomItem> ldest = new ArrayList<XDomItem>(length);
        for (XDomItem osrc : lsrc) {
            if (osrc == null) {
                ldest.add(null);
            } else if (osrc.isElementNamed(tagNull)) {
                ldest.add(null);
            } else {
                ldest.add(osrc);
            }
        }
        return ldest;
    }
    
    /** Add a new node name 'name', set an attribute 'length' and 
     *  add an array XDomItems to it. */
    public boolean setArray(String name, XDomItem[] elements) {
        if (node == null) return false;
        XDomItem container = createContainer(name);
        if (container == null) return false;
        Factory factory = getFactory();
        if (factory == null) return false;
        container.setAttribute(tagLength, Integer.toString(elements.length));
        for (int i=0; i<elements.length; i++) {
            XDomItem obj = elements[i];
            if (obj == null) {
                XDomItem nullObj = factory.createElement(tagNull);
                container.addChild(nullObj);
            } else {
                container.addChild(obj);
            }
        }
        return true;
    }
    
    /** Finds an node named 'name' and reads an array of elements from it. */
    public XDomItem[] getArray(String name) {
        if (node == null) return null;
        XDomItem container = getContainer(name);
        if (container == null) return null;
        int length = Integer.parseInt(container.getAttribute(tagLength));
        Collection<XDomItem> lsrc = container.getChildElements();
        if (lsrc.size() > length)
            length = lsrc.size();
        XDomItem destobjs[] = new XDomItem[length];
        int i=0;
        for (XDomItem osrc : lsrc) {
            if (osrc == null)
                destobjs[i] = null;
            else if (osrc.isElementNamed(tagNull)) {
                destobjs[i] = null;
            } else {
                destobjs[i] = osrc;
            }
        }
        return destobjs;
    }
    
    /** Sets a boolean element of this node. */
    public boolean setBoolean(String name, boolean value) {
        return setText(name, Boolean.toString(value));
    }
    /** Gets a boolean element of this node. */
    public boolean getBoolean(String name, boolean defaultValue) {
        String s = getText(name);
        return (s != null) ? Boolean.parseBoolean(s) : defaultValue;
    }
    
    /** Sets an integer element of this node. */
    public boolean setInteger(String name, int value) {
        return setText(name, Integer.toString(value));
    }
    /** Gets an integer element of this node. */
    public int getInteger(String name, int defaultValue) {
        String s = getText(name);
        return (s != null) ? Integer.parseInt(s) : defaultValue;
    }
    
    /** Sets a double element of this node. */
    public boolean setDouble(String name, double value) {
        return setText(name, Double.toString(value));
    }
    /** Gets a double element of this node. */
    public double getDouble(String name, double defaultValue) {
        String s = getText(name);
        return (s != null) ? Double.parseDouble(s) : defaultValue;
    }
    /** Sets a string element of this node. */
    public boolean setString(String name, String value) {
        return setText(name, (value == null) ? "" : value);
    }
    /** Gets a string element of this node. */
    public String getString(String name, String defaultValue) {
        String s = getText(name);
        return (s != null) ? s : defaultValue;
    }
}
