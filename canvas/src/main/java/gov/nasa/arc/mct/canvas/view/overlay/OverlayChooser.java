package gov.nasa.arc.mct.canvas.view.overlay;

import gov.nasa.arc.mct.canvas.view.Augmentation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.JComboBox;

public class OverlayChooser extends JComboBox {
    private static final long serialVersionUID = 4749301723217975689L;
    
    public OverlayChooser(final Augmentation augmentation) {
        this(augmentation, augmentation.getOverlays());
    }
    
    public OverlayChooser(final Augmentation augmentation, Collection<CanvasOverlay> overlays) {
        super();
        addItem(new Item(null));
        for (CanvasOverlay overlay : overlays) {
            addItem(new Item(overlay));
        }
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Item item = (Item) getSelectedItem();
                augmentation.setActiveLayer(item.o);
            }        
        });
    }
    
    private static class Item {
        private CanvasOverlay o;
        private Item (CanvasOverlay o) {
            this.o = o;
        }
        public String toString() {
            return (o == null) ? "Panels" : o.getName();
        }
    }
}
