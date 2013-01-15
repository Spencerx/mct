package gov.nasa.arc.mct.canvas.view.overlay;

import gov.nasa.arc.mct.canvas.view.overlay.CanvasOverlay.OverlayListener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DrawingOverlayTest {
    private static final int TEST_IMAGE_SIZE = 30;
    
    private DrawingOverlay  overlay;
   
    @Mock private OverlayListener mockListener;
    @Mock private MouseEvent mockMouseEvent;
    
    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
        overlay = new DrawingOverlay();
    }
    
    @Test
    public void testDrawingActions() {
        MouseAdapter adapter = overlay.getControlAdapter();
        BufferedImage original = drawOverlay(overlay);      

        // Do a drawing-like action
        adapter.mousePressed(makeMouseEvent(5, 5, true));
        adapter.mouseDragged(makeMouseEvent(10, 10, true));
        adapter.mouseReleased(makeMouseEvent(10, 10, true));
        
        // Should look different now
        Assert.assertFalse(imagesMatch(original, drawOverlay(overlay)));
        
        // Now, try to delete (right-click)
        adapter.mousePressed(makeMouseEvent(7, 7, false));
        adapter.mouseReleased(makeMouseEvent(7, 7, false));
        
        // Line should be gone, should look normal
        Assert.assertTrue(imagesMatch(original, drawOverlay(overlay)));        
    }
    
    @Test
    public void testListenersNotified() {
        MouseAdapter adapter = overlay.getControlAdapter();
        overlay.addOverlayListener(mockListener);
        
        // Do a drawing-like action
        adapter.mousePressed(makeMouseEvent(5, 5, true));
        adapter.mouseDragged(makeMouseEvent(10, 10, true));
        Mockito.verify(mockListener, Mockito.atLeastOnce()).overlayUpdating();
        Mockito.verify(mockListener, Mockito.never()).overlayUpdated();
        adapter.mouseReleased(makeMouseEvent(10, 10, true));
        Mockito.verify(mockListener, Mockito.atLeastOnce()).overlayUpdated();        
    }
    
    @Test
    public void testOverlayPersistence() {
        MouseAdapter adapter = overlay.getControlAdapter();

        // Do a drawing-like action
        adapter.mousePressed(makeMouseEvent(5, 5, true));
        adapter.mouseDragged(makeMouseEvent(10, 10, true));
        adapter.mouseReleased(makeMouseEvent(10, 10, true));
        
        DrawingOverlay other = new DrawingOverlay();
        
        // Overlays should look different, since only the original has received drawing actions
        Assert.assertFalse(imagesMatch(drawOverlay(other), drawOverlay(overlay)));
        
        // Try to copy the state from one to the other
        other.getStatePersistence().setModelState(overlay.getStatePersistence().getModelState());
        
        // Overlays should now look the same.
        Assert.assertTrue(imagesMatch(drawOverlay(other), drawOverlay(overlay)));
    }
    
    
    private MouseEvent makeMouseEvent(int x, int y, boolean isLeftButton) {
        Mockito.when(mockMouseEvent.getX()).thenReturn(x);
        Mockito.when(mockMouseEvent.getY()).thenReturn(y);
        Mockito.when(mockMouseEvent.getButton()).thenReturn(isLeftButton ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3);
        return mockMouseEvent;
    }

    private boolean imagesMatch(BufferedImage a, BufferedImage b) {
        if (a.getWidth() != b.getWidth() || a.getHeight() != b.getHeight()) {
            return false;
        }
        for (int y = 0; y < a.getHeight(); y++) {
            for (int x = 0; x < a.getWidth(); x++) {
                if (a.getRGB(x, y) != b.getRGB(x, y)) {
                    return false;
                }
            }
        }        
        return true;
    }
    
    private BufferedImage drawOverlay(DrawingOverlay d) {
        BufferedImage b = new BufferedImage(TEST_IMAGE_SIZE, TEST_IMAGE_SIZE, BufferedImage.TYPE_4BYTE_ABGR);
        d.draw(b.getGraphics());
        return b;
    }
    
}
