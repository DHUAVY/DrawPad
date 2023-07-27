package code.DrawPad.src;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class ImageFilter{

    BufferedImage exImg = null;// 当前正在操作的图片 原图
    BufferedImage filteredImg = null; // 当前屏幕上正在显示的图片。
    int[][] exArr = null;

    private static int getAverage(int[] array, int count) {
        int sum = 0;
        for (int i = 0; i < count; i++) {
            sum += array[i];
        }
        return sum / count;
    }

    private static int getGrayValue(int pixel) {
        int red = pixel & 0xff0000 >> 16;
        int green = pixel & 0xff00 >> 8;
        int blue = pixel & 0xff;

        // 使用灰度转换公式将RGB转换为灰度值
        return (int) (0.3 * red + 0.6 * green + 0.1 * blue);
    }

    // 对图片的每个像素点进行反色处理。
    private static int invertColor(int rgb) {
        int red = rgb & 0xff0000 >> 16;
        int green = rgb & 0xff00 >> 8;
        int blue = rgb & 0xff;

        // 对每个颜色通道取反
        red = 255 - red;
        green = 255 - green;
        blue = 255 - blue;

        // 合并颜色通道得到新的RGB值
        return 255<<24 | (red << 16) | (green << 8) | blue;
    }

    private BufferedImage getTemplateImage(){
        return new BufferedImage (exImg.getWidth (), exImg.getHeight (), BufferedImage.TYPE_INT_ARGB);
    }

    // 将文件读取为一张图片
    public int[][] getImagePixels(File file){
        try {
            BufferedImage buffimg = ImageIO.read (file);
            exImg = buffimg;
            exArr = buffToArr();
            filteredImg = exImg;
            return exArr;
        } catch (IOException e) {
            throw new RuntimeException (e);
        }
    }

    public int[][] buffToArr(){
        int width = exImg.getWidth ();
        int height = exImg.getHeight ();
        int[][] imgArr = new int[width][height];
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                imgArr[i][j] = exImg.getRGB (i, j);
            }
        }
        return imgArr;
    }
    // 图像滤镜操作

    // 绘制原图
    public BufferedImage drawOriginalImage(){
        BufferedImage bfimg = getTemplateImage();
        for(int i = 0; i < bfimg.getWidth (); i++){
            for(int j = 0; j < bfimg.getHeight (); j++){
                bfimg.setRGB (i, j, exImg.getRGB (i, j));
            }
        }
        return bfimg;
    }

    // 绘制马赛克
    public BufferedImage drawMosaic(){
        BufferedImage bfimg = getTemplateImage();
        for(int i = 0; i < bfimg.getWidth () - 10; i += 10){
            for(int j = 0; j < bfimg.getHeight () - 10; j += 10){
                for(int k = 0; k < 10; k++){
                    for(int l = 0; l < 10; l++){
                        bfimg.setRGB (i + k, j + l, exImg.getRGB (i, j));
                    }
                }

            }
        }
        return bfimg;
    }

    // 灰度
    public BufferedImage drawGray(){
        BufferedImage bfimg = getTemplateImage();
        for(int i = 0; i < bfimg.getWidth (); i++){
            for(int j = 0; j < bfimg.getHeight (); j++){
                int pix = exImg.getRGB (i, j);
                int gray = getGrayValue(pix);
                int newPix = 255<<24 | gray << 16 | gray << 8 | gray;
                bfimg.setRGB (i, j, newPix);
            }
        }
        return bfimg;
    }

    // 二值化
    public BufferedImage drawBinary(){
        BufferedImage bfimg = getTemplateImage();
        for(int i = 0; i < bfimg.getWidth ()-2; i++){
            for(int j = 0; j < bfimg.getHeight ()-2; j++){
                int pix = exImg.getRGB (i, j);
                int gray = getGrayValue(pix);

                int pix1 = exImg.getRGB (i + 2, j + 2);
                int gray1 = getGrayValue(pix1);

                if(Math.abs (gray - gray1) < 15){
                    gray = 0;
                } else{
                    gray = 255;
                }

                int newPix = 255<<24 | gray << 16 | gray << 8 | gray;
                bfimg.setRGB (i, j, newPix);
            }
        }
        return bfimg;
    }

    // 绘制浮雕
    public BufferedImage drawRelief(){
        int[][] kernel = {
            {1, 0, 0},
            {0, 0, 0},
            {0, 0, -1}
        };

        BufferedImage bfimg = getTemplateImage();

        for(int i = 0; i < bfimg.getWidth ()-3; i++){
            for(int j = 0; j < bfimg.getHeight ()-3; j++){

                int rednew = 0;
                int greennew = 0;
                int bluenew = 0;

                for(int k = 0; k < 3; k++){
                    for(int l = 0; l < 3; l++){
                        int pix = exImg.getRGB (i+k, j+l);
                        int red = pix & 0xff0000 >> 16;
                        int green = pix & 0xff00 >> 8;
                        int blue = pix & 0xff;
                        rednew += red * kernel[k][l];
                        greennew += green * kernel[k][l];
                        bluenew += blue * kernel[k][l];
                    }
                }
                rednew = Math.min(Math.max(rednew, 0), 255);
                greennew = Math.min(Math.max(greennew, 0), 255);
                bluenew = Math.min(Math.max(bluenew, 0), 255);

                int newPix = 255<<24 | rednew << 16 | greennew << 8 | bluenew;
                bfimg.setRGB (i, j, newPix);
            }
        }
        return bfimg;
    }

    // 反色
    public BufferedImage drawRevert(){

        BufferedImage bfimg = getTemplateImage();

        for (int y = 0; y < exImg.getHeight(); y++) {
            for (int x = 0; x < exImg.getWidth(); x++) {
                int rgb = exImg.getRGB(x, y);
                int invertedRgb = invertColor(rgb);
                bfimg.setRGB(x, y, invertedRgb);
            }
        }
        return bfimg;
    }

    // 轮廓
    public BufferedImage drawEdge(){

        BufferedImage bfimg = getTemplateImage();
        int width = bfimg.getWidth();
        int height = bfimg.getHeight();

        // 定义Sobel算子
        int[][] sobelX = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
        int[][] sobelY = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};

        // 对每个像素进行边缘检测
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int gx = 0;
                int gy = 0;

                // 计算X方向梯度
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        int pixel = exImg.getRGB(x + j, y + i);
                        int gray = getGrayValue(pixel);
                        gx += sobelX[i + 1][j + 1] * gray;
                    }
                }

                // 计算Y方向梯度
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        int pixel = exImg.getRGB(x + j, y + i);
                        int gray = getGrayValue(pixel);
                        gy += sobelY[i + 1][j + 1] * gray;
                    }
                }

                // 计算梯度幅值
                int gradient = (int) Math.sqrt(gx * gx + gy * gy);
                bfimg.setRGB(x, y, 255<<24 | gradient << 16 | gradient << 8 | gradient);
            }
        }

        return bfimg;
    }

    // 油画
    public BufferedImage drawOilPaint(){
        BufferedImage bfimg = getTemplateImage();
        int width = bfimg.getWidth();
        int height = bfimg.getHeight();
        // 定义领域滤波的窗口大小
        int brushSize = 5;

        // 对每个像素进行油画效果处理
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // 获取领域内像素的颜色值
                int[] reds = new int[brushSize * brushSize];
                int[] greens = new int[brushSize * brushSize];
                int[] blues = new int[brushSize * brushSize];
                int count = 0;

                for (int i = -brushSize / 2; i <= brushSize / 2; i++) {
                    for (int j = -brushSize / 2; j <= brushSize / 2; j++) {
                        int offsetX = x + j;
                        int offsetY = y + i;

                        // 边界处理，确保不越界
                        if (offsetX >= 0 && offsetX < width && offsetY >= 0 && offsetY < height) {
                            int pixel = exImg.getRGB(offsetX, offsetY);
                            reds[count] = (pixel >> 16) & 0xFF;
                            greens[count] = (pixel >> 8) & 0xFF;
                            blues[count] = pixel & 0xFF;
                            count++;
                        }
                    }
                }

                // 获取领域内像素的平均颜色值
                int avgRed = getAverage(reds, count);
                int avgGreen = getAverage(greens, count);
                int avgBlue = getAverage(blues, count);

                // 设置油画效果后的颜色值
                int oilPaintColor = 255<<24 | (avgRed << 16) | (avgGreen << 8) | avgBlue;
                bfimg.setRGB(x, y, oilPaintColor);
            }
        }

        return bfimg;
    }

    // 毛玻璃
    public BufferedImage drawGroundGlass(){
        BufferedImage bfimg = getTemplateImage();
        int width = bfimg.getWidth();
        int height = bfimg.getHeight();

        // 定义毛玻璃效果的窗口大小
        int windowSize = 5;

        // 对每个像素进行毛玻璃效果处理
        Random random = new Random();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // 随机采样窗口内的像素
                int offsetX = x + random.nextInt(windowSize) - windowSize / 2;
                int offsetY = y + random.nextInt(windowSize) - windowSize / 2;

                // 边界处理，确保不越界
                offsetX = Math.max(0, Math.min(offsetX, width - 1));
                offsetY = Math.max(0, Math.min(offsetY, height - 1));

                // 获取采样到的像素值
                int pixel = exImg.getRGB(offsetX, offsetY);
                bfimg.setRGB(x, y, pixel);
            }
        }
        return bfimg;
    }

    // 锐化
    public BufferedImage drawSharpening(){
        BufferedImage bfimg = getTemplateImage();
        int width = bfimg.getWidth();
        int height = bfimg.getHeight();

        // 定义锐化的卷积核
        int[][] kernel = {
                {-1, -1, -1},
                {-1,  9, -1},
                {-1, -1, -1}
        };

        // 对每个像素进行锐化处理
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int sumRed = 0;
                int sumGreen = 0;
                int sumBlue = 0;

                // 应用卷积核
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        int offsetX = x + j;
                        int offsetY = y + i;

                        // 获取周围像素的颜色值
                        int pixel = exImg.getRGB(offsetX, offsetY);
                        int red = (pixel >> 16) & 0xFF;
                        int green = (pixel >> 8) & 0xFF;
                        int blue = pixel & 0xFF;

                        // 应用卷积核
                        sumRed += kernel[i + 1][j + 1] * red;
                        sumGreen += kernel[i + 1][j + 1] * green;
                        sumBlue += kernel[i + 1][j + 1] * blue;
                    }
                }

                // 确保颜色值在0-255之间
                int finalRed = Math.min(Math.max(sumRed, 0), 255);
                int finalGreen = Math.min(Math.max(sumGreen, 0), 255);
                int finalBlue = Math.min(Math.max(sumBlue, 0), 255);

                // 设置锐化效果后的颜色值
                int sharpenedColor = 255<<24 |(finalRed << 16) | (finalGreen << 8) | finalBlue;
                bfimg.setRGB(x, y, sharpenedColor);
            }
        }

        return bfimg;
    }
}
