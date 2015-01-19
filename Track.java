package finalProject;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

public class Track {
	float d = 0;
	final int NUM_TEXTURES = 4;
	final int TREE_COUNT = 50;
	final double[] TREES = new double[ TREE_COUNT * 2 ];
	
	private int textures[] = new int[ NUM_TEXTURES ];
	
	protected TextureLoader texture_loader = null;
	public static final String PATH_TO_TEXTURES = "src/finalProject/textures/";
	public static final String PATH_TO_GROUND = "vectorTrack.png";
	public static final String PATH_TO_SIDE = "skyline.png";
	public static final String PATH_TO_TREE = "smallTree.png";
	public static final String PATH_TO_BIG_CITY = "City_Texture.png";
	
	public Track( TextureLoader texture_loader , float size) {
		this.texture_loader = texture_loader;
		d = size/2;
		loadTextures();
		for( int i=0; i<TREE_COUNT; i++) {
			TREES[i] = 5;
		}
		for( int i=0; i<TREE_COUNT/4; i++) {
			double randX = Math.random() * -30 -60;
			double randY = Math.random() * 80 + 10;
			TREES[2*i] = randX;
			TREES[2*i+1] = randY;
		}
		for( int i=TREE_COUNT/4; i<2*TREE_COUNT/4; i++) {
			double randX = Math.random() * 180 -90;
			double randY = Math.random() * 9 + -90;
			TREES[2*i] = randX;
			TREES[2*i+1] = randY;
		}
		for( int i=2*TREE_COUNT/4; i<3*TREE_COUNT/4; i++) {
			double randX = Math.random() * 75 - 30;
			double randY = Math.random() * 20 + -50;
			TREES[2*i] = randX;
			TREES[2*i+1] = randY;
		}
		for( int i=3*TREE_COUNT/4; i<TREE_COUNT; i++) {
			double randX = Math.random() * 150 - 70;
			double randY = Math.random() * 15 + 80;
			TREES[2*i] = randX;
			TREES[2*i+1] = randY;
		}
	}
	
	protected void loadTextures() {
		for ( int i = 0; i < NUM_TEXTURES; ++i ) 
			textures[ i ] = texture_loader.generateTexture();
		
		try {
			texture_loader.loadTexture( textures[ 0 ], PATH_TO_TEXTURES
					+  PATH_TO_GROUND );
		} catch ( Exception e ) {
			System.err.println( "Unable to load texture: " + e.getMessage());
		}
		
		try {
			texture_loader.loadTexture( textures[ 1 ], PATH_TO_TEXTURES
					+  PATH_TO_SIDE );
		} catch ( Exception e ) {
			System.err.println( "Unable to load texture: " + e.getMessage());
		}
		
		try {
			texture_loader.loadTexture( textures[ 2 ], PATH_TO_TEXTURES
					+  PATH_TO_TREE );
		} catch ( Exception e ) {
			System.err.println( "Unable to load texture: " + e.getMessage());
		}
		
		try {
			texture_loader.loadTexture( textures[ 3 ], PATH_TO_TEXTURES
					+  PATH_TO_BIG_CITY );
		} catch ( Exception e ) {
			System.err.println( "Unable to load texture: " + e.getMessage());
		}
	}
	
	public void drawTree(GL2 gl, float x, float y) {
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL2.GL_ALPHA_TEST);
		gl.glAlphaFunc(GL.GL_GREATER, 0);
		
		gl.glBindTexture(GL2.GL_TEXTURE_2D, textures[2]);
		
		gl.glPushMatrix();
			gl.glTranslatef(x, y, 0.0f);
			
		float modelview[] = new float[16];
		int i, j;
		
		gl.glPushMatrix();
		
		gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, modelview, 0);
		
		for( i=0; i<3; i++ ) {
			for( j=0; j<3; j++ ) {
				if(i == j)
					modelview[i*4+j] = 1.0f;
				else
					modelview[i*4+j] = 0.0f;
			}
		}
		
		gl.glLoadMatrixf(modelview, 0);
			
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(0,0);gl.glVertex3f(-3.0f, 0.0f, 0.0f);
		gl.glTexCoord2f(1,0);gl.glVertex3f(3.0f, 0.0f, 0.0f);
		gl.glTexCoord2f(1,1);gl.glVertex3f(3.0f, 6.0f,  0.0f);
		gl.glTexCoord2f(0,1);gl.glVertex3f(-3.0f, 6.0f,  0.0f);
		gl.glEnd();
		
		gl.glPopMatrix();
		gl.glPopMatrix();
	}
	
	public void drawForest( GL2 gl ) {
		for( int i=0; i<TREE_COUNT; i++) {
			drawTree(gl, (float)TREES[2*i], (float)TREES[2*i+1]);
		}
	}
	
	public void drawTrack( GL2 gl ) {
		final float z = 0;
		
		// Tile 1
		gl.glBindTexture( GL2.GL_TEXTURE_2D, textures[ 0 ]);
		gl.glBegin(GL2.GL_QUADS );
		
		gl.glTexCoord2f( 0.0f, 1.0f);
		gl.glVertex3f(d, d, z);
		
		gl.glTexCoord2f( 1.0f, 1.0f );
		gl.glVertex3f(d, -d, z);
		
		gl.glTexCoord2f( 1.0f, 0.0f );
		gl.glVertex3f(-d, -d, z);
		
		gl.glTexCoord2f( 0.0f, 0.0f );
		gl.glVertex3f(-d, d, z);
		
		gl.glEnd();
		
		/*// Tile 2
		gl.glBindTexture( GL2.GL_TEXTURE_2D, textures[ 0 ]);
		gl.glBegin(GL2.GL_QUADS );
		
		gl.glTexCoord2f( 0.0f, 1.0f);
		gl.glVertex3f(d, 0, z);
		
		gl.glTexCoord2f( 1.0f, 1.0f );
		gl.glVertex3f(d, -d, z);
		
		gl.glTexCoord2f( 1.0f, 0.0f );
		gl.glVertex3f(0, -d, z);
		
		gl.glTexCoord2f( 0.0f, 0.0f );
		gl.glVertex3f(0, 0, z);
		
		gl.glEnd();
		
		// Tile 3
		gl.glBindTexture( GL2.GL_TEXTURE_2D, textures[ 0 ]);
		gl.glBegin(GL2.GL_QUADS );

		gl.glTexCoord2f( 0.0f, 1.0f);
		gl.glVertex3f(0, d, z);

		gl.glTexCoord2f( 1.0f, 1.0f );
		gl.glVertex3f(0, 0, z);

		gl.glTexCoord2f( 1.0f, 0.0f );
		gl.glVertex3f(-d, 0, z);

		gl.glTexCoord2f( 0.0f, 0.0f );
		gl.glVertex3f(-d, d, z);

		gl.glEnd();
		
		// Tile 4
		gl.glBindTexture( GL2.GL_TEXTURE_2D, textures[ 0 ]);
		gl.glBegin(GL2.GL_QUADS );

		gl.glTexCoord2f( 0.0f, 1.0f);
		gl.glVertex3f(0, 0, z);

		gl.glTexCoord2f( 1.0f, 1.0f );
		gl.glVertex3f(0, -d, z);

		gl.glTexCoord2f( 1.0f, 0.0f );
		gl.glVertex3f(-d, -d, z);

		gl.glTexCoord2f( 0.0f, 0.0f );
		gl.glVertex3f(-d, 0, z);

		gl.glEnd();*/
	}
	
	public void drawSides1( GL2 gl ) {
		final float height = 50;
		
		gl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL2.GL_ALPHA_TEST);
		gl.glAlphaFunc(GL.GL_GREATER, 0);
		
		// Front
		gl.glBindTexture( GL2.GL_TEXTURE_2D, textures[ 3 ] );
		gl.glBegin( GL2.GL_QUADS );
		
		gl.glTexCoord2f( 0.0f, 1.0f );
		gl.glVertex3f( d, d, height );
		
		gl.glTexCoord2f( 0.0f, 0.0f );
		gl.glVertex3f( d, d, 0 );
		
		gl.glTexCoord2f( 0.25f, 0.0f );
		gl.glVertex3f( d, -d, 0 );
		
		gl.glTexCoord2f( 0.25f, 1.0f );
		gl.glVertex3f( d, -d, height );
		
		gl.glEnd();
		
		// Left
		gl.glBindTexture( GL2.GL_TEXTURE_2D, textures[ 3 ] );
		gl.glBegin( GL2.GL_QUADS );
		
		gl.glTexCoord2f( 0.75f, 1.0f );
		gl.glVertex3f( -d, d, height );
		
		gl.glTexCoord2f( 0.75f, 0.0f );
		gl.glVertex3f( -d, d, 0 );
		
		gl.glTexCoord2f( 1.0f, 0.0f );
		gl.glVertex3f( d, d, 0 );
		
		gl.glTexCoord2f( 1.0f, 1.0f );
		gl.glVertex3f( d, d, height );
		
		gl.glEnd();
		
		// Right
		gl.glBindTexture( GL2.GL_TEXTURE_2D, textures[ 3 ] );
		gl.glBegin( GL2.GL_QUADS );
		
		gl.glTexCoord2f( 0.25f, 1.0f );
		gl.glVertex3f( d, -d, height );
		
		gl.glTexCoord2f( 0.25f, 0.0f );
		gl.glVertex3f( d, -d, 0 );
		
		gl.glTexCoord2f( 0.5f, 0.0f );
		gl.glVertex3f( -d, -d, 0 );
		
		gl.glTexCoord2f( 0.5f, 1.0f );
		gl.glVertex3f( -d, -d, height );
		
		gl.glEnd();
		
		// Back
		gl.glBindTexture( GL2.GL_TEXTURE_2D, textures[ 3 ] );
		gl.glBegin( GL2.GL_QUADS );
		
		gl.glTexCoord2f( 0.5f, 1.0f );
		gl.glVertex3f( -d, -d, height );
		
		gl.glTexCoord2f( 0.5f, 0.0f );
		gl.glVertex3f( -d, -d, 0 );
		
		gl.glTexCoord2f( 0.75f, 0.0f );
		gl.glVertex3f( -d, d, 0 );
		
		gl.glTexCoord2f( 0.75f, 1.0f );
		gl.glVertex3f( -d, d, height );
		
		gl.glEnd();
	}
	
	public void drawSides4( GL2 gl ) {
		final float height = 50;
		
		gl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL2.GL_ALPHA_TEST);
		gl.glAlphaFunc(GL.GL_GREATER, 0);
		
		// Front
		gl.glBindTexture( GL2.GL_TEXTURE_2D, textures[ 1 ] );
		gl.glBegin( GL2.GL_QUADS );
		
		gl.glTexCoord2f( 0.0f, 1.0f );
		gl.glVertex3f( d, d, height );
		
		gl.glTexCoord2f( 0.0f, 0.0f );
		gl.glVertex3f( d, d, 0 );
		
		gl.glTexCoord2f( 1.0f, 0.0f );
		gl.glVertex3f( d, -d, 0 );
		
		gl.glTexCoord2f( 1.0f, 1.0f );
		gl.glVertex3f( d, -d, height );
		
		gl.glEnd();
		
		/*gl.glBindTexture( GL2.GL_TEXTURE_2D, textures[ 1 ] );
		gl.glBegin( GL2.GL_QUADS );
		
		gl.glTexCoord2f( 0.0f, 1.0f );
		gl.glVertex3f( d, 0, height );
		
		gl.glTexCoord2f( 0.0f, 0.0f );
		gl.glVertex3f( d, 0, 0 );
		
		gl.glTexCoord2f( 1.0f, 0.0f );
		gl.glVertex3f( d, -d, 0 );
		
		gl.glTexCoord2f( 1.0f, 1.0f );
		gl.glVertex3f( d, -d, height );
		
		gl.glEnd();*/
		
		// Left
		gl.glBindTexture( GL2.GL_TEXTURE_2D, textures[ 1 ] );
		gl.glBegin( GL2.GL_QUADS );
		
		gl.glTexCoord2f( 0.0f, 1.0f );
		gl.glVertex3f( -d, d, height );
		
		gl.glTexCoord2f( 0.0f, 0.0f );
		gl.glVertex3f( -d, d, 0 );
		
		gl.glTexCoord2f( 1.0f, 0.0f );
		gl.glVertex3f( d, d, 0 );
		
		gl.glTexCoord2f( 1.0f, 1.0f );
		gl.glVertex3f( d, d, height );
		
		gl.glEnd();
		
		/*gl.glBindTexture( GL2.GL_TEXTURE_2D, textures[ 1 ] );
		gl.glBegin( GL2.GL_QUADS );
		
		gl.glTexCoord2f( 0.0f, 1.0f );
		gl.glVertex3f( 0, d, height );
		
		gl.glTexCoord2f( 0.0f, 0.0f );
		gl.glVertex3f( 0, d, 0 );
		
		gl.glTexCoord2f( 1.0f, 0.0f );
		gl.glVertex3f( d, d, 0 );
		
		gl.glTexCoord2f( 1.0f, 1.0f );
		gl.glVertex3f( d, d, height );
		
		gl.glEnd();*/
		
		// Right
		gl.glBindTexture( GL2.GL_TEXTURE_2D, textures[ 1 ] );
		gl.glBegin( GL2.GL_QUADS );
		
		gl.glTexCoord2f( 0.0f, 1.0f );
		gl.glVertex3f( d, -d, height );
		
		gl.glTexCoord2f( 0.0f, 0.0f );
		gl.glVertex3f( d, -d, 0 );
		
		gl.glTexCoord2f( 1.0f, 0.0f );
		gl.glVertex3f( -d, -d, 0 );
		
		gl.glTexCoord2f( 1.0f, 1.0f );
		gl.glVertex3f( -d, -d, height );
		
		gl.glEnd();
		
		/*gl.glBindTexture( GL2.GL_TEXTURE_2D, textures[ 1 ] );
		gl.glBegin( GL2.GL_QUADS );
		
		gl.glTexCoord2f( 0.0f, 1.0f );
		gl.glVertex3f( 0, -d, height );
		
		gl.glTexCoord2f( 0.0f, 0.0f );
		gl.glVertex3f( 0, -d, 0 );
		
		gl.glTexCoord2f( 1.0f, 0.0f );
		gl.glVertex3f( -d, -d, 0 );
		
		gl.glTexCoord2f( 1.0f, 1.0f );
		gl.glVertex3f( -d, -d, height );
		
		gl.glEnd();*/
		
		// Back
		gl.glBindTexture( GL2.GL_TEXTURE_2D, textures[ 1 ] );
		gl.glBegin( GL2.GL_QUADS );
		
		gl.glTexCoord2f( 0.0f, 1.0f );
		gl.glVertex3f( -d, -d, height );
		
		gl.glTexCoord2f( 0.0f, 0.0f );
		gl.glVertex3f( -d, -d, 0 );
		
		gl.glTexCoord2f( 1.0f, 0.0f );
		gl.glVertex3f( -d, d, 0 );
		
		gl.glTexCoord2f( 1.0f, 1.0f );
		gl.glVertex3f( -d, d, height );
		
		gl.glEnd();
		
		/*gl.glBindTexture( GL2.GL_TEXTURE_2D, textures[ 1 ] );
		gl.glBegin( GL2.GL_QUADS );
		
		gl.glTexCoord2f( 0.0f, 1.0f );
		gl.glVertex3f( -d, 0, height );
		
		gl.glTexCoord2f( 0.0f, 0.0f );
		gl.glVertex3f( -d, 0, 0 );
		
		gl.glTexCoord2f( 1.0f, 0.0f );
		gl.glVertex3f( -d, d, 0 );
		
		gl.glTexCoord2f( 1.0f, 1.0f );
		gl.glVertex3f( -d, d, height );
		
		gl.glEnd();*/
	}
	
	public void drawGround( GL2 gl ) {
		final float s = 3000;
		final float h = -1;
		gl.glColor3f(0.0f, 0.3f, 0.0f);
		
		gl.glBindTexture( GL2.GL_TEXTURE_2D, textures[ 0 ]);
		gl.glBegin( GL2.GL_QUADS );

		gl.glTexCoord2f( 0.0f, 1.0f );
		gl.glVertex3f( s, s, h );
		
		gl.glTexCoord2f( 0.0f, 0.0f );
		gl.glVertex3f( -s, s, h );
		
		gl.glTexCoord2f( 1.0f, 0.0f );
		gl.glVertex3f( -s, -s, h );
		
		gl.glTexCoord2f( 1.0f, 1.0f );
		gl.glVertex3f( s, -s, h );
		
		gl.glEnd();
	}
}