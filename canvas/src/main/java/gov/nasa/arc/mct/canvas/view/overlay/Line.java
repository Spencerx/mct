/*******************************************************************************
 * Mission Control Technologies, Copyright (c) 2009-2012, United States Government
 * as represented by the Administrator of the National Aeronautics and Space 
 * Administration. All rights reserved.
 *
 * The MCT platform is licensed under the Apache License, Version 2.0 (the 
 * "License"); you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations under 
 * the License.
 *
 * MCT includes source code licensed under additional open source licenses. See 
 * the MCT Open Source Licenses file included with this distribution or the About 
 * MCT Licenses dialog available at runtime from the MCT Help menu for additional 
 * information. 
 *******************************************************************************/
package gov.nasa.arc.mct.canvas.view.overlay;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Line extends DrawingElement {
	
    @XmlTransient
    private Color color = Color.RED;

    @XmlJavaTypeAdapter(PointAdapter.class)
	private Point start, end;
	
	public Line() {
	    this(0,0,0,0);
	}
	
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
	
//    @Override
//    public String toPersistableObject() {        
//        return "" + start.x + "," + start.y + "," + end.x + "," + end.y;
//    }
//
//    @Override
//    public void fromPersistableObject(String str) {
//        String[] parts = str.split(",");
//        start.x = Integer.parseInt(parts[0]);
//        start.y = Integer.parseInt(parts[1]);
//        end  .x = Integer.parseInt(parts[2]);
//        end  .y = Integer.parseInt(parts[3]);
//    }
//
	
	/**
	 * Assists in serializing java.awt.Point; JAXB will hit an infinite loop 
	 * interpreting getLocation if used on Point directly.
	 */
	public static class PointAdapter extends XmlAdapter<String, Point> {
	    @Override
	    public String marshal(Point arg0) throws Exception {
	        return arg0.x + "," + arg0.y;
	    }

	    @Override
	    public Point unmarshal(String arg0) throws Exception {
	        String[] s = arg0.split(",");
	        return new Point(Integer.parseInt(s[0]), Integer.parseInt(s[1]));
	    }
	}
}
