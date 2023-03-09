package PaooGame.Tiles;
import PaooGame.Graphics.Assets;

/*! \class Diamond2Tile
    \brief Abstractizeaza notiunea de dala de tip diamant(2).
 */
public class Diamond2Tile extends Tile
{
    /*! \fn public Diamond2Tile(int id)
       \brief Constructorul de initializare al clasei

       \param id : Id-ul dalei util in desenarea hartii.
    */
    public Diamond2Tile(int id)
    {
        super(Assets.diamond2, id);
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
