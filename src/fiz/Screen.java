
package fiz;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;


public final class Screen
    extends Object
{
    volatile static Screen Current;



    public final GraphicsEnvironment environment;

    public final GraphicsDevice device;

    public final GraphicsConfiguration configuration;

    public final Rectangle screen, window;


    /**
     * Fullscreen, with insets.
     */
    public Screen(Window window){
        this(window,GraphicsEnvironment.getLocalGraphicsEnvironment(),window.getGraphicsConfiguration());
    }
    private Screen(Window window, GraphicsEnvironment environment, GraphicsConfiguration gc){
        this(window,environment,gc,gc.getDevice());
    }
    private Screen(Window window, GraphicsEnvironment environment, GraphicsConfiguration gc, GraphicsDevice device){
        super();
        Current = this;
        this.environment = environment;
        this.device = device;
        this.configuration = gc;
        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
        this.screen = this.configuration.getBounds();
        this.screen.y += insets.top;
        this.screen.height -= (insets.top + insets.bottom);
        this.screen.x += insets.left;
        this.screen.height -= (insets.left + insets.right);
        this.window = new Rectangle(this.screen);
        insets = window.getInsets();
        this.window.y += insets.top;
        this.window.height -= (insets.top + insets.bottom);
        this.window.x += insets.left;
        this.window.height -= (insets.left + insets.right);
    }


    public final GraphicsEnvironment getEnvironment(){
        return this.environment;
    }
    public final GraphicsDevice getDevice(){
        return this.device;
    }
    public final GraphicsConfiguration getConfiguration(){
        return this.configuration;
    }
}
