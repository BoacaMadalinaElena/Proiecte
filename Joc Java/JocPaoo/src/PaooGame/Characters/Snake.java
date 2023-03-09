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

/*! \class public class Snake implements Elements
    \brief Implementeaza notiunea de dusman(sarpele).

    Are ca scop simularea actiunilor sarpelui : miscare , coliziuni , moarte
 */
public class Snake extends Elements {

    private BufferedImage actual;                           /*!< Referinta catre imaginea actuala a sarpelui.*/
    private boolean right = true;                           /*!< Referinta catre o variabila ce retine sensul sarpelui.*/
    ///sarpele incarca mai multe imagini pentru a simula miscarea
    int cnt = 0;                                            /*!< Referinta spre un numer ce retine ce imagine incarc*/

    /*! \fn public Snake(int x, int y, int width, int height, int widthRectangle, int heightRectangle)
        \brief Constructorul clasei

        \param x,y,width,height,widthRectangle,heightRectangle  parametri pentru clasa de baza
     */
    public Snake(int x, int y, int width, int height, int widthRectangle, int heightRectangle) {
        super(x, y, width, height, widthRectangle, heightRectangle);
        actual = Assets.snakeRight[0];                                  //cadrul actual al sarpelui
    }

    /*! \fn public boolean Move()
        \brief Actualizeaza miscarile sarpelui

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
                actual = Assets.snakeLeft[0];
                space.setLocation(x, y);                                                   //noua lui pozitie pt dreptunghi
            } else {
                x = x + 3;
                right = true;
                actual = Assets.snakeRight[0];
                space.setLocation(x, y);                                                   //noua lui pozitie pt dreptunghi
            }
        }
        return true;
    }

    /*! \fn public void Update()
        \brief Actualizeaza sarpele

        Se apeleaza functia de miscare si se verifica daca a murit
    */
    @Override
    public void Update() {
        Move();
        for (int i = 0; i < Variable.gloante.size(); i++) {
            if (Variable.gloante.get(i) != null) {
                if (space.intersects(Variable.gloante.get(i).space)) {
                    //sarpele moare deci il scot din ecran
                    if (State.GetState() == Variable.game.level1GameState)
                        Game.snake.remove(this);
                    else if (State.GetState() == Variable.game.level2GameState)
                        Game.snake.remove(this);
                    else
                        Game.snake.remove(this);
                    Variable.gloante.remove(Variable.gloante.get(i));
                }
            }
        }

    }

    /*! \fn public void Draw()
        \brief Redeseneaza sarpele
     */
    @Override
    public void Draw() {
        if (right) {
            Variable.g.drawImage(actual, x, y, width, height, null);
            //selectare pozitii sarpe
            if (cnt <= 5) {
                cnt = cnt + 1;
                actual = Assets.snakeRight[2];
            } else if (cnt > 5 && cnt <= 10) {
                cnt = cnt + 1;
                actual = Assets.snakeRight[0];
            } else if (cnt > 10 && cnt <= 15) {
                cnt = cnt + 1;
                actual = Assets.snakeRight[1];
            } else if (cnt > 15 && cnt <= 20) {
                cnt = cnt + 1;
                actual = Assets.snakeRight[5];
            } else if (cnt > 20 && cnt <= 25) {
                cnt = cnt + 1;
                actual = Assets.snakeRight[6];
            } else if (cnt > 25 && cnt <= 30) {
                cnt = cnt + 1;
                actual = Assets.snakeRight[6];
            } else {
                cnt = 0;
            }
        } else {
            Variable.g.drawImage(actual, x, y, width, height, null);
            if (cnt <= 5) {
                cnt = cnt + 1;
                actual = Assets.snakeLeft[2];
            } else if (cnt > 5 && cnt <= 10) {
                cnt = cnt + 1;
                actual = Assets.snakeLeft[0];
            } else if (cnt > 10 && cnt <= 15) {
                cnt = cnt + 1;
                actual = Assets.snakeLeft[1];
            } else if (cnt > 15 && cnt <= 20) {
                cnt = cnt + 1;
                actual = Assets.snakeLeft[5];
            } else if (cnt > 20 && cnt <= 25) {
                cnt = cnt + 1;
                actual = Assets.snakeLeft[6];
            } else if (cnt > 25 && cnt <= 30) {
                cnt = cnt + 1;
                actual = Assets.snakeLeft[6];
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
        \brief Returneaza coliziunile sarpelui cu elemente din harta
     */
    public Vector<RectangleId> getTileCollision(int xx, int yy) {
        int i;
        Vector<RectangleId> v = new Vector<>();
        if (Variable.game.level1GameState == State.GetState()) {
            for (i = 0; i < Map.RectangleTileL1.size(); i++) {
                if (new Rectangle(xx, yy, space.width, space.height).intersects(Map.RectangleTileL1.get(i).r)) {
                    //daca sarpele se intersecteaza cu ceva
                    v.add(Map.RectangleTileL1.get(i));
                }
            }
        } else if (Variable.game.level2GameState == State.GetState()) {
            for (i = 0; i < Map.RectangleTileL2.size(); i++) {
                if (new Rectangle(xx, yy, space.width, space.height).intersects(Map.RectangleTileL2.get(i).r)) {
                    //daca sarpele se intersecteaza cu ceva
                    v.add(Map.RectangleTileL2.get(i));
                }
            }
        } else {
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

