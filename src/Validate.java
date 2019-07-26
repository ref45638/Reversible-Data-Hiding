import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

public class Validate {

	public static void main(String[] args) {
		/* �ŧi */
		Validate pm = new Validate();

		System.out.println("Start valid");
		for (int b = 0; b < 2; b++) {
			for (int x = 1; x < 10; x++) {
				System.out.println("x:" + x);
				for (int y = 1; y < 10; y++) {
					for (int v1 = 1; v1 < 10; v1++) {
						for (int v3 = 1; v3 < 10; v3++) {
							for (int v4 = 1; v4 < 10; v4++) {
								for (int v5 = 1; v5 < 10; v5++) {
									int[] r1 = pm.embed2(b, x, y, v1, v3, v4, v5);
									int[] r2 = pm.extra2(r1[1], r1[2], v1, v3, v4, v5);

									if (r2[0] != r1[0])
										System.out.println("b!");
									if (r2[1] != x)
										System.out.println("x!");
									if (r2[2] != y)
										System.out.println("y!");

									if ((r2[0] != -1 && r2[0] != b) || r2[1] != x || r2[2] != y) {
										int z = pm.zzz(v1, v3, v4, v5);
										int zz = pm.zz(v1, v3, v4, v5);
										int d1 = x - y;
										int d2 = y - z;
										int d3 = zz - x;
										System.out.println(x + " " + y + " " + z + " " + zz + " " + b);
										System.out.println(d1 + " " + d2 + " " + d3);
										System.out.println(r1[0] + " " + r1[1] + " " + r1[2]);
										System.out.println(r2[0] + " " + r2[1] + " " + r2[2]);
									}
								}
							}
						}
					}
				}
			}
		}
		System.out.println("End valid");

	}

	public int zz(int v1, int v3, int v4, int v5) {

		float a = 0.33f;
		float b = 0.25f;

		return (int) ((1 - a) * (1 - b) * v3 + (1 - a) * b * v1 + a * (1 - b) * v4 + a * b * v5);
	}

	public int zzz(int v1, int v3, int v4, int v5) {

		float a = 0.66f;
		float b = 0.5f;

		return (int) ((1 - a) * (1 - b) * v3 + (1 - a) * b * v1 + a * (1 - b) * v4 + a * b * v5);
	}

	public int[] embed2(int b, int x, int y, int v1, int v3, int v4, int v5) {

		int z = zzz(v1, v3, v4, v5);
		int zz = zz(v1, v3, v4, v5);
		int ax = x;
		int ay = y;
		int d1 = x - y;
		int d2 = y - z;
		int d3 = zz - x;
		
		//System.out.println(x + " " + y + " " + z + " " + zz);

		if ((d1 >= 0 && d2 >= 0 && d3 == 0) || (d1 == 0 && d2 >= 0 && d3 < 0)) { // x+b
			ax += b;
		} else if (((d1 <= 0 && d2 <= 0 && d3 == 0) && !(d1 == 0 && d2 == 0 && d3 == 0)) || (d1 == 0 && d2 <= 0 && d3 > 0) || (d1 == -1 && d2 == 0 && d3 == 1)) { // x-b
			ax -= b;
		} else if ((d1 == -1 && d2 >= 1 && d3 >= 0) || (d1 < -1 && d2 == 1 && d3 >= 0)) { // y+b
			ay += b;
		} else if ((d1 == 1 && d2 <= -1 && d3 <= 0) || (d1 > 1 && d2 == -1 && d3 <= 0)) { // y-b
			ay -= b;
		} else if ((d1 <= -1 && d2 == 0 && d3 <= -1) || (d1 <= -1 && d2 > 0 && d3 == -1)) { // x+b y+b
			ax += b;
			ay += b;
		} else if ((d1 >= 1 && d2 == 0 && d3 >= 1) || (d1 >= 1 && d2 < 0 && d3 == 1)) { // x-b y-b
			ax -= b;
			ay -= b;
		}

		else if (d1 > 0 && d2 >= 0 && d3 < 0) { // ���äJ
			ax += 1;
			b = -1;
		} else if ((d1 < 0 && d2 <= 0 && d3 > 0) && !(d1 == -1 && d2 == 0 && d3 == 1)) { // ���äJ
			ax -= 1;
			b = -1;
		} else if (d1 < -1 && d2 > 1 && d3 >= 0) { // ���äJ
			ay += 1;
			b = -1;
		} else if (d1 > 1 && d2 < -1 && d3 <= 0) { // ���äJ
			ay -= 1;
			b = -1;
		} else if (d1 <= -1 && d2 > 0 && d3 < -1) { // ���äJ
			ax += 1;
			ay += 1;
			b = -1;
		} else if (d1 >= 1 && d2 < 0 && d3 > 1) { // ���äJ
			ax -= 1;
			ay -= 1;
			b = -1;
		} else {
			b = -1;
		}

		int[] result = new int[3];
		result[0] = b;
		result[1] = ax;
		result[2] = ay;

		return result;
	}

	public int[] extra2(int x, int y, int v1, int v3, int v4, int v5) {

		int z = zzz(v1, v3, v4, v5);
		int zz = zz(v1, v3, v4, v5);
		int d1 = x - y;
		int d2 = y - z;
		int d3 = zz - x;

		int bb = 0;
		if ((d1 >= 0 && d2 >= 0 && d3 == 0) || (d1 > 0 && d2 >= 0 && d3 == -1)) { // x+b
			bb = -1 * d3;
			x = x - bb;
		} else if ((d1 == 0 && d2 >= 0 && d3 < 0) || (d1 == 1 && d2 >= 0 && d3 < -1)) { // x+b
			bb = d1;
			x = x - bb;
		}

		else if (((d1 <= 0 && d2 <= 0 && d3 == 0) || (d1 < 0 && d2 <= 0 && d3 == 1)) && !(d1 == 0 && d2 == 0 && d3 == 0) && !(d1 == -1 && d2 == 0 && d3 == 1)) { // x-b
			bb = d3;
			x = x + bb;
		} else if ((d1 == 0 && d2 <= 0 && d3 > 0) || (d1 == -1 && d2 <= 0 && d3 > 1)) { // x-b
			bb = -d1;
			x = x + bb;
		} else if ((d1 == -1 && d2 == 0 && d3 == 1) || (d1 == -2 && d2 == 0 && d3 == 2)) { // x-b
			bb = d3 - 1;
			x = x + bb;
		}

		else if ((d1 == -1 && d2 >= 1 && d3 >= 0) || (d1 == -2 && d2 > 1 && d3 >= 0)) { // y+b
			bb = -1 * d1 - 1;
			y = y - bb;
		} else if ((d1 < -1 && d2 == 1 && d3 >= 0) || (d1 < -2 && d2 == 2 && d3 >= 0)) { // y+b
			bb = d2 - 1;
			y = y - bb;
		}

		else if ((d1 == 1 && d2 <= -1 && d3 <= 0) || (d1 == 2 && d2 < -1 && d3 <= 0)) { // y-b
			bb = d1 - 1;
			y = y + bb;
		} else if ((d1 > 1 && d2 == -1 && d3 <= 0) || (d1 > 2 && d2 == -2 && d3 <= 0)) { // y-b
			bb = -d2 - 1;
			y = y + bb;
		}

		else if ((d1 <= -1 && d2 == 0 && d3 <= -1) || (d1 <= -1 && d2 == 1 && d3 < -1)) { // x+b,y+b
			bb = d2;
			x = x - bb;
			y = y - bb;
		} else if ((d1 <= -1 && d2 > 0 && d3 == -1) || (d1 <= -1 && d2 > 1 && d3 == -2)) { // x+b,y+b
			bb = -1 * d3 - 1;
			x = x - bb;
			y = y - bb;
		}

		else if ((d1 >= 1 && d2 == 0 && d3 >= 1) || (d1 >= 1 && d2 == -1 && d3 > 1)) { // x-b,y-b
			bb = -1 * d2;
			x = x + bb;
			y = y + bb;
		} else if ((d1 >= 1 && d2 < 0 && d3 == 1) || (d1 >= 1 && d2 < -1 && d3 == 2)) { // x-b,y-b
			bb = d3 - 1;
			x = x + bb;
			y = y + bb;
		}

		else if (d1 > 1 && d2 >= 0 && d3 < -1) { // �L���K
			x = x - 1;
			bb = -1;
		} else if ((d1 < -1 && d2 <= 0 && d3 > 1) && !(d1 == -1 && d2 == 0 && d3 == 1)) { // ���äJ
			x = x + 1;
			bb = -1;
		} else if (d1 < -2 && d2 > 2 && d3 >= 0) { // �L���K
			y = y - 1;
			bb = -1;
		} else if (d1 > 2 && d2 < -2 && d3 <= 0) { // �L���K
			y = y + 1;
			bb = -1;
		} else if (d1 <= -1 && d2 > 1 && d3 < -2) { // �L���K
			x = x - 1;
			y = y - 1;
			bb = -1;
		} else if (d1 >= 1 && d2 < -1 && d3 > 2) { // �L���K
			x = x + 1;
			y = y + 1;
			bb = -1;
		} else {
			bb = -1;
		}

		int[] result = new int[3];
		result[0] = bb;
		result[1] = x;
		result[2] = y;

		return result;
	}

}