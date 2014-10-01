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

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class PanelNew extends JPanel implements Runnable, KeyListener {
	// Dont allow the block that was just combined to combine twice, i notice it
	// while going down.

	int width = 160;
	int height = 200;

	int score = 0;

	Image[] imageAr;

	Thread thread;
	Image image;
	Graphics g;

	int turnTime = 100;
	Image[] txtAr;

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

	public PanelNew() {
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
					} else if (grid[w][h] == 1) {
						g.setColor(Color.BLUE);
						g.fillRect(w * 32, h * 32, 32, 32);
					} else if (grid[w][h] == 2) {
						g.setColor(Color.RED);
						g.fillRect(w * 32, h * 32, 32, 32);
					} else if (grid[w][h] == 3) {
						g.setColor(Color.YELLOW);
						g.fillRect(w * 32, h * 32, 32, 32);
					} else if (grid[w][h] == 4) {
						g.setColor(Color.GREEN);
						g.fillRect(w * 32, h * 32, 32, 32);
					} else if (grid[w][h] == 5) {
						g.setColor(Color.PINK);
						g.fillRect(w * 32, h * 32, 32, 32);
					} else {
						g.setColor(Color.WHITE);
						g.fillRect(w * 32, h * 32, 32, 32);
					}
					int[] b = converter(Integer.toString((int) (Math.pow(2,
							grid[w][h]))));
					for (int c = 0; c < b.length; c++) {
						if (grid[w][h] != 0) {
							if (b.length == 1) {
								g.drawImage(txtAr[b[c]], (w * 32) + 10,
										(h * 32) + 10, null);
							} else if (b.length == 2) {
								g.drawImage(txtAr[b[c]], (c * 12) + (w * 32)
										+ 4, (h * 32) + 10, null);
							} else if (b.length == 3) {
								if (c < 2) {
									g.drawImage(txtAr[b[c]], (c * 12)
											+ (w * 32) + 4, (h * 32) - 6 + 10,
											null);
								}
								if (c == 2) {
									g.drawImage(txtAr[b[c]],
											(9) + (w * 32) + 4,
											(h * 32) + 6 + 10, null);
								}
							}
						}
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

			g.setColor(Color.WHITE);
			g.fillRect(10, 140, 200, 20);
			int[] b = converter(Integer.toString(score));
			for (int c = 0; c < b.length; c++) {
				g.drawImage(txtAr[b[c]], 10 + (c * 12), 140, null);
			}

			// And above here.
			drwGm(g);

			ticks++;
			// Runs once a second and keeps track of ticks;
			// 1000 ms since last output
			if (timer() - lastSec > 1000) {
				if (ticks < tps - 1 || ticks > tps + 1) {
					if (timer() - startTime < 200) {
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
		int which = rand.nextInt(2);
		if (openTiles.size() == 0) {
			System.out.println("YOU LOSE FUCKER");
		} else {
			int randTile = rand.nextInt(openTiles.size());
			if (which == 0) {
				grid[openTiles.get(randTile)[0]][openTiles.get(randTile)[1]] = 1;
			} else if (which == 1) {
				grid[openTiles.get(randTile)[0]][openTiles.get(randTile)[1]] = 2;
			}
		}
	}

	void slideTiles(int dir) {
		boolean thereWasAMove = false;
		if (dir == 0) {
			for (int w = 0; w < 4; w++) {
				if (upColCheck(w)) {
					thereWasAMove = true;
				}
			}
		} else if (dir == 1) {
			for (int h = 0; h < 4; h++) {
				if (rightRowCheck(h)) {
					thereWasAMove = true;
				}
			}
		} else if (dir == 2) {
			for (int w = 0; w < 4; w++) {
				if (downColCheck(w)) {
					thereWasAMove = true;
				}
			}
		} else if (dir == 3) {
			for (int h = 0; h < 4; h++) {
				if (leftRowCheck(h)) {
					thereWasAMove = true;
				}
			}
		}
		if (thereWasAMove) {
			addRandomTile();
		} else {
			System.out.println("there was not a mvoe");
		}
	}

	// Do not a let a block just created (from combination) be combined.
	// could make an boolean[4][4] for combinable.
	void goUp() {
		// If there was a move then run the whole go over thing again.
		// But allow for time inbetween for animation.
		// from right to left
		int runTimes = 0;
		boolean thereWasAMove = true;
		while (thereWasAMove) {
			thereWasAMove = false;
			for (int a = 3; a >= 0; a--) {
				for (int b = 1; b < 4; b++) {
					// block (2, 0) and try to drag right.
					if (grid[a][b] != 0) {
						// This is a tile
						if (grid[a][b - 1] == 0) {
							// nexto it is moveable
							grid[a][b - 1] = grid[a][b];
							grid[a][b] = 0;
							thereWasAMove = true;
						} else if (grid[a][b - 1] == grid[a][b]) {
							System.out.println("combine");
							grid[a][b - 1] += 1;
							grid[a][b] = 0;
							score -= Math.pow(2, grid[a][b - 1]);
							thereWasAMove = true;
						}
					}
				}
			}
			try {
				Thread.sleep(turnTime);
			} catch (Exception ex) {

			}
			runTimes++;
		}
		if (runTimes > 1) {
			addRandomTile();
		}
	}

	void goRight() {
		// If there was a move then run the whole go over thing again.
		// But allow for time inbetween for animation.
		// from right to left
		int runTimes = 0;
		boolean thereWasAMove = true;
		while (thereWasAMove) {
			thereWasAMove = false;
			for (int a = 2; a >= 0; a--) {
				for (int b = 0; b < 4; b++) {
					// block (2, 0) and try to drag right.
					if (grid[a][b] != 0) {
						// This is a tile
						if (grid[a + 1][b] == 0) {
							// nexto it is moveable
							grid[a + 1][b] = grid[a][b];
							grid[a][b] = 0;
							thereWasAMove = true;
						} else if (grid[a + 1][b] == grid[a][b]) {
							System.out.println("combine");
							grid[a + 1][b] += 1;
							grid[a][b] = 0;
							score += Math.pow(2, grid[a + 1][b]);
							thereWasAMove = true;
						}
					}
				}
			}
			try {
				Thread.sleep(turnTime);
			} catch (Exception ex) {

			}
			runTimes++;
		}
		if (runTimes > 1) {
			addRandomTile();
		}
	}

	void goDown() {
		// If there was a move then run the whole go over thing again.
		// But allow for time inbetween for animation.
		// from right to left
		int runTimes = 0;
		boolean thereWasAMove = true;
		while (thereWasAMove) {
			thereWasAMove = false;
			for (int a = 3; a >= 0; a--) {
				for (int b = 2; b >= 0; b--) {
					// block (2, 0) and try to drag right.
					if (grid[a][b] != 0) {
						// This is a tile
						if (grid[a][b + 1] == 0) {
							// nexto it is moveable
							grid[a][b + 1] = grid[a][b];
							grid[a][b] = 0;
							thereWasAMove = true;
						} else if (grid[a][b + 1] == grid[a][b]) {
							System.out.println("combine");
							grid[a][b + 1] += 1;
							grid[a][b] = 0;
							score += Math.pow(2, grid[a][b + 1]);
							thereWasAMove = true;
						}
					}
				}
			}
			try {
				Thread.sleep(turnTime);
			} catch (Exception ex) {

			}
			runTimes++;
		}
		if (runTimes > 1) {
			addRandomTile();
		}
	}

	void goLeft() {
		// If there was a move then run the whole go over thing again.
		// But allow for time inbetween for animation.
		// from right to left
		//
		int runTimes = 0;
		boolean thereWasAMove = true;
		while (thereWasAMove) {
			thereWasAMove = false;
			for (int a = 1; a < 4; a++) {
				for (int b = 0; b < 4; b++) {
					// block (2, 0) and try to drag right.
					if (grid[a][b] != 0) {
						// This is a tile
						if (grid[a - 1][b] == 0) {
							// nexto it is moveable
							grid[a - 1][b] = grid[a][b];
							grid[a][b] = 0;
							thereWasAMove = true;
						} else if (grid[a - 1][b] == grid[a][b]) {
							System.out.println("combine");
							grid[a - 1][b] += 1;
							grid[a][b] = 0;
							score -= Math.pow(2, grid[a - 1][b]);
							thereWasAMove = true;
						}
					}
				}
			}
			try {
				Thread.sleep(turnTime);
			} catch (Exception ex) {

			}
			runTimes++;
		}
		if (runTimes > 1) {
			addRandomTile();
		}
	}

	boolean rightRowCheck(int row) {
		boolean thereWasAMove = false;
		boolean moved = true;
		while (moved) {
			// System.out.println("right");
			moved = false;
			for (int w = 2; w >= 0; w--) {
				if (grid[w][row] != 0) {
					// This is a tile
					if (grid[w + 1][row] == 0) {
						// nexto it is moveable
						grid[w + 1][row] = grid[w][row];
						grid[w][row] = 0;
						moved = true;
						thereWasAMove = true;
						try {
							Thread.sleep(turnTime);
						} catch (Exception ex) {

						}
					} else if (grid[w + 1][row] == grid[w][row]) {
						System.out.println("combine");
						grid[w + 1][row] += 1;
						grid[w][row] = 0;
						score += Math.pow(2, grid[w + 1][row]);
						// System.out.println("ad score: " + grid[w + 1][row]);
						thereWasAMove = true;
						try {
							Thread.sleep(turnTime);
						} catch (Exception ex) {

						}
					}
				}
			}
		}
		return thereWasAMove;
	}

	boolean leftRowCheck(int row) {
		boolean thereWasAMove = false;
		boolean moved = true;
		while (moved) {
			// System.out.println("right");
			moved = false;
			for (int w = 1; w <= 3; w++) {
				if (grid[w][row] != 0) {
					// This is a tile
					if (grid[w - 1][row] == 0) {
						// nexto it is moveable
						grid[w - 1][row] = grid[w][row];
						grid[w][row] = 0;
						moved = true;
						thereWasAMove = true;
						try {
							Thread.sleep(turnTime);
						} catch (Exception ex) {

						}
					} else if (grid[w - 1][row] == grid[w][row]) {
						System.out.println("combine");
						grid[w - 1][row] += 1;
						grid[w][row] = 0;
						score += Math.pow(2, grid[w - 1][row]);
						thereWasAMove = true;
						try {
							Thread.sleep(turnTime);
						} catch (Exception ex) {

						}
					}
				}
			}
		}
		return thereWasAMove;
	}

	boolean downColCheck(int col) {
		boolean thereWasAMove = false;
		boolean moved = true;
		while (moved) {
			// System.out.println("right");
			moved = false;
			for (int h = 2; h >= 0; h--) {
				if (grid[col][h] != 0) {
					// This is a tile
					if (grid[col][h + 1] == 0) {
						// nexto it is moveable
						grid[col][h + 1] = grid[col][h];
						grid[col][h] = 0;
						moved = true;
						thereWasAMove = true;
						try {
							Thread.sleep(turnTime);
						} catch (Exception ex) {

						}
					} else if (grid[col][h + 1] == grid[col][h]) {
						System.out.println("combine");
						grid[col][h + 1] += 1;
						grid[col][h] = 0;
						score += Math.pow(2, grid[col][h + 1]);
						thereWasAMove = true;
						try {
							Thread.sleep(turnTime);
						} catch (Exception ex) {

						}
					}
				}
			}
		}
		return thereWasAMove;
	}

	boolean upColCheck(int col) {
		boolean thereWasAMove = false;
		boolean moved = true;
		while (moved) {
			moved = false;
			for (int h = 1; h <= 3; h++) {
				if (grid[col][h] != 0) {
					// This is a tile
					if (grid[col][h - 1] == 0) {
						// nexto it is moveable
						grid[col][h - 1] = grid[col][h];
						grid[col][h] = 0;
						score += Math.pow(2, grid[col][h - 1]);
						moved = true;
						thereWasAMove = true;
						try {
							Thread.sleep(turnTime);
						} catch (Exception ex) {

						}
					} else if (grid[col][h - 1] == grid[col][h]) {
						System.out.println("combine");
						grid[col][h - 1] += 1;
						grid[col][h] = 0;
						thereWasAMove = true;
						try {
							Thread.sleep(turnTime);
						} catch (Exception ex) {

						}
					}
				}
			}
		}
		return thereWasAMove;
	}

	/**
	 * Methods go above here.
	 * 
	 */

	public static int[] converter(String st) {
		int a = st.length();
		int[] nw = new int[a];

		for (int b = 0; b < a; b++) {
			if (st.charAt(b) == 'a') {
				nw[b] = 0;
			} else if (st.charAt(b) == 'A') {
				nw[b] = 0;
			} else if (st.charAt(b) == 'b') {
				nw[b] = 1;
			} else if (st.charAt(b) == 'B') {
				nw[b] = 1;
			} else if (st.charAt(b) == 'c') {
				nw[b] = 2;
			} else if (st.charAt(b) == 'C') {
				nw[b] = 2;
			} else if (st.charAt(b) == 'd') {
				nw[b] = 3;
			} else if (st.charAt(b) == 'D') {
				nw[b] = 3;
			} else if (st.charAt(b) == 'e') {
				nw[b] = 4;
			} else if (st.charAt(b) == 'E') {
				nw[b] = 4;
			} else if (st.charAt(b) == 'f') {
				nw[b] = 5;
			} else if (st.charAt(b) == 'F') {
				nw[b] = 5;
			} else if (st.charAt(b) == 'g') {
				nw[b] = 6;
			} else if (st.charAt(b) == 'G') {
				nw[b] = 6;
			} else if (st.charAt(b) == 'h') {
				nw[b] = 7;
			} else if (st.charAt(b) == 'H') {
				nw[b] = 7;
			} else if (st.charAt(b) == 'i') {
				nw[b] = 8;
			} else if (st.charAt(b) == 'I') {
				nw[b] = 8;
			} else if (st.charAt(b) == 'j') {
				nw[b] = 9;
			} else if (st.charAt(b) == 'J') {
				nw[b] = 9;
			} else if (st.charAt(b) == 'k') {
				nw[b] = 10;
			} else if (st.charAt(b) == 'K') {
				nw[b] = 10;
			} else if (st.charAt(b) == 'l') {
				nw[b] = 11;
			} else if (st.charAt(b) == 'L') {
				nw[b] = 11;
			} else if (st.charAt(b) == 'm') {
				nw[b] = 12;
			} else if (st.charAt(b) == 'M') {
				nw[b] = 12;
			} else if (st.charAt(b) == 'n') {
				nw[b] = 13;
			} else if (st.charAt(b) == 'N') {
				nw[b] = 13;
			} else if (st.charAt(b) == 'o') {
				nw[b] = 14;
			} else if (st.charAt(b) == 'O') {
				nw[b] = 14;
			} else if (st.charAt(b) == 'p') {
				nw[b] = 15;
			} else if (st.charAt(b) == 'P') {
				nw[b] = 15;
			} else if (st.charAt(b) == 'q') {
				nw[b] = 16;
			} else if (st.charAt(b) == 'Q') {
				nw[b] = 16;
			} else if (st.charAt(b) == 'r') {
				nw[b] = 17;
			} else if (st.charAt(b) == 'R') {
				nw[b] = 17;
			} else if (st.charAt(b) == 's') {
				nw[b] = 18;
			} else if (st.charAt(b) == 'S') {
				nw[b] = 18;
			} else if (st.charAt(b) == 't') {
				nw[b] = 19;
			} else if (st.charAt(b) == 'T') {
				nw[b] = 19;
			} else if (st.charAt(b) == 'u') {
				nw[b] = 20;
			} else if (st.charAt(b) == 'U') {
				nw[b] = 20;
			} else if (st.charAt(b) == 'v') {
				nw[b] = 21;
			} else if (st.charAt(b) == 'V') {
				nw[b] = 21;
			} else if (st.charAt(b) == 'w') {
				nw[b] = 22;
			} else if (st.charAt(b) == 'W') {
				nw[b] = 22;
			} else if (st.charAt(b) == 'x') {
				nw[b] = 23;
			} else if (st.charAt(b) == 'X') {
				nw[b] = 23;
			} else if (st.charAt(b) == 'y') {
				nw[b] = 24;
			} else if (st.charAt(b) == 'Y') {
				nw[b] = 24;
			} else if (st.charAt(b) == 'z') {
				nw[b] = 25;
			} else if (st.charAt(b) == 'Z') {
				nw[b] = 25;
			} else if (st.charAt(b) == ' ') {
				nw[b] = 26;
			} else if (st.charAt(b) == '0') {
				nw[b] = 27;
			} else if (st.charAt(b) == '1') {
				nw[b] = 28;
			} else if (st.charAt(b) == '2') {
				nw[b] = 29;
			} else if (st.charAt(b) == '3') {
				nw[b] = 30;
			} else if (st.charAt(b) == '4') {
				nw[b] = 31;
			} else if (st.charAt(b) == '5') {
				nw[b] = 32;
			} else if (st.charAt(b) == '6') {
				nw[b] = 33;
			} else if (st.charAt(b) == '7') {
				nw[b] = 34;
			} else if (st.charAt(b) == '8') {
				nw[b] = 35;
			} else if (st.charAt(b) == '9') {
				nw[b] = 36;
			} else if (st.charAt(b) == ',') {
				nw[b] = 37;
			} else if (st.charAt(b) == '?') {
				nw[b] = 38;
			} else if (st.charAt(b) == '¿') {
				nw[b] = 39;
			} else if (st.charAt(b) == '(') {
				nw[b] = 40;
			} else if (st.charAt(b) == ')') {
				nw[b] = 41;
			} else if (st.charAt(b) == 'é') {
				nw[b] = 4;
			} else if (st.charAt(b) == 'á') {
				nw[b] = 0;
			} else if (st.charAt(b) == 'ó') {
				nw[b] = 14;
			} else if (st.charAt(b) == 'í') {
				nw[b] = 8;
			} else if (st.charAt(b) == '.') {
				nw[b] = 26;
			}

		}
		return nw;
	}

	public long timer() {
		return System.currentTimeMillis() - startTime;

	}

	public void drwGm(Graphics g) {
		Graphics g2 = this.getGraphics();
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
	}

	public void imageInit() {
		txtAr = new Image[43];
		ImageIcon ii = new ImageIcon(this.getClass().getResource(
				"res/font/tx/cA.png"));
		txtAr[0] = ii.getImage();
		ii = new ImageIcon(this.getClass().getResource("res/font/tx/cB.png"));
		txtAr[1] = ii.getImage();
		ii = new ImageIcon(this.getClass().getResource("res/font/tx/cC.png"));
		txtAr[2] = ii.getImage();
		ii = new ImageIcon(this.getClass().getResource("res/font/tx/cD.png"));
		txtAr[3] = ii.getImage();
		ii = new ImageIcon(this.getClass().getResource("res/font/tx/cE.png"));
		txtAr[4] = ii.getImage();
		ii = new ImageIcon(this.getClass().getResource("res/font/tx/cF.png"));
		txtAr[5] = ii.getImage();
		ii = new ImageIcon(this.getClass().getResource("res/font/tx/cG.png"));
		txtAr[6] = ii.getImage();
		ii = new ImageIcon(this.getClass().getResource("res/font/tx/cH.png"));
		txtAr[7] = ii.getImage();
		ii = new ImageIcon(this.getClass().getResource("res/font/tx/cI.png"));
		txtAr[8] = ii.getImage();
		ii = new ImageIcon(this.getClass().getResource("res/font/tx/cJ.png"));
		txtAr[9] = ii.getImage();
		ii = new ImageIcon(this.getClass().getResource("res/font/tx/cK.png"));
		txtAr[10] = ii.getImage();
		ii = new ImageIcon(this.getClass().getResource("res/font/tx/cL.png"));
		txtAr[11] = ii.getImage();
		ii = new ImageIcon(this.getClass().getResource("res/font/tx/cM.png"));
		txtAr[12] = ii.getImage();
		ii = new ImageIcon(this.getClass().getResource("res/font/tx/cN.png"));
		txtAr[13] = ii.getImage();
		ii = new ImageIcon(this.getClass().getResource("res/font/tx/cO.png"));
		txtAr[14] = ii.getImage();
		ii = new ImageIcon(this.getClass().getResource("res/font/tx/cP.png"));
		txtAr[15] = ii.getImage();
		ii = new ImageIcon(this.getClass().getResource("res/font/tx/cQ.png"));
		txtAr[16] = ii.getImage();
		ii = new ImageIcon(this.getClass().getResource("res/font/tx/cR.png"));
		txtAr[17] = ii.getImage();
		ii = new ImageIcon(this.getClass().getResource("res/font/tx/cS.png"));
		txtAr[18] = ii.getImage();
		ii = new ImageIcon(this.getClass().getResource("res/font/tx/cT.png"));
		txtAr[19] = ii.getImage();
		ii = new ImageIcon(this.getClass().getResource("res/font/tx/cU.png"));
		txtAr[20] = ii.getImage();
		ii = new ImageIcon(this.getClass().getResource("res/font/tx/cV.png"));
		txtAr[21] = ii.getImage();
		ii = new ImageIcon(this.getClass().getResource("res/font/tx/cW.png"));
		txtAr[22] = ii.getImage();
		ii = new ImageIcon(this.getClass().getResource("res/font/tx/cX.png"));
		txtAr[23] = ii.getImage();
		ii = new ImageIcon(this.getClass().getResource("res/font/tx/cY.png"));
		txtAr[24] = ii.getImage();
		ii = new ImageIcon(this.getClass().getResource("res/font/tx/cZ.png"));
		txtAr[25] = ii.getImage();
		ii = new ImageIcon(this.getClass()
				.getResource("res/font/tx/cSpace.png"));
		txtAr[26] = ii.getImage();

		ii = new ImageIcon(this.getClass().getResource("res/font/tx/n0.png"));
		txtAr[27] = ii.getImage();
		ii = new ImageIcon(this.getClass().getResource("res/font/tx/n1.png"));
		txtAr[28] = ii.getImage();
		ii = new ImageIcon(this.getClass().getResource("res/font/tx/n2.png"));
		txtAr[29] = ii.getImage();
		ii = new ImageIcon(this.getClass().getResource("res/font/tx/n3.png"));
		txtAr[30] = ii.getImage();
		ii = new ImageIcon(this.getClass().getResource("res/font/tx/n4.png"));
		txtAr[31] = ii.getImage();
		ii = new ImageIcon(this.getClass().getResource("res/font/tx/n5.png"));
		txtAr[32] = ii.getImage();
		ii = new ImageIcon(this.getClass().getResource("res/font/tx/n6.png"));
		txtAr[33] = ii.getImage();
		ii = new ImageIcon(this.getClass().getResource("res/font/tx/n7.png"));
		txtAr[34] = ii.getImage();
		ii = new ImageIcon(this.getClass().getResource("res/font/tx/n8.png"));
		txtAr[35] = ii.getImage();
		ii = new ImageIcon(this.getClass().getResource("res/font/tx/n9.png"));
		txtAr[36] = ii.getImage();

		ii = new ImageIcon(this.getClass().getResource(
				"res/font/Text/slash.png"));
		txtAr[37] = ii.getImage();
		ii = new ImageIcon(this.getClass().getResource(
				"res/font/Text/qMark.png"));
		txtAr[38] = ii.getImage();
		ii = new ImageIcon(this.getClass().getResource(
				"res/font/Text/qMarkI.png"));
		txtAr[39] = ii.getImage();
		ii = new ImageIcon(this.getClass().getResource("res/font/Text/(.png"));
		txtAr[40] = ii.getImage();
		ii = new ImageIcon(this.getClass().getResource("res/font/Text/).png"));
		txtAr[41] = ii.getImage();
		ii = new ImageIcon(this.getClass().getResource(
				"res/font/Text/underscore.png"));
		txtAr[42] = ii.getImage();
	}

	boolean oneTime = false;

	@Override
	public void keyPressed(KeyEvent ke) {
		if (ke.getKeyCode() == KeyEvent.VK_SPACE) {
			if (!oneTime) {
				addRandomTile();
			}
			// grid[3][2] = 1;
		}
		if (ke.getKeyCode() == KeyEvent.VK_UP) {
			// slideTiles(0);
			goUp();
		}
		if (ke.getKeyCode() == KeyEvent.VK_RIGHT) {
			// slideTiles(1);
			goRight();
		}
		if (ke.getKeyCode() == KeyEvent.VK_DOWN) {
			// slideTiles(2);
			goDown();
		}
		if (ke.getKeyCode() == KeyEvent.VK_LEFT) {
			// slideTiles(3);
			goLeft();
		}
		if (ke.getKeyCode() == KeyEvent.VK_D) {
			score += 100;
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
