package PaooGame.Tiles;
import PaooGame.Graphics.Assets;

/*! \class Earth4Tile
    \brief Abstractizeaza notiunea de dala de tip pamant.
 */
public class Earth4Tile extends Tile
{
    /*! \fn public Earth4Tile(int id)
       \brief Constructorul de initializare al clasei

       \param : id Id-ul dalei util in desenarea hartii.
    */
    public Earth4Tile(int id)
    {
        super(Assets.earth4, id);
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
