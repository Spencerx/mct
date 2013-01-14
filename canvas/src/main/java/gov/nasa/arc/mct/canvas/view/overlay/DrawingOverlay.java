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

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({Line.class})
public class DrawingOverlay extends AbstractCanvasOverlay {

    private Collection<DrawingElement> elements = new ArrayList<DrawingElement>();

    @XmlTransient
    private Collection<ElementHandle> handles = new ArrayList<ElementHandle>();

    @XmlTransient
    private final MouseAdapter controlAdapter = new MouseAdapter() {

        /* (non-Javadoc)
         * @see java.awt.event.MouseAdapter#mouseDragged(java.awt.event.MouseEvent)
         */
        @Override
        public void mouseDragged(MouseEvent evt) {
            for (ElementHandle handle : handles) {
                handle.moveTo(evt.getX(), evt.getY());
            }
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
                for (DrawingElement element : elements) {
                    ElementHandle h = element.getHandle(evt.getX(), evt.getY());
                    if (h != null) {
                        handles.add(h);
                    }
                }
                if (handles.isEmpty()) {
                    // Start new line
                    Line l = new Line(0,0,0,0);
                    l.setPoint(0, evt.getX(), evt.getY());
                    l.setPoint(1, evt.getX(), evt.getY());
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
            if (evt.getButton() == MouseEvent.BUTTON1) {
                if (!handles.isEmpty()) {
                    fireOverlayUpdated();
                }
                handles.clear();	
            } else if (evt.getButton() == MouseEvent.BUTTON3){
                List<DrawingElement> toRemove = new ArrayList<DrawingElement>();
                for (DrawingElement element : elements) {
                    ElementHandle h = element.getHandle(evt.getX(), evt.getY());
                    if (h != null) {
                        toRemove.add(element);
                    }
                }
                elements.removeAll(toRemove);
                if (!toRemove.isEmpty()) {
                    fireOverlayUpdated();
                }
            }
        }
    };


//    @Override
//    public String toPersistableObject() {
//        String serial = "";
//        for (DrawingElement e : elements) {
//            serial += e.toPersistableObject() + ";";
//        }
//        return serial;
//    }
//
//
//    @Override
//    public void fromPersistableObject(String str) {
//        String[] parts = str.split(";");
//        for (String part : parts) {
//            if (!part.isEmpty()) {
//                Line l = new Line(0,0,0,0);
//                l.fromPersistableObject(part);
//                elements.add(l);
//            }
//        }
//    }


    @Override
    public void draw(Graphics g) {
        for (Drawable element : elements) {
            element.draw(g);
        }
    }


    @Override
    public String getName() {
        return "Drawing";
    }
    
    @Override
    public MouseAdapter getControlAdapter() {
        // TODO Auto-generated method stub
        return controlAdapter;
    }


    @Override
    public ModelStatePersistence getStatePersistence() {
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
