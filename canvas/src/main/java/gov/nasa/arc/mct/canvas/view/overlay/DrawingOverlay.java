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

import gov.nasa.arc.mct.canvas.view.overlay.DrawingElement.ElementHandle;
import gov.nasa.arc.mct.components.JAXBModelStatePersistence;
import gov.nasa.arc.mct.components.ModelStatePersistence;

import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

/**
 * A DrawingOverlay permits basic drawing operations atop a canvas (currently, 
 * just line drawing, although futures updates to this class may be used to 
 * support other shapes or more complicated drawing)
 * 
 * @author vwoeltje
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({LineElement.class})
public class DrawingOverlay extends AbstractCanvasOverlay {
    /**
     * All drawing elements which exist upon this overlay.
     */
    private Collection<DrawingElement> elements = new ArrayList<DrawingElement>();

    /**
     * Any active handles being used within this overlay.
     */
    @XmlTransient
    private Collection<ElementHandle> handles = new ArrayList<ElementHandle>();

    @XmlTransient
    private final MouseAdapter controlAdapter = new MouseAdapter() {

        /* (non-Javadoc)
         * @see java.awt.event.MouseAdapter#mouseDragged(java.awt.event.MouseEvent)
         */
        @Override
        public void mouseDragged(MouseEvent evt) {
            /*
             *  When the mouse is dragged, notify any active handles so that they 
             *  can modify drawing elements as appropriate.
             */
            for (ElementHandle handle : handles) {
                handle.moveTo(evt.getX(), evt.getY());
            }
            
            /* 
             * Notify listeners that the overlay is being updated, unless there are no handles
             * in use which may have changed anything.
             */            
            if (!handles.isEmpty()) {
                fireOverlayUpdating();
            }
        }

        /* (non-Javadoc)
         * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
         */
        @Override
        public void mousePressed(MouseEvent evt) {            
            if (evt.getButton() == MouseEvent.BUTTON1) {
                /*
                 * Initiate modification of existing elements...
                 */
                for (DrawingElement element : elements) {
                    ElementHandle h = element.getHandle(evt.getX(), evt.getY());
                    if (h != null) {
                        handles.add(h);
                    }
                }
                /*
                 * ...or, if there are no element handles at this location, 
                 * start drawing a new one (currently, just draw a line.)
                 */
                if (handles.isEmpty()) {
                    // Start new line
                    LineElement l = new LineElement(evt.getX(),evt.getY(),evt.getX(),evt.getY());
                    elements.add(l);
                    handles.add(l.getEndHandle());
                }
            }
        }

        /* (non-Javadoc)
         * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
         */
        @Override
        public void mouseReleased(MouseEvent evt) {
            if (evt.getButton() == MouseEvent.BUTTON1) { // Left mouse button...
                /*
                 * If there are active handles, then a manipulation of some drawing 
                 * element has just completed - notify listeners and clear the 
                 * list of active handles.
                 */
                if (!handles.isEmpty()) {
                    fireOverlayUpdated();
                    handles.clear();
                }	
            } else if (evt.getButton() == MouseEvent.BUTTON3){ // Right mouse button...
                /*
                 * Delete elements. Elements are chosen for deletion if they offer 
                 * any handles at this location.
                 */
                List<DrawingElement> toRemove = new ArrayList<DrawingElement>();
                for (DrawingElement element : elements) {
                    ElementHandle h = element.getHandle(evt.getX(), evt.getY());
                    if (h != null) {
                        toRemove.add(element);
                    }
                }
                elements.removeAll(toRemove);
                /*
                 * If drawing elements were removed, notify listeners.
                 */
                if (!toRemove.isEmpty()) {
                    fireOverlayUpdated();
                }
            }
        }
    };

    @Override
    public void draw(Graphics g) {
        for (DrawingElement element : elements) {
            element.draw(g);
        }
    }


    @Override
    public String getName() {
        return "Drawing";
    }
    
    @Override
    public MouseAdapter getControlAdapter() {
        return controlAdapter;
    }

    @Override
    public ModelStatePersistence getStatePersistence() {
        /*
         * Support conversion to/from Strings using JAXB. 
         * 
         * Note that DrawingElements supported should have appropriate JAXB annotations, 
         * and should be listed under XmlSeeAlso (see DrawingOverlay's declaration)
         */
        return new JAXBModelStatePersistence<DrawingOverlay>() {
            @Override
            protected DrawingOverlay getStateToPersist() {
                return DrawingOverlay.this;
            }

            @Override
            protected void setPersistentState(DrawingOverlay modelState) {
                elements = modelState.elements;
            }

            @Override
            protected Class<DrawingOverlay> getJAXBClass() {
                return DrawingOverlay.class;
            }
            
        };
    }


}
