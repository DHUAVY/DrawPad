package code.DrawPad.src;

import com.github.sarxos.webcam.Webcam;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class WebCamTest{
    public static void main(String[] args){

        JFrame jf = new JFrame ();
        jf.setSize (1000, 900);
        jf.setVisible (true);
        jf.setTitle ("摄像头测试");
        Graphics g = jf.getGraphics ();

        // 获取默认的摄像头
        Webcam webcam = Webcam.getDefault ();
        System.out.println ("默认摄像头："+webcam.getName ());
        // 获取摄像头列表
        List<Webcam> webcams = Webcam.getWebcams ();
        for(Webcam webcam1 : webcams){
            System.out.println ("摄像头："+webcam1.getName ());
            for(int i = 0; i < webcam1.getViewSizes ().length; i++){
                System.out.println (webcam1.getViewSizes ()[i]);
            }
        }
        for(int i = 0; i < webcam.getViewSizes ().length; i++){
            System.out.println (webcam.getViewSizes ()[i]);
        }
        // 设置摄像头分辨率
        webcam.setViewSize (webcam.getViewSizes ()[2]);
        webcam.open ();// 启动

        while(true){
            // 获取图像
            BufferedImage image = webcam.getImage ();
            // 显示图像
            // ImageUtil.showImage (image);
            g.drawImage (image, 300, 200, null);
        }

    }
}
