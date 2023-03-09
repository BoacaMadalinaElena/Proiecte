package PaooGame.Map;

import PaooGame.Tiles.Tile;

import java.awt.*;

/*! \class RectangleId
    \brief Fiecare element din matrice si buton este definit ca un RectangleId(util la coliziuni).
 */
public class RectangleId {
    public Rectangle r;                             /*!< Referinta spre element(x,y,lungime,latime)*/
    public Tile tile;                               /*!< Referinta spre dala elementului*/

    /*! \fn public RectangleId(int x, int y, int w,int h,Tile t)
        \brief Constructorul clasei .

        \param x : coordonata x
        \param y : coordonata y
        \param w : lungimea
        \param h : latimea
        \param t : dala pe care o reprezinta
     */
    public RectangleId(int x, int y, int w, int h, Tile t) {
        r = new Rectangle(x, y, w, h);
        tile = t;
    }
}
