package PaooGame.Characters;

import PaooGame.Exception.TypeNotFound;

/*! \class public class NewConcreteFactory extends FactoryMethod
    \brief Implementeaza clasa FactoryMethod pentru a defini cum se construiesc obiectele.
 */
public class NewConcreteFactory extends FactoryMethod {

    /*! \fn public  Elements createElements(String tip,int sens,int x,int y,int width,int height,int widthSpace,int heightSpace)
        \brief Returneaza un obiect nou.

        \param sunt utilizati pentru a crea sarpi,mamuti,bastinasi,gloantele.
     */
    @Override
    public  Elements createElements(String tip,int sens,int x,int y,int width,int height,int widthSpace,int heightSpace) throws TypeNotFound{
        switch (tip) {
            case "bullet":
                //bullet(int x, int y, int width, int height, int widthRectangle, int heightRectanle, int sens) {
                return new Bullet(x, y, width, height, widthSpace, heightSpace, sens);
            case "snake":
                //Snake(int x, int y, int width, int height, int widthRectangle, int heightRectangle)
                return new Snake(x, y, width, height, widthSpace, heightSpace);
            case "mogly":
                return new Mogly(x, y, width, height, widthSpace, heightSpace);
            case "rino":
                return new Rino(x, y, width, height, widthSpace, heightSpace);
        }
        throw new TypeNotFound(tip);
    }
}
