package PaooGame.Tiles;
import PaooGame.Graphics.Assets;

/*! \class Stone5Tile
    \brief Abstractizeaza notiunea de dala de tip piatra.
 */
public class Stone5Tile extends Tile
{
    /*! \fn public Stone5Tile(int id)
       \brief Constructorul de initializare al clasei

       \param id : Id-ul dalei util in desenarea hartii.
    */
    public Stone5Tile(int id)
    {
        super(Assets.stone5, id);
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
