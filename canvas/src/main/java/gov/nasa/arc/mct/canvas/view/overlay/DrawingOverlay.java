package gov.nasa.arc.mct.canvas.view.overlay;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DrawingOverlay extends AbstractCanvasOverlay {

	private Collection<DrawingElement> elements = new ArrayList<DrawingElement>();

	
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


        @Override
        public String stringify() {
            String serial = "";
            for (DrawingElement e : elements) {
                serial += e.stringify() + ";";
            }
            return serial;
        }


        @Override
        public void destringify(String str) {
            String[] parts = str.split(";");
            for (String part : parts) {
                if (!part.isEmpty()) {
                    Line l = new Line(0,0,0,0);
                    l.destringify(part);
                    elements.add(l);
                }
            }
        }

	
}
