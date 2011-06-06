
package fiz;

import com.jogamp.graph.font.Font;
import com.jogamp.graph.font.FontSet;
import jogamp.graph.font.JavaFontLoader;
import jogamp.graph.font.typecast.TypecastFont;
import jogamp.graph.font.typecast.TypecastGlyph;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;


public class Main 
    extends java.awt.Frame 
    implements java.awt.event.ComponentListener,
               java.awt.event.KeyListener,
               java.awt.event.MouseListener,
               java.awt.event.MouseMotionListener,
               java.awt.event.MouseWheelListener, 
               java.awt.event.WindowListener
{

    public static void main(String[] argv){

        Main main = new Main();
        Screen screen = new Screen(main);
        main.init(screen);
    }

    private enum Nav {
        Fonts, Glyphs;
    }



    private final FontSet fontsDir;

    private volatile int fontsDirFamily = FontSet.FAMILY_MONOSPACED;


    private volatile FizFont font ;

    private volatile Rectangle2D.Double display;

    private volatile Point2D.Double displayCenter;

    private volatile char glyphIndex;

    private volatile boolean cursor;

    private volatile Nav nav = Nav.Glyphs;

    private volatile String titleString;

    private volatile FizGlyph glyph;

    private volatile BufferedImage backing;


    public Main(){
        super("");
        this.addComponentListener(this);
        this.addKeyListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
        this.addWindowListener(this);

        this.setBackground(Style.BG);
        this.setForeground(Style.FG);

        this.fontsDir = JavaFontLoader.get();
    }


    public void init(Screen screen){
        Rectangle window = screen.window;
        this.reshape(window.x,window.y,window.width,window.height);
        this.show();
    }
    public void init(){
        Font font = this.fontsDir.get(this.fontsDirFamily,0);
        if (null != font){
            this.font = new FizFont( (TypecastFont)font,(this.display.width*0.9),(this.display.height*0.9));

            this.setName(this.font.getName());

            if (null != this.glyph){
                if (!this.glyphSet(this.glyph.getSymbol(),false))
                    this.glyphSet('A',false);
            }
            else
                this.glyphSet('A',false);
        }
    }
    public void reshape(int x, int y, int w, int h){
        super.reshape(x,y,w,h);
        
        this.display = new Rectangle2D.Double(72d,72d,(w-144d),(h-144d));

        this.displayCenter = new Point2D.Double(((display.width-display.x)/2.0),((display.height-display.y)/2.0));

        this.init();
    }
    protected boolean glyphDec(int dec){

        return this.glyphSet( (char)(this.glyphIndex-dec));
    }
    protected boolean glyphInc(int inc){

        return this.glyphSet( (char)(this.glyphIndex+inc));
    }
    protected boolean glyphHome(){
        
        return this.glyphSet('!'); // ASCII begin
    }
    protected boolean glyphEnd(){

        return this.glyphSet('~'); // ASCII end
    }
    protected boolean glyphSet(char ch){
        return this.glyphSet(ch,true);
    }
    protected boolean glyphSet(char ch, boolean nav){

        if (nav)
            this.nav = Nav.Glyphs;

        if (ch != this.glyphIndex || (!nav)){

            FizGlyph glyph = this.font.getGlyph(ch);
            if (null != glyph){
                this.glyphIndex = ch;
                this.glyph = glyph;

                this.titleString = String.format("%s glyph '%c' 0x%x",this.font.getName(),ch,(int)ch);
            }
            else {
                this.titleString = String.format("%s glyph not found '%c' 0x%x",this.font.getName(),ch,(int)ch);
            }
            return true;
        }
        else
            return false;
    }
    protected boolean fontsInc(int n){
        this.nav = Nav.Fonts;

        this.fontsDirFamily += n;
        if (this.fontsDirFamily > FontSet.FAMILY_MONOSPACED)
            this.fontsDirFamily = FontSet.FAMILY_REGULAR;
        this.init();
        return true;
    }
    protected boolean fontsDec(int n){
        this.nav = Nav.Fonts;

        this.fontsDirFamily -= n;
        if (0 > this.fontsDirFamily)
            return this.fontsHome();
        else {
            this.init();
        }
        return true;
    }
    protected boolean fontsHome(){
        this.nav = Nav.Fonts;
        int idx = FontSet.FAMILY_REGULAR;
        if (idx != this.fontsDirFamily){
            this.fontsDirFamily = idx;
            this.init();
            return true;
        }
        else
            return false;
    }
    protected boolean fontsEnd(){
        this.nav = Nav.Fonts;
        int idx = FontSet.FAMILY_MONOSPACED;
        if (idx != this.fontsDirFamily){
            this.fontsDirFamily = idx;
            this.init();
            return true;
        }
        else
            return false;
    }

    protected boolean inc(){
        switch (this.nav){
        case Fonts:
            return this.fontsInc(1);
        default:
            return this.glyphInc(1);
        }
    }
    protected boolean dec(){

        switch (this.nav){
        case Fonts:
            return this.fontsDec(1);
        default:
            return this.glyphDec(1);
        }
    }
    protected boolean pgUp(){
        switch (this.nav){
        case Fonts:
            return this.fontsDec(10);
        default:
            return this.glyphDec(10);
        }
    }
    protected boolean pgDn(){
        switch (this.nav){
        case Fonts:
            return this.fontsInc(10);
        default:
            return this.glyphInc(10);
        }
    }
    protected boolean home(){
        switch (this.nav){
        case Fonts:
            return this.fontsHome();
        default:
            return this.glyphHome();
        }
    }
    protected boolean end(){
        switch (this.nav){
        case Fonts:
            return this.fontsEnd();
        default:
            return this.glyphEnd();
        }
    }
    public void update(Graphics g){
        BufferedImage backing = this.backing;
        if (null == backing)
            backing = this.backing();
        Graphics2D bg = backing.createGraphics();
        try {
            this.draw(bg);
        }
        finally {
            bg.dispose();
        }
        g.drawImage(backing,0,0,this);
    }
    public void paint(Graphics g){
        this.update(g);
    }
    public void draw(Graphics2D g){
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                           RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                           RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);


        g.setColor(Color.white);
        Rectangle bounds = this.getBounds();
        g.fillRect(0,0,bounds.width,bounds.height);

        FizGlyph glyph = this.glyph;

        if (null != glyph){

            this.drawTitle(g);

            g.setColor(this.getForeground());

            glyph.draw(g);
        }
        else {
            this.drawTitle(g);
        }
    }

    private void drawTitle(Graphics2D g){

        String title = this.titleString;
        if (null != title){
            float x = 30.0f, y = 30.0f;

            g.setColor(this.getBackground());
            g.drawString(title,x-0.8f,y-0.8f);
            g.setColor(this.getForeground());
            g.drawString(title,x,y);
        }
    }

    private BufferedImage backing(){
        if (null != this.backing)
            this.backing.flush();
        this.backing = new BufferedImage(this.getWidth(),this.getHeight(),BufferedImage.TYPE_INT_ARGB);
        return this.backing;
    }
    public void componentResized(ComponentEvent e){
        Rectangle bounds = this.getBounds();

        BufferedImage backing = this.backing;
        if (null != backing){
            this.backing = null;
            backing.flush();
        }
        this.backing = new BufferedImage(bounds.width,bounds.height,BufferedImage.TYPE_INT_ARGB);

        this.reshape(bounds.x,bounds.y,bounds.width,bounds.height);
        this.repaint();
    }
    public void componentMoved(ComponentEvent e){
    }
    public void componentShown(ComponentEvent e){
        this.backing();
    }
    public void componentHidden(ComponentEvent e){
    }
    public void windowOpened(WindowEvent e){
        this.requestFocus();
    }
    public void windowClosing(WindowEvent e) {
        this.hide();
        this.dispose();
    }
    public void windowClosed(WindowEvent e) {
        System.exit(0);
    }
    public void windowIconified(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}

    public void keyTyped(KeyEvent e){
        if (!e.isActionKey()){
            char ch = e.getKeyChar();
            this.glyphSet(ch);
            this.repaint();
        }
    }
    public void keyPressed(KeyEvent e){
    }
    public void keyReleased(KeyEvent e){
        if (e.isActionKey()){
            switch (e.getKeyCode()){
            case KeyEvent.VK_HOME:
                if (this.home())
                    this.repaint();
                break;
            case KeyEvent.VK_PAGE_UP:
                if (this.pgUp())
                    this.repaint();
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_UP:
                if (this.dec())
                    this.repaint();
                break;
            case KeyEvent.VK_END:
                if (this.end())
                    this.repaint();
                break;
            case KeyEvent.VK_PAGE_DOWN:
                if (this.pgDn())
                    this.repaint();
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_DOWN:
                if (this.inc())
                    this.repaint();
                break;
            default:
                this.hide();
                this.dispose();
                break;
            }
        }
    }
    public void mouseClicked(MouseEvent e){

        if (e.getX() < 600){

            if (e.getY() < 100){
                if (e.isPopupTrigger())
                    this.fontsDec(1);
                else 
                    this.fontsInc(1);
            }

        }
        else {
            if (e.getY() < 100){
                if (e.isPopupTrigger())
                    this.fontsDec(1);
                else 
                    this.fontsInc(1);
            }
            else {
                if (e.isPopupTrigger())
                    this.glyphDec(1);
                else 
                    this.glyphInc(1);
            }
        }
        this.repaint();
        this.requestFocus();
    }
    public void mousePressed(MouseEvent e){
    }
    public void mouseReleased(MouseEvent e){
    }
    public void mouseEntered(MouseEvent e){
    }
    public void mouseExited(MouseEvent e){
    }
    public void mouseWheelMoved(MouseWheelEvent e){
        if (0 > e.getWheelRotation()){
            if (this.pgUp())
                this.repaint();
        }
        else {
            if (this.pgDn())
                this.repaint();
        }
    }
    public void mouseDragged(MouseEvent e){
    }
    public void mouseMoved(MouseEvent e){
        if (e.getX() < 600){
            if (this.cursor)
                return;
            else {
                this.cursor = true;
                this.setCursor(Cursor.HAND_CURSOR);
            }
        }
        else if (this.cursor){
            this.cursor = false;
            this.setCursor(Cursor.DEFAULT_CURSOR);
        }
    }
}
