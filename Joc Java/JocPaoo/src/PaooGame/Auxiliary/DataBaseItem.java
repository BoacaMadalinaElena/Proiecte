package PaooGame.Auxiliary;

import PaooGame.Map.RectangleId;
import PaooGame.Tiles.Tile;

/*! \class public class DataBaseItem
    \brief Retine un element salvat in baza de date pentru nivel.
 */
public class DataBaseItem {
    private String name;
    private String date;
    private RectangleId rectangle;                                    //dreptunghi pentru coliziuni

    /*! \fn public DataBaseItem(String name, String date, int x, int y, int w, int h, Tile tile)
        \brief Constructorul clasei
     */
    public DataBaseItem(String name, String date, int x, int y, int w, int h, Tile tile){
        this.name = name;
        this.date = date;
        rectangle = new RectangleId(x,y,w,h,tile);
    }
    public String getName(){
        return name;
    }
    public String getDate(){
        return date;
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
