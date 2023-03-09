package PaooGame.Tiles;
import PaooGame.Graphics.Assets;

/*! \class Barrel1Tile
    \brief Abstractizeaza notiunea de butoi pentru joc. Extinde Tile
 */
public class Barrel1Tile extends Tile
{
    /*! \fn public Barrel1Tile(int id)
       \brief Constructorul de initializare al clasei

       \param id : Id-ul dalei util in desenarea hartii.
    */
    public Barrel1Tile(int id)
    {
        super(Assets.barrel1, id);
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
