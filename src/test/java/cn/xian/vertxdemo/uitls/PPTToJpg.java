package cn.xian.vertxdemo.uitls;


import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hslf.model.TextRun;
import org.apache.poi.hslf.usermodel.RichTextRun;
import org.apache.poi.hslf.usermodel.SlideShow;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class PPTToJpg {
    public static void main(String[] args) {
        // 读入PPT文件
        pptToImage("/home/xian/Downloads/1.pptx", "/home/xian/Desktop/images");
    }

    public static void toImage(String src, String dir) {
        if (src.endsWith(".ppt")) {
        } else if (src.endsWith(".pptx")) {
            pptxToImage(src, dir);
        } else {
            return;
        }
    }

    public static void pptToImage(String src, String dir) {
        FileInputStream is = null;
        try {
            File file = new File(src);
            is = new FileInputStream(file);
            SlideShow ppt = new SlideShow(is);
            Dimension pgsize = ppt.getPageSize();
            org.apache.poi.hslf.model.Slide[] slide = ppt.getSlides();
            for (int i = 0; i < slide.length; i++) {
                BufferedImage img = new BufferedImage(pgsize.width, pgsize.height,
                        BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics = img.createGraphics();
                graphics.setPaint(Color.BLUE);
                graphics.fill(new Rectangle2D.Float(0, 0, pgsize.width, pgsize.height));
                slide[i].draw(graphics);
                // 这里设置图片的存放路径和图片的格式(jpeg,png,bmp等等),注意生成文件路径
                File path = new File(dir);
                if (!path.exists()) {
                    path.mkdir();
                }
                // 可测试多种图片格式
                FileOutputStream out = new FileOutputStream(String.format("%s/px%03d.jpg", path, (i + 1)));
                javax.imageio.ImageIO.write(img, "png", out);
                out.close();
            }
            System.out.println("success!!");
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    public static void pptxToImage(String src, String dir) {
        FileInputStream is = null;
        try {
            File file = new File(src);
            is = new FileInputStream(file);
            XMLSlideShow ppt = new XMLSlideShow(is);
            Dimension pgsize = ppt.getPageSize();
            XSLFSlide[] slide = ppt.getSlides();
            for (int i = 0; i < slide.length; i++) {
                BufferedImage img = new BufferedImage(pgsize.width, pgsize.height,
                        BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics = img.createGraphics();
                graphics.setPaint(Color.BLUE);
                graphics.fill(new Rectangle2D.Float(0, 0, pgsize.width, pgsize.height));
                slide[i].draw(graphics);
                // 这里设置图片的存放路径和图片的格式(jpeg,png,bmp等等),注意生成文件路径
                File path = new File(dir);
                if (!path.exists()) {
                    path.mkdir();
                }
                // 可测试多种图片格式
                FileOutputStream out = new FileOutputStream(String.format("%s/px%03d.jpg", path, (i + 1)));
                javax.imageio.ImageIO.write(img, "png", out);
                out.close();
            }
            System.out.println("success!!");
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }
}