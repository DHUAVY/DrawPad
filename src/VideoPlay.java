package code.DrawPad.src;

import com.github.sarxos.webcam.Webcam;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;

// 绘制视频
public class VideoPlay extends Thread{
	Webcam webcam = null;
	ShowPanel showPanel = null;

	public VideoPlay(ShowPanel showPanel){
		this.showPanel = showPanel;
		webcam = Webcam.getDefault ();
		webcam.setViewSize (webcam.getViewSizes ()[2]);

	}

	boolean isStart = true;

	BufferedImage exImg;

	public BufferedImage getPhoto(){
		if(isStart){
			return exImg;
		}
		return null;
	}

	String videoFilter = "原图";
	ImageFilter imgF = new ImageFilter ();

	String videoOP = "停止录像";

	@Override
	public void run(){
		webcam.open ();
		Graphics g = showPanel.getGraphics ();
		while(isStart){
			BufferedImage bfImg = webcam.getImage ();
			imgF.exImg = bfImg;
			switch(videoFilter) {
				case "原图":
					exImg = imgF.drawOriginalImage ();
					break;
				case "马赛克":
					exImg = imgF.drawMosaic ();
					break;
				case "灰度":
					exImg = imgF.drawGray ();
					break;
				case "二值化":
					exImg = imgF.drawBinary ();
					break;
				case "浮雕":
					exImg = imgF.drawRelief ();
				default:
					break;
			}
			g.drawImage (exImg, 50, 50, null);

			if(videoOP.equals ("录像")){
				System.out.println ("正在录像...");
				// 保存图片
				// 获取一个时间戳
				try {
					String projectRootDirectoryPath = System.getProperty("user.dir");
					System.out.println(projectRootDirectoryPath);
					ImageIO.write (exImg, "PNG", new File (projectRootDirectoryPath + "/image/"+System.currentTimeMillis ()+".jpg"));
				} catch (IOException e) {
					throw new RuntimeException (e);
				}
			}


		}
		// 线程关闭的方式 就是run方法执行完成 线程就结束了
		webcam.close ();
		System.out.println ("线程结束");
		this.interrupt ();
	}


}
