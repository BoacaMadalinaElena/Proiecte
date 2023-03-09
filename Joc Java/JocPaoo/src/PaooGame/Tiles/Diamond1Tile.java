package PaooGame.Tiles;
import PaooGame.Graphics.Assets;

/*! \class Diamond1Tile
    \brief Abstractizeaza notiunea de dala de tip diamant(model 1).
 */
public class Diamond1Tile extends Tile
{
    /*! \fn public Diamond1Tile(int id)
       \brief Constructorul de initializare al clasei

       \param id : Id-ul dalei util in desenarea hartii.
    */
    public Diamond1Tile(int id)
    {
        super(Assets.diamond1, id);
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
