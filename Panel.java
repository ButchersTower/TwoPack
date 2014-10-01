package twoPack;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JPanel;

public class Panel extends JPanel implements Runnable, KeyListener {
	int width = 160;
	int height = 200;

	Image[] imageAr;

	Thread thread;
	Image image;
	Graphics g;

	// Vars for gLoop Below
	public int tps = 20;
	public int milps = 1000 / tps;
	long lastTick = 0;
	int sleepTime = 0;
	long lastSec = 0;
	int ticks = 0;
	long startTime;
	long runTime;
	private long nextTick = 0;
	private boolean running = false;

	// Vars for gLoop Above

	int[][] grid = new int[4][4];

	public Panel() {
		super();

		setPreferredSize(new Dimension(width, height));
		setFocusable(true);
		requestFocus();
	}

	public void addNotify() {
		super.addNotify();
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}

	public void run() {
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) image.getGraphics();
		this.setSize(new Dimension(width, height));

		addKeyListener(this);

		startTime = System.currentTimeMillis();

		gStart();
	}

	/**
	 * Methods go below here.
	 * 
	 */

	public void gStart() {
		imageInit();

		running = true;
		gLoop();
	}

	public void gLoop() {
		while (running) {
			// Do the things you want the gLoop to do below here

			for (int w = 0; w < grid.length; w++) {
				for (int h = 0; h < grid[w].length; h++) {
					if (grid[w][h] == 0) {
						g.setColor(Color.WHITE);
						g.fillRect(w * 32, h * 32, 32, 32);
					}
					if (grid[w][h] == 1) {
						g.setColor(Color.BLUE);
						g.fillRect(w * 32, h * 32, 32, 32);
					}
					if (grid[w][h] == 2) {
						g.setColor(Color.RED);
						g.fillRect(w * 32, h * 32, 32, 32);
					}
				}
			}

			// Draws outline between tiles

			g.setColor(Color.BLACK);
			g.drawLine(32, 0, 32, 128);
			g.drawLine(64, 0, 64, 128);
			g.drawLine(96, 0, 96, 128);

			g.drawLine(0, 32, 128, 32);
			g.drawLine(0, 64, 128, 64);
			g.drawLine(0, 96, 128, 96);

			// And above here.
			drwGm(g);

			ticks++;
			// Runs once a second and keeps track of ticks;
			// 1000 ms since last output
			if (timer() - lastSec > 1000) {
				if (ticks < tps - 1 || ticks > tps + 1) {
					if (timer() - startTime < 2000) {
						System.out.println("Ticks this second: " + ticks);
						System.out.println("timer(): " + timer());
						System.out.println("nextTick: " + nextTick);
					}
				}

				ticks = 0;
				lastSec = (System.currentTimeMillis() - startTime);
			}

			// Used to protect the game from falling beind.
			if (nextTick < timer()) {
				nextTick = timer() + milps;
			}

			// Limits the ticks per second
			if (timer() - nextTick < 0) {
				sleepTime = (int) (nextTick - timer());
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
				}

				nextTick += milps;
			}
		}
	}

	Random rand = new Random();

	void addRandomTile() {
		ArrayList<int[]> openTiles = new ArrayList<int[]>();
		for (int w = 0; w < grid.length; w++) {
			for (int h = 0; h < grid[w].length; h++) {
				if (grid[w][h] == 0) {
					openTiles.add(new int[] { w, h });
				}
			}
		}
		if (openTiles.size() == 0) {
			System.out.println("YOU LOSE FUCKER");
		} else {
			int randTile = rand.nextInt(openTiles.size());
			grid[openTiles.get(randTile)[0]][openTiles.get(randTile)[1]] = 1;
		}
	}

	void slideTiles() {
		// RIGHT

		System.out.println("*****");
		int[] numTilesOnRow = new int[4];

		for (int h = 3; h >= 0; h--) {
			for (int w = 3; w >= 0; w--) {
				if (grid[w][h] != 0) {
					numTilesOnRow[h] += 1;

				}
			}
		}

		// NOW NEED TO COMBINE
		// If tower directly before the sliding = the same then simple delete
		// the slider and double the other.

		// If there is a tile on the row that is not the same type then simple
		// move the tile

		// runs throught all the rows
		for (int i = 0; i < numTilesOnRow.length; i++) {
			// ignore n amount of things
			// this gets ran once for each tile on that row
			for (int n = 0; n < numTilesOnRow[i]; n++) {
				int towersBefore = n;
				int towerTypeDirectlyBefore = -1;
				System.out.println("ran");
				// find the tile closest to the wall and move it nextto the
				// wall.
				boolean tileFound = false;
				boolean combined = false;
				int tileX = -1;
				for (int w = 3; w >= 0; w--) {
					System.out.println("i: " + i);
					if (grid[w][i] != 0) {
						if (!tileFound) {
							if (towersBefore > 0) {
								towersBefore--;
								towerTypeDirectlyBefore = grid[w][i];
							} else {
								if (towerTypeDirectlyBefore == 1) {
									System.out.println("COMBINE");
									combined = true;
									grid[w][i] = 0;
									grid[4 - n][i] = 2;
								} else {
									tileFound = true;
									tileX = w;
								}
							}
						}
					}
				}
				
				if (tileFound) {
					if (tileX != 3) {
						grid[tileX][i] = 0;
						if (!combined) {
							int openSpacesOnThisRow = 0;
							System.out.println("tileX: "+tileX);
							// need to figure out the space that the figure can move.
							for (int zz = tileX; zz < 3; zz++) {
								System.out.println("zz: " + zz);
								if (grid[zz][i] == 0) {
									openSpacesOnThisRow++;
								}
							}
							grid[tileX + openSpacesOnThisRow][i] = 1;
						}
					} else {
						System.out.println("tile Not Found");
					}
				}
			}
		}

		/*
		 * 
		 * System.out.println("*****"); for (int h = 3; h >= 0; h--) { boolean
		 * tileOnThisRow = false; int tilesAfter = 0; for (int w = 3; w >= 0;
		 * w--) { boolean tileRightHere = false; if (grid[w][h] == 1) { //
		 * Record how many spaces there are to the right of this. // How do i do
		 * that. System.out.println("tile at: (" + w + ", " + h + ")");
		 * tileOnThisRow = true; tileRightHere = true; } else { if
		 * (!tileOnThisRow) { tilesAfter++; } } if (tileRightHere) {
		 * System.out.println("tilesAfter: " + tilesAfter); } } }
		 */
	}

	/**
	 * Methods go above here.
	 * 
	 */

	public long timer() {
		return System.currentTimeMillis() - startTime;

	}

	public void drwGm(Graphics g) {
		Graphics g2 = this.getGraphics();
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
	}

	public void imageInit() {
		/**
		 * imageAr = new Image[1]; ImageIcon ie = new
		 * ImageIcon(this.getClass().getResource( "res/image.png")); imageAr[0]
		 * = ie.getImage();
		 */

	}

	@Override
	public void keyPressed(KeyEvent ke) {
		if (ke.getKeyCode() == KeyEvent.VK_SPACE) {
			addRandomTile();
		}
		if (ke.getKeyCode() == KeyEvent.VK_RIGHT) {
			slideTiles();
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}
}
