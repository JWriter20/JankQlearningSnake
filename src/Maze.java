// Maze class
// Name: Jane Hoffswell
// Date: March 5, 2011
// Time: 2 hours
// Comments:

// This maze class adds a bit more functionality from last week's
// for example, the ability to load a maze from an array of strings
// you'll add more capability in the form of the multiBFS function and
// some helper functions...

import java.io.BufferedReader;  // This is required for dealing with files
import java.io.FileReader;      // This is required for dealing with files

class Maze
{

    // MazeCell - an inner class supporting Maze
    //
    // The following convention is used in mazes:
    // Walls are represented by '*'
    // Empty area is represented by the blank symbol ' '
    // Starting point is represented by 'S'
    // Destination (SPAM!) is represented by 'D'

    // Definiton of Magic Values:
    protected char WALL = '*';
    protected char SPACE = ' ';
    protected static char SPAM = 'D';
    protected char HEAD = 'S';
    protected char BODY = 'P';
    protected char PATH = 'o';
    protected char ENEMY = 'E';
    protected char ENEMYBODY = 'B';

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

        public String toString() { return "[" + row + "," + col + "," + contents + "]"; }
        protected boolean isWall() { return this.contents == WALL; }
        protected boolean isBody() { return (this.contents == BODY || this.contents == ENEMYBODY); }

        // this was copied from the given solutions
        protected boolean isOpen()  { return this.contents == SPACE || this.contents == SPAM; }

        // The 'getters and setters' for a MazeCell
        // getRow, getCol, getContents, setContents
        public int getRow() { return this.row; }
        public int getCol() { return this.col; }
        public char getContents() { return this.contents; }
        public void setContents(char newcontents) { this.contents = newcontents; }

    }

    /* data member for the Maze class...
     * a 2d rectangular array of MazeCells
     */
    MazeCell[][] maze;  // this is the maze!


    /* constructor (single input)
     * this delegates most work to the file-loading method
     */
    public Maze(String filename)
    {
        this.maze = null;
        this.loadMazeFromFile(filename);
    }

    /* method: constructor (zero input)
     * inputs: none
     * output: a newly constructed Maze object (using the maze below)
     * notes:  the zero-input construct is given in the assignment page
     * */
    protected Maze(){
        int ROWS = M.maze0.length;
        int COLUMNS = M.maze0[0].length();
        this.maze = new MazeCell[ROWS][COLUMNS];
        for (int r=0 ; r< ROWS ; r++){
            for (int c=0 ; c< COLUMNS ; c++){
                maze[r][c] = new MazeCell(r,c,M.maze0[r].charAt(c));
            }
        }
    } // end constructor

    /* method: constructor
     * inputs: level - an integer representing the level
     * output: a newly constructed Maze object (using the maze below)
     * notes:  the zero-input construct is given in the assignment page
     * */
    protected Maze(int level){

        // determiens the maze for the given level
        String[] mazeInput = null;
        if (level == 0)
            mazeInput = M.maze0;
        else if (level == 1)
            mazeInput = M.maze1;
        else if (level == 2)
            mazeInput = M.maze2;
        else if (level == 3)
            mazeInput = M.maze3;
        else if (level == 4)
            mazeInput = M.maze4;
        else if (level == 5)
            mazeInput = M.maze5;
        else if (level == 6)
            mazeInput = M.maze6;
        else if (level == 7)
            mazeInput = M.maze7;
        else if (level == 8)
            mazeInput = M.maze8;
        else if (level == 9)
            mazeInput = M.maze9;

        // constructs the maze
        int ROWS = mazeInput.length;
        int COLUMNS = mazeInput[0].length();
        this.maze = new MazeCell[ROWS][COLUMNS];
        for (int r=0 ; r< ROWS ; r++){
            for (int c=0 ; c< COLUMNS ; c++){
                maze[r][c] = new MazeCell(r,c,mazeInput[r].charAt(c));
            }
        }
    } // end constructor

    // the findMazeCell method takes the maze and a char token
    // (either 'S' or 'D') and returns the MazeElement containing the
    // location of that token in the maze.
    public MazeCell findMazeCell(char charToFind){
        for (int r = 0; r < maze.length; r++)
            for (int c = 0; c < maze[r].length; c++)
                if (maze[r][c].contents == charToFind)
                    return maze[r][c];
        return null;  // Error in finding the charToFind: it's not there!
    } // end findMazeCell

    // BFS method takes the start MazeElement and the maze as input and
    // performs the breadth-first search algorithm in the maze beginning
    // at location start.  When the method ends, each reachable
    // empty (' ') MazeElement should have its "parent" data member
    // set to the MazeElement from which it was visited.
    public void BFS(MazeCell start, MazeCell destination, String directions)
    {
        Queue mazeCells = new Queue();
        start.visited = true;
        mazeCells.enqueue(start);
        boolean route = false; // a boolean determing whether a path has been found

        while (mazeCells.isEmpty() == false && route == false){
            MazeCell current = (MazeCell) mazeCells.dequeue();
            int row = current.row;
            int column = current.col;

            // determines the neighboring cells for each direction
            // note: it also determines whether or not the neighbor is in bounds
            //       if not, the cell wraps around to the opening on the other side
            MazeCell n, e, s, w;
            // North case
            if ((row-1) < 0){
                n = maze[maze.length-1][column];
            }else{
                n = maze[row-1][column];
            }
            // East case
            if ((column+1) >= maze[0].length){
                e = maze[row][0];
            }else{
                e = maze[row][column+1];
            }
            // South case
            if ((row+1) >= maze.length){
                s = maze[0][column];
            }else{
                s = maze[row+1][column];
            }
            // West case
            if ((column-1) < 0){
                w = maze[row][maze[0].length-1];
            }else{
                w = maze[row][column-1];
            }

            // determines the order of the search using the input directions
            char[] d = {directions.charAt(0), directions.charAt(1),
                    directions.charAt(2), directions.charAt(3)};
            MazeCell[] neighbors = new MazeCell[4];
            for (int i=0; i<4; i++){
                if (d[i] == 'N'){
                    neighbors[i] = n;
                }else if (d[i] == 'E'){
                    neighbors[i] = e;
                }else if (d[i] == 'S'){
                    neighbors[i] = s;
                }else{
                    neighbors[i] = w;
                }
            }

            // now, conducts BFS to find the shortest path
            for (int i=0; i<4; i++){
                if (neighbors[i].row == destination.row && neighbors[i].col == destination.col){
                    neighbors[i].visited = true;
                    neighbors[i].parent = current;
                    route = true;
                }else if (neighbors[i].visited == false && neighbors[i].isWall() == false){
                    neighbors[i].visited = true;
                    neighbors[i].parent = current;
                    mazeCells.enqueue(neighbors[i]);
                }
            }
        } // end while

        // fills in the shortest path with 'o' or returns an unsolvable error
        if (route == false){
            System.out.println("Error: Maze not solvable!\n");
        }else{
            MazeCell current = destination.parent;
            while (current.row != start.row || current.col != start.col){
                current.contents = PATH;
                current = current.parent;
            }
        }

    } // end BFS

    /* method: toString
     * inputs: none
     * output: the String representation of the maze
     */
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

    /* method: multiBFS
     * inputs: start - the starting cell for BFS, destination - the character
     *         designating the destination
     * output: the next MazeCell in the path to the closest destination cell
     * notes:  BFS should print the path in the maze, then clear all parents and
     *         visited flags, thus returning the maze to normal
     * */
    protected MazeCell multiBFS(MazeCell start, char destination){

        Queue mazeCells = new Queue();

        start.visited = true;
        mazeCells.enqueue(start);
        boolean route = false; // a boolean determing whether a path has been found

        MazeCell d = null; // the destination MazeCell

        // Proceeds with BFS using the next cell in the queue
        while (mazeCells.isEmpty() == false && route == false){
            MazeCell current = (MazeCell) mazeCells.dequeue();
            int row = current.row;
            int column = current.col;

            MazeCell[] neighbors = this.getNeighbors(row, column);

            for (int i=0; i<4; i++){
                if (neighbors[i].contents == destination){
                    neighbors[i].visited = true;
                    neighbors[i].parent = current;
                    route = true;
                    d = neighbors[i];
                }else if (neighbors[i].visited == false && neighbors[i].isWall() == false
                        && neighbors[i].isBody() == false
                        && (neighbors[i].getContents() != HEAD && neighbors[i].getContents() != ENEMY)){
                    neighbors[i].visited = true;
                    neighbors[i].parent = current;
                    mazeCells.enqueue(neighbors[i]);
                }
            } // end for loop
        } // end while loop

        // if there was no conceivable route:
        if (route == false){
            // Commented out the line below to avoid mild lag
            //System.out.println("Error: The maze was not solvable!");
            this.clearFlags();
            MazeCell[] possibleReturns = this.getNeighbors(start.row, start.col);
            for (int i=0; i<4; i++){
                if (possibleReturns[i].isOpen()){
                    return possibleReturns[i];
                }
            }
            // otherwise, returns an arbitrary MazeCell
            return possibleReturns[0];
            // if a route is known
        }else{
            // fills in the path to the destination
            MazeCell current = d.parent;
            MazeCell step = d;
            while (current.row != start.row || current.col != start.col){
                current.contents = PATH;
                step = step.parent;
                current = current.parent;
            }

            // prints the maze with the path marked
            // I noticed that the AI ran better with this commented out
            //System.out.println(this);

            // removes the path to the destination
            this.clearFlags();

            // returns the next step in the path
            return step;
        }

    } // end multiBFS

    /* method: getNeighbors
     * inputs: row - the current row, column - the current column
     * output: an array of MazeCells
     * */
    public MazeCell[] getNeighbors(int row, int column){

        MazeCell n, e, s, w;
        // North case
        if ((row - 1) < 0){
            n = maze[maze.length-1][column];
        }else{
            n = maze[row-1][column];
        }
        // East case
        if ((column + 1) >= maze[0].length){
            e = maze[row][0];
        }else{
            e = maze[row][column+1];
        }
        // South case
        if ((row + 1) >= maze.length){
            s = maze[0][column];
        }else{
            s = maze[row+1][column];
        }
        // West case
        if ((column - 1) < 0){
            w = maze[row][maze[0].length-1];
        }else{
            w = maze[row][column-1];
        }

        MazeCell[] neighbors = {n, e, w, s};
        return neighbors;

    } // end getNeighbors

    /* method: clearFlags
     * inputs: none
     * output: removes all parent flags, visited flags, and path (o) flags
     * */
    private void clearFlags(){

        MazeCell[][] maze = this.maze;

        int rows = maze.length;
        int columns = maze[0].length;

        for (int r=0; r<rows; r++){
            for (int c=0; c<columns; c++){
                if (maze[r][c].contents == PATH)
                    maze[r][c].contents = SPACE;
                maze[r][c].parent = null;
                maze[r][c].visited = false;
            }
        }

    } // end clearFlags

    // PROVIDED METHOD.  NO NEED TO ALTER.
    // this method was copied from the given solution for Maze for testing
    public static void main(String args[])
    {
        Maze M = null;

        if (args.length < 1) {
            M = new Maze();               // the no-input constructor will use the
            // array of strings above!
        } else {
            String filename = args[0];    // The filename comes from the command line
            M = new Maze(filename);       // this creates the maze
        }

        MazeCell start = M.findMazeCell('S');    // get the source
        char destination = 'D';

        MazeCell nextCellToGo = null;
        nextCellToGo = M.multiBFS(start, 'D');  // comment back in when you've implemented this

        System.out.println("starting cell is   " + start);
        System.out.println("next cell to go is " + nextCellToGo);
        System.out.println("\nM is" + M);         // M should not change!
    } // end main

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
