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
package gov.nasa.arc.mct.graphics.view;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.graphics.component.GraphicalComponent;
import gov.nasa.arc.mct.graphics.component.GraphicalModel;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.services.component.ViewInfo;

import java.awt.BorderLayout;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;


public class StaticGraphicalView extends View {
    private static ResourceBundle bundle = ResourceBundle.getBundle("GraphicsResourceBundle");
    
	private static final long serialVersionUID = -6823838565608622054L;
	
	public static final String VIEW_ROLE_NAME = bundle.getString("View_Name");
	
	public StaticGraphicalView(AbstractComponent component, ViewInfo vi) {
		super(component, vi);

		setBackground(UIManager.getColor("background"));
		setForeground(UIManager.getColor("foreground"));
		
		if (component instanceof GraphicalComponent) { 
			GraphicalModel model =  ((GraphicalComponent)component).getModelRole();
			add(new ResizableImagePanel(model.getGraphicURI()));
		} else {
			prepareFailureLabel(bundle.getString("Component_Error"));
		}
		
		setOpaque(false);
	}	
	
	private void prepareFailureLabel(final String failureText) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				removeAll();
				setLayout(new BorderLayout());
				JLabel failureLabel = new JLabel(failureText);
				failureLabel.setBackground(getBackground());
				failureLabel.setForeground(getForeground());
				add(failureLabel, BorderLayout.NORTH);
				repaint();
			}
		});
	}
}

