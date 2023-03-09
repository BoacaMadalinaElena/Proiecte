package PaooGame.Tiles;
import PaooGame.Graphics.Assets;

/*! \class Earth3Tile
    \brief Abstractizeaza notiunea de dala de tip pamant.
 */
public class Earth3Tile extends Tile
{
    /*! \fn public Earth3Tile(int id)
       \brief Constructorul de initializare al clasei

       \param id : Id-ul dalei util in desenarea hartii.
    */
    public Earth3Tile(int id)
    {
        super(Assets.earth3, id);
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
