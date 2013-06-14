/*
 * XDomTranslate.java
 *
 * Created on April 28, 2005, 9:49 AM
 */

package kuhnlab.xml;

/**
 *
 * @author drjrkuhn
 */
public interface XDomTranslate {
    /** Create an XML Element from this object. */
    abstract public XDomItem toXDomItem (XDomItem.Factory factory);
    
    /**
     * Fill in this object from an XML element.
     * 
     * This function can only fill the members of an existing object.
     * I suggest writing an additional static helper function which
     * creates a new object and fills it using this method. <P>
     * For example, if the class was FooBar, add a function of the form:<P>
     * <CODE>
     * public static FooBar FooBar_fromXDomItem(XDomItem item) {
     *     FooBar f = new FooBar();    // construct an empty foobar
     *     if (f.fromXDomItem(item))
     *         return f;               // translation was successful
     *     else
     *         return null;            // translation failed
     * }
     * </CODE>
     */
    abstract public boolean fromXDomItem (XDomItem item);
    
}
