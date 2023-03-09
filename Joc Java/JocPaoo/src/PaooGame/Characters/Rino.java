package PaooGame.Characters;

import PaooGame.Game;
import PaooGame.Graphics.Assets;
import PaooGame.Map.Map;
import PaooGame.States.State;
import PaooGame.Variable;

import java.util.Vector;

import PaooGame.Map.RectangleId;

import java.awt.*;
import java.awt.image.BufferedImage;

/*! \class public class Rino extends Elements
    \brief Implementeaza notiunea de dusman(mamutul Rino).

    Are ca scop simularea actiunilor sarpelui : miscare , coliziuni , moarte
 */
public class Rino extends Elements {

    private BufferedImage actual;                       /*!< Referinta catre imaginea actuala cu Rino */
    private boolean right = true;                       /*!< Referinta catre sensul de deplasare */
    int cnt = 0;                                        /*!< Referinta catre o variabila ce retine imaginea actuala a lui Rino*/
    int life;                                           /*!< Referints catre numarul de vieti a lui Rino */

    /*! \fn public Rino(int x, int y, int width, int height, int widthRectangle, int heightRectangle)
        \brief Constructorul clasei

        \param x,y,width,height,widthRectangle,heightRectangle parametri pentru clasa de baza
     */
    public Rino(int x, int y, int width, int height, int widthRectangle, int heightRectangle) {
        super(x, y, width, height, widthRectangle, heightRectangle);

        actual = Assets.rinoRight[0];                                  //cadrul actual a lui Rino
        life = 2;
    }

    /*! \fn public boolean Move()
        \brief Actualizeaza miscarile mamutului

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
                actual = Assets.rinoLeft[0];
                space.setLocation(x, y);                                                   //noua lui pozitie pt dreptunghi
            } else {
                x = x + 3;
                right = true;
                actual = Assets.rinoRight[0];
                space.setLocation(x, y);                                                   //noua lui pozitie pt dreptunghi
            }
        }
        return true;
    }

    /*! \fn public void Update()
        \brief Actualizeaza mamutul

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
                        if (Variable.game.level2GameState == State.GetState())
                            Game.rino.remove(this);
                        else if (Variable.game.level3GameState == State.GetState())
                            Game.rino.remove(this);
                }
            }
        }
    }

    /*! \fn public void Draw()
        \brief Redeseneaza mamutul
     */
    @Override
    public void Draw() {
        if (right) {
            Variable.g.drawImage(actual, x, y, width, height, null);
            if (life == 2)
                Variable.g.drawImage(Assets.life1[3], x + width / 2 - 30, y + 40, 60, 20, null);
            if (life == 1)
                Variable.g.drawImage(Assets.life1[1], x + width / 2 - 30, y + 40, 60, 20, null);
            //selectare pozitii sarpe
            if (cnt <= 5) {
                cnt = cnt + 1;
                actual = Assets.rinoRight[2];
            } else if (cnt > 5 && cnt <= 10) {
                cnt = cnt + 1;
                actual = Assets.rinoRight[0];
            } else if (cnt > 10 && cnt <= 15) {
                cnt = cnt + 1;
                actual = Assets.rinoRight[1];
            } else if (cnt > 15 && cnt <= 20) {
                cnt = cnt + 1;
                actual = Assets.rinoRight[3];
            } else {
                cnt = 0;
            }
        } else {
            Variable.g.drawImage(actual, x, y, width, height, null);
            if (life == 2)
                Variable.g.drawImage(Assets.life1[3], x + width / 2 - 30, y + 40, 60, 20, null);
            if (life == 1)
                Variable.g.drawImage(Assets.life1[1], x + width / 2 - 30, y + 40, 60, 20, null);
            if (cnt <= 5) {
                cnt = cnt + 1;
                actual = Assets.rinoLeft[2];
            } else if (cnt > 5 && cnt <= 10) {
                cnt = cnt + 1;
                actual = Assets.rinoLeft[0];
            } else if (cnt > 10 && cnt <= 15) {
                cnt = cnt + 1;
                actual = Assets.rinoLeft[1];
            } else if (cnt > 15 && cnt <= 20) {
                cnt = cnt + 1;
                actual = Assets.rinoLeft[3];
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

