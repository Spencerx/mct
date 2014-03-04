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
package gov.nasa.arc.mct.gui.actions;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.context.GlobalContext;
import gov.nasa.arc.mct.gui.ActionContext;
import gov.nasa.arc.mct.gui.ContextAwareAction;
import gov.nasa.arc.mct.gui.OptionBox;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.gui.dialogs.DuplicateObjectDialog;
import gov.nasa.arc.mct.gui.impl.ActionContextImpl;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;
import gov.nasa.arc.mct.policy.PolicyContext;
import gov.nasa.arc.mct.policy.PolicyInfo;
import gov.nasa.arc.mct.policymgr.PolicyManagerImpl;
import gov.nasa.arc.mct.registry.ExternalComponentRegistryImpl;
import gov.nasa.arc.mct.registry.ExternalComponentRegistryImpl.ExtendedComponentProvider;
import gov.nasa.arc.mct.services.internal.component.ComponentInitializer;
import gov.nasa.arc.mct.util.StringUtil;

import java.awt.event.ActionEvent;
import java.util.Collections;

import org.testng.annotations.AfterMethod;

/**
 * This action duplicates a component.
 * @author jjpuin
 */
@SuppressWarnings("serial")
public class DuplicateAction extends ContextAwareAction {

    private static String TEXT = "Duplicate Object...";
    private static String SHORT_TEXT = "Copy";
        
    private ActionContextImpl actionContext;
    private AbstractComponent destinationComponent = null;
    
    public DuplicateAction() {
        this(false);
    }    

    /**
     * Duplicate object has a shorter name when appearing in a 
     * dialog box.
     * @param useShortText true if short name should be used
     */
    public DuplicateAction(boolean useShortText) {
        super(useShortText ? SHORT_TEXT : TEXT);
    }    
    
    @AfterMethod
    protected void teardown() {
        PolicyManagerImpl.getInstance().refreshExtendedPolicies(Collections.<ExtendedComponentProvider>emptyList());
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        // Only use startRelatedOperations if this is invoked from move/copy dialog
        boolean groupOperations = e == null;
        
        if (groupOperations) {
            PlatformAccess.getPlatform().getPersistenceProvider().startRelatedOperations();
        }
        try {
            for (View view : actionContext.getSelectedManifestations()) {                    
                AbstractComponent parentComponent = destinationComponent;
                AbstractComponent selectedComponent = view.getManifestedComponent();
            
                if (selectedComponent == null) {
                    OptionBox.showMessageDialog(null, "Unable to create duplicate of this object!", "Error creating duplicate.", OptionBox.ERROR_MESSAGE);
                    return;
                }
                
                DuplicateObjectDialog dialog = new DuplicateObjectDialog(actionContext.getTargetHousing().getHostedFrame(),
                                                                selectedComponent.getDisplayName());
                String name = dialog.getConfirmedTelemetryGroupName();
    
                if (!StringUtil.isEmpty(name)) {
                    AbstractComponent duplicate = selectedComponent.clone();
                    ComponentInitializer ci = duplicate.getCapability(ComponentInitializer.class);
                    ci.setCreator(GlobalContext.getGlobalContext().getUser().getUserId());
                    ci.setOwner(GlobalContext.getGlobalContext().getUser().getUserId());
                    duplicate.setDisplayName(name);
                    duplicate.save();
                    parentComponent.addDelegateComponent(duplicate);
                    parentComponent.save();
                }
            }
        } finally {
            if (groupOperations) {
                PlatformAccess.getPlatform().getPersistenceProvider().completeRelatedOperations(true);
            }
        }

    }

    protected boolean isComponentCreatable(AbstractComponent ac) {
        ExternalComponentRegistryImpl extCompRegistry = ExternalComponentRegistryImpl.getInstance();
        return ac != null && extCompRegistry != null && extCompRegistry.isCreatable(ac.getClass());
    }
    
    @Override
    public boolean canHandle(ActionContext context) {
        actionContext = (ActionContextImpl) context;
        
        // This action works only for selected items in the directory area.
        if (actionContext.getSelectedManifestations() == null || 
                actionContext.getSelectedManifestations().isEmpty()) {
            return false;
        }
        
        for (View view : actionContext.getSelectedManifestations()) {
            if (!isComponentCreatable(view.getManifestedComponent())) {
                return false;
            }
        }
        
        destinationComponent = actionContext.getTargetComponent();
        if (destinationComponent == null) {
            String destinationId = null;
            for (View view : actionContext.getSelectedManifestations()) {
                AbstractComponent dest = view.getParentManifestation();
                if (dest != null) {
                    String id = dest.getComponentId();
                    if (destinationId != null && destinationId.equals(id)) {
                        return false; // Ambiguous destination                       
                    }
                    destinationComponent = dest;
                    destinationId = id;
                }
            }
        }

        return destinationComponent != null && isParentComponentModifiable();
    }

    private boolean isParentComponentModifiable() {
        // Check if parent component can be modified.
        PolicyContext policyContext = new PolicyContext();
        policyContext.setProperty(PolicyContext.PropertyName.TARGET_COMPONENT.getName(), destinationComponent);
        policyContext.setProperty(PolicyContext.PropertyName.ACTION.getName(), 'w');
        String compositionKey = PolicyInfo.CategoryType.COMPOSITION_POLICY_CATEGORY.getKey();
        return PolicyManagerImpl.getInstance().execute(compositionKey, policyContext).getStatus();
    }
    
    @Override
    public boolean isEnabled() {
        for (View v : actionContext.getSelectedManifestations()) {
            AbstractComponent c = v.getManifestedComponent();
            if (c == null || c.getExternalKey() != null) {
                return false;
            }
        }        
        return true;
    }
    
}
