/*  take a JPEG image file and extract data, 
    saving in "raw" file format ready for Pic
    to read
*/

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.image.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

class Convert
{
  // data ready for Pic
  private static int numRows, numCols;  // image size
  private static ByteBuffer data;  // pixels extracted into rgba

  public static void main(String[] args)
  {
    if( args.length != 1 )
    {
      System.out.println("Usage:  jcr Convert <image file name without .jpg>");
      System.exit(0);
    }

    // some arcane Java stuff that seems to work
    // ***********************************************************
    Image image = null;
    Frame observer = new Frame();
     try{
       MediaTracker tracker = new MediaTracker(observer);
       image = Toolkit.getDefaultToolkit().getImage( 
                                             "Pictures/" + args[0] + ".jpg" );
       tracker.addImage( image, 1 );  
       try{ tracker.waitForID( 1 ); }
       catch(InterruptedException e){}
     }
     catch(Exception e){
       System.out.println("some problem getting the image");
       System.exit(0);
     }

     // get the actual size of the image:
     numCols = image.getWidth(observer);
     numRows = image.getHeight(observer);

     System.out.println("found actual size of " + numCols + " by " + numRows );

     // obtain the image data 
     int[] pixels = new int[ numCols * numRows ];
     try{
       PixelGrabber pg = new PixelGrabber( image, 0, 0, numCols, 
          numRows, pixels, 0, numCols );
       pg.grabPixels();
       if((pg.status() & ImageObserver.ABORT) != 0)
       {
         System.out.println("error while grabbing pixels for " + args[0] );
         System.exit(0);
       }
     }
     catch(InterruptedException e)
     { System.out.println("pixel grabbing was interrupted"); 
       System.exit(0); }
    // ***********************************************************
    // end of arcane stuff, now should make sense
    // Have numCols, numRows, pixels all ordinary int values
     
     // pull each int apart to obtain the individual pieces
     // of data and store conveniently

     int alpha, red, green, blue, pixel;

     // extract pixel data to form ready for texture
     //   Note:  decided to work with ByteBuffer directly here
     //          rather than calling Util methods
     data = ByteBuffer.allocate( 4*numCols*numRows );
     data.order(ByteOrder.nativeOrder());

     for( int r=0; r<numRows; r++ )
       for( int c=0; c<numCols; c++ ){
       int k = (numRows-1 - r )*numCols + c;  // turn vertically
       pixel = pixels[ k ];

       // apparently jpg puts bytes in order:   alpha red green blue
       data.put( (byte) ( (pixel >> 16) & 0xff ) );   // red in JPG format
       data.put( (byte) ( (pixel >>  8) & 0xff ) );   // green
       data.put( (byte) ( (pixel      ) & 0xff ) );   // blue
       data.put( (byte) ( (pixel >> 24) & 0xff ) );   // alpha
     }

     // now save to args[1]

     try{
       FileOutputStream out = new FileOutputStream( 
                                    new File( "Pictures/" + args[0] ) );

       // write width using 2 bytes in base 256
       byte upperNumCols = (byte) (numCols / 256 );
       byte lowerNumCols = (byte) (numCols % 256 );
       System.out.println("representing width=" + numCols +
          " using " + upperNumCols + " " + lowerNumCols );
       out.write( upperNumCols );  out.write( lowerNumCols );

       // write height using 2 bytes in base 256
       byte upperNumRows = (byte) (numRows / 256 );
       byte lowerNumRows = (byte) (numRows % 256 );
       System.out.println("representing height=" + numRows +
          " using " + upperNumRows + " " + lowerNumRows );
       out.write( upperNumRows );  out.write( lowerNumRows );

       data.rewind();

       for( int k=0; k<4*numCols*numRows; k++ )
          out.write( data.get() );

       out.close();
     }
     catch(Exception e1)
     {
       System.out.println("problem creating raw byte file");
       System.exit(1);
     }

  }// main

}
