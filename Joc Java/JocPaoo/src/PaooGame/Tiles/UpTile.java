package PaooGame.Tiles;
import PaooGame.Graphics.Assets;

/*! \class public class UpTile extends Tile
    \brief Creaza un buton pentru load
 */
public class UpTile extends Tile
{
    /*! \fn public UpTile(int id)
       \brief Constructorul de initializare al clasei

       \param id: Nu este folosit(compatibilitate).
    */
    public UpTile(int id)
    {
        super(Assets.up, id);
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
