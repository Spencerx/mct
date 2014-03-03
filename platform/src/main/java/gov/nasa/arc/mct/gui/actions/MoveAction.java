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
import gov.nasa.arc.mct.gui.ActionContext;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;
import gov.nasa.arc.mct.policy.ExecutionResult;
import gov.nasa.arc.mct.policy.PolicyContext;
import gov.nasa.arc.mct.policy.PolicyInfo;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;

public class MoveAction extends RemoveManifestationAction {
    private static final long serialVersionUID = -3683849461079613041L;

    private AbstractComponent targetComponent = null;
    private Collection<AbstractComponent> sourceComponents = new ArrayList<AbstractComponent>();
    
    public MoveAction(AbstractComponent destination) {
        super("Move");
        this.targetComponent = destination;
    }

    @Override
    public boolean canHandle(ActionContext context) {        
        sourceComponents.clear();
        for (View view : context.getSelectedManifestations()) {
            sourceComponents.add(view.getManifestedComponent());
        }
        return super.canHandle(context) && compositionIsAllowed(); 
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        targetComponent.addDelegateComponents(sourceComponents);
        targetComponent.save();
        super.actionPerformed(e);
    }
    
    private boolean compositionIsAllowed() {
        // Establish policy context.
        PolicyContext context = new PolicyContext();
        context.setProperty(PolicyContext.PropertyName.TARGET_COMPONENT.getName(), targetComponent);
        context.setProperty(PolicyContext.PropertyName.SOURCE_COMPONENTS.getName(), sourceComponents);
        context.setProperty(PolicyContext.PropertyName.ACTION.getName(), Character.valueOf('w'));
        String compositionKey = PolicyInfo.CategoryType.COMPOSITION_POLICY_CATEGORY.getKey();
        String acceptDelegateKey = PolicyInfo.CategoryType.ACCEPT_DELEGATE_MODEL_CATEGORY.getKey();
        // Execute policy
        ExecutionResult result = PlatformAccess.getPlatform().getPolicyManager().execute(compositionKey, context);
        if (result.getStatus()) {
            result = PlatformAccess.getPlatform().getPolicyManager().execute(acceptDelegateKey, context);
        }
        return result.getStatus();
    }
    
}
