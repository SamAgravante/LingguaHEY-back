package edu.cit.lingguahey.Service;

import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

@Service
public class TextToSpeechService {  

    private TextToSpeechClient textToSpeechClient;

    @PostConstruct
    public void init() throws IOException {
        // Initialize the TextToSpeechClient. ADC will handle authentication.
        textToSpeechClient = TextToSpeechClient.create();
    }

    public byte[] synthesizeSpeech(String text, String languageCode, SsmlVoiceGender ssmlGender, AudioEncoding audioEncoding) throws IOException {
        // Set the text input to be synthesized
        SynthesisInput input = SynthesisInput.newBuilder()
                .setText(text)
                .build();

        // Build the voice request
        VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                .setLanguageCode(languageCode)
                .setSsmlGender(ssmlGender)
                .build();

        // Select the type of audio encoding
        AudioConfig audioConfig = AudioConfig.newBuilder()
                .setAudioEncoding(audioEncoding)
                .build();

        // Perform the text-to-speech request
        SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

        // Get the audio contents from the response
        ByteString audioContents = response.getAudioContent();

        return audioContents.toByteArray();
    }

    @PreDestroy
    public void cleanup() {
        // Close the client when the application stops
        if (textToSpeechClient != null) {
            textToSpeechClient.close();
        }
    }
}