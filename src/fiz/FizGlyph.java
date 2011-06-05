
package fiz;


import jogamp.graph.font.typecast.TypecastGlyph;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;

/**
 * 
 * @author jdp
 */
public class FizGlyph
    extends Object
{
    private final static double ControlR = 3.0;
    private final static double ControlD = (2.0 * ControlR);
    private final static float PathPointDX = 14f;
    private final static float PathPointDY = 8f;



    private FizFont font;

    private char ch;

    private TypecastGlyph glyph;

    protected Path2D.Float path2d;



    protected FizGlyph(FizFont font, char ch, TypecastGlyph glyph){
        super();
        this.font = font;
        this.ch = ch;
        this.glyph = glyph;
        if (null != this.glyph){
            jogamp.graph.geom.plane.Path2D source = this.glyph.getPath();
            if (jogamp.graph.geom.plane.Path2D.WIND_EVEN_ODD == source.getWindingRule())
                this.path2d = new Path2D.Float(Path2D.WIND_EVEN_ODD);
            else
                this.path2d = new Path2D.Float(Path2D.WIND_NON_ZERO);

            jogamp.graph.geom.plane.PathIterator si = source.iterator();
            float[] coords = new float[6];

            while (!si.isDone()){
                int op = si.currentSegment(coords);
                switch(op){
                case 0:
                    this.path2d.moveTo(coords[0],coords[1]);
                    break;
                case 1:
                    this.path2d.lineTo(coords[0],coords[1]);
                    break;
                case 2:
                    this.path2d.quadTo(coords[0],coords[1],coords[2],coords[3]);
                    break;
                case 3:
                    this.path2d.curveTo(coords[0],coords[1],coords[2],coords[3],coords[4],coords[5]);
                    break;
                case 4:
                    this.path2d.closePath();
                    break;
                default:
                    throw new IllegalStateException(String.valueOf(op));
                }
                si.next();
            }
        }
        else
            throw new IllegalArgumentException(String.valueOf(ch));
    }


    public char getSymbol(){

        return this.glyph.getSymbol();
    }
    public void draw(Graphics2D g){

        g.draw(this.path2d);
    }
}
