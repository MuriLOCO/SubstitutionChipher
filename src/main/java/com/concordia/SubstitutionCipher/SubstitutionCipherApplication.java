package com.concordia.SubstitutionCipher;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.concordia.SubstitutionCipher.utils.CipherUtils;
import com.concordia.SubstitutionCipher.utils.GeneralUtils;

@SpringBootApplication
public class SubstitutionCipherApplication {

  private static final Logger LOGGER = LoggerFactory.getLogger(SubstitutionCipherApplication.class);

  public static void main(String[] args) throws IOException {
    SpringApplication.run(SubstitutionCipherApplication.class, args);

    try {
      String option = GeneralUtils
            .getUserResponse("Choose 1 for Encrypt a Plain Text or 2 to find the key of a Ciphertext:");

      switch (Integer.parseInt(option)) {
      case 1:
        System.out.println("You chose to Encrypt a Plain Text.");
        final String text = GeneralUtils.getUserResponse("\nPlease insert the text: ");
        final String key = GeneralUtils.getUserResponse("\nPlease insert the key: ");
        final String adaptedText = GeneralUtils.adaptString(text);
        final String adaptedKey = GeneralUtils.adaptString(key);
        GeneralUtils.validate(adaptedText, adaptedKey);
        System.out.println("Encrypting...");
        String result = CipherUtils.encrypt(adaptedText, adaptedKey);
        System.out.print("Encrypted message: " + result);
        System.exit(0);
        break;
      case 2:
        System.out.println("You chose to find the key.");
        String cipherText = GeneralUtils.getUserResponse("Please insert the cipher text: ");
        String adapterCipherText = GeneralUtils.adaptString(cipherText);
        // for debugging purpose hard code the text
        String originalkey = "phqgiumeaylnofdxjkrcvstzwb".toUpperCase();
        //String inputString = "How your brain responds to music listening can reveal whether you have received musical training, according to new Nordic research conducted in Finland (University of Jyväskylä and AMI Center) and Denmark (Aarhus University).By applying methods of computational music analysis and machine learning on brain imaging data collected during music listening, the researchers we able to predict with a significant accuracy whether the listeners were musicians or not. These results emphasize the striking impact of musical training on our neural responses to music to the extent of discriminating musicians' brains from non-musicians' brains despite other independent factors such as musical preference and familiarity.The research also revealed that the brain areas that best predict musicianship exist predominantly in the frontal and temporal areas of the brain's right hemisphere. These findings conform to previous work on how the brain processes certain acoustic characteristics of music as well as intonation in speech. The paper was published on January 15 in the journal Scientific Reports.The study utilized functional magnetic resonance imaging (fMRI) brain data collected by Professor Elvira Brattico's team at Aarhus University. The data was collected from 18 musicians and 18 non-musicians while they attentively listened to music of different genres. Computational algorithms were applied to extract musical features from the presented music.\"A novel feature of our approach was that, instead of relying on static representations of brain activity, we modelled how music is processed in the brain over time. Taking the temporal dynamics into account was found to improve the results remarkably,\" explains Pasi Saari, Postdoctoral Researcher at the University of Jyväskylä and the main author of the study.As the last step of modelling, the researchers used machine learning to form a model that predicts musicianship from a combination of brain regions.The machine learning model was able to predict the listeners' musicianship with 77 % accuracy, a result that is on a par with similar studies on participant classification with, for example, clinical populations of brain-damaged patients. The areas where music processing best predicted musicianship resided mostly in the right hemisphere, and included areas previously found to be associated with engagement and attention, processing of musical conventions, and processing of music-related sound features (e.g. pitch and tonality).\"These areas can be regarded as core structures in music processing which are most affected by intensive, lifelong musical training,\" states Iballa Burunat, Postdoctoral Researcher at the University of Jyväskylä and a co-author of the study.In these areas, the processing of higher-level features such as tonality and pulse was the best predictor of musicianship, suggesting that musical training affects particularly the processing of these aspects of music.\"The novelty of our approach is the integration of computational acoustic feature extraction with functional neuroimaging measures, obtained in a realistic music-listening environment, and taking into account the dynamics of neural processing. It represents a significant contribution that complements recent brain-reading methods which decode participant information from brain activity in realistic conditions,\" concludes Petri Toiviainen, Academy Professor at the University of Jyväskylä and the senior author of the study.The research was funded by the Academy of Finland and Danish National Research Foundation.";
        String inputString = "CHAPTER I. Down the Rabbit-Hole  Alice was beginning to get very tired of sitting by her sister on the bank, and of having nothing to do: once or twice she had peeped into the book her sister was reading, but it had no pictures or conversations in it, ‘and what is the use of a book,’ thought Alice ‘without pictures or conversations?’  So she was considering in her own mind (as well as she could, for the hot day made her feel very sleepy and stupid), whether the pleasure of making a daisy-chain would be worth the trouble of getting up and picking the daisies, when suddenly a White Rabbit with pink eyes ran close by her.  There was nothing so VERY remarkable in that; nor did Alice think it so VERY much out of the way to hear the Rabbit say to itself, ‘Oh dear! Oh dear! I shall be late!’ (when she thought it over afterwards, it occurred to her that she ought to have wondered at this, but at the time it all seemed quite natural); but when the Rabbit actually TOOK A WATCH OUT OF ITS WAISTCOAT-POCKET, and looked at it, and then hurried on, Alice started to her feet, for it flashed across her mind that she had never before seen a rabbit with either a waistcoat-pocket, or a watch to take out of it, and burning with curiosity, she ran across the field after it, and fortunately was just in time to see it pop down a large rabbit-hole under the hedge.  In another moment down went Alice after it, never once considering how in the world she was to get out again.  The rabbit-hole went straight on like a tunnel for some way, and then dipped suddenly down, so suddenly that Alice had not a moment to think about stopping herself before she found herself falling down a very deep well.  Either the well was very deep, or she fell very slowly, for she had plenty of time as she went down to look about her and to wonder what was going to happen next. First, she tried to look down and make out what she was coming to, but it was too dark to see anything; then she looked at the sides of the well, and noticed that they were filled with cupboards and book-shelves; here and there she saw maps and pictures hung upon pegs. She took down a jar from one of the shelves as she passed; it was labelled ‘ORANGE MARMALADE’, but to her great disappointment it was empty: she did not like to drop the jar for fear of killing somebody, so managed to put it into one of the cupboards as she fell past it.  ‘Well!’ thought Alice to herself, ‘after such a fall as this, I shall think nothing of tumbling down stairs! How brave they’ll all think me at home! Why, I wouldn’t say anything about it, even if I fell off the top of the house!’ (Which was very likely true.)  Down, down, down. Would the fall NEVER come to an end! ‘I wonder how many miles I’ve fallen by this time?’ she said aloud. ‘I must be getting somewhere near the centre of the earth. Let me see: that would be four thousand miles down, I think--’ (for, you see, Alice had learnt several things of this sort in her lessons in the schoolroom, and though this was not a VERY good opportunity for showing off her knowledge, as there was no one to listen to her, still it was good practice to say it over) ‘--yes, that’s about the right distance--but then I wonder what Latitude or Longitude I’ve got to?’ (Alice had no idea what Latitude was, or Longitude either, but thought they were nice grand words to say.)  ";
        inputString = GeneralUtils.adaptString(inputString);
        adapterCipherText = CipherUtils.encrypt(inputString.toString(), originalkey.toString());
        // cipherText = CipherUtils.encrypt(inputString, originalkey);
        LOGGER.info("****Original Key: " + originalkey);
        LOGGER.info("****PlainText: " + inputString);
        LOGGER.info("****CipherText: " + adapterCipherText);
        System.out.println("Original Key: " + originalkey);
        System.out.println("PlainText: " + inputString);
        System.out.println("cipherText: " + adapterCipherText);

        // end for debugging
        CipherUtils.decrypt(adapterCipherText);
        System.exit(0);
        break;
      default:
        System.out.println("You chose an invalid option.");
        System.exit(0);
        break;
      }
    } catch (NumberFormatException e) {
      throw new NumberFormatException("You must choose a number for the option.");
    }
  }
}
