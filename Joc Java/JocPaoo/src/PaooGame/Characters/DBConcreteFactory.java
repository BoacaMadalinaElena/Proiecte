package PaooGame.Characters;

import PaooGame.Exception.TypeNotFound;

/*! \class public class DBConcreteFactory extends FactoryMethod
    \brief Implementeaza clasa FactoryMethod pentru a defini cum se construiesc obiectele.
 */
public class DBConcreteFactory extends FactoryMethod {

    /*! \fn public  Elements createElements(String tip,int sens,int x,int y,int width,int height,int widthSpace,int heightSpace)
        \brief Returneaza un obiect nou.

        \param sunt utilizati pentru a crea sarpi,mamuti,bastinasi,gloantele.
     */
    @Override
    public  Elements createElements(String tip,int sens,int width,int height,int widthSpace,int heightSpace,int x,int y) throws TypeNotFound {
        switch (tip) {
            case "bullet":
                return new Bullet(x, y, width, height, widthSpace, heightSpace, sens);
            case "snake":
                return new Snake(x, y, width, height, widthSpace, heightSpace);
            case "mogly":
                return new Mogly(x, y, width, height, widthSpace, heightSpace);
            case "rino":
                return new Rino(x, y, width, height, widthSpace, heightSpace);
        }
        throw  new TypeNotFound(tip);
    }
}
