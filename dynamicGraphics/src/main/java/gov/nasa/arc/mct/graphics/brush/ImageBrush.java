package gov.nasa.arc.mct.graphics.brush;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;

public class ImageBrush extends Brush {
	private Image image;
	
	public ImageBrush(Image img) {
		image = img;
	}

	@Override
	protected void drawTransformed(Shape s, Graphics2D g) {
		g.drawImage(image, 0, 0, g.getClipBounds().width, g.getClipBounds().height, null);
	}
}
