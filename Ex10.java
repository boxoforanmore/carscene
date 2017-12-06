/*  
   basic framework for drawing textured triangles
*/

import static org.lwjgl.glfw.GLFW.*;  // for key codes

import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.PrintWriter;

public class Ex10 extends Basic
{
  public static void main(String[] args)
  {
    if( args.length != 0 ){
      System.out.println("Usage:  j Ex10");
      System.exit(1);
    }

    Ex10 app = new Ex10( "", 0, 1200, 600, 120 );

    app.start();

  }// main

  // instance variables 

  private Camera camera;        // first-person view
  private MapViewer mapViewer;  // map view

  private Soups mutableSoups, frozenSoups;

  private FirePyramid pyramid;
  private AutoCar car1;
  private AutoCar car2;
  private AutoCar car3;
  private AutoCar car4;
  private AutoCar launch;

  private AutoCar myCar;

  // Create position objects for each car
  private double[] carPos1 = {10, 10}; 
  private double[] carPos2 = {10, 50}; 
  private double[] carPos3 = {90, 90}; 

  // Create speed objects for each car
  private double[] speed1 = {0, .1}; 
  private double[] speed2 = {.2, 0}; 
  private double[] speed3 = {-.15, 0};
  private double[] speed4 = {0, .3};

  // construct basic application with given title, pixel width and height
  // of drawing area, frames per second, and name of world data file
  public Ex10( String appTitle, int windowShift, int pw, int ph, int fps )
  {
    super( appTitle, windowShift, pw, ph, (long) ((1.0/fps)*1000000000) );

    Pic.init();      // load all the textures
    Util.init();     // set up single large buffer for soup use

  }// Ex10 constructor

  protected void init()
  {
    OpenGL.init();

    // activate all the textures
    for( int k=0; k<Pic.size(); k++ ){
      OpenGL.loadTexture( Pic.get(k) );
      System.out.println("loaded texture number " + k );
    }

    OpenGL.setBackColor( 1, 1, 1 );

    camera = new Camera( 0, 0, 
                          600, 600,        // first person viewport within pixel grid
                         -.1, .1, -.1, .1, .4, 1000,  // frustum (camera shape)
                          50, 0, 1.55,           // eye point
                          90, 0 );           // azimuth, altitude

    // set up map view stuff
    mapViewer = new MapViewer( 600, 0, 600, 600, 0, 100, 0, 100, -500, 1 );
    
    // set up frozenSoups for display once and for all
    frozenSoups = new Soups( Pic.size() );
 
    // add some "frozen" triangles to frozenSoups:
/*
    frozenSoups.addTri( new Triangle( new Vertex(45,50,0, 0,0),
                                      new Vertex(55,50,0, 1,0),
                                      new Vertex(50,50,20, 0.5,1),
                                   12 ) );
    frozenSoups.addTri( new Triangle( new Vertex(45,30,0, 0,0),
                                      new Vertex(55,30,0, 1,0),
                                      new Vertex(50,30,10, 0.5,1),
                                   8 ) );
    frozenSoups.addTri( new Triangle( new Vertex(65, 35, 30, 0, 0 ),
                                      new Vertex(65, 45, 30, 1, 0 ),
                                      new Vertex(55,40,30, 0.5,1),
                                      16 ) );
    frozenSoups.addTri( new Triangle( new Vertex(0,0,20, 0,0),
                                      new Vertex(5,0,20, 1,0),
                                      new Vertex(0,5,20, 0,1),
                                      9 ) );
    frozenSoups.addTri( new Triangle( new Vertex(95,0,20, 0,0),
                                      new Vertex(100,0,20, 1,0),
                                      new Vertex(100,5,20, 1,1),
                                      10 ) );
    frozenSoups.addTri( new Triangle( new Vertex(95,100,20, 0,1),
                                      new Vertex(100,95,20, 1,0),
                                      new Vertex(100,100,20, 1,1),
                                      11 ) );
    frozenSoups.addTri( new Triangle( new Vertex(0,95,20, 0,0),
                                      new Vertex(5,100,20, 1,1),
                                      new Vertex(0,100,20, 0,1),
                                      12 ) );
*/
    ground(50, 50, 100, 100);
    building(2, 2, 4, 4, 25);
    building(50, 60, 3, 3, 30);
    building(4, 6, 3, 8, 50);
    building(70, 70, 10, 3, 45);
    building(30, 70, 10, 20, 35);
    building(90, 85, 5, 10, 22);
    building(35, 40, 4, 6, 18);
    building(10, 28, 4, 3, 35);
    building(10, 93, 5, 5, 25);
    building(36, 92, 3, 5, 16);
    building(40, 5, 2, 2, 10);
    building(45, 75, 3, 3, 20);
    building(55, 60, 4, 5, 25);
    building(25, 60, 3, 5, 15);
    building(38, 35, 4, 3, 24);
    building(60, 30, 2, 2, 18);
    building(90, 5, 3, 5, 38);
    building(80, 40, 4, 2, 22);
    building(75, 60, 4, 4, 20);
    building(80, 40, 2, 2, 16);
    building(65, 20, 5, 6, 30);
    building(25, 25, 4, 4, 16);

    skybox();
    boxOfMortys();

    frozenSoups.sortByTexture();

    pyramid = new FirePyramid();
      pyramid.scaleBy( 10, 10, 10 );
      pyramid.translateBy( 20, 70, 0 );

    car1 = new AutoCar();
      car1.scaleBy(.15, .15, .15);
      car1.initialPos(10, 10, 0);
      car1.rotateBy(-90, 0, 0, 1);
   //   car1.translateBy(carPos1[0], carPos1[1] , 0);

    car2 = new AutoCar();
      car2.scaleBy(.15, .15, .15);
      car2.initialPos(10, 50, 0);
      car2.rotateBy(180, 0, 0, 1);
   //   car2.translateBy(carPos2[0], carPos2[1], 0);

    car3 = new AutoCar();
      car3.scaleBy(.15, .15, .15);
   //   car3.translateBy(carPos3[0], carPos3[1], 0);
      car3.initialPos(90, 90, 0 );

    car4 = new AutoCar();
      car4.scaleBy(.15, .15, .15);
      car4.initialPos(90, 20, 0);
      car4.rotateBy(-90, 0, 0, 1);

    myCar = new AutoCar();
      myCar.scaleBy(.15, .15, .15);
      myCar.initialPos(camera.getLocation().x, camera.getLocation().y, 0);
      myCar.rotateBy(-camera.azimuth, 0, 0, 1);
      
/*    launch = new AutoCar();
      myCar.scaleBy(.05, .05, .05);
      myCar.initialPos(camera.getLocation().x, camera.getLocation().y, 0);
      myCar.rotateBy(-camera.azimuth, 0, 0, 1);
*/
  }// init

  // Creates a building based on given coordinates and size information
  protected void building(double xCoord, double yCoord, double len, double width, double height)
  {
    double x1 = (xCoord - (width/2));   // Low x
    double x2 = (xCoord + (width/2));   // High x
    double y1 = (yCoord - (len/2));     // Low y
    double y2 = (yCoord + (len/2));     // High y

    Vertex vertex1, vertex2, vertex3;

    Triple pos1, pos2, pos3, pos4, pos5, pos6, pos7, pos8, col1, col2, col3, col4;
    Vertex vert1, vert2, vert3;

    pos1 = new Triple(x1, y1, 0); 
    pos2 = new Triple(x2, y1, 0); 
    pos3 = new Triple(x2, y1, height);
    pos4 = new Triple(x1, y1, height);
    pos5 = new Triple(x1, y2, 0); 
    pos6 = new Triple(x2, y2, 0); 
    pos7 = new Triple(x2, y2, height);
    pos8 = new Triple(x1, y2, height);

    col1 = new Triple(1, 0, 0); 
    col2 = new Triple(0, 1, 0); 
    col3 = new Triple(0, 0, 1); 
    col4 = new Triple(1, 0, 1); 
    
    // Front Triangles
    frozenSoups.addTri( new Triangle(new Vertex(pos1, 0, 0), 
                                     new Vertex(pos2, 0, 1), 
                                     new Vertex(pos3, 1, 1), 
				     25 ) );

    frozenSoups.addTri( new Triangle(new Vertex(pos3, 1, 1), 
                                     new Vertex(pos4, 1, 0), 
                                     new Vertex(pos1, 0, 0),
                                     25 ) );
    
    // Left Triangles
    frozenSoups.addTri( new Triangle(new Vertex(pos1, 0, 0), 
                                     new Vertex(pos5, 0, 1), 
                                     new Vertex(pos8, 1, 1),
                                     26 ) );

    frozenSoups.addTri( new Triangle(new Vertex(pos8, 1, 1), 
                                     new Vertex(pos4, 1, 0), 
                                     new Vertex(pos1, 0, 0),
                                     26 ) );

    // Right Triangles
    frozenSoups.addTri( new Triangle(new Vertex(pos2, 0, 0), 
                                     new Vertex(pos6, 0, 1), 
                                     new Vertex(pos7, 1, 1),
                                     24 ) );

    frozenSoups.addTri( new Triangle(new Vertex(pos7, 1, 1), 
                                     new Vertex(pos3, 1, 0), 
                                     new Vertex(pos2, 0, 0),
                                     24 ) );

    // Back Triangles
    frozenSoups.addTri( new Triangle(new Vertex(pos5, 0, 0), 
                                     new Vertex(pos6, 0, 1), 
                                     new Vertex(pos7, 1, 1),
                                     27 ) );

    frozenSoups.addTri( new Triangle(new Vertex(pos7, 1, 1), 
                                     new Vertex(pos8, 1, 0), 
                                     new Vertex(pos5, 0, 0),
                                     27 ) );

    // Top Triangles
    frozenSoups.addTri( new Triangle(new Vertex(pos4, 0, 0), 
                                     new Vertex(pos3, 0, 1), 
                                     new Vertex(pos7, 1, 1),
                                     28 ) );

    frozenSoups.addTri( new Triangle(new Vertex(pos7, 1, 1), 
                                     new Vertex(pos8, 1, 0), 
                                     new Vertex(pos4, 0, 0),
                                     28 ) );
  }

  // Creates a skybox at the edge of the active plane
  protected void skybox() {
    Triple pos1, pos2, pos3, pos4, pos5, pos6, pos7, pos8, col1, col2, col3, col4;

    pos1 = new Triple(0, 0, 0);
    pos2 = new Triple(100, 0, 0);
    pos3 = new Triple(100, 0, 100);
    pos4 = new Triple(0, 0, 100);
    pos5 = new Triple(0, 100, 0);
    pos6 = new Triple(100, 100, 0);
    pos7 = new Triple(100, 100, 100);
    pos8 = new Triple(0, 100, 100);

    // Front Triangles
    frozenSoups.addTri( new Triangle(new Vertex(pos1, 0, 0),
                                     new Vertex(pos2, .25, 0),
                                     new Vertex(pos3, .25, 1),
                                     21 ) );

    frozenSoups.addTri( new Triangle(new Vertex(pos3, .25, 1),
                                     new Vertex(pos4, 0, 1),
                                     new Vertex(pos1, 0, 0),
                                     21 ) );

    // Left Triangles
    frozenSoups.addTri( new Triangle(new Vertex(pos1, 1, 0),
                                     new Vertex(pos5, .75, 0),
                                     new Vertex(pos8, .75, 1),
                                     21 ) );

    frozenSoups.addTri( new Triangle(new Vertex(pos8, .75, 1),
                                     new Vertex(pos4, 1, 1),
                                     new Vertex(pos1, 1, 0),
                                     21 ) );

    // Right Triangles
    frozenSoups.addTri( new Triangle(new Vertex(pos2, .25, 0),
                                     new Vertex(pos6, .5, 0),
                                     new Vertex(pos7, .5, 1),
                                     21 ) );

    frozenSoups.addTri( new Triangle(new Vertex(pos7, .5, 1),
                                     new Vertex(pos3, .25, 1),
                                     new Vertex(pos2, .25, 0),
                                     21 ) );

    // Back Triangles
    frozenSoups.addTri( new Triangle(new Vertex(pos5, .75, 0),
                                     new Vertex(pos6, .5, 0),
                                     new Vertex(pos7, .5, 1),
                                     21 ) );

    frozenSoups.addTri( new Triangle(new Vertex(pos7, .5, 1),
                                     new Vertex(pos8, .75, 1),
                                     new Vertex(pos5, .75, 0),
                                     21 ) );

    // Top Triangles
//    frozenSoups.addTri( new Triangle(new Vertex(pos4, 0, 0),
//                                     new Vertex(pos3, 0, 1),
//                                     new Vertex(pos7, 1, 1),
//                                     20 ) );

//    frozenSoups.addTri( new Triangle(new Vertex(pos7, 0, 0),
//                                     new Vertex(pos8, 1, 0),
//                                     new Vertex(pos4, 1, 1),
//                                     20 ) );
  }

  // Creates a box of Morty's --> nearly a perfect hiding place from the Federation of Ricks
  protected void boxOfMortys() {
    Triple pos1, pos2, pos3, pos4, pos5, pos6, pos7, pos8, col1, col2, col3, col4;

    pos1 = new Triple(-10, -10, 0);
    pos2 = new Triple(110, -10, 0);
    pos3 = new Triple(110, -10, 120);
    pos4 = new Triple(-10, -10, 120);
    pos5 = new Triple(-10, 110, 0);
    pos6 = new Triple(110, 110, 0);
    pos7 = new Triple(110, 110, 120);
    pos8 = new Triple(-10, 110, 120);

    // Front Triangles
    frozenSoups.addTri( new Triangle(new Vertex(pos1, 0, 0),
                                     new Vertex(pos2, 1, 0),
                                     new Vertex(pos3, 1, 1),
                                     22 ) );

    frozenSoups.addTri( new Triangle(new Vertex(pos3, 1, 1),
                                     new Vertex(pos4, 0, 1),
                                     new Vertex(pos1, 0, 0),
                                     22 ) );

    // Left Triangles
    frozenSoups.addTri( new Triangle(new Vertex(pos1, 0, 0),
                                     new Vertex(pos5, 1, 0),
                                     new Vertex(pos8, 1, 1),
                                     22 ) );

    frozenSoups.addTri( new Triangle(new Vertex(pos8, 1, 1),
                                     new Vertex(pos4, 0, 1),
                                     new Vertex(pos1, 0, 0),
                                     22 ) );

    // Right Triangles
    frozenSoups.addTri( new Triangle(new Vertex(pos2, 0, 0),
                                     new Vertex(pos6, 1, 0),
                                     new Vertex(pos7, 1, 1),
                                     22 ) );

    frozenSoups.addTri( new Triangle(new Vertex(pos7, 1, 1),
                                     new Vertex(pos3, 0, 1),
                                     new Vertex(pos2, 0, 0),
                                     22 ) );

    // Back Triangles
    frozenSoups.addTri( new Triangle(new Vertex(pos5, 0, 0),
                                     new Vertex(pos6, 1, 0),
                                     new Vertex(pos7, 1, 1),
                                     22 ) );

    frozenSoups.addTri( new Triangle(new Vertex(pos7, 1, 1),
                                     new Vertex(pos8, 0, 1),
                                     new Vertex(pos5, 0, 0),
                                     22 ) );

    // Top Triangles
//    frozenSoups.addTri( new Triangle(new Vertex(pos4, 0, 0),
//                                     new Vertex(pos3, 0, 1),
//                                     new Vertex(pos7, 1, 1),
//                                     20 ) );

//    frozenSoups.addTri( new Triangle(new Vertex(pos7, 0, 0),
//                                     new Vertex(pos8, 1, 0),
//                                     new Vertex(pos4, 1, 1),
//                                     20 ) );
  }



  // Creates a ground based on given coordinate and size information
  protected void ground(double xCoord, double yCoord, double len, double width)
  {
    double x1 = (xCoord - (width/2));
    double x2 = (xCoord + (width/2));
    double y1 = (yCoord - (len/2));
    double y2 = (yCoord + (len/2));

    Triple pos1, pos2, pos3, pos4, col1;

    pos1 = new Triple(x1, y1, 0);
    pos2 = new Triple(x2, y1, 0);
    pos3 = new Triple(x2, y2, 0);
    pos4 = new Triple(x1, y2, 0);
    col1 = new Triple(0.5, 0.5, 0.5);

    frozenSoups.addTri(new Triangle(new Vertex(pos1, 0, 0), 
                                    new Vertex(pos2, 0, 1), 
                                    new Vertex(pos3, 1, 1),
                                    23 ) );
    frozenSoups.addTri(new Triangle(new Vertex(pos3, 1, 1), 
                                    new Vertex(pos4, 1, 0), 
                                    new Vertex(pos1, 0, 0),
                                    23 ) );
  }


  // update view matrix based on eye, angles
  private void updateView(){
  
    camera.updateView();
  }

  private static double amount = 1;  // distance to move per step
  private static int mode = 2;

  private static int camAziAmount = 5;
  private static int camAltAmount = 3;

  protected void processInputs()
  {
    // process all waiting input events
    while( InputInfo.size() > 0 )
    {
      InputInfo info = InputInfo.get();

      if( info.kind == 'k' && (info.action == GLFW_PRESS || 
                               info.action == GLFW_REPEAT) )
      {
        int code = info.code, mods = info.mods;
        // System.out.println("code: " + code + " mods: " + mods );

         // keys to control the camera:

   //         if( code == GLFW_KEY_LEFT && mods == 0 )    camera.shift( -amount, 0, 0 );
   //         else if( code == GLFW_KEY_RIGHT  && mods == 0 )  camera.shift( amount, 0, 0 );
   //         else if( code == GLFW_KEY_DOWN  && mods == 0 )  camera.shift( 0, -amount, 0 );
   //         else if( code == GLFW_KEY_UP  && mods == 0 )  camera.shift( 0, amount, 0 );
   //         else if( (code == GLFW_KEY_PAGE_DOWN || code==GLFW_KEY_9)  && mods == 0 )  camera.shift( 0, 0, -amount );
   //         else if( (code == GLFW_KEY_PAGE_UP || code==GLFW_KEY_0)  && mods == 0 )  camera.shift( 0, 0, amount );

            if( code == GLFW_KEY_DOWN && mods == 0 ) mySpeed--;
            else if( code == GLFW_KEY_UP && mods == 0 ) mySpeed++;
            else if( code == GLFW_KEY_LEFT && mods == 0) {
              camera.turn(camAziAmount);
              myCar.rotateBy(camAziAmount, 0, 0, 1);
            }
            else if( code == GLFW_KEY_RIGHT && mods == 0) {
              camera.turn(-camAziAmount);
              myCar.rotateBy(-camAziAmount, 0, 0, 1);
            }

//            else if( code == GLFW_KEY_MINUS && mods == 0 )
//              camera.turn( camAziAmount );
//            else if( code == GLFW_KEY_EQUAL && mods == 0 )
//              camera.turn( -camAziAmount );
            else if( code == GLFW_KEY_D && mods == 0 )
              camera.tilt( -camAltAmount );
            else if( code == GLFW_KEY_U && mods == 0 )
              camera.tilt( camAltAmount );

            // Launch a minicar (one at a time)
            else if ( code == GLFW_KEY_SPACE && mods == 0 ) launched++;
        // keys to control the fire pyramid
     
        else if( code == GLFW_KEY_A && mods == 0 )
          pyramid.translateBy( -1, 0, 0 );
        else if( code == GLFW_KEY_D && mods == 0 )
          pyramid.translateBy( 1, 0, 0 );
        else if( code == GLFW_KEY_X && mods == 0 )
          pyramid.translateBy( 0, -1, 0 );
        else if( code == GLFW_KEY_W && mods == 0 )
          pyramid.translateBy( 0, 1, 0 );
        else if( code == GLFW_KEY_Z && mods == 0 )
          pyramid.rotateBy( 3, 0, 0, 1 );
        else if( code == GLFW_KEY_C && mods == 0 )
          pyramid.rotateBy( -3, 0, 0, 1 );
      }// input event is a key

      else if ( info.kind == 'm' )
      {// mouse moved
      }

      else if( info.kind == 'b' )
      {// button action

      }// 'b' action

    }// loop to process all input events

  }// processInputs

  protected int mySpeed = 0;
//  protected int turnCam = camera.azimuth;
  protected int turnCar = 0;

  protected int launched = 0;
  protected double launchX, launchY;
  
  protected void update()
  {
    car1.translateBy(speed1[0], speed1[1], 0);
    carTrack(car1, speed1);

    car2.translateBy(speed2[0], speed2[1], 0);
    carTrack(car2, speed2);

    car3.translateBy(speed3[0], speed3[1], 0);
    carTrack(car3, speed3);

   // camera.shift(speed*camera.getDirection().x, camera.getDirection().y, 0);
    
    car4.translateBy(speed4[0], speed4[1], 0);
    carTrack(car4, speed4);

    myCar.translateBy(0.1*mySpeed*camera.getDirection().x, 0.1*mySpeed*camera.getDirection().y, 0);
    camera.shiftTo(myCar.xpos, myCar.ypos, camera.getLocation().z);
/*
   if( launched == 1 ) {
     launchX = camera.getDirection().x;
     launchY = camera.getDirection().y;

     launch.translateBy(0.5*launchX, 0.5*launchY, 0);
     launched++;
   }
   else if (launched > 1) {
     launch.translateBy(0.5*launchX, 0.5*launchY, 0);
     launched++;
     if(launched == 10) {
       launched = 0;
       launch.xpos = myCar.xpos;
       launch.ypos = myCar.ypos;
     }
   }
   else {
     launch.translateBy(0.1*mySpeed*camera.getDirection().x, 0.1*mySpeed*camera.getDirection().y, 0);
   }
*/
   // myCar.translateBy(camera.getLocation().x + speed*camera.getDirection().x, camera.getLocation().y + speed*camera.getDirection().y, 0);    
   // myCar.translateBy();
   // myCar
    //  System.out.println( getStepNumber() + "================================" );

  }// update

  // Tracks car position so that all cars stay on the active plane
  protected void carTrack(AutoCar car,  double[] speed) {
   // carPos[0] += speed[0];
   // carPos[1] += speed[1];

    if(car.xpos >= 99 || car.xpos <= 1 || car.ypos >= 99 || car.ypos <= 1)
    {
      car.rotateBy(180, 0, 0, 1);
      speed[0] = -speed[0];
      speed[1] = -speed[1];
    }
  }

  protected void display()
  {
    if( true ) {
      // clear console
      System.out.print("\033[H\033[2J");
      System.out.flush();

      camera.consoleDisplay();

    }// display info to console

    // start with empty soups for mutable soups
    if( mutableSoups != null )
      mutableSoups.cleanup();
    mutableSoups = new Soups( Pic.size() );

    // code to add all mutable triangles
//    pyramid.draw( mutableSoups );
    car1.draw( mutableSoups );
    car2.draw( mutableSoups );
    car3.draw( mutableSoups );
    car4.draw( mutableSoups );
//    launch.draw( mutableSoups );
    myCar.draw( mutableSoups );
    mutableSoups.sortByTexture();

    OpenGL.drawBackground();

    // draw the first-person view
    camera.activate();  // sets up viewport for first person
                        // and sends over proj and view matrices
                        // stored in camera
    frozenSoups.draw();
    mutableSoups.draw();

    // draw the map view
    mapViewer.activate();
    frozenSoups.draw();
    mutableSoups.draw();

  }

}// Ex10
