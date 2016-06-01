
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class Main extends JComponent implements MouseListener, KeyListener{
 
    static final int WIDTH = 800, HEIGHT = 800;
    static JFrame frame;
    
    Color[][] grid = new Color[100][100];
    BufferedImage img;
    
    int pixWidth = WIDTH/grid[0].length;
    int pixHeight = HEIGHT/grid.length;
    
    boolean done = false;
    ArrayList<Point> targets = new ArrayList();
    ArrayList<Worm> worms = new ArrayList();
    
    final int FPS = 60;
    
    Color backGroundColor = Color.BLACK;
    
    int targetX, targetY;
    
    boolean paused = false;
    
    public static void main(String[] args){
        
        Main main = new Main();
        
        frame = new JFrame("Colors");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(main);
        frame.getContentPane().setPreferredSize(new Dimension(WIDTH, HEIGHT));
        frame.pack();
        frame.setVisible(true);
        main.addMouseListener(main);
        frame.addKeyListener(main);
        
        main.run();
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        g.setColor(backGroundColor);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        
        g.setColor(Color.DARK_GRAY);
        for (int i = 0; i < HEIGHT; i += pixHeight)
        {
            g.drawLine(0, i, WIDTH, i);
        }
        for (int i = 0; i < WIDTH; i += pixWidth)
        {
            g.drawLine(i, 0, i, HEIGHT);
        }
        
        for (Worm worm: worms)
        {
            for (Point p: worm.getPoints())
            {
                g.setColor(grid[p.y][p.x]);
                g.fillRect(p.x*pixWidth, p.y*pixHeight, pixWidth, pixHeight);
            }
        }
        for (Point p: targets)
        {
            g.setColor(Color.GREEN);
            g.fillRect(p.x*pixWidth, p.y*pixHeight, pixWidth, pixHeight);
        }
    }
    
    private void saveImage()
    {
        int[] rgbArray = new int[grid.length*grid[0].length];
        for (int y = 0; y < grid.length; y ++)
        {
            for (int x = 0; x < grid[y].length; x ++)
            {
                rgbArray[y*grid[y].length+x] = grid[y][x].getRGB();
            }
        }
        
        img = new BufferedImage( grid[0].length, grid.length, BufferedImage.TYPE_INT_RGB );
        img.setRGB(0, 0, grid[0].length, grid.length, rgbArray, 0, grid[0].length);
        
        
        
        try {
            int fileNum = getNumImgs("images/")+1;
            String filepath = "images/img_" + fileNum + ".png";
            File outputfile = new File(filepath).getAbsoluteFile();
            ImageIO.write(img, "png", outputfile);
        } catch (IOException e) {
            System.err.println(e);
        }
    }
    
    private void run()
    {
        for (int y = 0; y < grid.length; y ++)
        {
            for (int x = 0; x < grid[y].length; x ++)
            {
                grid[y][x] = Color.GREEN;
                targets.add(new Point(x, y));
            }
        }
        while(!done)
        {
            if (!paused)
            {
                if (!targets.isEmpty())
                {
                    for (Worm worm: worms)
                    {
                        worm.move(targets, grid);
                    }
                } else {
                    for (Worm worm: worms)
                    {
                        if (worm.shrinking())
                            worm.shrink(grid);
                    }
                }
            }
            repaint();
            
            try {
                Thread.sleep(1000/FPS);
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            repaint();
        }
            
        frame.setVisible(false); 
        frame.dispose();
    }
    
    private void spawnWorm(int x, int y )
    {
        grid[y][x] = Color.WHITE;
        worms.add(new Worm(x, y, randomInt(5, 20)));
    }
    
    private int randomInt(int min, int max)
    {
        return (int)(Math.random()*(max-min+1))+min;
    }
    
    public void spawnTarget(int x, int y)
    {
        grid[y][x] = Color.GREEN;
        targets.add(new Point(x, y));
    }
    
    public void reset()
    {
        for (int y = 0; y < grid.length; y ++)
        {
            for (int x = 0; x < grid[y].length; x ++)
            {
                grid[y][x] = Color.BLACK;
            }
        }
        worms.clear();
        targets.clear();
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

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int clickX = e.getX()/pixWidth;
        int clickY = e.getY()/pixHeight;
        //if (grid[clickY][clickX] == Color.BLACK){
            if (e.getButton() == 1)
                spawnWorm(clickX, clickY);
            else if (e.getButton() == 3)
                spawnTarget(clickX, clickY);
        //}
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e){
        if (e.getKeyChar()== 's')
        {
            saveImage();
        }
        else if (e.getKeyCode() == KeyEvent.VK_SPACE)
        {
            paused = paused ? false: true;
            backGroundColor = paused ? Color.BLUE : Color.BLACK;
        } else if (e.getKeyChar() == 'r')
        {
            reset();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
    
}
