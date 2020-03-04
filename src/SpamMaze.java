// SpamMaze class
// Name: Jane Hoffswell
// Date: March 6, 2011
// Time: 1.5 hours
// Comments:

/*
 * class SpamMaze
 *
 * represents and handles the model for the Spampede applet
 */

import java.lang.Math;
import java.util.LinkedList;
import java.util.Random;

class SpamMaze extends Maze
{
    // The data members representing the spam and the centipede
    private LinkedList<MazeCell> spamCells;
    private LinkedList<MazeCell> pedeCells;
    private LinkedList<MazeCell> enemyCells;

    public char dir;
    public char dirEnemy;

    boolean enemyPede = false;

    // Definition of 'Magic Numbers'
    public static final char NORTH = 'N';
    public static final char EAST = 'E';
    public static final char SOUTH = 'S';
    public static final char WEST = 'W';
    public static final char AUTO = 'A';
    public static final char INVI = '%';
    public static final char ENEMYSPAM = 'd';
    public static final char ENEMYWALL = '#';


    // Magic Starting Index
    public static final int PEDEROWSTART = 1;
    public static final int PEDECOLHEAD = 2;
    public static final int PEDECOLBODY = 1;
    public static final int ENEMYROWSTART = 1;
    public static final int ENEMYCOLHEAD = 47;
    public static final int ENEMYCOLBODY = 48;

    /* method: SpamMaze constructor
     * inputs: enemy - true if there is an enemy pede
     * output: a constructor
     * */
    public SpamMaze(boolean enemy){
        super();
        this.pedeCells = new LinkedList<MazeCell>();
        this.pedeCells.addFirst(maze[PEDEROWSTART][PEDECOLHEAD]);
        this.pedeCells.addLast(maze[PEDEROWSTART][PEDECOLBODY]);
        if (enemy == true){
            this.enemyCells = new LinkedList<MazeCell>();
            this.enemyCells.addFirst(maze[ENEMYROWSTART][ENEMYCOLHEAD]);
            this.enemyCells.addLast(maze[ENEMYROWSTART][ENEMYCOLBODY]);
            maze[ENEMYROWSTART][ENEMYCOLHEAD].setContents(BODY);
            maze[ENEMYROWSTART][ENEMYCOLBODY].setContents(ENEMY);
            this.enemyPede = true;
        }
        this.spamCells = new LinkedList<MazeCell>();
        this.dir = EAST;
        this.dirEnemy = WEST;

    } // end SpamMaze

    /* method: SpamMaze constructor
     * inputs: enemy - true if there is an enemy pede, level - an integer input
     * output: a constructor
     * */
    public SpamMaze(boolean enemy, int level){
        super(level);
        this.pedeCells = new LinkedList<MazeCell>();
        this.pedeCells.addFirst(maze[PEDEROWSTART][PEDECOLHEAD]);
        this.pedeCells.addLast(maze[PEDEROWSTART][PEDECOLBODY]);
        if (enemy == true){
            this.enemyCells = new LinkedList<MazeCell>();
            this.enemyCells.addFirst(maze[ENEMYROWSTART][ENEMYCOLHEAD]);
            this.enemyCells.addLast(maze[ENEMYROWSTART][ENEMYCOLBODY]);
            maze[ENEMYROWSTART][ENEMYCOLHEAD].setContents(ENEMYBODY);
            maze[ENEMYROWSTART][ENEMYCOLBODY].setContents(ENEMY);
            this.enemyPede = true;
        }
        this.spamCells = new LinkedList<MazeCell>();
        this.dir = EAST;

    } // end SpamMaze

    // getters and setters for the SpamMaze class
    public int getRows(){ return this.maze.length; } // end getRows
    public int getColumns(){ return this.maze[0].length; } // end getColumns
    public char getContents(int r, int c){ return this.maze[r][c].getContents(); } // end getContents
    public void setContents(int r, int c, char newcontents){ this.maze[r][c].setContents(newcontents); } // end setContents

    public int getHeadCol(){return this.pedeCells.getFirst().getCol();}
    public int getHeadRow(){return this.pedeCells.getFirst().getRow();}


    // additional getters
    public int getSizeSpam(){ return this.spamCells.size(); } // end getSizeSpam
    public char getDir(){ return this.dir; } // end getDirection

    /* method: addSpam
     * inputs: none
     * output: adds one can of spam to the environment
     * */
    public void addSpam(){

        // generates random integers
        Random generator = new Random();
        int row = generator.nextInt(this.getRows());
        int col = generator.nextInt(this.getColumns());

        // checks to see if the new location is teh wall or pede
        while (maze[row][col].isWall() == true || maze[row][col].isBody() == true){
            row = generator.nextInt(this.getRows());
            col = generator.nextInt(this.getColumns());
        }

        this.setContents(row, col, SPAM);
        this.spamCells.addFirst(maze[row][col]);

    } // end addSpam

    /* method: removeSpam
     * inputs: none
     * output: removes one can of spam from the environment
     * */
    public void removeSpam(){

        MazeCell spam = this.spamCells.getLast();
        this.spamCells.removeLast();
        this.setContents(spam.getRow(), spam.getCol(), SPACE);

    } // end removeSpam

    /* method: advancePede
     * inputs: direction - NESW is the direction moved by the pede's head
     * output: advances the pede one step in the direction given
     * */
    public char advancePede(char direction){

        MazeCell newCell = null;
        MazeCell head = this.pedeCells.getFirst();
        this.dir = direction;

        // determines the next MazeCell to move to
        // note: this also takes into consideration wrap around capabilities
        if (direction == NORTH){
            if ((head.getRow()-1) < 0){
                newCell = maze[maze.length-1][head.getCol()];
            }else{
                newCell = maze[head.getRow()-1][head.getCol()];
            }
        } else if (direction == EAST){
            if ((head.getCol()+1) >= maze[0].length){
                newCell = maze[head.getRow()][0];
            }else{
                newCell = maze[head.getRow()][head.getCol()+1];
            }
        } else if (direction == SOUTH) {
            if ((head.getRow()+1) >= maze.length){
                newCell = maze[0][head.getCol()];
            }else{
                newCell = maze[head.getRow()+1][head.getCol()];
            }
        } else if (direction == WEST) {
            if ((head.getCol()-1) < 0){
                newCell = maze[head.getRow()][maze[0].length-1];
            }else{
                newCell = maze[head.getRow()][head.getCol()-1];
            }
        } if (direction == AUTO) {
            newCell = this.multiBFS(head, SPAM);
        }

        // moves the pede by one cell
        char pedeReturn = ' ';
        if (newCell.getContents() == SPAM){
            // changes contents
            newCell.setContents(HEAD);
            pedeCells.getFirst().setContents(BODY);
            // changes list
            this.pedeCells.addFirst(newCell);
            this.spamCells.remove(newCell);
            pedeReturn = SPAM;
        } else if (newCell.getContents() == WALL || newCell.getContents() == INVI){
            pedeReturn = WALL;
        } else if (newCell.getContents() == BODY
                || newCell.getContents() == ENEMY || newCell.getContents() == ENEMYBODY){
            pedeReturn = BODY;
        }else {
            // changes contents
            newCell.setContents(HEAD);
            pedeCells.getFirst().setContents(BODY);
            pedeCells.getLast().setContents(SPACE);
            // changes list
            this.pedeCells.addFirst(newCell);
            this.pedeCells.removeLast();
            pedeReturn = SPACE;
        }

        // handles the enemy pede
        char enemyReturn = ' ';
        if (enemyPede == true){
            MazeCell newEnemyCell = this.multiBFS(enemyCells.getFirst(), SPAM);

            if (newEnemyCell.getContents() == SPAM){
                // changes contents
                int row = newEnemyCell.getRow();
                int col = newEnemyCell.getCol();
                newEnemyCell.setContents(ENEMY);
                enemyCells.getFirst().setContents(ENEMYBODY);
                // changes list
                this.enemyCells.addFirst(newEnemyCell);
                this.spamCells.remove(newEnemyCell);
                enemyReturn = SPAM;
            } else if (newEnemyCell.getContents() == WALL || newCell.getContents() == INVI){
                enemyReturn = WALL;
            } else if (newEnemyCell.getContents() == BODY || newEnemyCell.getContents() ==  HEAD
                    || newEnemyCell.getContents() == ENEMYBODY){
                enemyReturn = BODY;
            }else {
                // changes contents
                newEnemyCell.setContents(ENEMY);
                enemyCells.getFirst().setContents(ENEMYBODY);
                enemyCells.getLast().setContents(SPACE);
                // changes list
                this.enemyCells.addFirst(newEnemyCell);
                this.enemyCells.removeLast();
                enemyReturn = SPACE;
            }
        } // end enemy pede case

        if (pedeReturn == enemyReturn){
            return pedeReturn;
        } else if (pedeReturn == BODY){
            return BODY;
        } else if (pedeReturn == WALL){
            return WALL;
        } else if (enemyReturn == WALL || enemyReturn == BODY) {
            return ENEMYWALL;
        } else if (pedeReturn == SPAM){
            return SPAM;
        } else if (enemyReturn == SPAM){
            return ENEMYSPAM;
        } else {
            return SPACE;
        }

    } // end advancePede

    /* method: reversePede()
     * inputs: none
     * output: changes the direction of the pede to be the opposite direction
     *         of the last two cells of the pede's body
     * */
    public char reversePede(){

        LinkedList<MazeCell> newPede = new LinkedList<MazeCell>();
        while (pedeCells.size() > 0){
            newPede.addFirst(this.pedeCells.getFirst());
            pedeCells.removeFirst();
        }
        this.pedeCells = newPede;
        pedeCells.getFirst().setContents(HEAD);
        pedeCells.getLast().setContents(BODY);
        char direction = getDirection();

        return direction;

    } // end reversePede

    /* method: getDirection
     * inputs: none
     * output: the direction the pede is moving
     * */
    public char getDirection(){

        MazeCell first = this.pedeCells.getFirst();
        MazeCell second = this.pedeCells.get(1);
        int row = first.getRow() - second.getRow();
        int col = first.getCol() - second.getCol();

        if (row == -1 && col == 0){
            this.dir = NORTH;
        } else if (row == 0 && col == 1){
            this.dir = EAST;
        } else if (row == 1 && col == 0){
            this.dir = SOUTH;
        } else if (row == 0 && col == -1){
            this.dir = WEST;
        }

        return this.dir;

    }

    /* method: main
     * inputs: the usual
     * output: method for testing
     * */
    public static void main(String[] args)
    {
        SpamMaze SM = new SpamMaze(false);

        System.out.println("SM is\n" + SM);
        MazeCell nextSpot = SM.multiBFS(SM.pedeCells.getFirst(), SPAM);
        System.out.println("nextSpot is\n" + nextSpot);
        System.out.println("SM is\n" + SM);

        SM.advancePede(SM.dir); // EAST
        System.out.println("SM is\n" + SM);
        System.out.println("pedeCells is " + SM.pedeCells);

        SM.advancePede(SM.dir); // EAST
        System.out.println("SM is\n" + SM);
        System.out.println("pedeCells is " + SM.pedeCells);

        SM.advancePede(SOUTH); // SOUTH
        System.out.println("SM is\n" + SM);
        System.out.println("pedeCells is " + SM.pedeCells);

        SM.advancePede(SM.reversePede()); // WEST
        System.out.println("SM is\n" + SM);
        System.out.println("pedeCells is " + SM.pedeCells);

        SM.advancePede(SM.dir); // WEST
        System.out.println("SM is\n" + SM);
        System.out.println("pedeCells is " + SM.pedeCells);
    } // end main

} // end SpamMaze