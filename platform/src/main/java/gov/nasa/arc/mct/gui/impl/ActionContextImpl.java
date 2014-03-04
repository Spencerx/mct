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
package gov.nasa.arc.mct.gui.impl;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.gui.ActionContext;
import gov.nasa.arc.mct.gui.CompositeViewManifestationProvider;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.gui.ViewProvider;
import gov.nasa.arc.mct.gui.housing.MCTHousing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 * This class defines a context for executing actions.
 * 
 * @author nija.shi@nasa.gov
 */
public class ActionContextImpl implements ActionContext {
    public static class PropertyKeys {
        /**
         * Indicates the destination of an action (e.g. the drop location of 
         * a drop operation).
         * Property values for this key should be of type AbstractComponent.
         */
        public static final String TARGET_COMPONENT = "TARGET_COMPONENT"; // NOI18N
        
        /**
         * Indicates the index for drop / reordering actions.
         * Property values for this key should be of type Integer.
         */
        public static final String TARGET_INDEX = "TARGET_INDEX"; // NOI18N
    }
    
    
    private Set<JComponent> targetViewComponents = new LinkedHashSet<JComponent>();
    private MCTHousing targetHousing;
    private Collection<View> selectedManifestations = new LinkedHashSet<View>();
    private AbstractComponent inspectorComponent;
    private Map<String, Object> properties = new HashMap<String, Object>();

    /**
     * Gets the target view manifestation, a Swing component
     * that will be affected by the action. If there is more
     * than one target view manifestation, return the first.
     * 
     * @return the target view manifestation
     */
    public JComponent getTargetViewComponent() {
        if (targetViewComponents.isEmpty()) { return null; }
        return targetViewComponents.iterator().next();
    }
    
    /**
     * Gets the set of all target view manifestations.
     * 
     * @return the target view manifestations
     */
    public Set<JComponent> getAllTargetViewComponent() {
        return targetViewComponents;
    }

    /**
     * Adds another target view manifestation for an action.
     * 
     * @param targetViewComponent the new target view manifestation
     */
    public void addTargetViewComponent(JComponent targetViewComponent) {
        this.targetViewComponents.add(targetViewComponent);

        if (targetViewComponent instanceof View)
            selectedManifestations.add((View) targetViewComponent);
    }
    
    /**
     * Gets the housing containing the target component for an action.
     * 
     * @return the target housing
     */
    public MCTHousing getTargetHousing() {
        return targetHousing;
    }

    /**
     * Sets the target housing for an action.
     * 
     * @param targetHousing the new target housing
     */
    public void setTargetHousing(MCTHousing targetHousing) {
        this.targetHousing = targetHousing;
    }
    
    @Override
    public Collection<View> getSelectedManifestations() {
        return Collections.unmodifiableCollection(selectedManifestations);
    }
    
    /** An empty action context used as a sentinel value. */
    public static final ActionContextImpl NULL_CONTEXT = new ActionContextImpl();

    @Override
    public View getWindowManifestation() {
        return targetHousing.getHousedViewManifestation();
    }
    
    @Override
    public Collection<View> getRootManifestations() {
        View housingManifestation = getWindowManifestation();
        if (housingManifestation == null) { return Collections.emptyList(); }
        
        Collection<View> viewManifestations = new ArrayList<View>();
        
        CompositeViewManifestationProvider compositeProvider = (CompositeViewManifestationProvider)SwingUtilities.getAncestorOfClass(CompositeViewManifestationProvider.class, housingManifestation);
        Collection<ViewProvider> manifestationsProviders = compositeProvider.getHousedManifestationProviders();
        for (ViewProvider manifestProvider: manifestationsProviders) {
            viewManifestations.add(manifestProvider.getHousedViewManifestation());
        }
        
        return viewManifestations;
    }

    /**
     * Gets the inspector component this is set.
     * @return instance of the inspector or null if the inspector is not active
     */
    public AbstractComponent getInspectorComponent() {
        return inspectorComponent;
    }

    /**
     * Sets the inspector component if the action is triggered from the inspector.
     * @param inspectorComponent to set as the inspector component
     */
    public void setInspectorComponent(AbstractComponent inspectorComponent) {
        this.inspectorComponent = inspectorComponent;
    }

    /**
     * Associate a custom property with the current context.
     * @param key a key indicating which property will be set
     * @param value the new value for that property 
     */
    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }
    
    /**
     * Set the target component for this context.
     * @param component the target component
     */
    public void setTargetComponent(AbstractComponent component) {
        setProperty(PropertyKeys.TARGET_COMPONENT, component);
    }
    
    @Override
    public <T> T getProperty(String key, Class<T> propertyClass) {
        Object value = properties.get(key);
        if (value != null && propertyClass.isAssignableFrom(value.getClass())) {
            return propertyClass.cast(value);
        }
        return null;
    }
}
