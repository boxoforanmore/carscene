/*
  encapsulate some desired behaviors
  of OpenGL
  (everything not handled
   elsewhere)
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
import java.io.File;

public class OpenGL{

  private static Shader v1, f1;
  private static int hp1;
  private static Program p1;

  public static void init()
  {
    String vertexShaderCode =
//----------------------------------------------------------------------
"#version 330 core\n"+
"layout (location = 0 ) in vec3 vertexPosition;\n"+
"layout (location = 1 ) in vec2 vertexTexCoord;\n"+
"out vec2 texCoord;\n"+
"uniform mat4 proj;\n"+
"uniform mat4 view;\n"+
"void main(void)\n"+
"{\n"+
"  texCoord = vertexTexCoord;\n"+
"  gl_Position = proj * view * vec4( vertexPosition, 1.0);\n"+
"}\n";
//----------------------------------------------------------------------

    System.out.println("Vertex shader:\n" + vertexShaderCode + "\n\n" );

    v1 = new Shader( "vertex", vertexShaderCode );

    String fragmentShaderCode =
//----------------------------------------------------------------------
"#version 330 core\n"+
"in vec2 texCoord;\n"+
"layout (location = 0 ) out vec4 fragColor;\n"+
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

    // enable depth buffering
    GL11.glEnable( GL11.GL_DEPTH_TEST );
    GL11.glClearDepth( 100.0f );
    GL11.glDepthFunc( GL11.GL_LESS );

  }

  // allow any code to get location for a uniform variable
  // (used by Camera)
  public static int getUniformLoc( String name ){
    return GL20.glGetUniformLocation( hp1, name );
  }

  public static void setBackColor( int r, int g, int b ){
    GL11.glClearColor( r/255f, g/255f, b/255f, 1.0f );
  }

  public static void drawBackground(){
    GL11.glClear( GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT );
  }

  public static void viewport( int px, int py, int pw, int ph ){
    GL11.glViewport( Basic.retinaFactor*px, Basic.retinaFactor*py, 
                     Basic.retinaFactor*pw, Basic.retinaFactor*ph );
  }

  // send Mat4 to uniform
  public static void sendUniformMatrix4( int loc, Mat4 matrix ){

    GL20.glUniformMatrix4fv( loc, false, matrix.toBuffer() );
        Util.error( "after sending matrix data with loc " + loc );
  }

  // load texture corresponding to pic
  public static void loadTexture( Pic pic ){

    // OpenGL promises that this will be same as like GL_TEXTURE13
    //   if textureNumber is 13
    GL13.glActiveTexture( GL13.GL_TEXTURE0 + pic.getIndex() );
           Util.error("after activate texture " + pic.getIndex() );

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

    // note:  there's some glTexStorage2D guy, we're using "older API"?
    GL30.glGenerateMipmap( GL11.GL_TEXTURE_2D );

    GL11.glTexParameteri( GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S,
                           GL11.GL_REPEAT );
           Util.error("after set wrap s");

    GL11.glTexParameteri( GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T,
                           GL11.GL_REPEAT );
           Util.error("after set wrap t");

    GL11.glTexParameteri( GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
                           GL11.GL_NEAREST );
           Util.error("after set mag filter");

    GL11.glTexParameteri( GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
                          GL11.GL_NEAREST_MIPMAP_NEAREST );
           Util.error("after set min filter");

  }

  // use the texture corresponding to pic
  public static void selectTexture( Pic pic ){

    GL13.glActiveTexture( GL13.GL_TEXTURE0 + pic.getIndex() );
           Util.error("after activate texture 0");

    GL11.glBindTexture( GL11.GL_TEXTURE_2D, pic.getTextureId() );
           Util.error("after bind texture");

    // send texture sampler as a uniform
    int loc = GL20.glGetUniformLocation( hp1, "texture" );
           Util.error("after get uniform location for texture");
//           System.out.println("got loc for texture: " + loc );
    GL20.glUniform1i( loc, pic.getIndex() );  // connect texture
           Util.error("after set value of texture");
  }

}
