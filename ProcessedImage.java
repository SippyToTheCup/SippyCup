import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 * @author Niel
 */
public class ProcessedImage extends JPanel {

    private BufferedImage img;

    public ProcessedImage(BufferedImage bim) {
        img = bim;

        Initialize();
    }

    public void Initialize() {
        //initialize BufferedImage
        try {
            img.getProperty(TOOL_TIP_TEXT_KEY);
        } catch (Exception e) {
            System.out.println("File not loaded!");
            System.exit(0);

        }

    }

    public void paintComponent(Graphics g) {



        g.drawImage(img, 0, 0, null);
super.paintComponent(g);
    }
}