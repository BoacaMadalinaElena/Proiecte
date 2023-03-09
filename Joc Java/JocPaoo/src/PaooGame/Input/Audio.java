package PaooGame.Input;

import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

/*! \class public class Audio
    \brief Va gestiona muzica de fundal a jocului.
 */
public class Audio {

    private Clip clip;                                                       /*!< Referinte catre un clip audio.*/
    public static boolean status;                                                   /*!< Referinte catre starea clipului.*/

    AudioInputStream audioInputStream;                               /*!< Referinte catre obiectul de tip audio.*/
    static String filePath;                                          /*!< Referinte catre sirul ce retine calea spre clipul audio.*/
    public static float volume;
    /*! \fn public Audio()
        \brief Constructorul clasei
     */
    public Audio() {
        volume = 0.5f;
        try {
            filePath = "res/sound.wav";
            //filePath = "C:\\Users\\Madalina\\Downloads\\saRadem.wav";
            //create AudioInputStream object
            audioInputStream =
                    AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());
            status = true;
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (javax.sound.sampled.UnsupportedAudioFileException | javax.sound.sampled.LineUnavailableException | java.io.IOException e) {
            System.out.println(e);
        }
    }

    /*! \fn public void play()
         \brief Pornirea clipului audio.
    */
    public void play(float vol) {
        clip.start();
        status = true;
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float dB = (float) (Math.log(vol) / Math.log(10.0) * 20.0);
        gainControl.setValue(dB);
    }

    /*! \fn public void stopAudio()
         \brief Oprirea clipului audio.
    */
    public void stopAudio() {
        clip.stop();
        status = false;
    }
}

