import java.util.Scanner;
import java.nio.FloatBuffer;

public class Mat4{

  public double[][] a;

  public Mat4( double a11, double a12, double a13, double a14,
               double a21, double a22, double a23, double a24,
               double a31, double a32, double a33, double a34,
               double a41, double a42, double a43, double a44 ){
    a = new double[4][4];
    a[0][0] = a11;  a[0][1] = a12;  a[0][2] = a13;  a[0][3] = a14;
    a[1][0] = a21;  a[1][1] = a22;  a[1][2] = a23;  a[1][3] = a24;
    a[2][0] = a31;  a[2][1] = a32;  a[2][2] = a33;  a[2][3] = a34;
    a[3][0] = a41;  a[3][1] = a42;  a[3][2] = a43;  a[3][3] = a44;
  }

  public Mat4( double[][] temp ){
    a = new double[4][4];
    for( int r=0; r<4; r++ )
      for( int c=0; c<4; c++ ){
        a[r][c] = temp[r][c];
      }
  }

  public Mat4( Scanner input ){
    a = new double[4][4];
    for( int r=0; r<4; r++ )
      for( int c=0; c<4; c++ ){
        a[r][c] = input.nextDouble();
      }
  }

  // create various handy matrices

  public static Mat4 identity(){
    return new Mat4( 1, 0, 0, 0,
                     0, 1, 0, 0,
                     0, 0, 1, 0,
                     0, 0, 0, 1 );
  }

  public static Mat4 translate( double a, double b, double c ){
    return new Mat4( 1, 0, 0, a,
                     0, 1, 0, b,
                     0, 0, 1, c,
                     0, 0, 0, 1 );
  }

  public static Mat4 rotate( double theta, double x, double y, double z ){
    double c = Math.cos( Math.toRadians( theta ) );
    double s = Math.sin( Math.toRadians( theta ) );
    Triple axis = new Triple( x, y, z );
    axis = axis.normalized();
    double d = 1-c;
    
    return new Mat4( x*x*d+c, x*y*d-s*z, x*z*d+s*y, 0,
                     x*y*d+s*z, y*y*d+c, y*z*d-s*x, 0,
                     x*z*d-s*y, y*z*d+s*x, z*z*d+c, 0,
                     0, 0, 0, 1 );
  }

  public static Mat4 scale( double a, double b, double c ){
    return new Mat4( a, 0, 0, 0,
                     0, b, 0, 0,
                     0, 0, c, 0,
                     0, 0, 0, 1 );
  }
 
  public static Mat4 frustum( double l, double r, double b, double t,
                               double n, double f ){
    return new Mat4( 2*n/(r-l), 0, (r+l)/(r-l), 0,
                     0, 2*n/(t-b), (t+b)/(t-b), 0,
                     0, 0, - (f+n)/(f-n), -(2*f*n)/(f-n),
                     0, 0, -1, 0 );
  }

  public static Mat4 ortho( double l, double r, double b, double t,
                               double n, double f ){
    return new Mat4( 2/(r-l), 0, 0, - (r+l)/(r-l),
                     0, 2/(t-b), 0, -(t+b)/(t-b),
                     0, 0, -2/(f-n), -(f+n)/(f-n),
                     0, 0, 0, 1 );
  }

  public static Mat4 lookAt( double eyex, double eyey, double eyez,
                             double cx, double cy, double cz,
                             double ux, double uy, double uz ){
    Triple e = new Triple( eyex, eyey, eyez );
    Triple c = new Triple( cx, cy, cz );
    Triple u = new Triple( ux, uy, uz );
    
    Triple n = c.subtract( e );
    Triple r = n.crossProduct( u );
    Triple w = r.crossProduct( n );
    n = n.normalized();
    r = r.normalized();
    w = w.normalized();
   
    Mat4 translate = new Mat4( 1, 0, 0, -e.x,
                               0, 1, 0, -e.y,
                               0, 0, 1, -e.z,
                               0, 0, 0, 1 );
    Mat4 rotate = new Mat4( r.x, r.y, r.z, 0,
                            w.x, w.y, w.z, 0,
                            -n.x, -n.y, -n.z, 0,
                            0, 0, 0, 1 );
    Mat4 lookAt = rotate.mult( translate );
    return lookAt;
  }

  public Mat4 mult( Mat4 m ){
    double[][] temp = new double[4][4];
    for( int r=0; r<4; r++ )
      for( int c=0; c<4; c++ ){
        // form temp[r][c]
        temp[r][c] = 0;
        for( int k=0; k<4; k++ )
          temp[r][c] += a[r][k] * m.a[k][c];
      }
    return new Mat4( temp );    
  }
  
  // multiply this Mat4 by v, pretending
  // as usual that there's a 1 in the 4th spot
  // and doing perspective division before
  // returning as a Triple
  public Triple mult( Triple v ) {
    Triple result = new Triple(
                     a[0][0]*v.x + a[0][1]*v.y + a[0][2]*v.z + a[0][3]*1,
                     a[1][0]*v.x + a[1][1]*v.y + a[1][2]*v.z + a[1][3]*1,
                     a[2][0]*v.x + a[2][1]*v.y + a[2][2]*v.z + a[2][3]*1
                    );
    double w = a[3][0]*v.x + a[3][1]*v.y + a[3][2]*v.z + a[3][3]*1;

    // perspective divide it!
    result = result.mult( 1/w );

    return result;
  }

  public String toString(){
    String s = "\n";
    for( int r=0; r<4; r++ ){
      for( int c=0; c<4; c++ ){
        s += Util.nice( a[r][c], 12, 5 );
      }
      s += "\n";
    }
    return s;
  }

  // convert this matrix a into handy column major
  // float[] and then make it a FloatBuffer
  public FloatBuffer toBuffer(){
    float[] fa = new float[16];

    int index = 0;

    for( int c=0; c<4; c++ )
      for( int r=0; r<4; r++ )
      {
        fa[index] = (float) a[r][c];
        index++;
      }

    return Util.arrayToBuffer( fa );
  }

}
