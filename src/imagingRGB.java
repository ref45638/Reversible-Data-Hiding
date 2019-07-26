
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author ANGGA
 */
public class imagingRGB {

	private BufferedImage cover;
	public int[][] matrix;

	public imagingRGB() {
	}

	public int[][] read(String coverPath) throws IOException {
		this.cover = ImageIO.read(new File(coverPath));
		int w = this.cover.getWidth();
		int h = this.cover.getHeight();
		int pix2D[][] = new int[w][h];
		this.matrix = new int[w][h];

		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				pix2D[i][j] = this.cover.getRGB(i, j);
			}
		}
		
		int row = pix2D.length;
		int col = pix2D[0].length;
		for (int i = 0; i < col; i++) {
			for (int j = 0; j < row; j++) {
				int red = 0xff & (pix2D[i][j] >> 16);// 抓取R的數值
				int green = 0xff & (pix2D[i][j] >> 8);// 抓取G的數值
				int blue = 0xff & pix2D[i][j];// 抓取B的數值
				int gray = (red + green + blue) / 3;
				this.matrix[i][j] = gray;
			}
		}

		return this.matrix;
	}

	public void write(String stegoPath) throws IOException {
/*
		int row = this.matrix.length;
		int col = this.matrix[0].length;
		int output[] = new int[row * col];
		for (int y = 0; y < this.matrix.length; y++) {
			for (int x = 0; x < this.matrix[y].length; x++) {
				output[x * col + y] = (0xff000000 | this.matrix[y][x] << 16 | this.matrix[y][x] << 8
						| this.matrix[y][x]);
			}
		}
		BufferedImage Output = new BufferedImage(row, col, BufferedImage.TYPE_INT_RGB);
		Output.setRGB(0, 0, row, col, output, 0, row);
		File File = new File(stegoPath);// 輸出檔
		try {
			ImageIO.write(Output, "jpg", File);
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		
		
		int row = this.matrix.length;
		int col = this.matrix[0].length;
		int output[] = new int[row * col];
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				output[j * col + i] = (0xff000000 | this.matrix[i][j] << 16 | this.matrix[i][j] << 8 | this.matrix[i][j]);
			}
		}
		BufferedImage Output = new BufferedImage(row, col, BufferedImage.TYPE_INT_RGB);
		Output.setRGB(0, 0, row, col, output, 0, row);
		File File = new File(stegoPath);// 輸出檔
		try {
			ImageIO.write(Output, "jpg", File);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}