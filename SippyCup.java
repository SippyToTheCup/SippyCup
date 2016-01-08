import java.awt.Graphics;
import java.awt.Window;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.File;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.LinkedList;
import javax.swing.*;

public class SippyCup extends JFrame {

    private static File test = new File("newfile.txt");                         // file for writing tests
    private static BufferedWriter write;
    private static LinkedList<Shape> shapes;                                    // list of detected shapes
    private static LinkedList<Shape> incompleteShapes;                          // list of incomplete shapes
    private static int[][] imageMatrix;                                         // matrix representation of image
    private static final int sensitivity = -14935268;                           // black/white sensitivity level
    private static int counter;
    //private static Images frame;

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        int imageFileNo = 0;

        try {

            // initialise variables
            BufferedImage img;
            String fileName = "raw_images/testseq00000.gif";
            File imageFile = new File(fileName);
            counter = 0;
            imageFileNo = 1;
            // create processed_images directory
            new File("processed_images").mkdirs();

            // read the image from the file
            img = ImageIO.read(imageFile);

            // first iteration of images
            firstIteration(img);

            // initialise the jframe
            Images imagesFrame = new Images (ImageIO.read(imageFile), img, counter);

            // build the name of the next file (could use a regex)
            fileName = fileName.substring(0, (fileName.length() - 5) -
                    (int) Math.log10(imageFileNo)) + imageFileNo + ".gif";
            imageFile = new File(fileName);
            int halfWidth = imageMatrix[0].length;

            // read the rest of the images
            while (imageFile.exists()) {
                long imagestart = System.currentTimeMillis();
                shapes = new LinkedList();
                img = ImageIO.read(imageFile);

                // write image to matrix in 1s and 0s
                imageMatrix = new int[img.getHeight()][halfWidth];
                buildMatrix(img);

                // list of the incomplete shapes before it is overwritten
                LinkedList<Shape> tempIncom = incompleteShapes;

                incompleteShapes = new LinkedList();

                // make an column array of only one line of pixels
                int[] strip = new int[imageMatrix.length];

                for (int x = 0; x < halfWidth - 1; x++) {
                    for (int y = 0; y < imageMatrix.length; y++) {
                        // copy the line to the strip
                        strip[y] = imageMatrix[y][x];
                    }
                    // call the method to find the sequence of shape pixels in the strip
                    getStrip(strip, x);
                }

                // second loop to get last strip of image
                for (int y = 0; y < imageMatrix.length; y++) {
                    // copy the line to the strip
                    strip[y] = imageMatrix[y][halfWidth - 1];

                }
                // call the method to find the sequence of shape pixels in the strip
                getLastStrip(strip, halfWidth - 1);


                // if any of the incomplete shapes from the previous image is now complete, add it to the counter
                for (Shape a : tempIncom) {
                    for (int i = shapes.size() - tempIncom.size(); i < shapes.size(); i++) {
                        // compare the previously incomplete shape if it matches with any of the new complete shapes
                        if (a.compareIncomplete(shapes.get(i)) == 0) {
                            counter++;
                        }
                    }
                }


                //change the colour of the detected pixels
                for (Shape s : shapes) {
                    //int ARGB = new Color(0, 200, 0, 0).getRGB();
                    for (int[] pixels : s.pixellist) {
                        img.setRGB(pixels[1], pixels[0], 0);
                    }
                }

                // reset the jframe with the new images and counter
                imagesFrame.reset(ImageIO.read(imageFile), img, counter);
                imagesFrame.repaint();


                // write the modified image to disk
                ImageIO.write(img, "gif", new File("processed_images/proccessed" + imageFileNo + ".gif"));

                System.out.println("Image " + imageFileNo + ": " + (double) (System.currentTimeMillis() - imagestart) / 1000 + " seconds");

                // get the name of the next file
                imageFileNo++;
                fileName = fileName.substring(0, (fileName.length() - 5) - (int) Math.log10(imageFileNo)) + imageFileNo + ".gif";
                imageFile = new File(fileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // print out proccessing time
            double time = (double) (System.currentTimeMillis() - start) / 1000;
            System.out.println(time + " seconds\n" + counter + " shapes\n" + imageFileNo / time + " frames per second");
        }
    }

    // write the integer matrix to a file
    private static void writeMatrixToFile() {
        try {
            write = new BufferedWriter(new FileWriter(test));
            // write matrix to file
            for (int[] y : imageMatrix) {
                for (int x : y) {
                    write.append(x + "");
                }
                write.append("\n");
            }

            write.close();
        } catch (Exception e) {
            System.out.println("writing error");
        }

    }

    // return a colour value based on the rgb components
    private static int returnRGB(int argb) {
        int rgb[] = new int[]{
            (argb >> 16) & 0xff, //red
            (argb >> 8) & 0xff, //green
            (argb) & 0xff //blue
        };
        int colour = rgb[0] + rgb[1] + rgb[2];
        return colour;
    }

    // build integer matrix representation of the image
    private static void buildMatrix(BufferedImage img) {
        for (int y = 0; y < imageMatrix.length; y++) {
            for (int x = 0; x < imageMatrix[0].length; x++) {

                int argb = img.getRGB(x, y);
                int colour = returnRGB(img.getRGB(x, y));
                if ((argb <= sensitivity)) {
                    imageMatrix[y][x] = 1;
                } else {
                    imageMatrix[y][x] = 0;
                }

            }
        }
    }

    // method taking a single strip of pixels and looking for a sequence in it to add the shapes
    private static void getStrip(int[] strip, int x) {
        for (int i = 1; i < strip.length; i++) {
            // if it is a light pixel
            if (strip[i] == 0) {
                int[] pixel = {i, x};

                // if it is the first shape
                if (shapes.size() == 0) {
                    shapes.add(new Shape(pixel));
                } // if it is the first in the sequence add it to shapes
                else if (strip[i - 1] != 0) {
                    shapes.add(new Shape(pixel));
                    // if its not add it to the latest shape
                } else {
                    shapes.getLast().addPixel(pixel);
                }

                // if the previous pixel was the last in the sequence
            } else if (strip[i - 1] == 0) {
                // and if this is not the first strip
                if (x > 0 && shapes.size() > 0) {
                    // get the last strip and test if it alligns with any of the previous strips
                    allignShapes(shapes.getLast(), x);
                }
            }
        }
    }

    // method to get a strip of pixels and test if it alligns with any of the previous shapes
    private static void allignShapes(Shape last, int x) {
        boolean alligned = false;

        LinkedList<Shape> adjacent = new LinkedList();

        // run through shapes and keep a list of adjacent shapes
        for (int i = 0; i < shapes.size() - 1; i++) {
            if (shapes.get(i).align(last, x)) {
                adjacent.add(shapes.get(i));
                alligned = true;
            }
        }

        // compose the whole list into one shape
        if (alligned) {
            Shape newShape = new Shape();

            // and remove the old shapes
            for (Shape s : adjacent) {
                newShape.append(s);
                shapes.remove(s);
            }
            newShape.append(last);
            shapes.remove(last);
            shapes.add(newShape);
        }
    }

    private static void getLastStrip(int[] strip, int x) {
        LinkedList<Shape> lastShapes = new LinkedList();

        for (int i = 1; i < strip.length; i++) {
            // if it is a light pixel
            if (strip[i] == 0) {
                int[] pixel = {i, x};

                // if it is the first shape
                if (lastShapes.size() == 0) {
                    lastShapes.add(new Shape(pixel));
                } // if it is the first in the sequence add it to shapes
                else if (strip[i - 1] != 0) {
                    lastShapes.add(new Shape(pixel));
                    // if its not add it to the latest shape
                } else {
                    lastShapes.getLast().addPixel(pixel);
                }

                // if the previous pixel was the last in the sequence
            } else if (strip[i - 1] == 0) {
                // and if this is not the first strip
                if (lastShapes.size() > 0) {
                    // get the last strip and test if it alligns with any of the previous strips
                    allignLastShapes(lastShapes.getLast(), x);
                }
            }
        }
    }

    // method to get a strip of pixels and test if it alligns with any of the previous shapes
    private static void allignLastShapes(Shape last, int x) {
        boolean alligned = false;

        LinkedList<Shape> adjacent = new LinkedList();

        // run through shapes and keep a list of adjacent shapes
        for (int i = 0; i < shapes.size(); i++) {
            if (shapes.get(i).align(last, x)) {
                adjacent.add(shapes.get(i));
                alligned = true;
            }
        }

        // if there are any shapes that align
        // compose the whole list into one shape
//        if (alligned) {
//            Shape newShape = new Shape();
//
//            // and remove the old shapes
//            for (Shape s : adjacent) {
//                newShape.append(s);
//
//            }
//            newShape.append(last);
//            incompleteShapes.add(newShape);
//        }

        // remove the incomplete shape from the complete list
        for (Shape s : adjacent) {
            incompleteShapes.add(s);
            shapes.remove(s);
        }

    }


    // runs the detection of the first image
    private static void firstIteration(BufferedImage img) {
        long imagestart = System.currentTimeMillis();

        incompleteShapes = new LinkedList();
        shapes = new LinkedList();

        // write image to matrix in 1s and 0s
        imageMatrix = new int[img.getHeight()][img.getWidth()];
        buildMatrix(img);

        // make an column array of only one line of pixels
        int[] strip = new int[imageMatrix.length];

        for (int x = 0; x < imageMatrix[0].length - 1; x++) {
            for (int y = 0; y < imageMatrix.length; y++) {
                // copy the line to the strip
                strip[y] = imageMatrix[y][x];
            }
            // call the method to find the sequence of shape pixels in the strip
            getStrip(strip, x);
        }

        // second loop to get last strip of image
        for (int y = 0; y < imageMatrix.length; y++) {
            // copy the line to the strip
            strip[y] = imageMatrix[y][imageMatrix[0].length - 1];

        }
        // call the method to find the sequence of shape pixels in the strip
        getLastStrip(strip, imageMatrix[0].length - 1);


        BufferedImage orig = img;
        // change the colour of the detected pixels
        for (Shape s : shapes) {
            for (int[] pixels : s.pixellist) {
                img.setRGB(pixels[1], pixels[0], 0);
            }
        }

        // frame = new Images (orig, img);

        try {
            // write the modified image to disk
            ImageIO.write(img, "gif", new File("processed_images/proccessed0.gif"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        counter = shapes.size();
        System.out.println("Image 0: " + (double) (System.currentTimeMillis() - imagestart) / 1000 + " seconds");
    }
}
