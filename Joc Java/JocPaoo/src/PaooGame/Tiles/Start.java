package PaooGame.Tiles;

import PaooGame.Graphics.Assets;

/*! \class Start
    \brief Abstractizeaza notiunea de dala de tip pancarda start(apare la inceputul fiecarui nivel).
 */
public class Start extends Tile
{
    /*! \fn public Start(int id)
       \brief Constructorul de initializare al clasei

       \param id : Id-ul dalei util in desenarea hartii.
    */
    public Start(int id)
    {
        super(Assets.start, id);
    }

    /*! \fn public boolean IsSolid()
       \brief  Suprascrie metoda IsSolid() stabileste daca obiectul face coliziune sau nu.
    */
    @Override
    public boolean IsSolid()
    {
        return true ;
    }                               //il inchid pentru coliziune il i-au separat altfel probleme la deplasare
}
