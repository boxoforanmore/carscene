import java.nio.FloatBuffer;

public class Vertex{

  private double x, y, z;  // position
  private double s, t;     // texture coordinates

  public Vertex( double xin, double yin, double zin, double sIn, double tIn ){
    x = xin;
    y = yin;
    z = zin;
    s = sIn;   t = tIn;
  }

  public Vertex( Triple p, double sIn, double tIn ){
    x = p.x;
    y = p.y;
    z = p.z;
    s = sIn;   t = tIn;
  }

  // append data for this vertex to the big app buffer

  // send position data to Util.appDataBuffer
  public void positionToBuffer(){
    Util.bufferPut( x );
    Util.bufferPut( y );
    Util.bufferPut( z );
  }

  // send texCoords data to Util.appDataBuffer
  public void texCoordsToBuffer(){
    Util.bufferPut( s );
    Util.bufferPut( t );
  }

  public String toString() {
    return "[" + x + " " + y + " " + z + "]";
  }

}
