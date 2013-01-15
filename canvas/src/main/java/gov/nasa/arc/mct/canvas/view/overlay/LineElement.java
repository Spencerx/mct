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

/**
 * A line is a basic drawing element. It appears as a simple line segment between two points. 
 * Handles for user interaction exist at either end of the line (to change end point), and 
 * at points along the line (to support moving the whole line)
 * 
 * @author vwoeltje
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class LineElement extends DrawingElement {
    /**
     * The distance, in pixels, from the line for which handles should be active. 
     * This is used to give some "wiggle room" around a line (so a user does not have 
     * to click on a pixel-thin line exactly in order to interact with it)
     */
	private static final int HANDLE_PROXIMITY = 8;

	private static final int START_INDEX      = 0;
	private static final int END_INDEX        = 1;
	
    /**
     * For demonstration purposes, this element has a default color of red.
     */
    @XmlTransient
    private Color color = Color.RED;

    /**
     * Start and end points of this line. 
     */
    @XmlJavaTypeAdapter(PointAdapter.class)
	private Point start, end;
	
    /**
     * Create a new line, with uninitialized end points (from 0,0 to 0,0). 
     * Used primarily for JAXB support.
     */
	public LineElement() {
	    this(0,0,0,0);
	}
	
	/**
	 * Create a new line, between two specific points
	 * @param x1 the x coordinate of the start point, in pixels
	 * @param y1 the y coordinate of the start point, in pixels
	 * @param x2 the x coordinate of the end point, in pixels
	 * @param y2 the y coordinate of the end point, in pixels
	 */
	public LineElement(int x1, int y1, int x2, int y2) {
		start = new Point();
		end   = new Point();
		setPoint(0, x1, y1);
		setPoint(1, x2, y2);
	}
	
	private void setPoint(int index, int x, int y) {
	    if (index > END_INDEX) {
	        throw new IllegalArgumentException("Cannot update point " + index + " for this element; no such point.");
	    }
		Point target = index == START_INDEX ? start : end;
		target.x = x;
		target.y = y;
	}
	
	/**
	 * Set the color of this line.
	 * @param c the color to be used to draw this line
	 */
	public void setColor(Color c) {
		color = c;
	}
	
	/**
	 * Get a handle for manipulating the end point of this line.
	 * @return a handle for manipulating the end point of this line
	 */
	public ElementHandle getEndHandle() {
		return new PointHandle(END_INDEX);
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
	    /*
	     * First, do a bounding box test to rule out any clicks clearly outside of this segment.
	     */
		if (x < Math.min(start.x, end.x) - HANDLE_PROXIMITY || 
		    x > Math.max(start.x, end.x) + HANDLE_PROXIMITY || 
			y < Math.min(start.y, end.y) - HANDLE_PROXIMITY || 
			y > Math.max(start.y, end.y) + HANDLE_PROXIMITY) {			
			return null;			
		}

		/*
		 * Next, check for proximity (as Euclidean distance) from either end point, and return 
		 * a handle for manipulating either the start or end point as appropriate.
		 */
		double startDist = Math.sqrt(Math.pow((x-start.x), 2) + Math.pow((y-start.y),2)); 
		if ( startDist < HANDLE_PROXIMITY ) {
			 return new PointHandle(0);
		}
			
		double endDist = Math.sqrt(Math.pow((x-end.x), 2) + Math.pow((y-end.y),2)); 		
		if ( endDist < HANDLE_PROXIMITY ) {
			 return new PointHandle(1);
		}

		/*
		 * Compute distance of the requested point from this line segment, by 
		 * first expressing the line in the form  (u,v) dot (x,y) + b = 0. 
		 * Note that, beyond checking for proximity to the line, there is no 
		 * need to check to see if this lies within the line segment; the 
		 * previous bounding box test ensures this.
		 */
		int dx = end.x - start.x;
		int dy = end.y - start.y;
		double length = Math.sqrt(dx * dx + dy * dy);
		double u = -(dy / length); // Perpendicular unit vector
		double v =   dx / length ; 
	    double b =   u * start.x + v * start.y; // Distance
	    double c =   u * x      + v * y ;       // Distance of observed point
		
		/* 
		 * c - b is the proximity, in pixels, of the chosen point to the line segment. 
		 * If it is close enough, then a handle for moving the entire line is returned. 
		 */
		return (c - b > -HANDLE_PROXIMITY && c - b < HANDLE_PROXIMITY) ? new LineHandle(x,y) : null;
	}

	private class PointHandle implements ElementHandle {
		private int index;
		/**
		 * Create a new handle for manipulating one of the points on this line segment
		 * @param i the point to manipulate; START_INDEX or END_INDEX
		 */
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
		
		/*
		 *  The start and end points of this line, at the time the handle was created. 
		 */		
		private Point s, e;
		
		/**
		 * 
		 * @param ix the initial x position of this interaction
		 * @param iy the initial y position of this interaction
		 */
		public LineHandle(int ix, int iy) {
			x = ix;
			y = iy;
			s = new Point(start.x, start.y);
			e = new Point(end.x,   end.y);
		}

		@Override
		public void moveTo(int x, int y) {
		    /*
		     * Adjust positions of end points, as determined relative 
		     * to the initial position.
		     */
			start.x = s.x + (x - this.x);
			start.y = s.y + (y - this.y);
			end.x   = e.x + (x - this.x);
			end.y   = e.y + (y - this.y);
		}		
	}

	
	/**
	 * Assists in serializing java.awt.Point; JAXB will hit an infinite loop 
	 * interpreting getLocation if used on java.awt.Point directly.
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
