package gov.nasa.arc.mct.graphics.view.drawing;

import java.awt.Rectangle;

public interface DrawingElement extends Drawable {
	public ElementHandle getHandle(int x, int y, Rectangle bounds);
}
