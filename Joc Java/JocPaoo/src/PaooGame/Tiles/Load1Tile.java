package PaooGame.Tiles;
import PaooGame.Graphics.Assets;

/*! \class Load1Tile
    \brief Abstractizeaza notiunea de dala de tip load(un buton).

    Butoanele au nevoie de coliziune pe care am implementato cu RectangleId -> am nevoie de dale pentru ele
 */
public class Load1Tile extends Tile
{
    /*! \fn public Load1Tile(int id)
       \brief Constructorul de initializare al clasei

       \param id : Nu este folosit(compatibilitate).
    */
    public Load1Tile(int id)
    {
        super(Assets.load1, id);
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
