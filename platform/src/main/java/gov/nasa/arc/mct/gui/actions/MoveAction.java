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
import gov.nasa.arc.mct.gui.ContextAwareAction;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;

import java.awt.event.ActionEvent;

import javax.swing.Action;

public class MoveAction extends ContextAwareAction {
    private static final long serialVersionUID = -3683849461079613041L;

    private ContextAwareAction[] actions;
    
    public MoveAction(AbstractComponent destination) {
        super("Move");
        actions = new ContextAwareAction[]{
                new LinkAction(destination),
                new TransactionalRemoveAction()                
        };
    }

    @Override
    public boolean canHandle(ActionContext context) {        
        boolean canHandle = true;
        for (ContextAwareAction action : actions) {
            canHandle &= action.canHandle(context);
        }
        return canHandle;
    }

    @Override
    public boolean isEnabled() {
        boolean canHandle = true;
        for (ContextAwareAction action : actions) {
            canHandle &= action.isEnabled();
        }
        return canHandle;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (Action action : actions) {
            action.actionPerformed(e);
        }
    }
    
    private static class TransactionalRemoveAction extends RemoveManifestationAction {
        private static final long serialVersionUID = -542809014751624322L;

        @Override
        public void actionPerformed(ActionEvent e) {
            PlatformAccess.getPlatform().getPersistenceProvider().startRelatedOperations();
            try {
                super.actionPerformed(e);
            } finally {
                PlatformAccess.getPlatform().getPersistenceProvider().completeRelatedOperations(true);
            }
        }
        
    }
}
