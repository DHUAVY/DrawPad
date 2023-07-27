package code.DrawPad.src;

import java.awt.image.BufferedImage;

public class ImageInterpolation {
    public static BufferedImage scaleImage(BufferedImage srcImage, int scale) {

        int width = srcImage.getWidth();
        int height = srcImage.getHeight();
        int newWidth = width * scale / 100;
        int newHeight = height * scale / 100;

        BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

        double scaleX = (double) width / newWidth;
        double scaleY = (double) height / newHeight;

        for (int y = 0; y < newHeight; y++) {
            for (int x = 0; x < newWidth; x++) {
                int srcX = (int) (x * scaleX);
                int srcY = (int) (y * scaleY);
                int x1 = Math.min(srcX + 1, width - 1);
                int y1 = Math.min(srcY + 1, height - 1);

                int dx = (int) (255 * (x * scaleX - srcX));
                int dy = (int) (255 * (y * scaleY - srcY));

                int c00 = srcImage.getRGB(srcX, srcY);
                int c01 = srcImage.getRGB(x1, srcY);
                int c10 = srcImage.getRGB(srcX, y1);
                int c11 = srcImage.getRGB(x1, y1);

                int red = bilinearInterpolation((c00 >> 16) & 0xFF, (c01 >> 16) & 0xFF, (c10 >> 16) & 0xFF, (c11 >> 16) & 0xFF, dx, dy);
                int green = bilinearInterpolation((c00 >> 8) & 0xFF, (c01 >> 8) & 0xFF, (c10 >> 8) & 0xFF, (c11 >> 8) & 0xFF, dx, dy);
                int blue = bilinearInterpolation(c00 & 0xFF, c01 & 0xFF, c10 & 0xFF, c11 & 0xFF, dx, dy);

                int rgb = (red << 16) | (green << 8) | blue;
                newImage.setRGB(x, y, rgb);
            }
        }

        return newImage;
    }

    private static int bilinearInterpolation(int c00, int c01, int c10, int c11, int dx, int dy) {
        int result = (c00 * (255 - dx) * (255 - dy) + c01 * dx * (255 - dy) + c10 * (255 - dx) * dy + c11 * dx * dy) / (255 * 255);
        return Math.min(255, Math.max(0, result));
    }
}
