

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * @author Niel
 */
public class Images extends JFrame {

    private static BufferedImage proci;
    private static BufferedImage origi;
    private static JLabel olabel;
    private static JLabel plabel;
    private static JLabel counter;

    public Images(BufferedImage oimg, BufferedImage pimg, int count) {

        // build jframe
        super("Images");
        proci = pimg;
        origi = oimg;
        this.setSize(266 * 2, 538);

        this.setResizable(false);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
//        this.add(new ProcessedImage(proci));
//        this.add(new OriginalImage(origi));


        //  JPanel panel = new JPanel();
        //  panel.setBackground(Color.RED);
        // panel.setBorder(new EmptyBorder(5,25,5,25));
        
        // add image labels
        olabel = new JLabel(new ImageIcon(origi));
        olabel.addNotify();
        plabel = new JLabel(new ImageIcon(proci));
        plabel.addNotify();

        // add counter label
        counter = new JLabel("\t\t\tNumber of shapes counted: "+count);

//        panel.add(label);
//        JLabel label2 = new JLabel("goodbye");
//        panel.add(label2);

        //   panel.addNotify();
        //  panel.setSize(panel.getPreferredSize());
        //   panel.validate();

        //  panel.add (label);
        this.add(olabel, BorderLayout.WEST);
        this.add(plabel, BorderLayout.EAST);
        this.add(counter, BorderLayout.SOUTH);
        //  JOptionPane.showMessageDialog(null, new JLabel(new ImageIcon(pimg)));


        this.setVisible(true);
    }

    public Images(BufferedImage img) {
        super("Images");

        this.setSize(266, 538);
        this.setResizable(true);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new FlowLayout());
        this.add(new OriginalImage(img));
        this.setVisible(true);
    }

    public void resetProcessedImage(BufferedImage reset) {
        // this.remove(plabel);
        plabel.setIcon(new ImageIcon(reset));
        plabel.addNotify();
        proci = reset;
        // this.add(plabel);
    }

    public void resetOriginalImage(BufferedImage reset) {
        //  this.remove(olabel);
        olabel.setIcon(new ImageIcon(reset));
        olabel.addNotify();
        origi = reset;

        // this.add(olabel);
    }

    
    // reset the frame to the new images and counter
    void reset(BufferedImage img, BufferedImage proc, int count) {
        olabel.setIcon(new ImageIcon(img));
        plabel.setIcon(new ImageIcon(proc));
        counter.setText("\t\t\tNumber of shapes counted: "+count);
    }
}
