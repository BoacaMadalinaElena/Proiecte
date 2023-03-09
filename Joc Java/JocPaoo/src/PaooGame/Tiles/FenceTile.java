package PaooGame.Tiles;
import PaooGame.Graphics.Assets;

/*! \class FenceTile
    \brief Abstractizeaza notiunea de dala de tip gard.
 */
public class FenceTile extends Tile
{
    /*! \fn public FenceTile(int id)
       \brief Constructorul de initializare al clasei

       \param :Pentru harta.
    */
    public FenceTile(int id)
    {
        super(Assets.fence, id);
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
