/*
 * SeriesJList.java
 *
 * Created on March 13, 2006, 5:15 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package kuhnlab.trixy;

import kuhnlab.trixy.data.SeriesList;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;
import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;
import org.jfree.chart.renderer.xy.XYItemRenderer;

/**
 *
 * @author drjrkuhn
 */
public class SeriesJList extends JList implements DragGestureListener,
        DropTargetListener, DragSourceListener {
    
    protected SeriesList data;
    
    protected SeriesList selected;
    protected DragSource dragSource;
    protected DropTarget dropTarget;
    protected DragGestureRecognizer dragGestureRecognizer;
    protected XYItemRenderer chartRenderer;
    
    protected static final ListModel emptyModel = new AbstractListModel() {
        public int getSize() { return 0; }
        public Object getElementAt(int i) { return "Empty ListModel"; }
    };
    
    public SeriesJList() {
        super();
        clearSeriesList();
        dragSource = DragSource.getDefaultDragSource() ;
        dropTarget = new DropTarget(this, this);
        dragGestureRecognizer = dragSource.createDefaultDragGestureRecognizer(
                this, DnDConstants.ACTION_COPY_OR_MOVE, this);
        
    }
    
    public void setSeriesList(SeriesList data, XYItemRenderer chartRenderer) {
        this.data = data;
        SeriesList.SeriesListModel model = data.getListModel();
        this.chartRenderer = chartRenderer;
        setModel(model);
        setCellRenderer(data.makeListRenderer(chartRenderer));
        setEnabled(true);
        setOpaque(true);
    }
    
    public void clearSeriesList() {
        data = null;
        clearSelection();
        // create an empty list model
        setModel(emptyModel);
        setCellRenderer(null);
        setEnabled(false);
        setOpaque(false);
        chartRenderer = null;
    }
    
    public void addListDataListener(ListDataListener listener) {
        if (data == null) return;
        data.getListModel().addListDataListener(listener);
    }
    
    public void removeListDataListener(ListDataListener listener) {
        if (data == null) return;
        data.getListModel().removeListDataListener(listener);
    }
    
    public int targetIndexFromLocation(Point pt) {
        //set cursor location
        int target = locationToIndex(pt);
        Rectangle bounds = super.getCellBounds(target, target);
        if (pt.y > bounds.y + bounds.height/2) {
            // insert after this index instead of before it
            target++;
        }
        return target;
    }
    
    //DEBUG
    //protected String actionToString(int action) {
    //    switch (action) {
    //        case DnDConstants.ACTION_COPY:
    //            return "ACTION_COPY";
    //        case DnDConstants.ACTION_COPY_OR_MOVE:
    //            return "ACTION_COPY_OR_MOVE";
    //        case DnDConstants.ACTION_LINK:
    //            return "ACTION_LINK";
    //        case DnDConstants.ACTION_MOVE:
    //            return "ACTION_MOVE";
    //        case DnDConstants.ACTION_NONE:
    //            return "ACTION_NONE";
    //        default:
    //            return "ACTION_Unknown";
    //    }
    //}
    
    protected Cursor actionToCursor(int action) {
        switch (action) {
            case DnDConstants.ACTION_COPY:
                return DragSource.DefaultCopyDrop;
            case DnDConstants.ACTION_COPY_OR_MOVE:
                return DragSource.DefaultCopyDrop;
            case DnDConstants.ACTION_LINK:
                return DragSource.DefaultLinkDrop;
            case DnDConstants.ACTION_MOVE:
                return DragSource.DefaultMoveDrop;
            default:
                return DragSource.DefaultMoveNoDrop;
        }
    }
    
    //=======================================================================
    // DragGestureListener implementation
    //=======================================================================
    public void dragGestureRecognized(DragGestureEvent dge) {
        if (data == null) return;
        int[] selectedIndices = this.getSelectedIndices();
        if (selectedIndices.length != 0) {
            selected = data.subList(selectedIndices);
            // Get the series as a transferable object
            Transferable transfer = (Transferable) selected;
            
            // Select the appropriate cursor;
            int action = dge.getDragAction();
            //System.out.println("dragGestureRecognized " + actionToString(action)+" action");
            Cursor cursor = actionToCursor(action);
            
            //begin the drag
            dragSource.startDrag(dge, cursor, transfer, this);
        }
    }
    
    //=======================================================================
    // DropTargetListener implementation
    //=======================================================================
    public void dragEnter(DropTargetDragEvent dtde) {
    }
    
    public void dragOver(DropTargetDragEvent dtde) {
        if (data == null) return;
        if (!data.canImport(dtde.getCurrentDataFlavors())) {
            dtde.rejectDrag();
            return;
        }
        
        int destIndex = targetIndexFromLocation(dtde.getLocation());
        //System.out.println("Drag destination index = "+destIndex);
        
        if (destIndex >= 0 && destIndex <= data.getSeriesCount()) {
            dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
        } else {
            dtde.rejectDrag() ;
        }
    }
    
    public void dropActionChanged(DropTargetDragEvent dtde) {
    }
    
    public void dragExit(DropTargetEvent dte) {
    }
    
    public void drop(DropTargetDropEvent dtde) {
        if (data == null) return;
        try {
            Transferable transfer = dtde.getTransferable();
            if (!data.canImport(transfer.getTransferDataFlavors())) {
                dtde.rejectDrop();
                return;
            }
            
            dtde.acceptDrop(dtde.getDropAction());
            int destIndex = targetIndexFromLocation(dtde.getLocation());
            //System.out.print("Final Drag destination index = "+destIndex);
            //System.out.println(" " + actionToString(dtde.getDropAction())+" action");
            
            if (destIndex < 0 || destIndex > 1+data.getSeriesCount()) {
                dtde.rejectDrop();
                return;
            }
            
            data.saveVisibility(chartRenderer);
            if (!data.insertTransferData(destIndex, transfer)) {
                data.restoreVisibility(chartRenderer, true);
                dtde.rejectDrop();
                return;
            }
            data.restoreVisibility(chartRenderer, true);
            clearSelection();
            dtde.dropComplete(true);
            //dtde.acceptDrop(dtde.getDropAction());
        } catch (IOException ex) {
            dtde.dropComplete(false);
        } catch (UnsupportedFlavorException ex) {
            dtde.dropComplete(false);
        }
    }
    
    //=======================================================================
    // DragSourceListener implementation
    //=======================================================================
    public void dragEnter(DragSourceDragEvent dsde) {
    }
    
    public void dragOver(DragSourceDragEvent dsde) {
    }
    
    public void dropActionChanged(DragSourceDragEvent dsde) {
        DragSourceContext context = dsde.getDragSourceContext();
        //intersection of the users selected action, and the source and target
        context.setCursor(actionToCursor(dsde.getDropAction()));
    }
    
    public void dragExit(DragSourceEvent dse) {
    }
    
    public void dragDropEnd(DragSourceDropEvent dsde) {
        if (data == null) return;
        //System.out.print("dragDropEnd " + actionToString(dsde.getDropAction())+" action ");
        //System.out.println(dsde.getDropSuccess()?"from successful drop":"from unsuccessful drop");
        if (dsde.getDropSuccess() && dsde.getDropAction() == DnDConstants.ACTION_MOVE
                && selected != null) {
            //System.out.println(" removing old data");
            // remove the old data
            data.saveVisibility(chartRenderer);
            data.removeSeries(selected.getSeries());
            data.restoreVisibility(chartRenderer, true);
            selected = null;
        }
    }
}
