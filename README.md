# OpenGLFinal
Simple racing game implemented in Java with JOGL

This was written by myself and one partner as our final project for a graphics class.

## Design

The main part of our code takes place in the JOGLEventListener.java file
where our display method lives and where we create all of our objects. 
Our main.java file basically only exists to create the JOGLEventListener
object. Similar to how we've been organizing our homework all semester.

We also have several other classes. AI.java, Car.java, Skybox.java, 
TextureLoader.java, and track.java. Each one of these pretty much 
handles what the name implies that it would. All of our implementation
for the AI racers are done in the AI.java file and so on. Each one of these
creates a class that we then use in JOGLEvenListener to create an object
and manipulate it in various ways. We tried to keep the JOGLEventListener
as clean as possible when dealing with other objects by putting as much
as we could in their respective classes. 

The textures folder holds all of the image resources we used. The Moskvitch folder holds our car object files.

The parser and builder folders contain the necessary parts for the object loader we used, which was written by someone else.

## User Manual

The game begins in first person view. The buttons to switch views are:
	1: First person driving view
	2: Third Person driving view
	3: Free Camera roaming view

In first and third person driving views:
	W and S to go forward and backward
	A and D to steer left and right

In free view camera mode:
	W and S move forward and backward on the plane you’re on
	A and D move left and right on the plane
	R and F move up and down vertically
	Clicking and dragging the mouse rotates the view

Pressing 1 or 2 will return the camera to the location of your car

Press G to start the timer and AI movement

After 3 laps, an alert box pops up to say you completed the race, as well as show your time to complete 3 laps
After you close this alert, you can continue playing if you choose
