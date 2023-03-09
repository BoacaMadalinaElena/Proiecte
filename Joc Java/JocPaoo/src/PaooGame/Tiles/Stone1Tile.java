package PaooGame.Tiles;
import PaooGame.Graphics.Assets;

/*! \class public class Stone1Tile extends Tile
    \brief Abstractizeaza notiunea de dala de tip piata.
 */
public class Stone1Tile extends Tile
{
    /*! \fn public Stone1Tile(int id)
       \brief Constructorul de initializare al clasei

       \param id : Id-ul dalei util in desenarea hartii.
    */
    public Stone1Tile(int id)
    {
        super(Assets.stone1, id);
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
