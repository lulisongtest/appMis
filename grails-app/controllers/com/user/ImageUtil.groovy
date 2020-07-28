package com.user

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage

//生成验证码
public final class ImageUtil {
    private static final String[] chars = ["0", "1", "2", "3", "4", "5", "6","7", "8", "9",
                                           "A", "B", "C", "D", "E", "F", "G", "H", "J", "K","L", "M", "N", "P",
                                           "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z","a", "b", "c", "d",
                                           "e", "f", "g", "h", "i", "j", "k", "l", "m", "n","o", "p", "q", "r",
                                           "s", "t", "u", "v", "w", "x", "y", "z"];
    private static final int SIZE = 5;// 字符长度
    private static final int LINES = 7;// 干扰线
    private static final int WIDTH = 110;
    private static final int HEIGHT = 50;
    private static final int FONT_SIZE = 30;// 字体大小

    public static Map<String, BufferedImage> createImage() {
        StringBuffer sb = new StringBuffer();
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics graphic = image.getGraphics();
        graphic.setColor(Color.LIGHT_GRAY);
        graphic.fillRect(0, 0, WIDTH, HEIGHT);
        Random ran = new Random();
        // 画随机字符
        for (int i = 1; i <= SIZE; i++) {
            int r = ran.nextInt(chars.length);
            graphic.setColor(getRandomColor());
            graphic.setFont(new Font(null, Font.BOLD + Font.ITALIC, FONT_SIZE));
            graphic.drawString(chars[r], (i - 1) * WIDTH / SIZE, HEIGHT / 2);
            sb.append(chars[r]);// 将字符保存，存入Session
        }
        // 画干扰线
        for (int i = 1; i <= LINES; i++) {
            graphic.setColor(getRandomColor());
            graphic.drawLine(ran.nextInt(WIDTH), ran.nextInt(HEIGHT),
                    ran.nextInt(WIDTH), ran.nextInt(HEIGHT));
        }
        Map<String, BufferedImage> map = new HashMap<String, BufferedImage>();
       // println("随机数 sb==111=="+sb)//返回对应的验证码
        map.put(sb.toString(), image);//返回对应的对应的验证码sb、验证码图片image
        return map;
    }

    public static Color getRandomColor() {
        Random ran = new Random();
        Color color = new Color(ran.nextInt(156), ran.nextInt(156),
                ran.nextInt(156));
        return color;
    }
}