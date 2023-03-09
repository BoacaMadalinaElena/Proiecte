package PaooGame.Tiles;
import PaooGame.Graphics.Assets;

/*! \class Stone4Tile
    \brief Abstractizeaza notiunea de dala de tip piatra.
 */
public class Stone4Tile extends Tile
{
    /*! \fn public Stone4Tile(int id)
       \brief Constructorul de initializare al clasei

       \param id : Id-ul dalei util in desenarea hartii.
    */
    public Stone4Tile(int id)
    {
        super(Assets.stone4, id);
    }

    /*! \fn public boolean IsSolid()
       \brief  Suprascrie metoda IsSolid() stabileste daca obiectul face coliziune sau nu.
    */
    @Override
    public boolean IsSolid()
    {
        return true;
    }
}
