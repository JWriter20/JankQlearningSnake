// Maze class
// Written by:
// Date:

// This maze class adds a bit more functionality from last week's
// for example, the ability to load a maze from an array of strings
// you'll add more capability in the form of the multiBFS function and
// some helper functions...

import java.io.BufferedReader;  // This is required for dealing with files
import java.io.FileReader;      // This is required for dealing with files

class Maze1
{

    // MazeCell - an inner class supporting Maze
    //
    // The following convention is used in mazes:
    // Walls are represented by '*'
    // Empty area is represented by the blank symbol ' '
    // Starting point is represented by 'S'
    // Destination (SPAM!) is represented by 'D'

    class MazeCell
    {

        private int row;                 // The row at which this cell is located
        private int col;                 // The col at which this cell is located
        private char contents;           // Each cell has contents (a char)
        private boolean visited;         // A cell can be marked as visited.
        private MazeCell parent;         // parent is where we came from!

        // Constructor of the MazeElement at row, col, with contents c
        //   "visited" is set to false, and "parent" is set to null
        private MazeCell(int row, int col, char c)
        {
            this.row = row;        // this is required to avoid name confusion!
            this.col = col;        // ditto
            this.contents = c;
            this.visited = false;  // we haven't been here yet...
            this.parent = null;    // ... so we have no parent yet
        }

        // toString returns the string representation of a MazeElement
        public String toString()  { return "[" + row + "," + col + "," + contents + "]"; }

        private boolean isWall()  { return this.contents == '*';  }
        private boolean isOpen()  { return this.contents == ' ' || this.contents == 'D'; }
    }


    /* data member for the Maze class...
     * a 2d rectangular array of MazeCells
     */
    protected MazeCell[][] maze;  // this is the maze!


    /* method: constructor
     * input: file name
     * output: a maze containing that file's data
     */
    public Maze1(String filename)
    {
        this.maze = null;
        this.loadMazeFromFile(filename);
    }

    /* method: constructor
     * input: none
     * output: a maze containing ththe data in mazeStrings, below
     */
    protected Maze1()
    {
        int HEIGHT = mazeStrings.length;
        int WIDTH = mazeStrings[0].length();
        this.maze = new MazeCell[HEIGHT][WIDTH];
        for (int r=0 ; r<HEIGHT ; ++r) {
            for (int c=0 ; c<WIDTH ; ++c) {
                maze[r][c] = new MazeCell(r,c,mazeStrings[r].charAt(c));
            }
        }
    }

    private static final String[] mazeStrings =  {
            "**************************************************",
            "*PS D                                            *",
            "*                                                *",
            "*                                                *",
            "*                                                *",
            "*                                                *",
            "*                                                *",
            "*                                                *",
            "*                                                *",
            "*                                                *",
            "*                                                *",
            "*                                                *",
            "*                       **                       *",
            "*                       **                       *",
            "*                       **                       *",
            "*                       **                       *",
            "*                                                *",
            "*                                                *",
            "*                                                *",
            "*                                                *",
            "*                                                *",
            "*                                                *",
            "*                                                *",
            "*                                                *",
            "*                                                *",
            "*                                        D       *",
            "*                                                *",
            "*                                                *",
            "*                                                *",
            "**************************************************"
    };

    // PROVIDED METHOD.  NO NEED TO ALTER.
    public static void main(String args[])
    {
        Maze1 M = null;

        if (args.length < 1) {
            M = new Maze1();               // the no-input constructor will use the
            // array of strings above!
        } else {
            String filename = args[0];    // The filename comes from the command line
            M = new Maze1(filename);       // this creates the maze
        }

        MazeCell start = M.findMazeCell('S');    // get the source
        char destination = 'D';

        MazeCell nextCellToGo = null;
        //nextCellToGo = M.multiBFS(start, 'D');  // comment back in when you've implemented this

        System.out.println("starting cell is   " + start);
        System.out.println("next cell to go is " + nextCellToGo);
        System.out.println("\nM is" + M);         // M should not change!
    }

    // the findMazeCell method takes the maze and a char token
    // (either 'S' or 'D') and returns the MazeElement containing the
    // location of that token in the maze.
    // PROVIDED METHOD.  NO NEED TO ALTER.
    public MazeCell findMazeCell(char charToFind)
    {
        for (int r = 0; r < maze.length; r++)
            for (int c = 0; c < maze[r].length; c++)
                if (maze[r][c].contents == charToFind)
                    return maze[r][c];
        return null;  // Error in finding the itemToFind: it's not there!
    }

    /* method: BFS
     * input: two maze cells
     * output: none; prints the path
     */
    public void BFS(MazeCell start, MazeCell destination)
    {
        //System.out.println("Breadth-first search\n");
        Queue cellsToVisit = new Queue();

        start.visited = true;
        cellsToVisit.enqueue( start );

        while (!cellsToVisit.isEmpty())
        {
            MazeCell current = (MazeCell)cellsToVisit.dequeue();

            if (current == destination) // have we reached the goal?
            {
                MazeCell pathElement = current.parent;
                while (pathElement != start && pathElement != null)
                {
                    pathElement.contents = 'o';
                    pathElement = pathElement.parent;
                }
                return; // done!
            }

            // not the destination, enqueue neighbors if unmarked and not walls
            int currentRow = current.row;
            int currentCol = current.col;
            MazeCell northNeighbor = maze[(currentRow-1)][currentCol];
            MazeCell southNeighbor = maze[(currentRow+1)][currentCol];
            MazeCell  eastNeighbor = maze[currentRow][(currentCol+1)];
            MazeCell  westNeighbor = maze[currentRow][(currentCol-1)];

            if (!southNeighbor.visited && !southNeighbor.isWall())
            {
                southNeighbor.visited = true;
                southNeighbor.parent = current;
                cellsToVisit.enqueue(southNeighbor);
            }

            if (!eastNeighbor.visited && !eastNeighbor.isWall())
            {
                eastNeighbor.visited = true;
                eastNeighbor.parent = current;
                cellsToVisit.enqueue(eastNeighbor);
            }

            if (!westNeighbor.visited && !westNeighbor.isWall())
            {
                westNeighbor.visited = true;
                westNeighbor.parent = current;
                cellsToVisit.enqueue(westNeighbor);
            }

            if (!northNeighbor.visited && !northNeighbor.isWall())
            {
                northNeighbor.visited = true;
                northNeighbor.parent = current;
                cellsToVisit.enqueue(northNeighbor);
            }


        } // end while cellsToVisit is not empty

        System.out.println("\nMaze not solvable!");
    }

    /* method: clearFlags
     * input: none
     * output: clears all visited and parent data members
     *         also removes any path indicators, e.g., 'o'
     */
    private void clearFlags()
    {
        ; // TO BE IMPLEMENTED!
    }


    /* method: multiBFS
     * input: a starting cell and a char to seek
     * output: the maze cell that is BESIDE START and NEXT ALONG
     *         the path to the nearest destination!
     *
     * optional: printing the path to the goal
     *           if you do, BE SURE TO CLEAR _EVERYTHING_ OUT! before returning...
     *
     * if there is no path, this method should return an open MazeCell
     * that is NEXT TO START
     *
     * if there is no open MazeCell that is NEXT TO START, this method
     * should return any MazeCell that is next to start (and it will crash)
     */
    protected MazeCell multiBFS(MazeCell start, char destination)
    {
        ; // TO BE IMPLEMENTED!
        return null;   // need to return an appropriate MazeCell...
    }

    // converts a maze to a string for printing
    public String toString()
    {
        String result = "\n";
        for (int r=0 ; r<maze.length ; ++r)
        {
            for (int c=0 ; c<maze[r].length ; ++c)
                result += maze[r][c].contents;
            result += "\n";
        }
        result += "\n";
        return result;
    }


    // loadMaze method takes the maze and a String with the name of the
    // 10 by 10 maze file to open and loads that file into the maze array.
    // PROVIDED METHOD.  NO NEED TO ALTER.
    public void loadMazeFromFile(String filename)
    {
        try // in case the file isn't there (or the name was misspelled, etc.)
        {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            // get the first line...
            String fileLine = reader.readLine();
            // this line should have two integers...
            String[] dimensions = fileLine.trim().split("\\s+"); // split on whitespace
            // get the two integers...
            int height = Integer.parseInt(dimensions[0]);
            int width = Integer.parseInt(dimensions[1]);
            // create the maze cell references ... but not the MazeCells themselves
            maze = new MazeCell[height][width];  // all of the MazeCells are null!!

            for (int r = 0; r < maze.length; r++)     // loop over the rows
            {
                fileLine = reader.readLine();
                for (int c = 0 ; c < maze[r].length; c++)  // loop over the columns
                {
                    char ch = fileLine.charAt(c);
                    maze[r][c] = new MazeCell(r,c,ch);
                }
                // now the MazeElements are not null...
            }
        }
        catch (Exception e)
        {
            System.out.println("The maze file " + filename + " was absent or misformatted.");
            System.exit(0);
        }
    }
}