package PaooGame.Characters;

import PaooGame.Game;
import PaooGame.Graphics.Assets;
import PaooGame.Map.Map;
import PaooGame.Variable;

/*! \class class bullet extends Elements
    \brief Implementeaza notiunea de glont : miscare
 */
public class Bullet extends Elements {
    int sens;

    /*! \fn public bullet(int x, int y, int width, int height, int widthRectangle, int heightRectanle,int sens)
        \brief Constructorul clasei

        \param 1-6 pentru clasa de baza
        \param 7 Sensul glontului.
     */
    public Bullet(int x, int y, int width, int height, int widthRectangle, int heightRectanle, int sens) {
        super(x, y, width, height, widthRectangle, heightRectanle);
        this.sens = sens;
        setSens(sens);
    }

    @Override
    public boolean Move() {
        return false;
    }

    /*! \fn public void Update()
            \brief Actualizeaza pozitia glontului
    */
    @Override
    public void Update() {
        if (sens == 1) {
            x = x + 4;
            space.setLocation(x, y);
        } else {
            x = x - 4;
            space.setLocation(x, y);
        }
        if (Game.map.getLevelValMap(Map.TipMap, x, y) != 0) {
            Variable.gloante.remove(this);
        }
    }

    /*! \fn public void Draw()
        \brief Deseneaza glontul
     */
    @Override
    public void Draw() {
        Variable.g.drawImage(Assets.glont, x, y, width, height, null);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getSens(){
        return sens;
    }
}

