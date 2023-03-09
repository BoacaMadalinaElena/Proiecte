package PaooGame.Tiles;
import PaooGame.Graphics.Assets;

/*! \class SavePauseTile
    \brief Abstractizeaza notiunea de dala de tip buton.

    Butoanele au nevoie de coliziune pe care am implementato cu RectangleId -> am nevoie de dale pentru ele
 */
public class SavePauseTile extends Tile
{
    /*! \fn public SavePauseTile(int id)
       \brief Constructorul de initializare al clasei

       \param id : Nu este folosit(compatibilitate).
    */
    public SavePauseTile(int id)
    {
        super(Assets.Save, id);
    }

    /*! \fn public boolean IsSolid()
       \brief  Suprascrie metoda IsSolid() stabileste daca obiectul face coliziune sau nu.
    */
    @Override
    public boolean IsSolid()
    {
        return true;
    }

    /*! \class public class ImageLoader
            \brief Clasa ce contine o metoda statica pentru incarcarea unei imagini in memorie.
         */
}
