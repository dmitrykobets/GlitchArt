
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author kobed6328
 */
public class Main extends JComponent{
 
    static final int WIDTH = 1600, HEIGHT = 900;
    static BufferedImage img;
    
    public static void main(String[] args){
        
        int fileNum = getNumImgs("images/")+1;
        img = genImage(WIDTH, HEIGHT);
        
        
        JFrame frame = new JFrame("Colors");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new Main());
        frame.getContentPane().setPreferredSize(new Dimension(WIDTH, HEIGHT));
        frame.pack();
        frame.setVisible(true);
        
        Scanner in = new Scanner(System.in);
        System.out.print("save img_" + fileNum + "?\n>> ");
        if (in.next().startsWith("y")){
            saveImage(img, "images/img_" + fileNum + ".png");
            
            frame.setVisible(false); 
            frame.dispose();
        }
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        g.drawImage(img, 0, 0, this);
    }
    
    private static void saveImage(BufferedImage img, String filepath)
    {
        try {
            File outputfile = new File(filepath).getAbsoluteFile();
            ImageIO.write(img, "png", outputfile);
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
    
    private static BufferedImage genImage(int width, int height)
    {
        
        int[] rgbArray = new int[width * height];
        int i = 0;
        
        int sourceX = 25;
        int sourceY = 25;
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int red = y&x;
                int green = y^red;
                int blue = red*green;
                rgbArray[i++] = (red << 16) | (green << 8) | blue;
            }
        }
        
        BufferedImage img  = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );
        img.setRGB(0, 0, width, height, rgbArray, 0, width);
        
        return img;
    }
    
    private static int getNumImgs(String filepath)
    {
        File[] folder = new File("images/").listFiles();
        int count = 0;
        for (File file: folder)
        {
            if (file.getName().endsWith(".png"))
                count ++;
        }
        return count;
    }
    
}
