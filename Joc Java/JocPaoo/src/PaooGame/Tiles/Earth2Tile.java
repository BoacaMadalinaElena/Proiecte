package PaooGame.Tiles;
import PaooGame.Graphics.Assets;

/*! \class Earth2Tile
    \brief Abstractizeaza notiunea de dala de tip pamant.
 */
public class Earth2Tile extends Tile
{
    /*! \fn public Earth2Tile(int id)
       \brief Constructorul de initializare al clasei

       \param id : Id-ul dalei util in desenarea hartii.
    */
    public Earth2Tile(int id)
    {
        super(Assets.earth2, id);
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
