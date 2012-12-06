package gov.nasa.arc.mct.graphics.view.drawing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

public class Line implements DrawingElement {
	private static final int INTERNAL_RESOLUTION = 10000;
	private Color color = Color.RED;
	private Point start, end;
	
	public Line(double x1, double y1, double x2, double y2) {
		start = new Point();
		end   = new Point();
		setPoint(0, x1, y1);
		setPoint(1, x2, y2);
	}
	
	public void setPoint(int index, int x, int y, Rectangle bounds) {
		setPoint(index, (double) x / (double) bounds.width, (double) y / (double) bounds.height);
	}
	
	public void setPoint(int index, double x, double y) {
		Point target = index == 0 ? start : end;
		target.x = (int) (x * INTERNAL_RESOLUTION);
		target.y = (int) (y * INTERNAL_RESOLUTION);
	}
	
	public void setColor(Color c) {
		color = c;
	}
	
	public ElementHandle getEndHandle() {
		return new PointHandle(1);
	}

	@Override
	public void draw(Graphics g, Rectangle bounds) {
		Color oldColor = g.getColor();
		g.setColor(color);
		g.drawLine(bounds.x + (int) (start.getX() * bounds.width / INTERNAL_RESOLUTION),
				bounds.y + (int) (start.getY() * bounds.height / INTERNAL_RESOLUTION),
				bounds.x + (int) (end.getX() * bounds.width / INTERNAL_RESOLUTION),
				bounds.y + (int) (end.getY() * bounds.height / INTERNAL_RESOLUTION)
				);
		g.setColor(oldColor);
	}

	@Override
	public ElementHandle getHandle(int x, int y, Rectangle bounds) {
		int ix = (int) ((x-bounds.x) * INTERNAL_RESOLUTION / bounds.width);
		int iy = (int) ((y-bounds.y) * INTERNAL_RESOLUTION / bounds.height);
		double px = INTERNAL_RESOLUTION / (double) bounds.width;
		double py = INTERNAL_RESOLUTION / (double) bounds.height;
		
		if (ix < Math.min(start.x, end.x) - 8*px || ix  > Math.max(start.x, end.x) + 8*px || 
			iy < Math.min(start.y, end.y) - 8*py || iy  > Math.max(start.y, end.y) + 8*py) {			
			return null;			
		}

		double startDist = Math.sqrt(Math.pow((ix-start.x)/px, 2) + Math.pow((iy-start.y)/py,2)); 
		if ( startDist < 8.0) {
			 return new PointHandle(0);
		}
			
		double endDist = Math.sqrt(Math.pow((ix-end.x)/px, 2) + Math.pow((iy-end.y)/py,2)); 		
		if ( endDist < 8.0) {
			 return new PointHandle(1);
		}
			
		int dx = end.x - start.x;
		int dy = end.y - start.y;
		double length = Math.sqrt(dx * dx + dy * dy);
		double u = -(dy / length); // Perpendicular unit vector
		double v =   dx / length ; 
	    double b =   u * start.x + v * start.y; // Distance
	    double c =   u * ix      + v * iy;      // Distance of observed point
		
		// pixel proximity to line
		double pix = ((c-b) / INTERNAL_RESOLUTION) * Math.min(bounds.width, bounds.height); 	

		return (pix*pix <= 1.0) ? new LineHandle(ix,iy) : null;
	}

	private class PointHandle implements ElementHandle {
		private int index;
		public PointHandle(int i) {
			index = i;
		}
		@Override
		public void moveTo(int x, int y, Rectangle bounds) {
			setPoint(index, x, y, bounds);
		}
	}
	
	private class LineHandle implements ElementHandle {
		private int x;
		private int y;
		
		private Point s, e;
		
		public LineHandle(int ix, int iy) {
			x = ix;
			y = iy;
			s = new Point(start.x, start.y);
			e = new Point(end.x,   end.y);
		}

		@Override
		public void moveTo(int x, int y, Rectangle bounds) {
			int ix = (int) ((x-bounds.x) * INTERNAL_RESOLUTION / bounds.width);
			int iy = (int) ((y-bounds.y) * INTERNAL_RESOLUTION / bounds.height);
			
			start.x = s.x + (ix - this.x);
			start.y = s.y + (iy - this.y);
			end.x   = e.x + (ix - this.x);
			end.y   = e.y + (iy - this.y);
		}		
	}
	
}
