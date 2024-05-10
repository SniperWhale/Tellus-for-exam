package Window;
//gradlew build && gradlew run  I'm lazy XD

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import Blocks.Liquids.Water;
import Blocks.Particle;
import Blocks.Solids.DynamicSolid.Gravel;
import Blocks.Solids.DynamicSolid.Sand;
import Blocks.Solids.DynamicSolid.Snow;
import Blocks.Solids.StaticSolid.Wood;


public class Window extends JPanel implements ActionListener {
    
    final int screenWidth;
    final int screenHeight;
    final int tileDimension;
    final int rows;
    final int columns;

    int FPS;
    int DELAY;
    Timer timer;

    private static Grid grid;
    private boolean restart;

    private Mouse mouse = new Mouse();

    private boolean windowShouldClose = false; // set when hit esc to quit
    public Particle currentSelectedParticle = new Sand(); //Sand
    

    public Window(int screenWidth, int screenHeight, int tileDimension, int fps) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.tileDimension = tileDimension;
        this.rows = screenHeight / tileDimension;
        this.columns = screenWidth / tileDimension;

        this.FPS = fps;
        this.DELAY = 1000 / FPS;

             


        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black); //dunno if its actually even useful since we color also the empty cell with a black square
        this.setDoubleBuffered(true); //improves performance

        this.setFocusable(true); //for using keyAdapter
        this.requestFocusInWindow();
        
        this.addKeyListener(new MyKeyAdapter());

        this.addMouseWheelListener(mouse);// for mouse wheel detection, changes cursour radius
        this.addMouseMotionListener(mouse);
        this.addMouseListener(mouse);


    }

    

    public void start() {
        restart = false;
        if (timer == null) { // keep same timer even if restarted
            timer = new Timer(DELAY, this);
            timer.setRepeats(true);
            timer.start();
        }

        grid = new Grid(screenWidth, screenHeight, tileDimension);

    }
    
    public void stop() {
        JFrame ancestor = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
        ancestor.dispose();
        System.exit(0);
    }

    public boolean getWindowShouldClose() {
        return windowShouldClose;
    }

    // NOTE: MAIN LOOP
    //called every timer clock cycle
    public void actionPerformed(ActionEvent event){
        //equivalent to pygame.display.update()
        //updates screen every clock cycle
        if (restart) start();
        if (getWindowShouldClose()) stop();

        grid.updateGrid();

        if (mouse.isDragged() || mouse.isPressed()) {
            setOnClick(); // set particle on the position of the mouse, when clicked
        };
        
        

        repaint(); // calls paintComponent

    }

    //called by repaint in actionPerformed
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g; // 2d gives more access on geometry, coords, ...

        drawGrid(g2);
        drawMouse(g2);

        g2.dispose(); // frees up memory
    }

    public void setOnClick() {
        int x = mouse.getX();
        int y = mouse.getY();
        if ( 0 > x || x > screenWidth - 1 || 0 > y || y > screenHeight - 1 ) return; // check if out of bounds
        if (currentSelectedParticle instanceof Sand) grid.setParticle(mouse.getY() / tileDimension, mouse.getX() / tileDimension, new Sand());
        else if (currentSelectedParticle instanceof Snow) grid.setParticle(mouse.getY() / tileDimension, mouse.getX() / tileDimension, new Snow());
        else if (currentSelectedParticle instanceof Wood) grid.setParticle(mouse.getY() / tileDimension, mouse.getX() / tileDimension, new Wood());
        else if (currentSelectedParticle instanceof Water) grid.setParticle(mouse.getY() / tileDimension, mouse.getX() / tileDimension, new Water());
        else if (currentSelectedParticle instanceof Gravel) grid.setParticle(mouse.getY() / tileDimension, mouse.getX() / tileDimension, new Gravel());
    }

 /*    public void setMouseOnClick(){
        List<int[]> positions = drawMousePoints(g)(null); // Pass null to avoid drawing
            for (int[] position : positions) {
                setOnClick(position[0], position[1]);
            }
        }
    } */
/*     public void setOnClick() {
        int x = mouse.getX();
        int y = mouse.getY();

        int radius = mouse.getRadius() * tileDimension;
        int circleCentreX = (mouse.getX() / tileDimension) * tileDimension;
        int circleCentreY = (mouse.getY() / tileDimension) * tileDimension;
        
        int c0 = (((circleCentreX + radius) / tileDimension) * tileDimension); //c0 stands for 0 degrees on the circumference
        int c180 = (((circleCentreX - radius) / tileDimension) * tileDimension); //c180 stands for 180 degrees on the circumference
        int c90 = (((circleCentreY + radius) / tileDimension) * tileDimension); //c90 stands for 90 degrees on the circumference
        int c270 = (((circleCentreY - radius) / tileDimension) * tileDimension); //c270 stands for 270 degrees on the circumference 

        if ( 0 > x || x > screenWidth - 1 || 0 > y || y > screenHeight - 1 ) return; // check if out of bounds
            for (int i = c180; i <= c0; i += tileDimension) {
                for (int j = c270; j <= c90; j += tileDimension) {
                    // Your existing condition to check if the pixel is within the circle
                    if (radius / tileDimension == 0){
                        grid.setParticle(mouse.getY() / tileDimension, mouse.getX() / tileDimension, new Sand());
                    } else {
                        if (Math.sqrt((i - circleCentreX) * (i - circleCentreX) + (j - circleCentreY) * (j - circleCentreY)) <= radius) {
                            grid.setParticle(i, j, new Sand());
                        }
                    }
                }
            }
            //grid.setParticle(mouse.getY() / tileDimension, mouse.getX() / tileDimension, new Sand());
       
    } */

    



    // TODO: change method to go j, i (if needed tbh idk if it will give problems)
    public void drawGrid(Graphics2D g){        
        for (int i = 0; i < rows; i++){
            for (int j = 0; j < columns; j++) {
                Particle curr = grid.getAtPosition(i, j);
                int colorRed = curr.getColorRed();
                int colorGreen = curr.getColorGreen();
                int colorBlue = curr.getColorBlue();
                g.setColor(new Color(colorRed, colorGreen, colorBlue));
                g.fillRect(j*tileDimension, i*tileDimension, tileDimension, tileDimension);                
            }
        }
    }

    // converts from window's coordinate to snapped window to grid coordinates for drawing
    private int snapToGrid(int coord) {
        // (pos / tiledimension) * tiledimension works 
        // because java rounds to int the one in brackets so then we can treat it as i or j of drawGrid()
        return (coord / tileDimension) * tileDimension;
    }

    public void drawMouse(Graphics2D g) {
        g.setColor(new Color(currentSelectedParticle.getColorRed(), currentSelectedParticle.getColorGreen(), currentSelectedParticle.getColorBlue()));
        
        /*
         int radiusInPixels = mouse.getRadius() * tileDimension;
         int centerX = mouse.getX() / tileDimension * tileDimension;
         int centerY = mouse.getY() / tileDimension * tileDimension;

         g.fillOval(centerX - radiusInPixels, centerY - radiusInPixels, radiusInPixels * 2, radiusInPixels * 2);
        */
        
        int radius = mouse.getRadius() * tileDimension;
        int circleCentreX = (mouse.getX() / tileDimension) * tileDimension;
        int circleCentreY = (mouse.getY() / tileDimension) * tileDimension;
        
        int c0 = (((circleCentreX + radius) / tileDimension) * tileDimension); //c0 stands for 0 degrees on the circumference
        int c180 = (((circleCentreX - radius) / tileDimension) * tileDimension); //c180 stands for 180 degrees on the circumference
        int c90 = (((circleCentreY + radius) / tileDimension) * tileDimension); //c90 stands for 90 degrees on the circumference
        int c270 = (((circleCentreY - radius) / tileDimension) * tileDimension); //c270 stands for 270 degrees on the circumference       
        
        //int radiusInTiles = radius / tileDimension;
        // Calculate the number of tiles the circle spans in both directions
        //int numTilesX = Math.abs(c180 - c0) / tileDimension;
        //int numTilesY = Math.abs(c270 - c90) / tileDimension;

        // Adjust the loop conditions based on the actual number of tiles the circle spans
        for (int x = c180; x <= c0; x += tileDimension) {
            for (int y = c270; y <= c90; y += tileDimension) {
                // Your existing condition to check if the pixel is within the circle
                if (radius / tileDimension == 0){
                    g.fillRect(mouse.getX(), mouse.getY(), tileDimension, tileDimension);
                } else {
                    if (Math.sqrt((x - circleCentreX) * (x - circleCentreX) + (y - circleCentreY) * (y - circleCentreY)) <= radius) {
                        g.fillRect(x, y, tileDimension, tileDimension);
                    }
                }
            }
        }
    }



/*     public List<int[]> drawMousePoints(Graphics2D g) {
        List<int[]> positions = new ArrayList<>();
        int radius = mouse.getRadius() * tileDimension;
        int circleCentreX = (mouse.getX() / tileDimension) * tileDimension;
        int circleCentreY = (mouse.getY() / tileDimension) * tileDimension;
        
        int c0 = (((circleCentreX + radius) / tileDimension) * tileDimension);
        int c180 = (((circleCentreX - radius) / tileDimension) * tileDimension);
        int c90 = (((circleCentreY + radius) / tileDimension) * tileDimension);
        int c270 = (((circleCentreY - radius) / tileDimension) * tileDimension);
        
        for (int x = c180; x <= c0; x += tileDimension) {
            for (int y = c270; y <= c90; y += tileDimension) {
                if (Math.sqrt((x - circleCentreX) * (x - circleCentreX) + (y - circleCentreY) * (y - circleCentreY)) <= radius) {
                    positions.add(new int[]{x, y});
                }
            }
        }
        return positions;
    } */

    


    private class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                //full list here https://stackoverflow.com/questions/15313469/java-keyboard-keycodes-list
                case 10: // enter
                    restart = true;
                    break;

                case 27: // esc
                    windowShouldClose = true;
                    break;
                
                //keyboards input to switch currently selected particle
                case 112: //F1
                    currentSelectedParticle = new Sand();
                    break;
            
                case 113: //F2
                    currentSelectedParticle = new Snow();
                    break;
                
                case 114: //F3
                    currentSelectedParticle = new Wood();
                    break;
                
                case 115: //F4
                    currentSelectedParticle = new Water();
                    break;
                
                case 116: // F5
                    currentSelectedParticle = new Gravel();
                    break;

                
                
                default:
                    break;
            }
        }
    }


}
