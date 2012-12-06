package gov.nasa.arc.mct.canvas.view.overlay;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public interface CanvasOverlay extends Drawable, MouseListener, MouseMotionListener, Stringifiable {
    public void   addOverlayListener(OverlayListener listener);
    public String getName();
}
