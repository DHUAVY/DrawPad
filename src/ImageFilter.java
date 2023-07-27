package code.DrawPad.src;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageFilter{

    BufferedImage exImg = null;// 当前正在操作的图片 原图
    int[][] exArr = null;

    // 将文件读取为一张图片
    public int[][] getImagePixels(File file){
        try {
            BufferedImage buffimg = ImageIO.read (file);
            int width = buffimg.getWidth ();
            int height = buffimg.getHeight ();
            int[][] imgArr = new int[width][height];
            for(int i = 0; i < width; i++){
                for(int j = 0; j < height; j++){
                    imgArr[i][j] = buffimg.getRGB (i, j);
                }
            }
            exImg = buffimg;
            exArr = imgArr;
            return imgArr;
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
        BufferedImage bfimg = new BufferedImage (exImg.getWidth (), exImg.getHeight (), BufferedImage.TYPE_INT_ARGB);
        for(int i = 0; i < bfimg.getWidth (); i++){
            for(int j = 0; j < bfimg.getHeight (); j++){
                bfimg.setRGB (i, j, exImg.getRGB (i, j));
            }
        }
        return bfimg;
    }

    // 绘制马赛克
    public BufferedImage drawMosaic(){
        BufferedImage bfimg = new BufferedImage (exImg.getWidth (), exImg.getHeight (), BufferedImage.TYPE_INT_ARGB);
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

    public BufferedImage drawGray(){
        BufferedImage bfimg = new BufferedImage (exImg.getWidth (), exImg.getHeight (), BufferedImage.TYPE_INT_ARGB);
        for(int i = 0; i < bfimg.getWidth (); i++){
            for(int j = 0; j < bfimg.getHeight (); j++){
                int pix = exImg.getRGB (i, j);
                int red = pix & 0xff0000 >> 16;
                int green = pix & 0xff00 >> 8;
                int blue = pix & 0xff;
                int gray = (red + green + blue) / 3;
                int newPix = 255<<24 | gray << 16 | gray << 8 | gray;
                bfimg.setRGB (i, j, newPix);
            }
        }
        return bfimg;
    }

    public BufferedImage drawBinary(){
        BufferedImage bfimg = new BufferedImage (exImg.getWidth (), exImg.getHeight (), BufferedImage.TYPE_INT_ARGB);
        for(int i = 0; i < bfimg.getWidth ()-2; i++){
            for(int j = 0; j < bfimg.getHeight ()-2; j++){
                int pix = exImg.getRGB (i, j);
                int red = pix & 0xff0000 >> 16;
                int green = pix & 0xff00 >> 8;
                int blue = pix & 0xff;
                int gray = (red + green + blue) / 3;

                int pix1 = exImg.getRGB (i + 2, j + 2);
                int red1 = pix1 & 0xff0000 >> 16;
                int green1 = pix1 & 0xff00 >> 8;
                int blue1 = pix1 & 0xff;
                int gray1 = (red1 + green1 + blue1) / 3;

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

    public BufferedImage drawRelief(){
        int[][] kernel = {
            {1, 0, 0},
            {0, 0, 0},
            {0, 0, -1}
        };

        BufferedImage bfimg = new BufferedImage (exImg.getWidth (), exImg.getHeight (), BufferedImage.TYPE_INT_ARGB);

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
                if(rednew > 255)    rednew = 255;
                if(rednew < 0)    rednew = 0;
                if(greennew > 255)    greennew = 255;
                if(greennew < 0)    greennew = 0;
                if(bluenew > 255)    bluenew = 255;
                if(bluenew < 0)    bluenew = 0;

                int newPix = 255<<24 | rednew << 16 | greennew << 8 | bluenew;
                bfimg.setRGB (i, j, newPix);
            }
        }
        return bfimg;
    }
}
