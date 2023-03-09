package PaooGame.Tiles;
import PaooGame.Graphics.Assets;

/*! \class public class Stone2Tile extends Tile
    \brief Abstractizeaza notiunea de dala de tip piatra.
 */
public class Stone2Tile extends Tile
{
    /*! \fn public Stone2Tile(int id)
       \brief Constructorul de initializare al clasei

       \param id : Id-ul dalei util in desenarea hartii.
    */
    public Stone2Tile(int id)
    {
        super(Assets.stone2, id);
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
