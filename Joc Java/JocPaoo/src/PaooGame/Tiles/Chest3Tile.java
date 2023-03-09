package PaooGame.Tiles;
import PaooGame.Graphics.Assets;

/*! \class Chest3Tile
    \brief Abstractizeaza notiunea de dala de tip cutie(3).
 */
public class Chest3Tile extends Tile
{
    /*! \fn public Chest3Tile(int id)
       \brief Constructorul de initializare al clasei

       \param id : Id-ul dalei util in desenarea hartii.
    */
    public Chest3Tile(int id)
    {
        super(Assets.chest3, id);
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
