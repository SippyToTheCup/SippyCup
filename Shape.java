import java.util.LinkedList;

/**
 * this object abstracts a shape
 *
 * @author Niel
 */
public class Shape implements Comparable {

    // list of the pixel arrays in this shape
    private LinkedList<int[]> pixellist;
    private boolean circle;          // if this hape is a circle
    private double errormargin = 0.25;       // error margin used if it is a circle
    private int comparisonMargin = 5;       // error margin for comparing two shapes
    private int category = -1;              // category of size, 0 = not a disc, 1 = small, 2 = med, 3 = large

    /**
     * constructor for the shape, takes one pixel initially
     *
     * @param pixel coordinate of one pixel in the shape
     */
    public Shape(int[] pixel) {
        // initialise the list of pixels and add the coordinate
        pixellist = new LinkedList();
        pixellist.add(pixel);

        // the shape is not a circle at first
        circle = false;
    }

    /**
     * default constructor
     */
    Shape() {
        pixellist = new LinkedList();
    }

    /**
     * add the pixel array to the pixel list
     *
     * @param pixel the array to be added to this list
     */
    public void addPixel(int[] pixel) {
        pixellist.add(pixel);
    }

    /**
     * add last shapes list of coordinates to this one
     *
     * @param last the shape to be added to this one
     */
    public void append(Shape last) {
        this.pixellist.addAll(last.pixellist);
    }

    /**
     * test if this strip shape aligns with the current shape
     *
     * @param last the strip shape to be tested
     * @param x the x-coordinate of the last shape
     * @return true if the strip aligns with this shape
     */
    public boolean align(Shape last, int x) {
        boolean alligned = false;

        // iterate through the pixellist
        for (int[] i : last.pixellist) {
            int y = i[1];
            // shift the coordinate one x-coordinate left
            int[] adjusted = {x - 1, y};

            // test if the shapes contains the adjusted pixel
            alligned = this.contains(adjusted);
            if (alligned) {
                break;
            }
        }
        return alligned;
    }

    @Override
    public String toString() {
        return ("Size: " + pixellist.size() + "\tCircle: " + circle);
    }

    /**
     * print out all the pixels in this shape
     */
    public void listCood() {
        for (int[] i : pixellist) {
            System.out.println("[" + i[0] + "], [" + i[1] + "]");
        }
    }

    /**
     * tests if this shape is a circle
     *
     * @return
     */
    public boolean isCircle() {
        // if the shape category has not been tested yet
        if (category == -1) {
            // equation of a circle: (x-h)^2 + (y-w)^2 = r^2

            // get the centre and other values
            int maxX = getMaxx();
            int minX = getMinx();
            int[] centre = this.getCentre();
            double diameter = this.getDiameter();
            double circumference = Math.PI * diameter;

            // number of pixels that do not fall in the circle equation
            int errors = 0;

            // iterate from the lowest to the highest x-coordinate
            for (int x = minX; x <= maxX; x++) {

                // calculate the y-w part of the circle equation, keeping in mind its is positive and negative
                double rootypartpos = Math.pow(Math.pow(diameter / 2, 2.0) - Math.pow(x - centre[0], 2.0), 0.5);
                double rootypartneg = rootypartpos * (-1);

                // work out the two y-coordinates
                int ypos = (int) (rootypartpos + centre[1]);
                int yneg = (int) (rootypartneg + centre[1]);

                int[] coods1 = {x, ypos};
                int[] coods2 = {x, yneg};

                // test if this shape contains this coordinate
                // if is doesnt then incremement the number of errors
                if (!this.contains(coods1)) {
                    errors++;
                }
                if (!this.contains(coods2)) {
                    errors++;
                }
            }

            // the precentage of the circumference that is not part of the shape
            double errorpercent = errors / circumference;

            // compare to the errormargin
            circle = errorpercent < errormargin;

            // if it is a circle set the category
            if (circle) {
                if (diameter < 12) {
                    category = 1;
                } else if (diameter < 18) {
                    category = 2;
                } else {
                    category = 3;
                }
            } else {
                category = 0;
            }
        }

        return circle;
    }

    /**
     * test if this list is empty
     *
     * @return true if the list is empty
     */
    public boolean isEmpty() {
        return pixellist.isEmpty();
    }

    /**
     * returns the category of this shape 0 = not a circle 1 = small 2 = medium
     * 3 = large
     *
     * @return integer representation of the category
     */
    public int getCategory() {
        return category;
    }

    /**
     * returns coordinate array of the center of this shape
     *
     * @return array containing the x-coordinate and the y-coordinate
     */
    public int[] getCentre() {
        // half of the minimum and maximum extremes
        int xcentre = (getMaxx() + getMinx()) / 2;
        int ycentre = (getMaxy() + getMiny()) / 2;

        // coordinate array
        int[] centre = {xcentre, ycentre};

        return centre;
    }

    /**
     * gets the uppermost left corner of this shape
     *
     * @return coordinate array of the corner
     */
    public int[] getLeftCorner() {
        // the corner is the minimum x and y-coordinates
        int x = getMinx();
        int y = getMiny();

        int[] corner = {x, y};

        return corner;
    }

    /**
     * returns the largest diameter of the shape
     *
     * @return this shapes diameter
     */
    public int getDiameter() {
        // get the difference
        int xD = Math.abs(getMaxx() - getMinx());
        int yD = Math.abs(getMaxy() - getMiny());

        // return the biggest one
        if (xD > yD) {
            return xD;
        } else {
            return yD;
        }
    }

    /**
     * gets the largest x coordinate
     *
     * @return largest x-coordinate in this shape
     */
    public int getMaxx() {
        int max = 0;

        for (int[] x : pixellist) {
            if (x[0] > max) {
                max = x[0];
            }
        }

        return max;
    }

    /**
     * find the lowest x value
     *
     * @return the lowest x-coordinate in this shape
     */
    public int getMinx() {
        int min = Integer.MAX_VALUE;

        for (int[] x : pixellist) {
            if (x[0] < min) {
                min = x[0];
            }
        }

        return min;
    }

    /**
     * gets the largest y coordinate
     *
     * @return largest y-coordinate in this shape
     */
    public int getMaxy() {
        int max = 0;

        for (int[] y : pixellist) {
            if (y[1] > max) {
                max = y[1];
            }
        }

        return max;
    }

    /**
     * find the lowest y value
     *
     * @return the lowest y-coordinate in this shape
     */
    public int getMiny() {
        int min = pixellist.get(0)[1];

        for (int[] y : pixellist) {
            if (y[1] < min) {
                min = y[1];
            }
        }

        return min;
    }

    /**
     * test if the pixel is in the pixellist
     *
     * @param pixel the coordinate to be tested
     * @return returns true if this pixel is in the shape
     */
    public boolean contains(int[] pixel) {
        boolean contains = false;

        // sequential iteration tests each pixel in the shape
        for (int[] test : this.pixellist) {
            contains = (test[0] == pixel[0] && test[1] == pixel[1]);

            if (contains) {
                break;
            }
        }
        return contains;
    }

    /**
     * overrides the compareTo method compares two shapes
     *
     * @param o the object (shape) this one is to be tested against
     * @return 0 if the two shapes are the same
     */
    @Override
    public int compareTo(Object o) {
        Shape comparing = (Shape) o;

        int equals = 1;

        // test if the shapes are of similar size
        int sizeCompare = Math.abs(this.pixellist.size() - comparing.pixellist.size());

        // if the sizes are less than five pixels difference
        if (sizeCompare < comparisonMargin) {
            int upperYrange = Math.abs(this.getMaxy() - comparing.getMaxy());
            int lowerYrange = Math.abs(this.getMiny() - comparing.getMiny());

            // test if they are in the same y range within five pixels
            if (upperYrange < 5 && lowerYrange < comparisonMargin) {
                int upperXrange = Math.abs(this.getMaxx() - comparing.getMaxx());
                int lowerXrange = Math.abs(this.getMinx() - comparing.getMinx());

                // test if it is in the same x range within five pixels
                if (upperXrange < 5 && lowerXrange < comparisonMargin) {
                    equals = 0;
                }
            }
        }

        return equals;
    }

    /**
     * compare this incomplete shape against a complete one
     *
     * @param get the incomplete shape this shape is tested against
     * @return true if the shapes are the same
     */
    int compareIncomplete(Shape get) {
        Shape comparing = get;
        int equals = 1;

        // test if the two shapes are within y-range by subtracting and testing withing a boundary
        int upperYrange = Math.abs(this.getMaxy() - comparing.getMaxy());
        int lowerYrange = Math.abs(this.getMiny() - comparing.getMiny());

        if (upperYrange < comparisonMargin && lowerYrange < comparisonMargin) {
            // test if they are within x range
            int upperXrange = Math.abs(this.getMaxx() - comparing.getMaxx());
            int lowerXrange = Math.abs(this.getMinx() - comparing.getMinx());

            if (upperXrange < comparisonMargin * 2 && lowerXrange < comparisonMargin * 2) {
                equals = 0;
            }
        }

        return equals;
    }
}
