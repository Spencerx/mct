package gov.nasa.arc.mct.graphics.view;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ResizableImagePanel extends JPanel {
    private static ResourceBundle bundle = ResourceBundle.getBundle("GraphicsResourceBundle");
    
	private static final long serialVersionUID = -6823838565608622054L;
	private SVGRasterizer rasterizer = null;
	private ImagePanel    imagePanel = new ImagePanel();	
	
	public ResizableImagePanel(String URI) {
		setLayout(new GridLayout(1,1));
		setOpaque(false);
		imagePanel.setOpaque(false);
		add(imagePanel);
		
		prepareGraphicalView(URI);
	}	
	
	private void prepareGraphicalView(final String graphicURI) {

		if (graphicURI.endsWith(".svg")) {
			rasterizer = new SVGRasterizer(graphicURI);
			rasterizer.setCallback(new Runnable() {
				public void run() {						
					if (rasterizer.hasFailed()) {
						prepareRasterImage(graphicURI); // Maybe the extension is wrong
					} else {							
						imagePanel.setImage(rasterizer.getLatestImage());
					}
					repaint();					
				}
			});
			
			addComponentListener( new ComponentListener() {
				@Override
				public void componentHidden(ComponentEvent arg0) {
					/* Get a smaller image - less memory use */
					rasterizer.requestRender(50, 50);					
				}

				@Override
				public void componentMoved(ComponentEvent arg0) {
				}

				@Override
				public void componentResized(ComponentEvent arg0) {
					rasterizer.requestRender(getWidth(), getHeight());
				}

				@Override
				public void componentShown(ComponentEvent arg0) {
					rasterizer.requestRender(getWidth(), getHeight());					
				}
				
			});
			
		} else {		
			prepareRasterImage(graphicURI);
		}
					
	}
	
	private void prepareRasterImage(String graphicURI) {
		try {
			BufferedImage img = ImageIO.read(new URL(graphicURI));
			if (img != null) {
				imagePanel.setImage(img);
			} else {
				prepareFailureLabel("Type_Error", graphicURI);
			}
		} catch (IOException ioe) {
			prepareFailureLabel(
					(ioe.getCause() instanceof FileNotFoundException) ? 
							"FileNotFound_Error" : "Location_Error", 
					graphicURI);
		}
	}
		
	private void prepareFailureLabel(String bundleKey, String graphicURI) {
		String failureString = bundle.getString(bundleKey);
		prepareFailureLabel( String.format(failureString, graphicURI) );
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
		
	
	private class ImagePanel extends JComponent {
		private static final long serialVersionUID = -2223786881389122841L;
		
		private BufferedImage image = null;
		
		public void setImage(BufferedImage image) {
			this.image = image;
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (image != null) {
				int x,y,w,h;
				boolean pillarbox = getWidth() * image.getHeight() > getHeight() * image.getWidth();
			    boolean letterbox = getWidth() * image.getHeight() < getHeight() * image.getWidth();
				w = !pillarbox ? getWidth()  : (getHeight() * image.getWidth() / image.getHeight());
				h = !letterbox ? getHeight() : (getWidth()  * image.getHeight() / image.getWidth());
				x = (getWidth()  - w) / 2;
				y = (getHeight() - h) / 2;
				g.drawImage(image, x, y, x+w, y+h, 
						0, 0, image.getWidth(), image.getHeight(), this);

			} 			
		}
		
	}

}
