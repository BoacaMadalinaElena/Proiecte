package PaooGame.Tiles;
import PaooGame.Graphics.Assets;

/*! \class Barrel2Tile
    \brief Abstractizeaza notiunea de dala de tip butoi(2).
 */
public class Barrel2Tile extends Tile
{
    /*! \fn public Barrel2Tile(int id)
       \brief Constructorul de initializare al clasei

       \param id : Id-ul dalei util in desenarea hartii.
    */
    public Barrel2Tile(int id)
    {
        super(Assets.barrel2, id);
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
