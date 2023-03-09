package PaooGame.Tiles;
import PaooGame.Graphics.Assets;

/*! \class chest1Tile
    \brief Abstractizeaza notiunea de dala de tip cutie(obstacol).
 */
public class Chest1Tile extends Tile
{
    /*! \fn public Chest1Tile(int id)
       \brief Constructorul de initializare al clasei

       \param : id Id-ul dalei util in desenarea hartii.
    */
    public Chest1Tile(int id)
    {
        super(Assets.chest1, id);
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
