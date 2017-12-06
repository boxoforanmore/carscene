/*  
   Draw a single, hard-coded square (two triangles)  with texture applied
*/

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

public class TryTextures extends Basic
{
  // test this basic application 
  public static void main(String[] args)
  {
    TryTextures app = new TryTextures( "Two Textured Triangles", 500,500, 30,
                                       args[0] );
    app.start();
  }// main

  // instance variables 

  // positions in space of the 6 vertices
  // of the two triangles

  // entire window
  private double[] positions = { -1.0, -1.0, 0.3,
                                 1.0, -1.0, 0.3,
                                 0.0, 1.0, 0.3,

                                 1.0, 0.5, 0.3,
                                 1.0, 1.0, 0.3,
                                 0.5, 1.0, 0.3,
                               };

  // texture coordinates for each vertex

  private double[] texCoords =
   {
     0.0, 0.0,
     1.0, 0.0,
     0.5, 1.0,

     1.0, 0.75,
     1.0, 1.0,
     0.75, 1.0
   };

  private Pic pic;

  private Shader v1, f1;
  private int hp1;
  private Program p1;

  private int vaoHandle, vboPositionHandle, vboTexCoordsHandle;

  // construct basic application with given title, pixel width and height
  // of drawing area, and frames per second
  public TryTextures( String appTitle, int pw, int ph, int fps, String fileName )
  {
    super( appTitle, pw, ph, (long) ((1.0/fps)*1000000000) );

    // load the texture
    pic = new Pic( "image", fileName );

  }

  protected void init()
  {
    String vertexShaderCode =
"#version 330 core\n"+
"layout (location = 0) in vec3 vertexPosition;\n"+
"layout (location = 1) in vec2 vertexTexCoord;\n"+
"out vec2 texCoord;\n"+
"void main(void)\n"+
"{\n"+
"  texCoord = vertexTexCoord;\n"+
"  gl_Position = vec4(vertexPosition,1.0);\n"+
"}\n";

    System.out.println("Vertex shader:\n" + vertexShaderCode + "\n\n" );

    v1 = new Shader( "vertex", vertexShaderCode );

    String fragmentShaderCode =
"#version 330 core\n"+
"in vec2 texCoord;\n"+
"layout (location = 0) out vec4 fragColor;\n"+
"uniform sampler2D texture1;\n"+
"void main(void)\n"+
"{\n"+
"  fragColor = texture( texture1, texCoord );\n"+
"}\n";

    System.out.println("Fragment shader:\n" + fragmentShaderCode + "\n\n" );

    f1 = new Shader( "fragment", fragmentShaderCode );

    hp1 = GL20.glCreateProgram();
         Util.error("after create program");
         System.out.println("program handle is " + hp1 );

    GL20.glAttachShader( hp1, v1.getHandle() );
         Util.error("after attach vertex shader to program");

    GL20.glAttachShader( hp1, f1.getHandle() );
         Util.error("after attach fragment shader to program");

    GL20.glLinkProgram( hp1 );
         Util.error("after link program" );

    GL20.glUseProgram( hp1 );
         Util.error("after use program");

    // set background color to white
    GL11.glClearColor( 1.0f, 1.0f, 1.0f, 1.0f );

    // enable depth testing
    GL11.glEnable( GL11.GL_DEPTH_TEST );
    GL11.glClearDepth( 1.0f );

    // build the data buffers for 4 positions and tex coords

    FloatBuffer positionData = Util.arrayToBuffer( positions );
    FloatBuffer texCoordData = Util.arrayToBuffer( texCoords );

    positionData.rewind();
    texCoordData.rewind();

    // debug--show the buffer contents
    positionData.rewind();
    texCoordData.rewind();
    Util.showBuffer("position buffer:", positionData );
    Util.showBuffer("tex coords buffer:", texCoordData );

    // set up vertex array object
    vaoHandle = GL30.glGenVertexArrays();
          Util.error("after generate single vertex array");
       System.out.println("vertex array handle: " + vaoHandle );
    GL30.glBindVertexArray( vaoHandle );
           Util.error("after bind vao");

    // set up the position VBO
    vboPositionHandle = GL15.glGenBuffers();
       Util.error("after generate position buffer handle");
       System.out.println("position handle: " + vboPositionHandle );
    GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, vboPositionHandle );
             Util.error("after bind positionHandle");
    GL15.glBufferData( GL15.GL_ARRAY_BUFFER,
                                     positionData, GL15.GL_STATIC_DRAW );
             Util.error("after set position data");
    GL20.glEnableVertexAttribArray(0);  // position
             Util.error("after enable attrib 0");
    GL20.glVertexAttribPointer( 0, 3, GL11.GL_FLOAT, false, 0, 0 );
             Util.error("after do position vertex attrib pointer");

    // set up the tex coords VBO
    vboTexCoordsHandle = GL15.glGenBuffers();
       Util.error("after generate tex coords buffer handle");
       System.out.println("tex coords handle: " + vboTexCoordsHandle );
    GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, vboTexCoordsHandle );
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
System.out.println( GL13.GL_TEXTURE0 );
    pic.setTextureId( GL11.glGenTextures() );
           Util.error("after generate texture id " + pic.getTextureId() );
    GL11.glBindTexture( GL11.GL_TEXTURE_2D, pic.getTextureId() );
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
    
  }

  protected void processInputs()
  {
    // process all waiting input events
    while( InputInfo.size() > 0 )
    {
      InputInfo info = InputInfo.get();

      if( info.kind == 'k' && (info.action == GLFW_PRESS || info.action == GLFW_REPEAT) )
      {
        int code = info.code;

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

    // activate vao
    GL30.glBindVertexArray( vaoHandle );
           Util.error("after bind vao");

    // draw the buffers
    GL11.glDrawArrays( GL11.GL_TRIANGLES, 0, 3*2 );
           Util.error("after draw arrays");

    // detach the vao
    GL30.glBindVertexArray( 0 );
           Util.error("after unbind vao");

  }

}// TryTextures
