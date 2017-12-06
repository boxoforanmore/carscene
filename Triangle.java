import java.nio.FloatBuffer;

public class Triangle{

  private Vertex v1, v2, v3;
  private int textureNumber;

  // create triangle from given vertices using given texture
  public Triangle( Vertex v1in, Vertex v2in, Vertex v3in, int texIn ){
    v1 = v1in;
    v2 = v2in;
    v3 = v3in;
    textureNumber = texIn;
  }

  // send position data for triangle to Util.appDataBuffer
  public void positionToBuffer(){
    v1.positionToBuffer();
    v2.positionToBuffer();
    v3.positionToBuffer();
  }

  // send texCoords data for triangle to Util.appDataBuffer
  public void texCoordsToBuffer(){
    v1.texCoordsToBuffer();
    v2.texCoordsToBuffer();
    v3.texCoordsToBuffer();
  }

  public int getTexture(){
    return textureNumber;
  }

  public String toString() {
    return v1 + " " + v2 + " " + v3;
  }

}// Triangle
