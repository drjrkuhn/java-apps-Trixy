/*
 * XDom.java
 *
 * Created on April 28, 2005, 9:50 AM
 */

package kuhnlab.xml;

import java.io.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.xml.sax.*;

/**
 *
 * @author drjrkuhn
 */
public class XDom {
    
   DocumentBuilderFactory factory;
   DocumentBuilder builder;
   Document doc;
   
   public XDom() throws ParserConfigurationException, 
           TransformerConfigurationException, TransformerException  {
       factory = DocumentBuilderFactory.newInstance();
       builder = factory.newDocumentBuilder();
   }
   
   /** Allow only one root item which must be named. */
   public XDomItem createRoot() {
       try {
           doc = builder.newDocument();
           return new XDomItem(doc);
       } catch (Exception e) {
           return null;
       }
    }
   
    /** Write a single root object to XML */
    public void writeXML(StreamResult result, XDomItem root) throws Exception {
        TransformerFactory transfac = TransformerFactory.newInstance();
        Transformer trans = transfac.newTransformer();
        trans.setOutputProperty(OutputKeys.INDENT, "yes");
        //Document doc = root.getOwnerDocument();
        //DOMSource source = new DOMSource(doc);
        DOMSource source = new DOMSource(root.node);
        trans.transform(source, result);
    }

    /** Read a single root object named 'rootName' to XML */
    public XDomItem readXML(InputSource input) throws Exception {
        doc = builder.parse(input);
        return new XDomItem(doc);
    }
    
    public void writeXML(Writer out, XDomItem root) throws Exception {
        writeXML(new StreamResult(out), root);
    }
    public void writeXML(OutputStream out, XDomItem root)  throws Exception {
        writeXML(new StreamResult(out), root);
    }
    public void writeXML(File out, XDomItem root)  throws Exception {
        writeXML(new StreamResult(out), root);
    }
    public XDomItem readXML(Reader in)  throws Exception {
        return readXML(new InputSource(in));
    }
    public XDomItem readXML(InputStream in, String rootName)  throws Exception {
        return readXML(new InputSource(in));
    }
    public XDomItem readXML(File in, String rootName)  throws Exception {
        return readXML(new InputSource(new FileInputStream(in)));
    }
}
