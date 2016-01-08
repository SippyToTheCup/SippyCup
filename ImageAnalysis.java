import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * the ImageAnalysis class contains all the methods used in analyzing an image
 * and detecting shapes
 *
 * @author Niel
 */
public class ImageAnalysis {

    private static LinkedList<Shape> shapes;                                    // list of detected shapes
    private static LinkedList<Shape> incompleteShapes;                          // list of incomplete shapes
    private static int[][] imageMatrix;                                         // matrix representation of image
    private static final int sensitivity = -14935268;                           // black/white sensitivity level
    private static int counter, circleCounter, imageWidth, imageHeight, small, med, large;  // counters
    private static int subimageSize;                                            // the size of the subimage used for partial processing
    private static int imageMovement = 4;
    private static int startPosition;
    private ImagesUI imagesFrame;
    private BufferedImage cachedImage;

    /**
     * constructor for the imageAnalysis class initializes variables
     */
    public ImageAnalysis() {
        counter = 0;
        circleCounter = 0;
        small = 0;
        med = 0;
        large = 0;
        incompleteShapes = new LinkedList();

    }

    /**
     * the first iteration of analysis it analyzes the very first image in the
     * set, in order to initialize counters and the user interface
     *
     * @param imageFile the first file image in the sequence
     */
    public void firstIteration(File imageFile) {
        BufferedImage img;
        try {
            // read the image from the file
            img = ImageIO.read(imageFile);

            // a backup image used as a cache when running the sequence
            cachedImage = img;

            // set the dimensions
            imageWidth = img.getWidth();
            imageHeight = img.getHeight();

            // the startposition for the subimage
            startPosition = imageWidth - 40;

            // the subimage is initially the same size as the whole image
            subimageSize = imageWidth;
            shapes = new LinkedList();

            // do the first image
            // write image to integer matrix in 1s and 0s
            buildMatrix(img);

            // analyse the matrix by going through is strip by strip
            // starting from the first strip for the first image
            getStrip(0);

            // the total number of shapes is in the list
            counter = shapes.size();

            // run through the list, update the counters, and draw the circles
            BufferedImage colorImage = cachedImage;
            for (Shape s : shapes) {
                colorImage = updateCounterAndDraw(s, colorImage);
            }
            // set the cache image as the updated image
            cachedImage = colorImage;

            // initialise the jframe
            imagesFrame = new ImagesUI(img, cachedImage, counter, circleCounter, small, med, large);

            //write the modified image to disk
            try {
                ImageIO.write(cachedImage, "jpg", new File("processed_images/processed1.jpg"));
            } catch (Exception e) {
                System.out.println("Error writing image to disk: File " + imageFile.getName());
            }


        } catch (IOException iex) {
            System.out.println("Error reading the file: " + iex.getMessage());
            System.exit(0);
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            System.exit(0);
        }
    }

    // public void followingIterations(File imageFile, int imageFileNo, ImagesUI imagePanel) {
    public void followingIterations(File imageFile, int imageFileNo) {

        shapes = new LinkedList();
        try {
            // read the next image
            BufferedImage img = ImageIO.read(imageFile);
            // write image to matrix in 1s and 0s
            buildMatrix(img);

            // list of the incomplete shapes before it is overwritten
            LinkedList<Shape> tempIncom = incompleteShapes;
            incompleteShapes = new LinkedList();

            // run the strip analysis from the specified starting position
            getStrip(startPosition);

            // shift the cached image up by four pixels in order to give the appearance of the next image
            for (int x = 0; x < imageWidth - imageMovement; x++) {
                for (int y = 0; y < imageHeight; y++) {
                    cachedImage.setRGB(x, y, cachedImage.getRGB(x + imageMovement, y));
                }
            }

            // replace the last four pixels in the cached image by the pixels from the new image
            for (int x = imageWidth - imageMovement; x < imageWidth; x++) {
                for (int y = 0; y < imageHeight; y++) {
                    cachedImage.setRGB(x, y, img.getRGB(x, y));
                }
            }

            // add the newly completed shapes (from the next image) to the current image
            cachedImage = addNewlyCompleted(tempIncom, cachedImage);

            // reset the jframe with the new images and counters
            imagesFrame.reset(ImageIO.read(imageFile), cachedImage, counter, circleCounter, small, med, large);
            //write the modified image to disk
            try {
                ImageIO.write(cachedImage, "jpg", new File("processed_images/processed" + imageFileNo + ".jpg"));
            } catch (Exception e) {
                System.out.println("Error writing image to disk: File " + imageFile.getName());
            }
        } catch (IOException iex) {
            System.out.println("Error reading the file: " + iex.getMessage());
            System.exit(0);
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            System.exit(0);
        }
    }

    /**
     * build integer matrix representation of the image
     *
     * @param img the BufferedImage of the file to be processed
     */
    private static void buildMatrix(BufferedImage img) {
        // matrix is the same dimensions as the image
        imageMatrix = new int[img.getWidth()][imageHeight];

        for (int y = 0; y < imageHeight; y++) {
            for (int x = 0; x < imageMatrix.length; x++) {

                // if its lighter than the threshold, make it 0s, otherwise, 1s
                if ((img.getRGB(x, y) <= sensitivity)) {
                    imageMatrix[x][y] = 1;
                } else {
                    imageMatrix[x][y] = 0;
                }

            }
        }
    }

    /**
     * this method starts the analysis of the image it start from a certain
     * position in the image, and iterates through each x-strip when a pixel is
     * light it adds it to a shape when it reaches the last light pixel it tests
     * if the new shape aligns with any of the previous one
     *
     * @param startposition the starting position in the image
     */
    private static void getStrip(int startposition) {

        // a single strip array of size image height
        int[] strip = new int[imageHeight];

        // start processing from start position
        for (int x = startposition; x < imageMatrix.length - 1; x++) {
            // copy the values in the x-line to the strip
            strip = imageMatrix[x];

            // iterate through the array
            for (int y = 1; y < imageHeight; y++) {
                // if it is a light pixel
                if (strip[y] == 0) {
                    // ensure the pixel is at the correct coordinate  by using the subimage offset
                    int[] pixel = {x + imageWidth - subimageSize, y};

                    // if it is the very first shape in this strip, make it a new shape
                    if (shapes.size() == 0) {
                        shapes.add(new Shape(pixel));
                    } // if it is the first in the sequence add it to shapes
                    else if (strip[y - 1] != 0) {
                        shapes.add(new Shape(pixel));
                        // if its not add it to the latest shape
                    } else {
                        shapes.getLast().addPixel(pixel);
                    }
                    // if the previous pixel was the last in the sequence
                } else if (strip[y - 1] == 0) {
                    // and if this is not the first strip
                    if (x > 0 && shapes.size() > 0) {
                        // get the last strip and test if it alligns with any of the previous strips
                        alignShapes(shapes.getLast(), x);
                    }
                }
            }
        }
        // at the very end test the last strip in the image
        getLastStrip(strip);
    }

    /**
     * this method takes a shape, in the form of a single strip, and its x
     * coordinate in the image then tests if it aligns with any of the
     * previously completed shapes
     *
     * @param last the latest completed shape to be tested
     * @param x the x coordinate
     */
    private static void alignShapes(Shape last, int x) {
        boolean alligned = false;

        LinkedList<Shape> adjacent = new LinkedList();

        // run through shapes and keep a list of all the adjacent shapes
        // this ensures that when two seperate parts of a shape becomes one, it is added together
        for (int i = 0; i < shapes.size() - 1; i++) {
            if (shapes.get(i).align(last, x)) {
                adjacent.add(shapes.get(i));
                alligned = true;
            }
        }

        // compose the whole list into one shape
        if (alligned) {
            Shape newShape = new Shape();

            // and remove the old shapes from the shapes list
            for (Shape s : adjacent) {
                newShape.append(s);
                shapes.remove(s);
            }
            newShape.append(last);
            shapes.remove(last);
            shapes.add(newShape);
        }
    }

    /**
     * the method gets the last array strip from the image and tests for shapes
     * and add them to a separate list otherwise it works the same as getStrip()
     *
     * @param strip an array of the last strip of pixels
     */
    private static void getLastStrip(int[] strip) {

        strip = imageMatrix[imageMatrix.length - 1];


        LinkedList<Shape> lastShapes = new LinkedList();

        // iterate through the array testing for pixels
        for (int y = 1; y < strip.length; y++) {
            // if it is a light pixel
            if (strip[y] == 0) {
                int[] pixel = {imageWidth - 1, y};
                // if it is the first shape
                if (lastShapes.size() == 0) {
                    lastShapes.add(new Shape(pixel));
                } // if it is the first in the sequence add it to shapes
                else if (strip[y - 1] != 0) {
                    lastShapes.add(new Shape(pixel));
                    // if its not add it to the latest shape
                } else {
                    lastShapes.getLast().addPixel(pixel);
                }

                // if the previous pixel was the last in the sequence
            } else if (strip[y - 1] == 0) {
                // and if this is not the first strip
                if (lastShapes.size() > 0) {
                    // get the last strip and test if it alligns with any of the previous strips
                    alignLastShapes(lastShapes.getLast(), imageWidth - 1);
                }
            }
        }
    }

    /**
     * this method gets the latest shape from the last strip of pixels and tests
     * if it aligns with any of the previous shapes
     *
     * @param last the latest shape
     * @param x the x-coordinate of that shape
     */
    private static void alignLastShapes(Shape last, int x) {
        LinkedList<Shape> adjacent = new LinkedList();

        boolean alligned = false;
        // run through shapes and keep a list of adjacent shapes and add them to one list
        for (Shape allignLast : shapes) {
            if (allignLast.align(last, x)) {
                adjacent.add(allignLast);
                alligned = true;
            }
        }

        // for all the adjacent shapes, remove them from the list of complete shapes,
        // and add them to the list of incomplete shapes
        for (Shape adjacentShapes : adjacent) {
            incompleteShapes.add(adjacentShapes);
            shapes.remove(adjacentShapes);
        }

        // if the current shape alligned with any shape add it to the incomplete list
        if (alligned) {
            incompleteShapes.add(last);
        }
    }

    /**
     * this method takes the list of incomplete shapes, tests if any of them are
     * newly completed, and updates the bufferedImage by drawing the new shapes
     *
     * @param tempIncom the list of incomplete shapes
     * @param img the current bufferedImage
     * @return returns an updated bufferedImage with the identified shapes
     */
    private static BufferedImage addNewlyCompleted(LinkedList<Shape> tempIncom, BufferedImage img) {
        BufferedImage color = img;
        // iterate through each incomplete shape
        for (Shape a : tempIncom) {
            // test if the incomplete shape is the same as any of the complete shapes
            for (int i = 0; i < shapes.size(); i++) {
                Shape s = shapes.get(i);
                // if it is the same, it is a complete shape and update the image
                if (a.compareIncomplete(s) == 0) {
                    counter++;
                    color = updateCounterAndDraw(s, color);
                }
            }
        }

        // return the finished image
        return color;
    }

    /**
     * this method takes in a shape and the bufferedImage of the image tests
     * what kind of shape the shape is and draw on the image appropriately
     *
     * @param s the shape to be drawn
     * @param img the image to be drawn on
     * @return returns the new image
     */
    private static BufferedImage updateCounterAndDraw(Shape s, BufferedImage img) {
        // make a new BufferedImage that can handle colour
        BufferedImage color = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        // copy the original image to the colour one
        color.createGraphics().drawImage(img, 0, 0, Color.BLACK, null);

        // get the graphics to draw with
        Graphics2D graph = (Graphics2D) color.getGraphics();

        // the offset for drawing the image
        int ovalOffset = 1;
        int[] corner = s.getLeftCorner();
        int diameter = s.getDiameter() + ovalOffset * 2;

        // if the shape is a circle
        if (s.isCircle()) {
            circleCounter++;
            // draw a red circle if it is in the small category
            if (s.getCategory() == 1) {
                graph.setColor(Color.red);
                graph.drawOval(corner[0] - ovalOffset, corner[1] - ovalOffset, diameter, diameter);
                graph.drawString("S", corner[0] - ovalOffset*2, corner[1] + ovalOffset);
                small++;
                // draw an orange circle if it is in the medium category
            } else if (s.getCategory() == 2) {
                graph.setColor(Color.orange);
                graph.drawOval(corner[0] - ovalOffset, corner[1] - ovalOffset, diameter, diameter);
                graph.drawString("M", corner[0] - ovalOffset*2, corner[1] + ovalOffset);
                med++;
                // draw a magenta circle if it is in the large category
            } else if (s.getCategory() == 3) {
                graph.setColor(Color.magenta);
                graph.drawOval(corner[0] - ovalOffset, corner[1] - ovalOffset, diameter, diameter);
                graph.drawString("L", corner[0] - ovalOffset*2, corner[1] + ovalOffset);
                large++;
            }
            // otherwise draw a grey circle
        } else {
            graph.setColor(Color.gray);
            graph.drawOval(corner[0] - ovalOffset, corner[1] - ovalOffset, diameter, diameter);
        }
        graph.dispose();

        // return the new image
        return color;
    }

    /**
     * gets the number of shapes counted so far
     *
     * @return integer counter of the total number of shapes
     */
    public int getCounter() {
        return counter;
    }

    /**
     * returns an array of statistics about the run
     * @return array of the counters
     */
    int[] getStats() {
        int[] stats = {0, counter, circleCounter, small, med, large};
        try {
            BufferedWriter write = new BufferedWriter(new FileWriter(new File("ImageAnalysisReport.txt"), true));
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

            write.append("\n"+dateFormat.format(new Date()) + ":\n");
            String output = "\n\tTotal: " + counter + "\n\tCircles: " + circleCounter + "\n\tSmall: " + small + "\n\tMedium: " + med + "\n\tLarge: " + large;
            write.append(output);

            write.close();
        } catch (IOException ex) {
            System.out.println("Error writing to file:\n" + ex.getMessage());
            System.exit(0);
        }
        return stats;
    }

    /**
     * closes the image frame
     */
    void close() {
        imagesFrame.dispose();
        imagesFrame.repaint();
    }
}
