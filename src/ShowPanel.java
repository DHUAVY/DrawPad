package code.DrawPad.src;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class ShowPanel extends JPanel{

    ArrayList<BufferedImage> buffImgList;
    BufferedImage buffImg;
    int x, y;
    public void setMaxImage(BufferedImage buffImg,int x,int y){
        this.buffImg = buffImg;
        this.x = x;
        this.y = y;
    }

    public ShowPanel(){
        setBackground (Color.GRAY);
    }

    public void setBuffImgList(ArrayList<BufferedImage> buffImgList){
        this.buffImgList = buffImgList;
    }

    @Override
    public void paint(Graphics g){
        super.paint (g);

        if(buffImgList == null || buffImgList.size () <= 0){
            return;
        }
        BufferedImage bfimg = buffImgList.get (buffImgList.size () - 1);

        if(bfimg != null){
            int x = (getWidth () - bfimg.getWidth ()) / 2;
            int y = (getHeight () - bfimg.getHeight ()) / 2;
            g.drawImage (bfimg, x, y, null);
        }
        if(buffImg!=null){
            // 放大镜显示
            g.drawImage (buffImg, x-20, y-20,180,180,null);
        }

    }
}
