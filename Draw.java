import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import javax.swing.*;

public class Draw extends JPanel {

    private static Graphics image;
    protected void paintComponent(Graphics g) {
        super.paintComponent(image);
        Graphics2D g2 = (Graphics2D)image;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

       // paint(image);


       // super.paint (image);
    }

    public Draw (Graphics img){
        image = img;
    }

    public Draw(){

    }
}
