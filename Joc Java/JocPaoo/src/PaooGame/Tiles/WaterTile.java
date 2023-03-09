package PaooGame.Tiles;
import PaooGame.Graphics.Assets;

/*! \class WaterTile
    \brief Abstractizeaza notiunea de dala de tip apa.
 */
public class WaterTile extends Tile
{
    /*! \fn public WaterTile(int id)
       \brief Constructorul de initializare al clasei

       \param id : Id-ul dalei util in desenarea hartii.
    */
    public WaterTile(int id)
    {
        super(Assets.water, id);
    }

    /*! \fn public boolean IsSolid()
       \brief  Suprascrie metoda IsSolid() stabileste daca obiectul face coliziune sau nu.
    */
    @Override
    public boolean IsSolid()
    {
        return false;
    }
}
