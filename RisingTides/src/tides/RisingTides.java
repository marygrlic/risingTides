package tides;

import java.util.*;

// Mary Grlic

/**
 * This class contains methods that provide information about select terrains 
 * using 2D arrays. Uses floodfill to flood given maps and uses that 
 * information to understand the potential impacts. 
 * Instance Variables:
 *  - a double array for all the heights for each cell
 *  - a GridLocation array for the sources of water on empty terrain 
 * 
 * @author Original Creator Keith Scharz (NIFTY STANFORD) 
 * @author  (Rutgers University)
 */
public class RisingTides {

    // Instance variables
    private double[][] terrain;     // an array for all the heights for each cell
    private GridLocation[] sources; // an array for the sources of water on empty terrain 

    /**
     * DO NOT EDIT!
     * Constructor for RisingTides.
     * @param terrain passes in the selected terrain 
     */
    public RisingTides(Terrain terrain) {
        this.terrain = terrain.heights;
        this.sources = terrain.sources;
    } 

    /**
     * Find the lowest and highest point of the terrain and output it.
     * 
     * @return double[][], with index 0 and index 1 being the lowest and 
     * highest points of the terrain, respectively
     */
    public double[] elevationExtrema() {
        double max = terrain[0][0];
        double min = terrain[0][0];
        double[] extrema = new double[2];

        for (int i = 0; i < terrain.length; i++) {
            for (int j = 0; j < terrain[i].length; j++) { 
                if (terrain[i][j] > max) {
                    max = terrain[i][j];
                } else if (terrain[i][j] < min) {
                    min = terrain[i][j];
                } 
            }
        }
        extrema[0] = min;
        extrema[1] = max;
        return extrema;
    }

    /**
     * Implement the floodfill algorithm using the provided terrain and sources.
     * 
     * All water originates from the source GridLocation. If the height of the 
     * water is greater than that of the neighboring terrain, flood the cells. 
     * Repeat iteratively till the neighboring terrain is higher than the water 
     * height.
     * 
     * 
     * @param height of the water
     * @return boolean[][], where flooded cells are true, otherwise false
     */
    public boolean[][] floodedRegionsIn(double height) {
        int rows = terrain.length;
        int columns = terrain[0].length;
        boolean[][] resultingArr = new boolean[rows][columns];
      
        ArrayList<GridLocation> sourceList = new ArrayList<GridLocation>();
        sourceList.addAll(Arrays.asList(sources));

        for (int i = 0; i < sources.length; i++) {
            resultingArr[sources[i].row][sources[i].col] = true;
        }
        
        while (!sourceList.isEmpty()) {
            int r = sourceList.get(0).row;
            int c = sourceList.get(0).col;
            sourceList.remove(0);

            if (r - 1 >= 0 && r - 1 < rows && c >= 0 && c < columns) {
                if (terrain[r-1][c] <= height && !resultingArr[r-1][c]) {
                    resultingArr[r-1][c] = true;
                    sourceList.add(new GridLocation(r-1, c));
                }
            }
            if (r >= 0 && r < rows && c - 1 >= 0 && c - 1 < columns) {
                if (terrain[r][c-1] <= height && !resultingArr[r][c-1]) {
                    resultingArr[r][c-1] = true;
                    sourceList.add(new GridLocation(r, c-1));
                }
            }
            if (r + 1 >= 0 && r + 1 < rows && c >= 0 && c < columns) {
                if (terrain[r+1][c] <= height && !resultingArr[r+1][c]) {
                    resultingArr[r+1][c] = true;
                    sourceList.add(new GridLocation(r+1, c));
                }
            }
            if (r >= 0 && r < rows && c + 1 >= 0 && c + 1 < columns) {
                if (terrain[r][c+1] <= height && !resultingArr[r][c+1]) {
                    resultingArr[r][c+1] = true;
                    sourceList.add(new GridLocation(r, c+1));
                }
            }
        }
        return resultingArr;
    }

    /**
     * Checks if a given cell is flooded at a certain water height.
     * 
     * @param height of the water
     * @param cell location 
     * @return boolean, true if cell is flooded, otherwise false
     */
    public boolean isFlooded(double height, GridLocation cell) {
        int r = cell.row;
        int c = cell.col;
        boolean[][] floodedRegions = floodedRegionsIn(height);
        return floodedRegions[r][c];
    }

    /**
     * Given the water height and a GridLocation find the difference between 
     * the chosen cells height and the water height.
     * 
     * If the return value is negative, the Driver will display "meters below"
     * If the return value is positive, the Driver will display "meters above"
     * The value displayed will be positive.
     * 
     * @param height of the water
     * @param cell location
     * @return double, representing how high/deep a cell is above/below water
     */
    public double heightAboveWater(double height, GridLocation cell) {
        int r = cell.row;
        int c = cell.col;
        
        double terrainHeight = terrain[r][c];
        double heightAbove = (terrainHeight - height);

        return heightAbove;
    }

    /**
     * Total land available (not underwater) given a certain water height.
     * 
     * @param height of the water
     * @return int, representing every cell above water
     */
    public int totalVisibleLand(double height) {
        int vis = 0;
        boolean[][] arr = floodedRegionsIn(height);

        for (int i = 0; i < terrain.length; i++) {
            for (int j = 0; j < terrain[0].length; j++) {
                if (!arr[i][j]) {
                    vis ++;
                }
            }
        }
        return vis;
    } 


    /**
     * Given 2 heights, find the difference in land available at each height. 
     * 
     * If the return value is negative, the Driver will display "Will gain"
     * If the return value is positive, the Driver will display "Will lose"
     * The value displayed will be positive.
     * 
     * @param height of the water
     * @param newHeight the future height of the water
     * @return int, representing the amount of land lost or gained
     */
    public int landLost(double height, double newHeight) {
        int availableNow = totalVisibleLand(height);
        int availableLater = totalVisibleLand(newHeight);
        int landLost = (int) (availableNow - availableLater);
        return landLost;
    }

    /**
     * Count the total number of islands on the flooded terrain.
     * 
     * Parts of the terrain are considered "islands" if they are completely 
     * surround by water in all 8-directions. Should there be a direction (ie. 
     * left corner) where a certain piece of land is connected to another 
     * landmass, this should be considered as one island. A better example 
     * would be if there were two landmasses connected by one cell. Although 
     * seemingly two islands, after further inspection it should be realized 
     * this is one single island. Only if this connection were to be removed 
     * (height of water increased) should these two landmasses be considered 
     * two separate islands.
     * 
     * @param height of the water
     * @return int, representing the total number of islands
     */
    public int numOfIslands(double height) {
        int rows = terrain.length;
        int cols = terrain[0].length;
        boolean[][] isFlooded = floodedRegionsIn(height);

        ArrayList<GridLocation> islands = new ArrayList<GridLocation>();

        WeightedQuickUnionUF UF = new WeightedQuickUnionUF(rows, cols);
        for (int i = 0; i < rows; i++){
            for (int j = 0; j < cols; j++) {
                if(!isFlooded[i][j]) {
                    for (int r = -1; r <= 1; r++) {
                        for(int c = -1; c <=1; c++) {
                            if(i+r>=0 && j+c>=0 && i+r<terrain.length && j+c<terrain[0].length) {
                                if(!isFlooded[i+r][j+c]) {
                                    UF.union(new GridLocation(i, j), new GridLocation(i+r, j+c));
                                }
                            }
                        }
                    }
                }
            }
        }
        for (int i = 0; i < terrain.length; i++) {
            for (int j = 0; j < terrain[0].length; j++) {
                GridLocation root = UF.find(new GridLocation(i, j));
                if (!isFlooded[i][j] && !islands.contains(root)) {
                    islands.add(root);
                }
            }
        }
        return islands.size();
    }
} 