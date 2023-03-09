package PaooGame.Characters;

import java.awt.*;

/*! \class public abstract class Elements
    \brief Clasa abstracta derivata ulterior in persoanaje concrete
 */
public abstract class Elements {
    protected int x;                                      /*!< Referinta spre x-ul actual al caracterului.*/
    protected int y;                                      /*!< Referinta spre y-ul actual al caracterului.*/
    protected int width;                                  /*!< Referinta spre latimea caracterului.*/
    protected int height;                                 /*!< Referinta spre inaltimea caracterului.*/
    protected Rectangle space;                            /*!< Referinta dreptunghiul de coliziune caracterului.*/
    private int Sens;                                     /*!< Referinta spre sensul de deplasare.*/

    /*! \fn public Elements(int x, int y, int width, int height, int widthRectangle, int heightRectanle)
        \brief  Constructorul clasei element
     */
    public Elements(int x, int y, int width, int height, int widthRectangle, int heightRectanle) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.space = new Rectangle(x, y, widthRectangle, heightRectanle);
        Sens = 0;                                                                           ///doar pt bullet
    }

    /*! \fn public abstract void Update()
        \brief  Actualizare stari element => se defineste in clasele efective
     */
    public abstract void Update();

    /*! \fn public abstract void Move()
        \brief  Actualizare pozitii element => se defineste in clasele efective
     */
    public abstract boolean Move();

    /*! \fn public abstract void Draw()
        \brief  Deseneaza elemente => se defineste in clasele efective
     */
    public abstract void Draw();

    public Rectangle getSpace() {
        return space;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getWidthRectangle() {
        return space.width;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSens() {
        return Sens;
    }

    public void setSens(int s) {
        Sens = s;
    }
}
