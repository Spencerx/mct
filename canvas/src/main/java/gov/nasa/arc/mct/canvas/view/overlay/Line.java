package gov.nasa.arc.mct.canvas.view.overlay;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

public class Line implements DrawingElement {
	private Color color = Color.RED;
	private Point start, end;
	
	public Line(int x1, int y1, int x2, int y2) {
		start = new Point();
		end   = new Point();
		setPoint(0, x1, y1);
		setPoint(1, x2, y2);
	}
	
	public void setPoint(int index, int x, int y) {
		Point target = index == 0 ? start : end;
		target.x = x;
		target.y = y;
	}
	
	public void setColor(Color c) {
		color = c;
	}
	
	public ElementHandle getEndHandle() {
		return new PointHandle(1);
	}

	@Override
	public void draw(Graphics g) {
		Color oldColor = g.getColor();
		g.setColor(color);
		g.drawLine(start.x, start.y, end.x, end.y);
		g.setColor(oldColor);
	}

	@Override
	public ElementHandle getHandle(int x, int y) {
		if (x < Math.min(start.x, end.x) - 8 || x  > Math.max(start.x, end.x) + 8 || 
			y < Math.min(start.y, end.y) - 8 || y  > Math.max(start.y, end.y) + 8) {			
			return null;			
		}

		double startDist = Math.sqrt(Math.pow((x-start.x), 2) + Math.pow((y-start.y),2)); 
		if ( startDist < 8.0) {
			 return new PointHandle(0);
		}
			
		double endDist = Math.sqrt(Math.pow((x-end.x), 2) + Math.pow((y-end.y),2)); 		
		if ( endDist < 8.0) {
			 return new PointHandle(1);
		}
			
		int dx = end.x - start.x;
		int dy = end.y - start.y;
		double length = Math.sqrt(dx * dx + dy * dy);
		double u = -(dy / length); // Perpendicular unit vector
		double v =   dx / length ; 
	    double b =   u * start.x + v * start.y; // Distance
	    double c =   u * x      + v * y ;       // Distance of observed point
		
		// pixel proximity to line
		double pix = c - b; 	

		return (c - b > -8 && c - b < 8) ? new LineHandle(x,y) : null;
	}

	private class PointHandle implements ElementHandle {
		private int index;
		public PointHandle(int i) {
			index = i;
		}
		@Override
		public void moveTo(int x, int y) {
			setPoint(index, x, y);
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
		public void moveTo(int x, int y) {
			start.x = s.x + (x - this.x);
			start.y = s.y + (y - this.y);
			end.x   = e.x + (x - this.x);
			end.y   = e.y + (y - this.y);
		}		
	}

	
	//TODO: This would be better handled by JAXB or similar?
    @Override
    public String stringify() {        
        return "" + start.x + "," + start.y + "," + end.x + "," + end.y;
    }

    @Override
    public void destringify(String str) {
        String[] parts = str.split(",");
        start.x = Integer.parseInt(parts[0]);
        start.y = Integer.parseInt(parts[1]);
        end  .x = Integer.parseInt(parts[2]);
        end  .y = Integer.parseInt(parts[3]);
    }
	
}
