import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Lee {
    public static void main(String[] args) {
        /* 宣告 */
        Lee pm = new Lee();
        BufferedImage bi;
        String path = "images/Peppers.png";
        String stegofilename = "stegotiffany.bmp";
        String recoverfilename = "recover.bmp";
        try {

            int num = 15000;// 控制藏量
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

            pm.embedImage(embed_result, embedbits, num, stegoArray);// 藏入1 原史論文

            System.out.println();
            pm.saveImage(stegoArray, stegofilename);// 輸出偽裝影像
            pm.PSNR(grayarray, stegoArray);

            // 擷取及還原*/
            for (int i = 0; i < w; i++) {
                for (int j = 0; j < h; j++) {
                    recoverArray[i][j] = stegoArray[i][j];
                }
            }

            extractpointer = pm.extractImage(extract, embed_result, recoverArray);// 提取秘密 原史論文

            // bit藏入與取出的驗證 System.out.println(); System.out.print("grayarray[0]: ");
            /*
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
*/
            int xxx = 0;
            for (int i = 0; i < w; i++) {
                for (int j = 0; j < h; j++) {
                    if (grayarray[i][j] != recoverArray[i][j]) {
                        System.out.println(i + " " + j);
                        xxx++;
                    }
                }
            }
            System.out.println("cover image 與  recover image 像素不同之數量: " + xxx);
/*
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
            */
            xxx = 0;
            if (num > extractpointer)
                num = extractpointer;
            for (int i = 0; i < num; i++) {
                if (embedbits[i] != extract[i]) { 
                    xxx++;
                }
            }
            System.out.println("秘密不同之數量: " + xxx);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
    public void embedImage(int embed_result[], int embedbits[], int num, int stegoArray[][]) {
        int embedpointer = 0;
        int row = stegoArray.length;
        int col = stegoArray[0].length;

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col - 1; j = j + 2) {

                int x = stegoArray[i][j];
                int y = stegoArray[i][j + 1];

                //if(i == 511 && j == 502) System.out.println("embed x:" + stegoArray[i][j] + " y:" + stegoArray[i][j + 1] + " " + embedpointer );

                if ((y - x) == 1) {
                    stegoArray[i][j + 1] += embedbits[embedpointer];
                    embedpointer++;
                } else if ((y - x) == -1) {
                    stegoArray[i][j + 1] -= embedbits[embedpointer];
                    embedpointer++;
                } else if ((y - x) > 1) {
                    stegoArray[i][j + 1] += 1;
                } else if ((y - x) < -1) {
                    stegoArray[i][j + 1] -= 1;
                }

                //if(i == 511 && j == 502) System.out.println("embed x:" + stegoArray[i][j] + " y:" + stegoArray[i][j + 1] + " " + embedpointer);

                if (embedpointer == num || (i == row - 1 && j == col - 2)) {
                    System.out.println("embedpointer:" + embedpointer);

                    embed_result[0] = embedpointer;
                    embed_result[1] = i;
                    embed_result[2] = j;
                    return;
                }
            }
        }
    }

    public int extractImage(int extract[], int embed_result[], int recoverArray[][]) {
        int extractedpointer = 0;
        int row = recoverArray.length;
        int col = recoverArray[0].length;

        System.out.println("embed_result0: " + embed_result[0]);
        System.out.println("embed_result1: " + embed_result[1]);
        System.out.println("embed_result2: " + embed_result[2]);
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col - 1; j = j + 2) {
                if (extractedpointer == embed_result[0]) {
                    if(embed_result[1] != row - 1 || embed_result[2] != col - 2) {
                        System.out.println("i:" + i + " j:" + j);
                        System.out.println("extractedpointer: " + extractedpointer);
                        return extractedpointer;
                    }
                }
                int x = recoverArray[i][j];
                int y = recoverArray[i][j + 1];

                //if(i == 511 && j == 502) System.out.println("extract x:" + recoverArray[i][j] + " y:" + recoverArray[i][j + 1]);

                if ((y - x) == 1 || (y - x) == 2) {
                    extract[extractedpointer] = (y - x) - 1;
                    recoverArray[i][j + 1] -= extract[extractedpointer];
                    extractedpointer++;
                } else if ((y - x) == -1 || (y - x) == -2) {
                    extract[extractedpointer] = -1 * (y - x) - 1;
                    recoverArray[i][j + 1] += extract[extractedpointer];
                    extractedpointer++;
                } else if ((y - x) > 2) {
                    recoverArray[i][j + 1] -= 1;
                } else if ((y - x) < -2) {
                    recoverArray[i][j + 1] += 1;
                }

                //if(i == 511 && j == 502) System.out.println("extract x:" + recoverArray[i][j] + " y:" + recoverArray[i][j + 1]);
            }
        }

        System.out.println("");
        System.out.println("extractedpointer2: " + extractedpointer);
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
        File File = new File(filename);// 輸出檔
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
