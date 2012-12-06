package gov.nasa.arc.mct.canvas.view.overlay;

import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCanvasOverlay extends MouseAdapter implements CanvasOverlay {
    private List<OverlayListener> listeners = new ArrayList<OverlayListener>();
    
    /* (non-Javadoc)
     * @see gov.nasa.arc.mct.canvas.view.overlay.CanvasOverlay#addOverlayListener(gov.nasa.arc.mct.canvas.view.overlay.OverlayListener)
     */
    @Override
    public void addOverlayListener(OverlayListener listener) {
        listeners.add(listener);
    }
    
    public void fireOverlayUpdating() {
        for (OverlayListener listener : listeners) listener.overlayUpdating();
    }

    public void fireOverlayUpdated() {
        for (OverlayListener listener : listeners) listener.overlayUpdated();
    }
}
