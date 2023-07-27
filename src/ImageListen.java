package code.DrawPad.src;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import static code.DrawPad.src.ControlPanel.photoButtonList;


public class ImageListen extends MouseAdapter implements ActionListener, ChangeListener{
    ImageFilter imgF = new ImageFilter ();
    ArrayList<BufferedImage> buffImgList = new ArrayList<> ();
    ShowPanel showPanel;

    VideoPlay videoPlay = null;
    public ImageListen(){

    }

    public void setShowPanel(ShowPanel showPanel){
        this.showPanel = showPanel;
        videoPlay = new VideoPlay (showPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        System.out.println (e.getActionCommand ());
        String ac = e.getActionCommand ();

        if(ac.equals ("FileOP")){
            JMenuItem item = (JMenuItem) e.getSource ();
            fileOP (item);
        } else if(ac.equals ("FilterOP")){
            JButton btn = (JButton) e.getSource ();
            filterOP (btn, photoButtonList);
        } else if(ac.equals ("VideoOP")){
            JButton btn = (JButton) e.getSource ();
            videoOP (btn);
        } else if(ac.equals ("VideoFilterOP")){
            JButton btn = (JButton) e.getSource ();
            videoFilterOP (btn);

        }

    }

    private void videoFilterOP(JButton btn){
        if(videoPlay != null && videoPlay.isStart){
            videoPlay.videoFilter = btn.getText ();
        } else{
            JOptionPane.showMessageDialog (null, "请先启动视频");
        }
    }


    private void videoOP(JButton btn){
        String op = btn.getText ();
        switch (op) {
            case "拍照":
                BufferedImage eximg = videoPlay.getPhoto ();
                videoPlay.isStart = false;
                try {
                    Thread.sleep (500);
                } catch (InterruptedException e) {
                    throw new RuntimeException (e);
                }
                if(eximg != null){
                    buffImgList.add (eximg);
                    showPanel.repaint ();
                }
                break;
            case "录像":
                videoPlay.videoOP = btn.getText ();
                btn.setText ("停止录像");
                break;
            case "停止":
                videoPlay.isStart = false;
                break;
            case "启动":
                videoPlay = new VideoPlay (showPanel);
                videoPlay.start ();
                break;
            case "停止录像":
                videoPlay.videoOP = btn.getText ();
                btn.setText ("录像");
                break;
        }
    }

    @Override
    public void stateChanged(javax.swing.event.ChangeEvent e){

        JSlider slider = (JSlider) e.getSource ();
        int value = slider.getValue ();
        System.out.println ("scale:" + value);
        buffImgList.add (ImageInterpolation.scaleImage(imgF.drawOriginalImage(), value));
        showPanel.repaint ();// 刷新
    }

    @Override
    public void mouseDragged(MouseEvent e){
        // 鼠标在面板上的坐标
        int x = e.getX ();
        int y = e.getY ();

        // 放大镜
        BufferedImage bfImg = buffImgList.get (buffImgList.size () - 1);
        // 去除边界尺寸
        int iw = (showPanel.getWidth () - bfImg.getWidth ()) / 2;
        int jh = (showPanel.getHeight () - bfImg.getHeight ()) / 2;
        if(x - (iw + 20) < 0 || y - (jh + 20) < 0){
            return;
        }
        if(x + (iw + 20) > showPanel.getWidth () || y + (jh + 20) > showPanel.getHeight ()){
            return;
        }
        int bx = x - (iw + 20);
        int by = y - (jh + 20);
        // 获取bfimg中的一部分像素
        BufferedImage miniImg = new BufferedImage (40, 40, BufferedImage.TYPE_INT_ARGB);
        for(int k = bx - 20; k < bx + 20; k++){
            for(int l = by - 20; l < by + 20; l++){
                int rgb = bfImg.getRGB (k, l);
                miniImg.setRGB (k - (bx - 20), l - (by - 20), rgb);
            }
        }
        showPanel.repaint ();// 刷新
        try {
            Thread.sleep (30);
        } catch (InterruptedException ex) {
            throw new RuntimeException (ex);
        }
        showPanel.setMaxImage (miniImg, x, y);
    }

    private void filterOP(JButton btn, ArrayList<JButton> btns){
        for (JButton jButton : btns) {
            jButton.setBackground(Color.WHITE);
            jButton.setForeground(Color.BLACK);
        }
        btn.setBackground(Color.BLUE);
        btn.setForeground(Color.WHITE);
        String op = btn.getText ();
        switch(op) {
            case "原图":
                buffImgList.add (imgF.drawOriginalImage ());
                break;
            case "马赛克":
                buffImgList.add (imgF.drawMosaic ());
                break;
            case "灰度":
                buffImgList.add (imgF.drawGray ());
                break;
            case "二值化":
                buffImgList.add (imgF.drawBinary ());
                break;
            case "浮雕":
                buffImgList.add (imgF.drawRelief ());
            default:
                break;
        }
        showPanel.repaint ();// 刷新

    }

    private void fileOP(JMenuItem btn){
        btn.setBackground (Color.RED);
        String op = btn.getText ();
        if(op.equals ("打开")){
            JFileChooser chooser = new JFileChooser ();
            FileNameExtensionFilter filter = new FileNameExtensionFilter (
                    "JPG & JPEG & PNG  Images", "jpg", "png", "jpeg");
            chooser.setFileFilter (filter);
            int returnVal = chooser.showOpenDialog (null);// 居中显示 一个打开文件的对话框
            if(returnVal == JFileChooser.APPROVE_OPTION){
                System.out.println ("You chose to open this file: " +
                        chooser.getSelectedFile ().getName ());
            }
            imgF.getImagePixels (chooser.getSelectedFile ());
            // 显示
            buffImgList.add (imgF.exImg);
            showPanel.repaint ();
        }
        else if(op.equals ("保存")){
            JFileChooser chooser = new JFileChooser ();
            FileNameExtensionFilter filter = new FileNameExtensionFilter (
                    "JPG & JPEG & PNG  Images", "jpg", "png", "jpeg");
            chooser.setFileFilter (filter);
            int returnVal = chooser.showSaveDialog (null);// 居中显示 一个打开文件的对话框
            if(returnVal == JFileChooser.APPROVE_OPTION){
                System.out.println ("You chose to open this file: " +
                        chooser.getSelectedFile ().getName ());
                BufferedImage buffimg = buffImgList.get (buffImgList.size () - 1);
                try {
                    ImageIO.write (buffimg, "png", chooser.getSelectedFile ());
                } catch (IOException e) {
                    throw new RuntimeException (e);
                }
            }
        }

    }
}