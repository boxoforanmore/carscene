/*  a Soups instance holds
    a collection of TriSoup instances,
    one for each texture
*/

import java.util.ArrayList;

public class Soups{

  private TriSoup[] soups;

  // accumulate triangles here
  private ArrayList<Triangle> triangles;

  // build a "soups" object with
  // given number of separate images,
  // create empty soup's for each, 
  // and create empty list of triangles
  public Soups( int numTextures ){
    soups = new TriSoup[ numTextures ];
    for( int k=0; k<soups.length; k++ ){
      soups[k] = new TriSoup();
    }

    triangles = new ArrayList<Triangle>();
  }

  // add this triangle to soups
  public void addTri( Triangle tri ) {
    triangles.add( tri );
System.out.println("after adding " + tri + " this soups has " +
     triangles.size() + " tris" );
  }

  // go through list of triangles and add
  // them to the soups
  public void addTris( ArrayList<Triangle> list ) {
    for( int k=0; k<list.size(); k++ ) {
      triangles.add( list.get(k) );
    }
  }

  // sort triangles into individual soups
  // for each image
  public void sortByTexture(){
    for( int k=0; k<triangles.size(); k++ ){
      Triangle tri = triangles.get(k);
      soups[ tri.getTexture() ].add( tri );
    }
  }

  // draw all the TriSoup's
  public void draw(){

    System.out.println("draw the soups " + this );
    // actually draw each soup
    for( int k=0; k<soups.length; k++ ){
      OpenGL.selectTexture( Pic.get(k) );
      System.out.println("soup for texture # " + k +
          " has " + soups[k].size() + " triangles" );
      soups[ k ].draw();
    }

  }
  
  // release all the TriSoup's in this soups
  public void cleanup(){
    for( int k=0; k<soups.length; k++ ){
      soups[k].cleanup();
    }
  }

}
