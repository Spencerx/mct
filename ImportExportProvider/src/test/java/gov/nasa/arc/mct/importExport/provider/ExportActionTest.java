package gov.nasa.arc.mct.importExport.provider;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.gui.ActionContext;
import gov.nasa.arc.mct.gui.ContextAwareAction;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.importExport.access.ComponentRegistryAccess;
import gov.nasa.arc.mct.services.component.ComponentRegistry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class ExportActionTest {
	private ContextAwareAction exportAction;
	private ContextAwareAction exportThisAction;
	
	@Mock AbstractComponent ownedComponent;
	@Mock AbstractComponent otherComponent;
	@Mock AbstractComponent uncreatableComponent;
	
	@Mock View              ownedView;
	@Mock View              otherView;
	@Mock View              uncreatableView;
	
	@Mock ComponentRegistry componentRegistry;
	
	@BeforeTest
	public void setup() {
		MockitoAnnotations.initMocks(this);
		exportAction     = new ExportAction();
		exportThisAction = new ExportThisAction();
		
		Mockito.when(ownedView.getManifestedComponent()).thenReturn(ownedComponent);
		Mockito.when(otherView.getManifestedComponent()).thenReturn(otherComponent);
		Mockito.when(uncreatableView.getManifestedComponent()).thenReturn(uncreatableComponent);
		
		(new ComponentRegistryAccess()).setRegistry(componentRegistry);
	}

	@Test
	public void testNoSelections() {
		ActionContext context = new TestContext(ownedView);
		Mockito.when(componentRegistry.isCreatable(Mockito.<Class<?>>any())).thenReturn(true);
		expect(exportThisAction, true, true, context);
		expect(exportAction, false, true, context);
	}

	@Test
	public void testNoSelectionsUncreatable() {
		ActionContext context = new TestContext(uncreatableView);
		Mockito.when(componentRegistry.isCreatable(Mockito.<Class<?>>any())).thenReturn(false);
		expect(exportThisAction, true, false, context);
		expect(exportAction, false, false, context);
	}

	@Test
	public void testMultiselectionCreatable() {
		ActionContext context = new TestContext(ownedView, otherView, ownedView);
		Mockito.when(componentRegistry.isCreatable(Mockito.<Class<?>>any())).thenReturn(true);
		expect(exportAction, true, true, context);
	}

	@Test
	public void testMultiselectionUncreatable() {
		ActionContext context = new TestContext(ownedView, otherView, ownedView);
		Mockito.when(componentRegistry.isCreatable(Mockito.<Class<?>>any())).thenReturn(false);
		expect(exportAction, true, false, context);
	}
	
	@Test
	public void testMultiselectionMixed() {
		ActionContext context = new TestContext(ownedView, otherView, ownedView);
		Mockito.when(componentRegistry.isCreatable(Mockito.<Class<?>>any())).thenReturn(true).thenReturn(false).thenReturn(true);
		expect(exportAction, true, false, context);
	}


	
	private void expect(ContextAwareAction action, boolean handles, boolean enabled, ActionContext context) {
		Assert.assertEquals(action.canHandle(context), handles);
		if (handles) Assert.assertEquals(action.isEnabled(),        enabled);	
	}
	
	private class TestContext implements ActionContext {
		private View winMan;
		private List<View> selected = new ArrayList<View>();
		
		public TestContext (View winMan, View... other) {
			this.winMan = winMan;
			for (View v : other) selected.add(v);
		}

		@Override
		public Collection<View> getSelectedManifestations() {
			return selected;
		}

		@Override
		public View getWindowManifestation() {
			return winMan;
		}

		@Override
		public Collection<View> getRootManifestations() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public AbstractComponent getTargetComponent() {
			return null;
		}
		
	}
	
}
