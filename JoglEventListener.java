package finalProject;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.Font;

import javax.media.opengl.*;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;
import javax.swing.JOptionPane;

import com.jogamp.opengl.util.awt.TextRenderer;

public class JoglEventListener implements GLEventListener, KeyListener, MouseListener, MouseMotionListener {
	private int windowWidth, windowHeight;
	private final int FLOOR_SIZE = 200;
	
	private TextureLoader texture_loader = null;
	private Skybox current_skybox = null;
	private Track track = null;
	private Car car = null;
	
	// AI OBJECTS
	private AI ai1 = null;
	private AI ai2 = null;
	private AI ai3 = null;
	
	private String lapS = "";
	private String time = "";
	private TextRenderer renderer = null;
	private final float skybox_size = 1000.0f;
	private int camera = 1;
	private final String[] skybox_names = {
		"TropicalSunnyDay", "ThickCloudsWater", 
		"DarkStormy", "FullMoon", 
		"SunSet", "CloudyLightRays"
	};
	// Making this larger will allocate more skybox textures to start, giving a
	// super slow startup, but allowing you to switch between them quickly.
	// Best to use a value of 1 for production code.
	private final int skybox_max_textures = 1;
	private Skybox[] skyboxes = new Skybox[ skybox_names.length ];
	
	private int lap = 1;
	private long startT = 0;
	private long currentT;
	private int seconds = 0; 
	private int minutes = 0;
	private boolean R = false;
	private boolean go = false;
	
	private float scene_eye_x = 10.0f;
	private float scene_eye_y = -69.0f;
	//private float scene_eye_x = 0.0f;
	//private float scene_eye_y = -63.0f;
	private float scene_eye_z = 1.5f;
	private float scene_look_x = 1.0f;
	private float scene_look_y = 0.0f;
	private float scene_look_z = 0.0f;
	
	// For testing 
	private float lscene_eye_x = 0;
	private float lscene_eye_y = 0;
	//private int mSeconds = 0;
	private int oldmSeconds = 0;
	private boolean record = false; 
	// end test
	
	private float fp_eye_x = 0.0f;
	private float fp_eye_y = 0.0f;
	private float fp_look_x = 1.0f;
	private float fp_look_y = 0.0f;
	
	private int mouse_x0 = 0;
	private int mouse_y0 = 0;
	
	private float angle = 0.0f;
	private float throttle_pan = 0.00f;
	private float multiplierA = 0.0f;
	private float multiplierB = 0.0f;
	private float turn = 0.0f;
	
	private int mouse_mode = 0;
	
	private final int MOUSE_MODE_NONE = 0;
	private final int MOUSE_MODE_ROTATE = 1;
	
	private boolean[] keys = new boolean[256];
	
	private GLU glu = new GLU();
	
	public void displayChanged( GLAutoDrawable gLDrawable, boolean modeChanged,
			boolean deviceChanged) { }

	@Override
	public void init( GLAutoDrawable gLDrawable ) {
		GL2 gl = gLDrawable.getGL().getGL2();
		gl.glClearColor( 0.0f, 0.0f, 0.0f, 1.0f );
		gl.glColor3f( 1.0f, 1.0f, 1.0f );
		gl.glClearDepth( 1.0f );
		gl.glEnable( GL.GL_DEPTH_TEST );
		gl.glDepthFunc( GL.GL_LEQUAL );
		gl.glEnable( GL.GL_TEXTURE_2D );
		
		// Initialize the texture loader and skybox.
		texture_loader = new TextureLoader( gl );
		
		for ( int i = 0; i < skybox_max_textures; ++i )
			skyboxes[ i ] = new Skybox( texture_loader, skybox_names[ i ] );
		
		current_skybox = skyboxes[ 0 ];
		
		// Initialize the track and buildings
		track = new Track(texture_loader, FLOOR_SIZE);
		
		renderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 44));
		startT = System.currentTimeMillis();
		
		// Initialize car
		car = new Car( texture_loader );
		
		// Initialize AI Cars
		ai1 = new AI( texture_loader, 1 );
		ai2 = new AI( texture_loader, 2 );
		ai3 = new AI( texture_loader, 3 );
		
		// Initialize the keys.
		for ( int i = 0; i < keys.length; ++i )
			keys[i] = false;
		
		gl.glMatrixMode( GLMatrixFunc.GL_MODELVIEW );
		gl.glLoadIdentity();
	}
	
	@Override
	public void reshape( GLAutoDrawable gLDrawable, int x, int y, int width, int height ) {
		windowWidth = width;
		windowHeight = height > 0 ? height : 1;
		
		final GL2 gl = gLDrawable.getGL().getGL2();
		
		gl.glViewport( 0, 0, width, height );
		gl.glMatrixMode( GLMatrixFunc.GL_PROJECTION );
		gl.glLoadIdentity();
		glu.gluPerspective( 60.0f, (float) windowWidth / windowHeight, 0.1f, skybox_size * (float) Math.sqrt( 3.0 ) / 2.0f );
	}
	
	@Override
	public void display( GLAutoDrawable gLDrawable ) {
		final GL2 gl = gLDrawable.getGL().getGL2();
		
		gl.glClear( GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT );
		
		gl.glMatrixMode( GLMatrixFunc.GL_MODELVIEW );
		gl.glPushMatrix();
		
		final float MAX_SPEED = 0.30f;
		final float MAX_TURN = 0.02f;
		
		if( camera == 1 || camera == 2 ) {
			scene_eye_z = 1.5f;
			scene_look_z = 0.0f;
			
			// FORWARD AND BACKWARD
			if ( keys[KeyEvent.VK_W] || keys[KeyEvent.VK_S] ) {
				float normxy = (float) Math.sqrt( scene_look_x * scene_look_x + scene_look_y * scene_look_y );
				multiplierA = keys[KeyEvent.VK_W] ? 1.0f : -1.0f;
				if( throttle_pan < MAX_SPEED ) {
					throttle_pan += 0.005f;
				}
				if(checkCollision(scene_eye_x + scene_look_x / normxy * throttle_pan * multiplierA, scene_eye_y + scene_look_y / normxy * throttle_pan * multiplierA)){
					scene_eye_x += scene_look_x / normxy * throttle_pan * multiplierA;
					scene_eye_y += scene_look_y / normxy * throttle_pan * multiplierA;
				}
			} else {
				float normxy = (float) Math.sqrt( scene_look_x * scene_look_x + scene_look_y * scene_look_y );
				if( throttle_pan > 0 ) {
					throttle_pan -= 0.005f;
				}
				if( throttle_pan <= 0.005f && throttle_pan >= -0.01f ) throttle_pan = 0.0f;
				if(checkCollision(scene_eye_x + scene_look_x / normxy * throttle_pan * multiplierA, scene_eye_y + scene_look_y / normxy * throttle_pan * multiplierA)){
					scene_eye_x += scene_look_x / normxy * throttle_pan * multiplierA;
					scene_eye_y += scene_look_y / normxy * throttle_pan * multiplierA;
				}
			}
			
			// TURNING
			if ( (keys[KeyEvent.VK_A] || keys[KeyEvent.VK_D]) && (throttle_pan != 0.0f) ) {
				multiplierB = keys[KeyEvent.VK_A] ? 1.0f : -1.0f;
				if( turn < MAX_TURN ) {
					turn += 0.001f;
				}
				angle += turn * multiplierB;
				scene_look_y = (float)Math.sin(angle);
				scene_look_x = (float)Math.cos(angle);
			} else {
				if( turn > 0 ) {
					turn -= 0.001f;
				}
				if( turn <= 0.001f && turn >= -0.01f ) turn = 0.0f;
				angle += turn * multiplierB;
				scene_look_y = (float)Math.sin(angle);
				scene_look_x = (float)Math.cos(angle);
			}
			if( go && (scene_eye_x>=-10.0f && scene_eye_x<=-9.7f) && (scene_eye_y>-70.0f && scene_eye_y<-60.0f)) {
				lap += 1;
				if(lap == 4) {
					JOptionPane.showMessageDialog(null, "Race complete! Time: " + time);
					go = !go;
				}
					/*renderer.beginRendering(windowWidth, windowHeight, true);
					renderer.setColor(1.0f, 1.0f, 1.0f, 1.0f);
					renderer.draw(lapS + " complete! Time: " + time, windowWidth*9/18, windowHeight*45/48);
					renderer.endRendering();*/
			}
			if(record && R && (scene_eye_x != lscene_eye_x || scene_eye_y != lscene_eye_y)) {
			System.out.println(scene_eye_x + "f, " + scene_eye_y + "f,");
			lscene_eye_x = scene_eye_x;
			lscene_eye_y = scene_eye_y;
			record = false;
			}
		}
		
		if( camera == 3 ) {
			// Update the camera state.
			final float free_throttle = 0.25f;
			if ( keys[KeyEvent.VK_W] || keys[KeyEvent.VK_S] ) {
				float normxy = (float) Math.sqrt( scene_look_x * scene_look_x + scene_look_y * scene_look_y );
				float multiplier = keys[KeyEvent.VK_W] ? 1.0f : -1.0f;
				scene_eye_x += scene_look_x / normxy * free_throttle * multiplier;
				scene_eye_y += scene_look_y / normxy * free_throttle * multiplier;
			} else {
				float normxy = (float) Math.sqrt( scene_look_x * scene_look_x + scene_look_y * scene_look_y );
				if( throttle_pan > 0 ) {
					throttle_pan -= 0.005f;
				}
				if( throttle_pan <= 0.005f && throttle_pan >= -0.01f ) throttle_pan = 0.0f;
				fp_eye_x += scene_look_x / normxy * throttle_pan * multiplierA;
				fp_eye_y += scene_look_y / normxy * throttle_pan * multiplierA;
			}
			
			if ( keys[KeyEvent.VK_R] ) {
				scene_eye_z += free_throttle;
			} else if ( keys[KeyEvent.VK_F] ) {
				if ( scene_eye_z > 0.5 ) {
					scene_eye_z -= free_throttle;
				}
			}
			
			if ( keys[KeyEvent.VK_A] || keys[KeyEvent.VK_D] ) {
				float theta = (float) Math.atan2( scene_look_y, scene_look_x );
				float phi = (float) Math.acos( scene_look_z );
				
				if ( keys[KeyEvent.VK_A] )
					theta += Math.PI / 2.0;
				else if ( keys[KeyEvent.VK_D] )
					theta -= Math.PI / 2.0;
				
				float strafe_x = (float)( Math.cos( theta ) * Math.sin( phi ) );
				float strafe_y = (float)( Math.sin( theta ) * Math.sin( phi ) );
				float normxy = (float) Math.sqrt( strafe_x * strafe_x + strafe_y * strafe_y );
				
				scene_eye_x += strafe_x / normxy * free_throttle;
				scene_eye_y += strafe_y / normxy * free_throttle;
			}
		}
		if(camera == 2) {
			glu.gluLookAt( scene_eye_x-scene_look_x*3, scene_eye_y-scene_look_y*3, scene_eye_z+1.5,
					scene_eye_x + scene_look_x, scene_eye_y + scene_look_y, scene_eye_z + scene_look_z-.5,
					0.0f, 0.0f, 1.0f );
		}
		else {
			glu.gluLookAt( scene_eye_x, scene_eye_y, scene_eye_z,
					scene_eye_x + scene_look_x, scene_eye_y + scene_look_y, scene_eye_z + scene_look_z,
					0.0f, 0.0f, 1.0f );
		}
		
		
		gl.glPushMatrix();
		gl.glTranslatef( scene_eye_x, scene_eye_y, scene_eye_z );
		current_skybox.draw( gl, skybox_size );
		gl.glPopMatrix();
		
		// Car matrix
		if( camera == 3 ) {
			gl.glPushMatrix();
			gl.glTranslatef(fp_eye_x, fp_eye_y, -0.025f);
			gl.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
			float rot = (float) (Math.atan2((fp_look_y),(fp_look_x)) * (180/Math.PI)) + 90;
				gl.glPushMatrix();
					gl.glRotatef(rot, 0.0f, 1.0f, 0.0f);
					car.drawCar(gl);
				gl.glPopMatrix();
			gl.glPopMatrix();
		}
		if (camera == 2) {
			gl.glPushMatrix();
			gl.glTranslatef(scene_eye_x+scene_look_x, scene_eye_y+scene_look_y, 0.0f);
			gl.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
			float rot = (float) (Math.atan2((scene_look_y),(scene_look_x)) * (180/Math.PI)) + 90;
				gl.glPushMatrix();
					gl.glRotatef(rot, 0.0f, 1.0f, 0.0f);
					car.drawCar(gl);
				gl.glPopMatrix();
			gl.glPopMatrix();
		}
		
		// AI Controller
		if( go ) {
			ai1.moveAI(gl);
			ai2.moveAI(gl);
			ai3.moveAI(gl);
			currentT = System.currentTimeMillis() - startT + oldmSeconds;
			seconds = (int)(currentT * 0.001);
			if(seconds > 59) {
				minutes += 1;
				seconds = seconds % 60;
				startT = System.currentTimeMillis();
			}
		} else {
			ai1.render(gl);
			ai2.render(gl);
			ai3.render(gl);
		}
		
		
		
		//for testing
		/*mSeconds = (int) currentT;
		if(mSeconds > oldmSeconds+500){
			record = true;
			oldmSeconds = mSeconds;}*/
		//end test
		
		
		
		time = minutes + ":";
		if(seconds < 10) time += "0" + seconds;
		else time += seconds;
		lapS = "Lap: " + lap;
		
		// Draw some trees
		track.drawTree(gl, 50f, 50f);
		track.drawTree(gl, 56f, 57f);
		track.drawTree(gl, -50f, 10f);
		track.drawTree(gl, -34f, 6f);
		track.drawTree(gl, -30f, -28f);
		track.drawTree(gl, -40f, -25f);
		track.drawTree(gl, -66f, -26f);
		
		// Draw track
		track.drawTrack(gl);
		track.drawSides1(gl);
		track.drawForest(gl);
		
		// Draw instrument panel
		if ( camera == 1 ) {
			car.drawPanel(gl, 0.6f, 0.4f, scene_eye_x, scene_eye_y, scene_look_x, scene_look_y, turn, multiplierB);
			// optionally set the color
			renderer.beginRendering(windowWidth, windowHeight, true);
			renderer.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			renderer.draw(lapS, windowWidth*5/18, windowHeight*10/48);
			renderer.draw(time, windowWidth*12/18, windowHeight*10/48);
			renderer.endRendering();
		} else {
			renderer.beginRendering(windowWidth, windowHeight, true);
			renderer.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			renderer.draw(lapS, windowWidth*1/18, windowHeight*45/48);
			renderer.draw(time, windowWidth*16/18, windowHeight*45/48);
			renderer.endRendering();
		}
		gl.glPopMatrix();
	}
	
	public boolean checkCollision(float x, float y) {
		final int collide = 4;
		if(Math.abs(x - ai1.ai_x) <= collide && Math.abs(y - ai1.ai_y) <= collide)
			return false;
		if(Math.abs(x - ai2.ai_x) <= collide && Math.abs(y - ai2.ai_y) <= collide)
			return false;
		if(Math.abs(x - ai3.ai_x) <= collide && Math.abs(y - ai3.ai_y) <= collide)
			return false;
		if(FLOOR_SIZE/2 - Math.abs(x) <= 3 || FLOOR_SIZE/2 - Math.abs(y) <= 3)
			return false;
		
		return true;
	}
	
	@Override
	public void dispose( GLAutoDrawable arg0 ) {
	}

	@Override
	public void keyTyped( KeyEvent e ) {
		char key = e.getKeyChar();
		
		switch ( key ) {
			case KeyEvent.VK_ESCAPE:
				System.exit(0);
			
			case '1':
				if(camera == 3) {
					scene_eye_x = fp_eye_x;
					scene_eye_y = fp_eye_y;
					scene_look_x = fp_look_x;
					scene_look_y = fp_look_y;
				}
				camera = 1;
				break;
				
			case '2':
				if(camera == 3) {
					scene_eye_x = fp_eye_x;
					scene_eye_y = fp_eye_y;
					scene_look_x = fp_look_x;
					scene_look_y = fp_look_y;
				}
				camera = 2;
				break;
				
			case '3':
				camera = 3;
				fp_eye_x = scene_eye_x;
				fp_eye_y = scene_eye_y;
				fp_look_x = scene_look_x;
				fp_look_y = scene_look_y;
				scene_eye_z = 2.0f;
				break;
				
			case 'r':
				R = !R;
				break;
				
			case 'g':
				if(!go) {
					startT = System.currentTimeMillis();
					//currentT += oldmSeconds*1000;
				}
				else oldmSeconds = (int) currentT;
				go = !go;
				break;
		}
	}

	@Override
	public void keyPressed( KeyEvent e ) {
		keys[ e.getKeyCode() ] = true;
	}

	@Override
	public void keyReleased( KeyEvent e ) {
		keys[ e.getKeyCode() ] = false;
	}

	@Override
	public void mouseDragged( MouseEvent e ) {
		int x = e.getX();
		int y = e.getY();
		
		final float throttle_rot = 128.0f;
		
		float dx = ( x - mouse_x0 );
		float dy = ( y - mouse_y0 );
		
		if ( camera == 3 ) {
			if ( MOUSE_MODE_ROTATE == mouse_mode ) {
				float phi = (float) Math.acos( scene_look_z );
				float theta = (float) Math.atan2( scene_look_y, scene_look_x );
				
				theta -= dx / throttle_rot;
				phi += dy / throttle_rot;
				
				if ( theta >= Math.PI * 2.0 )
					theta -= Math.PI * 2.0;
				else if ( theta < 0 )
					theta += Math.PI * 2.0;
				
				if ( phi > Math.PI - 0.1 )
					phi = (float)( Math.PI - 0.1 );
				else if ( phi < 0.1f )
					phi = 0.1f;
				
				scene_look_x = (float)( Math.cos( theta ) * Math.sin( phi ) );
				scene_look_y = (float)( Math.sin( theta ) * Math.sin( phi ) );
				scene_look_z = (float)( Math.cos( phi ) );
			}
			
			mouse_x0 = x;
			mouse_y0 = y;
		}
	}
	
	@Override
	public void mouseMoved( MouseEvent e ) {
	}

	@Override
	public void mouseClicked( MouseEvent e ) {
	}

	@Override
	public void mousePressed( MouseEvent e ) {
		mouse_x0 = e.getX();
		mouse_y0 = e.getY();
		
		if ( MouseEvent.BUTTON1 == e.getButton() ) {
			mouse_mode = MOUSE_MODE_ROTATE;
		} else {
			mouse_mode = MOUSE_MODE_NONE;
		}
	}

	@Override
	public void mouseReleased( MouseEvent e ) {
	}

	@Override
	public void mouseEntered( MouseEvent e ) {
	}

	@Override
	public void mouseExited( MouseEvent e ) {
	}
}