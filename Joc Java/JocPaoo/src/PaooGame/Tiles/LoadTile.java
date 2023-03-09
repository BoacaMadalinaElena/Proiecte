package PaooGame.Tiles;
import PaooGame.Graphics.Assets;

/*! \class LoadTile
    \brief Abstractizeaza notiunea de dala de tip buton.

    Butoanele au nevoie de coliziune pe care am implementato cu RectangleId -> am nevoie de dale pentru ele
 */
public class LoadTile extends Tile
{
    /*! \fn public LoadTile(int id)
       \brief Constructorul de initializare al clasei

       \param id : Nu este folosit(compatibilitate)..
    */
    public LoadTile(int id)
    {
        super(Assets.load, id);
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
