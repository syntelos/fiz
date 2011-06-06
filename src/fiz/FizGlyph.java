
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
    public static class Point
        extends Ellipse2D.Float
    {
        private final static float Rad = 3.0f;
        private final static float Dia = (2.0f * Rad);
        private final static float LDX = 14f;
        private final static float LDY = 8f;


        public static class Control
            extends Point
        {
            public Control(int index, float x, float y){
                super(index,Style.L2,x,y);
            }
        }
        public static class Oncurve
            extends Point
        {
            public Oncurve(int index, float x, float y){
                super(index,Style.L1,x,y);
            }
        }


        public final int index;

        private Color color;


        public Point(int index, Color color, float x, float y){
            super(x,y,Dia,Dia);
            this.index = index;
            this.color = color;
        }

        public void draw(Graphics2D g){
            g.setColor(this.color);
            g.fill(this);
            g.drawString(String.valueOf(this.index),(int)(this.x+LDX),(int)(this.y+LDY));
        }
    }





    private FizFont font;

    private char ch;

    private TypecastGlyph glyph;

    protected Path2D.Float path2d;

    protected Point[] points2d;



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

            int index = 0;

            while (!si.isDone()){
                int op = si.currentSegment(coords);
                switch(op){
                case 0:
                    this.moveTo(index,coords[0],coords[1]);
                    index += 1;
                    break;
                case 1:
                    this.lineTo(index,coords[0],coords[1]);
                    index += 1;
                    break;
                case 2:
                    this.quadTo(index,coords[0],coords[1],coords[2],coords[3]);
                    index += 2;
                    break;
                case 3:
                    this.curveTo(index,coords[0],coords[1],coords[2],coords[3],coords[4],coords[5]);
                    index += 3;
                    break;
                case 4:
                    this.closePath();
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


    private void moveTo(int ix, float x0, float y0){
        this.addOncurve(ix,x0,y0);
        this.path2d.moveTo(x0, y0);
    }
    private void lineTo(int ix, float x0, float y0){
        this.addOncurve(ix,x0,y0);
        this.path2d.lineTo(x0, y0);
    }
    private void quadTo(int ix, float x0, float y0, float x1, float y1){
        this.addControl(ix++,x0,y0);
        this.addOncurve(ix,x1,y1);
        this.path2d.quadTo(x0, y0, x1, y1);
    }
    private void curveTo(int ix, float x0, float y0, float x1, float y1, float x2, float y2){
        this.addControl(ix++,x0,y0);
        this.addControl(ix++,x1,y1);
        this.addOncurve(ix,x2,y2);
        this.path2d.curveTo(x0, y0, x1, y1, x2, y2);
    }
    private void closePath(){
        this.path2d.closePath();
    }
    private void addControl(int ix, float x, float y){

        this.add(new Point.Control(ix,x,y));
    }
    private void addOncurve(int ix, float x, float y){

        this.add(new Point.Oncurve(ix,x,y));
    }
    private void add(Point ellipse){
        if (null == this.points2d)
            this.points2d = new Point[]{ellipse};
        else {
            int len = this.points2d.length;
            if (1 == len)
                this.points2d = new Point[]{this.points2d[0],ellipse};
            else {
                Point[] copier = new Point[len+1];
                System.arraycopy(this.points2d,0,copier,0,len);
                copier[len] = ellipse;
                this.points2d = copier;
            }
        }
    }
    public char getSymbol(){

        return this.glyph.getSymbol();
    }
    public void draw(Graphics2D g){

        g.setColor(Style.FG);

        g.draw(this.path2d);

        for (Point pt: this.points2d){

            pt.draw(g);
        }
    }
}
