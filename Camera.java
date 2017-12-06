/*
   a camera maintains the 
   viewport, projection, and
   viewing info
   (using azimuth,altitude,+z is up
     scheme)
*/

public class Camera{
  
  private int px, py, pw, ph;  // viewport info
  private double left, right, bottom, top, near, far;  // projection info
  private Mat4 proj;  // corresponding matrix
  private int projLoc;  // uniform location

  // view info
  protected double eyeX, eyeY, eyeZ, azimuth, altitude;
  private Mat4 view;  // corresponding matrix
  private int viewLoc;  // uniform location

  public Camera( int vx, int vy, int vw, int vh, 
                 double l, double r, double b, double t, double n, double f,
                 double ex, double ey, double ez,
                 double azi, double alt ){

    px=vx; py=vy; pw=vw; ph=vh;

    left=l;  right=r; bottom=b; top=t; near=n; far=f;
    proj = Mat4.frustum( left, right, bottom, top, near, far );

    eyeX=ex; eyeY=ey; eyeZ=ez;
    azimuth = azi;  altitude = alt;

    updateView();

    projLoc = OpenGL.getUniformLoc( "proj" );
    viewLoc = OpenGL.getUniformLoc( "view" );
  }

  public String getUserInfo() {
    return eyeX + " " + eyeY + " " + eyeZ + " " + azimuth + " " + altitude;
  }

  // recompute view matrix because info has changed
  public void updateView(){

    double c = Math.cos( Math.toRadians( azimuth ) );
    double s = Math.sin( Math.toRadians( azimuth ) );
    double c2 = Math.cos( Math.toRadians( altitude ) );
    double s2 = Math.sin( Math.toRadians( altitude ) );

    view = Mat4.lookAt( eyeX, eyeY, eyeZ, 
                        eyeX + c*c2, eyeY + s*c2, eyeZ + s2,
                        0, 0, 1 );
  }

  // return unit vector from eye point to look at point
  public Triple getDirection() {
    // same as in updateView:
    double c = Math.cos( Math.toRadians( azimuth ) );
    double s = Math.sin( Math.toRadians( azimuth ) );
    double c2 = Math.cos( Math.toRadians( altitude ) );
    double s2 = Math.sin( Math.toRadians( altitude ) );

    return new Triple( c*c2, s*c2, s2 );
  }

  // return location of this camera
  public Triple getLocation() {
    return new Triple( eyeX, eyeY, eyeZ );
  }

  // return x-y rotation of camera
  public double getAzimuth() {
    return azimuth;
  }

  // set up viewport and send matrices
  public void activate(){

    OpenGL.viewport( px, py, pw, ph );

    OpenGL.sendUniformMatrix4( projLoc, proj );
    OpenGL.sendUniformMatrix4( viewLoc, view );

  }

  // shift eye point by given vector
  public void shift( double dx, double dy, double dz ){
    eyeX += dx; eyeY += dy; eyeZ += dz;
    updateView();
  }

  // shift eye point to given location
  public void shiftTo( double dx, double dy, double dz ){
    eyeX = dx; eyeY = dy; eyeZ = dz;
    updateView();
  }

  // change azimuth by given amount
  public void turn( double amount ){
    azimuth += amount;
    if( azimuth < 0 )
      azimuth += 360;
    if( azimuth > 360 )
      azimuth -= 360;
    updateView();
  }

  // change azimuth to given angle
  public void turnTo( double angle ){
    azimuth = angle;
    if( azimuth < 0 )
      azimuth += 360;
    if( azimuth > 360 )
      azimuth -= 360;
    updateView();
  }

  // change altitude by given amount
  public void tilt( double amount ){
    if( amount > 0 && altitude + amount <= 90-amount ) altitude += amount;
    if( amount < 0 && altitude + amount >= -90-amount ) altitude += amount;
    updateView();
  }

  // change altitude to given angle
  public void tiltTo( double angle ){
    if( -89 <= angle && angle <= 89 )
      altitude = angle;
    updateView();
  }

  public void consoleDisplay() {
    System.out.println("At x= " + eyeX + " y= " + eyeY + " z= " + eyeZ );
    double alt = altitude;
    System.out.println("azimuth= " + azimuth + " altitude= " + altitude );
  }

}
