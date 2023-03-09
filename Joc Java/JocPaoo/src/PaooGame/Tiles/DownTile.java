package PaooGame.Tiles;
import PaooGame.Graphics.Assets;

/*! \class public class DownTile extends Tile
    \brief Folosita pentru buton la load.
 */
public class DownTile extends Tile
{
    /*! \fn public DownTile(int id)
       \brief Constructorul de initializare al clasei

       \param id : Nu este folosit(compatibilitate).
    */
    public DownTile(int id)
    {
        super(Assets.down1, id);
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
