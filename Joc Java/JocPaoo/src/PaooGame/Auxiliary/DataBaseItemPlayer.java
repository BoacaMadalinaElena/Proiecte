package PaooGame.Auxiliary;

import PaooGame.Map.RectangleId;
import PaooGame.Tiles.Tile;

/*! \class public class DataBaseItemPlayer
    \brief Retine un element salvat in baza de date pentru jucatori.
 */
public class DataBaseItemPlayer {
    private String name;
    private int point;
    private RectangleId rectangle;                                    //dreptunghi pentru coliziuni

    /*! \fn public DataBaseItemPlayer(String name, int point, int x, int y, int w, int h, Tile tile)
        \brief Constructorul clasei
     */
    public DataBaseItemPlayer(String name, int point, int x, int y, int w, int h, Tile tile){
        this.name = name;
        this.point = point;
        rectangle = new RectangleId(x,y,w,h,tile);
    }
    public String getName(){
        return name;
    }
    public int getPoint(){
        return point;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setPoint(int point){
        this.point = point;
    }
    public RectangleId getRectangle(){
        return rectangle;
    }
    public void setX(int x){
        this.rectangle.r.x = x;
    }
    public void setY(int y){
        this.rectangle.r.y = y;
    }
}
