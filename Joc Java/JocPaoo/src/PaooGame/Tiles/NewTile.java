package PaooGame.Tiles;
import PaooGame.Graphics.Assets;

/*! \class NewTile
    \brief Abstractizeaza notiunea de dala de tip buton.

    Butoanele au nevoie de coliziune pe care am implementato cu RectangleId -> am nevoie de dale pentru ele
 */
public class NewTile extends Tile
{
    /*! \fn public NewTile(int id)
       \brief Constructorul de initializare al clasei

       \param id Nu este folosit(compatibilitate).
    */
    public NewTile(int id)
    {
        super(Assets._new, id);
    }

    /*! \fn public boolean IsSolid()
       \brief Suprascrie metoda IsSolid() stabileste daca obiectul face coliziune sau nu.
    */
    @Override
    public boolean IsSolid()
    {
        return true;
    }
}
