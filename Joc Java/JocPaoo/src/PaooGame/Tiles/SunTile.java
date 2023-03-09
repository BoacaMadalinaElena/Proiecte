package PaooGame.Tiles;
import PaooGame.Graphics.Assets;

/*! \class SunTile
    \brief Abstractizeaza notiunea de dala de tip soare.
 */
public class SunTile extends Tile
{
    /*! \fn public SunTile(int id)
       \brief Constructorul de initializare al clasei

       \param id : Id-ul dalei util in desenarea hartii.
    */
    public SunTile(int id)
    {
        super(Assets.sun, id);
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
