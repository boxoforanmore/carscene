/*
   a fire pyramid has
   4 fiery triangles,
   can be resized,
   rotated, moved
*/
public class FirePyramid {

  private Triple[] model;  // model vertices of the pyramid
  private Mat4 scale,rotate,translate;  // transform it cumulatively

  // construct a model pyramid
  public FirePyramid() {
    model = new Triple[ 5 ];
    model[0] = new Triple( -1, -1, 0 );
    model[1] = new Triple( 1, -1, 0 );
    model[2] = new Triple( 1, 1, 0 );
    model[3] = new Triple( -1, 1, 0 );
    model[4] = new Triple( 0, 0, 2 );
    scale = Mat4.identity();
    rotate = Mat4.identity();
    translate = Mat4.identity();
  }

  public void scaleBy( double sx, double sy, double sz ) {
    scale = Mat4.scale( sx, sy, sz ).mult( scale );
  }

  public void rotateBy( double angle, double x, double y, double z ) {
    rotate = Mat4.rotate( angle, x, y, z ).mult( rotate );
  }

  public void translateBy( double x, double y, double z ) {
    translate = Mat4.translate( x, y, z ).mult( translate );
  }

  // add current vertices to soups
  public void draw( Soups soups ) {
    Triple[] current = new Triple[5];
    for( int k=0; k<current.length; k++ ) {
      current[k] = translate.mult( rotate.mult( scale.mult( model[k] ) ) );
System.out.println("fire pyramid vertex " + k + " is " + current[k] );
    }

    soups.addTri( new Triangle( new Vertex( current[0], 0,0),
                             new Vertex( current[1], 1,0),
                             new Vertex( current[4], 0.5,1),
                             16 ) );
    soups.addTri( new Triangle( new Vertex( current[1], 0,0),
                             new Vertex( current[2], 1,0),
                             new Vertex( current[4], 0.5,1),
                             10 ) );
    soups.addTri( new Triangle( new Vertex( current[2], 0,0),
                             new Vertex( current[3], 1,0),
                             new Vertex( current[4], 0.5,1),
                             11 ) );
    soups.addTri( new Triangle( new Vertex( current[3], 0,0),
                             new Vertex( current[0], 1,0),
                             new Vertex( current[4], 0.5,1),
                             12 ) );
  }

}
