package PaooGame.Tiles;
import PaooGame.Graphics.Assets;

/*! \class Chest2Tile
    \brief Abstractizeaza notiunea de dala de tip cutie(2).
 */
public class Chest2Tile extends Tile
{
    /*! \fn public Chest2Tile(int id)
       \brief Constructorul de initializare al clasei

       \param id : Id-ul dalei util in desenarea hartii.
    */
    public Chest2Tile(int id)
    {
        super(Assets.chest2, id);
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
