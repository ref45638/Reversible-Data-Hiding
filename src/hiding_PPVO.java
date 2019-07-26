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

public class hiding_PPVO {

	public static void main(String[] args) {
		/* 宣告 */
		hiding_PPVO pm = new hiding_PPVO();
		BufferedImage bi;
		String path = "lena_gray.jpg";
		String stegofilename = "stegotiffany.jpg";
		try {
			int num = 20000;// 控制藏量
			bi = ImageIO.read(new File(path));
			int h = bi.getHeight();
			int w = bi.getWidth();
			int pix2D[][] = new int[w][h];
			int grayarray[][] = new int[w][h];// 原始圖
			int stegoArray[][] = new int[w][h];// 藏入後圖
			int recoverArray[][] = new int[w][h];// 還原圖
			int embedbits[] = new int[w * h];
			int embedpointer = 0;
			int extract[] = new int[w * h];
			int extractpointer = 0;
			int mark[][] = new int[w][h];
			for (int i = 0; i < embedbits.length; i++) {
				embedbits[i] = (int) (Math.random() * 2);
			}
			pm.loadImage(path, pix2D);// 讀檔
			pm.transToGray(pix2D, grayarray,stegoArray);// 轉灰階
			pm.preprocess(grayarray, stegoArray, mark);// 溢位前處理
			embedpointer = pm.embedImage(embedbits, num, stegoArray);// 藏入
			// pm.saveImage(stegoArray, stegofilename);// 輸出偽裝影像
			// extractpointer = pm.extractImage(stegoArray, extract, num, recoverArray);//
			// 擷取及還原*/
			 pm.PSNR(grayarray, stegoArray);
			// pm.validBits(embedbits, extract, embedpointer, extractpointer);// 位元比對
			// pm.validImage(grayarray, recoverArray);// 影像還原偵測

			// pm.validAlgo(1, 15);//測試

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// System.setOut(new PrintStream(new
		// FileOutputStream("output.txt")));

	}

	/* 讀檔 */
	public void loadImage(String path, int pix2D[][]) {
		BufferedImage bi;
		try {
			bi = ImageIO.read(new File(path));
			int h = bi.getHeight();
			int w = bi.getWidth();
			for (int i = 0; i < h; i++) {
				for (int j = 0; j < w; j++) {
					pix2D[i][j] = bi.getRGB(i, j);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // catch
	}

	/* 轉灰階 */
	public void transToGray(int pix2D[][], int grayarray[][], int stegoArray[][]) {
		int row = grayarray.length;
		int col = grayarray[0].length;
		for (int i = 0; i < col; i++) {
			for (int j = 0; j < row; j++) {
				int red = 0xff & (pix2D[i][j] >> 16);// 抓取R的數值
				int green = 0xff & (pix2D[i][j] >> 8);// 抓取G的數值
				int blue = 0xff & pix2D[i][j];// 抓取B的數值
				int gray = (red + green + blue) / 3;
				grayarray[i][j] = gray;
				stegoArray[i][j] = gray;
			}
		}
	}

	/* 溢位前處理 */
	public void preprocess(int grayarray[][], int stegoArray[][], int mark[][]) {
		int row = grayarray.length;
		int col = grayarray[0].length;
		for (int i = 0; i < col; i++) { // 先處理0和255
			for (int j = 0; j < row; j++) {
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

	/* 藏入 */
	public int embedImage(int embedbits[], int num, int stegoArray[][]) {
		int embedpointer = 0;
		int row = stegoArray.length;
		int col = stegoArray[0].length;
		int n = 3;
		int[] c = new int[n*n];
		int no = 0;

		for (int i = (n/2); i < row - n; i++) {// block n*n
			for (int j = (n/2); j < col - n; j++) {
				//System.out.println("stegoArray: " + stegoArray[i][j]);
				if(embedpointer == num) continue;
				int nn = 0;
				
				for(int x = i-(n/2); x < i+(n/2)+1; x++) { //c
					for(int y = j-(n/2); y < j+(n/2)+1; y++) {
						if(i==x && j==y) continue;
						c[nn] = stegoArray[x][y];
						++nn;
					}
				}
				/*
				for(int x = i; x < i + n; x++) {
					for(int y = j; y < j + n; y++) {
						if(i==x && j==y) continue;
						c[nn] = stegoArray[x][y];
						//System.out.println("c[" + nn + "]" + c[nn]);
						++nn;
					}
				}*/
				
				int max = 0;//求MAX MIN
				int min = 999;
				for(int x = 0; x < n*n-1; x++) 
					if(c[x] > max) max = c[x];
				for(int x = 0; x < n*n-1; x++) 
					if(c[x] < min) min = c[x];
				
				
				
				
				
				/*System.out.println("i: " + i + " j: " + j);
				System.out.println("max: " + max + " min: " + min);
				System.out.println("embedbits[" + embedpointer + "]" + embedbits[embedpointer]);
				System.out.println("b" + stegoArray[i][j]);*/
				if(min != max) {//藏秘密
					if(min == stegoArray[i][j]) {
						stegoArray[i][j] -= embedbits[embedpointer];
						embedpointer++;
					}
					else if(max == stegoArray[i][j]) {
						stegoArray[i][j] += embedbits[embedpointer];
						embedpointer++;
					}
					else if(min > stegoArray[i][j]) {
						stegoArray[i][j]--;
						no++;
					}
					else if(max < stegoArray[i][j]) {
						stegoArray[i][j]++;
						no++;
					}
					else no++;
				}
				else {
					if(min == 254 && max == 254) {
						stegoArray[i][j] -= embedbits[embedpointer];
						embedpointer++;
					}
					else if(min == stegoArray[i][j] && max == stegoArray[i][j]) {
						stegoArray[i][j] -= embedbits[embedpointer];
						embedpointer++;
					}
					else if(min > stegoArray[i][j] && max > stegoArray[i][j]) {
						stegoArray[i][j]--;
						no++;
					}
				}
				//System.out.println("a" + stegoArray[i][j]);
				
				
				
				
			}
		}
		System.out.println("embedpointer: " + embedpointer);
		//System.out.println("no: " + no);
		return embedpointer;
	}

	public int embedPixel(int min, int max, int pixelValue, int embedbits[]) {
		int embedbitPerBlock = 0;
		pixelValue++;
		
		
		
		return embedbitPerBlock;

	}

	public int extractImage(int stegoArray[][], int extract[], int num, int recoverArray[][]) {
		// extract
		int extractpointer = 0;
		int row = stegoArray.length;
		int col = stegoArray[0].length;
		for (int i = 0; i < row - 1; i += 2) {// block2*2
			for (int j = 0; j < col - 1; j += 2) {
				int window[] = new int[4];
				window[0] = stegoArray[i][j];
				window[1] = stegoArray[i][j + 1];
				window[2] = stegoArray[i + 1][j];
				window[3] = stegoArray[i + 1][j + 1];
				int sortedwindow[] = new int[4];
				int sortedindex[] = new int[4];// 紀錄原始位置
				int outputwindow[] = new int[4];
				for (int q = 0; q < window.length; q++) {
					sortedwindow[q] = window[q];
					sortedindex[q] = q;
				}
				int n = sortedindex.length;
				int temp = 0;
				int tempindex = 0;
				for (int c = 0; c < n; c++) {// bubble sort ascend order
					for (int d = 1; d < (n - c); d++) {

						if (sortedwindow[d - 1] > sortedwindow[d]) {// sort
							temp = sortedwindow[d - 1];
							sortedwindow[d - 1] = sortedwindow[d];
							sortedwindow[d] = temp;

							tempindex = sortedindex[d - 1];// record
							// original
							// index
							sortedindex[d - 1] = sortedindex[d];
							sortedindex[d] = tempindex;
						}
					}
				}
				// Arrays.sort(sortedwindow);

				// extract
				int extractBitNum = extractBlock(sortedwindow, extract, extractpointer, num);
				extractpointer += extractBitNum;

				for (int v = 0; v < sortedwindow.length; v++) {// 把像素放回原來位置
					for (int t = 0; t < sortedwindow.length; t++) {
						if (v == sortedindex[t]) {
							outputwindow[v] = sortedwindow[t];
							continue;
						}
					}
				}
				recoverArray[i][j] = outputwindow[0];
				recoverArray[i][j + 1] = outputwindow[1];
				recoverArray[i + 1][j] = outputwindow[2];
				recoverArray[i + 1][j + 1] = outputwindow[3];
			}
		}
		return extractpointer;

	}

	public int extractBlock(int sortedwindow[], int extract[], int extractpointer, int num) {
		int PE[] = new int[4];// prediction error
		int extractbitPerBlock = 0;
		PE[3] = sortedwindow[3] - sortedwindow[1];
		PE[2] = sortedwindow[2] - sortedwindow[1];
		boolean move = false;// 判斷最大值是否-1
		if (extractpointer < num) {
			if (PE[3] == 0) {
			} else if (PE[3] == 1) {// 是1的話秘密就是0
				extract[extractpointer] = 0;
				extractbitPerBlock++;
				extractpointer++;
			} else if (PE[3] == 2) {// 是2的話秘密就是1
				sortedwindow[3] -= 1;
				extract[extractpointer] = 1;
				extractbitPerBlock++;
				extractpointer++;
				move = true;
			} else if (PE[3] > 2) {// 大於2的話還援救減1
				sortedwindow[3] -= 1;
				move = true;
			}
			if (move == true) {
				if (PE[2] == 0) {
				} else if (PE[2] == 1) {
					extract[extractpointer] = 0;
					extractbitPerBlock++;
					extractpointer++;
				} else if (PE[2] == 2) {
					sortedwindow[2] -= 1;
					extract[extractpointer] = 1;
					extractbitPerBlock++;
					extractpointer++;
				} else if (PE[2] > 2) {
					sortedwindow[2] -= 1;
				} else {
				}
			}

		}
		PE[0] = sortedwindow[0] - sortedwindow[1];

		// min prediction extract
		if (extractpointer < num) {
			if (PE[0] == 0) {
			} else if (PE[0] == -2) {
				sortedwindow[0] += 1;
				extract[extractpointer] = 1;
				extractbitPerBlock++;
				extractpointer++;
			} else if (PE[0] == -1) {
				extract[extractpointer] = 0;
				extractbitPerBlock++;
				extractpointer++;
			} else {
				sortedwindow[0] += 1;
			}

		}
		return extractbitPerBlock;

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
		File File = new File(filename);// 輸出檔
		try {
			ImageIO.write(Output, "jpg", File);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void validBits(int embedbits[], int extract[], int embedpointer, int extractpointer) {
		double error = 0;
		for (int i = 0; i < embedpointer; i++) {
			if (embedbits[i] != extract[i]) {
				error++;
				// System.out.println("i=" + i + " ");
			}
		}
		System.out.print("embedbit=  ");
		for (int i = 0; i < 1000; i++) {
			System.out.print(embedbits[i]);
		}
		System.out.println("");
		System.out.print("extractbit=");
		for (int i = 0; i < 1000; i++) {
			System.out.print(+extract[i]);
		}
		System.out.println("");
		System.out.println("embedpointer=" + embedpointer + " ");
		System.out.println("extractpointer=" + extractpointer + " ");
		error = error / embedpointer * 100;
		System.out.println("error=" + error + " ");

	}

	public void validImage(int[][] orignal, int[][] recover) {
		int row = orignal.length;
		int col = orignal[0].length;
		boolean error = false;
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				if (orignal[i][j] != recover[i][j]) {
					System.out.println("I=" + i + " J=" + j + "有錯誤");
					error = true;
				}
			}
		}
		if (error == true) {
			System.out.println("還原有錯誤");
		} else {
			System.out.println("還原正確");
		}
	}
/*
	public void validAlgo(int lb, int ub) {
		int embedpointer = 0;
		int extractpointer = 0;
		int embednum = 0;
		int extractnum = 0;
		int[] embedbits0 = { 0, 0, 0 };
		int[] embedbits1 = { 0, 0, 1 };
		int[] embedbits2 = { 0, 1, 0 };
		int[] embedbits3 = { 0, 1, 1 };
		int[] embedbits4 = { 1, 0, 0 };
		int[] embedbits5 = { 1, 0, 1 };
		int[] embedbits6 = { 1, 1, 0 };
		int[] embedbits7 = { 1, 1, 1 };

		int[][] stegoBlock = new int[2][2];
		int[][] recoverBlock = new int[2][2];
		int[] extract = new int[3];
		int num = 5000;
		int testgrayarray[][] = new int[2][2];// 原始圖
		int teststegoArray[][] = new int[2][2];// 藏入後圖
		int testrecoverArray[][] = new int[2][2];// 還原圖
		for (int i0 = 15; i0 >= 1; i0--)
			for (int i1 = i0; i1 >= 1; i1--)
				for (int i2 = i1; i2 >= 1; i2--)
					for (int i3 = i2; i3 >= 1; i3--) {
						testgrayarray[0][0] = i0;
						testgrayarray[0][1] = i1;
						testgrayarray[1][0] = i2;
						testgrayarray[1][1] = i3;

						// 000
						System.out.println("-----Cover Image=" + testgrayarray[0][0] + "," + testgrayarray[0][1] + ","
								+ testgrayarray[1][0] + "," + testgrayarray[1][1]);
						embedpointer = embedImage(testgrayarray, embedbits0, num, teststegoArray);// 藏入
						System.out.println("-----Stego Image=" + teststegoArray[0][0] + "," + teststegoArray[0][1] + ","
								+ teststegoArray[1][0] + "," + teststegoArray[1][1]);
						extractpointer = extractImage(teststegoArray, extract, num, testrecoverArray);// 擷取及還原
						validBits(embedbits0, extract, embedpointer, extractpointer);

						// 001
						System.out.println("-----Cover Image=" + testgrayarray[0][0] + "," + testgrayarray[0][1] + ","
								+ testgrayarray[1][0] + "," + testgrayarray[1][1]);
						embedpointer = embedImage(testgrayarray, embedbits1, num, teststegoArray);// 藏入
						System.out.println("-----Stego Image=" + teststegoArray[0][0] + "," + teststegoArray[0][1] + ","
								+ teststegoArray[1][0] + "," + teststegoArray[1][1]);
						extractpointer = extractImage(teststegoArray, extract, num, testrecoverArray);// 擷取及還原
						validBits(embedbits1, extract, embedpointer, extractpointer);

						// 010
						System.out.println("-----Cover Image=" + testgrayarray[0][0] + "," + testgrayarray[0][1] + ","
								+ testgrayarray[1][0] + "," + testgrayarray[1][1]);
						embedpointer = embedImage(testgrayarray, embedbits2, num, teststegoArray);// 藏入
						System.out.println("-----Stego Image=" + teststegoArray[0][0] + "," + teststegoArray[0][1] + ","
								+ teststegoArray[1][0] + "," + teststegoArray[1][1]);
						extractpointer = extractImage(teststegoArray, extract, num, testrecoverArray);// 擷取及還原
						validBits(embedbits2, extract, embedpointer, extractpointer);

						// 011
						System.out.println("-----Cover Image=" + testgrayarray[0][0] + "," + testgrayarray[0][1] + ","
								+ testgrayarray[1][0] + "," + testgrayarray[1][1]);
						embedpointer = embedImage(testgrayarray, embedbits3, num, teststegoArray);// 藏入
						System.out.println("-----Stego Image=" + teststegoArray[0][0] + "," + teststegoArray[0][1] + ","
								+ teststegoArray[1][0] + "," + teststegoArray[1][1]);
						extractpointer = extractImage(teststegoArray, extract, num, testrecoverArray);// 擷取及還原
						validBits(embedbits3, extract, embedpointer, extractpointer);

						// 100
						System.out.println("-----Cover Image=" + testgrayarray[0][0] + "," + testgrayarray[0][1] + ","
								+ testgrayarray[1][0] + "," + testgrayarray[1][1]);
						embedpointer = embedImage(testgrayarray, embedbits4, num, teststegoArray);// 藏入
						System.out.println("-----Stego Image=" + teststegoArray[0][0] + "," + teststegoArray[0][1] + ","
								+ teststegoArray[1][0] + "," + teststegoArray[1][1]);
						extractpointer = extractImage(teststegoArray, extract, num, testrecoverArray);// 擷取及還原
						validBits(embedbits4, extract, embedpointer, extractpointer);

						// 101
						System.out.println("-----Cover Image=" + testgrayarray[0][0] + "," + testgrayarray[0][1] + ","
								+ testgrayarray[1][0] + "," + testgrayarray[1][1]);
						embedpointer = embedImage(testgrayarray, embedbits5, num, teststegoArray);// 藏入
						System.out.println("-----Stego Image=" + teststegoArray[0][0] + "," + teststegoArray[0][1] + ","
								+ teststegoArray[1][0] + "," + teststegoArray[1][1]);
						extractpointer = extractImage(teststegoArray, extract, num, testrecoverArray);// 擷取及還原
						validBits(embedbits5, extract, embedpointer, extractpointer);

						// 110
						System.out.println("-----Cover Image=" + testgrayarray[0][0] + "," + testgrayarray[0][1] + ","
								+ testgrayarray[1][0] + "," + testgrayarray[1][1]);
						embedpointer = embedImage(testgrayarray, embedbits6, num, teststegoArray);// 藏入
						System.out.println("-----Stego Image=" + teststegoArray[0][0] + "," + teststegoArray[0][1] + ","
								+ teststegoArray[1][0] + "," + teststegoArray[1][1]);
						extractpointer = extractImage(teststegoArray, extract, num, testrecoverArray);// 擷取及還原
						validBits(embedbits6, extract, embedpointer, extractpointer);

						// 111
						System.out.println("-----Cover Image=" + testgrayarray[0][0] + "," + testgrayarray[0][1] + ","
								+ testgrayarray[1][0] + "," + testgrayarray[1][1]);
						embedpointer = embedImage(testgrayarray, embedbits7, num, teststegoArray);// 藏入
						System.out.println("-----Stego Image=" + teststegoArray[0][0] + "," + teststegoArray[0][1] + ","
								+ teststegoArray[1][0] + "," + teststegoArray[1][1]);
						extractpointer = extractImage(teststegoArray, extract, num, testrecoverArray);// 擷取及還原
						validBits(embedbits7, extract, embedpointer, extractpointer);
					}

	}
*/
	public static double log10(double x) {
		return Math.log(x) / Math.log(10);
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

}