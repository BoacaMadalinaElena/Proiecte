package PaooGame.Tiles;
import PaooGame.Graphics.Assets;

/*! \class public class ItemDataBaseTile1 extends Tile
    \brief buton.
 */
public class ItemDataBaseTile1 extends Tile
{
    /*! \fn public ItemDataBaseTile1(int id)
       \brief Constructorul de initializare al clasei

       \param id : Nu este folosit(compatibilitate).
    */
    public ItemDataBaseTile1(int id)
    {
        super(Assets.itemDataBaseImg, id);
    }

    /*! \fn public boolean IsSolid()
       \brief Suprascrie metoda IsSolid() stabileste daca obiectul face coliziune sau nu .
    */
    @Override
    public boolean IsSolid()
    {
        return true;
    }
}
