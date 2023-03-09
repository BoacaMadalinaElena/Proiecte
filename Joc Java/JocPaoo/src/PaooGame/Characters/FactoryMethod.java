package PaooGame.Characters;

import PaooGame.Exception.TypeNotFound;

/*! \public public abstract class FactoryMethod
    \brief Va fi extinsa pentru a crea producatori concreti.
 */
public abstract class FactoryMethod {
    public abstract  Elements createElements(String tip, int sens, int width, int height, int widthSpace, int heightSpace, int x, int y) throws TypeNotFound;
}
