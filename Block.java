/** The "Card" class.
  * Keeps track of a standard playing card object
  * This class is the bare minimum for the MoveACard Demo
  * @author G. Ridout Copyright December 2002
  * @version April 2002 (Updated December 2002)
  * (Images updated to png's December 2011)
  */

import hsa.Console;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

// To reduce the amount of code needed, our Card object will inherit data
// and methods from the Rectangle class since a Card is just a rectangle
// with an image. In particular, we will be using the following data:
//      x, y , height and width
// And we will be using the following methods;
//      contains() and translate()

public class Block extends Rectangle
{
    // Card variables to keep track of a cards data (characteristics)
    // We can also use x, y, height and width (inherited from Rectangle)
    private int orientation;
    private Image image;

    // Constructor that creates a new card object
    public Block (String imageFileName, int x, int y,int orientation,Component parentFrame)
    {
	// Set up the underlying rectangle for this Card object
	// Initial size will be zero
	// We will set its size later once we know the size of the image
	super (x, y, 0, 0);
	
	this.orientation = orientation;
	image = new ImageIcon (imageFileName).getImage ();

	// Set the size of the card based on the image size
	setSize (image.getWidth (parentFrame), image.getHeight (parentFrame));
    }


    
    // Change the position of the card by the difference between
    // the two given points
    public void move (Point initialPos, Point finalPos)
    {
	if (orientation == 0)
	    translate (finalPos.x - initialPos.x, 0);
	else
	    translate (0, finalPos.y - initialPos.y);
    }


    /** Draws a card in a Graphics context
      * @param g Graphics to draw the card in
       */
    public void draw (Graphics g)
    {
	    g.drawImage (image, x, y, null);
	
    }


}


