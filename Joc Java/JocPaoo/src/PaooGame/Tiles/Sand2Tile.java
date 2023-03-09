package PaooGame.Tiles;
import PaooGame.Graphics.Assets;

/*! \class Sand2Tile
    \brief Abstractizeaza notiunea de dala de tip nisip.
 */
public class Sand2Tile extends Tile
{
    /*! \fn public Sand2Tile(int id)
       \brief Constructorul de initializare al clasei

       \param id : Id-ul dalei util in desenarea hartii.
    */
    public Sand2Tile(int id)
    {
        super(Assets.sand2, id);
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
