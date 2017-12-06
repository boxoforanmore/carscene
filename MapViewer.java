/*
   a map viewer maintains the 
   viewport and ortho projection
   for specified fixed map view
*/

public class MapViewer{
  
  private int px, py, pw, ph;  // viewport info
  private double left, right, bottom, top, near, far;  // projection info
  private Mat4 proj;  // ortho projection matrix
  private int projLoc;  // uniform location

  // view info
  // (not really needed because "map view"
  //  is already looking down the -z axis,
  // so ortho takes care of everything
  // Since the vertex shader expects proj and
  // view, use view = identity matrix
  private Mat4 view;  // will just be identity
  private int viewLoc;  // uniform location

  public MapViewer( int vx, int vy, int vw, int vh, 
                 double l, double r, double b, double t, double n, double f
                  ) {

    px=vx; py=vy; pw=vw; ph=vh;

    left=l;  right=r; bottom=b; top=t; near=n; far=f;

    proj = Mat4.ortho( left, right, bottom, top, near, far );
    projLoc = OpenGL.getUniformLoc( "proj" );

    view = Mat4.identity();
    viewLoc = OpenGL.getUniformLoc( "view" );

  }

  // set up viewport and send matrices
  public void activate(){

    OpenGL.viewport( px, py, pw, ph );

    OpenGL.sendUniformMatrix4( projLoc, proj );
    OpenGL.sendUniformMatrix4( viewLoc, view );

  }

}
