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

import gov.nasa.arc.mct.canvas.view.Augmentation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Collections;

import javax.swing.JComboBox;

public class OverlayChooser extends JComboBox {
    private static final long serialVersionUID = 4749301723217975689L;
    
    public OverlayChooser(final Augmentation augmentation) {
        this(augmentation, augmentation != null ? augmentation.getOverlays() : Collections.<CanvasOverlay>emptyList() );
    }
    
    public OverlayChooser(final Augmentation augmentation, Collection<CanvasOverlay> overlays) {
        super();
        addItem(new Item(null));
        for (CanvasOverlay overlay : overlays) {
            addItem(new Item(overlay));
        }
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Item item = (Item) getSelectedItem();
                augmentation.setActiveLayer(item.o);
            }        
        });
    }
    
    private static class Item {
        private CanvasOverlay o;
        private Item (CanvasOverlay o) {
            this.o = o;
        }
        public String toString() {
            return (o == null) ? "Panels" : o.getName();
        }
    }
}
