package edu.cit.lingguahey.Controller;

import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import edu.cit.lingguahey.Service.TextToSpeechService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lingguahey/tts")
public class TextToSpeechController {

    @Autowired
    private TextToSpeechService textToSpeechService;

    @PostMapping(value = "/synthesize", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> synthesizeSpeech(@RequestBody TextToSpeechRequest request) {
        try {
            byte[] audioContent = textToSpeechService.synthesizeSpeech(
                    request.getText(),
                    request.getLanguageCode(),
                    request.getSsmlGender(),
                    request.getAudioEncoding()
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf("audio/" + request.getAudioEncoding().toString().toLowerCase()));
            headers.setContentLength(audioContent.length);

            return new ResponseEntity<>(audioContent, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Define a simple request body class
    public static class TextToSpeechRequest {
        private String text;
        private String languageCode = "fil-PH";
        private SsmlVoiceGender ssmlGender = SsmlVoiceGender.FEMALE;
        private AudioEncoding audioEncoding = AudioEncoding.MP3;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getLanguageCode() {
            return languageCode;
        }

        public void setLanguageCode(String languageCode) {
            this.languageCode = languageCode;
        }

        public SsmlVoiceGender getSsmlGender() {
            return ssmlGender;
        }

        public void setSsmlGender(SsmlVoiceGender ssmlGender) {
            this.ssmlGender = ssmlGender;
        }

        public AudioEncoding getAudioEncoding() {
            return audioEncoding;
        }

        public void setAudioEncoding(AudioEncoding audioEncoding) {
            this.audioEncoding = audioEncoding;
        }
    }
}