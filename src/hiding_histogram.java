
public class hiding_histogram {
	public static void main(String[] args) {
		System.out.println("Start hiding message by histogram");

		imagingRGB test = null;
		int[][] matrix = null;
		try {
			test = new imagingRGB();
			matrix = test.read("DE-stego_hist.jpg");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

  		System.out.println("Cover data:");
		int max = -1;
		int zero = 999;
		int[] hist = new int[256];
		for (int i = 0; i < 256; i++)
			hist[i] = 0;

		for (int y = 0; y < matrix.length; y++) {
			for (int x = 0; x < matrix[y].length; x++) {
				hist[matrix[y][x]]++;
			}
		}
		
		for (int i = 0; i < 256; i++) {
			if (max == -1 || hist[max] < hist[i])
				max = i;
		}

		for (int i = 0; i < 256; i++) {
			if (hist[i] == 0 && max < i && Math.abs(max - zero) > Math.abs(max - i))
				zero = i;
		}
		System.out.println("max: " + max);
		System.out.println("zero: " + zero);
		System.out.println();
		

		String mm = "https://www.facebook.com/permalink.php?story_fbid=1844961718&id=100049649171009678191844961718&id=100000967819&id=1000009678191844961718&id=1000009678191844961718&id=10000096781961718&id=1000009678191844961718&id=100000967819293k.comk.comk.comkdd/permalink.php?story_fbid=2517471844961718&id=1000009678192931718&id=100000967819293https://www.facebook.com/permalink.php?story_fbid=2517471844961718&id=100000967819293https://www.facebook.com/permalink.php?story_fbid=2517471844961718&id=100000967819293";
		String message = "";
		for (int i = 0; i < 1; i++)
			message += mm;
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
		System.out.println();
		String secret_tmp = secret;

		
		for (int y = 0; y < matrix.length; y++) {
			for (int x = 0; x < matrix[y].length; x++) {
				if (matrix[y][x] > max && matrix[y][x] < zero)
					matrix[y][x] += 1;
			}
		}
		
		for (int y = 0; y < matrix.length; y++) {
			for (int x = 0; x < matrix[y].length; x++) {
				if (secret_tmp.length() > 0 && matrix[y][x] == max) {
					if(secret_tmp.substring(0, 1).equals("1"))
						matrix[y][x] += 1;
					secret_tmp = secret_tmp.substring(1);
				}
			}
		}
		
		
		
		test.matrix = matrix;
		try {
			test.write("DE-stego_hist_1.jpg");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		
		System.out.println("Extract data:");
		/*try {
			test = new imagingRGB();
			matrix = test.read("DE-stego_hist_1.jpg");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}*/
		
		
		String message2 = "";
		String tmp = "";
		String secret2 = "";
		int num = 0;
		for (int y = 0; y < matrix.length; y++) {
			for (int x = 0; x < matrix[y].length; x++) {
				if(num > (lll * 7))
					continue;
				
				if(matrix[y][x] == max) {
					tmp += "0";
					num++;
				}
				else if(matrix[y][x] == (max+1)) {
					tmp += "1";
					matrix[y][x] = matrix[y][x] - 1;
					num++;
				}
				if (tmp.length() == 7) {
					secret2 += tmp;
					message2 += (char) Integer.parseInt(tmp, 2);
					tmp = "";
				}
			}
		}
		System.out.println("Secret message: " + message2);
		System.out.println("Secret message: " + secret2);
		System.out.println("Secret message: " + secret2.length());
		System.out.println();
		
		
		
		test.matrix = matrix;
		try {
			test.write("DE-stego_hist_2.jpg");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		
		int error = 0;
		for (int i = 0; i < message2.length(); i++) {
			if (!message.substring(i, i + 1).equals(message2.substring(i, i + 1)))
				error++;
		}

		System.out.println("Error message: " + error + " " + (float) error / message.length() * 100 + "%");

		error = 0;
		for (int i = 0; i < secret2.length(); i++) {
			if (!secret.substring(i, i + 1).equals(secret2.substring(i, i + 1)))
				error++;
		}

		System.out.println("Error message: " + error + " " + (float) error / secret.length() * 100 + "%");
		System.out.println();
		
		
		try {
			new PSNR(test.read("lena_gray.jpg"), matrix);
		} catch (Exception e) {
			e.getStackTrace();
		}
	}
}
