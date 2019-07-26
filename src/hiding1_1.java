import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.io.File;

import javax.imageio.ImageIO;

public class hiding1_1 {
	public static void main(String[] args) {
		// TODO code application logic here
		int[][] matrix = null;
		try {
			BufferedImage im = ImageIO.read(new File("lena.gif"));
			Raster r = im.getRaster();
			//r.getSample
			SampleModel a = r.getSampleModel();
			matrix = new int[im.getHeight()][im.getWidth()];
			for (int j = 0; j < im.getHeight(); j++) {
				for (int i = 0; i < im.getWidth(); i++) {
					matrix[j][i] = r.getSample(j, i, 0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		System.out.println("Start hiding message in 2 pixels");
		String message = "https://www.facebook.com/permalink.php?story_fbid=2517471844961718&id=100000967819293https://www.facebook.com/permalink.php?story_fbid=251747184496https://www.facebook.com/permalink.php?story_fbid=2517471844961718&id=100000967819293https://www.facebook.com/permalink.php?story_fbid=2517471844961718&id=100000967819293https://www.facebook.com/permalink.php?story_fbid=2517471844961718&id=1000009678192931718&id=100000967819293https://www.facebook.com/permalink.php?story_fbid=2517471844961718&id=100000967819293https://www.facebook.com/permalink.php?story_fbid=2517471844961718&id=100000967819293";
		message += message + message + message + message + message + message + message + message + message + message
				+ message + message + message;
		System.out.println("Secret message: " + message);
		int lll = message.length();
		System.out.println("Secret message length: " + message.length());
		char[] temp = message.toCharArray();
		String secret = "";
		for (char tmp : temp) {
			secret += String.format("%7s", Integer.toBinaryString((int) tmp)).replace(' ', '0');
		}
		System.out.println("Secret message: " + secret);
		System.out.println("Secret message: " + secret.length());

		int l, h, haksen, xaksen, yaksen; // variabelnya
		String secret_tmp = secret;

		for (int y = 0; y < matrix.length; y++) {
			for (int x = 0; x < matrix[y].length; x += 2) {
				if (secret_tmp.length() > 0) {
					int A = matrix[y][x];
					int B = matrix[y][x + 1];
					int[] array = new int[2];
					array[0] = A;
					array[1] = B;
					for (int i = array.length - 1; i > 0; --i) {
						for (int j = 0; j < i; ++j) {
							final int jj = j + 1;
							if (array[j] > array[jj]) {
								final int buffer = array[j];
								array[j] = array[jj];
								array[jj] = buffer;
							}
						}
					}
					l = ((A + B) / 2);
					// h = array[1] - array[0];
					h = A - B;
					haksen = (2 * h) + Integer.parseInt(secret_tmp.substring(0, 1));
					secret_tmp = secret_tmp.substring(1);
					xaksen = (int) (l + (Math.floor((double) (haksen + 1) / 2)));
					yaksen = (int) (l - (Math.floor((double) haksen / 2)));
					matrix[y][x] = xaksen;
					matrix[y][x + 1] = yaksen;
				}

			}
		}
		
		
		try {
			BufferedImage stego =  new BufferedImage(matrix[0].length, matrix.length, BufferedImage.TYPE_4BYTE_ABGR);
			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

		System.out.println("Extract data:");
		String message2 = "";
		String tmp = "";
		String secret2 = "";
		int num = 0;
		for (int y = 0; y < matrix.length; y++) {
			for (int x = 0; x < matrix[y].length; x += 2) {
				// System.out.println(Math.abs((matrix[y][x]-matrix[y][x+1]) % 2));
				if (num > (lll * 7))
					continue;
				tmp += Math.abs((matrix[y][x] - matrix[y][x + 1]) % 2);
				if (tmp.length() == 7) {
					secret2 += tmp;
					message2 += (char) Integer.parseInt(tmp, 2);
					tmp = "";
				}
				num++;
			}
		}

		System.out.println("Secret message: " + message2);
		System.out.println("Secret message: " + secret2);
		System.out.println("Secret message: " + secret2.length());

		int error = 0;
		for (int i = 0; i < message.length(); i++) {
			if (!message.substring(i, i + 1).equals(message2.substring(i, i + 1)))
				error++;
		}

		System.out.println("Error message: " + error + " " + (float) error / message.length() * 100 + "%");

		error = 0;
		for (int i = 0; i < secret.length(); i++) {
			if (!secret.substring(i, i + 1).equals(secret2.substring(i, i + 1)))
				error++;
		}

		System.out.println("Error message: " + error + " " + (float) error / secret.length() * 100 + "%");
/*
		try {
			new PSNR("lena.gif", "DE-stego2.gif");
		} catch (Exception e) {
			e.getStackTrace();
		}*/
	}

}