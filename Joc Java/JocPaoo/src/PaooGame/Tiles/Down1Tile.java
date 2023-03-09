package PaooGame.Tiles;
import PaooGame.Graphics.Assets;

/*! \class public class Down1Tile extends Tile
    \brief Buton folosit la load
 */
public class Down1Tile extends Tile
{
    /*! \fn public Down1Tile(int id)
       \brief Constructorul de initializare al clasei

       \param id : Nu este folosit(compatibilitate).
    */
    public Down1Tile(int id)
    {
        super(Assets.down, id);
    }

    /*! \fn public boolean IsSolid()
       \brief  Suprascrie metoda IsSolid() stabileste daca obiectul face coliziune sau nu.
    */
    @Override
    public boolean IsSolid()
    {
        return false;
    }
}
