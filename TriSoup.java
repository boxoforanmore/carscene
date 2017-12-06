/*
   hold a collection of triangles,
   with OpenGL stuff needed to draw them

   draw  draws all the triangles, checking whether
         they need to be shipped to the GPU, and if so,
         it ships them first
*/

import java.util.ArrayList;
import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class TriSoup{

  private ArrayList<Triangle> tris;  // holds the triangles
  private boolean loaded;  // remember whether the triangles have been shipped
                           // to GPU

  private int vaoHandle;  // handle to loaded vertex array object
  private int posHandle;  // handle for positions VBO
  private int texHandle;  // handle for texture coords VBO

  public TriSoup(){
    tris = new ArrayList<Triangle>();
    loaded = false;
  }

  // add given triangle to the list
  // (and note now need loading)
  public void add( Triangle tri ){
    tris.add( tri );
    noteChange();
  }
  
  // send vertex data for
  // the triangles to GPU
  private void ship(){

    // System.out.println("start ship for " + this );
    loaded = true;

    // set up vertex array object 
    // ---------------------------------------------------------------
    vaoHandle = GL30.glGenVertexArrays();
           Util.error("after generate single vertex array");
    GL30.glBindVertexArray( vaoHandle );
           Util.error("after bind the vao");
    //           System.out.println("vao is " + vaoHandle );

    // set everything up for position VBO:
    // ---------------------------------------------------------------

    // scan tris and fill app buffer:
    for( int k=0; k<tris.size(); k++ ){
      tris.get(k).positionToBuffer();
    }
    Util.bufferFlip();

    // create vertex buffer object and its handle
    posHandle = GL15.glGenBuffers();
    //    System.out.println("have position handle " + posHandle );

    // now create GPU buffer and connect to app buffer
    GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, posHandle );
             Util.error("after bind posHandle");
    Util.sendBufferToGPU( GL15.GL_STATIC_DRAW );

    // enable the vertex array attribute
    GL20.glEnableVertexAttribArray(0);  // position
             Util.error("after enable attrib 0");

    // map index 0 to the position buffer
    GL20.glVertexAttribPointer( 0, 3, GL11.GL_FLOAT, false, 0, 0 );
             Util.error("after do position vertex attrib pointer");

    // set everything up for texCoords VBO:
    // ---------------------------------------------------------------

    // scan tris and fill app buffer:
    for( int k=0; k<tris.size(); k++ ){
      tris.get(k).texCoordsToBuffer();
    }
    Util.bufferFlip();

    // create vertex buffer object and its handle
    texHandle = GL15.glGenBuffers();
    //    System.out.println("have texCoords handle " + texHandle );

    // now create GPU buffer and connect to app buffer 
    GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, texHandle );
             Util.error("after bind posHandle");
    Util.sendBufferToGPU( GL15.GL_STATIC_DRAW );

    // enable the vertex array attribute
    GL20.glEnableVertexAttribArray(1);  // texCoords
             Util.error("after enable attrib 1");

    // map index 1 to the texCoords buffer
    GL20.glVertexAttribPointer( 1, 2, GL11.GL_FLOAT, false, 0, 0 );
             Util.error("after do texCoords vertex attrib pointer");

    // System.out.println("finished ship for " + this );

  }// ship

  public void draw(){

    if( !loaded )
      ship();

    // activate vao
    GL30.glBindVertexArray( vaoHandle );
           Util.error("after bind vao");

    // draw the triangles
    GL11.glDrawArrays( GL11.GL_TRIANGLES, 0, 3 * tris.size() );
           Util.error("after draw arrays");

  }// draw

  // a change has happened, so if is first such,
  // clean up the obsolete buffer stuff, and
  // either way set  loaded
  public void noteChange(){
    if( loaded ){
      cleanup();
    }
 
    loaded = false;
  }// noteChange

  // clean up buffer stuff due to change in
  // tris forcing another load
  public void cleanup(){

    GL30.glDeleteVertexArrays( vaoHandle );
    GL15.glDeleteBuffers( posHandle );
    GL15.glDeleteBuffers( texHandle );
  }// cleanup

  public int size(){
    return tris.size();
  }

}
