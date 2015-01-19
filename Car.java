package finalProject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import finalProject.builder.*;
import finalProject.parser.Parse;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

class Car {
	final int NUM_TEXTURES = 3;
	
	private int textures[] = new int[ NUM_TEXTURES ];
	
	protected TextureLoader texture_loader = null;
	public static final String PATH_TO_CAR = "src/finalProject/textures/";
	public static final String PATH_TO_TEX = "Moskvitch/Texture/moskvitch.png";
	public static final String PATH_TO_OBJ = "Moskvitch/Moskvitch.obj";
	public static final String PATH_TO_PANEL = "iPanel.jpg";
	public static final String PATH_TO_WHEEL = "wheel2.png";
	
	Build builder = new Build();
	
	public Car( TextureLoader texture_loader ) {
		this.texture_loader = texture_loader;
		loadTextures();
		
		try {
			new Parse(builder, PATH_TO_CAR + PATH_TO_OBJ);
			//parser.processMaterialLib(PATH_TO_CAR + "Moskvitch.mtl");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void loadTextures() {
		for ( int i = 0; i < NUM_TEXTURES; ++i ) 
			textures[ i ] = texture_loader.generateTexture();
		
		try {
			texture_loader.loadTexture( textures[ 0 ], PATH_TO_CAR
					+  PATH_TO_PANEL );
		} catch ( Exception e ) {
			System.err.println( "Unable to load texture: " + e.getMessage());
		}
		
		try {
			texture_loader.loadTexture( textures[ 1 ], PATH_TO_CAR
					+  PATH_TO_TEX );
		} catch ( Exception e ) {
			System.err.println( "Unable to load texture: " + e.getMessage());
		}
		
		try {
			texture_loader.loadTexture( textures[ 2 ], PATH_TO_CAR
					+  PATH_TO_WHEEL );
		} catch ( Exception e ) {
			System.err.println( "Unable to load texture: " + e.getMessage());
		}
	}
	
	void drawCar( GL2 gl ) {
		gl.glBindTexture( GL2.GL_TEXTURE_2D, textures[ 1 ] );
		gl.glColor3f( 1.0f, 1.0f, 1.0f );
		
		for(int i=0; i < builder.faces.size(); i++) {
			ArrayList<FaceVertex> vertices = builder.faces.get(i).vertices;
			gl.glBegin( GL2.GL_TRIANGLES );
			for(int j=0; j < vertices.size(); j++) {
				FaceVertex vertex = vertices.get(j);
				VertexTexture tex = vertex.t;
				gl.glTexCoord2d( tex.u, tex.v );
				gl.glVertex3f( vertex.v.x, vertex.v.y, vertex.v.z );
			}
			gl.glEnd();
		}
	}
	
	void moveTo(GL2 gl, float x, float y, float prev_x, float prev_y, double theta) {
		gl.glPushMatrix();
		gl.glTranslated(x, y, -.025f);
		gl.glRotatef(90f, 1f, 0f, 0f);
		x = x-prev_x;
		y = y-prev_y;
		gl.glPushMatrix();
		gl.glRotatef((float)(theta * 180/Math.PI)+90, 0f, 1f, 0f);
		drawCar(gl);
		gl.glPopMatrix();
		gl.glPopMatrix();
	}
	
	void drawPanel( GL2 gl, float width, float height, float scene_eye_x, float scene_eye_y, float scene_look_x, float scene_look_y, float turn, float dir) {
		final float MAX_TURN = 0.02f;
		gl.glBindTexture( GL2.GL_TEXTURE_2D, textures[ 0 ]);
		
		gl.glPushMatrix();
			float rot = (float) (Math.atan2((scene_look_y),(scene_look_x)) * (180/Math.PI)) + 45;
		
			gl.glTranslatef(scene_eye_x, scene_eye_y, 0.5f);
			gl.glTranslatef(scene_look_x, scene_look_y, 0.0f);
			
			gl.glPushMatrix();
				gl.glRotatef(rot, 0.0f, 0.0f, 1.0f);
				
				gl.glBindTexture( GL2.GL_TEXTURE_2D, textures[ 0 ]);
				gl.glBegin( GL2.GL_QUADS );

				gl.glTexCoord2f(0.0f, 0.0f);
				gl.glVertex3f( + width,  + width, .33f);

				gl.glTexCoord2f(1.0f, 0.0f);
				gl.glVertex3f( - width,  - width, .33f);

				gl.glTexCoord2f(1.0f, 1.0f);
				gl.glVertex3f( - width,  - width, .33f + height);

				gl.glTexCoord2f(0.0f, 1.0f);
				gl.glVertex3f( + width,  + width, .33f + height);
		
				gl.glEnd();
			gl.glPopMatrix();
		gl.glPopMatrix();
			
		gl.glPushMatrix();
			float ratio = turn * 35.0f/MAX_TURN;
			gl.glPushMatrix();
				gl.glTranslatef(scene_eye_x, scene_eye_y, 0.5f);
				gl.glTranslatef(scene_look_x, scene_look_y, 0.0f);
				
				gl.glPushMatrix();
					gl.glTranslatef(-0.1f*scene_look_x, -0.1f*scene_look_y, 0.0f);
					gl.glRotatef(rot, 0.0f, 0.0f, 1.0f);
					gl.glTranslatef(0.0f, 0.0f, 0.6f);
					
					gl.glPushMatrix();
						gl.glRotatef(-dir*ratio, 1.0f, -1.0f, 0.0f);
						gl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
						gl.glEnable(GL.GL_BLEND);
						gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
						gl.glEnable(GL2.GL_ALPHA_TEST);
						gl.glAlphaFunc(GL.GL_GREATER, 0);
	
						gl.glBindTexture( GL2.GL_TEXTURE_2D, textures[ 2 ]);
						gl.glBegin( GL2.GL_QUADS );
	
						gl.glTexCoord2f(0.0f, 0.0f);
						gl.glVertex3f( + width/4,  + width/4, -height/2);
	
						gl.glTexCoord2f(1.0f, 0.0f);
						gl.glVertex3f( - width/4,  - width/4, -height/2);
	
						gl.glTexCoord2f(1.0f, 1.0f);
						gl.glVertex3f( - width/4,  - width/4, height/2);
	
						gl.glTexCoord2f(0.0f, 1.0f);
						gl.glVertex3f( + width/4,  + width/4, height/2);
	
						gl.glEnd();
					gl.glPopMatrix();
				gl.glPopMatrix();
			gl.glPopMatrix();
		gl.glPopMatrix();
	}
}