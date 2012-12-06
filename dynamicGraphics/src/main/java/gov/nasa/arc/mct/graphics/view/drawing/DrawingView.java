package gov.nasa.arc.mct.graphics.view.drawing;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.services.component.ViewInfo;

import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;

public class DrawingView extends View {
	public static final String VIEW_ROLE_NAME = "Drawing";

	private Collection<DrawingElement> elements = new ArrayList<DrawingElement>();
	
//	public static void main (String[] args) {
//		JFrame frame = new JFrame();
//		frame.setSize(500, 500);
//		frame.getContentPane().add(new DrawingView());
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.setVisible(true);
//	}
	
	public DrawingView(AbstractComponent component, ViewInfo vi) {
		super(component, vi);
		setOpaque(false);
		this.addMouseListener(mouseAdapter);
		this.addMouseMotionListener(mouseAdapter);
		elements.add(new Line(0, 0, 0.5, 0.25));
	}
	
	public void paintComponent(Graphics g) {		
		for (Drawable element : elements) {
			element.draw(g, getBounds());
		}
	}
	
	
	private final MouseAdapter mouseAdapter = new MouseAdapter() {
		private Collection<ElementHandle> handles = new ArrayList<ElementHandle>();
		
		/* (non-Javadoc)
		 * @see java.awt.event.MouseAdapter#mouseDragged(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseDragged(MouseEvent evt) {
//			if (activeElement != null) {
//				activeElement.setPoint(1, evt.getX(), evt.getY(), getBounds());
//				repaint();
//			}
			for (ElementHandle handle : handles) {
				handle.moveTo(evt.getX(), evt.getY(), getBounds());
			}
			if (!handles.isEmpty()) repaint();
		}

		/* (non-Javadoc)
		 * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
		 */
		@Override
		public void mousePressed(MouseEvent evt) {
			for (DrawingElement element : elements) {
				ElementHandle h = element.getHandle(evt.getX(), evt.getY(), getBounds());
				if (h != null) handles.add(h);
			}
			if (handles.isEmpty()) {
				// Start new line
				Line l = new Line(0,0,0,0);
				l.setPoint(0, evt.getX(), evt.getY(), getBounds());
				l.setPoint(1, evt.getX(), evt.getY(), getBounds());
				elements.add(l);
				handles.add(l.getEndHandle());
			}
		}

		/* (non-Javadoc)
		 * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseReleased(MouseEvent arg0) {
			handles.clear();
		}
	};
	
}
