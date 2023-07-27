package code.DrawPad.src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ControlPanel extends JPanel{

    String[] photoButtons = {"原图", "马赛克", "灰度", "二值化", "反色", "轮廓", "油画", "浮雕", "毛玻璃", "锐化"};
    String[] camButtons = {"拍照","录像","停止","启动","退出"};

    public static ArrayList<JButton> photoButtonList = new ArrayList<> ();

    public ControlPanel(ImageListen il){
        // 两块功能面板
        setPreferredSize (new Dimension (0, 100));
        JPanel jp = new JPanel ();
        jp.setBackground (Color.GREEN);
        JButton camBtn = new JButton ("相机模式");
        JButton photoBtn = new JButton ("图片模式");
        jp.add (camBtn);
        jp.add (photoBtn);

        JPanel jp1 = new JPanel ();
        jp1.setBackground (Color.RED);
        setBackground (Color.DARK_GRAY);
        for (String photoButton : photoButtons) {
            JButton btn = new JButton(photoButton);
            photoButtonList.add(btn);
            jp1.add(btn);
            btn.setBackground(Color.WHITE);
            btn.addActionListener(il);
            btn.setActionCommand("FilterOP");
        }

        JPanel jp2 = new JPanel ();
        jp2.setBackground (Color.BLUE);
        for (String camButton : camButtons) {
            JButton btn = new JButton(camButton);
            jp2.add(btn);
            btn.setBackground(Color.WHITE);
            btn.addActionListener(il);
			btn.setActionCommand ("VideoOP");
        }
        for (String photoButton : photoButtons) {
            JButton btn = new JButton(photoButton);
            photoButtonList.add(btn);
            jp2.add(btn);
            btn.setBackground(Color.WHITE);
            btn.addActionListener(il);
            btn.setActionCommand("VideoFilterOP");
        }

        JSlider jSlider = new JSlider (1, 150, 100);
        jSlider.setPaintLabels (true);
        jSlider.setPaintTicks (true);
        jSlider.addChangeListener (il);
        jp1.add (jSlider);

        this.setLayout (new BorderLayout ());
        jp.setPreferredSize (new Dimension (100, 0));
        add (jp, BorderLayout.WEST);
        add (jp1,BorderLayout.CENTER);

        camBtn.addActionListener (new ActionListener (){
            @Override
            public void actionPerformed(ActionEvent e){
                remove (jp1);
                add (jp2);
                revalidate ();
                repaint ();
            }
        });

        photoBtn.addActionListener (new ActionListener (){
            @Override
            public void actionPerformed(ActionEvent e){
                remove (jp2);
                add (jp1);
                revalidate ();
                repaint ();
            }
        });

    }

}