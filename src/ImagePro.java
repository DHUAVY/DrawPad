package code.DrawPad.src;
import javax.swing.*;
import java.awt.*;

public class ImagePro extends JFrame{
    ImageListen imageListen = new ImageListen ();

    public ImagePro(){
        JFrame jf = new JFrame ();
        jf.setTitle ("图片处理");
        jf.setSize (1000, 900);
        jf.setDefaultCloseOperation (EXIT_ON_CLOSE);
        jf.setVisible (true);

        ShowPanel showPanel = new ShowPanel ();// 显示图片的面板

        // 初始化图层
        showPanel.setBuffImgList (imageListen.buffImgList);
        imageListen.setShowPanel (showPanel);
        showPanel.addMouseListener (imageListen);//鼠标监听
        showPanel.addMouseMotionListener (imageListen);// 鼠标移动监听

        JPanel imgPanel = new JPanel ();// 图片选择的面板
        ControlPanel controlPanel = new ControlPanel (imageListen);// 滤镜操作面板


        jf.add (showPanel, BorderLayout.CENTER);
        jf.add (controlPanel, BorderLayout.SOUTH);

        JMenuBar jmb = new JMenuBar ();// 菜单栏
        JMenu jm = new JMenu ("文件");// 菜单
        JMenuItem jmiOpen = new JMenuItem ("打开");// 菜单项
        JMenuItem jmiSave = new JMenuItem ("保存");// 菜单项
        jmiOpen.addActionListener (imageListen);
        jmiSave.addActionListener (imageListen);
        jmiOpen.setActionCommand ("FileOP");
        jmiSave.setActionCommand ("FileOP");

        jm.add (jmiOpen);
        jm.add (jmiSave);
        jmb.add (jm);
        jf.setJMenuBar (jmb);
        jf.setVisible (true);


    }


    public static void main(String[] args){
        new ImagePro ();
    }


}
