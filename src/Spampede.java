// Spampede.java
// Name: Jane Hoffswell
// Date: March 6, 2011
// Time: 2 hours 45 minutes
// Time (Extra Credit): Do you really want to know?  I don't even know...
// Comments:

// Imports
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.URL;
import java.net.URLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;

public class Spampede extends JApplet
        implements ActionListener, KeyListener, Runnable
{

    Image image;                // off-screen buffer
    Graphics g;                 // that buffer's graphical tools

    int sleepTime = 50;         // 50 milliseconds between updates
    int cycleNum;               // number of update cycles so far
    int level = 0;              // the current level of the game
    int score;                  // the current score
    int initialScore = 0;       // the score at the start of a new level
    int enemyScore;             // the enemy's score
    boolean autoLevel = true;   // automatically changes the level
    boolean dead = true;        // whether or not the pede is dead
    boolean muteSound = false;  // whether or not to mute the sound
    boolean enemyPede = false;  // whether or not there is an enemy pede

    protected SpamMaze themaze;   // the model for our Spampede game
    protected char dir;           // the direction we're moving now...
    String message;             // A String that will be printed on screen
    String messageOver = "";    // A String to be printed on the screen
    String settings;            // The string of current settings
    LinkedList<String> instructions = new LinkedList<String>();    // the instructions string

    // Current settings:
    String AISetting = "Off";
    String speedSetting = "Normal";
    String spamSetting = "Normal";
    String autoLevelSetting = "On";
    String levelSetting = "0";
    String muteSetting = "Off";

    // DEFINE CONSTANTS FOR YOUR PROGRAM HERE TO AVOID MAGIC VALUES!
    // Magic lengths
    public static int STRINGX = 10;
    public static int STRINGY = 50;
    public static final int SIZE = 10;
    // I removed the height option, to create the screen size
    // using the size of the maze
    //public static final int HEIGHT = 490; //Recommended values:
    //490 with both menu bar and buttons
    //525 with only the menu bar
    //515 with only buttons

    // Magic Colors
    public static final Color BGCOLOR = Color.white;

    // Magic Characters
    public static final char WALL = '*';
    public static final char INVI = '%';
    public static final char SPACE = ' ';
    public static final char SPAM = 'D';
    public static final char HEAD = 'S';
    public static final char BODY = 'P';

    public static final char ENEMY = 'E';
    public static final char ENEMYBODY = 'B';
    public static final char ENEMYSPAM = 'd';
    public static final char ENEMYWALL = '#';

    // Magic Cycles
    public static int ADDSPAMCYCLE = 13;
    public static int REMOVESPAMCYCLE = 39;
    public static int MAXSPAMBOARD = 7;        // will not remove if spam < max

    // Magic Keys
    public static final char REVERSE = 'r';
    public static final char NORTHDIR = 'i';
    public static final char WESTDIR = 'j';
    public static final char EASTDIR = 'l';
    public static final char SOUTHDIR = 'k';
    public static final char AUTO = 'a';
    public static final char PAUSE = 'p';

    // Magic Directions
    public static final char NORTH = 'N';
    public static final char EAST = 'E';
    public static final char SOUTH = 'S';
    public static final char WEST = 'W';

    // Other Magic Values
    public static final int LEVELDIFFERENCE = 100;  // for each level, you need to consume 500*level more spam

    // BELOW ARE DEFINITIONS OF BUTTONS AND MENU ITEMS WHICH WILL APPEAR
    private JButton newGameButton;
    private JButton pauseButton;
    private JButton startButton;
    private JButton muteButton;

    private JMenu startMenu;
    private JMenuItem newGameItem;
    private JMenuItem pauseItem;
    private JMenuItem startItem;
    private JMenuItem muteItem;

    private JMenu gameMenu;
    private JMenuItem normalItem;   // normal Spampede game
    private JMenuItem enemyItem;    // Spampede vs. an enemy

    private JMenu difficultyMenu;
    private JMenu speedItem;        // Change the speed of the game
    private JMenuItem easySpeed;
    private JMenuItem normalSpeed;
    private JMenuItem hardSpeed;
    private JMenuItem epicSpeed;
    private JMenu spamItem;         // Change the generation of spam
    private JMenuItem easySpam;
    private JMenuItem normalSpam;
    private JMenuItem hardSpam;
    private JMenuItem epicSpam;

    private JMenu levelMenu;            // Change the level (map)
    private JMenuItem autoLevelItem;
    private JMenuItem level0;
    private JMenuItem level1;
    private JMenuItem level2;
    private JMenuItem level3;
    private JMenuItem level4;
    private JMenuItem level5;
    private JMenuItem level6;
    private JMenuItem level7;
    private JMenuItem level8;
    private JMenuItem level9;

    private JMenu helpMenu;
    private JMenuItem overviewItem;
    private JMenuItem keysItem;
    private JMenuItem gamesItem;
    private JMenuItem optionsItem;
    private JMenuItem creditsItem;
    private JMenuItem exitHelpItem;

    // Here are other data members you might like to use (optional)...
    private AudioClip audioSpam;    // This is for playing a sound
    private AudioClip audioCrunch;  // This is for playing a sound
    private Image     imageSpam;    // This is for loading an image

    private Color     currentColor; // This is for the big square


    // Initialize the applet.  This is called each time the applet is started.
    public void init()
    {

        // set up the maze here
        this.themaze = new SpamMaze(enemyPede, level);
        this.dir = themaze.getDir();

        // you may want other game-based set up to be in reset(), so that it will be
        //   redone each time the spampede crashes...

        this.addKeyListener(this);                // listen for key events
        this.setLayout(new BorderLayout());       //set up layout on the form

        //beginning of button code
        //add a panel for buttons
        JPanel buttonPane = new JPanel(new FlowLayout());
        buttonPane.setBackground(BGCOLOR);
        add(buttonPane, BorderLayout.PAGE_START);

        newGameButton = new JButton("New Game");  // the text in the button
        newGameButton.addActionListener(this);    // watch for button presses
        newGameButton.addKeyListener(this);       // listen for key presses here
        buttonPane.add(newGameButton);            // add button to the panel

        pauseButton = new JButton("Pause");       // a second button
        pauseButton.addActionListener(this);
        pauseButton.addKeyListener(this);
        buttonPane.add(pauseButton);

        startButton = new JButton("Start");       // a third button
        startButton.addActionListener(this);
        startButton.addKeyListener(this);
        buttonPane.add(startButton);

        muteButton = new JButton("Mute");         // a fourth button
        muteButton.addActionListener(this);
        muteButton.addKeyListener(this);
        buttonPane.add(muteButton);
        //end of button code

        //beginning of menu bar code
        //Set up the menu bar
        JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);

        //add a menu to contain items
        // Start Menu
        startMenu = new JMenu("Start");   //The menu name
        menuBar.add(startMenu);   //Add the menu to the menu bar

        newGameItem = new JMenuItem("New Game");  //the text in the menu item
        newGameItem.addActionListener(this);  //Watch for button presses
        newGameItem.addKeyListener(this);  //Listen for key presses here
        startMenu.add(newGameItem);   //Add the item to the menu

        pauseItem = new JMenuItem("Pause");  //A second menu item
        pauseItem.addActionListener(this);
        pauseItem.addKeyListener(this);
        startMenu.add(pauseItem);

        startItem = new JMenuItem("Start");   //A third menu item
        startItem.addActionListener(this);
        startItem.addKeyListener(this);
        startMenu.add(startItem);

        muteItem = new JMenuItem("Mute");   //A fourth menu item
        muteItem.addActionListener(this);
        muteItem.addKeyListener(this);
        startMenu.add(muteItem);

        // Game Menu
        gameMenu = new JMenu("Game");
        menuBar.add(gameMenu);

        normalItem = new JMenuItem("Normal");  //First menu item
        normalItem.addActionListener(this);
        normalItem.addKeyListener(this);
        gameMenu.add(normalItem);

        enemyItem = new JMenuItem("Enemy");  //A second menu item
        enemyItem.addActionListener(this);
        enemyItem.addKeyListener(this);
        gameMenu.add(enemyItem);

        // Difficulty Menu
        difficultyMenu = new JMenu("Difficulty");
        menuBar.add(difficultyMenu);

        speedItem = new JMenu("Speed");  //First menu item
        difficultyMenu.add(speedItem);

        easySpeed = new JMenuItem("Easy");  //Sub menu item
        easySpeed.addActionListener(this);
        easySpeed.addKeyListener(this);
        speedItem.add(easySpeed);

        normalSpeed = new JMenuItem("Normal");  //Sub menu item
        normalSpeed.addActionListener(this);
        normalSpeed.addKeyListener(this);
        speedItem.add(normalSpeed);

        hardSpeed = new JMenuItem("Hard");  //Sub menu item
        hardSpeed.addActionListener(this);
        hardSpeed.addKeyListener(this);
        speedItem.add(hardSpeed);

        epicSpeed = new JMenuItem("Epic");  //Sub menu item
        epicSpeed.addActionListener(this);
        epicSpeed.addKeyListener(this);
        speedItem.add(epicSpeed);

        spamItem = new JMenu("Spam");  //Second menu item
        difficultyMenu.add(spamItem);

        easySpam = new JMenuItem("Easy");  //Sub menu item
        easySpam.addActionListener(this);
        easySpam.addKeyListener(this);
        spamItem.add(easySpam);

        normalSpam = new JMenuItem("Normal");  //Sub menu item
        normalSpam.addActionListener(this);
        normalSpam.addKeyListener(this);
        spamItem.add(normalSpam);

        hardSpam = new JMenuItem("Hard");  //Sub menu item
        hardSpam.addActionListener(this);
        hardSpam.addKeyListener(this);
        spamItem.add(hardSpam);

        epicSpam = new JMenuItem("Epic");  //Sub menu item
        epicSpam.addActionListener(this);
        epicSpam.addKeyListener(this);
        spamItem.add(epicSpam);

        // Level Menu
        levelMenu = new JMenu("Level");
        menuBar.add(levelMenu);

        autoLevelItem = new JMenuItem("Auto Level");  //First menu item
        autoLevelItem.addActionListener(this);
        autoLevelItem.addKeyListener(this);
        levelMenu.add(autoLevelItem);

        level0 = new JMenuItem("Level 0");  //Second menu item
        level0.addActionListener(this);
        level0.addKeyListener(this);
        levelMenu.add(level0);

        level1 = new JMenuItem("Level 1");  //Third menu item
        level1.addActionListener(this);
        level1.addKeyListener(this);
        levelMenu.add(level1);

        level2 = new JMenuItem("Level 2");  //Fourth menu item
        level2.addActionListener(this);
        level2.addKeyListener(this);
        levelMenu.add(level2);

        level3 = new JMenuItem("Level 3");  //Fifth menu item
        level3.addActionListener(this);
        level3.addKeyListener(this);
        levelMenu.add(level3);

        level4 = new JMenuItem("Level 4");  //Sixth menu item
        level4.addActionListener(this);
        level4.addKeyListener(this);
        levelMenu.add(level4);

        level5 = new JMenuItem("Level 5");  //Seventh menu item
        level5.addActionListener(this);
        level5.addKeyListener(this);
        levelMenu.add(level5);

        level6 = new JMenuItem("Level 6");  //Eighth menu item
        level6.addActionListener(this);
        level6.addKeyListener(this);
        levelMenu.add(level6);

        level7 = new JMenuItem("Level 7");  //Ninth menu item
        level7.addActionListener(this);
        level7.addKeyListener(this);
        levelMenu.add(level7);

        level8 = new JMenuItem("Level 8");  //Tenth menu item
        level8.addActionListener(this);
        level8.addKeyListener(this);
        levelMenu.add(level8);

        level9 = new JMenuItem("Level 9");  //Eleventh menu item
        level9.addActionListener(this);
        level9.addKeyListener(this);
        levelMenu.add(level9);

        // Help Menu
        helpMenu = new JMenu("Help");
        menuBar.add(helpMenu);

        overviewItem = new JMenuItem("Overview");  //First menu item
        overviewItem.addActionListener(this);
        overviewItem.addKeyListener(this);
        helpMenu.add(overviewItem);

        keysItem = new JMenuItem("Keys");  //A second menu item
        keysItem.addActionListener(this);
        keysItem.addKeyListener(this);
        helpMenu.add(keysItem);

        gamesItem = new JMenuItem("Games");  //A third menu item
        gamesItem.addActionListener(this);
        gamesItem.addKeyListener(this);
        helpMenu.add(gamesItem);

        optionsItem = new JMenuItem("Options");  // A fourth menu item
        optionsItem.addActionListener(this);
        optionsItem.addKeyListener(this);
        helpMenu.add(optionsItem);

        creditsItem = new JMenuItem("Credits");  //A fifth menu item
        creditsItem.addActionListener(this);
        creditsItem.addKeyListener(this);
        helpMenu.add(creditsItem);

        exitHelpItem = new JMenuItem("Exit Help");  //A sixth menu item
        exitHelpItem.addActionListener(this);
        exitHelpItem.addKeyListener(this);
        helpMenu.add(exitHelpItem);

        //end of menu bar code

        instructions.addFirst("");

        // Sets up the back (off-screen) buffer for drawing, named image
        image = createImage((themaze.getColumns())*SIZE, (themaze.getRows()+6)*SIZE);
        g = image.getGraphics();                 // g holds the drawing routines
        clear();                                 // clears the screen
        reset();                                 // Set up the game internals!

        //add a central panel which holds the buffer (the game board)
        add(new ImagePanel(image), BorderLayout.CENTER);

        // This is an example of loading in an image and a sound file.
        // You can play with this if you like, but it's not required.
        // So, you can also just comment it out (perhaps until later...)
        try
        {
            URL url = getCodeBase();
            audioSpam = getAudioClip(url, "Spam.au");
            audioCrunch = getAudioClip(url, "crunch.au");
            System.out.println("successful loading of audio/images!");
        }
        catch (Exception e)
        {
            System.out.println("problem loading audio/images!");
            audioSpam = null;
            audioCrunch = null;
            imageSpam = null;
        }

        drawEnvironment();   // re-render the environment to our offscreen buffer
        repaint();           // re-render the environment to the screen
    }

    // Each time you start a new game, you will want to reset the
    // internal representation of the game.  Here's a good place to do it!
    // Remember, the applet will be initialized just once, but you may
    // play the game many times within that run of the applet!
    // input: whether or not the pede is dead
    void reset(){
        message = "Entering level " + level;

        // resets the game
        if (this.dead == true && this.autoLevel == true){
            this.level = 0;
            this.score = 0;
            this.initialScore = 0;
            this.enemyScore = 0;
        }

        // resets the board
        if (this.dir != 'A')
            this.dir = themaze.getDir();

        this.themaze = new SpamMaze(enemyPede, this.level);
        this.dead = false;
        drawEnvironment();
        displayMessage();
        repaint();
        // if you select a new level, but autoLevel is off, then if you turn
        // autoLevel on, you will still need to consume the same difference in
        // spam as you would if autoLevel was always on (i.e. a change in level
        // is denoted by a change in score from start).
        this.initialScore = score;
        pause(); // Pauses on the new level

    } // end reset()

    // This is where you will draw your 2D array of colored squares
    // Notice that all drawing occurs in the off-screen buffer "image".
    //     and that the drawing commands themselves are held in the Graphics g
    // repaint() copies the image to the screen for fast rendering and smooth motion
    void drawEnvironment()
    {
        clear();                       // first, clear everything

        // Magic values for this method only
        int startX = 0;
        int startY = 0;
        int size = SIZE;

        for (int r=0; r<themaze.getRows(); r++){
            for (int c=0; c<themaze.getColumns(); c++){

                // sets the color for the various objects
                if (themaze.maze[r][c].getContents() == WALL){
                    g.setColor(Color.black);
                } else if (themaze.maze[r][c].getContents() == INVI){
                    g.setColor(Color.gray);
                } else if (themaze.maze[r][c].getContents() == HEAD){
                    g.setColor(Color.pink);
                } else if (themaze.maze[r][c].getContents() == BODY){
                    g.setColor(Color.red);
                } else if (themaze.maze[r][c].getContents() == SPAM){
                    g.setColor(Color.blue);
                } else if (themaze.maze[r][c].getContents() == ENEMY){
                    g.setColor(Color.yellow);
                } else if (themaze.maze[r][c].getContents() == ENEMYBODY){
                    g.setColor(Color.orange);
                } else if (themaze.maze[r][c].getContents() == SPACE){
                    g.setColor(Color.gray);
                } else {
                    g.setColor(BGCOLOR);
                }

                // draws the square for the object and prepares for new loop
                g.fillRect(startX,startY,size,size);
                startX += size;

            }
            // prepares for new loop
            startX = 0;
            startY += size;
        }

        // this draws an image which we've loaded in, if we have one to draw
        if (imageSpam != null)
            g.drawImage(imageSpam,500,100,null);

    }

    // You might use this method to move the centipede one square
    // Also, this method can check if the centipede runs
    // into a can of spam, a wall, itself, etc. and act appropriately.
    void updateCentipede(){
        char output = themaze.advancePede(dir);
        if (output == WALL){
            messageOver = "Sorry, you lose.";
            this.dead = true;
            reset();
        } else if (output == BODY){
            messageOver = "Sorry, you lose.";
            this.dead = true;
            reset();
        } else if (output == SPAM){
            if (audioCrunch != null && muteSound == false)    // Example of playing a sound
                audioCrunch.play();
            score += 10;
            // automatically updates the level map
            if (autoLevel == true){
                if ((score-initialScore) == LEVELDIFFERENCE*(level+1)){
                    if (score > enemyScore){
                        messageOver = "You win Level " + level + "!";
                    } else {
                        messageOver = "You loose Level " + level + "!";
                    }
                    if (this.level < 9){
                        this.level += 1;
                        reset(); // Draws the new map
                    }

                }
            }
        } else if (output == ENEMYWALL){
            messageOver = "You Win!";
            this.dead = true;
            reset();
        } else if (output == ENEMYSPAM){
            if (audioCrunch != null && muteSound == false)    // Example of playing a sound
                audioCrunch.play();
            enemyScore += 10;
        }

    } // end updateCentipede

    // You might use this method to add/delete spam cans periodically
    void updateSpam(){

        if (cycleNum % ADDSPAMCYCLE == 0)
            themaze.addSpam();

        if ((cycleNum % REMOVESPAMCYCLE == 0) && (themaze.getSizeSpam() > MAXSPAMBOARD))
            themaze.removeSpam();

    } // end updateSpam

    // You might use this method to draw a status String on the screen
    void displayMessage(){

        // set up
        g.setColor(Color.black);
        int newYPosition = ((themaze.getRows()+1)*SIZE)+5;
        int newXPosition = (themaze.getColumns()*SIZE)/2;

        g.drawString(message, STRINGX, newYPosition);
        g.drawString(messageOver, STRINGX, newYPosition + 15);
        if (cycleNum % REMOVESPAMCYCLE == 0){
            message = "";
            messageOver = "";
        }

        if (instructions.getFirst() != ""){
            for (int i=0; i<instructions.size(); i++)
                g.drawString(instructions.get(i), STRINGX, STRINGY + (i*15));
        }

        // settings message
        if (this.dir != 'A')
            this.AISetting = "Off";
        this.levelSetting = "" + level;
        settings = ("Settings: AI: " + this.AISetting +
                "     Speed: " + this.speedSetting +
                "     Spam: " + this.spamSetting +
                "     AutoLevel: " + this.autoLevelSetting +
                "     Level: " + this.levelSetting +
                "     Mute: " + this.muteSetting);
        g.drawString(settings, STRINGX, newYPosition +35);

        // prints scores
        g.drawString("Score: " + score, newXPosition, newYPosition);
        if (enemyPede == true)
            g.drawString("Enemy Score: " + enemyScore, newXPosition, newYPosition + 15);
    }

    // Things you want to happen at each update step
    // should be placed in this method. It's called from run().
    void cycle()
    {
        updateCentipede();  // update the Spampede deque
        updateSpam();       // update the Spam deque
        drawEnvironment();  // draw things to buffer
        displayMessage();   // display messages
        repaint();          // send buffer to the screen
        cycleNum++;         // One cycle just elapsed
    }

    // Here is how buttons and menu items work...
    public void actionPerformed(ActionEvent evt)
    {
        Object source = evt.getSource();

        // start menu
        if (source == newGameButton || source == newGameItem){
            this.dead = true;
            reset();
        }

        if (source == pauseButton || source == pauseItem)
            pause();

        if (source == startButton || source == startItem)
            go();

        if (source == muteButton || source == muteItem){
            if (muteSound == false){
                this.muteSetting = "On";
                muteSound = true;
            } else {
                this.muteSetting = "Off";
                muteSound = false;
            }
        }

        // game menu
        if (source == normalItem){
            this.enemyPede = false;
            reset();
        }

        if (source == enemyItem){
            this.enemyPede = true;
            reset();
        }

        // Game speed adjustments
        if (source == easySpeed){
            this.speedSetting = "Easy";
            this.sleepTime = 75;
        }

        if (source == normalSpeed){
            this.speedSetting = "Normal";
            this.sleepTime = 50;
        }

        if (source == hardSpeed){
            this.speedSetting = "Hard";
            this.sleepTime = 25;
        }

        if (source == epicSpeed){
            this.speedSetting = "Epic";
            this.sleepTime = 10;
        }

        // Spam speed adjustments
        if (source == easySpam){ // Adds 4 spam for every 1 removed
            this.spamSetting = "Easy";
            this.ADDSPAMCYCLE = 13;
            this.REMOVESPAMCYCLE = 52;
            this.MAXSPAMBOARD = 10;
        }

        if (source == normalSpam){ // Adds 3 spam for every 1 removed
            this.spamSetting = "Normal";
            this.ADDSPAMCYCLE = 13;
            this.REMOVESPAMCYCLE = 39;
            this.MAXSPAMBOARD = 7;
        }

        if (source == hardSpam){ // Adds 2 spam for every 1 removed
            this.spamSetting = "Hard";
            this.ADDSPAMCYCLE = 13;
            this.REMOVESPAMCYCLE = 26;
            this.MAXSPAMBOARD = 4;
        }

        if (source == epicSpam){ // Adds 1.5 spam for every 1 removed
            this.spamSetting = "Epic";
            this.ADDSPAMCYCLE = 13;
            this.REMOVESPAMCYCLE = 20;
            this.MAXSPAMBOARD = 1;
        }

        // Level adjustments
        if (source == autoLevelItem){
            if (autoLevel == true){
                this.autoLevel = false;
                message = "Automatically changing levels - OFF";
                this.autoLevelSetting = "Off";
            }else{
                this.autoLevel = true;
                this.initialScore = score;
                message = "Automatically changing levels - ON";
                this.autoLevelSetting = "On";
            }
        }

        if (source == level0){
            this.level = 0;
            reset();
        }

        if (source == level1){
            this.level = 1;
            reset();
        }

        if (source == level2){
            this.level = 2;
            reset();
        }

        if (source == level3){
            this.level = 3;
            reset();
        }

        if (source == level4){
            this.level = 4;
            reset();
        }

        if (source == level5){
            this.level = 5;
            reset();
        }

        if (source == level6){
            this.level = 6;
            reset();
        }

        if (source == level7){
            this.level = 7;
            reset();
        }

        if (source == level8){
            this.level = 8;
            reset();
        }

        if (source == level9){
            this.level = 9;
            reset();
        }

        // game menu

        if (source == overviewItem){
            this.instructions.clear();
            this.instructions.addFirst("Welcome to Spampede!");
            this.instructions.addLast("The goal is to guide the snake to eat the spam which appears on the screen (in blue).");
            this.instructions.addLast("To do so, you may use the arrow keys as follows: ");
            this.instructions.addLast("Right: L or l");
            this.instructions.addLast("Left: J or j");
            this.instructions.addLast("Up: I or i");
            this.instructions.addLast("Down: D or d");
            this.instructions.addLast("Reverse: R or r");
            this.instructions.addLast("Autonomous (AI): A or a");
            this.instructions.addLast("Pause: P or p");
            this.instructions.addLast("Games Menu: Choose to play on your own or against an enemy pede.");
            this.instructions.addLast("The game will automatically level up as you eat spam.");
            this.instructions.addLast("To toggle this feature, select Auto Level from the Level menu.");
            this.instructions.addLast("Difficulty Menu: You can change the difficulty (speed or amount of spam)");
            this.instructions.addLast("Now, enjoy the game!");
            this.instructions.addLast("Select Exit Help to remove this screen.");
            reset();
        }

        if (source == keysItem){
            this.instructions.clear();
            this.instructions.addFirst("Keys:");
            this.instructions.addLast("Right: L or l");
            this.instructions.addLast("Left: J or j");
            this.instructions.addLast("Up: I or i");
            this.instructions.addLast("Down: D or d");
            this.instructions.addLast("Reverse: R or r");
            this.instructions.addLast("Autonomous (AI): A or a");
            this.instructions.addLast("Pause: P or p");
            this.instructions.addLast("Select Exit Help to remove this screen.");
            reset();
        }

        if (source == gamesItem){
            this.instructions.clear();
            this.instructions.addLast("Games Menu: Choose to play on your own or against an enemy pede.");
            this.instructions.addLast("Normal: ");
            this.instructions.addLast("Try to collect enough spam to advance to each of the new levels.");
            this.instructions.addLast("Enemy:");
            this.instructions.addLast("Face an enemy in the process of working through the levels.");
            this.instructions.addLast("Select Exit Help to remove this screen.");
            reset();
        }

        if (source == optionsItem){
            this.instructions.clear();
            this.instructions.addFirst("To change automatically leveling up, select Auto Level from the Levels menu.");
            this.instructions.addLast("The level that you would like to try may also be selected from this menu.");
            this.instructions.addLast("Difficulty Menu:");
            this.instructions.addLast("To change the speed of the player centipede, select the speed from the Difficulty menu.");
            this.instructions.addLast("To change the rate at which spam appears and diappears, select the spam option");
            this.instructions.addLast("Select Exit Help to remove this screen.");
            reset();
        }

        if (source == creditsItem){
            this.instructions.clear();
            this.instructions.addFirst("Game Code and Extras designed by: Jane Hoffswell");
            this.instructions.addLast("Harvey Mudd College CS60");
            this.instructions.addLast("Thanks to Jeb, Mark, and Prof. Dodds for the help along the way.");
            this.instructions.addLast("Select Exit Help to remove this screen.");
            reset();
        }

        if (source == exitHelpItem){
            this.instructions.clear();
            this.instructions.addFirst("");
            reset();
        }

        this.requestFocus(); // make sure the Applet keeps kbd focus
    }

    // Here's how keyboard events are handled...
    public void keyPressed(KeyEvent evt)
    {
        switch(evt.getKeyChar()) // method returning the key pressed
        {
            case REVERSE:
                dir = themaze.reversePede();
                //drawEnvironment();
                break;
            case NORTHDIR:
            case KeyEvent.VK_I:
                dir = NORTH;
                break;
            case SOUTHDIR:
            case KeyEvent.VK_K:
                dir = SOUTH;
                break;
            case EASTDIR:
            case KeyEvent.VK_L:
                dir = EAST;
                break;
            case WESTDIR:
            case KeyEvent.VK_J:
                dir = WEST;
                break;
            case AUTO:
            case KeyEvent.VK_A:
                message = "Autonomous mode";
                this.AISetting = "On";
                dir = 'A';
                break;
            case PAUSE:
            case KeyEvent.VK_P:
                pause();
            default:
                message = "";
                messageOver = "";
                dir = this.dir;
        }
    }

    // NO NEED TO CHANGE ANYTHING AFTER THIS!

    public void keyReleased(KeyEvent evt) {}
    public void keyTyped(KeyEvent evt) {}

    /*
     * A method to clear the applet's drawing area
     */
    void clear()
    {
        g.setColor(BGCOLOR);
        g.fillRect(0, 0, getSize().width, getSize().height);
        g.setColor(Color.blue);
        g.drawRect(0, 0, getSize().width-1, HEIGHT-1);
    }

    /*
     * The following methods and data members are used
     *   to implement the Runnable interface and to
     *   support pausing and resuming the applet.
     *
     */
    Thread thread;           // the thread controlling the updates
    boolean threadSuspended; // whether or not the thread is suspended
    boolean running;         // whether or not the thread is stopped

    /*
     * This is the method that calls the "cycle()"
     * method every so often (every sleepTime milliseconds).
     */
    public void run()
    {
        while (running) {
            try {
                if (thread != null) {
                    thread.sleep(sleepTime);
                    synchronized(this) {
                        while (threadSuspended)
                            wait(); // sleeps until notify() wakes it up
                    }
                }
            }
            catch (InterruptedException e) { ; }

            cycle();  // this represents 1 update cycle for the environment
        }
        thread = null;
    }

    /* This is the method attached to the "Start" button
     */
    public synchronized void go()
    {
        if (thread == null)  {
            thread = new Thread(this);
            running = true;
            thread.start();
            threadSuspended = false;
        } else {
            threadSuspended = false;
        }
        notify(); // wakes up the call to wait(), above
    }

    /*
     * This is the method attached to the "Pause" button
     */
    void pause()
    {
        if (thread == null)
            ;
        else
            threadSuspended = true;
    }

    /*
     * This is a method called when you leave the page
     *   that contains the applet. It stops the thread altogether.
     */
    public synchronized void stop()
    {
        System.exit(0);
        running = false;
        notify();
    }

    /* This is the end of the Spampede class */
}