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

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class to serve as a base implementation for CanvasOverlays.
 * 
 * @author vwoeltje
 */
public abstract class AbstractCanvasOverlay implements CanvasOverlay {
    private List<OverlayListener> listeners = new ArrayList<OverlayListener>();
    
    /* (non-Javadoc)
     * @see gov.nasa.arc.mct.canvas.view.overlay.CanvasOverlay#addOverlayListener(gov.nasa.arc.mct.canvas.view.overlay.OverlayListener)
     */
    @Override
    public void addOverlayListener(OverlayListener listener) {
        listeners.add(listener);
    }
    
    /**
     * Fire an overlayUpdating event; this will notify listeners that the overlay is in the process 
     * of being updated (for instance, to trigger repainting)
     * @see gov.nasa.arc.mct.canvas.view.overlay.CanvasOverlay.OverlayListener#overlayUpdating()
     */
    public void fireOverlayUpdating() {
        for (OverlayListener listener : listeners) listener.overlayUpdating();
    }

    /**
     * For an overlayUpdated event; this will notify listeners that the overlay is finished 
     * being updated (for instance, to trigger changes to view properties)
     * @see gov.nasa.arc.mct.canvas.view.overlay.CanvasOverlay.OverlayListener#overlayUpdated()
     */
    public void fireOverlayUpdated() {
        for (OverlayListener listener : listeners) listener.overlayUpdated();
    }
}
