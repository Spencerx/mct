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

import java.awt.Graphics;

import javax.xml.bind.annotation.XmlTransient;

/**
 * A drawing element is a visible object in a DrawingOverlay (for instance, a Line).
 * This serves as a general-purpose superclass to allow multiple different elements of a
 * drawing package to be treated uniformly.
 * @author vwoeltje
 */
@XmlTransient
public abstract class DrawingElement {
    /**
     * Request a handle for interacting with this element, at a specific location.
     * The return value will be null if there is no appropriate handle available at this 
     * location.
     * 
     * Typically, this method will be called when the user clicks at a specific point on 
     * the screen to initiate interaction; this interaction will then be mediated by the 
     * returned handle.
     *  
     * @param x the x coordinate of the desired handle, in pixels
     * @param y the y coordinate of the desired handle, in pixels
     * @return a handle for interacting with this element (or null, if none at this location)
     */
	public abstract ElementHandle getHandle(int x, int y);
	
    /**
     * Draw this element.
     * @param g the graphics context in which to draw
     */
	public abstract void draw(Graphics g);
	
	/**
	 * An ElementHandle mediates user interactions with a drawing element; it is responsible 
	 * for updating the elements state as necessary (during mouse drags, for instance)
	 * @author vwoeltje
	 */
	public static interface ElementHandle {
	    /**
	     * Move this handle to a specific location.
	     * @param x the x coordinate, in pixels
	     * @param y the y coordinate, in pixels
	     */
	    public void moveTo(int x, int y);
	}
}
