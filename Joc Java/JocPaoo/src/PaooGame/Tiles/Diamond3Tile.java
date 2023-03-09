package PaooGame.Tiles;
import PaooGame.Graphics.Assets;

/*! \class Diamond3Tile
    \brief Abstractizeaza notiunea de dala de tip diamant(3).
 */
public class Diamond3Tile extends Tile
{
    /*! \fn public Diamond3Tile(int id)
       \brief Constructorul de initializare al clasei

       \param id : Id-ul dalei util in desenarea hartii.
    */
    public Diamond3Tile(int id)
    {
        super(Assets.diamond3, id);
    }

    /*! \fn public boolean IsSolid()
       \brief Suprascrie metoda IsSolid() stabileste daca obiectul face coliziune sau nu .
    */
    @Override
    public boolean IsSolid()
    {
        return false ;
    }
}
