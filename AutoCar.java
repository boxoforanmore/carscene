 /*
   a fire pyramid has
   4 fiery triangles,
   can be resized,
   rotated, moved
*/
public class AutoCar {

  private Triple[] model;  // model vertices of the pyramid
  private Mat4 scale,rotate,translate;  // transform it cumulatively
  protected double xpos, ypos, zpos, initAngle;

/*
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
*/


  public AutoCar() {
    model = new Triple[ 18 ];

    // Left side of Car
    model[0] = new Triple(-11.5, -5, 1.5);
    model[1] = new Triple(-7.5, -5, 0);  
    model[2] = new Triple(10, -5, 0);
    model[3] = new Triple(12, -5, 2.5);
    model[4] = new Triple(11, -5, 6);
    model[5] = new Triple(10, -3, 10);
    model[6] = new Triple(-0.5, -3, 10);
    model[7] = new Triple(-6, -5, 6.5);
    model[8] = new Triple(-10, -5, 6);

    // Right side of Car
    model[9] = new Triple(-11.5, 5, 1.5);
    model[10] = new Triple(-7.5, 5, 0);  
    model[11] = new Triple(10, 5, 0); 
    model[12] = new Triple(12, 5, 2.5);
    model[13] = new Triple(11, 5, 6);
    model[14] = new Triple(10, 3, 10);
    model[15] = new Triple(-0.5, 3, 10);
    model[16] = new Triple(-6, 5, 6.5);
    model[17] = new Triple(-10, 5, 6); 

    scale = Mat4.identity();
    rotate = Mat4.identity();
    translate = Mat4.identity();
  }

  public void scaleBy( double sx, double sy, double sz ) { 
    scale = Mat4.scale( sx, sy, sz ).mult( scale );
  }

  public void rotateBy( double angle, double x, double y, double z ) { 
    initAngle += angle;

    rotate = Mat4.rotate( angle, x, y, z ).mult( rotate );
  }

  public void translateBy( double x, double y, double z ) { 
    xpos += x;
    ypos += y;
    zpos += z;

    translate = Mat4.translate( x, y, z ).mult( translate );
  }

  public void initialPos( double x, double y, double z ) {
    xpos = x;
    ypos = y;
    zpos = z;
    initAngle = 0;

    translate = Mat4.translate( x, y, z ).mult( translate );
  }

  public void draw( Soups soups ) {

    Triple[] current = new Triple[18];

    for( int k = 0; k < current.length; k++ ) {
      current[k] = translate.mult( rotate.mult( scale.mult( model[k] ) ) );

      System.out.println("auto-car vertex " + k + " is " + current[k]);
    }
/*   
    aLSvert = new Vertex(current[0], 0, 0);
    aRSvert = new Vertex(current[1], 1, 0);
    bLSvert = new Vertex(current[2], 2, 0);
    b2Svert = new Vertex(current[3]
    c1Svert = new Vertex(current[4]
    c2Svert = new Vertex(current[5]
    d1Svert = new Vertex(current[6]
    d2Svert = new Vertex(current[7]
    e1Svert = new Vertex(current[8]
    e2Svert = new Vertex(current[9]
    f1Svert = new Vertex(current[10]
    f2Svert = new Vertex(current[11]
    g1Svert = new Vertex(current[12]
    g2Svert = new Vertex(current[13]
    h1Svert = new Vertex(current[14]
    h2Svert = new Vertex(current[15]
    i1Svert = new Vertex(current[16]
    i2Svert = new Vertex(current[17]
*/

    // Note -- Wrong vertices are used for texture mapping, but it creates a funny effect, so I'll keep them
    //         This is likely because the texture coordinates mostly exceed the coordinates of the textures
    // Left Side
    soups.addTri(new Triangle(new Vertex(current[0], -.94, -.7),
                              new Vertex(current[8], -.81, .18 ),
                              new Vertex(current[7], -.49, .28),
                              17) );
    soups.addTri(new Triangle(new Vertex(current[7], -.49, .28),
                              new Vertex(current[0], -.94, -.7),
                              new Vertex(current[1], -.61, -1),
                              17) );
    soups.addTri(new Triangle(new Vertex(current[1], -.61, -.5),
                              new Vertex(current[7], -.49, .28),
                              new Vertex(current[6], -.04, .96),
                              17) );
    soups.addTri(new Triangle(new Vertex(current[6], -.04, .96),
                              new Vertex(current[1], -.61, -1),
                              new Vertex(current[2], 0.82, -1),
                              17) );
    soups.addTri(new Triangle(new Vertex(current[2], .82, -1),
                              new Vertex(current[6], -.04, .96),
                              new Vertex(current[5], .82, .96),
                              17) );
    soups.addTri(new Triangle(new Vertex(current[5], .82, .96),
                              new Vertex(current[4], .90, .18),
                              new Vertex(current[2], .82, .96),
                              17) );
    soups.addTri(new Triangle(new Vertex(current[2], .82, .96),
                              new Vertex(current[4], .90, .18),
                              new Vertex(current[3], .98, -.5),
                              17) );

    // Right Side
    soups.addTri(new Triangle(new Vertex(current[9], -.94, -0.7),
                              new Vertex(current[17], -.81, .18 ),
                              new Vertex(current[16], -.49, .28),
                              17) );
    soups.addTri(new Triangle(new Vertex(current[16], -.49, .28),
                              new Vertex(current[9], -.94, -.7),
                              new Vertex(current[10], -.61, -1), 
                              17) );
    soups.addTri(new Triangle(new Vertex(current[10], -.61, -1), 
                              new Vertex(current[16], -.49, .28),
                              new Vertex(current[15], -.04, .96),
                              17) );
    soups.addTri(new Triangle(new Vertex(current[15], -.04, .96),
                              new Vertex(current[10], -.61, -1), 
                              new Vertex(current[11], .82, -1), 
                              17) );
    soups.addTri(new Triangle(new Vertex(current[11], .82, -1), 
                              new Vertex(current[15], -.04, .96),
                              new Vertex(current[14], .82, .96),
                              17) );
    soups.addTri(new Triangle(new Vertex(current[14], .82, .96),
                              new Vertex(current[13], .90, .18),
                              new Vertex(current[11], .82, -1), 
                              17) );
    soups.addTri(new Triangle(new Vertex(current[11], .82, -1), 
                              new Vertex(current[13], .90, .18),
                              new Vertex(current[12], .98, -.5),
                              17) );


    // Front
    soups.addTri(new Triangle(new Vertex(current[10], -.96, -1),
                              new Vertex(current[1], .96, -1),
                              new Vertex(current[0], .96, -.7),
                              18) );
    soups.addTri(new Triangle(new Vertex(current[0], .96, -.7),
                              new Vertex(current[10], -.96, -1),
                              new Vertex(current[9], -.96,  -.7),
                              18) );
    soups.addTri(new Triangle(new Vertex(current[9], -.96, -.7),
                              new Vertex(current[0], .96, -.7),
                              new Vertex(current[8], .96, .18),
                              18) );
    soups.addTri(new Triangle(new Vertex(current[8], .96, .18),
                              new Vertex(current[9], -.96, -.7),
                              new Vertex(current[17], -.96, .18),
                              18) );
    soups.addTri(new Triangle(new Vertex(current[17], -.96, .18),
                              new Vertex(current[16], -.96, .28),
                              new Vertex(current[8], .96, .18),
                              18) );
    soups.addTri(new Triangle(new Vertex(current[8], .96, .18),
                              new Vertex(current[7], .96, .28),
                              new Vertex(current[16], -.96, .28),
                              18) );
    soups.addTri(new Triangle(new Vertex(current[16], -.96, .28),
                              new Vertex(current[7], .96, .28),
                              new Vertex(current[6], .58, .96),
                              18) );
    soups.addTri(new Triangle(new Vertex(current[6], .58, .96),
                              new Vertex(current[16], -.96, .28),
                              new Vertex(current[15], -.58, .28),
                              18) );

    // Back
    soups.addTri(new Triangle(new Vertex(current[2], -.96, -1),
                              new Vertex(current[11], .96, -1),
                              new Vertex(current[12], .96, -.5),
                              19) );
    soups.addTri(new Triangle(new Vertex(current[12], .96, -.5),
                              new Vertex(current[2], -.96, -1),
                              new Vertex(current[3], -.96, -.5),
                              19) );
    soups.addTri(new Triangle(new Vertex(current[3], -.96, -.5),
                              new Vertex(current[12], .96, -.5),
                              new Vertex(current[13], .96, .18),
                              19) );
    soups.addTri(new Triangle(new Vertex(current[13], .96, .18),
                              new Vertex(current[3], -.96, -.5),
                              new Vertex(current[4], -.96, .18),
                              19) );
    soups.addTri(new Triangle(new Vertex(current[4], -.96, .18),
                              new Vertex(current[13], .96, .18),
                              new Vertex(current[14], .58, .96),
                              19) );
    soups.addTri(new Triangle(new Vertex(current[14], .58, .96),
                              new Vertex(current[4], -.96, .18),
                              new Vertex(current[5], -.58, .96),
                              19) );


    // Top 
    soups.addTri(new Triangle(new Vertex(current[14], 1, 1),
                              new Vertex(current[5], 1, -1),
                              new Vertex(current[6], -1, -1),
                              20) );
    soups.addTri(new Triangle(new Vertex(current[6], -1, -1),
                              new Vertex(current[14], 1, -1),
                              new Vertex(current[15], -1, 1),
                              20) );


  }

/*
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
                             9 ) );
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
*/

}
