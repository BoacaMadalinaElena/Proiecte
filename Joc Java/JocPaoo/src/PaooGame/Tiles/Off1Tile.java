package PaooGame.Tiles;
import PaooGame.Graphics.Assets;

/*! \class Off1Tile
    \brief Butonul de oprire a sunetului
 */
public class Off1Tile extends Tile
{
    /*! \fn  public OnTile(int id)
       \brief Constructorul de initializare al clasei

       \param id : Id-ul dalei nu este folosit
    */
    public Off1Tile(int id)
    {
        super(Assets.off1, id);
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
