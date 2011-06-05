
package fiz;

import com.jogamp.graph.font.Font;

import jogamp.graph.font.typecast.TypecastFont;
import jogamp.graph.font.typecast.TypecastGlyph;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;

/**
 * 
 * @author jdp
 */
public class FizFont
    extends java.awt.geom.Dimension2D
{
    private final static float Pad = 3.0f;
    private final static float PadV = 14f;


    private TypecastFont font;

    private Font.Metrics metrics;

    private double width, height;


    public FizFont(TypecastFont font, double w, double h){
        super();
        this.font = font;
        this.width = w;
        this.height = h;
        this.metrics = font.getMetrics();
    }


    public String getName(){
        return this.font.getName(0);
    }
    public double getWidth(){
        return this.width;
    }
    public double getHeight(){
        return this.height;
    }
    public void setSize( double w, double h){
        throw new UnsupportedOperationException();
    }
    public Font.Metrics getMetrics(){
        return this.metrics;
    }
    public FizGlyph getGlyph(char ch){
        try {
            return new FizGlyph(this,ch,(TypecastGlyph)this.font.getGlyph(ch));
        }
        catch (IllegalArgumentException exc){

            return null;
        }
    }
}
