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

public class hiding_DPM {

	public static void main(String[] args) {
		/* 宣告 */
		hiding_DPM pm = new hiding_DPM();
		BufferedImage bi;
		String path = "Lena2.png";
		String stegofilename = "stegotiffany.bmp";
		String recoverfilename = "recover.bmp";
		try {
			
			int num = 5000000;// 控制藏量
			bi = ImageIO.read(new File(path));
			int h = bi.getHeight();
			int w = bi.getWidth();
			int pix2D[][] = new int[w][h];
			int grayarray[][] = new int[w][h];// 原始圖
			int stegoArray[][] = new int[w][h];// 藏入後圖
			int recoverArray[][] = new int[w][h];// 還原圖
			int embedbits[] = new int[w * h];
			int[] embed_result = new int[3];
			int extract[] = new int[w * h];
			int extractpointer = 0;
			int mark[][] = new int[w][h];
			int push[][] = new int[6][2];
			for (int i = 0; i < embedbits.length; i++) {
				embedbits[i] = (int) (Math.random() * 2);
			}
			pm.loadImage(path, pix2D);// 讀檔
			pm.transToGray(pix2D, grayarray, stegoArray);// 轉灰階
			pm.preprocess(grayarray, stegoArray, mark);// 溢位前處理
			
			//pm.embedImage1(embed_result, embedbits, num, stegoArray);// 藏入1  原史論文
			pm.embedImage2(embed_result, embedbits, num, stegoArray, push);// 藏入2 我的方法

			System.out.println();
			pm.saveImage(stegoArray, stegofilename);// 輸出偽裝影像
			pm.PSNR(grayarray, stegoArray);
			
			
			// 擷取及還原*/
			
			for (int i = 0; i < w; i++) {
				for(int j = 0; j < h; j++) {
					recoverArray[i][j] = stegoArray[i][j];
				}
			}
			
			//extractpointer = pm.extractImage1(extract, embed_result, recoverArray);//提取秘密  原史論文
			extractpointer = pm.extractImage2(extract, embed_result, recoverArray, push);//提取秘密 我的方法
		
			/*
			System.out.println();
			System.out.print("grayarray[0]:   ");
			for(int j = 0; j < h; j++) {
				System.out.print(grayarray[0][j] + " ");
			}
			System.out.println();
			System.out.print("stegoArray[0]:  ");
			for(int j = 0; j < h; j++) {
				System.out.print(stegoArray[0][j] + " ");
			}
			System.out.println();
			System.out.print("recoverArray[0]:");
			for(int j = 0; j < h; j++) {
				System.out.print(recoverArray[0][j] + " ");
			}
			System.out.println();
			
			int xxx = 0;
			for (int i = 0; i < w; i++) {
				for(int j = 0; j < h; j++) {
					if(grayarray[i][j] != recoverArray[i][j]) {
						System.out.println(i + " " + j);
						xxx++;
					}
				}
			}
			System.out.println("cover image 與  recover image 像素不同之數量: " + xxx);
			
			
			System.out.println();
			System.out.print("embedbits[i]:");
			for(int i = 0; i < 1000; i++) {
				System.out.print(embedbits[i] + " ");
			}
			System.out.println();
			System.out.print("extract[i]:  ");
			for(int i = extractpointer - 1; i >= extractpointer - 1000; i--) {
				System.out.print(extract[i] + " ");
			}
			System.out.println();
			xxx = 0;
			if(num > extractpointer) num = extractpointer;
			for (int i = 0; i < num; i++) {
				if(embedbits[i] != extract[extractpointer - 1 - i]) {
					//System.out.println(i);
					xxx++;
				}
			}
			System.out.println("秘密不同之數量: " + xxx);
			*/
			/*
			System.out.println("Start valid");
			for(int b = 0; b < 2; b++) {
				for(int x = 1; x < 10; x++) {
					System.out.println("x:" + x);
					for(int y = 1; y < 10; y++) {
						for(int v1 = 1; v1 < 10; v1++) {
							for(int v3 = 1; v3 < 10; v3++) {
								for(int v4 = 1; v4 < 10; v4++) {
									int[] r1 = pm.embed2(b, x, y, v1, v3, v4);
									int[] r2 = pm.extra2(r1[1], r1[2], v1, v3, v4);
									
									if(r2[0] != r1[0]) System.out.println("b!");
									if(r2[1] != x) System.out.println("x!");
									if(r2[2] != y) System.out.println("y!");
									
									int z = (v1 + v3 + v4) / 3;
									if((r2[0]!= -1 && r2[0] != b) || r2[1] != x || r2[2] != y) {
										System.out.println(x + " " + y + " " + v1 + " " + v3 + " " + v4 + " " + b);
										System.out.println(r2[0] + " " + r2[1] + " " + r2[2]);
									}
								}
							}
						}
					}
				}
			}
			System.out.println("End valid");
			*/
			
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

	/* 轉灰階 */
	public void transToGray(int pix2D[][], int grayarray[][], int stegoArray[][]) {
		int row = grayarray.length;
		int col = grayarray[0].length;
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
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
		for (int i = 0; i < row; i++) { // 先處理0和255
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

	/* 藏入 */
	public void embedImage1(int embed_result[], int embedbits[], int num, int stegoArray[][]) {
		int no = 0;
		int xx = 0;
		int yy = 0;
		int embedpointer = 0;
		int row = stegoArray.length;
		int col = stegoArray[0].length;

		for (int i = 0; i < col - 2; i++) {
			for (int j = 0; j < row - 3; j = j + 2) {
				yy++;

				int x = stegoArray[i][j];
				int y = stegoArray[i][j + 1];
				int z = z(stegoArray, i, j);
				
				int d1 = x - y;
				int d2 = y - z;
				
				if (d1 == 1 && d2 > 0) { // 右上
					stegoArray[i][j] += embedbits[embedpointer];
					embedpointer++; 
				} else if (d1 == -1 && d2 < 0) { // 左下 
					stegoArray[i][j] -= embedbits[embedpointer];
					embedpointer++; 
				} else if ((d1 == 0 && d2 >= 0) || (d1 < 0 && d2 == 0)) { // 左上 
					stegoArray[i][j + 1] += embedbits[embedpointer];
					embedpointer++; 
				} else if ((d1 == 0 && d2 < 0) || (d1 > 0 && d2 == 0) || (d1 == 1 && d2 == -1)) { // 右下
					stegoArray[i][j + 1] -= embedbits[embedpointer];
					embedpointer++; 
				} else if (d1 > 1 && d2 > 0) { // 右上不藏入 
					stegoArray[i][j] += 1;
					no++; 
				} else if (d1 < -1 && d2 < 0) { // 左下不藏入 
					stegoArray[i][j] -= 1;
					no++;
				} else if (d1 < 0 && d2 > 0) { // 左上不藏入 
					stegoArray[i][j + 1] += 1;
					no++; 
				} else if ((d1 > 1 && d2 < 0) || (d1 == 1 && d2 < -1)) { // 右下不藏入
					stegoArray[i][j + 1] -= 1;
					no++;
				} else {
					no++;
					xx++;
				}
				
				if (embedpointer == num  || (i == col-3 && j == row-4)) {
					System.out.println("embedpointer: " + embedpointer);
					System.out.println("no: " + no);
					System.out.println("xx: " + xx);
					System.out.println("yy: " + yy);
					
					System.out.println("i: " + i);
					System.out.println("j: " + j);
					
					embed_result[0] = embedpointer;
					embed_result[1] = i;
					embed_result[2] = j;
					return;
				}
			}
		}
	}
	
	public void embedImage2(int embed_result[], int embedbits[], int num, int stegoArray[][], int push[][]) {
		int no = 0;
		int xx = 0;
		int yy = 0;
		int embedpointer = 0;
		int row = stegoArray.length;
		int col = stegoArray[0].length;
		int pixels[][] = new int[3][507];
		int tmp[][][] = new int[6][11][11];
		
		int times[] = new int[13];
		
		
		for (int i = 0; i < row - 1; i++) {
			for (int j = 0; j < col - 2; j = j + 2) {

				int x = stegoArray[i][j];
				int y = stegoArray[i][j + 1];
				int z = zzz(stegoArray, i, j);
				int zz = zz(stegoArray, i, j);
	
				int d1 = x - y;
				int d2 = y - z;
				int d3 = zz - x;
				/*
				if(i==0 && j==268) {
					System.out.println("x: " + x);
					System.out.println("y: " + y);
					System.out.println("z: " + z);
					System.out.println("zz: " + zz);
					System.out.println("d1: " + d1);
					System.out.println("d2: " + d2);
					System.out.println("d3: " + d3);
					for(int ii=0;ii<13;ii++) 
						System.out.println("times: " + (ii+1) + " " + times[ii]);
				}
				*/
				//if(d1 == -1 && d2 <= 0 && d3 == 1) System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
				if ((d1 >= 0 && d2 >= 0 && d3 == 0) || (d1 == 0 && d2 >= 0 && d3 < 0)) { // x+b
					stegoArray[i][j] += embedbits[embedpointer];
					embedpointer++;
					if(d1 >= 0 && d2 >= 0 && d3 == 0) times[0]++;
					else times[1]++;
				} else if (((d1 <= 0 && d2 <= 0 && d3 == 0) && !(d1 == 0 && d2 == 0 && d3 == 0)) || (d1 == 0 && d2 <= 0 && d3 > 0) || (d1 == -1 && d2 == 0 && d3 == 1)) { // x-b
					stegoArray[i][j] -= embedbits[embedpointer];
					embedpointer++;
					if(d1 < 0 && d2 <= 0 && d3 == 0) times[2]++;
					else if(d1 == 0 && d2 <= 0 && d3 > 0) times[3]++;
					else times[4]++;
				} else if ((d1 == -1 && d2 >= 1 && d3 >= 0) || (d1 < -1 && d2 == 1 && d3 >= 0)) { // y+b
					stegoArray[i][j + 1] += embedbits[embedpointer];
					embedpointer++;
					if(d1 == -1 && d2 >= 1 && d3 >= 0) times[5]++;
					else times[6]++;
				} else if ((d1 == 1 && d2 <= -1 && d3 <= 0) || (d1 > 1 && d2 == -1 && d3 <= 0)) { // y-b
					stegoArray[i][j + 1] -= embedbits[embedpointer];
					embedpointer++;
					if(d1 == 1 && d2 <= -1 && d3 <= 0) times[7]++;
					else times[8]++;
				} else if ((d1 <= -1 && d2 == 0 && d3 <= -1) || (d1 <= -1 && d2 > 0 && d3 == -1)) { // x+b y+b
					stegoArray[i][j] += embedbits[embedpointer];
					stegoArray[i][j + 1] += embedbits[embedpointer];
					embedpointer++;
					if(d1 <= -1 && d2 == 0 && d3 <= -1) times[9]++;
					else times[10]++;
				} else if ((d1 >= 1 && d2 == 0 && d3 >= 1) || (d1 >= 1 && d2 < 0 && d3 == 1)) { // x-b y-b
					stegoArray[i][j] -= embedbits[embedpointer];
					stegoArray[i][j + 1] -= embedbits[embedpointer];
					embedpointer++;
					if(d1 >= 1 && d2 == 0 && d3 >= 1) times[11]++;
					else times[12]++;
				} else if (d1 > 0 && d2 >= 0 && d3 < 0) { // 不藏入
					stegoArray[i][j] += 1;
					no++;
				} else if ((d1 < -1 && d2 == 0 && d3 >= 1) || (d1 == -1 && d2 == 0 && d3 > 1) || (d1 < 0 && d2 < 0 && d3 > 0)) { // 不藏入
					stegoArray[i][j] -= 1;
					no++;
				} else if (d1 < -1 && d2 > 1 && d3 >= 0) { // 不藏入
					stegoArray[i][j + 1] += 1;
					no++;
				} else if (d1 > 1 && d2 < -1 && d3 <= 0) { // 不藏入
					stegoArray[i][j + 1] -= 1;
					no++;
				} else if (d1 <= -1 && d2 > 0 && d3 < -1) { // 不藏入
					stegoArray[i][j] += 1;
					stegoArray[i][j + 1] += 1;
					no++;
				} else if (d1 >= 1 && d2 < 0 && d3 > 1) { // 不藏入
					stegoArray[i][j] -= 1;
					stegoArray[i][j + 1] -= 1;
					no++;
				} else {
					no++;
					xx++;
				}
				/*
				if(i==0 && j==268) {
					System.out.println("x: " + stegoArray[i][j]);
					System.out.println("y: " + stegoArray[i][j+1]);
					for(int ii=0;ii<13;ii++) 
						System.out.println("times: " + (ii+1) + " " + times[ii]);
				}
				*/
				
				//System.out.println("i: " + i + " j: " + j);
				if (embedpointer == num  || (i == row-2 && (j == col-4 || (j == col-3)))) {
					System.out.println("embedpointer: " + embedpointer);
					System.out.println("no: " + no);
					System.out.println("xx: " + xx);
					System.out.println(i + " " + j);
					/*for(int ii=0;ii<13;ii++) 
						System.out.println("times: " + (ii+1) + " " + times[ii]);*/
					
					/*
					for(int ii=0;ii<11;ii++) 
						System.out.println("quadrant: " + ii + " " + quadrant[ii]);
					*/
					embed_result[0] = embedpointer;
					embed_result[1] = i;
					embed_result[2] = j;
					return;
				}
			}
		}
		/*
		System.out.println("embedpointer: " + embedpointer);
		System.out.println("no: " + no);
		System.out.println("xx: " + xx);
		*/
		/*
		for(int i=0;i<507;i++) {
			System.out.println("d1: " + (i-253) + " " + pixels[0][i]);
		}
		for(int i=0;i<507;i++) {
			System.out.println("d2: " + (i-253) + " " + pixels[1][i]);
		}
		for(int i=0;i<507;i++) {
			System.out.println("d3: " + (i-253) + " " + pixels[2][i]);
		}
		*/
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
	
	public int zz(int stegoArray[][], int i, int j) {
		int v1 = stegoArray[i][j + 2];
		int v3 = stegoArray[i + 1][j];
		int v4 = stegoArray[i + 1][j + 1];
		int v5 = stegoArray[i + 1][j + 2];
		
		float a = 0.33f;
		float b = 0.25f;
		
		return (int)((1-a) * (1-b) * v3 + (1-a) * b * v1 + a * (1-b) * v4 + a * b * v5);
	}
	
	public int zzz(int stegoArray[][], int i, int j) {
		int v1 = stegoArray[i][j + 2];
		int v3 = stegoArray[i + 1][j];
		int v4 = stegoArray[i + 1][j + 1];
		int v5 = stegoArray[i + 1][j + 2];
		
		float a = 0.66f;
		float b = 0.5f;
		
		return (int)((1-a) * (1-b) * v3 + (1-a) * b * v1 + a * (1-b) * v4 + a * b * v5);
	}

	public int extractImage1(int extract[], int embed_result[], int recoverArray[][]) {
		// extract
		int no = 0;
		int xx = 0;
		int yy = 0;
		int extractedpointer = 0;
		int row = recoverArray.length;
		int col = recoverArray[0].length;
		boolean first = true;

		for (int i = embed_result[1]; i >= 0; i--) {
			for (int j = row - 4; j >= 0; j = j - 2) {
				if(first && i == embed_result[1]) {
					j = embed_result[2];
					first = false;
				}
				//System.out.println(i + " " + j);
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
		System.out.println("no: " + no);
		System.out.println("yy: " + yy);
		return extractedpointer;
	}
	
	public int extractImage2(int extract[], int embed_result[], int recoverArray[][], int push[][]) {
		// extract
		int no = 0;
		int xx = 0;
		int extractedpointer = 0;
		int row = recoverArray.length;
		int col = recoverArray[0].length;
		boolean first = true;

		for (int i = embed_result[1]; i >= 0; i--) {
			for (int j = col - (col%2==0 ? 4 : 3); j >= 0; j = j - 2) {
				if(first && i == embed_result[1]) {
					j = embed_result[2];
					first = false;
				}
				int x = recoverArray[i][j];
				int y = recoverArray[i][j + 1];
				int z = zzz(recoverArray, i, j);
				int zz = zz(recoverArray, i, j);
	
				int d1 = x - y;
				int d2 = y - z;
				int d3 = zz - x;
				/*
				if(i==0 && j==268) {
					System.out.println("x: " + x);
					System.out.println("y: " + y);
					System.out.println("z: " + z);
					System.out.println("zz: " + zz);
					System.out.println("d1: " + d1);
					System.out.println("d2: " + d2);
					System.out.println("d3: " + d3);
				}
				*/
				if ((d1 >= 0 && d2 >= 0 && d3 == 0) || (d1 > 0 && d2 >= 0 && d3 == -1)) { // x+b
					extract[extractedpointer] = -1 * d3;
					recoverArray[i][j] = recoverArray[i][j] - extract[extractedpointer];
					extractedpointer++;
				} else if ((d1 == 0 && d2 >= 0 && d3 < 0) || (d1 == 1 && d2 >= 0 && d3 < -1)) { // x+b
					extract[extractedpointer] = d1;
					recoverArray[i][j] = recoverArray[i][j] - extract[extractedpointer];
					extractedpointer++;
				} 
				
				else if (((d1 <= 0 && d2 <= 0 && d3 == 0) || (d1 < 0 && d2 <= 0 && d3 == 1)) && !(d1 == 0 && d2 == 0 && d3 == 0) && !(d1 == -1 && d2 == 0 && d3 == 1)) { // x-b
					extract[extractedpointer] = d3;
					recoverArray[i][j] = recoverArray[i][j] + extract[extractedpointer];
					extractedpointer++;
				} else if ((d1 == 0 && d2 <= 0 && d3 > 0) || (d1 == -1 && d2 <= 0 && d3 > 1)) { // x-b
					extract[extractedpointer] = -d1;
					recoverArray[i][j] = recoverArray[i][j] + extract[extractedpointer];
					extractedpointer++;
				} else if ((d1 == -1 && d2 == 0 && d3 == 1) || (d1 == -2 && d2 == 0 && d3 == 2)) { // x-b
					extract[extractedpointer] = d3 - 1;
					recoverArray[i][j] = recoverArray[i][j] + extract[extractedpointer];
					extractedpointer++;
				} 
				
				else if ((d1 == -1 && d2 >= 1 && d3 >= 0) || (d1 == -2 && d2 > 1 && d3 >= 0)) { // y+b
					extract[extractedpointer] = -1 * d1 - 1;
					recoverArray[i][j + 1] = recoverArray[i][j + 1] - extract[extractedpointer];
					extractedpointer++;
				} else if ((d1 < -1 && d2 == 1 && d3 >= 0) || (d1 < -2 && d2 == 2 && d3 >= 0)) { // y+b
					extract[extractedpointer] = d2 - 1;
					recoverArray[i][j + 1] = recoverArray[i][j + 1] - extract[extractedpointer];
					extractedpointer++;
				} 
				
				else if ((d1 == 1 && d2 <= -1 && d3 <= 0) || (d1 == 2 && d2 < -1 && d3 <= 0)) { // y-b
					extract[extractedpointer] = d1 - 1;
					recoverArray[i][j + 1] = recoverArray[i][j + 1] + extract[extractedpointer];
					extractedpointer++;
				} else if ((d1 > 1 && d2 == -1 && d3 <= 0) || (d1 > 2 && d2 == -2 && d3 <= 0)) { // y-b
					extract[extractedpointer] = -d2 - 1;
					recoverArray[i][j + 1] = recoverArray[i][j + 1] + extract[extractedpointer];
					extractedpointer++;
				}
				
				else if ((d1 <= -1 && d2 == 0 && d3 <= -1) || (d1 <= -1 && d2 == 1 && d3 < -1)) { // x+b,y+b
					extract[extractedpointer] = d2;
					recoverArray[i][j] = recoverArray[i][j] - extract[extractedpointer];
					recoverArray[i][j + 1] = recoverArray[i][j + 1] - extract[extractedpointer];
					extractedpointer++;
				} else if ((d1 <= -1 && d2 > 0 && d3 == -1) || (d1 <= -1 && d2 > 1 && d3 == -2)) { // x+b,y+b
					extract[extractedpointer] = -1 * d3 - 1;
					recoverArray[i][j] = recoverArray[i][j] - extract[extractedpointer];
					recoverArray[i][j + 1] = recoverArray[i][j + 1] - extract[extractedpointer];
					extractedpointer++;
				}
				
				else if ((d1 >= 1 && d2 == 0 && d3 >= 1) || (d1 >= 1 && d2 == -1 && d3 > 1)) { // x-b,y-b
					extract[extractedpointer] = -1 * d2;
					recoverArray[i][j] = recoverArray[i][j] + extract[extractedpointer];
					recoverArray[i][j + 1] = recoverArray[i][j + 1] + extract[extractedpointer];
					extractedpointer++;
				} else if ((d1 >= 1 && d2 < 0 && d3 == 1) || (d1 >= 1 && d2 < -1 && d3 == 2)) { // x-b,y-b
					extract[extractedpointer] = d3 - 1;
					recoverArray[i][j] = recoverArray[i][j] + extract[extractedpointer];
					recoverArray[i][j + 1] = recoverArray[i][j + 1] + extract[extractedpointer];
					extractedpointer++;
				}
				
				
				else if (d1 > 1 && d2 >= 0 && d3 < -1) { // 無秘密
					recoverArray[i][j] = recoverArray[i][j] - 1;
					no++;
				} else if ((d1 < -2 && d2 == 0 && d3 > 1) || (d1 == -2 && d2 == 0 && d3 > 2) || (d1 < -1 && d2 < 0 && d3 > 1)) { // 不藏入
					recoverArray[i][j] = recoverArray[i][j] + 1;
					no++;
				} else if (d1 < -2 && d2 > 2 && d3 >= 0) { // 無秘密
					recoverArray[i][j + 1] = recoverArray[i][j + 1] - 1;
					no++;
				} else if (d1 > 2 && d2 < -2 && d3 <= 0) { // 無秘密
					recoverArray[i][j + 1] = recoverArray[i][j + 1] + 1;
					no++;
				} else if (d1 <= -1 && d2 > 1 && d3 < -2) { // 無秘密
					recoverArray[i][j] = recoverArray[i][j] - 1;
					recoverArray[i][j + 1] = recoverArray[i][j + 1] - 1;
					no++;
				} else if (d1 >= 1 && d2 < -1 && d3 > 2) { // 無秘密
					recoverArray[i][j] = recoverArray[i][j] + 1;
					recoverArray[i][j + 1] = recoverArray[i][j + 1] + 1;
					no++;
				} else {
					no++;
					xx++;
				}
				/*
				if(i==0 && j==268) {
					System.out.println("x: " + recoverArray[i][j]);
					System.out.println("y: " + recoverArray[i][j+1]);
				}
				*/
			}	
		}
		
		System.out.println("");
		System.out.println("extractedpointer: " + extractedpointer);
		System.out.println("no: " + no);
		System.out.println("xx: " + xx);
		return extractedpointer;

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
		System.out.println();
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
	 * public void validAlgo(int lb, int ub) { int embedpointer = 0; int
	 * extractpointer = 0; int embednum = 0; int extractnum = 0; int[] embedbits0 =
	 * { 0, 0, 0 }; int[] embedbits1 = { 0, 0, 1 }; int[] embedbits2 = { 0, 1, 0 };
	 * int[] embedbits3 = { 0, 1, 1 }; int[] embedbits4 = { 1, 0, 0 }; int[]
	 * embedbits5 = { 1, 0, 1 }; int[] embedbits6 = { 1, 1, 0 }; int[] embedbits7 =
	 * { 1, 1, 1 };
	 * 
	 * int[][] stegoBlock = new int[2][2]; int[][] recoverBlock = new int[2][2];
	 * int[] extract = new int[3]; int num = 5000; int testgrayarray[][] = new
	 * int[2][2];// 原始圖 int teststegoArray[][] = new int[2][2];// 藏入後圖 int
	 * testrecoverArray[][] = new int[2][2];// 還原圖 for (int i0 = 15; i0 >= 1; i0--)
	 * for (int i1 = i0; i1 >= 1; i1--) for (int i2 = i1; i2 >= 1; i2--) for (int i3
	 * = i2; i3 >= 1; i3--) { testgrayarray[0][0] = i0; testgrayarray[0][1] = i1;
	 * testgrayarray[1][0] = i2; testgrayarray[1][1] = i3;
	 * 
	 * // 000 System.out.println("-----Cover Image=" + testgrayarray[0][0] + "," +
	 * testgrayarray[0][1] + "," + testgrayarray[1][0] + "," + testgrayarray[1][1]);
	 * embedpointer = embedImage(testgrayarray, embedbits0, num, teststegoArray);//
	 * 藏入 System.out.println("-----Stego Image=" + teststegoArray[0][0] + "," +
	 * teststegoArray[0][1] + "," + teststegoArray[1][0] + "," +
	 * teststegoArray[1][1]); extractpointer = extractImage(teststegoArray, extract,
	 * num, testrecoverArray);// 擷取及還原 validBits(embedbits0, extract, embedpointer,
	 * extractpointer);
	 * 
	 * // 001 System.out.println("-----Cover Image=" + testgrayarray[0][0] + "," +
	 * testgrayarray[0][1] + "," + testgrayarray[1][0] + "," + testgrayarray[1][1]);
	 * embedpointer = embedImage(testgrayarray, embedbits1, num, teststegoArray);//
	 * 藏入 System.out.println("-----Stego Image=" + teststegoArray[0][0] + "," +
	 * teststegoArray[0][1] + "," + teststegoArray[1][0] + "," +
	 * teststegoArray[1][1]); extractpointer = extractImage(teststegoArray, extract,
	 * num, testrecoverArray);// 擷取及還原 validBits(embedbits1, extract, embedpointer,
	 * extractpointer);
	 * 
	 * // 010 System.out.println("-----Cover Image=" + testgrayarray[0][0] + "," +
	 * testgrayarray[0][1] + "," + testgrayarray[1][0] + "," + testgrayarray[1][1]);
	 * embedpointer = embedImage(testgrayarray, embedbits2, num, teststegoArray);//
	 * 藏入 System.out.println("-----Stego Image=" + teststegoArray[0][0] + "," +
	 * teststegoArray[0][1] + "," + teststegoArray[1][0] + "," +
	 * teststegoArray[1][1]); extractpointer = extractImage(teststegoArray, extract,
	 * num, testrecoverArray);// 擷取及還原 validBits(embedbits2, extract, embedpointer,
	 * extractpointer);
	 * 
	 * // 011 System.out.println("-----Cover Image=" + testgrayarray[0][0] + "," +
	 * testgrayarray[0][1] + "," + testgrayarray[1][0] + "," + testgrayarray[1][1]);
	 * embedpointer = embedImage(testgrayarray, embedbits3, num, teststegoArray);//
	 * 藏入 System.out.println("-----Stego Image=" + teststegoArray[0][0] + "," +
	 * teststegoArray[0][1] + "," + teststegoArray[1][0] + "," +
	 * teststegoArray[1][1]); extractpointer = extractImage(teststegoArray, extract,
	 * num, testrecoverArray);// 擷取及還原 validBits(embedbits3, extract, embedpointer,
	 * extractpointer);
	 * 
	 * // 100 System.out.println("-----Cover Image=" + testgrayarray[0][0] + "," +
	 * testgrayarray[0][1] + "," + testgrayarray[1][0] + "," + testgrayarray[1][1]);
	 * embedpointer = embedImage(testgrayarray, embedbits4, num, teststegoArray);//
	 * 藏入 System.out.println("-----Stego Image=" + teststegoArray[0][0] + "," +
	 * teststegoArray[0][1] + "," + teststegoArray[1][0] + "," +
	 * teststegoArray[1][1]); extractpointer = extractImage(teststegoArray, extract,
	 * num, testrecoverArray);// 擷取及還原 validBits(embedbits4, extract, embedpointer,
	 * extractpointer);
	 * 
	 * // 101 System.out.println("-----Cover Image=" + testgrayarray[0][0] + "," +
	 * testgrayarray[0][1] + "," + testgrayarray[1][0] + "," + testgrayarray[1][1]);
	 * embedpointer = embedImage(testgrayarray, embedbits5, num, teststegoArray);//
	 * 藏入 System.out.println("-----Stego Image=" + teststegoArray[0][0] + "," +
	 * teststegoArray[0][1] + "," + teststegoArray[1][0] + "," +
	 * teststegoArray[1][1]); extractpointer = extractImage(teststegoArray, extract,
	 * num, testrecoverArray);// 擷取及還原 validBits(embedbits5, extract, embedpointer,
	 * extractpointer);
	 * 
	 * // 110 System.out.println("-----Cover Image=" + testgrayarray[0][0] + "," +
	 * testgrayarray[0][1] + "," + testgrayarray[1][0] + "," + testgrayarray[1][1]);
	 * embedpointer = embedImage(testgrayarray, embedbits6, num, teststegoArray);//
	 * 藏入 System.out.println("-----Stego Image=" + teststegoArray[0][0] + "," +
	 * teststegoArray[0][1] + "," + teststegoArray[1][0] + "," +
	 * teststegoArray[1][1]); extractpointer = extractImage(teststegoArray, extract,
	 * num, testrecoverArray);// 擷取及還原 validBits(embedbits6, extract, embedpointer,
	 * extractpointer);
	 * 
	 * // 111 System.out.println("-----Cover Image=" + testgrayarray[0][0] + "," +
	 * testgrayarray[0][1] + "," + testgrayarray[1][0] + "," + testgrayarray[1][1]);
	 * embedpointer = embedImage(testgrayarray, embedbits7, num, teststegoArray);//
	 * 藏入 System.out.println("-----Stego Image=" + teststegoArray[0][0] + "," +
	 * teststegoArray[0][1] + "," + teststegoArray[1][0] + "," +
	 * teststegoArray[1][1]); extractpointer = extractImage(teststegoArray, extract,
	 * num, testrecoverArray);// 擷取及還原 validBits(embedbits7, extract, embedpointer,
	 * extractpointer); }
	 * 
	 * }
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
	

	
	public void embed1(int x, int y, int z) {
		
		int ax = x;
		int ay = y;
		int d1 = x - y;
		int d2 = y - z;
		
		int b = 0;
		if (d1 == 1 && d2 > 0) { // 右上
			ax += b;
		} else if (d1 == -1 && d2 < 0) { // 左下 
			ax -= b;
		} else if ((d1 == 0 && d2 >= 0) || (d1 < 0 && d2 == 0)) { // 左上 
			ay += b;
		} else if ((d1 == 0 && d2 < 0) || (d1 > 0 && d2 == 0) || (d1 == 1 && d2 == -1)) { // 右下
			ay -= b;
		} else if (d1 > 1 && d2 > 0) { // 右上不藏入 
			ax += 1;
		} else if (d1 < -1 && d2 < 0) { // 左下不藏入 
			ax -= 1;
		} else if (d1 < 0 && d2 > 0) { // 左上不藏入 
			ay += 1;
		} else if ((d1 > 1 && d2 < 0) || (d1 == 1 && d2 < -1)) { // 右下不藏入
			ay -= 1;
		}
		
		
		d1 = ax - ay;
		d2 = ay - z;
		
		int bb = 0;
		if ((d1 == 1 || d1 == 2) && d2 > 0) { // x+b
			bb = d1 - 1;
			ax = ax - bb;
		} else if ((d1 == -1 || d1 == -2) && d2 < 0) { // x-b
			bb = -1 * d1 - 1;
			ax = ax + bb;
		} 
		
		else if ((d1 == 0 && d2 >= 0) || (d1 == -1 && d2 >= 1)) { // y+b
			bb = -1 * d1;
			ay = ay - bb;
		} else if ((d1 < 0 && d2 == 0) || (d1 < -1 && d2 == 1)) { // y+b
			bb = d2;
			ay = ay - bb;
		}
		
		else if ((d1 == 0 && d2 < 0) || (d1 == 1 && d2 < -1)) { // y-b
			bb = d1;
			ay = ay + bb;
		} else if ((d1 > 0 && d2 == 0) || (d1 > 1 && d2 == -1)) { // y-b
			bb = -1 * d2;
			ay = ay + bb;
		} else if ((d1 == 1 && d2 == -1) || (d1 == 2 && d2 == -2)) { // y-b
			bb = d1 - 1;
			ay = ay + bb;
		} 
		
		else if (d1 > 2 && d2 > 0) { // x+b
			ax = ax - 1;
		}
		
		else if (d1 < -2 && d2 < 0) { // x-b
			ax = ax + 1;
		}
		
		else if (d1 < -1 && d2 > 1) { // y+b
			ay = ay - 1;
		}
		
		else if (d1 > 2 && d2 < -1) { // y-b
			ay = ay + 1;
		} else if (d1 == 2 && d2 < -2) { // y-b
			ay = ay + 1;
		}
		
		if(ax != x) System.out.println("x!");
		if(ay != y) System.out.println("y!");
		if(bb != b) System.out.println("b!");
		if(ax != x || ay != y || bb != b) System.out.println(x + " " + y + " " + z);
		//if(ax == x && ay == y && bb == b) System.out.println("true!");
		
		
		
		
		
		
		
		
		
		
		
		d1 = x - y;
		d2 = y - z;
		
		b = 1;
		if (d1 == 1 && d2 > 0) { // 右上
			ax += b;
		} else if (d1 == -1 && d2 < 0) { // 左下 
			ax -= b;
		} else if ((d1 == 0 && d2 >= 0) || (d1 < 0 && d2 == 0)) { // 左上 
			ay += b;
		} else if ((d1 == 0 && d2 < 0) || (d1 > 0 && d2 == 0) || (d1 == 1 && d2 == -1)) { // 右下
			ay -= b;
		} else if (d1 > 1 && d2 > 0) { // 右上不藏入 
			ax += 1;
			b = -1;
		} else if (d1 < -1 && d2 < 0) { // 左下不藏入 
			ax -= 1;
			b = -1;
		} else if (d1 < 0 && d2 > 0) { // 左上不藏入 
			ay += 1;
			b = -1;
		} else if ((d1 > 1 && d2 < 0) || (d1 == 1 && d2 < -1)) { // 右下不藏入
			ay -= 1;
			b = -1;
		}
		
		
		d1 = ax - ay;
		d2 = ay - z;
		
		bb = 0;
		if ((d1 == 1 || d1 == 2) && d2 > 0) { // x+b
			bb = d1 - 1;
			ax = ax - bb;
		} else if ((d1 == -1 || d1 == -2) && d2 < 0) { // x-b
			bb = -1 * d1 - 1;
			ax = ax + bb;
		} 
		
		else if ((d1 == 0 && d2 >= 0) || (d1 == -1 && d2 >= 1)) { // y+b
			bb = -1 * d1;
			ay = ay - bb;
		} else if ((d1 < 0 && d2 == 0) || (d1 < -1 && d2 == 1)) { // y+b
			bb = d2;
			ay = ay - bb;
		}
		
		else if ((d1 == 0 && d2 < 0) || (d1 == 1 && d2 < -1)) { // y-b
			bb = d1;
			ay = ay + bb;
		} else if ((d1 > 0 && d2 == 0) || (d1 > 1 && d2 == -1)) { // y-b
			bb = -1 * d2;
			ay = ay + bb;
		} else if ((d1 == 1 && d2 == -1) || (d1 == 2 && d2 == -2)) { // y-b
			bb = d1 - 1;
			ay = ay + bb;
		} 
		
		else if (d1 > 2 && d2 > 0) { // x+b
			ax = ax - 1;
			bb = -1;
		}
		
		else if (d1 < -2 && d2 < 0) { // x-b
			ax = ax + 1;
			bb = -1;
		}
		
		else if (d1 < -1 && d2 > 1) { // y+b
			ay = ay - 1;
			bb = -1;
		}
		
		else if (d1 > 2 && d2 < -1) { // y-b
			ay = ay + 1;
			bb = -1;
		} else if (d1 == 2 && d2 < -2) { // y-b
			ay = ay + 1;
			bb = -1;
		}
		
		if(ax != x) System.out.println("1 x!");
		if(ay != y) System.out.println("1 y!");
		if(bb != b) System.out.println("1 b!");
		if(ax != x || ay != y || bb != b) {
			System.out.println("1 " + x + " " + y + " " + z + " " + b);
			System.out.println("1 " + ax + " " + ay + " " + z + " " + bb);
		}
		//if(ax == x && ay == y && bb == b) System.out.println("1 true!");
	}
	

	
	public int[] embed2(int b, int x, int y, int v1, int v3, int v4) {
		//System.out.println(x + " " + y + " " + v3 + " " + v4);
		
		int z = (v1 + v3 + v4) / 3;
		int ax = x;
		int ay = y;
		int d1 = x - y;
		int d2 = y - z;
		int d3 = z - x;
				
		if ((d1 >= 0 && d2 >= 0 && d3 == 0) || (d1 == 0 && d2 >= 0 && d3 < 0)) { // x+b
			ax += b;
		} else if ((d1 < 0 && d2 <= 0 && d3 == 0) || (d1 == 0 && d2 <= 0 && d3 > 0) || (d1 == -1 && d2 <= 0 && d3 == 1)) { // x-b
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
		
		else if (d1 > 0 && d2 >= 0 && d3 < 0) { // 不藏入
			ax += 1;
			b = -1;
		} else if ((d1 < -1 && d2 <= 0 && d3 >= 1) || (d1 == -1 && d2 <= 0 && d3 > 1)) { // 不藏入
			ax -= 1;
			b = -1;
		} else if (d1 < -1 && d2 > 1 && d3 >= 0) { // 不藏入
			ay += 1;
			b = -1;
		} else if (d1 > 1 && d2 < -1 && d3 <= 0) { // 不藏入
			ay -= 1;
			b = -1;
		} else if (d1 <= -1 && d2 > 0 && d3 < -1) { // 不藏入
			ax += 1;
			ay += 1;
			b = -1;
		} else if (d1 >= 1 && d2 < 0 && d3 > 1) { // 不藏入
			ax -= 1;
			ay -= 1;
			b = -1;
		}
		
		int[] result = new int[3];
		result[0] = b;
		result[1] = ax;
		result[2] = ay;
		
		return result;
	}
	
	public int[] extra2(int x, int y, int v1, int v3, int v4) {
		
		int z = (v1 + v3 + v4) / 3;
		int d1 = x - y;
		int d2 = y - z;
		int d3 = z - x;
		
		int bb = 0;
		if ((d1 >= 0 && d2 >= 0 && d3 == 0) || (d1 > 0 && d2 >= 0 && d3 == -1)) { // x+b
			bb = -1 * d3;
			x = x - bb;
		} else if ((d1 == 0 && d2 >= 0 && d3 < 0) || (d1 == 1 && d2 >= 0 && d3 < -1)) { // x+b
			bb = d1;
			x = x - bb;
		} 
		
		else if ((d1 <= -1 && d2 <= 0 && d3 == 0) || (d1 < -1 && d2 <= 0 && d3 == 1)) { // x-b
			bb = d3;
			x = x + bb;
		} else if ((d1 == 0 && d2 <= 0 && d3 > 0) || (d1 == -1 && d2 <= 0 && d3 > 1)) { // x-b
			bb = -d1;
			x = x + bb;
		} else if ((d1 == -1 && d2 <= 0 && d3 == 1) || (d1 == -2 && d2 <= 0 && d3 == 2)) { // x-b
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
		
		
		else if (d1 > 1 && d2 >= 0 && d3 < -1) { // 無秘密
			x = x - 1;
			bb = -1;
		} else if ((d1 < -2 && d2 <= 0 && d3 > 1) || (d1 == -2 && d2 <= 0 && d3 > 2)) { // 不藏入
			x = x + 1;
			bb = -1;
		} else if (d1 < -2 && d2 > 2 && d3 >= 0) { // 無秘密
			y = y - 1;
			bb = -1;
		} else if (d1 > 2 && d2 < -2 && d3 <= 0) { // 無秘密
			y = y + 1;
			bb = -1;
		} else if (d1 <= -1 && d2 > 1 && d3 < -2) { // 無秘密
			x = x - 1;
			y = y - 1;
			bb = -1;
		} else if (d1 >= 1 && d2 < -1 && d3 > 2) { // 無秘密
			x = x + 1;
			y = y + 1;
			bb = -1;
		} 
		
		int[] result = new int[3];
		result[0] = bb;
		result[1] = x;
		result[2] = y;
		
		return result;
	}

}




/*
if(ax != x) System.out.println("x!");
		if(ay != y) System.out.println("y!");
		if(bb != b) System.out.println("b!");
		if(ax != x || ay != y || bb != b) {
			System.out.println(x + " " + y + " " + z + " " + bb);
			System.out.println(d1 + " " + d2 + " " + d3);
			System.out.println(ax + " " + ay + " " + z + " " + bb);
		}
 */






/*
else if ((d1 < 0 && d2 == -1 && d3 > 0 && 2 * d1 + d3 == 0) || (d1 == -1 && d2 < -1 && d3 == 2)) { // x+b,z-b
	b += embedbits[embedpointer];
	embedpointer++;
	stegoArray[i + 1][j] -= embedbits[embedpointer];
	embedpointer++;
} else if ((d1 > 0 && d2 == 1 && d3 < 0 && 2 * d1 + d3 == 0) || (d1 == 1 && d2 > 1 && d3 == -2)) { // x-b,z+b
	stegoArray[i][j] -= embedbits[embedpointer];
	embedpointer++;
	stegoArray[i + 1][j] += embedbits[embedpointer];
	embedpointer++;
} else if ((d1 > 0 && d2 == -2 && d3 > 0 && d1 - d3 == 0) || (d1 == 1 && d2 < -2 && d3 == 1)) { // y-b,z+b
	stegoArray[i][j + 1] -= embedbits[embedpointer];
	embedpointer++;
	stegoArray[i + 1][j] += embedbits[embedpointer];
	embedpointer++;
} else if ((d1 < 0 && d2 == 2 && d3 < 0 && d1 - d3 == 0) || (d1 == -1 && d2 > 2 && d3 == -1)) { // y+b,z-b
	stegoArray[i][j + 1] += embedbits[embedpointer];
	embedpointer++;
	stegoArray[i + 1][j] -= embedbits[embedpointer];
	embedpointer++;
} else if ((d1 < 0 && d2 == 1 && d3 > 0 && d1 + 2 * d3 == 0) || (d1 == -2 && d2 > 1 && d3 == 1)) { // x-b,y+b
	stegoArray[i][j] -= embedbits[embedpointer];
	embedpointer++;
	stegoArray[i][j + 1] += embedbits[embedpointer];
	embedpointer++;
} else if ((d1 > 0 && d2 == -1 && d3 < 0 && d1 + 2 * d3 == 0) || (d1 == 2 && d2 < -1 && d3 == -1)) { // x+b,y-b
	stegoArray[i][j] += embedbits[embedpointer];
	embedpointer++;
	stegoArray[i][j + 1] -= embedbits[embedpointer];
	embedpointer++;
} else if (d1 < 0 && d2 <= -1 && d3 > 0 && 2 * d1 + d3 == 0) { // 不藏入
	stegoArray[i][j] += 1;
	stegoArray[i + 1][j] -= 1;
	no++;
} else if (d1 > 0 && d2 >= 1 && d3 < 0 && 2 * d1 + d3 == 0) { // 不藏入
	stegoArray[i][j] -= 1;
	stegoArray[i + 1][j] += 1;
	no++;
} else if (d1 > 0 && d2 <= -2 && d3 > 0 && d1 - d3 == 0) { // 不藏入
	stegoArray[i][j + 1] -= 1;
	stegoArray[i + 1][j] += 1;
	no++;
} else if (d1 < 0 && d2 >= 2 && d3 < 0 && d1 - d3 == 0) { // 不藏入
	stegoArray[i][j + 1] -= 1;
	stegoArray[i + 1][j] += 1;
	no++;
} else if (d1 < 0 && d2 >= 1 && d3 > 0 && d1 + 2 * d3 == 0) { // 不藏入
	stegoArray[i][j] -= 1;
	stegoArray[i][j + 1] += 1;
	no++;
} else if (d1 > 0 && d2 <= -1 && d3 < 0 && d1 + 2 * d3 == 0) { // 不藏入
	stegoArray[i][j] += 1;
	stegoArray[i][j + 1] -= 1;
	no++;
}*/





















/*

public void embedImage2(int embed_result[], int embedbits[], int num, int stegoArray[][], int push[][]) {
		int no = 0;
		int xx = 0;
		int yy = 0;
		int embedpointer = 0;
		int row = stegoArray.length;
		int col = stegoArray[0].length;
		int pixels[][] = new int[3][507];
		int tmp[][][] = new int[6][11][11];
		
		for (int i = 0; i < col - 1; i++) {
			for (int j = 0; j < row - 2; j = j + 2) {
				
				int x = stegoArray[i][j];
				int y = stegoArray[i][j + 1];
				int z = (stegoArray[i][j+2] + stegoArray[i+1][j] + stegoArray[i+1][j+1] ) / 3;
	
				int d1 = x - y;
				int d2 = y - z;
				int d3 = z - x;
				
				pixels[0][d1+253] ++;
				pixels[1][d2+253] ++;
				pixels[2][d3+253] ++;
				
				for(int ii = 0; ii > -11; ii--) {
					for(int jj = 0; jj < 11; jj++) {
						if((d1 >= jj && d2 >= 0 && d3 == ii) || (d1 == jj && d2 >= 0 && d3 < ii))
							tmp[0][-ii][jj]++;
					}
				}
				
				for(int ii = 0; ii < 11; ii++) {
					for(int jj = 0; jj > -11; jj--) {
						if((d1 < jj && d2 <= 0 && d3 == ii) || (d1 == jj && d2 <= 0 && d3 > ii) || (d1 == jj-1 && d2 <= 0 && d3 == ii+1))
							tmp[1][ii][-jj]++;
					}
				}
				
				for(int ii = 0; ii > -11; ii--) {
					for(int jj = 0; jj < 11; jj++) {
						if((d1 == ii && d2 >= jj && d3 >= 0) || (d1 < ii && d2 == jj && d3 >= 0))
							tmp[2][-ii][jj]++;
					}
				}
				
				for(int ii = 0; ii < 11; ii++) {
					for(int jj = 0; jj > -11; jj--) {
						if((d1 == ii && d2 <= jj && d3 <= 0) || (d1 > ii && d2 == jj && d3 <= 0))
							tmp[3][ii][-jj]++;
					}
				}
				
				for(int ii = 0; ii < 11; ii++) {
					for(int jj = 0; jj > -11; jj--) {
						if((d1 <= -1 && d2 == ii && d3 <= jj) || (d1 <= -1 && d2 > ii && d3 == jj))
							tmp[4][ii][-jj]++;
					}
				}
				
				for(int ii = 0; ii > -11; ii--) {
					for(int jj = 0; jj < 11; jj++) {
						if((d1 >= 1 && d2 == ii && d3 >= jj) || (d1 >= 1 && d2 < ii && d3 == jj))
							tmp[5][-ii][jj]++;
					}
				}
			}
		}
		
		for(int k = 0; k < 6; k++) {
			int max = -1;
			System.out.println("k " + k);
			for(int ii = 0; ii < 2; ii++) {
				for(int jj = 0; jj < 2; jj++) {
					System.out.println("ii " + ii + " jj " + jj + " rrr " + tmp[k][ii][jj]);
					if(max < tmp[k][ii][jj]) {
						max = tmp[k][ii][jj];
						push[k][0] = ii;
						push[k][1] = jj;
					}
				}
			}
		}

		System.out.println("1 " + tmp[0][0][0]+tmp[1][0][0]+tmp[2][1][1]+tmp[3][1][1]+tmp[4][0][0]+tmp[5][0][0]);
		
		
		
		
		
		
		
		for (int i = 0; i < col - 1; i++) {
			for (int j = 0; j < row - 2; j = j + 2) {

				int x = stegoArray[i][j];
				int y = stegoArray[i][j + 1];
				int z = (stegoArray[i][j+2] + stegoArray[i+1][j] + stegoArray[i+1][j+1] ) / 3;
	
				int d1 = x - y;
				int d2 = y - z;
				int d3 = z - x;

				if ((d1 >= push[0][1] && d2 >= 0 && d3 == -push[0][0]) || (d1 == push[0][1] && d2 >= 0 && d3 < -push[0][0])) { // x+b
					stegoArray[i][j] += embedbits[embedpointer];
					embedpointer++;
				} else if ((d1 < -push[1][1] && d2 <= 0 && d3 == push[0][0]) || (d1 == -push[1][1] && d2 <= 0 && d3 > push[0][0]) || (d1 == -push[1][1]-1 && d2 <= 0 && d3 == push[1][0]+1)) { // x-b
					stegoArray[i][j] -= embedbits[embedpointer];
					embedpointer++;
				} else if ((d1 == -push[2][0] && d2 >= push[2][1] && d3 >= 0) || (d1 < -push[2][0] && d2 == push[2][1] && d3 >= 0)) { // y+b
					stegoArray[i][j + 1] += embedbits[embedpointer];
					embedpointer++;
				} else if ((d1 == push[3][0] && d2 <= -push[3][1] && d3 <= 0) || (d1 > push[3][0] && d2 == -push[3][1] && d3 <= 0)) { // y-b
					stegoArray[i][j + 1] -= embedbits[embedpointer];
					embedpointer++;
				} else if ((d1 <= -1 && d2 == push[4][0] && d3 <= -push[4][1]) || (d1 <= -1 && d2 > push[4][0] && d3 == -push[4][1])) { // x+b y+b
					stegoArray[i][j] += embedbits[embedpointer];
					stegoArray[i][j + 1] += embedbits[embedpointer];
					embedpointer++;
				} else if ((d1 >= 1 && d2 == -push[5][0] && d3 >= push[5][1]) || (d1 >= 1 && d2 < -push[5][0] && d3 == push[5][1])) { // x-b y-b
					stegoArray[i][j] -= embedbits[embedpointer];
					stegoArray[i][j + 1] -= embedbits[embedpointer];
					embedpointer++;
				} else if (d1 > push[0][1] && d2 >= 0 && d3 < -push[0][0]) { // 不藏入
						stegoArray[i][j] += 1;
						no++;
				} else if ((d1 < -push[1][1]-1 && d2 <= 0 && d3 >= push[1][0]+1) || (d1 == -push[1][1]-1 && d2 <= 0 && d3 > push[1][0]+1)) { // 不藏入
						stegoArray[i][j] -= 1;
						no++;
				} else if (d1 < -push[2][0] && d2 > push[2][1] && d3 >= 0) { // 不藏入
						stegoArray[i][j + 1] += 1;
						no++;
				} else if (d1 > push[3][0] && d2 < -push[3][1] && d3 <= 0) { // 不藏入
						stegoArray[i][j + 1] -= 1;
						no++;
				} else if (d1 <= -1 && d2 > push[4][0] && d3 < -push[4][1]) { // 不藏入
						stegoArray[i][j] += 1;
						stegoArray[i][j + 1] += 1;
						no++;
				} else if (d1 >= 1 && d2 < -push[5][0] && d3 > push[5][1]) { // 不藏入
						stegoArray[i][j] -= 1;
						stegoArray[i][j + 1] -= 1;
						no++;
				} else {
						no++;
						xx++;
				}

				if (embedpointer == num  || (i == col-2 && j == row-4)) {
					System.out.println("embedpointer: " + embedpointer);
					System.out.println("no: " + no);
					System.out.println("xx: " + xx);
					System.out.println(i + " " + j);
					
					
					for(int ii=0;ii<11;ii++) 
						System.out.println("quadrant: " + ii + " " + quadrant[ii]);
					
					embed_result[0] = embedpointer;
					embed_result[1] = i;
					embed_result[2] = j;
					return;
				}
			}
		}
		
		System.out.println("embedpointer: " + embedpointer);
		System.out.println("no: " + no);
		System.out.println("xx: " + xx);
		
		
		for(int i=0;i<507;i++) {
			System.out.println("d1: " + (i-253) + " " + pixels[0][i]);
		}
		for(int i=0;i<507;i++) {
			System.out.println("d2: " + (i-253) + " " + pixels[1][i]);
		}
		for(int i=0;i<507;i++) {
			System.out.println("d3: " + (i-253) + " " + pixels[2][i]);
		}
		
	}


	public int extractImage2(int extract[], int embed_result[], int recoverArray[][], int push[][]) {
		// extract
		int no = 0;
		int xx = 0;
		int extractedpointer = 0;
		int row = recoverArray.length;
		int col = recoverArray[0].length;
		boolean first = true;

		for (int i = embed_result[1]; i >= 0; i--) {
			for (int j = row - 4; j >= 0; j = j - 2) {
				if(first && i == embed_result[1]) {
					j = embed_result[2];
					first = false;
				}
				
				int x = recoverArray[i][j];
				int y = recoverArray[i][j + 1];
				int z = (recoverArray[i][j+2] + recoverArray[i+1][j] + recoverArray[i+1][j+1] ) / 3;
				
				int d1 = x - y;
				int d2 = y - z;
				int d3 = z - x;
				
				if ((d1 >= push[0][1] && d2 >= 0 && d3 == -push[0][0]) || (d1 > push[0][1] && d2 >= 0 && d3 == -push[0][0]-1)) { // x+b
					if(d3 == -push[0][0]) extract[extractedpointer] = 0;
					else extract[extractedpointer] = 1;
					recoverArray[i][j] = recoverArray[i][j] - extract[extractedpointer];
					extractedpointer++;
				} else if ((d1 == push[0][1] && d2 >= 0 && d3 < -push[0][0]) || (d1 == push[0][1]+1 && d2 >= 0 && d3 < -push[0][0]-1)) { // x+b
					if(d1 == push[0][1]) extract[extractedpointer] = 0;
					else extract[extractedpointer] = 1;
					recoverArray[i][j] = recoverArray[i][j] - extract[extractedpointer];
					extractedpointer++;
				} 
				
				else if ((d1 <= -push[1][1]-1 && d2 <= 0 && d3 == push[1][0]) || (d1 < -push[1][1]-1 && d2 <= 0 && d3 == push[1][0]+1)) { // x-b
					if(d3 == push[1][0]) extract[extractedpointer] = 0;
					else extract[extractedpointer] = 1;
					recoverArray[i][j] = recoverArray[i][j] + extract[extractedpointer];
					extractedpointer++;
				} else if ((d1 == -push[1][1] && d2 <= 0 && d3 > push[1][0]) || (d1 == -push[1][1]-1 && d2 <= 0 && d3 > push[1][0]+1)) { // x-b
					if(d1 == -push[1][1]) extract[extractedpointer] = 0;
					else extract[extractedpointer] = 1;
					recoverArray[i][j] = recoverArray[i][j] + extract[extractedpointer];
					extractedpointer++;
				} else if ((d1 == -push[1][1]-1 && d2 <= 0 && d3 == push[1][0]+1) || (d1 == -push[1][1]-2 && d2 <= 0 && d3 == push[1][0]+2)) { // x-b
					if(d3 == push[1][0]+1) extract[extractedpointer] = 0;
					else extract[extractedpointer] = 1;
					recoverArray[i][j] = recoverArray[i][j] + extract[extractedpointer];
					extractedpointer++;
				} 
				
				else if ((d1 == -push[2][0] && d2 >= push[2][1] && d3 >= 0) || (d1 == -push[2][0]-1 && d2 > push[2][1] && d3 >= 0)) { // y+b
					if(d1 == -push[2][0]) extract[extractedpointer] = 0;
					else extract[extractedpointer] = 1;
					recoverArray[i][j + 1] = recoverArray[i][j + 1] - extract[extractedpointer];
					extractedpointer++;
				} else if ((d1 < -push[2][0] && d2 == push[2][1] && d3 >= 0) || (d1 < -push[2][0]-1 && d2 == push[2][1]+1 && d3 >= 0)) { // y+b
					if(d2 == push[2][1]) extract[extractedpointer] = 0;
					else extract[extractedpointer] = 1;
					recoverArray[i][j + 1] = recoverArray[i][j + 1] - extract[extractedpointer];
					extractedpointer++;
				} 
				
				else if ((d1 == push[3][0] && d2 <= -push[3][1] && d3 <= 0) || (d1 == push[3][0]+1 && d2 < -push[3][1] && d3 <= 0)) { // y-b
					if(d1 == push[3][0]) extract[extractedpointer] = 0;
					else extract[extractedpointer] = 1;
					recoverArray[i][j + 1] = recoverArray[i][j + 1] + extract[extractedpointer];
					extractedpointer++;
				} else if ((d1 > push[3][0] && d2 == -push[3][1] && d3 <= 0) || (d1 > push[3][0]+1 && d2 == -push[3][1]-1 && d3 <= 0)) { // y-b
					if(d2 == -push[3][1]) extract[extractedpointer] = 0;
					else extract[extractedpointer] = 1;
					recoverArray[i][j + 1] = recoverArray[i][j + 1] + extract[extractedpointer];
					extractedpointer++;
				}
				
				else if ((d1 <= -1 && d2 == push[4][0] && d3 <= -push[4][1]) || (d1 <= -1 && d2 == push[4][0]+1 && d3 < -push[4][1])) { // x+b,y+b
					if(d2 == push[4][0]) extract[extractedpointer] = 0;
					else extract[extractedpointer] = 1;
					recoverArray[i][j] = recoverArray[i][j] - extract[extractedpointer];
					recoverArray[i][j + 1] = recoverArray[i][j + 1] - extract[extractedpointer];
					extractedpointer++;
				} else if ((d1 <= -1 && d2 > push[4][0] && d3 == -push[4][1]) || (d1 <= -1 && d2 > push[4][0]+1 && d3 == -push[4][1]-1)) { // x+b,y+b
					if(d3 == -push[4][1]) extract[extractedpointer] = 0;
					else extract[extractedpointer] = 1;
					recoverArray[i][j] = recoverArray[i][j] - extract[extractedpointer];
					recoverArray[i][j + 1] = recoverArray[i][j + 1] - extract[extractedpointer];
					extractedpointer++;
				}
				
				else if ((d1 >= 1 && d2 == -push[5][0] && d3 >= push[5][1]) || (d1 >= 1 && d2 == -push[5][0]-1 && d3 > push[5][1])) { // x-b,y-b
					if(d2 == -push[5][0]) extract[extractedpointer] = 0;
					else extract[extractedpointer] = 1;
					recoverArray[i][j] = recoverArray[i][j] + extract[extractedpointer];
					recoverArray[i][j + 1] = recoverArray[i][j + 1] + extract[extractedpointer];
					extractedpointer++;
				} else if ((d1 >= 1 && d2 < -push[5][0] && d3 == push[5][1]) || (d1 >= 1 && d2 < -push[5][0]-1 && d3 == push[5][1]+1)) { // x-b,y-b
					if(d3 == push[5][1]) extract[extractedpointer] = 0;
					else extract[extractedpointer] = 1;
					recoverArray[i][j] = recoverArray[i][j] + extract[extractedpointer];
					recoverArray[i][j + 1] = recoverArray[i][j + 1] + extract[extractedpointer];
					extractedpointer++;
				}
				
				
				else if (d1 > push[0][1]+1 && d2 >= 0 && d3 < -push[0][0]-1) { // 無秘密
					recoverArray[i][j] = recoverArray[i][j] - 1;
					no++;
				} else if ((d1 < -push[1][1]-2 && d2 <= 0 && d3 > push[1][0]+1) || (d1 == -push[1][1]-2 && d2 <= 0 && d3 > push[1][0]+2)) { // 不藏入
					recoverArray[i][j] = recoverArray[i][j] + 1;
					no++;
				} else if (d1 < -push[2][0]-1 && d2 > push[2][1]+1 && d3 >= 0) { // 無秘密
					recoverArray[i][j + 1] = recoverArray[i][j + 1] - 1;
					no++;
				} else if (d1 > push[3][0]+1 && d2 < -push[3][1]-1 && d3 <= 0) { // 無秘密
					recoverArray[i][j + 1] = recoverArray[i][j + 1] + 1;
					no++;
				} else if (d1 <= -1 && d2 > push[4][0]+1 && d3 < -push[4][1]-1) { // 無秘密
					recoverArray[i][j] = recoverArray[i][j] - 1;
					recoverArray[i][j + 1] = recoverArray[i][j + 1] - 1;
					no++;
				} else if (d1 >= 1 && d2 < -push[5][0]-1 && d3 > push[5][1]+1) { // 無秘密
					recoverArray[i][j] = recoverArray[i][j] + 1;
					recoverArray[i][j + 1] = recoverArray[i][j + 1] + 1;
					no++;
				} else {
					no++;
					xx++;
				}
			}	
		}
		
		System.out.println("");
		System.out.println("extractedpointer: " + extractedpointer);
		System.out.println("no: " + no);
		System.out.println("xx: " + xx);
		return extractedpointer;

	}
*/



/*
for (int i = 0; i < col - 1; i++) {
	for (int j = 0; j < row - 2; j = j + 2) {
		
		int x = stegoArray[i][j];
		int y = stegoArray[i][j + 1];
		int z = zz(stegoArray, i, j);

		int d1 = x - y;
		int d2 = y - z;
		int d3 = z - x;
		
		pixels[0][d1+253] ++;
		pixels[1][d2+253] ++;
		pixels[2][d3+253] ++;
		
		for(int ii = 0; ii > -11; ii--) {
			for(int jj = 0; jj < 11; jj++) {
				if((d1 >= jj && d2 >= 0 && d3 == ii) || (d1 == jj && d2 >= 0 && d3 < ii))
					tmp[0][-ii][jj]++;
			}
		}
		
		for(int ii = 0; ii < 11; ii++) {
			for(int jj = 0; jj > -11; jj--) {
				if((d1 < jj && d2 <= 0 && d3 == ii) || (d1 == jj && d2 <= 0 && d3 > ii) || (d1 == jj-1 && d2 <= 0 && d3 == ii+1))
					tmp[1][ii][-jj]++;
			}
		}
		
		for(int ii = 0; ii > -11; ii--) {
			for(int jj = 0; jj < 11; jj++) {
				if((d1 == ii && d2 >= jj && d3 >= 0) || (d1 < ii && d2 == jj && d3 >= 0))
					tmp[2][-ii][jj]++;
			}
		}
		
		for(int ii = 0; ii < 11; ii++) {
			for(int jj = 0; jj > -11; jj--) {
				if((d1 == ii && d2 <= jj && d3 <= 0) || (d1 > ii && d2 == jj && d3 <= 0))
					tmp[3][ii][-jj]++;
			}
		}
		
		for(int ii = 0; ii < 11; ii++) {
			for(int jj = 0; jj > -11; jj--) {
				if((d1 <= -1 && d2 == ii && d3 <= jj) || (d1 <= -1 && d2 > ii && d3 == jj))
					tmp[4][ii][-jj]++;
			}
		}
		
		for(int ii = 0; ii > -11; ii--) {
			for(int jj = 0; jj < 11; jj++) {
				if((d1 >= 1 && d2 == ii && d3 >= jj) || (d1 >= 1 && d2 < ii && d3 == jj))
					tmp[5][-ii][jj]++;
			}
		}
	}
}

for(int k = 0; k < 6; k++) {
	int max = -1;
	//System.out.println("k " + k);
	for(int ii = 0; ii < 2; ii++) {
		for(int jj = 0; jj < 2; jj++) {
			//System.out.println(ii + " " + jj + " " + tmp[k][ii][jj]);
			if(max < tmp[k][ii][jj]) {
				max = tmp[k][ii][jj];
				push[k][0] = ii;
				push[k][1] = jj;
			}
		}
	}
}



*/

