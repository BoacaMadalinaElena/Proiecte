package PaooGame.Tiles;
import PaooGame.Graphics.Assets;

/*! \class NextLevel1Tile
    \brief Abstractizeaza notiunea de dala de tip buton.

    Butoanele au nevoie de coliziune pe care am implementato cu RectangleId -> am nevoie de dale pentru ele
 */
public class NextLevel1Tile extends Tile
{
    /*! \fn public NextLevel1Tile(int id)
       \brief Constructorul de initializare al clasei

       \param id : Nu este folosit(compatibilitate).
    */
    public NextLevel1Tile(int id)
    {
        super(Assets.nextLevel1, id);
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