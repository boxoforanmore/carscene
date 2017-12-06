/*  
   Open a selected image file, convert to raw, allow
   interactive selection of a sub-image to clip to
   a new raw file
*/

//import org.lwjgl.Sys;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.util.Scanner;
import java.util.ArrayList;
import java.io.*;
import javax.swing.JFileChooser;

public class Clipper extends Basic
{
  private static Pic image;
  private static FloatBuffer clipPts = Util.createFloatBuffer( 24 );

  private static Runtime runtime = Runtime.getRuntime();
  private static long current = 0;

  public static void main(String[] args)
  {
    if( args.length != 1 ){
      System.out.println("Usage:  j Clipper <raw file name>");
      System.exit(1);
    }

    String imageFileName = args[0];

    Pic image = new Pic( imageFileName, "Pictures/" + imageFileName );
 
    Clipper app = new Clipper( "Viewing image file [" + imageFileName + "]", 
                               image.getWidth(), image.getHeight(), 30,
                              image );
    app.start();
  }// main

  // instance variables 

  private int snapshotNumber;

  // data for drawing two textured triangles:

  // positions in space of the 6 vertices
  // of the two triangles (not using triangle strip here)
  private double[] positions = { -1.0, -1.0, 0.5,
                                 1.0, -1.0, 0.5,
                                 1.0, 1.0, 0.5,
                                 -1.0, -1.0, 0.5,
                                  1.0, 1.0, 0.5,
                                  -1.0, 1.0, 0.5  };
  // texture coordinates for each vertex
  private double[] texCoords = { 0.0, 0.0,
                                 1.0, 0.0,
                                 1.0, 1.0,
                                 0.0, 0.0,
                                 1.0, 1.0,
                                 0.0, 1.0  };

  private Pic pic;

  // stuff for drawing textured triangles
  private int textureId1;
  private Shader v1, f1;
  private int hp1;
  private Program p1;
  private int vaoHandle1, vboPositionHandle1, vboTexCoordsHandle1;

  // stuff for drawing clip rectangle
  // data for drawing the clip rectangle
  private int clipX, clipY, clipW, clipH;
  private int color;  // clip rectangle drawing color
  private Shader v2, f2;
  private int hp2;
  private Program p2;
  private int vaoHandle2, vboPositionHandle2, vboTexCoordsHandle2;
  private int colorLoc;  // uniform location for uniform variable color

  // construct basic application with given title, pixel width and height
  // of drawing area, and frames per second
  public Clipper( String appTitle, int pw, int ph, int fps, Pic image ){

    super( appTitle, 0, pw, ph, (long) ((1.0/fps)*1000000000) );

    // load the texture
    pic = image;

    // initial setting for clip rectangle
    clipX = pw/4;  clipY = ph/4;
    clipW = pw/2;  clipH = ph/2;

    color = 1;
    snapshotNumber = 0;
  }

  protected void init()
  {
    //====================================================================
    // setup for drawing textured triangles:

    String vertexShaderCode1 =
"#version 330 core\n"+
"layout (location = 0) in vec3 vertexPosition;\n"+
"layout (location = 1) in vec2 vertexTexCoord;\n"+
"out vec2 texCoord;\n"+
"void main(void)\n"+
"{\n"+
"  texCoord = vertexTexCoord;\n"+
"  gl_Position = vec4(vertexPosition,1.0);\n"+
"}\n";

    System.out.println("Vertex shader for textured triangles:\n" + 
                        vertexShaderCode1 + "\n\n" );

    v1 = new Shader( "vertex", vertexShaderCode1 );

    String fragmentShaderCode1 =
"#version 330 core\n"+
"in vec2 texCoord;\n"+
"layout (location = 0) out vec4 fragColor;\n"+
"uniform sampler2D texture1;\n"+
"void main(void)\n"+
"{\n"+
"  fragColor = texture( texture1, texCoord );\n"+
"}\n";

    System.out.println("Fragment shader for textured triangles:\n" + 
                          fragmentShaderCode1 + "\n\n" );

    f1 = new Shader( "fragment", fragmentShaderCode1 );

    hp1 = GL20.glCreateProgram();
         Util.error("after create program");
         System.out.println("program handle is " + hp1 );

    GL20.glAttachShader( hp1, v1.getHandle() );
         Util.error("after attach vertex shader to program");

    GL20.glAttachShader( hp1, f1.getHandle() );
         Util.error("after attach fragment shader to program");

    GL20.glLinkProgram( hp1 );
         Util.error("after link program" );

    // build the data buffers for 6 positions and tex coords

    FloatBuffer positionData = Util.arrayToBuffer( positions );
    FloatBuffer texCoordData = Util.arrayToBuffer( texCoords );

    positionData.rewind();
    texCoordData.rewind();

    // set up vertex array object
    vaoHandle1 = GL30.glGenVertexArrays();
          Util.error("after generate single vertex array");
       System.out.println("vertex array handle: " + vaoHandle1 );
    GL30.glBindVertexArray( vaoHandle1 );
           Util.error("after bind vao");

    // set up the position VBO
    vboPositionHandle1 = GL15.glGenBuffers();
       Util.error("after generate position buffer handle");
       System.out.println("position handle: " + vboPositionHandle1 );
    GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, vboPositionHandle1 );
             Util.error("after bind positionHandle");
    GL15.glBufferData( GL15.GL_ARRAY_BUFFER,
                                     positionData, GL15.GL_STATIC_DRAW );
             Util.error("after set position data");
    GL20.glEnableVertexAttribArray(0);  // position
             Util.error("after enable attrib 0");
    GL20.glVertexAttribPointer( 0, 3, GL11.GL_FLOAT, false, 0, 0 );
             Util.error("after do position vertex attrib pointer");

    // set up the tex coords VBO
    vboTexCoordsHandle1 = GL15.glGenBuffers();
       Util.error("after generate tex coords buffer handle");
       System.out.println("tex coords handle: " + vboTexCoordsHandle1 );
    GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, vboTexCoordsHandle1 );
             Util.error("after bind tex coords Handle");
    GL15.glBufferData( GL15.GL_ARRAY_BUFFER,
                                     texCoordData, GL15.GL_STATIC_DRAW );
             Util.error("after set tex coords data");
    GL20.glEnableVertexAttribArray(1);  // tex coords
             Util.error("after enable attrib 1");
    GL20.glVertexAttribPointer( 1, 2, GL11.GL_FLOAT, false, 0, 0 );
             Util.error("after do tex coords attrib pointer");

    // set up texture

    GL13.glActiveTexture( GL13.GL_TEXTURE0 );
           Util.error("after activate texture 0");
    textureId1 = GL11.glGenTextures();
           Util.error("after generate texture id " + textureId1 );
    GL11.glBindTexture( GL11.GL_TEXTURE_2D, textureId1 );
           Util.error("after bind texture");
    GL11.glTexImage2D( GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA,
                       pic.getWidth(), pic.getHeight(), 0,   
      // with this image is messed up:  pic.getHeight(), pic.getWidth(), 0, 
                       GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, 
                       pic.getData() );
           Util.error("after set data");
    GL11.glTexParameteri( GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
                           GL11.GL_NEAREST );
           Util.error("after set mag filter");
    GL11.glTexParameteri( GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
                           GL11.GL_NEAREST );
           Util.error("after set min filter");
    
    // send texture sampler as a uniform
    int loc = GL20.glGetUniformLocation( hp1, "texture1" );
           Util.error("after get uniform location for texture1");
           System.out.println("got loc for texture1: " + loc );
    GL20.glUniform1i( loc, 0 );  // connect texture1 to texture unit 0
           Util.error("after set value of texture1");
    

    // setup for drawing lines (selection rectangle): =======================

    String vertexShaderCode2 =
"#version 330 core\n"+
"layout (location = 0) in vec3 vertexPosition;\n"+
"void main(void)\n"+
"{\n"+
"  gl_Position = vec4(vertexPosition,1.0);\n"+
"}\n";

    System.out.println("Vertex shader for drawing lines:\n" + 
                        vertexShaderCode2 + "\n\n" );

    v2 = new Shader( "vertex", vertexShaderCode2 );

    String fragmentShaderCode2 =
"#version 330 core\n"+
"in vec2 texCoord;\n"+
"layout (location = 0) out vec4 fragColor;\n"+
"uniform int color;\n"+
"void main(void)\n"+
"{\n"+
"  if( color == 0 )\n"+
"    fragColor = vec4(0.0,0.0,0.0,1.0);\n"+
"  else if( color == 1 )\n"+
"    fragColor = vec4(1.0,0.0,0.0,1.0);\n"+
"  else\n"+
"    fragColor = vec4(1.0,1.0,1.0,1.0);\n"+
"}\n";

    System.out.println("Fragment shader for drawing lines:\n" + 
                          fragmentShaderCode2 + "\n\n" );

    f2 = new Shader( "fragment", fragmentShaderCode2 );

    hp2 = GL20.glCreateProgram();
         Util.error("after create program");
         System.out.println("program handle is " + hp2 );

    GL20.glAttachShader( hp2, v2.getHandle() );
         Util.error("after attach vertex shader to program");

    GL20.glAttachShader( hp2, f2.getHandle() );
         Util.error("after attach fragment shader to program");

    GL20.glLinkProgram( hp2 );
         Util.error("after link program" );

    // set up things for vertexPosition attribute variable
    // but don't do any work for the actual data---
    // that is done in display anew each time

    // set up vertex array object
    vaoHandle2 = GL30.glGenVertexArrays();
          Util.error("after generate single vertex array");
       System.out.println("vertex array handle: " + vaoHandle2 );
    GL30.glBindVertexArray( vaoHandle2 );
           Util.error("after bind vao");

    // set up the position VBO
    vboPositionHandle2 = GL15.glGenBuffers();
       Util.error("after generate position buffer handle");
       System.out.println("position handle: " + vboPositionHandle2 );

    GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, vboPositionHandle1 );
             Util.error("after bind positionHandle");
    GL15.glBufferData( GL15.GL_ARRAY_BUFFER,
                                     positionData, GL15.GL_STATIC_DRAW );
             Util.error("after set position data");
    GL20.glEnableVertexAttribArray(0);  // position
             Util.error("after enable attrib 0");
    GL20.glVertexAttribPointer( 0, 3, GL11.GL_FLOAT, false, 0, 0 );
             Util.error("after do position vertex attrib pointer");

    // set up the tex coords VBO
    vboTexCoordsHandle1 = GL15.glGenBuffers();
       Util.error("after generate tex coords buffer handle");
       System.out.println("tex coords handle: " + vboTexCoordsHandle1 );
    GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, vboTexCoordsHandle1 );
             Util.error("after bind tex coords Handle");
    GL15.glBufferData( GL15.GL_ARRAY_BUFFER,
                                     texCoordData, GL15.GL_STATIC_DRAW );
             Util.error("after set tex coords data");
    GL20.glEnableVertexAttribArray(1);  // tex coords
             Util.error("after enable attrib 1");
    GL20.glVertexAttribPointer( 1, 2, GL11.GL_FLOAT, false, 0, 0 );
             Util.error("after do tex coords attrib pointer");


    // send color as a uniform
    colorLoc = GL20.glGetUniformLocation( hp2, "color" );
           Util.error("after get uniform location for color");
           System.out.println("got loc for color: " + colorLoc );

    //=================================================================
    // OpenGL setup 

    // set background color to white
    GL11.glClearColor( 1.0f, 1.0f, 1.0f, 1.0f );
    // enable depth testing
    GL11.glEnable( GL11.GL_DEPTH_TEST );
    GL11.glClearDepth( 1.0f );

  }

  private static int amount = 1;

  protected void processInputs()
  {
    // process all waiting input events
    while( InputInfo.size() > 0 )
    {
      InputInfo info = InputInfo.get();

      if( info.kind == 'k' && (info.action == GLFW_PRESS || 
                               info.action == GLFW_REPEAT) 
        ){
        int code = info.code;

        if( code == GLFW_KEY_C ){// change color
          color = (color+1) % 3;
        }
        else if( code == GLFW_KEY_L && clipX > amount ){
          clipX -= amount;
        }
        else if( code == GLFW_KEY_R && clipX+clipW+amount < pic.getWidth() ){
          clipX += amount;
        }
        else if( code == GLFW_KEY_U && clipY > amount ){
          clipY -= amount;
        }
        else if( code == GLFW_KEY_D && clipY+clipH+amount < pic.getHeight() ){
          clipY += amount;
        }

        else if( code == GLFW_KEY_W && clipW+amount < pic.getWidth() ){
          clipW += amount;
          if( clipX + clipW >= pic.getWidth() )
            clipX = pic.getWidth()-clipW - 1;
        }
        else if( code == GLFW_KEY_N && clipW-amount > 2 ){
          clipW -= amount;
        }
        else if( code == GLFW_KEY_T && clipH+amount < pic.getHeight() ){
          clipH += amount;
          if( clipY + clipH > pic.getHeight() )
            clipY = pic.getHeight()-clipH - 1;
        }
        else if( code == GLFW_KEY_S && clipH-amount > 2 ){
          clipH -= amount;
        }

        else if( code == GLFW_KEY_1 ){
          amount = 1;
        }
        else if( code == GLFW_KEY_2 ){
          amount = 4;
        }
        else if( code == GLFW_KEY_3 ){
          amount = 16;
        }
        else if( code == GLFW_KEY_4 ){
          amount = 64;
        }
        else if( code == GLFW_KEY_ESCAPE ){
          snapshotNumber++;
          pic.save( "Pictures/" + pic.getName()+"-"+snapshotNumber, 
                        clipX, clipY, clipW, clipH );
        }

//        System.out.println("Snapshot size is " + clipW + " wide and " +
//                             clipH + " high");

      }// input event is a key

      else if ( info.kind == 'm' )
      {// mouse moved
      //  System.out.println( info );
      }

      else if( info.kind == 'b' )
      {// button action
       //  System.out.println( info );
      }

    }// loop to process all input events

  }

  protected void update()
  {
  }

  protected void display()
  {
    GL11.glClear( GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT );

    // draw the textured triangles ======================================

    GL20.glUseProgram( hp1 );
         Util.error("after use program");

    // activate vao
    GL30.glBindVertexArray( vaoHandle1 );
           Util.error("after bind vao");

    // for this very simple application, the triangles being drawn
    // never change, so creation of the data buffer was done once and
    // for all in init() method

    // draw the buffers
    GL11.glDrawArrays( GL11.GL_TRIANGLES, 0, 3*2 );
           Util.error("after draw arrays");

    // detach the vao
    GL30.glBindVertexArray( 0 );
           Util.error("after unbind vao");

    // draw lines ======================================

    GL20.glUseProgram( hp2 );
         Util.error("after use program");

    // activate vao
    GL30.glBindVertexArray( vaoHandle2 );
           Util.error("after bind vao");

    // build the data from application information
    double[] pointsData = new double[24];  // xyz for 4 lines, 2 endpoints each

    double depth = 0.4;

    // put data for drawing 4 lines in 3D space in the buffer clipPts
    clipPts.clear();

    // first line from
    clipPts.clear();
    clipPts.put( (float) pic.mapX( clipX ) );      // lower left corner
    clipPts.put( (float) pic.mapY( clipY ) );
    clipPts.put( (float) depth );
    clipPts.put( (float) pic.mapX( clipX+clipW ) ); // to lower right corner
    clipPts.put( (float) pic.mapY( clipY ) );
    clipPts.put( (float) depth );

    // second line from 
    clipPts.put( (float) pic.mapX( clipX+clipW ) ); // lower right corner
    clipPts.put( (float) pic.mapY( clipY ) );
    clipPts.put( (float) depth );
    clipPts.put( (float) pic.mapX( clipX+clipW ) ); // to upper right corner
    clipPts.put( (float) pic.mapY( clipY+clipH ) );
    clipPts.put( (float) depth );

    // third line from
    clipPts.put( (float) pic.mapX( clipX+clipW ) ); // upper right corner
    clipPts.put( (float) pic.mapY( clipY+clipH ) );
    clipPts.put( (float) depth );
    clipPts.put( (float) pic.mapX( clipX ) ); // to upper left corner
    clipPts.put( (float) pic.mapY( clipY+clipH ) );
    clipPts.put( (float) depth );
      
    // fourth line from
    clipPts.put( (float) pic.mapX( clipX ) ); // upper left corner
    clipPts.put( (float) pic.mapY( clipY+clipH ) );
    clipPts.put( (float) depth );
    clipPts.put( (float) pic.mapX( clipX ) ); // to lower left corner
    clipPts.put( (float) pic.mapY( clipY ) );
    clipPts.put( (float) depth );

    // rewind so the data is actually available!
    clipPts.rewind();

    // actually send the data over
    GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, vboPositionHandle2 );
             Util.error("after bind positionHandle");
    GL15.glBufferData( GL15.GL_ARRAY_BUFFER,
                                     clipPts, GL15.GL_STATIC_DRAW );
             Util.error("after set points data");
    GL20.glEnableVertexAttribArray(0);  // vertexPosition
             Util.error("after enable attrib 0");
    GL20.glVertexAttribPointer( 0, 3, GL11.GL_FLOAT, false, 0, 0 );
             Util.error("after do position vertex attrib pointer");

    //------------------------------
    // send value of uniform color over to GPU
    if( color==0 )      GL20.glUniform1i( colorLoc, 0 );
    else if( color==1 ) GL20.glUniform1i( colorLoc, 1 );
    else                GL20.glUniform1i( colorLoc, 2 );
           Util.error("after set value of color");
    //------------------------------

    // draw the arrays
    GL11.glDrawArrays( GL11.GL_LINES, 0, 8 );
           Util.error("after draw arrays");

    // detach the vao
    GL30.glBindVertexArray( 0 );
           Util.error("after unbind vao");

    
    long temp = runtime.freeMemory();
    if( temp != current )
      System.out.println( getStepNumber() + ", change in free memory: " + (temp-current) );

    current = runtime.freeMemory();
  }

}// Clipper
