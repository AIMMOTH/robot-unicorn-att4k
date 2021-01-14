import java.applet.Applet;
import java.awt.Color;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Polygon;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class G extends Applet implements Runnable {

	private final static float R = 11050.0f;
	private final static float P = 6.28318531f / R;
	private final static int BS = (int) (2 * R);
	private final static int B = (BS >> 4);

	private boolean gr = false;
	private int sc = 0;
	private SourceDataLine dl = null;
	
	private int p = 0; // Latest pressed
	private boolean d = false; // Mouse down?
	
	public boolean handleEvent(Event e) {
		switch (e.id) {
		case Event.KEY_PRESS:
			p = e.key;
			break;
		case Event.MOUSE_DOWN:
			d = true;
			break;
		}
		return false;
	}

	public void start() {
		new Thread(this).start();
	}
	
	public void run() {
		int i;
		int l;
		if (dl == null) {
			try {
				final AudioFormat f = new AudioFormat(R, 8, 1, true, false);
				final DataLine.Info dli = new DataLine.Info(SourceDataLine.class, f, BS);
				dl = (SourceDataLine) AudioSystem.getLine(dli);
				dl.open();
				dl.start();
			} catch (final Exception e) {
				// Optad nada.
			}

			final byte[] b = new byte[BS];

			// start the graphics thread
			new Thread(this).start();

			// generate the frequencies of some notes
			final float[] fs = new float[80];
			// float f = 110.0f; // A
			float f = 65.41f; // C
			for (i = 1; i < 80; i++) {
				fs[i] = f;
				f *= 1.0594630944f; // Evenly tempered scale (12th root 2)
			}

			final int[][][] ps = new int[2][][];
			final int[][] po = new int[2][];
			final int[][] po2 = new int[2][];
			final int[] pi = new int[] { 0, 0};

			int[] nu = new int[] {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
			po2[0] = nu;
			po[0] = new int[] {
					2,3,0,4,2,5,4,6,7,
					8,9,10,11,12,13,14,15,16,17
					,18,19,20,21,0,0,0,0,0 // to 68
				};
			ps[0] = new int [][] {
					nu,
					new int[] {47,0,0,0,45,0,0,0,0,0,0,0,0,0,0,0},
					new int[] {35,0,35,0,33,0,38,0,0,36,35,0,0,0,0,0},
					new int[] {0,0,35,0,33,0,31,0,0,0,0,0,0,0,28,0},
					new int[] {31,0,33,0,35,0,33,0,31,0,0,0,0,0,0,0},
					new int[] {0,0,35,0,33,0,31,0,0,0,0,0,0,0,0,0},
					new int[] {0,0,35,0,38,0,40,0,0,0,0,0,0,0,0,0},
					new int[] {0,0,35,0,40,0,42,0,0,0,0,0,0,0,0,0},
					new int[] {0,0,38,0,38,0,42,0,0,0,0,0,0,0,0,0},
					new int[] {0,0,38,0,0,0,45,0,0,0,43,42,43,0,0,0},
					
					new int[] {0,0,0,0,38,0,43,0,0,0,0,0,0,0,0,0},
					new int[] {0,0,38,0,38,0,45,0,0,0,0,0,0,0,0,0},
					new int[] {0,0,43,0,45,0,47,0,0,0,0,0,0,0,43,0},
					new int[] {0,0,0,0,40,0,0,0,0,0,0,0,0,0,0,0},
					new int[] {0,0,0,0,0,0,0,0,43,0,0,0,0,0,38,0},
					new int[] {0,0,43,0,38,0,43,0,45,0,0,0,42,0,38,0},
					new int[] {0,0,45,0,42,0,38,0,45,0,0,0,42,0,38,0},
					new int[] {0,0,45,0,42,0,38,0,40,0,42,0,43,0,43,0},
					new int[] {45,0,47,0,47,0,0,0,45,0,0,0,43,0,0,0},
					new int[] {0,0,38,0,0,0,43,0,38,0,43,0,45,0,0,0},
					
					new int[] {42,0,38,0,0,0,45,0,42,0,38,0,45,0,0,0},
					new int[] {42,0,38,0,0,0,45,0,42,0,38,0,40,0,42,0},
					};
			
			po2[1] = new int[] {1,2,3,4,5,6,7, 8};
			po[1] = new int[] {
					10,11,10,11,10,11,10,12,13,
					14,15,16,17,15,18,19,20,21,22,
					23,24,25,26,27,28,29,28,29 // to 68
					};
			ps[1] = new int [][] {
					nu,
					new int[] {65,0,65,0,65,0,64,0,64,0,64,0,64,0,0,0},
					new int[] {62,0,62,0,62,0,60,0,60,0,60,0,60,0,62,64},
					new int[] {65,64,65,64,64,0,64,0,62,0,62,0,60,0,60,0},
					new int[] {64,0,64,0,64,0,64,0,0,0,0,0,0,0,62,64},
					new int[] {65,64,65,64,64,0,64,0,62,0,62,0,62,0,62,60},
					new int[] {64,60,64,60,60,0,60,0,57,0,57,0,57,0,57,59},
					new int[] {60,59,60,59,59,0,59,0,57,0,57,0,55,0,55,0},
					new int[] {57,0,57,0,57,0,57,0,0,0,0,0,0,0,0,0},

					new int[] {57,0,59,0,59,0,62,0,40,40,50,52,0,40,40,40}, // 9
					new int[] {52,0,0,50,38,38,50,50,31,31,31,43,0,31,31,31},
					new int[] {43,0,0,43,38,38,47,50,40,40,50,52,0,40,40,40},
					new int[] {43,0,0,43,38,38,47,50,40,40,40,52,0,40,40,40},
					new int[] {52,0,0,52,40,40,52,52,38,38,38,50,0,38,38,38},
					new int[] {50,0,0,50,38,38,50,50,38,38,38,50,0,38,38,38},
					new int[] {50,0,0,50,38,38,50,50,40,40,40,52,0,40,40,40},
					
					new int[] {52,0,0,52,40,40,52,52,43,43,43,55,0,43,43,43},
					new int[] {55,0,0,55,43,43,55,55,38,38,38,50,0,38,38,38},
					new int[] {52,0,0,52,40,40,52,52,40,40,40,52,0,40,40,40},
					new int[] {52,0,0,52,40,40,52,52,62,0,0,0,0,0,0,0},
					new int[] {0,67,0,67,0,0,0,0,59,0,0,0,0,0,0,0},
					new int[] {0,59,0,59,0,0,0,0,62,0,0,0,0,0,0,0},
					new int[] {0,62,0,62,0,0,0,0,60,0,0,0,0,0,59,0},
					new int[] {0,0,0,62,0,0,0,0,60,0,0,0,62,0,0,0},
					new int[] {0,0,0,0,0,67,0,67,0,0,0,0,59,0,0,0},
					new int[] {0,0,0,0,0,59,0,59,0,0,0,0,62,0,0,0},
					
					new int[] {0,0,0,0,0,62,0,62,0,0,0,0,60,0,0,0},
					new int[] {0,0,59,0,0,0,0,62,0,0,0,0,60,0,0,0},
					new int[] {40,40,50,52,0,40,40,40,52,0,0,50,38,38,50,50},
					new int[] {31,31,31,43,0,31,31,31,43,0,0,43,38,38,47,50}
					
			};

			// generate sound
			boolean lgr = gr;
			while (true) {
				int o = 0;
				if (lgr != gr) {Arrays.fill(pi, 0); lgr = gr; }
				for (int beat = 0; beat < 16; beat++) {
					// clear the buffer first
					Arrays.fill(b, o, o + B, (byte) 0);

					// render the notes
					for (int in = 0; in < ps.length; in++) {
						// find out which pattern to play
						int order = po[in][pi[in]];
						if (!gr) order = po2[in][pi[in]];
						final int note = ps[in][order][beat];

						if (note <= 0) {
							// nothing to play here
							continue;
						}
						// render the samples
						for (i = 0; i < B; i++) {
							b[o + i] = (byte) (b[o + i] + (Math.sin(fs[note] * (o + i) * P)* (in == 0?80f:5f)));
						}
					}
					o += B;
				}
				// Increment the pattern index counter
				for (i = 0; i < ps.length; i++) {
					pi[i] = (pi[i] + 1) % (gr?po[i].length:po2[i].length);
				}
				dl.write(b, 0, b.length);
			}
		}

		final int w = getWidth();
		final int h = getHeight();
		final int[] b = new int[640];
		int cH = 0;
		double dyb = 0;
		double dyf = 0;
		int hBP = 10;
		int hFP = 80;
		int hB = 0;
		int hF = 0;
		byte js = 0;
		GraphicsEnvironment ge;
		GraphicsDevice gd;
		GraphicsConfiguration gc;
		ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		gd = ge.getDefaultScreenDevice();
		gc = gd.getDefaultConfiguration();
		// Create images
		Polygon[] ps = new Polygon[] {
				new Polygon(new int[] {73,63,48,37,34,30,20,3,1,19,66,105,113,124,137,149,153,132,113,103,102,81,73},new int[] {80,77,68,53,43,30,33,39,27,26,20,16,11,4,6,6,31,23,44,57,76,79,80},23),
				new Polygon(new int[] {79,53,52,33,31,22,9,3,1,31,90,101,106,131,157,153,149,142,126,113,123,114,118,104,97,78,83,96,75,63,75,74,68,80},new int[] {84,63,51,32,34,33,40,33,25,25,16,11,8,1,0,27,31,27,23,44,57,81,67,56,74,59,64,69,49,52,64,70,68,86},34),
				new Polygon(new int[] {42,31,26,21,9,1,30,86,103,111,123,136,160,153,144,133,117,120,140,137,127,109,101,114,78,64,84,67,45,46,49},new int[] {88,36,29,26,32,23,23,17,9,6,1,1,1,28,30,20,33,50,72,76,61,70,65,58,48,59,78,68,63,85,88},31),
				new Polygon(new int[] {52,39,38,9,9,20,31,21,2,6,43,93,108,118,136,145,156,153,148,139,119,127,129,143,128,124,109,101,49,50,57},new int[] {86,61,51,78,87,55,25,27,33,18,17,11,7,3,3,4,7,32,34,24,29,45,64,76,68,72,50,48,57,81,86},31),
				new Polygon(new int[] {119,114,97,61,30,28,21,18,1,22,38,46,28,14,23,83,111,122,144,157,165,166,169,163,154,135,145,165,150,119,125,118},new int[] {87,49,48,45,67,85,80,64,65,56,44,23,26,23,18,18,10,6,1,4,10,16,34,35,24,37,50,75,59,60,84,87},32),
				new Polygon(new int[] {150,120,89,79,88,94,61,33,0,15,36,41,38,31,17,16,57,89,118,135,148,158,171,167,160,153,142,128,153},new int[] {85,50,69,81,63,47,42,56,62,61,43,24,25,28,28,15,16,16,9,1,3,5,10,29,33,25,23,44,85},29),
				new Polygon(new int[] {92,87,62,74,87,55,48,33,33,31,15,2,7,33,27,21,2,19,75,108,113,121,129,137,147,148,154,142,133,114,102,96,96},new int[] {89,67,71,70,53,43,50,53,69,68,59,67,65,44,30,30,31,23,19,11,6,4,5,1,8,14,32,28,23,44,55,86,90},33)
//				};
		};
		
		BufferedImage[] bH = new BufferedImage[ps.length];

		for (i=0; i<bh.length; i++) { bh[i]="gc.createCompatibleImage(440," 230, transparency.bitmask); graphics2d g2="(Graphics2D)" bh[i].getgraphics(); g2.setcolor(color.white); for (int j="0;" j<ps[i].npoints; j++) { ps[i].xpoints[j] *="2.5;" ps[i].ypoints[j] *="2.5;" } g2.fillpolygon(ps[i]); } double[] rs="new" double[100]; int rsi="0;" for (i="0;" i<rs.length; i++) rs[i]="Math.random();" create background image bufferedimage i2="new" bufferedimage(w, h, bufferedimage.type_int_rgb); graphics i2g="i2.getGraphics();" int middle="h" 2; i2g.setcolor(new color(152, 193, 254)); i2g.fillrect(0, 0, w, h); i2g.setcolor(new color(167, 177, 241)); i2g.fillrect(0, middle, w, h); i2g.filloval(0, 120, 100, 200); i2g.filloval(150, 200, 120, 200); i2g.filloval(300, 100, 80, 200); i2g.filloval(450, 150, 120, 200); i2g.filloval(580, 120, 100, 200); i2g.setcolor(new color(175, 185, 244)); i2g.filloval(20, 140, 60, 100); i2g.filloval(180, 220, 60, 120); i2g.filloval(310, 140, 60, 100); i2g.filloval(480, 180, 60, 100); i2g.filloval(600, 140, 60, 120); color wbg="new" color(94, 38, 143); bufferedimage sr="new" bufferedimage(640, 480, bufferedimage.type_int_rgb); graphics g="sr.getGraphics();" graphics ag="getGraphics();" while (true) { mouse click and keyboard press hold up. boolean mc="false;" boolean pr="false;" string text="Click here!" ; while (!pr) { if (!mc && d) { mc="true;" text="Press Z to make your wishes come true!" ; } if (mc && (p="122" || p="90))" { z pr="true;" p="0;" } g.setcolor(wbg); g.fillrect(0, 0, w, h); g.setcolor(color.white); g.drawstring("robot unicorn att4k", 250, 240); if (sc> 0) g.drawString("Last score: ".concat(String.valueOf(sc)), 310, 300);
			g.drawString(text, 300, 440);
			
			ag.drawImage(sr, 0, 0, null);
			try {
				Thread.sleep(100);
			} catch (Exception ie) {
				return;
			}
			if (!isActive())
				return;
		}
		// Create first platform
		cH = h / 2;
		boolean ip = true;
		int cG = 0;
		int[][] cPs = null;
		int cPB = 0;
		int cPPos = 0;
		int hBD = 0;
		
		int CF = 0;

		boolean m = true;
		sc = 0;
		int ls = 3;
		gr = true;
		while (gr) {
			// Start animation.
			int aI = 0;
			int px = -350;
			while (px < 660) {
				g.setColor(wBg);
				g.fillRect(0, 0, w, h);
				g.setColor(Color.WHITE);
				g.drawImage(bH[aI++], px, 130, this);
				g.drawString("Chase your dreams!", 250, 400);
				
				ag.drawImage(sr, 0, 0, this);
				if (aI == bH.length)
					aI = 0;
				px += 60;
				try {
					Thread.sleep(90);
				} catch (Exception ie) {
					return;
				}
				if (!isActive())
					return;
			}
		// Create start platforms.
		for (i=0; i<490; i++)
			b[i] = cH;
		for (i=490; i<b.length; i++) b[i]="10000;" pit hb="b[10]" - 10; hf="b[40]" - 10; boolean nl="false;" boolean sf="false;" double sp="3.0d;" game loop! while (!nl) { paint g.drawimage(i2, 0, 0, this); center horse front on screen. cf="(h" 2) - hf; for (i="0;" i<b.length && i < w; i++) { g.setcolor(color.black); g.drawline(i, cf + b[i], i, cf + b[i] + 1); g.setcolor(new color(196, 98, 240)); g.drawline(i, cf + b[i] + 1, i, cf + b[i] + 10); g.setcolor(new color(177, 122, 123)); g.drawline(i, cf + b[i] + 10, i, cf + b[i] + 30); g.setcolor(color.black); g.drawline(i, cf + b[i] + 31, i, cf + b[i] + 32); } g.setcolor(color.white); g.drawstring(string.valueof(sc), 310, 20); g.drawstring(string.valueof(ls), 600, 20); if (m) { g.drawimage(bh[ai], hbp, cf + hb-15, 70, 30, this); if (sf && ++ai="bH.length)" ai="0;" } else { g.drawimage(bh[3], hbp, cf + hb-15, 70, 30, this); } sf="!sf;" ag.drawimage(sr, 0, 0, this); update if (rsi> 86) rsi = 0;
			// Create new platform or gap.
			if (ip && cPs == null) {
				cPs = new int[(int) (rs[rsi++] * 3) + 1][];
				for (int j=0; j<cps.length; j++) { int[] bit; double rt="rs[rsi++];" if (rt < 0.7) { l="40" + (int) (rs[rsi++] * 3) * 30; int hei="((int)" (rs[rsi++] * 3)) * 40; boolean inv="(rs[rsi++]" < 0.5); bit="new" int[l]; int move="(int)" (l * 0.2); double c="l" * l (hei * 1.0); for (i="0;" i<l; i++) { int x="i" - move; bit[i]="(int)" ((x*x) c); if (inv) bit[i]="hei" - bit[i]; bit[i] +="cH;" } } else if (rt < 0.8) { l="60" + ((int) (rs[rsi++] * 2)) * 30; bit="new" int[l]; for (i="0;" i<bit.length; i++) bit[i]="cH;" } else { l="60" + (int) (rs[rsi++] * 2) * 30; bit="new" int[l]; boolean early="(rs[rsi++]<" 0.5); int start="(early)" ? l 2 : l * 3 4; int stop="(early)" ? l * 3 4 : bit.length; for (i="0;" i<bit.length; i++) { bit[i]="cH;" if (i>=start && i < stop)
								bit[i] -= 20;
						}
					}
					cH = bit[bit.length -1];
					cPs[j] = bit;
				}
			} else if (!ip && cG == 0) {
				cG = 100 + ((int) (rs[rsi++] * 3)) * 10;
			}
			
			// Move ground
			l = 0;
			while (l < sp) {
				for (i=0; i<b.length - 1; i++) { b[i]="b[i+1];" } l++; } sp +="0.01d;" sc +="sp;" if (ip) { b[b.length - 1]="cPs[cPB][cPPos++];" if (cppos="cPs[cPB].length)" { cppos="0;" if (++cpb="cPs.length)" { cpb="0;" cps="null;" ip="false;" } } } else { b[b.length - 1]="10000;" if (--cg="0)" { ip="true;" cg="0;" } } if ((p="122" || p="90))" { p="0;" if (js < 2) { js++; if (hb> hF) hF = hB;
					else hB = hF;
					dyb = 0;
					dyf = -4;
					p = 0;
					hBD = 10;
					m = false;
				}
			} else if (hBD > 0) {
				hBD--;
				if (hBD == 0)
					dyb = -4;
			}
			hB += dyb;
			hF += dyf;
			if (hBD == 0) { // Only apply gravity after delay
				// On ground?
				if (dyb >= 0 && hB >= b[hBP] - 10) {
					hB = b[hBP] - 10;
					dyb = 0;
				} else
					dyb += 0.1d;
			}
			
			if (hF > b[hFP + 1] + 5) {
				nL = true;
				ls--;
			}
			// On ground?
			if (dyf >= 0 && hF >= b[hFP] - 10) {
				hF = b[hFP] - 10;
				dyf = 0;
				m = true;
				js = 0;
			} else {
				dyf += 0.1d;
			}
			gr = ls > 0;
			if (!gr) pR = false;
			try {
				Thread.sleep(20);
			} catch (Exception ie) {
				return;
			}
			if (!isActive())
				return;
		}
	}
	}
	}
}
