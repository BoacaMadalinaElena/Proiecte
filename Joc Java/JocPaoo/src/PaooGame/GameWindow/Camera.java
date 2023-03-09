package PaooGame.GameWindow;

import PaooGame.Game;
import PaooGame.Characters.Hero;

/*! \class public class Camera
    \brief Creaza conceptul de camera(deplsare imagine dupa personaj)

    Clasa retine pozitia actuala din centrul camerei
 */
public class Camera {
    private int x;                      /*!< Referinta catre pozitia x a camerei*/
    private int y;                      /*!< Referinta catre pozitia y a cemerei*/

    public Camera(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /*! \fn  public void update(Hero cat)
        \brief Schimba coordonate unde trebuie sa fie camera(pe ox)
    */
    public void update(Hero cat) {
        x = -cat.getX() + Game.Width / 2;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
