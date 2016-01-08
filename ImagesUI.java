import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * this is the user interface object it displays the original image, and the
 * processed one next to it along with counters of shapes and circles
 *
 * @author Niel
 */
public class ImagesUI extends JFrame {
    //   public class ImagesUI extends JPanel {

    private static BufferedImage proci;                     // processed image
    private static BufferedImage origi;                     // original image
    private static JLabel olabel;
    private static JLabel plabel;
    private static JPanel counterpanel;
    private static JLabel numbercounter;
    private static JLabel categorycounter;

    public ImagesUI(BufferedImage oimg, BufferedImage pimg, int count, int circleCounter, int small, int med, int large) {

        // build jframe
        super("Images");
        proci = pimg;
        origi = oimg;
        this.setSize(266 * 2, 590);
        this.setLocation(20, 20);
        this.setResizable(true);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        // set the images in the frames as JLabels
        olabel = new JLabel(new ImageIcon(origi));
        plabel = new JLabel(new ImageIcon(proci));

        // initialise a panel for displaying the number counters
        counterpanel = new JPanel(new BorderLayout());

        // add counter label
        numbercounter = new JLabel("Number of shapes counted: " + count + "     Number of circles counted: " + circleCounter);
        categorycounter = new JLabel("Small: " + small + " Medium: " + med + " Large: " + large);

        counterpanel.add(numbercounter, BorderLayout.NORTH);
        counterpanel.add(categorycounter, BorderLayout.SOUTH);

        // add the components
        this.add(olabel, BorderLayout.WEST);
        this.add(plabel, BorderLayout.EAST);
        this.add(counterpanel, BorderLayout.SOUTH);

        this.setVisible(true);
    }

    /**
     * resets the frame with the new images and counters
     *
     * @param img the original image
     * @param proc the processed image
     * @param count the total shape count
     * @param circleCounter count of circles
     * @param small count of small circles
     * @param med count of medium circles
     * @param large count of large circles
     */
    void reset(BufferedImage img, BufferedImage proc, int count, int circleCounter, int small, int med, int large) {
        olabel.setIcon(new ImageIcon(img));
        plabel.setIcon(new ImageIcon(proc));
        numbercounter.setText("Number of shapes counted: " + count + "     Number of circles counted: " + circleCounter);
        categorycounter.setText("Small: " + small + "  Medium: " + med + "  Large: " + large);
    }
}
