import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Arrays;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.UIManager;

public class SippyCup {

    private static javax.swing.JFileChooser fileChooser;

    /**
     * main method for the project, initializes most variables and user
     * interfaces
     *
     * @param args
     */
    public static void main(String[] args) {
        try{
        // open the file - runs the filechooser
        openFile();
        }
        catch (Exception e){
            System.out.println ("Error:\n"+e.getMessage());
            System.exit(0);
        }
    }

    /**
     * runs a filechooser dialog so that the user can select which file to
     * process
     */
    public static void openFile() {
        try {
            // set the feel to the local machines
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            JFrame frame = new JFrame();
            String fileName = "";

            // open a filechooser dialog
            fileChooser = new javax.swing.JFileChooser(new File("."));
            fileChooser.setDialogTitle("Open first GIF image in the sequence");
            fileChooser.setFileFilter(new customFilter());
            fileChooser.setFileHidingEnabled(true);
            int returnVal = fileChooser.showOpenDialog(frame);

            // get the filename, otherwise exit the system
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                fileName = (file.getAbsolutePath());

                // start the analysis from the chosen file
                runAnalysis(fileName);
            } else {
                System.exit(0);
            }
        } catch (ClassNotFoundException cnf) {
            System.out.println("UI manager error:\n" + cnf.getMessage());
            System.exit(0);
        } catch (Exception ex) {
            System.out.println("Error:\n" + ex.getMessage());
            System.exit(0);
        }
    }

    /**
     * starts the analysis on the file
     *
     * @param fileName the initial image file
     */
    private static void runAnalysis(String fileName) {
        ImageAnalysis analysis = new ImageAnalysis();
        try {
            // load sequence of files:
            File imageFile = new File(fileName);

            // if the file doesnt exist throw an exception
            if (!imageFile.exists()) {
                throw new FileNotFoundException(fileName + " not found");
            }
            // create processed_images directory
            new File("processed_images").mkdirs();

            // get all the image files in this directory
            int start = fileName.lastIndexOf(".");
            final String ends = fileName.substring(start);

            File dir = imageFile.getParentFile();
            File[] fileSequence = dir.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(ends);
                }
            });
            Arrays.sort(fileSequence);

            // run the analysis on the first image
            // this is needed to initialise the images and counters
            analysis.firstIteration(fileSequence[0]);

            // run through all the files and do the analysis
            for (int i = 1; i<fileSequence.length; i++) {
                analysis.followingIterations(fileSequence[i], i);
            }

            // get the stats from the analysis
            int[] stats = analysis.getStats();
            stats[0] = fileSequence.length;

            // after anaylis show the secondary GUI
            showSecondaryUI(analysis, stats);
        } catch (FileNotFoundException f) {
            System.out.println("File error: \n"+f.getMessage());
            System.exit(0);
        } catch (IOException ex) {
            System.out.println("Input/Output error:\n" + ex.getMessage());
            System.exit(0);
        } catch (Exception e) {
            System.out.println("Error: \n"+e.getMessage());
            System.exit(0);
        }
    }

    /**
     * initializes the secondary GUI
     *
     * @param ia the imageanalysis object
     * @param stats the stats of the analysis
     */
    private static void showSecondaryUI(ImageAnalysis ia, int[] stats) {
        new SecondaryUI(ia, stats);
    }
}

// a custom class setting the type the filechooser looks for
class customFilter extends javax.swing.filechooser.FileFilter {

    @Override
    public boolean accept(File file) {
        // Allow only directories, or files with ".gif" extension
        return file.isDirectory() || file.getAbsolutePath().endsWith(".gif");
    }

    @Override
    public String getDescription() {
        return "GIF Images (*.gif)";
    }
}
