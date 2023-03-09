package PaooGame.Tiles;
import PaooGame.Graphics.Assets;

/*! \class NextLevelTile
    \brief Abstractizeaza notiunea de dala de tip buton.

    Butoanele au nevoie de coliziune pe care am implementato cu RectangleId -> am nevoie de dale pentru ele
 */
public class NextLevelTile extends Tile
{
    /*! \fn public  NextLevelTile(int id)
       \brief Constructorul de initializare al clasei

       \param id : Nu este folosit(compatibilitate).
    */
    public NextLevelTile(int id)
    {
        super(Assets.nextLevel, id);
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
