package PaooGame.Tiles;
import PaooGame.Graphics.Assets;

/*! \class public class ItemDataBaseTile extends Tile
    \brief Buton
 */
public class ItemDataBaseTile extends Tile
{
    /*! \fn public ItemDataBaseTile(int id)
       \brief Constructorul de initializare al clasei

       \param id : Nu este folosit(compatibilitate).
    */
    public ItemDataBaseTile(int id)
    {
        super(Assets.itemDataBaseImg1, id);
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
