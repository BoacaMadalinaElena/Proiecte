package PaooGame.Tiles;
import PaooGame.Graphics.Assets;

/*! \class Earth1Tile
    \brief Abstractizeaza notiunea de dala de tip pamant.
 */
public class Earth1Tile extends Tile
{
    /*! \fn public Earth1Tile(int id)
       \brief Constructorul de initializare al clasei

       \param id : Id-ul dalei util in desenarea hartii.
    */
    public Earth1Tile(int id)
    {
        super(Assets.earth1, id);
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
