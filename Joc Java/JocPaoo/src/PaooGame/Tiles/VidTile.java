package PaooGame.Tiles;
import PaooGame.Graphics.Assets;

/*! \class VidTile
    \brief Abstractizeaza notiunea de dala de tip vid.
 */
public class VidTile extends Tile
{
    /*! \fn public VidTile(int id)
       \brief Constructorul de initializare al clasei

       \param id : Id-ul dalei util in desenarea hartii.
    */
    public VidTile(int id)
    {
        super(Assets.vid, id);
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
