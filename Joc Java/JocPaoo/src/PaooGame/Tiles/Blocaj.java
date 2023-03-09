package PaooGame.Tiles;
import PaooGame.Graphics.Assets;

/*! \class Blocaj
    \brief Defineste zonele invalide(pentru a evita iesirea din cadrul de joc).
 */
public class Blocaj extends Tile
{
    /*! \fn public Blocaj(int id)
       \brief Constructorul de initializare al clasei

       \param id : Id-ul dalei util in desenarea hartii.
    */
    public Blocaj(int id)
    {
        super(Assets.vid, id);
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
