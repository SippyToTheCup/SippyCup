

import java.util.LinkedList;

/**
 * @author Niel
 */
public class Shape implements Comparable {

    // list of the pixel arrays in this shape
    LinkedList<int[]> pixellist;
    String name;

    public Shape(int[] pixel) {
        pixellist = new LinkedList();
        pixellist.add(pixel);
        //name = n; 
    }

    Shape() {
        pixellist = new LinkedList();
    }

    // test if the pixel is in the pixellist
    public boolean contains(int[] pixel) {
        boolean contains = false;
        for (int[] test : this.pixellist) {
            contains = (test[0] == pixel[0] && test[1] == pixel[1]);

            if (contains) {
                break;
            }
        }
        return contains;
    }

    
    // test if the pixel is part of this shape
    public boolean sameShape(int[] pixel) {
        boolean same = false;

        try {
            int x = -1;
            while (!same && x < 2) {
                int y = -1;
                while (!same && y < 2) {
                    int test[] = {pixel[0] + x, pixel[1] + y};
                    same = this.contains(test);
                    y++;
                }
                x++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return same;
    }

    public void addPixel(int[] pixel) {
        pixellist.add(pixel);
    }

    @Override
    public String toString() {
        return ("Shape no " + name);
    }

    public int[] getDimensions() {
        int xmin = 0;
        int xmax = Integer.MAX_VALUE;
        int ymin = 0;
        int ymax = Integer.MAX_VALUE;


        for (int[] i : pixellist) {
            if (i[0] < xmin) {
                xmin = i[0];
            } else if (i[0] > xmax) {
                xmax = i[0];
            }

            if (i[1] < ymin) {
                ymin = i[1];
            } else if (i[1] > ymax) {
                ymax = i[1];
            }
        }


        int[] dimen = {xmin, xmax, ymin, ymax};
        return dimen;
    }

    public int getMaxx() {
        int max = 0;

        for (int[] x : pixellist) {
            if (x[0] > max) {
                max = x[0];
            }
        }

        return max;
    }

    public int getMaxy() {
        int max = 0;

        for (int[] y : pixellist) {
            if (y[1] > max) {
                max = y[1];
            }
        }

        return max;
    }

    // print out all the pixels in this shape
    public void listCood() {
        for (int[] i : pixellist) {
            System.out.println("[" + i[0] + "], [" + i[1] + "]");
        }
    }

    public boolean isEmpty() {
        return pixellist.isEmpty();
    }
//    public boolean isSameShape (int []){
//        boolean same
//    }

    
    // test if this strip shape alligns with the current shape
    public boolean allign(Shape last, int x) {
        boolean alligned = false;
        for (int[] i : last.pixellist) {
            int y = i[0];
            int[] adjusted = {y, x - 1};

            alligned = this.contains(adjusted);
            if (alligned) {
                break;
            }
        }
        return alligned;
    }

    // add last shape to this one
    public void append(Shape last) {
        this.pixellist.addAll(last.pixellist);
    }

    
    // testing if the shapes are exactly the same
    public boolean doubleCounting(Shape s) {
        boolean dcount = false;
        for (int[] test : s.pixellist) {
            dcount = this.pixellist.contains(test);

            if (dcount) {
                break;
            }
        }

        return dcount;
    }

    @Override
    public int compareTo(Object o) {
        Shape c = (Shape) o;

        int equals = 0;
        boolean xInRange = false;

        // if the sizes are not the same shape then not the same
        if (this.pixellist.size() != c.pixellist.size()) {
            equals = 1;

        } else {
            for (int i = 0; i < this.pixellist.size(); i++) {
                // test if they are not in the same y range
                if (this.pixellist.get(i)[0] != c.pixellist.get(i)[0]) {
                    equals = 1;
                    break;
                } else if (!xInRange) {
                     // test if they are not in the same x range
                    if (this.pixellist.get(i)[1] <= c.pixellist.get(i)[1]) {
                        xInRange = this.pixellist.get(i)[1] >= c.pixellist.get(i)[1] - 5;
                    } else if (this.pixellist.get(i)[1] >= c.pixellist.get(i)[1]) {
                        xInRange = this.pixellist.get(i)[1] <= c.pixellist.get(i)[1] + 5;
                    }
                }
            }
        }

        if (!xInRange) {
            equals = 1;
        }

        return equals;
    }

    // compare this incomplete shape against a complete one
    int compareIncomplete(Shape get) {
        Shape c = get;

        int equals = 1;
        boolean xInRange = false;


//        for (int i = 0; i < this.pixellist.size(); i++) {
//            if (this.pixellist.get(i)[0] != c.pixellist.get(i)[0]) {
//                equals = 1;
//                break;
//            } else if (!xInRange) {
//
//                if (this.pixellist.get(i)[1] <= c.pixellist.get(i)[1]) {
//                    xInRange = this.pixellist.get(i)[1] >= c.pixellist.get(i)[1] - 5;
//                } else if (this.pixellist.get(i)[1] >= c.pixellist.get(i)[1]) {
//                    xInRange = this.pixellist.get(i)[1] <= c.pixellist.get(i)[1] + 5;
//                }
//            }
//
//        }
//        
//        
//        
//        if (!xInRange){
//                equals =1;
//               
//            }
     

        
        for (int i = 0; i < this.pixellist.size(); i++) {
            try {
                // test if the shapes are in the same y-range
                if (this.pixellist.get(i)[0] == c.pixellist.get(i)[0]) {
                    // test if they are in the same x range
                    if (this.pixellist.get(i)[1] <= c.pixellist.get(i)[1]) {
                        xInRange = this.pixellist.get(i)[1] >= c.pixellist.get(i)[1] - 5;
                    } else if (this.pixellist.get(i)[1] >= c.pixellist.get(i)[1]) {
                        xInRange = this.pixellist.get(i)[1] <= c.pixellist.get(i)[1] + 5;
                    }
                }
                // catch if the shapes are not the same size
            } catch (Exception e) {
                xInRange = false;
            }
            if (xInRange) {
                equals = 0;
                break;
            }

        }

        return equals;
    }
}
