import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;
import hsa.*;

/** The "WizardVsWitchesBoard" class.
  * The main program of this game. Also has mouse handling  class,
  * mouse motion handling class, and the drawPanel class in it
  * This class makes the board, and everything, except for the images,
  * needed for the game.
  * @author Joel Abraham, Jason Du, Justin Kim
  * @version January 18, 2013
 */

public class WizardVsWitchesBoard extends JFrame implements ActionListener
{
    //Constants
    private final int WIZARD_Y = 256;

    // All the variables
    private JMenuItem exitOption, instructionsMenuItem, aboutMenuItem;                          //Menu options in the menu bar
    private DrawingPanel drawingArea;                                                           //The drawing class of this game
    private boolean levelFinished = true;
    private boolean gameStarted = false;
    private boolean win = false;
    private Point lastPoint;                                                                    //The last point the object was at before moving
    private int noOfMoves = 0;                                                                  //Counts the number of times the objects were dragged
    private Block selectedPiece = null;                                                         //The objects created in the Block class
    private int noOfBlocks;                                                                     //The number of objects made in the Block class
    private Image imageBackground, mainMenu, winScreen;                                         //Images for the game
    private int level = 1;                                                                      //Finds the level the user is at
    private int[] gridPoint = {0, 128, 256, 384, 512, 640, 768, 1024};                          //The grid points of the board
    private String[] witchFileName = {"i.png", "a.png", "b.png", "c.png", "d.png"};             //The objects for the game
    private int[] orientations = {0, 0, 0, 1, 1};                                               //Finds if the objects are either horizontal or vertical
    private int[] highScores;                                                                   //The high scores for all the levels
    private Block[] blocks;                                                                     //An array of the objects
    private String text;                                                                        //Reads the text from the text files
    private int originalPointX;                                                                 //Finds the x coordinate of the original position of the object clicked
    private int originalPointY;                                                                 //Finds the y coordinate of the original position of the object clicked

    /** Constructs a new WizardVsWitchesBoard object
      */
    public WizardVsWitchesBoard ()
    {
	//Title
	super ("Wizard vs Witches");

	// Does not allow the screen size to change;
	setResizable (true);

	//Sets the icon for the game
	setIconImage (Toolkit.getDefaultToolkit ().getImage ("i.png"));

	// Background image and the main menu made for the entire screen (1024 x 768) and the win screen
	imageBackground = new ImageIcon ("backgroundImage.png").getImage ();
	mainMenu = new ImageIcon ("mainmenu.png").getImage ();
	winScreen = new ImageIcon ("winScreen.png").getImage ();


	//Set size of the drawing panel to accommodate the images
	Dimension imageSize = new Dimension (1024, 768);

	//Create and add the drawing panel.
	drawingArea = new DrawingPanel (imageSize);
	Container contentPane = getContentPane ();
	contentPane.add (drawingArea, BorderLayout.CENTER);

	//Options for the menu bar

	//The exit option
	exitOption = new JMenuItem ("Exit");
	exitOption.setAccelerator (
		KeyStroke.getKeyStroke (KeyEvent.VK_X, InputEvent.CTRL_MASK));
	exitOption.addActionListener (this);

	// Set up the Help Menu
	JMenu helpMenu = new JMenu ("Help");
	instructionsMenuItem = new JMenuItem ("Instructions...", 'R');
	instructionsMenuItem.addActionListener (this);
	helpMenu.add (instructionsMenuItem);
	aboutMenuItem = new JMenuItem ("About...", 'A');
	aboutMenuItem.addActionListener (this);
	helpMenu.add (aboutMenuItem);

	// Add each MenuItem to the Game Menu (with a separator)
	JMenu gameMenu = new JMenu ("Game");
	gameMenu.addSeparator ();
	gameMenu.add (exitOption);
	JMenuBar mainMenu = new JMenuBar ();
	mainMenu.add (gameMenu);
	mainMenu.add (helpMenu);

	// Set the menu bar for this frame to mainMenu
	setJMenuBar (mainMenu);

	//Adds the high scores from the text file to the array in this game
	highScores = new int [15];
	TextInputFile fileIn = new TextInputFile ("highScores.txt");
	for (int level = 0 ; level < 15 ; level++)
	{
	    highScores [level] = fileIn.readInt ();
	}
	fileIn.close ();

    }


    /** Allows the user to click on the various options in the menu bar
      * @ param event Finds which option the user clicked
      */
    public void actionPerformed (ActionEvent event)
    {

	if (event.getSource () == exitOption)  // Selected "Exit"
	{
	    hide ();
	    System.exit (0);
	}
	else if (event.getSource () == instructionsMenuItem)  // Selected "Instructions"
	{
	    JOptionPane.showMessageDialog (this,
		    "In the 14th centuryn in a town named East Du-Kim," +
		    "\nthere was no more water in the town. People were dying " +
		    "\nof thirst but a wizard made a potion that would be able " +
		    "\nto quench people's thirst for their whole lifetime. But a group " +
		    "\nof witches stole the potion and hid it in a tall tower. The wizard has " +
		    "\nto get to the top of the tower, but the witches are in his way. So he would " +
		    "\nhave to pass 20 levels and pass the witches to retrieve the potion. " +
		    "\n\nThe main objective is to try to get the wizard to the other side." +
		    "\n\nPlayer selects the pieces by clicking on the objects using the mouse," +
		    "\nand draging them in one direction only (either horizontally or vertically)." +
		    "\n\nPlayer can try to get the least number of moves to make a new high score." +
		    " \n\nGood luck!",
		    "Instructions",
		    JOptionPane.INFORMATION_MESSAGE);
	}
	else if (event.getSource () == aboutMenuItem)  // Selected "About"
	{
	    JOptionPane.showMessageDialog (this,
		    "Images and Programming by "+
		    "\nJOEL ABRAHAM, " +
		    "JASON DU, " +
		    "JUSTIN KIM" +
		    "\n\n\u00a9 2012", "About Wizard Vs Witches",
		    JOptionPane.INFORMATION_MESSAGE);
	}
    } //actionPerformed method


    /** This keep tracks of high scores of each level
      * @param numMoves The number of moves used by the player
      */
    public void highScore (int numMoves)
    {
	if (highScores [level - 1] == 0)
	    highScores [level - 1] = numMoves + 1;
	else if (numMoves + 1 < highScores [level - 1])
	    highScores [level - 1] = numMoves + 1;
	TextOutputFile fileOut = new TextOutputFile ("highScores.txt");
	for (int level = 0 ; level < 15 ; level++)
	{
	    fileOut.println (highScores [level]);
	}
	fileOut.close ();
    } //highScore method


    /** This creats a new game when the level is started, restarted or completed
      */
    public void newGame ()
    {
	noOfMoves = 0;
	// Draw the blocks after reading in the text files
	text = "Level " + level + ".txt";
	TextInputFile fileIn = new TextInputFile (text);
	noOfBlocks = fileIn.readInt ();
	blocks = new Block [noOfBlocks];
	for (int i = 0 ; i < noOfBlocks ; i++)
	{
	    int blockType = fileIn.readInt ();
	    int x = fileIn.readInt ();
	    int y = fileIn.readInt ();
	    blocks [i] = new Block (witchFileName [blockType], x, y, orientations [blockType], this);

	}
	fileIn.close ();
	levelFinished = false;
    } //newGame method


    // Creates and draws the main program panel that the user sees
    private class DrawingPanel extends JPanel
    {
	/** Constructs a new DrawingPanel object of the specified dimension/size
	  */
	public DrawingPanel (Dimension size)
	{
	    // set size of this panel
	    setPreferredSize (size);
	    this.setFocusable (true);
	    this.requestFocusInWindow ();

	    // Font for drawing text in this panel with g.drawString()
	    setFont (new Font ("Arial", Font.BOLD, 30));

	    // Adding mouse handling so that the user can click or drag
	    this.addMouseListener (new MouseHandler ());
	    this.addMouseMotionListener (new MouseMotionHandler ());
	}

	/** Draws any graphics needed to draw in the game
	  * @ param g The console for this method so it allows any graphics to be drawn when beginning with this variable
	  */
	public void paintComponent (Graphics g)
	{
	    //Draws the levels
	    if (gameStarted)
	    {
		// Drawing the background image and the features
		if (levelFinished)
		{
		    if (win)
		    {
			//If player wins the level, diplays this win screen and the number of moves player took for the level.
			g.drawImage (winScreen, 0, 0, this);
			g.drawString ("Your Score: " + noOfMoves, 90, 320);
			g.drawString ("High Score: " + highScores [level - 2], 90, 380);
		    }
		    else
		    {
			// Display the new game.
			newGame ();
		    }

		}
		//Draws the pieces needed for the level
		if (levelFinished == false)
		{
		    // Drawing the background image
		    g.drawImage (imageBackground, 0, 0, this);
		    g.setColor (Color.BLACK);
		    g.drawString ("Level: " + level, 778, 75);
		    if (highScores [level - 1] == 0)
			g.drawString ("High Score: ", 778, 125);
		    else
			g.drawString ("High Score: " + highScores [level - 1], 778, 125);
		    g.drawString ("No of Moves: " + noOfMoves, 778, 175);
		    // Draw the pieces
		    for (int i = 0 ; i < blocks.length ; i++)
		    {
			blocks [i].draw (g);
		    }


		}
	    }

	    //Draws the main menu
	    else
	    {
		g.drawImage (mainMenu, 0, 0, this);
	    }

	} // paintComponent Method
    } //DrawingPanel class


    // Inner class to handle mouse events
    private class MouseHandler extends MouseAdapter
    {
	/** Allows user to click on the mouse on objects
	  * @ param event The console for this method to find where the user clicked
	  */
	public void mousePressed (MouseEvent event)
	{
	    Point selectedPoint = event.getPoint ();

	    //Allows user to click on any option in main menu
	    if (!gameStarted)
	    {
		//Allows user to click on the New Game Button
		if ((selectedPoint.x >= 265 && selectedPoint.x <= 744) && (selectedPoint.y >= 275 && selectedPoint.y <= 375))
		{
		    gameStarted = true;
		    setCursor (Cursor.getPredefinedCursor (Cursor.HAND_CURSOR));
		    repaint ();
		}

		//Allows the user to click on the Exit Game Button
		else if ((selectedPoint.x >= 265 && selectedPoint.x <= 744) && (selectedPoint.y >= 590 && selectedPoint.y <= 690))
		{
		    setCursor (Cursor.getPredefinedCursor (Cursor.HAND_CURSOR));
		    System.exit (0);
		}

		//Allows the user to click on the Instructions Button
		else if ((selectedPoint.x >= 265 && selectedPoint.x <= 744) && (selectedPoint.y >= 431 && selectedPoint.y <= 531))
		{
		    setCursor (Cursor.getPredefinedCursor (Cursor.HAND_CURSOR));
		    Component mainFrame = WizardVsWitchesBoard.this;
		    JOptionPane.showMessageDialog (mainFrame, "In the 14th centuryn in a town named East Du-Kim," +
			    "\nthere was no more water in the town. People were dying " +
			    "\nof thirst but a wizard made a potion that would be able " +
			    "\nto quench people's thirst for their whole lifetime. But a group " +
			    "\nof witches stole the potion and hid it in a tall tower. The wizard has " +
			    "\nto get to the top of the tower, but the witches are in his way. So he would " +
			    "\nhave to pass 20 levels and pass the witches to retrieve the potion. " +
			    "\n\nThe main objective is to try to get the wizard to the other side." +
			    "\n\nPlayer selects the pieces by clicking on the objects using the mouse," +
			    "\nand draging them in one direction only (either horizontally or vertically)." +
			    "\n\nPlayer can try to get the least number of moves to make a new high score." +
			    "\n\nGood luck!",
			    "Instructions", JOptionPane.INFORMATION_MESSAGE);

		    repaint ();
		}
	    }

	    //Allows user to click on any option in win screen
	    if (win)
	    {
		//Allows user to click on the Next Level Button
		if ((selectedPoint.x >= 354 && selectedPoint.x <= 648) && (selectedPoint.y >= 262 && selectedPoint.y <= 348))
		{
		    setCursor (Cursor.getPredefinedCursor (Cursor.HAND_CURSOR));
		    win = false;
		}

		//Allows the user to click on the Main Menu Button
		else if ((selectedPoint.x >= 320 && selectedPoint.x <= 695) && (selectedPoint.y >= 398 && selectedPoint.y <= 488))
		{
		    setCursor (Cursor.getPredefinedCursor (Cursor.HAND_CURSOR));
		    win = false;
		    gameStarted = false;
		    level = 1;
		    noOfMoves = 0;
		}

		//Allows th user to click on the Play Again Button
		else if ((selectedPoint.x >= 335 && selectedPoint.x <= 670) && (selectedPoint.y >= 542 && selectedPoint.y <= 628))
		{
		    setCursor (Cursor.getPredefinedCursor (Cursor.HAND_CURSOR));
		    win = false;
		    level--;
		    newGame ();
		}
	    }

	    //Allows user to click on any option in win screen
	    if (!levelFinished)
	    {
		if ((selectedPoint.x >= 797 && selectedPoint.x <= 1006) && (selectedPoint.y >= 429 && selectedPoint.y <= 492))
		{
		    //Allows user to click on the Main Menu Button
		    setCursor (Cursor.getPredefinedCursor (Cursor.HAND_CURSOR));
		    gameStarted = false;
		    level = 1;
		    levelFinished = true;
		}

		//Allows the user to click on the Restart Button
		else if ((selectedPoint.x >= 800 && selectedPoint.x <= 1006) && (selectedPoint.y >= 540 && selectedPoint.y <= 593))
		{
		    setCursor (Cursor.getPredefinedCursor (Cursor.HAND_CURSOR));
		    levelFinished = true;
		    repaint ();
		}

		//Allows the user to click on the Quit Button
		else if ((selectedPoint.x >= 803 && selectedPoint.x <= 1006) && (selectedPoint.y >= 651 && selectedPoint.y <= 694))
		{

		    setCursor (Cursor.getPredefinedCursor (Cursor.HAND_CURSOR));
		    System.exit (0);
		}
	    }

	    //Clicks on the objects using point selectedPiece
	    for (int i = noOfBlocks - 1 ; i >= 0 ; i--)
	    {
		if (blocks [i].contains (selectedPoint))
		{
		    selectedPiece = blocks [i];
		    originalPointX = selectedPiece.x;
		    originalPointY = selectedPiece.y;
		    lastPoint = selectedPoint;
		    setCursor (Cursor.getPredefinedCursor (Cursor.HAND_CURSOR));
		    repaint ();
		}
	    }
	} //mousePressed method

	/** Allows user to release the objects and snaps it to certain coordinates
	  * @ param event The console for this method to find where the user released the mouse click
	  */
	public void mouseReleased (MouseEvent event)
	{
	    Point releasedPoint = event.getPoint ();
	    int fixedDistance = 0;
	    int firstDistance = 0;

	    // Checks to see if the selected piece is a actual piece.
	    if (selectedPiece != null)
	    {
		// When piece is being dragged, find the closest position to move in a imaginary grid 6X6 (square size 128)
		for (int index = 0 ; index < gridPoint.length ; index++)
		{
		    if (selectedPiece.x < gridPoint [index])
		    {
			fixedDistance = gridPoint [index];
			if ((index - 1) >= 0)
			    firstDistance = gridPoint [index - 1];
			index = gridPoint.length;
		    }

		}
		// Check the closes position and number of moves increase when block/piece moves
		if (fixedDistance - selectedPiece.x < selectedPiece.x - firstDistance)
		    selectedPiece.x = fixedDistance;
		else
		    selectedPiece.x = firstDistance;
		if (originalPointX != selectedPiece.x)
		    noOfMoves++;

		// When piece is being dragged, find the closest position to move in a imaginary grid 6X6 (square size 128)
		for (int index = 0 ; index < gridPoint.length ; index++)
		{
		    if (selectedPiece.y < gridPoint [index])
		    {
			fixedDistance = gridPoint [index];
			firstDistance = gridPoint [index - 1];
			index = gridPoint.length;
		    }
		}

		// Check the closes position and number of moves increase when block/piece moves
		if (fixedDistance - selectedPiece.y < selectedPiece.y - firstDistance)
		    selectedPiece.y = fixedDistance;
		else
		    selectedPiece.y = firstDistance;
		if (originalPointY != selectedPiece.y)
		    noOfMoves++;
		setCursor (Cursor.getDefaultCursor ());
		repaint ();
	    }
	} //mouseReleased method
    } //MouseHandler class


    // Inner Class to handle mouse movements
    private class MouseMotionHandler implements MouseMotionListener
    {

	/** Allows the user to move the objects
	  * @ param event The console for this method to find where the user moved the mouse
	  */
	public void mouseMoved (MouseEvent event)
	{
	    Point currentPoint = event.getPoint ();

	    for (int i = noOfBlocks - 1 ; i >= 0 ; i--)
		if (blocks [i].contains (currentPoint))
		    setCursor (Cursor.getPredefinedCursor (Cursor.HAND_CURSOR));
	    setCursor (Cursor.getDefaultCursor ());
	} //mouseMoved mthod

	/** Allows the user to drag the objects in the game
	  * @ param event The console for this method to find where the user dragged the objects
	  */
	public void mouseDragged (MouseEvent event)
	{
	    Point currentPoint = event.getPoint ();

	    //Checks to see if the selectedPiece is an actual piece
	    if (selectedPiece != null)
	    {
		//Allows collisions with border to occur
		selectedPiece.move (lastPoint, currentPoint);
		if (selectedPiece.x < 0 || (selectedPiece.x + selectedPiece.width > 768 & selectedPiece.y != WIZARD_Y) || selectedPiece.y < 0 || selectedPiece.y + selectedPiece.height > 768)
		{
		    selectedPiece.move (currentPoint, lastPoint);
		    repaint ();
		}

		//Allows collisions with other objects to occur and drags the objects selected
		for (int i = 0 ; i < noOfBlocks ; i++)
		{
		    if (blocks [i] != selectedPiece)
		    {
			if (selectedPiece.intersects (blocks [i]))
			{
			    selectedPiece.move (currentPoint, lastPoint);
			    repaint ();
			}
		    }
		}
		lastPoint = currentPoint;
		repaint ();

		//Checks ifthe user won the level
		if (selectedPiece.x + selectedPiece.width >= 1024 && selectedPiece.y == WIZARD_Y)
		{
		    selectedPiece.x = 0;
		    levelFinished = true;
		    win = true;
		    highScore (noOfMoves);
		    level++;
		}

	    }

	} //mouseDragged method
    } //MouseMotionHandler class


    //Runs the whole program
    public static void main (String[] args)
    {
	WizardVsWitchesBoard c = new WizardVsWitchesBoard ();
	c.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
	c.pack ();
	c.setVisible (true);
    } //main method
} // WizardVsWitchesBoard class


