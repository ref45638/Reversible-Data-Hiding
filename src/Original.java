import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Original {
	public static void main(String[] args) {
		/* ??i */
		Original pm = new Original();
		BufferedImage bi;
		String path = "Lena.png";
		String stegofilename = "stegotiffany.bmp";
		String recoverfilename = "recover.bmp";
		try {

			int num = 2500;// ??????q
			bi = ImageIO.read(new File(path));
			int h = bi.getHeight();
			int w = bi.getWidth();
			int pix2D[][] = new int[w][h];
			int grayarray[][] = new int[w][h];// ??l??
			int stegoArray[][] = new int[w][h];// ??J???
			int recoverArray[][] = new int[w][h];// ????
			int embedbits[] = new int[w * h];
			int[] embed_result = new int[3];
			int extract[] = new int[w * h];
			int extractpointer = 0;
			int mark[][] = new int[w][h];
			int push[][] = new int[6][2];
			for (int i = 0; i < embedbits.length; i++) {
				embedbits[i] = (int) (Math.random() * 2);
			}
			pm.loadImage(path, pix2D);// ???
			pm.transToGray(pix2D, grayarray, stegoArray);// ????
			pm.preprocess(grayarray, stegoArray, mark);// ????e?B?z

			// ??J1 ??v???
			pm.embedImage(embed_result, embedbits, num, stegoArray);

			System.out.println();
			pm.saveImage(stegoArray, stegofilename);// ??X????v??
			pm.PSNR(grayarray, stegoArray);

			// ?^???????*/
			for (int i = 0; i < w; i++) {
				for (int j = 0; j < h; j++) {
					recoverArray[i][j] = stegoArray[i][j];
				}
			}

			extractpointer = pm.extractImage(extract, embed_result, recoverArray);// ???????K ??v???
/*
			// bit??J?P???X??????
			System.out.println();
			System.out.print("grayarray[0]:   ");
			for (int j = 0; j < h; j++) {
				System.out.print(grayarray[0][j] + " ");
			}
			System.out.println();
			System.out.print("stegoArray[0]:  ");
			for (int j = 0; j < h; j++) {
				System.out.print(stegoArray[0][j] + " ");
			}
			System.out.println();
			System.out.print("recoverArray[0]:");
			for (int j = 0; j < h; j++) {
				System.out.print(recoverArray[0][j] + " ");
			}
			System.out.println();

			int xxx = 0;
			for (int i = 0; i < w; i++) {
				for (int j = 0; j < h; j++) {
					if (grayarray[i][j] != recoverArray[i][j]) {
						System.out.println(i + " " + j);
						xxx++;
					}
				}
			}
			System.out.println("cover image ?P  recover image ???????P????q: " + xxx);

			System.out.println();
			System.out.print("embedbits[i]:");
			for (int i = 0; i < 1000; i++) {
				System.out.print(embedbits[i] + " ");
			}
			System.out.println();
			System.out.print("extract[i]:  ");
			for (int i = extractpointer - 1; i >= extractpointer - 1000; i--) {
				System.out.print(extract[i] + " ");
			}
			System.out.println();
			xxx = 0;
			if (num > extractpointer)
				num = extractpointer;
			for (int i = 0; i < num; i++) {
				if (embedbits[i] != extract[extractpointer - 1 - i]) {
					// System.out.println(i);
					xxx++;
				}
			}
			System.out.println("???K???P????q: " + xxx);
*/
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* ??? */
	public void loadImage(String path, int pix2D[][]) {
		BufferedImage bi;
		try {
			bi = ImageIO.read(new File(path));
			int w = bi.getWidth();
			int h = bi.getHeight();
			for (int i = 0; i < w; i++) {
				for (int j = 0; j < h; j++) {
					pix2D[i][j] = bi.getRGB(i, j);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // catch
	}

	/* ???? */
	public void transToGray(int pix2D[][], int grayarray[][], int stegoArray[][]) {
		int row = grayarray.length;
		int col = grayarray[0].length;
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				int red = 0xff & (pix2D[i][j] >> 16);// ???R?????
				int green = 0xff & (pix2D[i][j] >> 8);// ???G?????
				int blue = 0xff & pix2D[i][j];// ???B?????
				int gray = (red + green + blue) / 3;
				grayarray[i][j] = gray;
				stegoArray[i][j] = gray;
			}
		}
	}

	/* ????e?B?z */
	public void preprocess(int grayarray[][], int stegoArray[][], int mark[][]) {
		int row = grayarray.length;
		int col = grayarray[0].length;
		for (int i = 0; i < row; i++) { // ???B?z0?M255
			for (int j = 0; j < col; j++) {
				if (grayarray[i][j] == 0) {
					grayarray[i][j]++;
					stegoArray[i][j]++;
					mark[i][j]++;
				} else if (grayarray[i][j] == 255) {
					grayarray[i][j]--;
					stegoArray[i][j]--;
					mark[i][j]++;
				}
			}
		}
	}

	/* ??J */
	public void embedImage(int embed_result[], int embedbits[], int num, int stegoArray[][]) {
		int no = 0;
		int xx = 0;
		int yy = 0;
		int embedpointer = 0;
		int row = stegoArray.length;
		int col = stegoArray[0].length;

		for (int i = 0; i < row - 2; i++) {
			for (int j = 0; j < col - 3; j = j + 2) {
				yy++;

				int x = stegoArray[i][j];
				int y = stegoArray[i][j + 1];
				int z = z(stegoArray, i, j);

				int d1 = x - y;
				int d2 = y - z;

				if (d1 == 1 && d2 > 0) { // ?k?W
					stegoArray[i][j] += embedbits[embedpointer];
					embedpointer++;
				} else if (d1 == -1 && d2 < 0) { // ???U
					stegoArray[i][j] -= embedbits[embedpointer];
					embedpointer++;
				} else if ((d1 == 0 && d2 >= 0) || (d1 < 0 && d2 == 0)) { // ???W
					stegoArray[i][j + 1] += embedbits[embedpointer];
					embedpointer++;
				} else if ((d1 == 0 && d2 < 0) || (d1 > 0 && d2 == 0) || (d1 == 1 && d2 == -1)) { // ?k?U
					stegoArray[i][j + 1] -= embedbits[embedpointer];
					embedpointer++;
				} else if (d1 > 1 && d2 > 0) { // ?k?W????J
					stegoArray[i][j] += 1;
					no++;
				} else if (d1 < -1 && d2 < 0) { // ???U????J
					stegoArray[i][j] -= 1;
					no++;
				} else if (d1 < 0 && d2 > 0) { // ???W????J
					stegoArray[i][j + 1] += 1;
					no++;
				} else if ((d1 > 1 && d2 < 0) || (d1 == 1 && d2 < -1)) { // ?k?U????J
					stegoArray[i][j + 1] -= 1;
					no++;
				} else {
					no++;
					xx++;
				}

				if (embedpointer == num || (i == col - 3 && j == row - 4)) {
					System.out.println("embedpointer: " + embedpointer);

					embed_result[0] = embedpointer;
					embed_result[1] = i;
					embed_result[2] = j;
					return;
				}
			}
		}
	}

	public int z(int stegoArray[][], int i, int j) {
		int v1 = stegoArray[i][j + 2];
		int v2 = stegoArray[i][j + 3];
		int v3 = stegoArray[i + 1][j];
		int v4 = stegoArray[i + 1][j + 1];
		int v5 = stegoArray[i + 1][j + 2];
		int v7 = stegoArray[i + 2][j];
		int v8 = stegoArray[i + 2][j + 1];
		int dv = Math.abs(v1 - v5) + Math.abs(v3 - v7) + Math.abs(v4 - v8);
		int dh = Math.abs(v1 - v2) + Math.abs(v3 - v4) + Math.abs(v4 - v5);
		int u = (v1 + v4) / 2 + (v3 - v5) / 4;

		if ((dv - dh) > 80)
			return v1;
		else if ((dv - dh) > 32 && (dv - dh) <= 80)
			return (v1 + u) / 2;
		else if ((dv - dh) > 8 && (dv - dh) <= 32)
			return (v1 + 3 * u) / 4;
		else if ((dv - dh) >= -8 && (dv - dh) <= 8)
			return u;
		else if ((dv - dh) > -32 && (dv - dh) <= -8)
			return (v4 + 3 * u) / 4;
		else if ((dv - dh) > -80 && (dv - dh) <= -32)
			return (v4 + u) / 2;
		else if ((dv - dh) < -80)
			return v4;
		else
			return 0;
	}

	public int extractImage(int extract[], int embed_result[], int recoverArray[][]) {
		// extract
		int no = 0;
		int xx = 0;
		int yy = 0;
		int extractedpointer = 0;
		int row = recoverArray.length;
		int col = recoverArray[0].length;
		boolean first = true;

		for (int i = embed_result[1]; i >= 0; i--) {
			for (int j = j = col - (col % 2 == 0 ? 4 : 3); j >= 0; j = j - 2) {
				if (first && i == embed_result[1]) {
					j = embed_result[2];
					first = false;
				}
				// System.out.println(i + " " + j);
				yy++;

				int x = recoverArray[i][j];
				int y = recoverArray[i][j + 1];
				int z = z(recoverArray, i, j);

				int d1 = x - y;
				int d2 = y - z;

				if ((d1 == 1 || d1 == 2) && d2 > 0) { // x+b
					extract[extractedpointer] = d1 - 1;
					recoverArray[i][j] = recoverArray[i][j] - extract[extractedpointer];
					extractedpointer++;
				} else if ((d1 == -1 || d1 == -2) && d2 < 0) { // x-b
					extract[extractedpointer] = -1 * d1 - 1;
					recoverArray[i][j] = recoverArray[i][j] + extract[extractedpointer];
					extractedpointer++;
				}

				else if ((d1 == 0 && d2 >= 0) || (d1 == -1 && d2 >= 1)) { // y+b
					extract[extractedpointer] = -1 * d1;
					recoverArray[i][j + 1] = recoverArray[i][j + 1] - extract[extractedpointer];
					extractedpointer++;
				} else if ((d1 < 0 && d2 == 0) || (d1 < -1 && d2 == 1)) { // y+b
					extract[extractedpointer] = d2;
					recoverArray[i][j + 1] = recoverArray[i][j + 1] - extract[extractedpointer];
					extractedpointer++;
				}

				else if ((d1 == 0 && d2 < 0) || (d1 == 1 && d2 < -1)) { // y-b
					extract[extractedpointer] = d1;
					recoverArray[i][j + 1] = recoverArray[i][j + 1] + extract[extractedpointer];
					extractedpointer++;
				} else if ((d1 > 0 && d2 == 0) || (d1 > 1 && d2 == -1)) { // y-b
					extract[extractedpointer] = -1 * d2;
					recoverArray[i][j + 1] = recoverArray[i][j + 1] + extract[extractedpointer];
					extractedpointer++;
				} else if ((d1 == 1 && d2 == -1) || (d1 == 2 && d2 == -2)) { // y-b
					extract[extractedpointer] = d1 - 1;
					recoverArray[i][j + 1] = recoverArray[i][j + 1] + extract[extractedpointer];
					extractedpointer++;
				}

				else if (d1 > 2 && d2 > 0) { // x+b
					recoverArray[i][j] = recoverArray[i][j] - 1;
					no++;
				}

				else if (d1 < -2 && d2 < 0) { // x-b
					recoverArray[i][j] = recoverArray[i][j] + 1;
					no++;
				}

				else if (d1 < -1 && d2 > 1) { // y+b
					recoverArray[i][j + 1] = recoverArray[i][j + 1] - 1;
					no++;
				}

				else if (d1 > 2 && d2 < -1) { // y-b
					recoverArray[i][j + 1] = recoverArray[i][j + 1] + 1;
					no++;
				} else if (d1 == 2 && d2 < -2) { // y-b
					recoverArray[i][j + 1] = recoverArray[i][j + 1] + 1;
					no++;
				}
			}
		}

		System.out.println("");
		System.out.println("extractedpointer: " + extractedpointer);
		return extractedpointer;
	}

	public void saveImage(int image[][], String filename) {
		int row = image.length;
		int col = image[0].length;
		int output[] = new int[row * col];
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				output[j * col + i] = (0xff000000 | image[i][j] << 16 | image[i][j] << 8 | image[i][j]);
			}
		}
		BufferedImage Output = new BufferedImage(row, col, BufferedImage.TYPE_INT_RGB);
		Output.setRGB(0, 0, row, col, output, 0, row);
		File File = new File(filename);// ??X??
		try {
			ImageIO.write(Output, "jpg", File);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void PSNR(int orignal[][], int changed[][]) {
		int row = orignal.length;
		int col = orignal[0].length;
		double signal = 0;
		double noise = 0;
		double peak = 0;
		int different = 0;
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				signal += orignal[i][j] * orignal[i][j];
				noise += (orignal[i][j] - changed[i][j]) * (orignal[i][j] - changed[i][j]);
				if (orignal[i][j] - changed[i][j] != 0)
					different += Math.abs(orignal[i][j] - changed[i][j]);
				if (peak < orignal[i][j])
					peak = orignal[i][j];
			}
		}
		double mse = noise / (512 * 512); // Mean square error
		System.out.println("MSE: " + mse);
		System.out.println("noise: " + noise);
		System.out.println("different: " + different);
		System.out.println("SNR: " + 10 * log10(signal / noise));
		System.out.println("PSNR(max=255): " + (10 * log10(255 * 255 / mse)));
		System.out.println("PSNR(max=" + peak + "): " + 10 * log10((peak * peak) / mse));
	}

	public static double log10(double x) {
		return Math.log(x) / Math.log(10);
	}
}
