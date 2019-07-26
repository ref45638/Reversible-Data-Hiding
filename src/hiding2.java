public class hiding2 {
	public static void main(String[] args) {
		System.out.println("Start hiding message in 3 pixels");
/*宣告*/
		imagingRGB test = null;
		int[][] matrix = null;//矩陣
/*輸入原始影像*/
		try {
			test = new imagingRGB();
			matrix = test.read("lena_gray.jpg");//存入原始影像
			System.out.println("Cover data:");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

		String mm = "https://www.facebook.com/permalink.php?story_fbid=1844961718&id=100049649171009678191844961718&id=100000967819&id=1000009678191844961718&id=1000009678191844961718&id=10000096781961718&id=1000009678191844961718&id=100000967819293k.comk.comk.comkdd/permalink.php?story_fbid=2517471844961718&id=1000009678192931718&id=100000967819293https://www.facebook.com/permalink.php?story_fbid=2517471844961718&id=100000967819293https://www.facebook.com/permalink.php?story_fbid=2517471844961718&id=100000967819293https://www.facebook.com/permalink.php?story_fbid=1844961718&id=100049649171009678191844961718&id=100000967819&id=1000009678191844961718&id=1000009678191844961718&id=10000096781961718&id=1000009678191844961718&id=100000967819293k.comk.comk.comkdd/permalink.php?story_fbid=2517471844961718&id=1000009678192931718&id=100000967819293https://www.facebook.com/permalink.php?story_fbid=2517471844961718&id=100000967819293https://www.facebook.com/permalink.php?story_fbid=2517471844961718&id=100000967819293";
		String message = "";
		for (int i = 0; i < 6; i++)
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
			for (int x = 0; x < matrix[y].length; x += 3) {
				if (secret_tmp.length() > 0) {
					if (x + 2 >= matrix[y].length)
						continue;
					int A = matrix[y][x];
					int B = matrix[y][x + 1];
					int C = matrix[y][x + 2];
					int[] array = new int[3];
					array[0] = A;
					array[1] = B;
					array[2] = C;
					/*
					 * System.out.print("before: "); for (int i = 0; i < 3; ++i)
					 * System.out.print(array[i] + " "); System.out.println();
					 */
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
					/*
					 * System.out.print("after:"); for (int i = 0; i < 3; ++i)
					 * System.out.print(array[i] + " "); System.out.println();
					 */
					/*
					 * int m = (A + B + C) / 3; int a = (A - B) / 2; int aa = 2 * a +
					 * Integer.parseInt(secret_tmp.substring(0, 1)); int b = (A - C) / 2;
					 */
					int m = (A + B + C) / 3;
					int a = (array[2] - array[1]) / 4;
					int aa = 2 * a + Integer.parseInt(secret_tmp.substring(0, 1));
					int b = (array[2] - array[0]) / 4;
					secret_tmp = secret_tmp.substring(1);
					int xaksen = (int) (m + aa + b);
					int yaksen = (int) (m + aa - b);
					int zaksen = (int) (m - aa + b);
					matrix[y][x] = xaksen;
					matrix[y][x + 1] = yaksen;
					matrix[y][x + 2] = zaksen;
				}

			}
		}

		test.matrix = matrix;
		try {
			test.write("DE-stego3_1.jpg");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

		System.out.println("Extract data:");
		/*
		try {
			test = new imagingRGB();
			matrix = test.read("DE-stego3_1.jpg");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}*/

		String message2 = "";
		String tmp = "";
		String secret2 = "";
		int num = 0;
		for (int y = 0; y < matrix.length; y++) {
			for (int x = 0; x < matrix[y].length; x += 3) {
				// System.out.println(Math.abs((matrix[y][x]-matrix[y][x+1]) % 2));
				if (num > (lll * 7))
					continue;
				else if (x + 2 >= matrix[y].length)
					continue;
				int A = matrix[y][x];
				int B = matrix[y][x + 1];
				int C = matrix[y][x + 2];
				int m = ((-2) * B - 2 * C) / (-4);
				int a = ((-2) * A + 2 * C) / (-4);
				int b = ((-2) * A + 2 * B) / (-4);
				tmp += Math.abs(a % 2);
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
		System.out.println();

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
/*
 * var a=1; var b=1; var c=1;
 * 
 * var d=1; var e=1; var f=-1;
 * 
 * var g=1; var h=-1; var i=1;
 * 
 * var p=parseFloat(document.getElementById("p").value); var
 * q=parseFloat(document.getElementById("q").value); var
 * r=parseFloat(document.getElementById("r").value);
 * 
 * var dtr=a*e*i+b*f*g+c*d*h-(c*e*g+a*f*h+d*b*i) = 1-1-1-(1+1+1) = -4; var
 * aa=e*i-f*h=1-1=0; var bb=c*h-b*i=-1-1=-2; var cc=b*f-c*e=-1-1=-2;
 * 
 * var dd=f*g-d*i=-1-1=-2; var ee=a*i-c*g=1-1=0; var ff=c*d-a*f=1+1=2;
 * 
 * var gg=d*h-e*g=-1-1=-2; var hh=b*g-a*h=1+1=2; var ii=a*e-b*d=1-1=0;
 * if(dtr!=0){ x=(p*aa+q*bb+r*cc)/dtr; = -2q-2r y=(p*dd+q*ee+r*ff)/dtr; = -2p+2r
 * z=(p*gg+q*hh+r*ii)/dtr; = -2p+2q
 * 
 * }
 */