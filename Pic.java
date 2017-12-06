/*  a Pic instance holds pixel data
    ready to be fed into
    OpenGL as a texture
*/

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.image.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

class Pic
{
  private String name;  // name of this image within the program
  private int index;    // position in list of all Pic's of this image
  private int textureId;

  private int numRows, numCols;  // image size
  private ByteBuffer data;  // pixels extracted into rgba

  // construct this pic from raw image file
  // with given name
  public Pic( String picName, String fileName )
  {
    name = picName;

    // scan and store the image data
    try{
      FileInputStream input = new FileInputStream( new File( fileName ) );
 
      byte upperNumCols = (byte) input.read();
      byte lowerNumCols = (byte) input.read();
      numCols = toInt( upperNumCols, lowerNumCols );
      System.out.println("got bytes for numCols " + upperNumCols + " " +
                             lowerNumCols + " giving numCols=" + numCols );

      byte upperNumRows = (byte) input.read();
      byte lowerNumRows = (byte) input.read();
      numRows = toInt( upperNumRows, lowerNumRows );
      System.out.println("got bytes for numRows " + upperNumRows + " " +
                             lowerNumRows + " giving numRows=" + numRows );

      data = ByteBuffer.allocateDirect( 4*numRows*numCols );
      data.order( ByteOrder.nativeOrder() );

      for( int k=0; k<4*numRows*numCols; k++ )
        data.put( (byte) input.read() );
      
      data.rewind();

      input.close();
    }
    catch(Exception e)
    {
      System.out.println("something went wrong reading raw image file [" +
               fileName + "]" );
      e.printStackTrace();
      System.exit(1);
    }
  }// constructor

  // construct one of various procedural textures
  public Pic( int kind )
  {
    if( kind == 1 )
    {// checkboard
       int numSq = 8, numPix = 8;
       numRows = numSq*numPix;      
       numCols = numSq*numPix;      
       data = ByteBuffer.allocateDirect( 4*numRows*numCols );
       data.order( ByteOrder.nativeOrder() );

       boolean color1 = true, rowStart = true;
       byte r1=(byte) 255, g1=(byte) 0, b1=(byte) 0, a1=(byte) 255;
       byte r2=(byte) 0, g2=(byte) 255, b2=(byte) 0, a2=(byte) 255;

       for( int r=0; r<numRows; r++ )
       {// create row r
         for( int j=0; j<numSq; j++ )
         {// create j little squares of row r
           for( int k=0; k<numPix; k++ )
           {// create numPix columns of current color
             if( color1 )
             {
               data.put( r1 ); data.put( g1 ); data.put( b1 ); data.put( a1 );
             }
             else
             {
               data.put( r2 ); data.put( g2 ); data.put( b2 ); data.put( a2 );
             }
           }
           color1 = ! color1;  // finished square on row, switch
         }
         // finished a pixel row
         if( (r+1) % numPix == 0 )
         {// finished a block
           rowStart = !rowStart;  // switch colors for next block
           color1 = rowStart;
         }
         else
         {// in same block
           color1 = rowStart;
         }
       }

    }

    else if( kind == 2 )
    {// checkboard
      numRows = 100;
      numCols = 200;
      data = ByteBuffer.allocateDirect( 4*numRows*numCols );
      data.order( ByteOrder.nativeOrder() );

      int first = 0, last = numCols-1;
      byte r1=(byte) 255, g1=(byte) 0, b1=(byte) 0, a1=(byte) 255;
      byte r2=(byte) 0, g2=(byte) 255, b2=(byte) 0, a2=(byte) 255;
      

      for( int r=0; r<numRows; r++ )
      {
        for( int c=0; c<numCols; c++ )
        {
          if( c<first || last<c )
          {// draw outside color
            data.put(r1); data.put(g1); data.put(b1); data.put(a1);
          }
          else
          {// draw inside color
            data.put(r2); data.put(g2); data.put(b2); data.put(a2);
          }
        }
        // advance both
        first++;  last--;
      }
      
    }

  }// procedural constructor

  // convert two bytes to single int,
  // treating the signed bytes as unsigned
  private int toInt( byte upper, byte lower )
  {
    int up, low;
    if( upper < 0 )
      up = upper + 256;
    else
      up = upper;
    if( lower < 0 )
      low = lower + 256;
    else
      low = lower;
    return 256*up + low;
  }

  public int getWidth()
  {
    return numCols;
  }

  public int getHeight()
  {
    return numRows;
  }

  public int getNumber(){
    return index;
  }

  public String getName(){
    return name;
  }

  public ByteBuffer getData()
  {
    return data; 
  }

  public void showData()
  {
    data.rewind();
    for( int k=0; k<4*numCols*numRows; k++ )
    {
      if( k % 4 == 0 )
        System.out.print( (k/4) + ":  " );
      System.out.print( data.get() + " " );
      if( (k+1) % 4 == 0 )
        System.out.println();
    }
  }

  // save this image sub-rectangle to given file
  public void save( String fileName, int clipX, int clipY,
                                     int clipW, int clipH )
  {
    System.out.println("save sub-image to " + fileName );

    try{
      FileOutputStream out = new FileOutputStream( new File( fileName ) );

// stub
//clipX=0;  clipY=0;  clipW=numCols;  clipH=numRows;

      // save the number of cols and rows of the sub-image
      out.write( (byte) (clipW / 256) );  out.write( (byte) (clipW % 256));
      out.write( (byte) (clipH / 256) );  out.write( (byte) (clipH % 256));

      data.rewind();

      for( int row=0; row<numRows; row++ ){
        for( int col=0; col<numCols; col++ ){
          int r = numRows-row-1;
          if( clipY<=r && r<clipY+clipH &&
              clipX<=col && col<clipX+clipW ){
            for( int m=0; m<4; m++ )
              out.write( data.get() );
          }
          else{
            // toss these 4 bytes
            for( int m=0; m<4; m++ )
              data.get();
          }
        }
      }

      out.close();
    }
    catch(Exception e){
      System.out.println("problem saving this pic");
      e.printStackTrace();
      System.exit(1);
    }
  }

  public void setIndex( int pos ){
    index = pos;
  }

  public int getIndex(){
    return index;
  }

  public void setTextureId( int x ){
    textureId = x;
  }

  public int getTextureId(){
    return textureId;
  }

  // return pixel coord x mapped into [-1,1] coords
  // used by OpenGL
  public double mapX( int x ){
    return (2.0/numCols)*x - 1;
  }

  // return pixel coord y mapped into [-1,1] coords
  // used by OpenGL
  public double mapY( int y ){
    return 1 - (2.0/numRows)*y;
  }

  public static void main(String[] args)
  {
    Pic pic = new Pic( 2 );
    System.out.println("pic has " + pic.getHeight() + " rows and " +
         pic.getWidth() + " cols");
    pic.showData();
    //pic.save( "triangle.raw" );
  }

  //*****************************************************
  // repository of all pics:

  private static ArrayList<Pic> list;  // holds all pic instances

  // change file names and add more textures here:
  public static void init(){
    list = new ArrayList<Pic>();

    // texture 0:
    addTexture( "sky-1" );

    // texture 1:
    addTexture( "dirt-1" );

    // texture 2:
    addTexture( "grass-1" );

    // texture 3:
    addTexture( "graystone-1" );

    // texture 4:
    addTexture( "graystone-2" );

    // texture 5:
    addTexture( "step-1" );

    // texture 6:
    addTexture( "step-2" );

    // texture 7:
    addTexture( "floor-1" );

    // texture 8:
    addTexture( "kitten-1" );

    // texture 9:
    addTexture( "greenfire-1" );

    // texture 10:
    addTexture( "purplefire-1" );

    // texture 11:
    addTexture( "orangefire-1" );

    // texture 12:
    addTexture( "bluefire-1" );

    // texture 13:
    addTexture( "yellowfire-1" );

    // texture 14:
    addTexture( "gold-1" );

    // texture 15:
    addTexture( "door-1" );

    // texture 16:
    addTexture( "bluecar" );

   // texture 17:
   addTexture( "bluecar-1" );

   // texture 18:
   addTexture( "bluecar-2" );

   // texture 19:
   addTexture( "bluecar-3" );

   // texture 20:
   addTexture( "bluecar-4" );

   // texture 21:
   addTexture( "skymap_photo" );

   // texture 22:
   addTexture( "mortywall-1" );

   // texture 23:
   addTexture( "ground" );

   // texture 24:
   addTexture( "buildingA" );

   // texture 25:
   addTexture( "buildingB" );

   // texture 26:
   addTexture( "buildingC" );

   // texture 27:
   addTexture( "buildingD" );

   // texture 28:
   addTexture( "buildingtop" );


  }

  private static void addTexture( String name ) {
    list.add( new Pic( name, "Pictures/" + name ) );
  }

  public static int size(){
    return list.size();
  }

  // provide access to any desired Pic
  public static Pic get( int index ){
    return list.get( index );
  }

}
