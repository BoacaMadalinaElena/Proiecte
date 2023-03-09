package PaooGame.Characters;

import PaooGame.Game;
import PaooGame.Graphics.Assets;
import PaooGame.Map.Map;
import PaooGame.States.State;
import PaooGame.States.Level2GameState;
import PaooGame.States.Level3GameState;
import PaooGame.Variable;

import java.util.Vector;

import PaooGame.Map.RectangleId;

import java.awt.*;
import java.awt.image.BufferedImage;

/*! \class public class Mogly extends Elements
    \brief Implementeaza notiunea de dusman(Mogly).

    Are ca scop simularea actiunilor mamutului : miscare , coliziuni , moarte
 */
public class Mogly extends Elements {

    private BufferedImage actual;                           /*!< Referinta spre imaginea actuala cu Mogly.*/
    private boolean right = true;                           /*!< Referinta ce retine directia actuala a lui Mogly.*/
    int cnt = 0;                                            /*!< Referinta spre numarul imagini actuale.*/
    int life;                                               /*!< Referinta spre numarul de vieti a lui Mogly.*/

    /*! \fn public Mogly(int x, int y, int width, int height, int widthRectangle, int heightRectangle)
        \brief Constructorul clasei

        \param x,y,width,height,widthRectangle,heightRectangle  parametri pentru clasa de baza
     */
    public Mogly(int x, int y, int width, int height, int widthRectangle, int heightRectangle) {
        super(x, y, width, height, widthRectangle, heightRectangle);

        actual = Assets.moglyRight[0];                                  //cadrul actual al sarpelui
        life = 3;
    }

    /*! \fn public boolean Move()
        \brief Actualizeaza miscarile lui Mogly

        Se va schimba pozitia lui , imaginea de afisat(cum arata - util la simularea miscari) si se trateaza coliziunile(miscari interzise)
     */
    @Override
    public boolean Move() {
        Vector<RectangleId> c;
        if (right)
            c = getTileCollision(x + 2, y);                                            //unde ajunge prima data
        else {
            c = getTileCollision(x - 2, y);
        }
        if (c.size() == 0 && Game.map.getLevelValMap(Map.TipMap, x, y + width) != 0) {
            //avanseaza in starea in care era
            if (right) {
                x = x + 3;
                space.setLocation(x, y);                                                   //noua lui pozitie pt dreptunghi
            } else {
                x = x - 3;
                space.setLocation(x, y);                                                   //noua lui pozitie pt dreptunghi
            }
        } else {
            //merge invers
            if (right) {
                x = x - 3;
                right = false;
                actual = Assets.moglyLeft[0];
                space.setLocation(x, y);                                                   //noua lui pozitie pt dreptunghi
            } else {
                x = x + 3;
                right = true;
                actual = Assets.moglyRight[0];
                space.setLocation(x, y);                                                   //noua lui pozitie pt dreptunghi
            }
        }
        return true;
    }

    /*! \fn public void Update()
        \brief Actualizeaza pozitia lui Mogly

        Se apeleaza functia de miscare si se verifica daca a murit
    */
    @Override
    public void Update() {
        Move();
        for (int i = 0; i < Variable.gloante.size(); i++) {
            if (Variable.gloante.get(i) != null) {
                if (space.intersects(Variable.gloante.get(i).space)) {
                    //sarpele moare deci il scot din ecran
                    life--;
                    Variable.gloante.remove(Variable.gloante.get(i));
                    if (life == 0)
                        Game.mogly.remove(this);
                }
            }
        }
    }

    /*! \fn public void Draw()
        \brief Il redeseneaza pe Mogly
     */
    @Override
    public void Draw() {
        Variable.g.drawImage(actual, x, y, width, height, null);
        if (life == 3) {
            Variable.g.drawImage(Assets.life1[5], x + width / 2 - 30, y - 5, 60, 20, null);
        }
        if (life == 2)
            Variable.g.drawImage(Assets.life1[3], x + width / 2 - 30, y - 5, 60, 20, null);
        if (life == 1)
            Variable.g.drawImage(Assets.life1[1], x + width / 2 - 30, y - 5, 60, 20, null);
        if (right) {

            //selectare pozitii sarpe
            if (cnt <= 5) {
                cnt = cnt + 1;
                actual = Assets.moglyRight[2];
            } else if (cnt > 5 && cnt <= 10) {
                cnt = cnt + 1;
                actual = Assets.moglyRight[0];
            } else if (cnt > 10 && cnt <= 15) {
                cnt = cnt + 1;
                actual = Assets.moglyRight[1];
            } else if (cnt > 15 && cnt <= 20) {
                cnt = cnt + 1;
                actual = Assets.moglyRight[3];
            } else {
                cnt = 0;
            }
        } else {
            if (cnt <= 5) {
                cnt = cnt + 1;
                actual = Assets.moglyLeft[2];
            } else if (cnt > 5 && cnt <= 10) {
                cnt = cnt + 1;
                actual = Assets.moglyLeft[0];
            } else if (cnt > 10 && cnt <= 15) {
                cnt = cnt + 1;
                actual = Assets.moglyLeft[1];
            } else if (cnt > 15 && cnt <= 20) {
                cnt = cnt + 1;
                actual = Assets.moglyLeft[3];
            } else {
                cnt = 0;
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /*! \fn public Vector<RectangleId> getTileCollision(int xx, int yy)
        \brief Returneaza coliziunile
     */
    public Vector<RectangleId> getTileCollision(int xx, int yy) {
        int i;
        Vector<RectangleId> v = new Vector<>();
        if (Variable.game.level2GameState == State.GetState()) {
            for (i = 0; i < Map.RectangleTileL2.size(); i++) {
                if (new Rectangle(xx, yy, space.width, space.height).intersects(Map.RectangleTileL2.get(i).r)) {
                    //daca sarpele se intersecteaza cu ceva
                    v.add(Map.RectangleTileL2.get(i));
                }
            }
        } else if (Variable.game.level3GameState == State.GetState()) {
            for (i = 0; i < Map.RectangleTileL3.size(); i++) {
                if (new Rectangle(xx, yy, space.width, space.height).intersects(Map.RectangleTileL3.get(i).r)) {
                    //daca sarpele se intersecteaza cu ceva
                    v.add(Map.RectangleTileL3.get(i));
                }
            }
        }
        return v;
    }
}

