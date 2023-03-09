package PaooGame.Tiles;
import PaooGame.Graphics.Assets;

/*! \class On1Tile
    \brief Butonul de pornire a sunetului
 */
public class On1Tile extends Tile
{
    /*! \fn  public OnTile(int id)
       \brief Constructorul de initializare al clasei

       \param id : Id-ul dalei nu este folosit
    */
    public On1Tile(int id)
    {
        super(Assets.on1, id);
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
