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

import gov.nasa.arc.mct.components.ModelStatePersistence;

import java.awt.Graphics;
import java.awt.event.MouseAdapter;

/**
 * A CanvasOverlay provides an extra layer for drawing and interaction atop an existing canvas.
 * An Augmentation may utilize one of more CanvasOverlay objects to add or swap out different 
 * layers in addition to the normal set of panels.
 * 
 * @author vwoeltje
 */
public interface CanvasOverlay {
    /**
     * Add a listener to detect changes made to this overlay (for instance, when the user 
     * has completed a drawing action.)
     * @param listener
     */
    public void   addOverlayListener(OverlayListener listener);

    /**
     * Get an adapter to handle user interactions with this layer. The typical pattern 
     * will be for the JComponent employing this overlay (Augmentation, for instance) 
     * to swap this listener in/out with its normal mouse listeners, to support different 
     * layers of drawing.
     * @return a mouse adapter to support user interactions with this layer
     */
    public MouseAdapter getControlAdapter();
    
    /**
     * Get the name for this overlay, as would be appropriate to show the user.
     * @return the name of this overlay
     */
    public String getName();
    
    /**
     * Draw this overlay.
     * @param g the graphics context in which to draw
     */
    public void draw(Graphics g);
    
    /**
     * Get an object which can be used to convert the current state of this overlay 
     * to or from a String, for purposes of persistence.
     * 
     * (Note that the ModelStatePersistence interface is used here, as it provides 
     * an suitable API for the task; ModelStatePersistence is more typically found 
     * as a component capability.)  
     * 
     * @return an object which can convert this overlay to/from a string for persistence
     */
    public ModelStatePersistence getStatePersistence();
    
    /**
     * A listener to detect changes made to an overlay. As an example, an Augmentation 
     * may wish to detect changes to a drawing layer which has been implemented as a 
     * CanvasOverlay, in order to update view properties; this may be accomplished 
     * by attaching an appropriate OverlayListener.
     * 
     * @author vwoeltje
     */
    public static interface OverlayListener {
        /**
         * Called when a change has been initiated or is occurring in an overlay; 
         * for instance, if the user begins dragging the end point of a drawn object.
         * 
         * Typically, a user of an overlay (such as Augmentation) will want to repaint 
         * when this occurs. 
         */
        public void overlayUpdating();
        
        /**
         * Called when a change has been completed in an overlay; for instnace, when a 
         * user finishes dragging the end point of a drawn object.
         *          
         * Typically, a user of an overlay (such as Augmentation) will want to update 
         * view properties when this occurs.
         */
        public void overlayUpdated();
    }
}
