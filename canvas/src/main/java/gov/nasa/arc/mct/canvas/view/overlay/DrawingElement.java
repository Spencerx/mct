package gov.nasa.arc.mct.canvas.view.overlay;

import java.awt.Rectangle;

public interface DrawingElement extends Drawable, Stringifiable {
	public ElementHandle getHandle(int x, int y);
}
